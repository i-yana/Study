import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class Proxy {
    private static final int BUFFER_SIZE = 1024*1024;
    private final int lport;


    public static void main(String[] args) {
        if(args.length!=1){
            System.err.println("Usage: port");
            return;
        }
        Proxy p = new Proxy(Integer.parseInt(args[0]));
        try {
            p.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Proxy(int lport){
        this.lport = lport;

    }

    public void start() throws IOException {

        Selector selector = Selector.open();
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(lport), 10);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, serverSocketChannel);

        while (true) {
            int count = selector.select();
            if (count == 0) {
                continue;
            }
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                try {
                    SelectionKey key = it.next();
                    if (key.isValid() && (key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                        accept(key, serverSocketChannel);
                    }
                    if (key.isValid() && (key.readyOps() & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT) {
                        connect(key);
                    }
                    if (key.isValid() && (key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                        read(key, byteBuffer);
                    }
                    if (key.isValid() && (key.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
                        write(key);
                    }
                }catch (IOException e){
                    System.err.println(e.getMessage());
                }
                finally {
                    it.remove();
                }
            }

        }
    }

    private void write(SelectionKey key) throws IOException {
        RoutingPair pair = (RoutingPair) key.attachment();
        pair.byteBuffer.flip();
        pair.toSocket.write(pair.byteBuffer);
        if (pair.byteBuffer.remaining() > 0) {
            pair.byteBuffer.compact();
            return;
        }
        key.interestOps(SelectionKey.OP_READ);
        pair.byteBuffer.clear();
    }


    private void read(SelectionKey key, ByteBuffer readBuffer) throws IOException {
        RoutingPair pair = (RoutingPair) key.attachment();
        if(pair.toSocket == null){
            readBuffer.clear();
            if(pair.fromSocket.read(readBuffer)<=0){
                pair.fromSocket.close();
                key.cancel();
                return;
            }

            try {
                pair.byteBuffer.put(Parser.getNewQuery(readBuffer));
                InetSocketAddress remoteAddress = Parser.getRemoteInetSocketAddress(readBuffer);
                System.out.println("remote address: " + remoteAddress);
                SocketChannel remote = SocketChannel.open();
                remote.configureBlocking(false);
                remote.connect(remoteAddress);
                pair.toSocket = remote;
                remote.register(key.selector(), SelectionKey.OP_CONNECT, key);
                key.interestOps(0);
                return;
            }
            catch (HTTPException e) {
                String answ = "Unknown error";
                int status = e.getStatusCode();
                if (status == 501) {
                    answ = "501 Not Implemented";
                }
                if (status == 502) {
                    answ = "502 Bad Gateway";
                }
                System.err.println("Unknown Protocol");
                pair.fromSocket.write(ByteBuffer.wrap(answ.getBytes("UTF-8")));
                return;
            }
            catch (UnresolvedAddressException e){
                    System.err.println("Unknown Protocol");
                    pair.fromSocket.write(ByteBuffer.wrap("502 Bad Gateway".getBytes("UTF-8")));
                    return;

            }

        }
        readBuffer.clear();
        if (pair.fromSocket.read(readBuffer) <= 0) {
            pair.fromSocket.close();
            pair.toSocket.close();
            key.cancel();
            return;
        }
        String s = new String(readBuffer.array(), readBuffer.arrayOffset(), readBuffer.array().length, "UTF-8");
        System.out.println("RECEIVE: "+ s);
        String allQuery = new String(readBuffer.array(), readBuffer.arrayOffset(), readBuffer.position(), "UTF-8");
        if(allQuery.substring(0,4).equals("HTTP")) {
            byte[] b = Parser.parseIn(readBuffer);
            readBuffer.clear();
            readBuffer.put(b);
        }

        String l = new String(readBuffer.array(), readBuffer.arrayOffset(), readBuffer.array().length, "UTF-8");
        System.out.println("PARSE RECEIVE: "+ l);
        readBuffer.flip();
        pair.toSocket.write(readBuffer);
        if (readBuffer.remaining() > 0) {
            pair.byteBuffer.put(readBuffer);
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    private void connect(SelectionKey key) throws IOException {
        SocketChannel remoteSocket = (SocketChannel) key.channel();
        remoteSocket.finishConnect();
        System.out.println("Connect to: " + remoteSocket);
        remoteSocket.socket().setTcpNoDelay(true);

        SelectionKey keyFrom = (SelectionKey) key.attachment();
        RoutingPair pairFrom = (RoutingPair)keyFrom.attachment();
        RoutingPair pairRemote = new RoutingPair();
        pairRemote.fromSocket = remoteSocket;
        pairRemote.toSocket = pairFrom.fromSocket;
        pairRemote.byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        key.attach(pairRemote);
        keyFrom.interestOps(SelectionKey.OP_WRITE);
        key.interestOps(SelectionKey.OP_READ);

    }

    private void accept(SelectionKey key, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel fromSocket = serverSocketChannel.accept();
        System.out.println("accept: "+ fromSocket);
        fromSocket.socket().setTcpNoDelay(true);
        fromSocket.configureBlocking(false);
        RoutingPair pair = new RoutingPair();
        pair.fromSocket = fromSocket;
        pair.toSocket = null;
        pair.byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        fromSocket.register(key.selector(), SelectionKey.OP_READ, pair);
    }

    class RoutingPair {
        SocketChannel fromSocket;
        SocketChannel toSocket;
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    }
}
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class PortForwarder {
    private static final int BUFFER_SIZE = 100000;
    private final int lport;
    private final String rhost;
    private final int rport;

    public static void main(String[] args) {
        if(args.length!=3){
            System.out.println("Not enough arguments. Usage: lport, rhost, rport");
        }
        PortForwarder p = new PortForwarder(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2]));
        try {
            p.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PortForwarder(int lport, String rhost, int rport){
        this.lport = lport;
        this.rhost = rhost;
        this.rport = rport;
    }

    public void start() throws IOException {
        InetSocketAddress remoteAddress = new InetSocketAddress(rhost, rport);
        System.out.println(remoteAddress);
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
                SelectionKey key = it.next();
                if (key.isValid() && (key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                    accept(key, remoteAddress);
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
                it.remove();
            }

        }
    }

    private void createSocketPair(Selector selector, SocketChannel socket, SocketChannel forwardToSocket) throws ClosedChannelException {
        RoutingPair routingLRPair = new RoutingPair();
        routingLRPair.fromSocket = socket;
        routingLRPair.toSocket = forwardToSocket;
        routingLRPair.fromSocket.register(selector, SelectionKey.OP_READ, routingLRPair);

        RoutingPair routingRLPair = new RoutingPair();
        routingRLPair.fromSocket = forwardToSocket;
        routingRLPair.toSocket = socket;
        routingRLPair.fromSocket.register(selector, SelectionKey.OP_READ, routingRLPair);
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
        readBuffer.clear();
        if (pair.fromSocket.read(readBuffer) <= 0) {
            pair.fromSocket.close();
            pair.toSocket.close();
            key.cancel();
            return;
        }
        String s = new String(readBuffer.array(), 0,readBuffer.array().length, "UTF-8");
        System.out.println(s);
        readBuffer.flip();
        pair.toSocket.write(readBuffer);
        if (readBuffer.remaining() > 0) {
            pair.byteBuffer.put(readBuffer);
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    private void connect(SelectionKey key) throws IOException {
        SocketChannel fromSocket = (SocketChannel) key.attachment();
        SocketChannel remoteSocket = (SocketChannel) key.channel();
        remoteSocket.finishConnect();
        remoteSocket.socket().setTcpNoDelay(true);
        createSocketPair(key.selector(), fromSocket, remoteSocket);
    }

    private void accept(SelectionKey key, InetSocketAddress remoteAddress) throws IOException {
        SocketChannel fromSocket = ((ServerSocketChannel)key.attachment()).accept();
        fromSocket.socket().setTcpNoDelay(true);
        fromSocket.configureBlocking(false);
        SocketChannel remoteSocket = SocketChannel.open();
        remoteSocket.configureBlocking(false);
        boolean connected = remoteSocket.connect(remoteAddress);
        if (!connected) {
            remoteSocket.register(key.selector(), SelectionKey.OP_CONNECT, fromSocket);
            return;
        }
        remoteSocket.socket().setTcpNoDelay(true);
        createSocketPair(key.selector(), fromSocket, remoteSocket);
    }

    class RoutingPair {
        SocketChannel fromSocket;
        SocketChannel toSocket;
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    }
}
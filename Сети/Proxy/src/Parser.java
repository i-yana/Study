import javax.xml.ws.http.HTTPException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Created by Yana on 26.11.15.
 */
public class Parser {

    private static String header;
    private static byte[] afterHeader;

    public static InetSocketAddress getRemoteInetSocketAddress(ByteBuffer buffer) throws HTTPException, UnsupportedEncodingException {
        String header = new String(buffer.array(), buffer.arrayOffset(),buffer.position(), "UTF-8");
        if(!header.contains("HTTP")){
            throw new HTTPException(501);
        }
        String url = header.substring(header.indexOf("http://"), header.indexOf("HTTP"));
        int port = parsePort(url);
        String tmp = header.substring(header.indexOf("http://")+7);
        String host = tmp.substring(0, tmp.indexOf('/'));
        return new InetSocketAddress(host, port);
    }



    public static byte[] getNewQuery(ByteBuffer buffer) throws UnsupportedEncodingException, HTTPException {
        String header = new String(buffer.array(), buffer.arrayOffset(),buffer.position(), "UTF-8");
        if(!header.contains("HTTP") || !header.contains("http://")){
            throw new HTTPException(501);
        }
        String url = header.substring(header.indexOf("http://"), header.indexOf("HTTP"));
        //System.out.println(url);
        int ind = url.indexOf('/', 7);
        String u = url.substring(ind);
        String parsedHeader = header.replace(url,u);
        parsedHeader = parsedHeader.replace("1.1", "1.0");
        parsedHeader = parsedHeader.replaceFirst("\n", "\nConnection: close\n");
        System.out.println("PARSER INPUT DATA:\n" + parsedHeader);
        return parsedHeader.getBytes("UTF-8");
    }

    private static int parsePort(String url) {
        String withoutHTTP = url.substring(url.indexOf("http://")+7);
        String domain = withoutHTTP.substring(0, withoutHTTP.indexOf('/'));
        int twoPointIndex = domain.indexOf(':');
        if(twoPointIndex ==-1){
            return 80;
        }
        return Integer.parseInt(domain.substring(twoPointIndex));
    }

    public static byte[] parseIn(ByteBuffer buffer) throws UnsupportedEncodingException {
        String allQuery = new String(buffer.array(), 0, buffer.position(), "UTF-8");
        int count = symbolCount(allQuery);
        String headerOfQuery = allQuery.substring(0, count);

        parseHeader(headerOfQuery);
        byte[] validHeaders = parseHeader(headerOfQuery).getBytes();
        int tailLen = buffer.position() - count;
        byte[] tail = new byte[tailLen];
        for(int i = count; i < buffer.position(); i++){
            tail[i - count] = buffer.array()[i];
        }
        byte[] validQuery = new byte[validHeaders.length + tailLen];
        System.arraycopy(validHeaders, 0, validQuery, 0, validHeaders.length);

        for(int i = validHeaders.length; i < validHeaders.length + tailLen; i++){
            validQuery[i] = tail[i - validHeaders.length];
        }


        return validQuery;
    }

    private static String parseHeader(String headerOfQuery){
        StringBuilder query = new StringBuilder();
        String[] queryLine = headerOfQuery.split("\\n");

        String[] firstLine = queryLine[0].split(" ");

        query.append("HTTP/1.0");

        for(int i = 1; i < firstLine.length; i++){
            query.append(" ").append(firstLine[i]);
        }
        query.append("\n");

        for(int i = 0; i < queryLine.length; i++){
            if(queryLine[i].length() >= 10){
                if(Objects.equals(queryLine[i].substring(0, 10), "Connection")){
                    queryLine[i] = "Connection: close";
                }
            }
        }

        for(int i = 1; i < queryLine.length; i++){
            query.append(queryLine[i]).append("\n");
        }

        return query.toString();
    }


    private static int symbolCount(String query){
        int count = 0;
        for (int i = 0; i < query.length() - 1; i++) {
            count++;
            if (query.charAt(i) == '\n') {
                if (query.charAt(i + 2) == '\n') {
                    count += 2;
                    break;
                }
            }
        }
        return count;
    }

}

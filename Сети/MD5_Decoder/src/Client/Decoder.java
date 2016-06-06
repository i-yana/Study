package Client;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Yana on 28.10.15.
 */
public class Decoder {

    private String md5;
    private long first;
    private long second;

    Decoder(String md5, long start, long end){
        this.md5 = md5;
        this.first = start;
        this.second = end;
    }

    public Object decrypt() throws NoSuchAlgorithmException {
        System.err.println("create a range...");
        MessageDigest md = null;
        md = MessageDigest.getInstance("MD5");
        int[] start = add(new int[]{0}, first);
        int[] end = add(new int[]{0}, second);
        int count = 0;
        while (!Arrays.equals(start, end)){
            String chars = getChars(start);
            if(count%500000==0) {
                System.err.println("check " + chars);
            }
            md.update(chars.getBytes(), 0 ,chars.length());
            //System.out.println(md5);
            String test = new BigInteger(1, md.digest()).toString(16);
            //System.out.println(test);
          //  System.out.println(md.toString());
            if (md5.equals(test)) {
                System.err.println("found " + chars);
                return first;
            }
            start = add(start, 1);
            count++;
        }
        return null;
    }

    public int[] add(int[] _start, long interval){
        int[] start = _start.clone();
        start[start.length - 1] += interval;
        for(int i = (start.length - 1); i>0; i--){
            if(start[i]>3) {
                int div = start[i];
                start[i] = start[i] % 4;
                start[i - 1] = start[i-1] + div/ 4;
            }
        }
        if(start[0] > 3){
            int[] result = new int[start.length + 1];
            System.arraycopy(start, 0, result, 1, start.length);

            int mod = start[0] % 4;
            result[1] = mod;
            result[0] = start[0] / 4 - 1;
            return add(result, 0);
        }else{
            int[] result = new int[start.length];
            for(int i = 0; i < start.length; i++){
                result[i] = (byte) start[i];
            }
            return result;
        }
    }


  /*  private long increment(long first, long second){
        for (int i = 0; i < second; i++) {
            long digit = 4;
            long last = first%10;
            long cur = 1;
            while (last!=4) {
                last = last+cur;
                cur = last/4;
                digit = digit*10+last%4;
                first = first/10;
                last = first%10;
            }
            if (cur==1) {
                digit = digit*10;
            }
            first = 4;
            while (digit!=4) {
                last = digit%10;
                digit = digit/10;
                first = first*10+last;
            }
        }
        return first;
    }*/

    private String getChars(int[] first) {
        StringBuilder charSet = new StringBuilder();
        for (int aFirst : first) {
            switch (aFirst) {
                case 0: {
                    charSet.append('A');
                    break;
                }
                case 1: {
                    charSet.append('C');
                    break;
                }
                case 2: {
                    charSet.append('G');
                    break;
                }
                case 3: {
                    charSet.append('T');
                    break;
                }
                case 4: {
                    break;
                }
            }
        }
        return String.valueOf(charSet);
    }
}

import tool.Parser;
import machine.LL.*;
import machine.LR.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Yana on 16.01.16.
 */
public class MainClass {
    public static void main(String[] args) {
        if(args.length!=1){
            System.err.println("Usage: file with grammar");
            return;
        }
        Parser parser = null;
        try {
            parser = new Parser(args[0]);
            LL ll = new LL(parser.getGrammar());
            LR1 lr1 = new LR1(parser.getGrammar());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println();
                System.out.println("Enter input:");
                String sq = br.readLine().replace(" ","");
                if (ll.analyses(sq)) {
                    System.out.println("LL::YES");
                } else {
                    System.out.println("LL::NO");
                }
                if(lr1.analyses(sq)){
                    System.out.println("LR::YES");
                } else {
                    System.out.println("LR::NO");
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

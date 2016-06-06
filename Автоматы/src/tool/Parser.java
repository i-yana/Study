package tool;

import machine.Grammar;
import machine.Rule;
import machine.RulesTable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Yana on 16.01.16.
 */
public class Parser {

    private String file;
    private ArrayList<String> nterm;
    private ArrayList<String> term;
    private RulesTable rules;


    public Parser(String file) throws IOException {
        this.file = file;
        nterm = new ArrayList<String>();
        term = new ArrayList<String>();
        rules = new RulesTable();
        parseFile();
    }

    private void parseFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        ArrayList<String> lines = new ArrayList<String>();
        while((line = br.readLine())!=null){
            lines.add(line);
        }
        //read non term
        String[] nt = lines.get(0).split(" ");
        Collections.addAll(nterm, nt);
        //read term
        String[] t = lines.get(1).split(" ");
        Collections.addAll(term, t);
        //read rules
        for (int i = 2; i < lines.size(); i++) {
            line = lines.get(i);
            if(line.charAt(0)=='#'){
                System.out.println(line);
                continue;
            }
            String left = line.substring(0, line.indexOf("->"));
            String[] r = line.substring(line.indexOf("->")+2).split(" ");
            ArrayList<String> right = new ArrayList<String>();
            Collections.addAll(right, r);
            Rule rule = new Rule(left, right);
            rules.add(rule);
        }
    }
    public static ArrayList<String> splitIntoTokens(String in, ArrayList<String> term, ArrayList<String> nterm){
        int pos = 0;
        int offset = 1;
        ArrayList<String> lex = new ArrayList<String>();

        while(true) {
            if (offset > in.length()) {
                break;
            }
            if (term.contains(in.substring(pos, offset)) || nterm.contains(in.substring(pos,offset))){
                lex.add(in.substring(pos, offset));
                if(offset==in.length()){
                    break;
                }
                pos = offset;
                offset=offset+1;
            }
            else{
                offset++;
            }
        }

        if(offset!=in.length() || lex.size()==0){
            System.out.println("input string contains unknown symbols");
            return null;
        }
        lex.add("$");
        return lex;
    }

    public Grammar getGrammar(){
        return new Grammar(rules, nterm, term);
    }
}

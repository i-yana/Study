package machine.LL;

import javafx.util.Pair;
import machine.Grammar;
import machine.RulesTable;
import tool.FirstFollow;
import tool.Parser;
import tool.Transformer;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Yana on 16.01.16.
 */
public class LL {
    private static final String EPS = "eps";
    private static final String END = "$";
    private static final String ARROW = "->";
    private ArrayList<String> term;
    private ArrayList<String> nterm;
    private RulesTable rulesTable;
    private FirstFollow firstFollow;
    private TransitionTable table;


    public LL(Grammar grammar) {
        this.firstFollow = new FirstFollow(grammar);
        grammar = Transformer.deleteAllRecursion(grammar);
        this.term = grammar.getTerm();
        this.nterm = grammar.getNterm();
        this.rulesTable = grammar.getRulesTable();
        createTable();
    }

    private void createTable() {
        table = new TransitionTable();
        for(String nt: nterm){
            ArrayList<ArrayList<String>> rule = rulesTable.getRules(nt);
            for(ArrayList<String> oneRule: rule){
                ArrayList<String> first = firstFollow.multiFirst(oneRule);
                for(String symbol: first){
                    if(!symbol.equals(EPS)){
                        table.add(new Pair<String, String>(nt,symbol), oneRule);
                    }
                }
                if(first.contains(EPS)){
                    ArrayList<String> follow = firstFollow.getFollowSet(nt);
                    for(String symbol: follow){
                        if(symbol.equals(END)){
                            continue;
                        }
                        table.add(new Pair<String, String>(nt, symbol), oneRule);
                    }
                    if(follow.contains(END)){
                        table.add(new Pair<String, String>(nt,END), oneRule);
                    }
                }
            }
        }
    }

    public boolean analyses(String in) {
        ArrayList<String> inLexems = Parser.splitIntoTokens(in, term, nterm);
        if(inLexems==null){
            return false;
        }
        Stack<String> magazine = new Stack<String>();
        magazine.push(END);
        magazine.push(nterm.get(0));
        int i = 0;
        String X = magazine.peek();
        String input = inLexems.get(i);
        while(!X.equals(END)){
            System.out.print(magazine + " ");
            for (int j = i; j < inLexems.size(); j++) {
                System.out.print(inLexems.get(j)+ " ");
            }
            if(term.contains(X)){
                if (X.equals(input)) {
                    magazine.pop();
                    input = inLexems.get(++i);
                } else {
                    System.out.println();
                    return false;
                }
                System.out.println();
            }
            else if(nterm.contains(X)){
                if(table.get(X,input)!=null){
                    magazine.pop();
                    ArrayList<String> rule = table.get(X, input);
                    System.out.println(" " + X + ARROW + rule);
                    if(!rule.contains(EPS)) {
                        for(int j = rule.size()-1; j>=0; j--){
                            magazine.push(rule.get(j));
                        }
                    }
                }
                else{
                    System.out.println();
                    return false;
                }
            }
            else {
                System.out.println();
                return false;
            }
            X = magazine.peek();
        }
        if(!input.equals(END)){
            System.out.println();
            return false;
        }
        return true;
    }
}

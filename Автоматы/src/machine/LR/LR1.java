package machine.LR;

import machine.Rule;
import machine.RulesTable;
import tool.FirstFollow;
import machine.Grammar;
import tool.Parser;
import tool.Transformer;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Yana on 18.01.16.
 */
public class LR1 {

    private static final String BEGIN_SYMBOL = "BEGIN'";
    private static final String END = "$";
    private static final String E = "eps";
    private static final String SHIFT = "shift ";
    private static final String ACCEPT = "accept";
    private static final String REDUCE = "reduce ";
    private static final String ARROW = "->";
    private static Rule EXTENSION_RULE;
    private final ArrayList<String> term;
    private ArrayList<String> nterm;
    private RulesTable rulesTable;
    private ArrayList<ArrayList<Item>> C;
    FirstFollow firstFollow;
    private String[][] actionTable;
    private int[][] gotoTable;



    public LR1(Grammar grammar) {
        grammar = Transformer.deleteAllRecursion(grammar);
        this.term = grammar.getTerm();
        this.nterm = grammar.getNterm();
        this.rulesTable = grammar.getRulesTable();
        ArrayList<String> right = new ArrayList<String>();
        right.add(".");
        right.add(nterm.get(0));
        rulesTable.add(new Rule(BEGIN_SYMBOL,right));
        ArrayList<String> r = new ArrayList<String>();
        r.add(".");
        r.add(nterm.get(0));
        EXTENSION_RULE = new Rule(BEGIN_SYMBOL, r);
        System.out.println(nterm);
        System.out.println(term);
        System.out.println(rulesTable);
        firstFollow = new FirstFollow(grammar);
        items();
        actionTable = createActionTable();
        gotoTable = createGoToTable();
    }

    private ArrayList<Item> closure(ArrayList<Item> itemSet){
        boolean found;
        do {
            found = false;
            ArrayList<Item> temp = new ArrayList<Item>(itemSet);
            for(Item item: temp) {
                ArrayList<ArrayList<String>> rules = rulesTable.getRules(item.getRule().getSymbolAfterPoint());
                for (ArrayList<String> rule: rules) {
                    Rule itemRule = item.getRule();
                    ArrayList<String> inputFirst = new ArrayList<String>();
                    ArrayList<String> sr = itemRule.right();
                    int pInd = sr.indexOf(".");
                    if (pInd + 2 < sr.size()) {
                        for (int i = pInd + 2; i < sr.size(); i++) {
                            inputFirst.add(sr.get(i));
                        }
                    }
                    ArrayList<String> firstSet = new ArrayList<String>();
                    inputFirst.add(item.getTerminal());
                    if(inputFirst.size()==1 && inputFirst.contains(END)){
                        firstSet.add(END);
                    }
                    else{
                        firstSet = firstFollow.multiFirst(inputFirst);
                    }
                    System.out.println("input" + inputFirst);
                    System.out.println(firstSet);
                    for (String terminal : firstSet) {
                        if(terminal.equals(E)){
                            terminal = item.getTerminal();
                        }
                        ArrayList<String> tmp = new ArrayList<String>(rule);
                        ArrayList<String> n_right = new ArrayList<String>();
                        n_right.add(".");
                        for (String s : tmp) {
                            n_right.add(s);
                        }
                        if(n_right.contains(E)){
                            n_right.remove(E);
                        }
                        Rule n_rule = new Rule(item.getRule().getSymbolAfterPoint(), n_right);
                        Item n_item = new Item(n_rule, terminal);
                        if (!itemSet.contains(n_item)) {
                            itemSet.add(n_item);
                            found = true;
                        }
                    }
                }
            }

        }while (found);
        return itemSet;
    }

    ArrayList<Item> GoTo(ArrayList<Item> itemSet, String symbol){
        ArrayList<Item> out = new ArrayList<Item>();
        for(Item item: itemSet){
            Rule r = item.getRule();
            if(r.getSymbolAfterPoint().equals(symbol)){
                String left = r.left();
                ArrayList<String> right = new ArrayList<String>();
                int pos = r.getPointPosition();
                for (int i = 0; i < pos; i++) {
                    right.add(r.right().get(i));
                }
                right.add(symbol);
                right.add(".");
                for (int i = pos+2; i < r.right().size(); i++) {
                    right.add(r.right().get(i));
                }

                Rule rule = new Rule(left,right);
                out.add(new Item(rule, item.getTerminal()));
            }
        }
        return new ArrayList<Item>(closure(out));
    }

    private void items() {
        C = new ArrayList<ArrayList<Item>>();
        ArrayList<Item> beginSet = new ArrayList<Item>();
        beginSet.add(new Item(EXTENSION_RULE, END));
        C.add(closure(beginSet));
        ArrayList<String> grammar = new ArrayList<String>();
        grammar.addAll(nterm);
        grammar.addAll(term);
        boolean found;
        do {
            found = false;
            ArrayList<ArrayList<Item>> temp = new ArrayList<ArrayList<Item>>(C);
            for(ArrayList<Item> I: temp){
                for(String symbol: grammar){
                    ArrayList<Item> gotoSet = GoTo(I, symbol);
                    if(!gotoSet.isEmpty() && !isConsist(C, gotoSet)){
                        C.add(gotoSet);
                        found = true;
                    }
                }
            }

        }while (found);
        for (ArrayList<Item> items:C){
            System.out.println(C.indexOf(items));
            printItems(items);
        }
    }

    private void printItems(ArrayList<Item> items) {
        for(Item item: items){
            System.out.println(item);
        }
    }

    private boolean isConsist(ArrayList<ArrayList<Item>> c, ArrayList<Item> i) {
        for(ArrayList<Item> items: c){
            if(items.size()!=i.size()){
                continue;
            }
            if(items.containsAll(i)){
                return true;
            }
        }
        return false;
    }

    public String[][] createActionTable(){
        String[][] actionTable = new String[C.size()][term.size()+1];
        for(ArrayList<Item> states: C){
            for(Item item: states){
                String symbol = item.getRule().getSymbolAfterPoint();
                if(term.contains(symbol)) {
                    ArrayList<Item> gotoSet = GoTo(states, symbol);
                    int j;
                    if((j=getStateIndex(C, gotoSet))!=-1){
                        actionTable[C.indexOf(states)][term.indexOf(symbol)] = SHIFT+j;
                    }
                    else {
                        System.out.println("unknown error");
                    }
                }
                else if(symbol.isEmpty()){
                    if(item.getRule().left().equals(BEGIN_SYMBOL)){
                        actionTable[C.indexOf(states)][term.size()] = ACCEPT;
                    }
                    else{
                        int index;
                        if(item.getTerminal().equals(END)){
                            index = term.size();
                        }
                        else {
                            index = term.indexOf(item.getTerminal());
                        }
                        if(index==-1){
                            continue;
                        }
                        actionTable[C.indexOf(states)][index] = REDUCE + item.getRule();
                    }
                }
            }
        }

        for (int i = 0; i < C.size(); i++) {
            for (int j = 0; j < term.size() + 1; j++) {
                if(actionTable[i][j]==null){
                    actionTable[i][j] = "";
                }
            }
        }
        for (int i = 0; i < C.size(); i++) {
            for (int j = 0; j < term.size() + 1; j++) {
                System.out.print("["+i+"]["+j+"]"+": "+ (actionTable[i][j].equals("")?"          ":actionTable[i][j]));
            }
            System.out.println();
        }
        return actionTable;
    }

    private int[][] createGoToTable() {
        int[][] gotoTable = new int[C.size()][nterm.size()];
        for(ArrayList<Item> states: C){
            for(String nt: nterm){
                ArrayList<Item> gotoSet = GoTo(states, nt);
                int j;
                if((j = getStateIndex(C, gotoSet))!=-1){
                    gotoTable[C.indexOf(states)][nterm.indexOf(nt)] = j;
                }
            }
        }
        /*for (int i = 0; i < C.size(); i++) {
            for (int j = 0; j < nterm.size(); j++) {
                System.out.print(gotoTable[i][j]);
            }
            System.out.println();
        }*/
        return gotoTable;

    }

    private int getStateIndex(ArrayList<ArrayList<Item>> c, ArrayList<Item> i) {
        for(ArrayList<Item> items: c){
            if(items.size()!=i.size()){
                continue;
            }
            if(items.containsAll(i)){
                return c.indexOf(items);
            }
        }
        return -1;
    }

    public boolean analyses(String in){
        ArrayList<String> inLexems = Parser.splitIntoTokens(in, term, nterm);
        if(inLexems==null){
            return false;
        }
        Stack<Integer> magazine = new Stack<Integer>();
        magazine.push(0);
        int i = 0;
        String input = inLexems.get(i);;
        int s;
        while (true){
            s = magazine.peek();
            System.out.print(magazine + " " + input + "       ");

            if (actionTable[s][term.indexOf(input)==-1?term.size():term.indexOf(input)].contains(SHIFT)){
                System.out.println(actionTable[s][term.indexOf(input)==-1?term.size():term.indexOf(input)]);

                String tmp = actionTable[s][term.indexOf(input)];
                String t = tmp.substring(SHIFT.length(), tmp.length());
                magazine.push(Integer.parseInt(t));
                input = inLexems.get(++i);
            }
            else if(actionTable[s][term.indexOf(input)==-1?term.size():term.indexOf(input)].contains(REDUCE)){
                System.out.println(actionTable[s][term.indexOf(input)==-1?term.size():term.indexOf(input)]);

                String tmp = actionTable[s][term.indexOf(input)==-1?term.size():term.indexOf(input)];
                String left = tmp.substring(REDUCE.length(), tmp.indexOf(ARROW));
                String right = tmp.substring(tmp.indexOf(ARROW)+2);
                String[] terms = right.split(" ");
                for (String term1 : terms) {
                    if(term1.equals(".")){
                        continue;
                    }
                    magazine.pop();
                }
                s = magazine.peek();
                magazine.push(gotoTable[s][nterm.indexOf(left)]);
            }
            else if(actionTable[s][term.indexOf(input)==-1?term.size():term.indexOf(input)].contains(ACCEPT)){
                System.out.println(ACCEPT);
                return true;
            }
            else{
                System.out.println();
                return false;
            }
        }
    }

}

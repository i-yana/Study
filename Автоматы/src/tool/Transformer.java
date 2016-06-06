package tool;

import machine.Grammar;
import machine.Rule;
import machine.RulesTable;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Yana on 19.01.16.
 */
public class Transformer {
    private static final String E = "eps";
    private static final String NT = "NT";
    private static boolean isEPSILON;
    private static final int[][] binTable = {
            {0,0,0,0},
            {1,0,0,0},
            {0,1,0,0},
            {1,1,0,0},
            {0,0,1,0},
            {1,0,1,0},
            {0,1,1,0},
            {1,1,1,0},
            {0,0,0,1},
            {1,0,0,1},
            {0,1,0,1},
            {1,1,0,1},
            {0,0,1,1},
            {1,0,1,1},
            {0,1,1,1},
            {1,1,1,1}
    };

    /*  удаляет левую рекурсию
        Вход: грамматика без е-правил
        Выход: грамматика без левой рекурсии
     */
    public static Grammar deleteAllRecursion(Grammar grammar){
        RulesTable rulesTable = new RulesTable(grammar.getRulesTable());
        ArrayList<String> nterm = new ArrayList<String>(grammar.getNterm());
        rulesTable = deleteE_rules(rulesTable);
        int size = nterm.size();
        boolean isRecursion = false;
        boolean found;
        for (int i = 0; i < size; i++) {
            do {
                found = false;
                for (int j = 0; j < i; j++) {
                    ArrayList<ArrayList<String>> iRules = rulesTable.getRules(nterm.get(i));
                    for (ArrayList<String> oneiRule : iRules) {
                        ArrayList<String> lastPartRight;
                        if (oneiRule.get(0).equals(nterm.get(j))) {
                            isRecursion = true;
                            lastPartRight = new ArrayList<String>(oneiRule);
                            lastPartRight.remove(0);
                            rulesTable.delete(nterm.get(i), oneiRule);
                            ArrayList<ArrayList<String>> jRules = rulesTable.getRules(nterm.get(j));
                            for (ArrayList<String> onejRule : jRules) {
                                ArrayList<String> right = new ArrayList<String>(onejRule);
                                for (String s : lastPartRight) {
                                    right.add(s);
                                }
                                rulesTable.add(new Rule(nterm.get(i), right));
                            }
                            found = true;
                        }
                    }
                }
            }while(found);

            if(deleteRecursion(nterm.get(i), rulesTable)){
                isRecursion = true;
            }
        }
        if(!isRecursion){
            return grammar;
        }
        ArrayList<String> n_nterms = new ArrayList<String>();
        if(isEPSILON){
            String sym = NT;
            ArrayList<String> right = new ArrayList<String>();
            right.add(nterm.get(0));
            rulesTable.add(new Rule(sym, right));
            ArrayList<String> rightEpsilon = new ArrayList<String>();
            rightEpsilon.add(E);
            rulesTable.add(new Rule(sym, rightEpsilon));
            n_nterms.add(NT);
            for(String nt: nterm){
                n_nterms.add(nt);
            }
        }
        for(Rule rule: rulesTable.getAllRules()){
            if(!n_nterms.contains(rule.left())){
                n_nterms.add(rule.left());
            }
        }
        nterm = n_nterms;
        return new Grammar(rulesTable, nterm, grammar.getTerm());
    }
    /*  Удаляет непосредственную рекурсию для нетерминала

     */
    private static boolean deleteRecursion(String nt, RulesTable rulesTable){
        ArrayList<ArrayList<String>> Arules = rulesTable.getRules(nt);
        ArrayList<ArrayList<String>> Afirst = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> AnotFirst = new ArrayList<ArrayList<String>>();
        for(ArrayList<String> oneRule: Arules){
            if(oneRule.get(0).equals(nt)){
                Afirst.add(oneRule);
            }
            else{
                AnotFirst.add(oneRule);
            }
        }
        if(Afirst.isEmpty()){
            return false;
        }
        String symbol = nt+"'";
        if(AnotFirst.isEmpty()){
            ArrayList<String> n_right = new ArrayList<String>();
            n_right.add(symbol);
            rulesTable.add(new Rule(nt, n_right));
        }
        for(ArrayList<String> right: AnotFirst){
            ArrayList<String> n_right = new ArrayList<String>(right);
            n_right.add(symbol);
            if(n_right.size()>1 && n_right.contains(E)){
                n_right.remove(E);
            }
            rulesTable.add(new Rule(nt, n_right));
        }
        for(ArrayList<String> right: Afirst){
            ArrayList<String> n_right = new ArrayList<String>(right);
            n_right.remove(0);
            if(n_right.size()>1 && n_right.contains(E)){
                n_right.remove(E);
            }
            rulesTable.add(new Rule(symbol, n_right));
            ArrayList<String> withSymbol = new ArrayList<String>(n_right);
            withSymbol.add(symbol);
            if(withSymbol.size()>1 && withSymbol.contains(E)){
                withSymbol.remove(E);
            }
            rulesTable.add(new Rule(symbol, withSymbol));
            rulesTable.delete(nt, right);
        }
        ArrayList<String> eps = new ArrayList<String>();
        eps.add(E);
        rulesTable.add(new Rule(symbol, eps));

        return true;
    }

    /*
        Удаление е-правил
     */
    private static RulesTable deleteE_rules(RulesTable rulesTable){
        RulesTable n_table = new RulesTable(rulesTable);
        isEPSILON = false;

        for(Rule rule: n_table.getAllRules()){
            if(rule.right().contains(E)){
                isEPSILON = true;
            }
        }
        ArrayList<String> e_nterm = findE_nterm(n_table);

        for(Rule rule: n_table.getAllRules()){
            ArrayList<Integer> positions = new ArrayList<Integer>();
            for (int i = 0; i < rule.right().size(); i++) {
                String s = rule.right().get(i);
                if(e_nterm.contains(s)){
                    positions.add(i);
                }
            }
            for (int i = 0; i < Math.pow(2, positions.size()); i++) {
                ArrayList<String> n_rule = new ArrayList<String>(rule.right());
                for (int j = 0; j < positions.size(); j++) {
                    if(binTable[i][j] == 0){
                        int p = positions.get(j);
                        n_rule.set(p,"");
                    }
                }
                while(n_rule.contains("")) {
                    n_rule.remove("");
                }
                Rule rule1 = new Rule(rule.left(),n_rule);
                if(!n_table.contains(rule1) && !n_rule.isEmpty()) {
                    n_table.add(rule1);
                }
            }
        }
        ArrayList<Rule> tmp = n_table.getAllRules();
        for(Rule rule: tmp){
            if(rule.right().contains(E)) {
                n_table.delete(rule);
            }
        }
        return n_table;
    }

    /*
        Поиск нетерминалов, порождающих е-правила
     */
    private static ArrayList<String> findE_nterm(RulesTable n_table) {
        ArrayList<String> nt = new ArrayList<String>();
        for(Rule rule: n_table.getAllRules()){
            if(rule.right().contains(E)){
                nt.add(rule.left());
            }
        }
        boolean found;
        do {
            found = false;
            for (Rule rule : n_table.getAllRules()) {
                ArrayList<String> r = rule.right();
                if(r.equals(nt) && !nt.contains(rule.left())){
                    nt.add(rule.left());
                    found = true;
                }
            }
        }while (found);
        return nt;
    }

    public static void main(String[] args) {
        try {
            Parser parser = new Parser(args[0]);
            Grammar grammar = parser.getGrammar();
            System.out.println(deleteAllRecursion(grammar));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

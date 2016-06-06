package tool;

import machine.Grammar;
import machine.RulesTable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Yana on 16.01.16.
 */
public class FirstFollow {
    private static String EPS = "eps";
    private RulesTable rules;
    private ArrayList<String> nterm;
    private ArrayList<String> term;
    private HashMap<String, ArrayList<String>> firstSet = new HashMap<String, ArrayList<String>>();
    private HashMap<String, ArrayList<String>> followSet = new HashMap<String, ArrayList<String>>();

    public FirstFollow(Grammar grammar){
        Grammar g = Transformer.deleteAllRecursion(grammar);
        this.nterm = g.getNterm();
        this.term = g.getTerm();
        this.rules = g.getRulesTable();
        fillFirstSet();
        fillFollowSet();
    }



    private void fillFirstSet(){
        System.out.println("create first set");
        for(String nt: nterm){
            ArrayList<String> first = first(nt);
            firstSet.put(nt, first);
            System.out.println(nt + " " + first);
        }
    }

    private void fillFollowSet(){
        System.out.println("create follow set");
        for(String nt: nterm){
            ArrayList<String> flw = follow(nt);
            followSet.put(nt, flw);
            System.out.println(nt + " " + flw);
        }
    }


    public ArrayList<String> getFirstSet(String symbol){
        return firstSet.get(symbol);
    }

    public ArrayList<String> getFollowSet(String symbol){
        return followSet.get(symbol);
    }

    private ArrayList<String> first(String nt) {
        boolean found;
        ArrayList<String> str = new ArrayList<String>();
        ArrayList<String> tmp = new ArrayList<String>();

        ArrayList<ArrayList<String>> rights = rules.getRules(nt);
        for (ArrayList<String> oneRule: rights) {
            for(String symbol: oneRule){
                found=false;
                if(nterm.contains(symbol)){
                    str = first(symbol);
                    if(!(str.size()==1 && str.get(0).equals(EPS))){
                        for (String s:str) {
                            tmp.add(s);
                        }
                    }
                    found=true;
                }
                if(found){
                    if(str.contains(EPS)){
                        continue;
                    }
                }
                else{
                    tmp.add(symbol);
                }
                break;
            }
        }
        return new ArrayList<String>(removeDuplicates(tmp));
    }

    public ArrayList<String> multiFirst(ArrayList<String> sequence){

        ArrayList<String> temp = new ArrayList<String>();
        ArrayList<String> result = new ArrayList<String>();
        boolean contain = false;
        if(term.contains(sequence.get(0)) || sequence.get(0).equals(EPS)){
            temp.add(sequence.get(0));
        }
        else{
            ArrayList<String> first = firstSet.get(sequence.get(0));
            for(String symbol: first) {
                temp.add(symbol);
            }
        }
        if(temp.contains(EPS)){
            temp.remove(EPS);
            contain = true;
        }
        for(String symbol: temp){
            result.add(symbol);
        }

        for(String symbol: sequence){
            temp.clear();
            if(term.contains(symbol) || symbol.equals(EPS)){
                temp.add(symbol);
            }
            else{
                if(firstSet.containsKey(symbol)) {
                    ArrayList<String> first = firstSet.get(symbol);
                    for (String s : first) {
                        temp.add(s);
                    }
                }
            }
            if(temp.contains(EPS) && contain){
                temp.remove(EPS);
                for(String s: temp){
                    result.add(s);
                }
            }
            else{
                break;
            }
        }
        if(contain){
            System.out.println("asasdasdasdad");
            result.add(EPS);
        }
        return result;
    }

    private ArrayList<String> follow(String symbol){
        ArrayList<String> temp=new ArrayList<String>();
        boolean found = false;
        if(symbol.equals(nterm.get(0))) {
            temp.add("$");
        }

        for(String nt: nterm){
            ArrayList<ArrayList<String>> rights = rules.getRules(nt);
            for(ArrayList<String> oneRule: rights){
                for(String sym: oneRule){
                    if(sym.equals(symbol)){
                        if(oneRule.lastIndexOf(sym)==oneRule.size()-1) {
                            if (nterm.indexOf(nt) < nterm.indexOf(symbol)) {
                                ArrayList<String> set = followSet.get(nt);
                                for (String s : set) {
                                    temp.add(s);
                                }
                            }
                        }
                        else {
                            for (String nonterm : nterm) {
                                if (oneRule.get(oneRule.indexOf(sym) + 1).equals(nonterm)) {
                                    ArrayList<String> frst = firstSet.get(nonterm);
                                    for (String symb : frst) {
                                        if (symb.equals(EPS)) {
                                            if (oneRule.indexOf(nonterm) == oneRule.size() - 1) {
                                                ArrayList<String> flw = followSet.get(nt);
                                                if(flw==null){
                                                }
                                                else {
                                                    for (String f : flw) {
                                                        temp.add(f);
                                                    }
                                                }
                                            } else {
                                                ArrayList<String> flw = followSet.get(nonterm);
                                                for (String f : flw) {
                                                    temp.add(f);
                                                }
                                            }
                                        } else {
                                            temp.add(symb);
                                        }
                                        found = true;
                                    }
                                }
                            }
                            if (!found) {
                                temp.add(oneRule.get(oneRule.indexOf(sym) + 1));
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<String>(removeDuplicates(temp));
    }

    private ArrayList<String> removeDuplicates(ArrayList<String> tmp) {
        ArrayList<String> seen = new ArrayList<String>();
        for (String s:tmp){
            if(!seen.contains(s)){
                seen.add(s);
            }
        }
        return new ArrayList<String>(seen);
    }
}

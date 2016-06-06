package machine;

import java.util.ArrayList;

/**
 * Created by Yana on 19.01.16.
 */
public class Grammar {
    private RulesTable rulesTable;
    private ArrayList<String> nterm;
    private ArrayList<String> term;

    public Grammar(RulesTable rulesTable, ArrayList<String> nterm, ArrayList<String> term){
        this.nterm = nterm;
        this.rulesTable = rulesTable;
        this.term = term;
    }



    public RulesTable getRulesTable() {
        return rulesTable;
    }

    public ArrayList<String> getNterm() {
        return nterm;
    }

    public ArrayList<String> getTerm() {
        return term;
    }

    @Override
    public String toString(){
        return String.valueOf(nterm) + "\n" + term + "\n" + rulesTable;
    }
}

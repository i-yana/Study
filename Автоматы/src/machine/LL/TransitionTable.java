package machine.LL;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Yana on 16.01.16.
 */
public class TransitionTable {

    private HashMap<Pair<String, String>, ArrayList<String>> table = new HashMap<Pair<String, String>, ArrayList<String>>();

    public void add(Pair<String, String> pair, ArrayList<String> value){
        table.put(pair, value);
    }
    public ArrayList<String> get(String nterm, String term){
        for (Pair<String, String> pair : table.keySet()) {
            if (pair.getKey().equals(nterm) && pair.getValue().equals(term)) {
                return table.get(pair);
            }
        }
        return null;
    }

    @Override
    public String toString(){
        return table.toString();
    }
}

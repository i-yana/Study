package machine.LR;

import machine.Rule;

import java.util.ArrayList;

/**
 * Created by Yana on 17.01.16.
 */
public class State {
    private ArrayList<Rule> rules;
    private int index;
    private String token;

    public State(ArrayList<Rule> rules, int index, String token){
        this.rules = rules;
        this.index = index;
        this.token = token;
    }


    public ArrayList<Rule> getRules() {
        return rules;
    }

    public int getIndex() {
        return index;
    }

    public String getToken() {
        return token;
    }


}

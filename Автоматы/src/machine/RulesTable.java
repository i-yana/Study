package machine;

import java.util.ArrayList;

/**
 * Created by Yana on 16.01.16.
 */
public class RulesTable {
    ArrayList<Rule> rules;

    public RulesTable(RulesTable rulesTable){
        rules = new ArrayList<Rule>(rulesTable.getAllRules());
    }

    public RulesTable(){
        rules = new ArrayList<Rule>();
    }

    public void add(Rule rule)
    {
        if(!rules.contains(rule)) {
            rules.add(rule);
        }
    }

    public ArrayList<ArrayList<String>> getRules(String nterm){
        ArrayList<ArrayList<String>> rights = new ArrayList<ArrayList<String>>();
        for (Rule rule: rules){
            if(rule.left().equals(nterm)){
                rights.add(rule.right());
            }
        }
        return new ArrayList<ArrayList<String>>(rights);
    }
    public ArrayList<Rule> getAllRules(){
        return new ArrayList<Rule>(rules);
    }

    public boolean contains(Rule newRule) {
        for (Rule rule: rules){
            if(rule.left().equals(newRule.left()) && rule.right().equals(newRule.right())){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Rule rule: rules){
            sb.append(rule).append("\n");
        }
        return sb.toString();
    }

    public void delete(Rule rule) {
        int index = -1;
        for (int i = 0; i < rules.size(); i++) {
            if(rule.left().equals(rules.get(i).left()) && rule.right().equals(rules.get(i).right())){
                index = i;
            }
        }
        if(index==-1){
            return;
        }
        rules.remove(index);
    }

    public void delete(String s, ArrayList<String> oneiRule) {
        int index = -1;
        for (int i = 0; i < rules.size(); i++) {
            if(s.equals(rules.get(i).left()) && oneiRule.equals(rules.get(i).right())){
                index = i;
            }
        }
        if(index==-1){
            return;
        }
        rules.remove(index);
    }
}

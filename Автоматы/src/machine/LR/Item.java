package machine.LR;

import machine.Rule;

/**
 * Created by Yana on 18.01.16.
 */
public class Item {
    private Rule rule;
    private String terminal;

    public Item(Rule n_rule, String terminal) {
        this.rule = n_rule;
        this.terminal = terminal;
    }


    public Rule getRule() {
        return rule;
    }

    public String getTerminal() {
        return terminal;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Item o = (Item)other;
        return rule.left().equals(o.getRule().left()) && rule.right().equals(o.getRule().right()) && terminal.equals(o.getTerminal());
    }
    @Override
    public int hashCode() {
        int result = rule.left().hashCode();
        result = 31 * result + rule.right().hashCode() + terminal.hashCode();
        return result;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(rule).append(", ").append(terminal);
        return sb.toString();
    }

}

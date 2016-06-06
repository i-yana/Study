package machine;

import java.util.ArrayList;

/**
 * Created by Yana on 16.01.16.
 */
public class Rule {

    private String left;
    private ArrayList<String> right;

    public Rule(String left, ArrayList<String> right){
        this.left = left;
        this.right = right;
    }

    public String left(){
        return left;
    }
    public ArrayList<String> right(){
        return right;
    }
    public String getSymbolAfterPoint(){
        if(!right.contains(".")){
            return null;
        }
        if(right.size()<=right.indexOf(".")+1){
            return "";
        }
        return right.get(right.indexOf(".")+1);
    }
    public Integer getPointPosition(){
        if(!right.contains(".")){
            return null;
        }
        return right.indexOf(".");
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Rule o = (Rule)other;
        return left().equals(o.left) && right().equals(o.right);
    }
    @Override
    public int hashCode() {
        int result = left().hashCode();
        result = 31 * result + right().hashCode();
        return result;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(left).append("->");
        for(String r: right()){
            sb.append(r).append(" ");
        }
        return sb.toString();
    }
}

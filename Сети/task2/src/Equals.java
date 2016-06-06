/**
 * Created by Yana on 16.09.15.
 */
public class Equals  {

    private double x0 = 0;
    private static final double EPS = 1e-10;

    double f(double x){
        return Math.exp(x)-2;
    }

    double g(double x){
        return Math.log(x+2);
    }

    public double fundFirstRoot(){
        double x = x0;
        double xNext = f(x);
        int iterations = 1;
        while(Math.abs(x-xNext)>EPS){
            x = xNext;
            xNext = f(x);
            iterations++;
        }
        System.out.println("first root " + xNext + " was found in "+iterations + " iterations");
        return xNext;
    }

    public double findSecondRoot(){
        double x = x0;
        double xNext = g(x);
        int iterations = 1;
        while(Math.abs(x-xNext)>EPS){
            x = xNext;
            xNext = g(x);
            iterations++;
        }
        System.out.println("second root " + xNext + " was found in "+iterations + " iterations");
        return xNext;
    }

    public double generalFunction(double x){
        return Math.exp(x)-x-2;
    }

    public static void main(String[] args) {
        Equals equals = new Equals();
        System.out.println("eps = "+ EPS);
        System.out.println("f(x1) = " + equals.generalFunction(equals.fundFirstRoot()));
        System.out.println("f(x2) = " + equals.generalFunction(equals.findSecondRoot()));
    }
}

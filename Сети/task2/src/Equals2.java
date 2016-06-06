/**
 * Created by Yana on 16.09.15.
 */
public class Equals2 {
    private static final double EPS = 1e-10;
    private static final double x0 = 0;

    public double f(double x){
        return x*x*x*x-1;
    }

    public double g(double x){
        return Math.pow((x + 1), 1. / 4.);
    }

    public double findFirstRoot(){
        double x = -0.1;
        double xNext = -g(x);
        int iterations = 1;
        while(Math.abs(x-xNext)>EPS){
            x = xNext;
            xNext = -g(x);
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
        return x*x*x*x-x-1;
    }

    public static void main(String[] args) {
        Equals2 equals = new Equals2();
        equals.findFirstRoot();
        equals.findSecondRoot();
    }
}

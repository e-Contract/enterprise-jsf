package be.e_contract.jsf.taglib;

public class GreatestCommonDivisor {

    public static long gcd(long a, long b) {
        if (a <= b) {
            throw new IllegalArgumentException("a <= b");
        }
        while (b > 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }
}

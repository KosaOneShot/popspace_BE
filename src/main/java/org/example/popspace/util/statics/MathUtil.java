package org.example.popspace.util.statics;

public class MathUtil {

    public static double toPercent(long numerator, long denominator) {
        if (denominator == 0) return 0.0;
        return ((double) numerator * 100) / denominator;
    }
}

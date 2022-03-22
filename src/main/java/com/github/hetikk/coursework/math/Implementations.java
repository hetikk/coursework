package com.github.hetikk.coursework.math;

import com.github.hetikk.coursework.CalculationInput;
import com.github.hetikk.coursework.CalculationOutput;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Implementations {

    public static CalculationOutput method1(CalculationInput input) {
        long time = -System.currentTimeMillis();
        double s = 0;

        for (int i = 1; i < input.n; i++)
            s += f(input.func, input.a + input.h * i);

        s = input.h * ((f(input.func, input.a) + f(input.func, input.b)) / 2 + s);
        time += System.currentTimeMillis();

        return new CalculationOutput(s, time);
    }

    public static CalculationOutput method2(CalculationInput input) {
        double s = 0;
        long time = -System.currentTimeMillis();

        for (double i = input.a; i <= input.b; i += input.h)
            s += f(input.func, i) * input.h;

        time += System.currentTimeMillis();
        return new CalculationOutput(s, time);
    }

    public static CalculationOutput method3(CalculationInput input) {
        double s = 0;
        long time = -System.currentTimeMillis();

        double x;
        for (int i = 1; i <= input.n; i++) {
            x = input.a + i * input.h;
            s += f(input.func, x) * input.h;
        }

        time += System.currentTimeMillis();
        return new CalculationOutput(s, time);
    }

    public static CalculationOutput method4(CalculationInput input) {
        double s = 0;
        double x = input.a + input.h / 2;
        long time = -System.currentTimeMillis();

        for (int i = 1; i < input.n; i++) {
            s += f(input.func, x);
            x += input.h;
        }

        time += System.currentTimeMillis();
        return new CalculationOutput(s, time);
    }

    public static CalculationOutput method5(CalculationInput input) {
        double[] x = new double[input.n];
        double s, sum = 0, sum2 = 0;
        long time = -System.currentTimeMillis();

        for (int i = 0; i < input.n; i++)
            x[i] = input.a + i * input.h;

        for (int i = 1; i < input.n; i++) {
            sum += f(input.func, x[i]);
            sum2 += f(input.func, (x[i - 1] + x[i]) / 2);
        }

        s = input.h / 6 * (f(input.func, input.a) + f(input.func, input.b) + 2 * sum + 4 * (sum2 + input.b));
        time += System.currentTimeMillis();

        return new CalculationOutput(s, time);
    }

    public static double f(String func, double x) {
        return new ExpressionBuilder(func)
                .variables("x")
                .build()
                .setVariable("x", x)
                .evaluate();
    }

}

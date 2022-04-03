package com.github.hetikk.coursework.math;

import com.github.hetikk.coursework.CalculationInput;
import com.github.hetikk.coursework.CalculationOutput;
import lombok.SneakyThrows;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.DoubleStream;

public class Implementations {

    public static CalculationOutput method1(CalculationInput input) {
        long time = -System.currentTimeMillis();

        double width = (input.b - input.a) / input.n;

        double trapezoidal_integral = 0;
        for (int step = 0; step < input.n; step++) {
            double x1 = input.a + step * width;
            double x2 = input.a + (step + 1) * width;
            trapezoidal_integral += 0.5 * (x2 - x1) * (f(input.func, x1) + f(input.func, x2));
        }

        time += System.currentTimeMillis();
        return new CalculationOutput(trapezoidal_integral, time);
    }

    @SneakyThrows
    public static CalculationOutput method1_multithreaded(CalculationInput input) {
        long time = -System.currentTimeMillis();

        final double width = (input.b - input.a) / input.n;

        List<Callable<Double>> callables = new ArrayList<>();

        int d = input.n < input.threadCount ? input.n : input.n / input.threadCount;
        int start = 0, tmpStart = 0;
        while (start < input.n) {
            start += d;
            if (start > input.n) start = input.n;
            System.out.printf("[%d - %d]\n", tmpStart, start);

            int finalTmpStart = tmpStart;
            int finalStart = start;

            callables.add(() -> {
                double res = 0;
                for (int step = finalTmpStart; step < finalStart; step++) {
                    double x1 = input.a + step * width;
                    double x2 = input.a + (step + 1) * width;
                    res += 0.5 * (x2 - x1) * (f(input.func, x1) + f(input.func, x2));
                }
                return res;
            });

            tmpStart = start;
        }

        ExecutorService executor = Executors.newFixedThreadPool(input.threadCount);
        double trapezoidal_integral = executor.invokeAll(callables)
                .stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMapToDouble(DoubleStream::of)
                .sum();

        time += System.currentTimeMillis();
        return new CalculationOutput(trapezoidal_integral, time);
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

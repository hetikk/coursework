package com.github.hetikk.coursework.math;

import com.github.hetikk.coursework.CalculationInput;
import com.github.hetikk.coursework.CalculationOutput;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.stream.DoubleStream;

public class Implementations {

    private static CyclicBarrier BARRIER;
    private static CyclicBarrier BARRIER;

    public static CalculationOutput method1(CalculationInput input) {
        long time = -System.currentTimeMillis();

//        double width = (input.b - input.a) / input.n;
//
//        double trapezoidal_integral = 0;
//        for (int step = 0; step < input.n; step++) {
//            double x1 = input.a + step * width;
//            double x2 = input.a + (step + 1) * width;
//            trapezoidal_integral += 0.5 * (x2 - x1) * (f(input.func, x1) + f(input.func, x2));
//        }

        double s = 0;

        for (int i = 1; i < input.n; i++)
            s += f(input.func, input.a + input.h * i);

        s = input.h * ((f(input.func, input.a) + f(input.func, input.b)) / 2 + s);
        time += System.currentTimeMillis();

        time += System.currentTimeMillis();
        return new CalculationOutput(s, time);
    }

    @SneakyThrows
    public static CalculationOutput method1_multithreaded(CalculationInput input) {
        long time = -System.currentTimeMillis();


        BARRIER = new CyclicBarrier(input.threadCount, () -> {
            System.out.println();
        });
        BARRIER = new CyclicBarrier(input.threadCount, () -> {
            System.out.println();
        });
        AtomicDouble globalRes = new AtomicDouble(0.0);

        int d = input.n < input.threadCount ? input.n : input.n / input.threadCount;
        int start = 0, tmpStart = 0;
        int threadId = 0;
        while (start < input.n) {
            start += d;
            if (start > input.n) start = input.n;

            int finalTmpStart = tmpStart;
            int finalStart = start;

            new Thread(new Worker(input, finalTmpStart, finalStart, globalRes), "thread-" + (threadId++)).start();

            tmpStart = start;
        }

        double res = input.h * ((f(input.func, input.a) + f(input.func, input.b)) / 2 + globalRes.get());

        time += System.currentTimeMillis();
        return new CalculationOutput(res, time);
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

    @RequiredArgsConstructor
    public static class Worker implements Runnable {

        private final CalculationInput input;
        private final int localA;
        private final int localB;
        private final AtomicDouble globalRes;

        @Override
        @SneakyThrows
        public void run() {
            double res = 0;

            int step = 5, currentStep = 0;

            for (int i = localA; i < localB; i++) {
                if ((++currentStep) == step) {
                    System.out.printf("%s [%d - %d; %d] = %.4f\n", Thread.currentThread().getName(), localA, localB, i, res);
                    BARRIER.await();
                    currentStep = 0;
                }
                res += f(input.func, input.a + input.h * i);
            }
            globalRes.addAndGet(res);

            BARRIER.await();
        }

    }

}

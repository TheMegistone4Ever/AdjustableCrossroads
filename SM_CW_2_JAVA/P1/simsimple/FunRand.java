package SM_CW_2_JAVA.P1.simsimple;

import java.util.Random;

public class FunRand {
    /**
     * Generates a random value according to an exponential
     * distribution
     *
     * @param timeMean mean value
     * @return a random value according to an exponential
     * distribution
     */
    public static double Exp(double timeMean) {
        double a = Math.random();
        while (a == 0) {
            a = Math.random();
        }
        return -timeMean * Math.log(a);
    }

    /**
     * Generates a random value according to a uniform
     * distribution
     *
     * @param timeMin minimum value
     * @param timeMax maximum value
     * @return a random value according to a uniform distribution
     */
    public static double Unif(double timeMin, double timeMax) {
        double a = Math.random();
        while (a == 0) {
            a = Math.random();
        }
        return timeMin + a * (timeMax - timeMin);
    }

    /**
     * Generates a random value according to a normal (Gauss)
     * distribution
     *
     * @param timeMean      mean value
     * @param timeDeviation deviation
     * @return a random value according to a normal (Gauss)
     * distribution
     */
    public static double Norm(double timeMean, double timeDeviation) {
        return timeMean + timeDeviation * (new Random()).nextGaussian();
    }

    /**
     * Generates a random value according to an Erlang distribution
     *
     * @param delayMean k (integer)
     * @param delayDev  lambda
     * @return a random value according to an Erlang distribution
     */
    public static double Erlang(double delayMean, double delayDev) {
        double product = 1.;
        for (int i = 0; i < (int) delayMean; ++i) {
            double a = Math.random();
            while (a == 0) {
                a = Math.random();
            }
            product *= a;
        }
        return -Math.log(product) / delayDev;
    }
}

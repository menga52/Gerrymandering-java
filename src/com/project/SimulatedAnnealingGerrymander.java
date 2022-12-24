package com.project;

import com.project.data.CONSTANTS;
import com.project.data.Parameters;

import java.util.function.Consumer;

public class SimulatedAnnealingGerrymander implements Gerrymander {
    public SAParams specs;
    public void initialize() {
        specs.initialize();
    }
    public int[][] gerrymander(Parameters params, int preferred_candidate) {
        double temperature = 0.3;
        int[][] ret = params.getMap();
        Metrics metrics = new Metrics(params);
        double standard = metrics.evaluateMap(ret, preferred_candidate);
        while(temperature>specs.end_temp) {
            int[][] alternative = params.getMap(ret).new_districts;
            double alternative_metric = metrics.evaluateMap(alternative, preferred_candidate);
            if(standard<alternative_metric || Utilities.random()<temperature) {
                standard = alternative_metric;
                ret = alternative;
            }
            temperature = specs.nextTemperature();
        }
//        System.out.println(standard);
        return ret;
    }
    public SimulatedAnnealingGerrymander() {
        this.specs = new SAParams();
    }
    public static class SAParams {
        public double init_temp;
        public double end_temp;
        CoolingFunction cooler;
        public SAParams() {
            this.init_temp = CONSTANTS.DEFAULT_INITIAL_TEMPERATURE;
            this.end_temp = CONSTANTS.DEFAULT_END_TEMPERATURE;
            this.cooler = new GeometricCooler(CONSTANTS.DEFAULT_TEMPERATURE_RATIO, this.init_temp);
        }
        public void initialize() {
            cooler.initialize();
        }
        public SAParams setEndTemp(double end_temp) {
            this.end_temp = end_temp;
            return this;
        }
        public double nextTemperature() {
            return cooler.cool();
        }
        public SAParams setInitTemp(double init_temp) {
            this.init_temp = init_temp;
            return this;
        }
        public SAParams setCooler(CoolingFunction cooler) {
            this.cooler = cooler;
            return this;
        }
        public MultiplicativeCooler linearMultiplicativeCooler(double alpha, double initial_temperature) {
            return new MultiplicativeCooler(alpha, initial_temperature, 1);
        }
        public MultiplicativeCooler quadraticMultiplicativeCooler(double alpha, double initial_temperature) {
            return new MultiplicativeCooler(alpha, initial_temperature, 2);
        }

        public class GeometricCooler implements CoolingFunction {
            public double ratio;
            public double current_temperature;
            public double initial_temperature;
            public GeometricCooler(double ratio, double initial_temperature) {
                this.initial_temperature = initial_temperature;
                this.ratio = ratio;
                this.current_temperature = initial_temperature;
            }
            public double cool() {
                return current_temperature *= ratio;
            }
            public void initialize() {
                current_temperature = initial_temperature;
            }
        }
        public class MultiplicativeCooler implements CoolingFunction {
            public double alpha;
            private int index = 0;
            public double initial_temperature;
            public double exponent;
            public MultiplicativeCooler(double alpha, double initial_temperature, double exponent) {
                this.alpha = alpha;
                this.initial_temperature = initial_temperature;
                this.exponent = exponent;
            }
            public double cool() {
                return initial_temperature/(1+alpha*index++);
            }
            public void initialize() {
                this.index = 0;
            }
        }
        public class LogarithmicMultiplicativeCooler implements CoolingFunction {
            public double alpha;
            private int index = 0;
            public double initial_temperature;
            public LogarithmicMultiplicativeCooler(double alpha, double initial_temperature) {
                this.alpha = alpha;
                this.initial_temperature = initial_temperature;
            }
            public double cool() {
                return initial_temperature/(1+alpha*Math.log(index));
            }
            public void initialize() {
                this.index = 0;
            }
        }
    }
    interface CoolingFunction {
        double cool();
        void initialize();
    }
}

package com.project;

import com.project.data.CONSTANTS;
import com.project.data.Parameters;
import com.project.data.State;

import java.util.Random;

public class Runner {
    static final String filename = "states/alternatingRowsVotingOutcome.txt";
    static final int num_districts = 10;
    static final int iterations = 100;
    static final int preferred_candidate = 0;
    public static void main(String a[]) {
//        CONSTANTS.setRandom(new Random(2));
        State state = new State(filename);
        SimulatedAnnealingGerrymander gerry = new SimulatedAnnealingGerrymander();
        Parameters p = new Parameters(state.voting_outcome, num_districts);
        Runner runner = new Runner();
        runner.trial1(p, gerry.specs);
        double proportion = runner.run(gerry, state, p, iterations);
        System.out.println(proportion);
    }

    private void trial1(Parameters params, SimulatedAnnealingGerrymander.SAParams specs) {
        params.setSearchRadius(5)
//                .setTieBreaker(new CONSTANTS.BestCase())
        ;
        specs.setInitTemp(1).setEndTemp(0.0001);
        ((SimulatedAnnealingGerrymander.SAParams.GeometricCooler)specs.cooler)
                .ratio = 0.9;
    }

    public double run(Gerrymander gerry, State state, Parameters p, int iterations) {
        double sum_of_proportions = 0; //for computing an average
        Metrics metrics = new Metrics(p);
        for(int i=0; i<iterations; i++) {
            gerry.initialize();
            int[][] output = gerry.gerrymander(p, preferred_candidate);
            State temp = new State(state.voting_outcome);
            temp.setDistricts(output);
            sum_of_proportions += metrics.computeVotingOutcomeMetric(temp, preferred_candidate);
        }
        return sum_of_proportions / iterations;
    }

    public double optimize(String file, State state, String field) {
        return -1;
    }
}

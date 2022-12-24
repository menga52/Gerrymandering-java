package com.project.tests;

import com.project.Metrics;
import com.project.data.CONSTANTS;
import com.project.data.Parameters;
import com.project.data.State;

public class VotingOutcomeTest implements Test {
    @Override
    public boolean run() {
        State state = new State("states/alternatingRowsState");
        Parameters p = new Parameters(state.voting_outcome, 10);
        Metrics metrics = new Metrics(p);
        if(Math.abs(0.5-metrics.computeVotingOutcomeMetric(state, 0))>=0.01) return false;
        p.setTieBreaker(new CONSTANTS.BestCase());
        if(Math.abs(0.7-metrics.computeVotingOutcomeMetric(state, 0))>=0.01) return false;
        p.setTieBreaker(new CONSTANTS.RandomCase());
        for(int i=0; i<10; i++) {
            if(Math.abs(0.6-metrics.computeVotingOutcomeMetric(state, 0))>=0.11) return false;
        }
        return true;
    }
}

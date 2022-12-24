package com.project.tests;

import com.project.Metrics;
import com.project.Utilities;
import com.project.data.Pair;
import com.project.data.Parameters;
import com.project.data.State;

import java.util.HashSet;

public class DiscontinuityTest implements Test {

    @Override
    public boolean run() {
        State state = new State("states/gridState");
        Parameters p = new Parameters(state.voting_outcome, 2);
        Metrics metrics = new Metrics(p);
        if(Math.abs(2499 - metrics.computeDiscontinuityMetric(state, 2))>0.01) return false;
        if(Math.abs(49 - metrics.computeDiscontinuityMetric(state, 1)) > 0.01) return false;
        int[][] temp = state.districts;
        state.districts = state.voting_outcome;
        state.voting_outcome = temp;
        if(Math.abs(metrics.computeDiscontinuityMetric(state, 1))>0.01) return false;
        return (Math.abs(metrics.computeDiscontinuityMetric(state, 100))<0.01);
    }
}

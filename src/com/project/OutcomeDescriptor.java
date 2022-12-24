package com.project;

import com.project.data.Parameters;
import com.project.data.State;

public class OutcomeDescriptor {
    State state;
    int preferred_candidate;
    Metrics metrics;
    public OutcomeDescriptor(State state, int preferred_candidate, Metrics metrics) {
        this.state = state;
        this.preferred_candidate = preferred_candidate;
        this.metrics = metrics;
    }
    public String votingOutcomeDescription() {
        double district_proportion = metrics.computeVotingOutcome(state, preferred_candidate);
        double vote_proportion = computePrecinctProportion();
        int sigfigs = 3;
        return "Candidate " + String.valueOf(preferred_candidate)
                + " received " + asPercentage(vote_proportion, sigfigs)
                + " of the votes and " + asPercentage(district_proportion, sigfigs)
                + " of the districts";
    }
    public String compactnessDescription() {
        //TODO: write descriptions for compactness and other metrics
        return null;
    }
    private double computePrecinctProportion() {
        int good_votes = 0;
        int total_votes = 0;
        for(int i=0; i<state.voting_outcome.length; i++) {
            total_votes += state.voting_outcome[i].length;
            for(int j=0; j<state.voting_outcome[i].length; j++) {
                if(state.voting_outcome[i][j] == preferred_candidate) good_votes++;
            }
        }
        return (double) good_votes / total_votes;
    }
    private String asPercentage(double d, int sigfigs) {
        d *= Math.pow(10, sigfigs);
        d = (int) d;
        return String.valueOf(d/Math.pow(10, sigfigs-2)) + "%";
    }
}

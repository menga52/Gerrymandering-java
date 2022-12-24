package com.project.tests;

import com.project.Utilities;
import com.project.data.State;

public class StateReadingTest implements Test {

    public boolean run() {
        State state = new State("states/alternatingRowsVotingOutcome");
        if (!("0 0 0 0 0 0 0 0 0 0 \n" +
                "1 1 1 1 1 1 1 1 1 1 \n" +
                "0 0 0 0 0 0 0 0 0 0 \n" +
                "1 1 1 1 1 1 1 1 1 1 \n" +
                "0 0 0 0 0 0 0 0 0 0 \n" +
                "1 1 1 1 1 1 1 1 1 1 \n" +
                "0 0 0 0 0 0 0 0 0 0 \n" +
                "1 1 1 1 1 1 1 1 1 1 \n" +
                "0 0 0 0 0 0 0 0 0 0 \n" +
                "1 1 1 1 1 1 1 1 1 1 \n").equals(Utilities.printMap(state.voting_outcome))) {
            return false;
        }
        return true;
    }
}

package com.project;

import com.project.data.CONSTANTS;
import com.project.data.MapUpdateOutput;
import com.project.data.Parameters;
import com.project.data.State;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Random;

public class Main {

    static final int size_scale = 50;
    static final long millisecond_delay = 0;
    static final int num_districts = 10;
    static final int preferred_candidate = 0;
    static final int search_radius = 5;

    public static void main(String[] args) {
        CONSTANTS.setRandom(new Random(1));
        State state = new State("states/alternatingRowsVotingOutcome.txt");

        int dim_x = state.voting_outcome.length;
        int dim_y = state.voting_outcome[0].length;
//        state.districts = Utilities.randomMap(dim_x, dim_y, num_districts);
        Display display = new Display(state);
        Parameters p = new Parameters(state.voting_outcome, num_districts).setSearchRadius(search_radius)
                .setDisplay(display, millisecond_delay)
//                .setMapGenerator(new CrazyMapGenerator())
                ;
        state.districts = p.getMap();
        display.setUndecorated(true);
        display.setSize(size_scale*dim_x, size_scale*dim_y);
        display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display.setVisible(true);
        Gerrymander worker = new SimulatedAnnealingGerrymander();
        state.districts = worker.gerrymander(p, preferred_candidate);
        System.out.println(Utilities.printMap(state.districts));
        display.repaint();
        System.out.println("finished running");
        System.out.println(new OutcomeDescriptor(state, preferred_candidate, new Metrics(p)).votingOutcomeDescription());
    }
}

package com.project;

import com.project.data.*;

import java.util.*;
import java.util.stream.DoubleStream;

public class Metrics {
    private HashMap<Integer, Integer> final_district_voting_outcomes = null;
    private final HashMap<State, HashMap<Integer, HashSet<Pair>>> stored_state_district_lists = new HashMap<>();
    private final Parameters parameters;

    public Metrics(Parameters params) {
        this.parameters = params;
    }

    public double evaluateMap(int[][] districts, int preferred_candidate) {
        State state = new State(parameters.voting_outcome).setDistricts(districts).clone();
        double[] metrics = new double[Characteristic.values().length];
        metrics[Characteristic.VOTING_OUTCOME.value] = computeVotingOutcomeMetric(state, preferred_candidate);
        metrics[Characteristic.COMPACTNESS.value] = computeCompactnessMetric(state, parameters.compactness_exponent);
        metrics[Characteristic.PUNCTURES.value] = computePuncturesMetric(state, parameters.punctures_exponent);
        //TODO: resolve major error in computeDiscontinuityMetric
        metrics[Characteristic.CONNECTEDNESS.value] = 0;//computeDiscontinuityMetric(state, parameters.discontinuity_exponent);

        return Utilities.dotProduct(metrics, CONSTANTS.default_weights) / DoubleStream.of(CONSTANTS.default_weights).sum();
    }

    //TODO: use Monte Carlo to approximate a win for a candidate
    public double computeVotingOutcomeMetric(State state, int preferred_candidate) {
        return computeVotingOutcome(state, preferred_candidate);
    }

    public double computeDiscontinuityMetric(State state, double exponent) {
        HashMap<Integer, HashSet<Pair>> pairs_by_district;
        if(!stored_state_district_lists.containsKey(state)) {
            pairs_by_district = listPairsInDistricts(state);
            stored_state_district_lists.put(state, pairs_by_district);
        }
        else pairs_by_district = stored_state_district_lists.get(state);
        double sum_of_powers = 0.0;
        for(int district: pairs_by_district.keySet()) {
            int connected_components = count_components(state, district);
            sum_of_powers += Math.pow(connected_components, exponent);
        }
        int num_districts = pairs_by_district.size();
        return sum_of_powers/num_districts - 1;
    }

    public int count_components(State state, int district) {
        return Utilities.computeGroupsOfDistrict(state.districts, district).size();
    }

    public double computeVotingOutcome(State state, int preferred_candidate) {
        //we compute the ratio (good voting districts)/(bad voting districts)
        //the following map tracks how many districts each candidate won. key=candidate, value=win total. for this, a candidate is a party
        HashMap<Integer, Integer> district_outcomes = new HashMap<>();
        district_outcomes.put(preferred_candidate, 0);
        //this map tracks the vote totals of each candidate for each district. key=district, value=map with (key=candidate, val=votes). for this, candidate is individual politician
        HashMap<Integer, HashMap<Integer, Integer>> votes_by_district = new HashMap<>();
        for(int i=0; i<state.voting_outcome.length; i++) {
            for(int j=0; j<state.voting_outcome[0].length; j++) {
                int chosen_candidate = state.voting_outcome[i][j];
                int district = state.districts[i][j];
                if(!votes_by_district.containsKey(district)) {
                    HashMap<Integer, Integer> district_votes = new HashMap<>();
                    //key=candidate, val=vote total
                    district_votes.put(chosen_candidate, 1);
                    votes_by_district.put(district, district_votes);
                }
                else {
                    HashMap<Integer, Integer> district_votes = votes_by_district.get(district);
                    if(!district_votes.containsKey(chosen_candidate)) {
                        district_votes.put(chosen_candidate, 1);
                    }
                    else {
                        district_votes.put(chosen_candidate, 1+district_votes.get(chosen_candidate));
                    }
                }
            }
        }
        for(int district: votes_by_district.keySet()) {
            ArrayList<Integer> leaders = new ArrayList<>();
            leaders.add(-1);
            int vote_max = -1;
            HashMap<Integer, Integer> district_votes = votes_by_district.get(district);
            for(int candidate: district_votes.keySet()) {
                int votes_for_candidate = district_votes.get(candidate);
                if(votes_for_candidate > vote_max) {
                    leaders = new ArrayList<>();
                    leaders.add(candidate);
                    vote_max = votes_for_candidate;
                }
                else if(votes_for_candidate==vote_max) {
                    leaders.add(candidate);
                }
            }

            int randomly_chosen_winner = parameters.tiebreaker.pickWinner(leaders, preferred_candidate);
            if(!district_outcomes.containsKey(randomly_chosen_winner)) {
                district_outcomes.put(randomly_chosen_winner, 1);
            }
            else district_outcomes.put(randomly_chosen_winner, 1+district_outcomes.get(randomly_chosen_winner));
        }
        this.final_district_voting_outcomes = district_outcomes;
        return (float)district_outcomes.get(preferred_candidate) / votes_by_district.size();
    }

    public double computePuncturesMetric(State state, double exponent) {
        HashSet<Integer> districts = enumerateDistricts(state);
        /*
            The district should separate the state into components;
            the district itself is one component. Formally, a component
            would be an equivalence class such that there exists a continuous
            path between any two points within the component that stays
            out of the district but any path connecting two points in
            different components must pass through the district.

            We will check to see if a component is bounded by a border of
            the state. Otherwise, it must be surrounded on all sides by
            the district - a puncture.
         */
        double acc = 0;
        for(int district: districts) {
            HashSet<HashSet<Pair>> groups = Utilities.computeGroupsDividedByDistrict(state, district);
            int punctures = 0;
            for(HashSet<Pair> group: groups) { if(group_punctures_district(state, district, Utilities.getStart(group))) punctures++; }
            if(punctures>0) acc+=Math.pow(punctures, exponent);
        }
        return acc;
    }

    public double computeCompactnessMetric(State state, double exponent) {
        HashMap<Integer, HashSet<Pair>> pairs_by_district;
        if(!stored_state_district_lists.containsKey(state)) {
            pairs_by_district = listPairsInDistricts(state);
            stored_state_district_lists.put(state, pairs_by_district);
        }
        else pairs_by_district = stored_state_district_lists.get(state);
        double sum_of_powers = 0.0;
        for(int district: pairs_by_district.keySet()) {
            int min_x=state.districts.length, min_y=state.districts[0].length, max_x=-1, max_y=-1;
            for(Pair p: pairs_by_district.get(district)) {
                if(p.x>max_x) max_x = p.x;
                if(p.x<min_x) min_x = p.x;
                if(p.y>max_y) max_y = p.y;
                if(p.y<min_y) min_y = p.y;
            }
            int distance = Math.max(max_x-min_x, max_y-min_y);
            sum_of_powers += Math.pow(distance, exponent);
        }
        int num_districts = pairs_by_district.size();
        double num_precincts = state.districts.length*state.districts[0].length;
        double avg_district_size = Math.pow(num_precincts/num_districts, 0.5);
        double optimal_score = num_districts * Math.pow(avg_district_size, exponent);
        return optimal_score / sum_of_powers;
    }

    public boolean group_punctures_district(State state, int district, Pair start) {
        LinkedList<Pair> queue = new LinkedList<>();
        queue.add(start);
        int[][] d = new int[state.districts.length][state.districts[0].length];
        for(int i=0; i<d.length; i++) {
            for(int j=0; j<d[0].length; j++) {
                if(state.districts[i][j]==district) d[i][j] = 1;
                else d[i][j] = -1;
            }
        }
        while(!queue.isEmpty()) {
            Pair current = queue.poll();
            for(Pair p: Utilities.getAllNeighbors(d, current)) {
                if(!Utilities.inBounds(d, p)) return false;
                if(d[p.x][p.y]==-1) {
                    d[p.x][p.y] = 0;
                    queue.add(p);
                }
            }
        }
        return true;
    }


    //returns a set of all the ids of districts
    public HashSet<Integer> enumerateDistricts(State state) {
        int[][] d = state.districts;
        HashSet<Integer> ret = new HashSet<>();
        for (int[] ints : d) {
            for (int j = 0; j < d[0].length; j++) {
                ret.add(ints[j]);
            }
        }
        return ret;
    }

    public HashMap<Integer, HashSet<Pair>> listPairsInDistricts(State state) {
        HashMap<Integer, HashSet<Pair>> ret = new HashMap<>();
        for(int i=0; i<state.districts.length; i++) {
            for(int j=0; j<state.districts[0].length; j++) {
                int district = state.districts[i][j];
                if(!ret.containsKey(district)) {
                    HashSet<Pair> district_set = new HashSet<>();
                    district_set.add(new Pair(i, j));
                    ret.put(district, district_set);
                }
                else ret.get(district).add(new Pair(i, j));
            }
        }
        return ret;
    }

}

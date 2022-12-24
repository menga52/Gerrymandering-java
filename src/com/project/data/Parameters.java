package com.project.data;

import com.project.ContinuousMapGenerator;
import com.project.Display;
import com.project.MapGenerator;
import com.project.Utilities;

public class Parameters {
    public int[][] voting_outcome;
    public double[] weights = new double[Characteristic.values().length];
    public double discontinuity_exponent;
    public double compactness_exponent;
    public double punctures_exponent;
    public int num_districts;
    public MapGenerator mapGenerator;
    private boolean weight_districts_by_size = false;
    public int search_radius = CONSTANTS.DEFAULT_SEARCH_RADIUS;
    public long delay = 0;
    public Display display = null;
    public CONSTANTS.TieBreaker tiebreaker = CONSTANTS.tie_breaker;

    public Parameters(int[][] voting_outcome, int num_districts) {
        this.voting_outcome = voting_outcome;
        for(Characteristic c: Characteristic.values()) {
            weights[c.value] = CONSTANTS.default_weights[c.value];
        }
        this.discontinuity_exponent = CONSTANTS.DEFAULT_CONNECTEDNESS_EXP;
        this.compactness_exponent = CONSTANTS.DEFAULT_COMPACTNESS_EXP;
        this.punctures_exponent = CONSTANTS.DEFAULT_PUNCTURES_EXP;
        this.num_districts = num_districts;
        this.mapGenerator = new ContinuousMapGenerator();
    }

    public int[][] getMap() {
        return mapGenerator.generateMap(voting_outcome.length, voting_outcome[0].length, num_districts);
    }
    public MapUpdateOutput getMap(int[][] old_map) {
        MapUpdateOutput output = mapGenerator.alterMap(old_map, num_districts, search_radius);
        if(this.display!=null && delay>100) {
            try{
                Thread.sleep(delay);
                display.repaint();
            }
            catch(InterruptedException e) { e.printStackTrace(); }
        }
        return output;
    }
    public Parameters setTieBreaker(CONSTANTS.TieBreaker tiebreaker) {
        this.tiebreaker = tiebreaker;
        return this;
    }
    public Parameters setDisplay(Display display, long delay) {
        this.display = display;
        this.delay = delay;
        return this;
    }
    public Parameters setSearchRadius(int search_radius) {
        this.search_radius = search_radius;
        return this;
    }
    public Parameters setMapGenerator(MapGenerator mapGenerator) {
        this.mapGenerator = mapGenerator;
        return this;
    }
    public Parameters setWeight(Characteristic c, double weight) {
        this.weights[c.value] = weight;
        this.discontinuity_exponent = 1.0;
        this.compactness_exponent = 2.0;
        return this;
    }
    public Parameters setConnectednessExponent(double val) {
        this.discontinuity_exponent = val;
        return this;
    }
    public Parameters setCompactnessExponent(double val) {
        this.compactness_exponent = val;
        return this;
    }
    public Parameters setPuncturesExponent(double val) {
        this.punctures_exponent = val;
        return this;
    }
    public Parameters setWeightDistrictsBySize(boolean weight) {
        this.weight_districts_by_size = weight;
        return this;
    }
}

package com.project.data;

import com.project.Utilities;

import java.util.List;
import java.util.Random;

public class CONSTANTS {
    /*
    default values
     */

    public static final double[] default_weights = new double[Characteristic.values().length];
    public static final double DEFAULT_COMPACTNESS_EXP = 2.0;
    public static final double DEFAULT_PUNCTURES_EXP = 0.0;// count total punctured districts
    public static final double DEFAULT_CONNECTEDNESS_EXP = 0.0;// count total discontinuous districts
    public static final int DEFAULT_SEARCH_RADIUS = 4;
    public static final double DEFAULT_TEMPERATURE_RATIO = 0.99;
    public static final double DEFAULT_INITIAL_TEMPERATURE = 0.3;
    public static final double DEFAULT_END_TEMPERATURE = 0.0005;
    static {
        default_weights[Characteristic.VOTING_OUTCOME.value] = 1.0;
        default_weights[Characteristic.COMPACTNESS.value] = 0;
        default_weights[Characteristic.PUNCTURES.value] = 0;
        default_weights[Characteristic.CONNECTEDNESS.value] = 0;
    }
    public static Random random = null;
    public static void setRandom(Random rand) {
        random = rand;
    }
    public static TieBreaker tie_breaker = new WorstCase();

    public interface TieBreaker {
        int pickWinner(List<Integer> leaders, int preferred_candidate);
    }
    public static class WorstCase implements TieBreaker {
        @Override
        public int pickWinner(List<Integer> leaders, int preferred_candidate) {
            if(leaders.size()>1) {
                if(leaders.get(0)==preferred_candidate) return leaders.get(1);
            }
            return leaders.get(0);
        }
    }
    public static class BestCase implements TieBreaker {
        @Override
        public int pickWinner(List<Integer> leaders, int preferred_candidate) {
            for(int leader: leaders) {
                if(leader == preferred_candidate) return leader;
            }
            return Utilities.getRandomItem(leaders);
        }
    }
    public static class RandomCase implements TieBreaker {
        @Override
        public int pickWinner(List<Integer> leaders, int preferred_candidate) {
            return Utilities.getRandomItem(leaders);
        }
    }
}

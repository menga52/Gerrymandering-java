package com.project.data;

import java.util.Set;

public class MapUpdateOutput {
    public Set<Pair> updates;
    public int[][] new_districts;

    public MapUpdateOutput(int[][] new_district, Set<Pair> updates) {
        this.new_districts = new_district;
        this.updates = updates;
    }

}

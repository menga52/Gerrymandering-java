package com.project;

import com.project.data.MapUpdateOutput;
import com.project.data.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CrazyMapGenerator implements MapGenerator {
    public int[][] generateMap(int dim_x, int dim_y, int num_districts) {
        int [][] ret = new int[dim_x][dim_y];
        int[] district_sizes = new int[num_districts];
        Arrays.fill(district_sizes, 0);
        for(int i=0; i<dim_x; i++) {
            for(int j=0; j<dim_y; j++) {
                int district = (int) (Utilities.random()*num_districts);
                ret[i][j] = district;
                district_sizes[district]++;
            }
        }
        boolean could_contain_empty_district = true;
        while(could_contain_empty_district) {
            could_contain_empty_district = false;
            for(int i=0; i<num_districts; i++) {
                if(district_sizes[i]<1) {
                    int rand_x = (int) (Utilities.random()*dim_x);
                    int rand_y = (int) (Utilities.random()*dim_y);
                    district_sizes[ret[rand_x][rand_y]]--;
                    district_sizes[i]++;
                    if(district_sizes[ret[rand_x][rand_y]]==0) could_contain_empty_district = true;
                    ret[rand_x][rand_y] = i;
                }
            }
        }
        return ret;
    }
    @Override
    public MapUpdateOutput alterMap(int[][] old_map, int num_districts, int distance) {
        int current_distance = 0;
        Set<Pair> updates = new HashSet<>();
        int[][] ret = Utilities.duplicate(old_map);
        while (current_distance < distance) {
            int x1 = (int) (Utilities.random() * ret.length);
            int y1 = (int) (Utilities.random() * ret[x1].length);
            int temp = ret[x1][y1];
            ret[x1][y1] = (int) (Utilities.random() * num_districts);
            if (!containsDistrict(ret, temp)) {
                ret[x1][y1] = temp;
                continue;
            }
            updates.add(new Pair(x1, y1));
            current_distance++;
        }
        return new MapUpdateOutput(ret, updates);
    }
    public boolean containsDistrict(int[][] map, int district) {
        for(int[] row: map) {
            for(int val: row) {
                if(val==district) return true;
            }
        }
        return false;
    }
    public boolean isLegal(int[][] map, int num_districts) {
        //repeated calls to containsDistrict would be less efficient
        int districts_remaining = num_districts;
        int[] districts = new int[num_districts];
        Arrays.fill(districts, 0);
        for(int[] row: map) {
            for(int entry: row) {
                districts[entry]++;
                if(districts[entry]<2) districts_remaining--;
            }
        }
        return districts_remaining==0;
    }
}

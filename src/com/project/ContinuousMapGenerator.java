package com.project;

import com.project.data.MapUpdateOutput;
import com.project.data.Pair;

import java.util.*;

public class ContinuousMapGenerator implements MapGenerator {
    public int[][] generateMap(int dim_x, int dim_y, int num_districts) {
        int[][] ret = new int[dim_x][dim_y];
        for(int[] row : ret) {
            Arrays.fill(row, -1);
        }
        LinkedList<Pair> queue = new LinkedList<>();
        int districts_created = 0;
        while(districts_created<num_districts) {
            int row = (int)(Utilities.random()*dim_x);
            int col = (int)(Utilities.random()*dim_y);
            if(Utilities.available(ret, row, col)) {
                ret[row][col] = districts_created++;
                queue.addAll(Utilities.getAvailableNeighbors(ret, row, col));
            }
        }

        while(!queue.isEmpty()) {
            Pair p = queue.poll();
            if (!Utilities.available(ret, p)) continue;
            List<Pair> assigned_neighbors = Utilities.getAssignedNeighbors(ret, p);
            Pair randomly_chosen_pair = Utilities.getRandomItem(assigned_neighbors);
            int new_district = ret[randomly_chosen_pair.x][randomly_chosen_pair.y];
            ret[p.x][p.y] = new_district;
            queue.addAll(Utilities.getAvailableNeighbors(ret, p.x, p.y));
        }
        return ret;
    }
    @Override
    public MapUpdateOutput alterMap(int[][] old_map, int num_districts, int distance) {
        int current_distance = 0;
        Set<Pair> updates = new HashSet<>();
        int[][] ret = Utilities.duplicate(old_map);
        while(current_distance < distance) {
            int change_x = (int) (Utilities.random() * ret.length);
            int change_y = (int) (Utilities.random() * ret[change_x].length);
            int old_district = ret[change_x][change_y];
            ret[change_x][change_y] = -2;
            if (!continuousDistrict(ret, old_district)) {
                ret[change_x][change_y] = old_district;
                continue;
            }
            List<Pair> neighbors = Utilities.getAllNeighbors(ret, change_x, change_y);
            neighbors.removeIf((Pair n) -> !(n.x >= 0 && n.x < ret.length && n.y >= 0 && n.y < ret[n.x].length && ret[n.x][n.y] != ret[change_x][change_y]));
            if (neighbors.isEmpty()) {
                ret[change_x][change_y] = old_district;
                continue;
            }
            Pair new_district_pair = Utilities.getRandomItem(neighbors);
            updates.add(new_district_pair);
            current_distance++;
            ret[change_x][change_y] = ret[new_district_pair.x][new_district_pair.y];
        }
        return new MapUpdateOutput(ret, updates);
    }
    public boolean isLegal(int[][] map, int num_districts) {
        if(!containsAllDistricts(map, num_districts)) return false;
        for(int i=0; i<num_districts; i++) {
            if(!continuousDistrict(map, i)) return false;
        }
        return true;
    }
    public boolean continuousDistrict(int[][] map, int district) {
        return Utilities.computeGroupsOfDistrict(map, district).size() == 1;
    }
    private boolean containsAllDistricts(int[][] map, int num_districts) {
        int districts_remaining = num_districts;
        int[] districts = new int[num_districts];
        Arrays.fill(districts, 0);
        for(int[] row: map) {
            for(int entry: row) {
                if(districts[entry]<1) districts_remaining--;
                districts[entry]++;
            }
        }
        return districts_remaining == 0;
    }
}

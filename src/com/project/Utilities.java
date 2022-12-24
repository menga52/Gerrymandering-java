package com.project;

import com.project.data.CONSTANTS;
import com.project.data.Pair;
import com.project.data.State;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * TODO: make this class not static
 */
public class Utilities {
    public static int[] @NotNull [] randomMap(int dim_x, int dim_y, int num_districts) {
        int[][] ret = new int[dim_x][dim_y];
        for(int[] row : ret) {
            Arrays.fill(row, -1);
        }
        LinkedList<Pair> queue = new LinkedList<>();
        int districts_created = 0;
        while(districts_created<num_districts) {
            int row = (int)(Math.random()*dim_x);
            int col = (int)(Math.random()*dim_y);
            if(available(ret, row, col)) {
                ret[row][col] = districts_created++;
                queue.addAll(getAvailableNeighbors(ret, row, col));
            }
        }

        while(!queue.isEmpty()) {
            Pair p = queue.poll();
            if (!available(ret, p)) continue;
            List<Pair> assigned_neighbors = getAssignedNeighbors(ret, p);
            Pair randomly_chosen_pair = getRandomItem(assigned_neighbors);
            int new_district = ret[randomly_chosen_pair.x][randomly_chosen_pair.y];
            ret[p.x][p.y] = new_district;
            queue.addAll(getAvailableNeighbors(ret, p.x, p.y));
        }
        return ret;
    }

    public static <T> T      getRandomItem(List<T> items) {
        if(items==null || items.isEmpty()) return null;
        int index = (int) (random()*items.size());
        return items.get(index);
    }
    public static List<Pair> getAllNeighbors(int[][] map, Pair p) {
        if(map==null || p==null) return null;
        return getAllNeighbors(map, p.x, p.y);
    }
    public static List<Pair> getAllNeighbors(int[][] map, int row, int col) {
        if(map==null) return null;
        ArrayList<Pair> pairs = new ArrayList<>();
        pairs.add(new Pair(row-1, col));
        pairs.add(new Pair(row+1, col));
        pairs.add(new Pair(row, col-1));
        pairs.add(new Pair(row, col+1));
        return pairs;
    }
    public static List<Pair> getAssignedNeighbors(int[][] map, Pair p) {
        if(map==null || p==null) return null;
        return getAssignedNeighbors(map, p.x, p.y);
    }
    public static List<Pair> getAssignedNeighbors(int[][] map, int row, int col) {
        if(map==null) return null;
        List<Pair> pairs = getAllNeighbors(map, row, col);
        pairs.removeIf(pair -> !assigned(map, pair));
        return pairs;
    }
    public static List<Pair> getAvailableNeighbors(int[][] map, Pair p) {
        if(map==null || p==null) return null;
        return getAvailableNeighbors(map, p.x, p.y);
    }
    public static List<Pair> getAvailableNeighbors(int[][] map, int row, int col) {
        List<Pair> pairs = getAllNeighbors(map, row, col);
        pairs.removeIf(pair -> !inBounds(map, pair) || assigned(map, pair));
        return pairs;
    }
    public static boolean    inBounds(int[][] map, Pair p) {
        if(map==null || p==null) return false;
        return inBounds(map, p.x, p.y);
    }
    public static boolean    inBounds(int[][] map, int i, int j) {
        if(map==null) return false;
        return i>=0 && i<map.length && j>=0 && j<map[i].length;
    }
    public static boolean    assigned(int[][] map, int i, int j) {
        return inBounds(map, i, j) && map[i][j] != -1;
    }
    public static boolean    assigned(int[][] map, Pair p) {
        if(map==null || p==null) return false;
        return assigned(map, p.x, p.y);
    }
    public static boolean    available(int[][] map, int i, int j) {
        return inBounds(map, i, j) && map[i][j] == -1;
    }
    public static boolean    available(int[][] map, Pair p) {
        if(map==null || p==null) return false;
        return available(map, p.x, p.y);
    }
    public static int[][]    duplicate(int[][] arg) {
        if(arg==null) return null;
        int[][] ret = new int[arg.length][arg[0].length];
        for(int i=0; i<ret.length; i++) {
            for(int j=0; j<ret[0].length; j++) {
                ret[i][j] = arg[i][j];
            }
        }
        return ret;
    }
    public static String     printMap(int[][]  map) {
        //prints a voting outcome matrix or a district map matrix
        StringBuilder ret = new StringBuilder();
        if(map==null) return null;
        for (int[] row : map) {
            for (int i : row) {
                ret.append(i);
                ret.append(" ");
            }
            ret.append('\n');
        }
        return ret.toString();
    }
    public static double dotProduct(double[] arr1, double[] arr2) {
        //computes the dot product of two vectors of the same length;
        double acc = 0.0;
        if(arr1.length!=arr2.length) {
            throw new ArithmeticException("Error: different length vectors in dot product");
        }
        for(int i=0; i<arr1.length; i++) acc += arr1[i]*arr2[i];
        return acc;
    }
    public static HashSet<HashSet<Pair>> computeGroupsOfDistrict(State state, int district) {
        return computeGroupsOfDistrict(state.districts, district);
    }
    public static HashSet<HashSet<Pair>> computeGroupsOfDistrict(int[][] districts, int district) {
        return computeGroups(districts, district, new NeighborInDistrict());
    }
    public static HashSet<HashSet<Pair>> computeGroupsDividedByDistrict(State state, int district) {
        return computeGroupsDividedByDistrict(state.districts, district);
    }
    public static HashSet<HashSet<Pair>> computeGroupsDividedByDistrict(int[][] districts, int district) {
        return computeGroups(districts, district, new NeighborNotInDistrict());
    }
    public static HashSet<HashSet<Pair>> computeGroups(int[][] districts, int district, Predicate predicate) {
        HashSet<Pair> unassigned = new HashSet<>();
        HashSet<HashSet<Pair>> ret = new HashSet<>();
        for(int i=0; i<districts.length; i++) {
            for(int j=0; j<districts[0].length; j++) {
                Pair p = new Pair(i,j);
                if(predicate.function(districts, p, district)) {
                    unassigned.add(p);
                }
            }
        }
        while(unassigned.size()>0) {
            Pair start = getStart(unassigned);
            unassigned.remove(start);
            HashSet<Pair> group = new HashSet<>();
            /*
                use a queue
                make sure queue has no redundancy
                find neighbors, add to queue, treat in district as inaccessible
                use unassigned hashset to track
             */
            LinkedList<Pair> queue = new LinkedList<>();
            queue.add(start);
            while(!queue.isEmpty()) {
                Pair current = queue.poll();
                group.add(current);
                for (Pair p: Utilities.getAllNeighbors(districts, current)) {
                    if(unassigned.contains(p) && predicate.function(districts, p, district)) {
                        unassigned.remove(p);
                        queue.add(p);
                    }
                }
            }
            ret.add(group);
        }
        return ret;
    }
    public static Pair getStart(HashSet<Pair> unassigned) {
        return unassigned.stream().iterator().next();
    }
    public static double random() {
        if(CONSTANTS.random!=null) return CONSTANTS.random.nextDouble();
        return Math.random();
    }
    interface Predicate {
        boolean function(int[][] districts, Pair arg, int district);
    }
    static class NeighborNotInDistrict implements Predicate {
        //for computeGroupsDividedByDistrict
        @Override
        public boolean function(int[][] districts, Pair arg, int district) {
            if(!inBounds(districts, arg)) return false;
            return districts[arg.x][arg.y] != district;
        }
    }
    static class NeighborInDistrict implements Predicate {
        //for computeGroupsOfDistrict
        @Override
        public boolean function(int[][] districts, Pair arg, int district) {
            return inBounds(districts, arg) && districts[arg.x][arg.y] == district;
        }


    }
}

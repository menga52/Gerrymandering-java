package com.project;


import com.project.data.MapUpdateOutput;

public interface MapGenerator {
    public int[][] generateMap(int dim_x, int dim_y, int num_districts);
    public MapUpdateOutput alterMap(int[][] old_map, int num_districts, int distance);
    public boolean isLegal(int[][] map, int num_districts);
}

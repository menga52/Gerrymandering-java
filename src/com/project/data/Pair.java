package com.project.data;

public class Pair {
    public int x, y;
    public Pair(int x, int y) {
        this.x = x; this.y = y;
    }
    public int hashCode() {
        return 100000*x+y;
    }
    public boolean equals(Object other) {
        if(!(other instanceof Pair)) return false;
        return this.x==((Pair)other).x && this.y==((Pair)other).y;
    }
}
package com.project;

import com.project.data.MapUpdateOutput;
import com.project.data.Pair;
import com.project.data.State;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Display extends JFrame {
    Random color_random = new Random(2);
    HashMap<Integer, Color> hmap = new HashMap<>();
    State state;
    public Display(State state) {
        this.state = state;
    }

    Set<Pair> updates = null;

    public void initPaint(Graphics g) {
        hmap.put(-1, Color.WHITE);
        for(int i = 0; i < state.districts.length; i++) {
            for(int j = 0; j < state.districts[0].length; j++) {
                paint(g, i, j);
            }
        }
    }
    public void paint(Graphics g, int i, int j) {
        int x_dim = this.getWidth() / state.districts.length;
        int y_dim = this.getHeight() / state.districts[0].length;
        int value = state.districts[i][j];
        Color color;
        if(!hmap.containsKey(value)) {
            color = new Color((int)(color_random.nextDouble()*256), (int)(color_random.nextDouble()*256), (int)(color_random.nextDouble()*256));
            hmap.put(value, color);
        }
        else {
            color = hmap.get(value);
        }
        g.setColor(color);
        g.fillRect(j*y_dim, i*x_dim, y_dim, x_dim);
        if(color.getGreen()+color.getBlue()+color.getRed()>256*3/2) g.setColor(Color.BLACK);
        else g.setColor(Color.WHITE);
        g.drawString(String.valueOf(state.voting_outcome[i][j]), j*y_dim+y_dim/2, i*x_dim+x_dim/2);
    }
    public void paint(Graphics g, Pair p) {
        paint(g, p.x, p.y);
    }
    public void paint(Graphics g) {
        super.paint(g);
        if(state.districts == null) return;
        if(hmap.isEmpty() || this.updates == null) {
            initPaint(g); return;
        }
        initPaint(g);
        this.updates = null;
    }
    public void paintUpdates(Graphics g) {
        for(Pair p: updates) {
            paint(g, p);
        }
    }
}

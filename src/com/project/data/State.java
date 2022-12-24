package com.project.data;

import com.project.Utilities;

import java.nio.file.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.util.List;

import static java.lang.Integer.parseInt;

public class State {
    public int[][] voting_outcome;
    public int[][] districts = null;

    public State(int[][] voting_outcome) {
        this.voting_outcome = voting_outcome;
    }

    public State setDistricts(int[][] districts) {
        this.districts = districts;
        return this;
    }

    public State clone() {
        State ret = new State(Utilities.duplicate(this.voting_outcome));
        ret.districts = Utilities.duplicate(this.districts);
        return ret;
    }

    //TODO: refactor to allow for weighted precincts (i.e. more people in this precinct)
    //TODO: refactor to allow for uncertainty
    public State(String path) {
        try {
            if(!path.endsWith(".txt")) path = path + ".txt";
            // make a connection to the file
            File file = new File(path);
            // read all lines of the file
            Scanner sc = new Scanner(file);
            ArrayList<String> voting_lines = new ArrayList<>();
            while(sc.hasNextLine()) {
                String next = sc.nextLine();
                if(next.equals("")) {
                    break;
                }
                voting_lines.add(next);
            }
            this.voting_outcome = new int[voting_lines.size()][voting_lines.get(0).split(" ").length];
            for(int i=0; i<voting_lines.size(); i++) {
                String[] line = voting_lines.get(i).split(" ");
                for(int j=0; j<voting_outcome[i].length; j++) {
                    voting_outcome[i][j] = parseInt(line[j]);
                }
            }
            if(sc.hasNextLine()) {

                ArrayList<String> district_lines = new ArrayList<>();
                while(sc.hasNextLine()) {
                    String next = sc.nextLine();
                    district_lines.add(next);
                }
                districts = new int[district_lines.size()][district_lines.get(0).split(" ").length];
                for(int i=0; i<district_lines.size(); i++) {
                    String[] line = district_lines.get(i).split(" ");
                    for(int j=0; j<districts[i].length; j++) {
                        districts[i][j] = parseInt(line[j]);
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    private int[] convertToIntArr(String[] strings) {
        int[] ret = new int[strings.length];
        for(int i=0; i<strings.length; i++) {
            ret[i] = parseInt(strings[i]);
        }
        return ret;
    }
}

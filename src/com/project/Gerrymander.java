package com.project;

import com.project.data.Parameters;

public interface Gerrymander {
    int[][] gerrymander(Parameters params, int preferred_candidate);
    void initialize();
}

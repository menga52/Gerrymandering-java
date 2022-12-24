package com.project.data;

public enum Characteristic {
    VOTING_OUTCOME(0),
    COMPACTNESS(1),
    PUNCTURES(2),
    CONNECTEDNESS(3);

    public final int value;

    Characteristic(int value) {
        this.value = value;
    }
}


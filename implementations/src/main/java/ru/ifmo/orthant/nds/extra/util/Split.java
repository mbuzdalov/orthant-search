package ru.ifmo.orthant.nds.extra.util;

/**
 * This class was imported from the following GitHub repository:
 * https://github.com/mbuzdalov/non-dominated-sorting
 * and adapted according to the needs of this repository.
 *
 * The particular revision location is:
 * https://github.com/mbuzdalov/non-dominated-sorting/tree/56fcfc61f5a4009e8ed02c0c3a4b00d390ba6aff
 */
public class Split {
    public int coordinate;
    public double value;
    public Split good, weak;

    public void initialize(int coordinate, double value, Split good, Split weak) {
        this.coordinate = coordinate;
        this.value = value;
        this.good = good;
        this.weak = weak;
    }

    public static final Split NULL_MAX_DEPTH = new Split();
    public static final Split NULL_POINTS = new Split();
}

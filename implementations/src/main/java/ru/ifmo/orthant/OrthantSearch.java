package ru.ifmo.orthant;

import java.util.Objects;

/**
 * This is an abstract class for all generic orthant search implementations.
 */
public abstract class OrthantSearch {
    /**
     * Returns the maximum number of points this implementation can handle.
     * @return the maximum number of points this implementation can handle.
     */
    public abstract int getMaximumPoints();

    /**
     * Returns the maximum dimension of points this implementation can handle.
     * @return the maximum dimension of points this implementation can handle.
     */
    public abstract int getMaximumDimension();

    /**
     * Returns the size of the additional collection needed to perform orthant search
     * with the given numbers of points.
     *
     * @param nPoints the number of points.
     * @return the size of the additional structure.
     */
    public abstract int getAdditionalCollectionSize(int nPoints);

    /**
     * Performs orthant search on the set of points given by the range of indices {@code [from; until)}.
     *
     * The {@code isObjectiveStrict} array determines the dominance relation:
     * if two points {@code X} and {@code Y} are not equal,
     * {@code X} dominates {@code Y} if for every objective {@code i}
     * it holds that {@code isObjectiveStrict ? X[i] < Y[i] || X[i] == Y[i]}.
     *
     * Every point can be a data point, a query point, or both.
     * A data point {@code P} carries its data as {@code collection[P]}.
     * A query point {@code Q} carries its auxiliary data as {@code collection[Q]}.
     *
     * All query points, which are also data points, must be served in any order which agrees with dominance relation:
     * whenever there are two such query points {@code Q1} and {@code Q2} such that {@code Q1} dominates {@code Q2},
     * the query point {@code Q1} must be served first.
     *
     * A query point {@code Q} is served the following way. First, the query answer {@code A} is evaluated
     * which is equal to composition ({@link ValueTypeClass#add(Object, int, Object, int)}) of
     * data values {@code collection[P]} for all data points {@code P} such that {@code P} dominates {@code Q}.
     * Second, the query result is put into {@code collection[Q]} using
     * {@link ValueTypeClass#storeQuery(Object, int, Object, int)}.
     *
     * @param points the points.
     * @param collection the collection for data values (in) and query answers (out).
     * @param from the index of the first (inclusively) point to be processed.
     * @param until the index of the last (exclusively) point to be processed.
     * @param isDataPoint {@code isDataPoint[i]} is {@code true} if {@code i} is a data point.
     * @param isQueryPoint {@code isQueryPoint[i]} is {@code true} if {@code i} is a query point.
     * @param additionalCollection the additional memory which can store data/query values.
     * @param typeClass the instance of {@link ValueTypeClass} which is capable of performing operations with data/query values.
     * @param isObjectiveStrict the array which determines whether a certain objective is strict when domination is computed.
     * @param <T> the type of a collection which stores data values for data points and query answers for query points.
     */
    public <T> void runSearch(
            double[][] points, T collection, int from, int until,
            boolean[] isDataPoint,
            boolean[] isQueryPoint,
            T additionalCollection,
            ValueTypeClass<T> typeClass,
            boolean[] isObjectiveStrict
    ) {
        Objects.requireNonNull(points, "points must not be null");
        Objects.requireNonNull(collection, "collection must not be null");
        Objects.requireNonNull(typeClass, "typeClass must not be null");
        Objects.requireNonNull(isObjectiveStrict, "isObjectiveStrict must not be null");
        Objects.requireNonNull(isDataPoint, "isDataPoint must not be null");
        Objects.requireNonNull(isQueryPoint, "isQueryPoint must not be null");

        if (0 > from || from > until) {
            throw new IllegalArgumentException("The interval [" + from + "; " + until
                    + ") is illegal");
        }
        if (until > typeClass.size(collection)) {
            throw new IllegalArgumentException("The interval [" + from + "; " + until
                    + ") is illegal for collection with size " + typeClass.size(collection));
        }
        if (until > points.length) {
            throw new IllegalArgumentException("The interval [" + from + "; " + until
                    + ") is illegal for points with array size of " + points.length);
        }
        int requiredAdditionalSize = getAdditionalCollectionSize(until - from);
        if (requiredAdditionalSize != 0) {
            Objects.requireNonNull(additionalCollection,
                    "When additional collection size must be non-zero, additionalCollection must not be null");
            if (requiredAdditionalSize < typeClass.size(additionalCollection)) {
                throw new IllegalArgumentException("additionalCollection of size "
                        + typeClass.size(additionalCollection) + " is too small for " + (until - from) + " points");
            }
        }
        if (from == until) {
            return;
        }
        for (int i = from; i < until; ++i) {
            Objects.requireNonNull(points[i],
                    "Elements of the points array in the requested range must not be null");
        }
        int dimension = points[from].length;
        for (int i = from; i < until; ++i) {
            double[] point = points[i];
            if (dimension != point.length) {
                throw new IllegalArgumentException("Points have different dimensions (point["
                        + from + "] has dimension " + dimension
                        + ", point[" + i + "] has dimension " + point.length);
            }
            for (int j = 0; j < dimension; ++j) {
                if (!Double.isFinite(point[j])) {
                    throw new IllegalArgumentException("points[" + i + "][" + j + "] is infinity or NaN");
                }
            }
        }
        if (isObjectiveStrict.length < dimension) {
            throw new IllegalArgumentException("The array isObjectiveStrict has size " + isObjectiveStrict.length
                    + ", which is too small for dimension " + dimension);
        }
        runSearchImpl(points, collection, from, until,
                isDataPoint, isQueryPoint, additionalCollection, typeClass, isObjectiveStrict);
    }

    protected abstract <T> void runSearchImpl(
            double[][] points, T collection, int from, int until,
            boolean[] isDataPoint,
            boolean[] isQueryPoint,
            T additionalCollection,
            ValueTypeClass<T> typeClass,
            boolean[] isObjectiveStrict
    );
}

package ru.ifmo.orthant;

/**
 * This class is a "type class" for a collection of values of a certain commutative monoid M,
 * which has:
 * <ul>
 *     <li>
 *         a neutral element {@code [M0]};
 *     </li>
 *     <li>
 *         a commutative composition operation {@code [M+]};
 *     </li>
 *     <li>
 *         an arbitrary "query-to-data" translation operation,
 *         called when the query result is computed for a query point,
 *         which is allowed to affect only the query point itself if it is a data point at the same time,
 *         or data points dominated by the query point.
 *     </li>
 * </ul>
 *
 * @param <T> the type of the collection.
 */
public abstract class ValueTypeClass<T> {
    /**
     * Creates a collection of a given size.
     * @param size the size of the collection.
     * @return the newly created collection.
     */
    public abstract T createCollection(int size);

    /**
     * Returns the size of the given collection.
     * @param collection the collection.
     * @return the size of the given collection.
     */
    public abstract int size(T collection);

    /**
     * Fills the range of the given collection with zeroes.
     * The "zero" is the neutral element {@code [M0]} of the commutative monoid M.
     *
     * @param collection the collection.
     * @param from the first index (included).
     * @param until the last index (excluded).
     */
    public abstract void fillWithZeroes(T collection, int from, int until);

    /**
     * Checks whether {@code target[targetIndex]} may change if {@code source[sourceIndex]} is added to it.
     *
     * This method must be fast. Implementations may call this method to determine whether it makes sense
     * to perform a domination check.
     *
     * The default implementation returns {@code true}, as it is the safest choice.
     * It is also perfectly safe to return whether {@code source[sourceIndex]} is not a neutral element.
     *
     * @param source the source collection.
     * @param sourceIndex the index of the element, which is about to be added, in the source collection.
     * @param target the target collection.
     * @param targetIndex the index of the element, to which it is need to add another one, in the target collection.
     * @return {@code true} if {@code target[targetIndex]} may change if {@code source[sourceIndex]} is added to it,
     * {@code false} otherwise.
     */
    public boolean targetChangesOnAdd(T source, int sourceIndex, T target, int targetIndex) {
        return true;
    }

    /**
     * For two collections, {@code source} and {@code target} (which might be the same collection),
     * adds {@code source[sourceIndex]} to {@code target[targetIndex]}.
     * The "add" operation is the commutative composition operation {@code [M+]} of the monoid M.
     *
     * @param source the source collection.
     * @param sourceIndex the index of the element, which is added, in the source collection.
     * @param target the target collection.
     * @param targetIndex the index of the element, to which it is need to add another one, in the target collection.
     */
    public abstract void add(T source, int sourceIndex, T target, int targetIndex);

    /**
     * For two collections, {@code source} and {@code target} (which might be the same collection),
     * once the query result for {@code source[sourceIndex]} is completely computed,
     * updates values associated with certain data points.
     *
     * Implementations may safely assume that {@code source} and {@code target} are the same collections
     * that those given to {@link OrthantSearch#runSearch(double[][], Object, Object, int, int, boolean[], boolean[], Object, ValueTypeClass, boolean[])}.
     *
     * Within this procedure, it is allowed to modify either {@code target[sourceIndex]},
     * or points which are dominated by the point related to {@code sourceIndex}.
     * Implementations of this procedure must ensure this invariant,
     * since implementations of {@link OrthantSearch} are not able to check what this procedure does.
     *
     * The expected complexity of this implementation is O(1). Please do not update too many points in this procedure.
     * The value at target collection may be non-zero and may mean something.
     *
     * @param source the source collection.
     * @param sourceIndex the index of the element in the source collection, which contains raw query results.
     * @param target the target collection.
     */
    public abstract void queryToData(T source, int sourceIndex, T target);
}

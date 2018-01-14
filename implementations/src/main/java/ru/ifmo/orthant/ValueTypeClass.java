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
 *         an arbitrary "store-query" translation operation.
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
     * stores the query result {@code source[sourceIndex]} to its final place {@code target[targetIndex]},
     * possibly altering it.
     *
     * The value at target collection may be non-zero and may mean something.
     *
     * @param source the source collection.
     * @param sourceIndex the index of the element in the source collection, which contains raw query results.
     * @param target the target collection.
     * @param targetIndex the index of the element in the target collection, which contains final query results..
     */
    public abstract void queryToData(T source, int sourceIndex, T target, int targetIndex);
}

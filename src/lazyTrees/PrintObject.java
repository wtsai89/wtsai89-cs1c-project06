package lazyTrees;

/**
 * Prints the object
 * @param <E>
 */
class PrintObject<E> implements Traverser<E>
{
    /**
     * Prints the current node
     * @param x
     */
    public void visit(E x)
    {
        System.out.print( x + " ");
    }
}

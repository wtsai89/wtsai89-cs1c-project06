package lazyTrees;

/**
 * Interface for PrintObject
 * @param <E>
 */
public interface Traverser<E>
{
    /**
     * Prints the current node
     * @param x
     */
    public void visit(E x);
}

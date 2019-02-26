package lazyTrees;

import java.util.*;

/**
 * BST which implements lazy deletion
 * @param <E>
 */
public class LazySearchTree<E extends Comparable< ? super E > > {
    protected int mSize, mSizeHard;     //mSize does not count soft deleted nodes
    protected LazySTNode mRoot;

    /**
     * default constructor
     */
    public LazySearchTree()
    {
        clear();
    }

    /**
     * Checks if the tree is empty
     * @return
     */
    public boolean empty()
    {
        return (mSize == 0);
    }

    /**
     * accessor method for mSize
     * @return
     */
    public int size()
    {
        return mSize;
    }

    /**
     * accessor method for mSizeHard
     * @return
     */
    public int sizeHard()
    {
        return mSizeHard;
    }

    /**
     * Clears the tree
     */
    public void clear()
    {
        mSize = 0;
        mSizeHard = 0;
        mRoot = null;
    }

    /**
     * public method for calculating the height of the tree
     * @return
     */
    public int showHeight()
    {
        return findHeight(mRoot, -1);
    }

    /**
     * public method for finding the minimum node of the tree
     * @return
     */
    public E findMin()
    {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMin(mRoot).data;
    }

    /**
     * public method for finding the hard minimum node of the tree
     * @return
     *
    public E findMinHard()
    {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMinHard(mRoot).data;
    }
    */

    /**
     * public method for finding the hard maximum node of the tree
     * @return
     *
    public E findMaxHard()
    {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMaxHard(mRoot).data;
    }
    */

    /**
     * public method for finding the maximum node of the tree
     * @return
     */
    public E findMax()
    {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMax(mRoot).data;
    }

    /**
     * public method for finding a node in the tree
     * @param x
     * @return
     */
    public E find( E x )
    {
        LazySTNode resultNode;
        resultNode = find(mRoot, x);
        if (resultNode == null)
            throw new NoSuchElementException();
        return resultNode.data;
    }

    /**
     * Checks if the tree contains a certain object
     * @param x
     * @return
     */
    public boolean contains(E x)
    { return find(mRoot, x) != null; }

    /**
     * public method for inserting a node
     * @param x
     * @return
     */
    public boolean insert( E x )
    {
        int oldSize = mSize;
        mRoot = insert(mRoot, x);
        return (mSize != oldSize);
    }

    /**
     * public method for removing a node using lazy deletion
     * @param x
     * @return
     */
    public boolean remove( E x )
    {
        int oldSize = mSize;
        remove(mRoot, x);
        return (mSize != oldSize);
    }

    /**
     * public method for traverseHard
     * @param func
     * @param <F>
     */
    public < F extends Traverser<? super E > >
    void traverseHard(F func)
    {
        traverseHard(func, mRoot);
    }

    /**
     * public method for traverseSoft
     * @param func
     * @param <F>
     */
    public < F extends Traverser<? super E > >
    void traverseSoft(F func)
    {
        traverseSoft(func, mRoot);
    }

    /**
     * public method for cloning the tree
     * @return
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException
    {
        LazySearchTree<E> newObject = (LazySearchTree<E>)super.clone();
        newObject.clear();  // can't point to other's data

        newObject.mRoot = cloneSubtree(mRoot);
        newObject.mSize = mSize;

        return newObject;
    }

    /**
     * public method for collectGarbage
     * @return
     */
    public boolean collectGarbage()
    {
        int oldSize = mSizeHard;
        collectGarbage(mRoot);
        return( mSizeHard != oldSize );
    }

    // private helper methods ----------------------------------------

    /**
     * Finds the minimum value
     * @param root
     * @return
     */
    protected LazySTNode findMin( LazySTNode root )
    {
        if (root == null)
            return null;
        if(root.lftChild == null)       //check for soft deletion at the bottom
        {
            if(root.deleted == false)
                return root;
            else if(root.rtChild == null)
                return null;
            else
                return findMin(root.rtChild);
        }

        LazySTNode tentativeMin = findMin(root.lftChild);
        if (tentativeMin == null)    // this is only true if root's entire left subtree has been soft deleted
        {
            if(root.deleted == false)
                return root;
            else if(root.rtChild == null)
                return null;
            else
                return findMin(root.rtChild);
        }
        return tentativeMin;
    }

    /**
     * Finds the hard minimum value
     * @param root
     * @return
     */
    protected LazySTNode findMinHard( LazySTNode root ) {
        if (root == null)
            return null;
        if (root.lftChild == null)
            return root;
        return findMinHard(root.lftChild);
    }

    /**
     * Finds the maximum value
     * @param root
     * @return
     */
    protected LazySTNode findMax( LazySTNode root )
    {
        if (root == null)
            return null;
        if(root.rtChild == null)    //check for soft deletion at the bottom
        {
            if(root.deleted == false)
                return root;
            else if(root.lftChild == null)
                return null;
            else
                return findMax(root.lftChild);
        }

        LazySTNode tentativeMax = findMax(root.rtChild);
        if (tentativeMax == null)    // this is only true if root's entire right subtree has been soft deleted
        {
            if(root.deleted == false)
                return root;
            else if(root.lftChild == null)
                return null;
            else
                return findMax(root.lftChild);
        }
        return tentativeMax;
    }

    /**
     * Finds the hard maximum value
     * @param root
     * @return
     */
    protected LazySTNode findMaxHard( LazySTNode root ) {
        if (root == null)
            return null;
        if (root.rtChild == null)
            return root;
        return findMaxHard(root.rtChild);
    }

    /**
     * Inserts an object into the tree
     * @param root
     * @param x
     * @return
     */
    protected LazySTNode insert( LazySTNode root, E x )
    {
        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
        {
            mSize++;
            mSizeHard++;
            return new LazySTNode(x, null, null);
        }

        compareResult = x.compareTo(root.data);
        if ( compareResult < 0 )
            root.lftChild = insert(root.lftChild, x);
        else if ( compareResult > 0 )
            root.rtChild = insert(root.rtChild, x);
        else if(root.deleted == true)
        {
            root.deleted = false;
            mSize++;
        }
        return root;
    }

    /**
     * Removes an object from the tree
     * @param root
     * @param x
     */
    protected void remove( LazySTNode root, E x  )
    {
        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
            return;

        compareResult = x.compareTo(root.data);
        if ( compareResult < 0 )
            remove(root.lftChild, x);
        else if ( compareResult > 0 )
            remove(root.rtChild, x);

            // found the node
        else
        {
            if(root.deleted == false) {
                root.deleted = true;
                mSize--;
            }
        }
    }

    /**
     * Permanently removes a node from the tree
     * @param root
     * @param x
     * @return
     */
    protected LazySTNode removeHard( LazySTNode root, E x )
    {
        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);
        if ( compareResult < 0 )
            root.lftChild = removeHard(root.lftChild, x);
        else if ( compareResult > 0 )
            root.rtChild = removeHard(root.rtChild, x);

            // found the node
        else if(root.deleted == false)
            return root;
        else if (root.lftChild != null && root.rtChild != null)
        {
            LazySTNode replacement = findMinHard(root.rtChild);
            root.data = replacement.data;
            if(replacement.deleted == false)
                root.deleted = false;
            root.rtChild = removeHard(root.rtChild, root.data);
        }
        else
        {
            if(root.data.compareTo(mRoot.data) == 0)
                mRoot = (mRoot.lftChild != null)? mRoot.lftChild : mRoot.rtChild;
            else
                root = (root.lftChild != null)? root.lftChild : root.rtChild;
            mSizeHard--;
        }
        return root;
    }

    /**
     * Hard remove nodes marked as "deleted" from the tree.
     * @param root
     */
    protected void collectGarbage( LazySTNode root)
    {
        if(root == null)
            return;

        collectGarbage(root.lftChild);
        collectGarbage(root.rtChild);
        if(root.deleted == true)
            removeHard( mRoot, root.data );
    }

    /**
     * Traverses the entire tree including soft deleted nodes
     * @param func
     * @param treeNode
     * @param <F>
     */
    protected <F extends Traverser<? super E>>
    void traverseHard(F func, LazySTNode treeNode)
    {
        if (treeNode == null)
            return;

        traverseHard(func, treeNode.lftChild);
        func.visit(treeNode.data);
        traverseHard(func, treeNode.rtChild);
    }

    /**
     * Traverses the entire tree excluding soft deleted nodes
     * @param func
     * @param treeNode
     * @param <F>
     */
    protected <F extends Traverser<? super E>>
    void traverseSoft(F func, LazySTNode treeNode)
    {
        if (treeNode == null)
            return;

        traverseSoft(func, treeNode.lftChild);
        if(treeNode.deleted == false)
            func.visit(treeNode.data);
        traverseSoft(func, treeNode.rtChild);
    }

    /**
     * Finds an object in the tree
     * @param root
     * @param x
     * @return
     */
    protected LazySTNode find( LazySTNode root, E x )
    {
        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);
        if (compareResult < 0)
            return find(root.lftChild, x);
        if (compareResult > 0)
            return find(root.rtChild, x);
        if (root.deleted == false)   // found
            return root;
        return null;
    }

    /**
     * Clones the tree
     * @param root
     * @return
     */
    protected LazySTNode cloneSubtree(LazySTNode root)
    {
        LazySTNode newNode;
        if (root == null)
            return null;

        // does not set myRoot which must be done by caller
        newNode = new LazySTNode
                (
                        root.data,
                        cloneSubtree(root.lftChild),
                        cloneSubtree(root.rtChild)
                );
        return newNode;
    }

    /**
     * Returns the height of the tree
     * @param treeNode
     * @param height
     * @return
     */
    protected int findHeight( LazySTNode treeNode, int height )
    {
        int leftHeight, rightHeight;
        if (treeNode == null)
            return height;
        height++;
        leftHeight = findHeight(treeNode.lftChild, height);
        rightHeight = findHeight(treeNode.rtChild, height);
        return (leftHeight > rightHeight)? leftHeight : rightHeight;
    }

    ////////////////////////////
    // LazyTree Node inner class
    ///////////////////////////

    /**
     * Node object for LazySearchTree
     */
    private class LazySTNode
    {
        private LazySTNode lftChild, rtChild;
        private E data;
        private LazySTNode myRoot;  // needed to test for certain error
        private boolean deleted;

        /**
         * Constructor
         * @param d
         * @param lft
         * @param rt
         */
        private LazySTNode( E d, LazySTNode lft, LazySTNode rt )
        {
            lftChild = lft;
            rtChild = rt;
            data = d;
            deleted = false;
        }

        /**
         * empty constructor
         */
        private LazySTNode()
        {
            this(null, null, null);
        }
    }

}

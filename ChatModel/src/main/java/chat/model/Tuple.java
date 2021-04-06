package chat.model;

public class Tuple<E1,E2> {

    private E1 left;
    private E2 right;

    /**
     * constructor
     * @param left the left entity of a tuple
     * @param right the right entity of a tuple
     */
    public Tuple(E1 left, E2 right) {
        this.left = left;
        this.right = right;
    }

    /**
     * return the left entity of a tuple
     * @return left E1
     */
    public E1 getLeft() {
        return left;
    }

    /**
     * set the left entity of a tuple
     * @param left the left entity of a tuple
     */
    public void setLeft(E1 left) {
        this.left = left;
    }

    /**
     * return the right entity of a tuple
     * @return right E2
     */
    public E2 getRight() {
        return right;
    }

    /**
     * set the right entity of a tuple
     * @param right the right entity of a tuple
     */
    public void setRight(E2 right) {
        this.right = right;
    }

}

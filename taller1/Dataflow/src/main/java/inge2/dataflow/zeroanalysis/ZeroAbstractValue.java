package inge2.dataflow.zeroanalysis;

/**
 * This enum represents the possible values of the zero analysis for a variable.
 */
public enum ZeroAbstractValue {

    /**
     * We don't have information about the variable.
     */
    BOTTOM("bottom"),

    /**
     * The variable is not zero.
     */
    NOT_ZERO("not-zero"),

    /**
     * The variable is zero.
     */
    ZERO("zero"),

    /**
     * The variable may be (or not) zero.
     */
    MAYBE_ZERO("maybe-zero");

    /**
     * The name of the ZeroAbstractValue.
     */
    private final String name;

    @Override
    public String toString() {
        return this.name;
    }

    ZeroAbstractValue(String name) {
        this.name = name;
    }

    /**
     * Returns the result of the addition between this ZeroAbstractValue and another.
     * @param another the other ZeroAbstractValue.
     * @return the result of the addition.
     */
    public ZeroAbstractValue add(ZeroAbstractValue another) {
        if(this.name.equals(ZeroAbstractValue.BOTTOM.toString()) || another.name.equals(ZeroAbstractValue.BOTTOM.toString())){
            return ZeroAbstractValue.BOTTOM;
        }
        if(this.name.equals(ZeroAbstractValue.ZERO.toString()) && another.name.equals(ZeroAbstractValue.ZERO.toString())){
            return ZeroAbstractValue.ZERO;
        }
        if(this.name.equals(ZeroAbstractValue.NOT_ZERO.toString()) && another.name.equals(ZeroAbstractValue.ZERO.toString())){
            return ZeroAbstractValue.NOT_ZERO;
        }
        if(this.name.equals(ZeroAbstractValue.ZERO.toString()) && another.name.equals(ZeroAbstractValue.NOT_ZERO.toString())){
            return ZeroAbstractValue.NOT_ZERO;
        }
        return ZeroAbstractValue.MAYBE_ZERO;
    }

    /**
     * Returns the result of the division between this ZeroAbstractValue and another.
     * @param another the other ZeroAbstractValue.
     * @return the result of the division.
     */
    public ZeroAbstractValue divideBy(ZeroAbstractValue another) {
        if(this.name.equals(ZeroAbstractValue.BOTTOM.toString()) || another.name.equals(ZeroAbstractValue.BOTTOM.toString())){
            return ZeroAbstractValue.BOTTOM;
        }
        else if(another.name.equals(ZeroAbstractValue.ZERO.toString())){
            return ZeroAbstractValue.BOTTOM;
        }
        else if(this.name.equals(ZeroAbstractValue.ZERO.toString())){
            return ZeroAbstractValue.ZERO;
        }
        return ZeroAbstractValue.MAYBE_ZERO;
    }

    /**
     * Returns the result of the multiplication between this ZeroAbstractValue and another.
     * @param another the other ZeroAbstractValue.
     * @return the result of the multiplication.
     */
    public ZeroAbstractValue multiplyBy(ZeroAbstractValue another) {
        if(this.name.equals(ZeroAbstractValue.BOTTOM.toString()) || another.name.equals(ZeroAbstractValue.BOTTOM.toString())){
            return ZeroAbstractValue.BOTTOM;
        }
        if(this.name.equals(ZeroAbstractValue.ZERO.toString()) || another.name.equals(ZeroAbstractValue.ZERO.toString())){
            return ZeroAbstractValue.ZERO;
        }
        if(this.name.equals(ZeroAbstractValue.NOT_ZERO.toString()) && another.name.equals(ZeroAbstractValue.NOT_ZERO.toString())){
            return ZeroAbstractValue.NOT_ZERO;
        }
        return ZeroAbstractValue.MAYBE_ZERO;
    }

    /**
     * Returns the result of the subtraction between this ZeroAbstractValue and another.
     * @param another the other ZeroAbstractValue.
     * @return the result of the subtraction.
     */
    public ZeroAbstractValue subtract(ZeroAbstractValue another) {
        if(this.name.equals(ZeroAbstractValue.BOTTOM.toString()) || another.name.equals(ZeroAbstractValue.BOTTOM.toString())){
            return ZeroAbstractValue.BOTTOM;
        }
        if(this.name.equals(ZeroAbstractValue.ZERO.toString()) && another.name.equals(ZeroAbstractValue.ZERO.toString())){
            return ZeroAbstractValue.ZERO;
        }
        if(this.name.equals(ZeroAbstractValue.NOT_ZERO.toString()) && another.name.equals(ZeroAbstractValue.ZERO.toString())){
            return ZeroAbstractValue.NOT_ZERO;
        }
        if(this.name.equals(ZeroAbstractValue.ZERO.toString()) && another.name.equals(ZeroAbstractValue.NOT_ZERO.toString())){
            return ZeroAbstractValue.NOT_ZERO;
        }
        return ZeroAbstractValue.MAYBE_ZERO;
    }

    /**
     * Returns the result of the merge between this ZeroAbstractValue and another.
     * @param another the other ZeroAbstractValue.
     * @return the result of the merge.
     */
    public ZeroAbstractValue merge(ZeroAbstractValue another) {
        if(this.name.equals(ZeroAbstractValue.MAYBE_ZERO.toString()) || another.name.equals(ZeroAbstractValue.MAYBE_ZERO.toString())){
            return ZeroAbstractValue.MAYBE_ZERO;
        }
        if(this.name.equals(ZeroAbstractValue.BOTTOM.toString()) && !another.name.equals(ZeroAbstractValue.BOTTOM.toString())){
            return another;
        }
        return this;
    }

}

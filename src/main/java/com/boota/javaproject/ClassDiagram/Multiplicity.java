package com.boota.javaproject.ClassDiagram;

import java.io.Serializable;
/**
 * Represents a range with a starting and ending value. This class is used
 * to define and work with a range of numbers, typically represented as
 * "start..end".
 *
 * Key Features:
 * - The range is defined using two Double values: `start` and `end`.
 * - Provides getter and setter methods to access and modify the start and
 *   end values of the range.
 * - Overrides the `toString` method to display the range in the format
 *   "start..end".
 *
 * Thread Safety:
 * - This class is not thread-safe. External synchronization is required
 *   if instances are shared between threads.
 *
 * Serialization:
 * - Implements the Serializable interface, which allows instances of this
 *   class to be serialized.
 */
public class Multiplicity implements Serializable {
    Double start;
    Double end;

    /**
     * Constructs a new instance of the Multiplicity class with the specified
     * starting and ending values of the range.
     *
     * @param start the starting value of the range
     * @param end the ending value of the range
     */
    public Multiplicity(Double start, Double end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Gets the starting value of the range.
     *
     * @return the starting value as a Double, representing the beginning of the range.
     */
    public Double getStart() {
        return start;
    }

    /**
     * Sets the starting value of the range.
     *
     * @param start the new starting value of the range, represented as a Double
     */
    public void setStart(Double start) {
        this.start = start;
    }

    /**
     * Gets the ending value of the range.
     *
     * @return the ending value as a Double, representing the end of the range.
     */
    public Double getEnd() {
        return end;
    }

    /**
     * Sets the ending value of the range.
     *
     * @param end the new ending value of the range, represented as a Double
     */
    public void setEnd(Double end) {
        this.end = end;
    }

    /**
     * Returns a string representation of the range in the format "start..end".
     * The `start` and `end` values represent the beginning and end of the range, respectively.
     *
     * @return a string representing the range in the format "start..end"
     */
    @Override
    public String toString() {
        return start + ".." + end;
    }
}

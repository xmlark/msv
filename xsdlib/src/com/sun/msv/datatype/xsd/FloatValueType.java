/*
 * @(#)FloatValueType.java	1.60 00/02/02
 *
 * Copyright 1994-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package com.sun.tranquilo.datatype;

/**
 * The FloatValueType class wraps a value of primitive type <code>float</code> in
 * an object. An object of type <code>FloatValueType</code> contains a single
 * field whose type is <code>float</code>.
 * <p>
 * In addition, this class provides several methods for converting a
 * <code>float</code> to a <code>String</code> and a
 * <code>String</code> to a <code>float</code>, as well as other
 * constants and methods useful when dealing with a
 * <code>float</code>.
 *
 * @author  Lee Boynton
 * @author  Arthur van Hoff
 * @version 1.60, 02/02/00
 * @since   JDK1.0
 */
public final class FloatValueType extends Number implements Comparable {
    /**
     * The positive infinity of type <code>float</code>. It is equal 
     * to the value returned by
     * <code>FloatValueType.intBitsToFloat(0x7f800000)</code>.
     */
    public static final float POSITIVE_INFINITY = 1.0f / 0.0f;

    /**
     * The negative infinity of type <code>float</code>. It is equal 
     * to the value returned by
     * <code>FloatValueType.intBitsToFloat(0xff800000)</code>.
     */
    public static final float NEGATIVE_INFINITY = -1.0f / 0.0f;

    /** 
     * The Not-a-Number (NaN) value of type <code>float</code>. 
     * It is equal to the value returned by
     * <code>FloatValueType.intBitsToFloat(0x7fc00000)</code>. 
     */
    public static final float NaN = 0.0f / 0.0f;

    /**
     * The largest positive value of type <code>float</code>. It is 
     * equal to the value returned by 
     * <code>FloatValueType.intBitsToFloat(0x7f7fffff)</code>.
     */
    public static final float MAX_VALUE = 3.40282346638528860e+38f;

    /**
     * The smallest positive value of type <code>float</code>. It 
     * is equal to the value returned by 
     * <code>FloatValueType.intBitsToFloat(0x1)</code>.
     */
    public static final float MIN_VALUE = 1.40129846432481707e-45f;

    /**
     * Returns the floating point value represented by the specified String. 
     * The string <code>s</code> is interpreted as the representation of a 
     * floating-point value and a <code>FloatValueType</code> object representing that 
     * value is created and returned. 
     * <p>
     * If <code>s</code> is <code>null</code>, then a 
     * <code>NullPointerException</code> is thrown. 
     * <p>
     * Leading and trailing whitespace characters in s are ignored. The rest 
     * of <code>s</code> should constitute a <i>FloatValue</i> as described 
     * by the lexical syntax rules:
     * <blockquote><pre><i>
     * FloatValue:
     * 
     *          Sign<sub>opt</sub> FloatingPointLiteral
     * </i></pre></blockquote>     	 
     * where <i>Sign</i>, <i>FloatingPointLiteral</i> are as defined in 
     * §3.10.2 of the 
     * <a href="http://java.sun.com/docs/books/jls/html/">Java Language 
     * Specification</a>. If it does not have the form of a <i>FloatValue</i>, 
     * then a <code>NumberFormatException</code> is thrown. Otherwise, it is 
     * regarded as representing an exact decimal value in the usual 
     * "computerized scientific notation"; this exact decimal value is then
     * conceptually converted to an "infinitely precise" binary value that 
     * is then rounded to type float by the usual round-to-nearest rule of 
     * IEEE 754 floating-point arithmetic.
     *
     * @param      s   the string to be parsed.
     * @return     a newly constructed <code>FloatValueType</code> initialized to the
     *             value represented by the <code>String</code> argument.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable number.
     */
    public static FloatValueType valueOf(String s) throws NumberFormatException {
	return new FloatValueType(FloatingDecimal.readJavaFormatString(s).floatValue());
    }

    /**
     * Returns a new float initialized to the value represented by the 
     * specified <code>String</code>, as performed by the <code>valueOf</code>
     * method of class <code>Double</code>.
     *
     * @param      s   the string to be parsed.
     * @return     the float value represented by the string argument.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable float.
     * @see        java.lang.Double#valueOf(String)
     * @since      1.2
     */
    public static float parseFloat(String s) throws NumberFormatException {
	return FloatingDecimal.readJavaFormatString(s).floatValue();
    }

    /**
     * Returns true if the specified number is the special Not-a-Number (NaN)
     * value.
     *
     * @param   v   the value to be tested.
     * @return  <code>true</code> if the argument is NaN;
     *          <code>false</code> otherwise.
     */
    static public boolean isNaN(float v) {
	return (v != v);
    }

    /**
     * Returns true if the specified number is infinitely large in magnitude.
     *
     * @param   v   the value to be tested.
     * @return  <code>true</code> if the argument is positive infinity or
     *          negative infinity; <code>false</code> otherwise.
     */
    static public boolean isInfinite(float v) {
	return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

    /**
     * The value of the FloatValueType.
     *
     * @serial
     */
    private float value;

    /**
     * Constructs a newly allocated <code>FloatValueType</code> object that
     * represents the primitive <code>float</code> argument.
     *
     * @param   value   the value to be represented by the <code>FloatValueType</code>.
     */
    public FloatValueType(float value) {
	this.value = value;
    }

    /**
     * Constructs a newly allocated <code>FloatValueType</code>object that
     * represents the argument converted to type <code>float</code>.
     *
     * @param   value   the value to be represented by the <code>FloatValueType</code>.
     */
    public FloatValueType(double value) {
	this.value = (float)value;
    }

    /**
     * Constructs a newly allocated <code>FloatValueType</code> object that 
     * represents the floating-point value of type <code>float</code> 
     * represented by the string. The string is converted to a 
     * <code>float</code> value as if by the <code>valueOf</code> method. 
     *
     * @param      s   a string to be converted to a <code>FloatValueType</code>.
     * @exception  NumberFormatException  if the string does not contain a
     *               parsable number.
     * @see        java.lang.FloatValueType#valueOf(java.lang.String)
     */
    public FloatValueType(String s) throws NumberFormatException {
	// REMIND: this is inefficient
	this(valueOf(s).floatValue());
    }

    /**
     * Returns true if this <code>FloatValueType</code> value is Not-a-Number (NaN).
     *
     * @return  <code>true</code> if the value represented by this object is
     *          NaN; <code>false</code> otherwise.
     */
    public boolean isNaN() {
	return isNaN(value);
    }

    /**
     * Returns true if this FloatValueType value is infinitely large in magnitude.
     *
     * @return  <code>true</code> if the value represented by this object is
     *          positive infinity or negative infinity;
     *          <code>false</code> otherwise.
     */
    public boolean isInfinite() {
	return isInfinite(value);
    }

    /**
     * Returns a String representation of this FloatValueType object.
     * The primitive <code>float</code> value represented by this object
     * is converted to a <code>String</code> exactly as if by the method
     * <code>toString</code> of one argument.
     *
     * @return  a <code>String</code> representation of this object.
     * @see     java.lang.FloatValueType#toString(float)
     */
    public String toString() {
	return String.valueOf(value);
    }

    /**
     * Returns the value of this FloatValueType as a byte (by casting to a byte).
     *
     * @since   JDK1.1
     */
    public byte byteValue() {
	return (byte)value;
    }

    /**
     * Returns the value of this FloatValueType as a short (by casting to a short).
     *
     * @since   JDK1.1
     */
    public short shortValue() {
	return (short)value;
    }

    /**
     * Returns the integer value of this FloatValueType (by casting to an int).
     *
     * @return  the <code>float</code> value represented by this object
     *          converted to type <code>int</code> and the result of the
     *          conversion is returned.
     */
    public int intValue() {
	return (int)value;
    }

    /**
     * Returns the long value of this FloatValueType (by casting to a long).
     *
     * @return  the <code>float</code> value represented by this object is
     *          converted to type <code>long</code> and the result of the
     *          conversion is returned.
     */
    public long longValue() {
	return (long)value;
    }

    /**
     * Returns the float value of this <tt>FloatValueType</tt> object.
     *
     * @return  the <code>float</code> value represented by this object.
     */
    public float floatValue() {
	return value;
    }

    /**
     * Returns the double value of this <tt>FloatValueType</tt> object.
     * 
     * @return the <code>float</code> value represented by this 
     *         object is converted to type <code>double</code> and the 
     *         result of the conversion is returned.
     */
    public double doubleValue() {
	return (double)value;
    }

    /**
     * Returns a hashcode for this <tt>FloatValueType</tt> object. The result 
     * is the integer bit representation, exactly as produced
     * by the method {@link #floatToIntBits(float)}, of the primitive float
     * value represented by this <tt>FloatValueType</tt> object.
     *
     * @return  a hash code value for this object.
     */
    public int hashCode() {
	return floatToIntBits(value);
    }

    /**
     * Compares this object against some other object.
     * The result is <code>true</code> if and only if the argument is 
     * not <code>null</code> and is a <code>FloatValueType</code> object that 
     * represents a <code>float</code> that has the identical bit pattern 
     * to the bit pattern of the <code>float</code> represented by this 
     * object. For this purpose, two float values are considered to be
     * the same if and only if the method {@link #floatToIntBits(float)} 
     * returns the same int value when applied to each.
     * <p>
     * Note that in most cases, for two instances of class
     * <code>FloatValueType</code>, <code>f1</code> and <code>f2</code>, the value
     * of <code>f1.equals(f2)</code> is <code>true</code> if and only if
     * <blockquote><pre>
     *   f1.floatValue() == f2.floatValue()
     * </pre></blockquote>
     * <p>
     * also has the value <code>true</code>. However, there are two exceptions:
     * <ul>
     * <li>If <code>f1</code> and <code>f2</code> both represent
     *     <code>FloatValueType.NaN</code>, then the <code>equals</code> method returns
     *     <code>true</code>, even though <code>FloatValueType.NaN==FloatValueType.NaN</code>
     *     has the value <code>false</code>.
     * <li>If <code>f1</code> represents <code>+0.0f</code> while
     *     <code>f2</code> represents <code>-0.0f</code>, or vice versa,
     *     the <code>equal</code> test has the value <code>false</code>,
     *     even though <code>0.0f==-0.0f</code> has the value <code>true</code>.
     * </ul>
     * This definition allows hashtables to operate properly.
     *
     * @param obj the object to be compared
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @see     java.lang.FloatValueType#floatToIntBits(float)
     */
    public boolean equals(Object obj) {
	return (obj instanceof FloatValueType)
	       && (floatToIntBits(((FloatValueType)obj).value) == floatToIntBits(value));
    }

    /**
     * Returns the bit represention of a single-float value.
     * The result is a representation of the floating-point argument 
     * according to the IEEE 754 floating-point "single 
     * precision" bit layout. 
     * <ul>
     * <li>Bit 31 (the bit that is selected by the mask 
     * <code>0x80000000</code>) represents the sign of the floating-point 
     * number. 
     * <li>Bits 30-23 (the bits that are selected by the mask 
     * <code>0x7f800000</code>) represent the exponent. 
     * <li>Bits 22-0 (the bits that are selected by the mask 
     * <code>0x007fffff</code>) represent the significand (sometimes called 
     * the mantissa) of the floating-point number. 
     * <li>If the argument is positive infinity, the result is 
     * <code>0x7f800000</code>. 
     * <li>If the argument is negative infinity, the result is 
     * <code>0xff800000</code>. 
     * <li>If the argument is NaN, the result is <code>0x7fc00000</code>. 
     * </ul>
     * In all cases, the result is an integer that, when given to the 
     * {@link #intBitsToFloat(int)} method, will produce a floating-point 
     * value equal to the argument to <code>floatToIntBits</code>.
     * 
     * @param   value   a floating-point number.
     * @return  the bits that represent the floating-point number.
     */
    public static native int floatToIntBits(float value);

    /**
     * Returns the bit represention of a single-float value.
     * The result is a representation of the floating-point argument 
     * according to the IEEE 754 floating-point "single 
     * precision" bit layout. 
     * <ul>
     * <li>Bit 31 (the bit that is selected by the mask 
     * <code>0x80000000</code>) represents the sign of the floating-point 
     * number. 
     * <li>Bits 30-23 (the bits that are selected by the mask 
     * <code>0x7f800000</code>) represent the exponent. 
     * <li>Bits 22-0 (the bits that are selected by the mask 
     * <code>0x007fffff</code>) represent the significand (sometimes called 
     * the mantissa) of the floating-point number. 
     * <li>If the argument is positive infinity, the result is 
     * <code>0x7f800000</code>. 
     * <li>If the argument is negative infinity, the result is 
     * <code>0xff800000</code>.
     * <p>
     * If the argument is NaN, the result is the integer
     * representing the actual NaN value.  Unlike the <code>floatToIntBits</code>
     * method, <code>intToRawIntBits</code> does not collapse NaN values.
     * </ul>
     * In all cases, the result is an integer that, when given to the 
     * {@link #intBitsToFloat(int)} method, will produce a floating-point 
     * value equal to the argument to <code>floatToRawIntBits</code>.
     * 
     * @param   value   a floating-point number.
     * @return  the bits that represent the floating-point number.
     */
    public static native int floatToRawIntBits(float value);

    /**
     * Returns the single-float corresponding to a given bit represention.
     * The argument is considered to be a representation of a
     * floating-point value according to the IEEE 754 floating-point
     * "single precision" bit layout.
     * <p>
     * If the argument is <code>0x7f800000</code>, the result is positive
     * infinity.
     * <p>
     * If the argument is <code>0xff800000</code>, the result is negative
     * infinity.
     * <p>
     * If the argument is any value in the range <code>0x7f800001</code> 
     * through <code>0x7fffffff</code> or in the range 
     * <code>0xff800001</code> through <code>0xffffffff</code>, the result is 
     * NaN. All IEEE 754 NaN values of type <code>float</code> are, in effect,
     * lumped together by the Java programming language into a single 
     * <code>float</code> value called NaN.  Distinct values of NaN are only
     * accessible by use of the <code>FloatValueType.floatToRawIntBits</code> method.
     * <p>
     * In all other cases, let <i>s</i>, <i>e</i>, and <i>m</i> be three 
     * values that can be computed from the argument: 
     * <blockquote><pre>
     * int s = ((bits >> 31) == 0) ? 1 : -1;
     * int e = ((bits >> 23) & 0xff);
     * int m = (e == 0) ?
     *                 (bits & 0x7fffff) << 1 :
     *                 (bits & 0x7fffff) | 0x800000;
     * </pre></blockquote>
     * Then the floating-point result equals the value of the mathematical 
     * expression <i>s&#183;m&#183;2<sup>e-150</sup></i>.
     *
     * @param   bits   an integer.
     * @return  the single-format floating-point value with the same bit
     *          pattern.
     */
    public static native float intBitsToFloat(int bits);

    /**
     * Compares two Floats numerically.  There are two ways in which
     * comparisons performed by this method differ from those performed
     * by the Java language numerical comparison operators (<code>&lt;, &lt;=,
     * ==, &gt;= &gt;</code>) when applied to primitive floats:
     * <ul><li>
     *		<code>FloatValueType.NaN</code> is considered by this method to be
     *		equal to itself and greater than all other float values
     *		(including <code>FloatValueType.POSITIVE_INFINITY</code>).
     * <li>
     *		<code>0.0f</code> is considered by this method to be greater
     *		than <code>-0.0f</code>.
     * </ul>
     * This ensures that FloatValueType.compareTo(Object) (which inherits its behavior
     * from this method) obeys the general contract for Comparable.compareTo,
     * and that the <i>natural order</i> on Floats is <i>total</i>.
     *
     * @param   anotherFloat   the <code>FloatValueType</code> to be compared.
     * @return  the value <code>0</code> if <code>anotherFloat</code> is
     *		numerically equal to this FloatValueType; a value less than
     *          <code>0</code> if this FloatValueType is numerically less than
     *		<code>anotherFloat</code>; and a value greater than
     *		<code>0</code> if this FloatValueType is numerically greater than
     *		<code>anotherFloat</code>.
     *		
     * @since   1.2
     * @see     Comparable#compareTo(Object)
     */
    public int compareTo(FloatValueType anotherFloat) {
        float thisVal = value;
        float anotherVal = anotherFloat.value;

        if (thisVal < anotherVal)
            return -1;		 // Neither val is NaN, thisVal is smaller
        if (thisVal > anotherVal)
            return 1;		 // Neither val is NaN, thisVal is larger

        int thisBits = FloatValueType.floatToIntBits(thisVal);
        int anotherBits = FloatValueType.floatToIntBits(anotherVal);

        return (thisBits == anotherBits ?  0 : // Values are equal
                (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
                 1));                          // (0.0, -0.0) or (NaN, !NaN)
    }

    /**
     * Compares this FloatValueType to another Object.  If the Object is a FloatValueType,
     * this function behaves like <code>compareTo(FloatValueType)</code>.  Otherwise,
     * it throws a <code>ClassCastException</code> (as Floats are comparable
     * only to other Floats).
     *
     * @param   o the <code>Object</code> to be compared.
     * @return  the value <code>0</code> if the argument is a FloatValueType
     *		numerically equal to this FloatValueType; a value less than
     *		<code>0</code> if the argument is a FloatValueType numerically
     *		greater than this FloatValueType; and a value greater than
     *		<code>0</code> if the argument is a FloatValueType numerically
     *		less than this FloatValueType.
     * @exception <code>ClassCastException</code> if the argument is not a
     *		  <code>FloatValueType</code>.
     * @see     java.lang.Comparable
     * @since   1.2
     */
    public int compareTo(Object o) {
	return compareTo((FloatValueType)o);
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -2671257302660747028L;
}

package com.company;

import java.io.Serial;
import java.math.BigInteger;
import java.util.Random;

import org.jlinalg.DivisionByZeroException;
import org.jlinalg.FieldElement;
import org.jlinalg.IRingElement;
import org.jlinalg.IRingElementFactory;
import org.jlinalg.InvalidOperationException;
import org.jlinalg.JLinAlgTypeProperties;
import org.jlinalg.RingElementFactory;

public class FModular extends FieldElement<FModular>
    {

        /**
         *
         */
        @Serial
        private static final long serialVersionUID = 1L;
        public static BigInteger modulus = BigInteger.valueOf(2791);
        BigInteger value;

        /**
         * Builds an element in F2.
         * <P>
         * This should exclusively be used to create the instances {@link #ONE} and
         * {@link #ZERO}. No internal value is needed as only these two instances
         * are created and used.
         */
        private FModular(BigInteger val)
        {
            value = val.mod(modulus);
        }

        public static FModularFactory FACTORY(BigInteger i) {
            modulus = i;
            return FACTORY;
        }

        public BigInteger getModulus(){
            return modulus;
        }

        public BigInteger getValue(){
            return value;
        }

        /**
         * Calculates the sum of this element and another one.
         *
         * @param val
         *            a F2-value
         * @return sum <=> logical XOR
         * @exception InvalidOperationException
         *                if val is not in {{@link #ONE},{@link #ZERO} .
         */
        @Override
        public FModular add(FModular val)
        {
            return new FModular(value.add(val.value).mod(modulus));
        }

        /**
         * Calculates the difference between this element and another one.
         *
         * @param val
         *            a F2-value
         * @return difference <=> logical XOR
         * @exception InvalidOperationException
         *                if val is not in {{@link #ONE},{@link #ZERO} .
         */
        @Override
        public FModular subtract(FModular val)
        {
            return new FModular((value.subtract(val.value)).mod(modulus));
        }

        /**
         * Calculates the product of this element and another one.
         *
         * @param val
         * @return product <=> logical AND
         * @exception InvalidOperationException
         *                if val is not in {{@link #ONE},{@link #ZERO} .
         */
        @Override
        public FModular multiply(FModular val)
        {
            return new FModular(value.multiply(val.value).mod(modulus));
        }

        /**
         * Calculates the quotient of this FieldElement and another one.
         *
         * @param val
         * @return quotient <=> this value if val = 1m2 and undefined (Exception)
         *         otherwise
         * @exception InvalidOperationException
         *                if val == 0m2
         * @exception InvalidOperationException
         *                if val is not in {{@link #ONE},{@link #ZERO} .
         */
        @Override
        public FModular divide(FModular val)
        {
            return new FModular(value.multiply(val.value.modInverse(modulus)).mod(modulus));
        }


        /**
         * Calculates the inverse element of addition for this element. This is
         * incidentally for F2 <code>this</code>.
         *
         * @return negated <=> this value
         */
        @Override
        public FModular negate()
        {
            return new FModular(BigInteger.ZERO.subtract(value).mod(modulus));
        }



        /**
         * Calculates the inverse element of multiplication for this element.
         *
         * @return ONE if <code>this=={@link #ONE}</code> .
         * @throws DivisionByZeroException
         *             if <code>this=={@link #ZERO}</code> .
         */
        @Override
        public FModular invert() throws DivisionByZeroException
        {
            if (this.value.equals(BigInteger.ZERO)) {
                throw new DivisionByZeroException("Tried to invert zero.");
            }
            return new FModular(value.modInverse(modulus));
        }

        /**
         * Checks two elements for equality.
         *
         * @param val
         * @return true if the two FieldElements are mathematically equal.
         */
        @Override
        public boolean equals(Object val)
        {
            // it is sufficient to examine the objects for identity as only instance
            // for each value can exists
            FModular input = (FModular) val;
            return this.value.equals(input.value);
        }

        @Override
        public int compareTo(FModular input) {

            if(equals(input)) return 0;
            BigInteger sub = value.subtract(input.value);
            if(sub.compareTo(BigInteger.ZERO)==1){
                return 1;
            }else {
                return -1;
            }
        }

        /**
         * Returns a String representation of this element.
         *
         * @return <code>0m2</code> or <code>1m2</code>.
         */
        @Override
        public String toString()
        {
            return value.toString() + "m" + modulus;
        }

        /**
         * Implements Comparable.compareTo(Object).
         *
         * @param val
         *            the object
         * @return one of {-1,+,0} as this object is less than, equal to, or greater
         *         than the specified object.
         * @exception InvalidOperationException
         *                if val is not in {{@link #ONE},{@link #ZERO}.
         */

        /**
         * @return 0 or 1.
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return value.intValue();
        }

        /**
         * @return a reference to the singleton factory for this type.
         */
        @Override
        public IRingElementFactory<FModular> getFactory()
        {
            return FACTORY;
        }

        /**
         * the zero-instance for F2
         */
        public static final FModular ZERO = new FModular(BigInteger.ZERO);

        /**
         * the one-instance for F2
         */
        public static final FModular ONE = new FModular(BigInteger.ONE);

        /**
         * The instance of the singleton factory.
         */
        public static FModularFactory FACTORY = ZERO.new FModularFactory();

        /**
         * The factory class for the F2 data type. Only one instance of this factory
         * should ever exist.
         */
        @JLinAlgTypeProperties(isExact = true, isDiscreet = true, hasNegativeValues = false)
        public class FModularFactory
                extends RingElementFactory<FModular>
        {
            /**
             * create an array to hold <code>size</code> F2-instances.
             */
            @Override
            public FModular[] getArray(int size)
            {
                return new FModular[size];
            }

            /**
             * @exception InvalidOperationException
             *                F2-elements can only be obtained for <code>int</code>
             *                or <code>long</code> values.
             */
            @Override
            public FModular get(Object o)
            {
                if (o instanceof Integer) {
                    return new FModular(BigInteger.valueOf((Integer) o));
                }
                if (o instanceof Long) {
                    return new FModular(BigInteger.valueOf((Long) o));
                }
                if(o instanceof BigInteger){
                    return  new FModular((BigInteger) o);
                }

                return null;
            }

            /**
             * @return {@link #ZERO} if <code>i%2==0</code> and {@link #ONE}
             *         otherwise
             */
            @Override
            public FModular get(int i)
            {
                return new FModular(BigInteger.valueOf(i));
            }

            @Override
            public FModular get(long d) {
                return null;
            }

            /**
             * @exception InvalidOperationException
             *                F2-elements can only be obtained for <code>int</code>
             *                or <code>long</code> values.
             */
            @Override
            public FModular get(double d)
            {
                throw new InvalidOperationException(
                        "Cannot instanciate an F2--element from " + d);
            }

            /**
             * @return the same as {@link #randomValue()}
             */
            @SuppressWarnings("deprecation")
            @Override
            @Deprecated
            public FModular gaussianRandomValue(@SuppressWarnings("unused") Random random)
            {
                return new FModular(BigInteger.valueOf(random.nextInt()));
            }

            @Override
            public FModular randomValue(Random random) {
                return new FModular(BigInteger.valueOf(random.nextInt()));
            }

            /**
             * create an array to hold <code>rows</code> time <code>columns</code>
             * F2-instances.
             */
            @Override
            public FModular[][] getArray(int rows, int columns)
            {
                return new FModular[rows][columns];
            }

            @Override
            public FModular one() {
                return new FModular(BigInteger.ONE);
            }

            @Override
            public FModular zero() {
                return new FModular(BigInteger.ZERO);
            }

            @Override
            public FModular m_one() {
                return new FModular(BigInteger.valueOf(-1));
            }


            @Override
            public FModular randomValue(FModular min, FModular max) {
                return null;
            }

            @Override
            public FModular gaussianRandomValue() {
                return null;
            }

            @Override
            public FModular randomValue() {
                return null;
            }

            @Override
            public FModular randomValue(Random random, FModular min, FModular max) {
                return null;
            }


        }

        /**
         * @return {@code this}
         * @see org.jlinalg.IRingElement#abs()
         */
        @Override
        public FModular abs()
        {
            return this;
        }

    }

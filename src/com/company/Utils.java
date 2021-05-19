package com.company;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public long[] xgcd(long a, long b) {
        //returns gcd(a,b), x, y
        //where ax + by = gcd(a,b)
        long[] retvals = {0, 0, 0};
        long aa[] = {1, 0}, bb[] = {0, 1}, q = 0;
        while (true) {
            q = a / b;
            a = a % b;
            aa[0] = aa[0] - q * aa[1];
            bb[0] = bb[0] - q * bb[1];
            if (a == 0) {
                retvals[0] = b;
                retvals[1] = aa[1];
                retvals[2] = bb[1];
                return retvals;
            }
            ;
            q = b / a;
            b = b % a;
            aa[1] = aa[1] - q * aa[0];
            bb[1] = bb[1] - q * bb[0];
            if (b == 0) {
                retvals[0] = a;
                retvals[1] = aa[0];
                retvals[2] = bb[0];
                return retvals;
            }
        }
    }

    public int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }


    private static class Triple {
        public final BigInteger d;
        public final BigInteger s;
        public final BigInteger t;
        private Triple(final BigInteger d, final BigInteger s, final BigInteger t) {
            this.d = d;
            this.s = s;
            this.t = t;
        }
        @Override
        public String toString() {
            return "Triple{" +
                    "d=" + d +
                    ", s=" + s +
                    ", t=" + t +
                    '}';
        }
    }

    public static Triple apply(final BigInteger a, final BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return new Triple(a, BigInteger.ONE, BigInteger.ZERO);
        } else {
            final Triple extension = apply(b, a.mod(b));
            return new Triple(extension.d, extension.t, extension.s.subtract(a.divide(b).multiply(extension.t)));
        }
    }



    public int invMod(int a, int mod) {
        //Finds the inverse of a modulo m ( b s.t. a*b = 1 (mod m))

        if (a < 0) {
            a = mod + a;
        }

        // a and m must be coprime to find an inverse
        if (gcd(a, mod) != 1) {
            //should hopefully not happen
            logger.warning("inv mod got bad numbers");
        }

        long inverse = xgcd(a, mod)[1];
        return (int) (inverse % mod);
    }

    public BigInteger invModBig(BigInteger a, BigInteger mod){

        if(a.compareTo(BigInteger.ZERO)==-1){
            //if  a<0
            a = a.add(mod);
        }
        // a and m must be coprime to find an inverse
        if (!a.gcd(mod).equals(BigInteger.ONE)) {
            //should hopefully not happen
            logger.warning("inv mod got bad numbers");
            logger.warning("gcd is " + a.gcd(mod));
            logger.warning("numbers are: " + a + " "+ mod);
        }
        System.out.println("invMod got numbers: " + a + " " + mod);
        BigInteger inverse = apply(a, mod).t;

        System.out.println("returns: " + inverse.mod(mod));
        return inverse.mod(mod);

    }

    int prod(List<Integer> list){
        //Returns the product of the numbers in the list.
        int res =1;
        for (Integer i : list) {
            res *= i;
        }
        return res;
    }

    BigInteger prodBig(List<BigInteger> list){
        //Returns the product of the numbers in the list.
        BigInteger res =BigInteger.valueOf(1);
        for (BigInteger i : list) {
            res = res.multiply(i);
        }
        return res;
    }

    //TODO: Make this nice
    public BigInteger floorDiv(final BigInteger x, final BigInteger y) {
        if (x.signum() * y.signum() >= 0) {
            return x.divide(y);
        }
        final BigInteger[] qr = x.divideAndRemainder(y);
        return qr[1].signum() == 0 ? qr[0] : qr[0].subtract(BigInteger.ONE);
    }


    public EncMatrix addEncMatrices(List<EncMatrix> matrixList) throws Exception {

        EncMatrix start = matrixList.get(0);
        matrixList.remove(0);
        for ( EncMatrix matrix: matrixList) {
            start.plus(matrix);
        }
        return start;
    }


/*
    int crm (List<Integer> aList, List<Integer> nList){
        //Applies the Chinese Remainder Theorem to find the unique x
        // such that x = a_i (mod n_i) for all i.

        int N = prod(nList);

        List<Integer> yList = new LinkedList<>();
        for (int i = 0; i < nList.size(); i++) {
            yList.add(Math.floorDiv(N, nList.get(i)));
        }

        List<Integer> zList = new LinkedList<>();
        for (int i = 0; i < nList.size(); i++) {
            zList.add(invMod(yList.get(i),nList.get(i) ));
        }

        int x = 0;

        for (int i = 0; i < aList.size(); i++) {
            x += aList.get(i) * yList.get(i) * zList.get(i);
        }
        return x;
    }


 */
}

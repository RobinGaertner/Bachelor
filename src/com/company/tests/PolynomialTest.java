package com.company.tests;

import com.company.Polynomial;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PolynomialTest {

    @org.junit.jupiter.api.Test
    void call() {
        Polynomial poly = new Polynomial();

        List<BigInteger> coeffs = new LinkedList<>();
        coeffs.add(BigInteger.valueOf(1));
        coeffs.add(BigInteger.valueOf(2));
        coeffs.add(BigInteger.valueOf(3));
        coeffs.add(BigInteger.valueOf(4));
        coeffs.add(BigInteger.valueOf(5));

        int modulus = 23;

        poly.init(coeffs, BigInteger.valueOf(modulus));

        assertEquals(poly.call(0), coeffs.get(0));

        BigInteger tmp = BigInteger.valueOf(0);

        for (int i = 0; i < coeffs.size(); i++) {
            tmp = tmp.add(coeffs.get(i).multiply ( BigInteger.valueOf((long) Math.pow(5, i))) );
        }
        tmp = tmp.mod(BigInteger.valueOf(modulus));
        assertEquals(poly.call(5), tmp);
    }

    @org.junit.jupiter.api.Test
    void call2() {
        for (int j = 1; j < 250; j++) {
            int threshold = j;
            int modulus = 23;

            Polynomial poly = new Polynomial();

            List<BigInteger> coeffs = new LinkedList<>();
            for (int i=1; i<=threshold; i++) {
                coeffs.add(BigInteger.valueOf(i));
            }


            poly.init(coeffs, BigInteger.valueOf(modulus));

            assertEquals(poly.call(0).longValue(), coeffs.get(0).longValue());
            assertEquals(poly.call(1).longValue(), coeffs.stream().mapToInt(BigInteger::intValue).sum() % modulus );

            BigInteger tmp = BigInteger.valueOf(0);

            for (int i = 0; i < coeffs.size(); i++) {
                BigInteger tmp2 = BigInteger.valueOf(threshold).pow(i);
                tmp = tmp.add(coeffs.get(i).multiply(tmp2));
            }
            tmp = tmp.mod(BigInteger.valueOf(modulus));
            System.out.println(j);
            assertEquals(poly.call(threshold), tmp);
        }

    }


    @org.junit.jupiter.api.Test
    void call3() {
        Polynomial poly = new Polynomial();

        List<BigInteger> coeffs = new LinkedList<>();
        coeffs.add(BigInteger.valueOf(1));
        coeffs.add(BigInteger.valueOf(2));
        coeffs.add(BigInteger.valueOf(3));
        coeffs.add(BigInteger.valueOf(4));
        coeffs.add(BigInteger.valueOf(5));



        int modulus = 23;

        poly.init(coeffs, BigInteger.valueOf(modulus));

        BigInteger tmp = BigInteger.ZERO;

        for (int i = 0; i < coeffs.size(); i++) {
            tmp = tmp.add(coeffs.get(i).multiply ( BigInteger.valueOf((long) Math.pow(5, i))) );
        }
        tmp = tmp.mod(BigInteger.valueOf(modulus));
        assertEquals(poly.call(5),tmp);
    }

}
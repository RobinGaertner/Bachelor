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

        List<Integer> coeffs = new LinkedList<>();
        coeffs.add(1);
        coeffs.add(2);
        coeffs.add(3);
        coeffs.add(4);
        coeffs.add(5);

        int modulus = 23;

        poly.init(coeffs, modulus);

        assertEquals(poly.call(0), coeffs.get(0));
        assertEquals(poly.call(1), coeffs.stream().mapToInt(Integer::intValue).sum() % modulus );

        int tmp = 0;

        for (int i = 0; i < coeffs.size(); i++) {
            tmp += coeffs.get(i) * ( Math.pow(5, i) );
        }
        tmp = tmp % modulus;
        assertEquals(poly.call(5), tmp);
    }

    @org.junit.jupiter.api.Test
    void call2() {
        for (int j = 1; j < 25; j++) {
            int threshold = j;
            int modulus = 23;

            Polynomial poly = new Polynomial();

            List<Integer> coeffs = new LinkedList<>();
            for (int i=1; i<=threshold; i++) {
                coeffs.add(i);
            }


            poly.init(coeffs, modulus);

            assertEquals(poly.call(0).longValue(), coeffs.get(0).longValue());
            assertEquals(poly.call(1).longValue(), coeffs.stream().mapToInt(Integer::intValue).sum() % modulus );

            BigInteger tmp = BigInteger.valueOf(0);

            for (int i = 0; i < coeffs.size(); i++) {
                BigInteger tmp2 = BigInteger.valueOf(threshold).pow(i);
                tmp = tmp.add(BigInteger.valueOf(coeffs.get(i)).multiply(tmp2));
            }
            tmp = tmp.mod(BigInteger.valueOf(modulus));
            System.out.println(j);
            assertEquals(poly.call(threshold), tmp);
        }

    }


    @org.junit.jupiter.api.Test
    void call3() {
        Polynomial poly = new Polynomial();

        List<Integer> coeffs = new LinkedList<>();
        coeffs.add(1);
        coeffs.add(2);
        coeffs.add(3);
        coeffs.add(4);
        coeffs.add(5);

        int modulus = 23;

        poly.init(coeffs, modulus);

        int tmp = 0;

        for (int i = 0; i < coeffs.size(); i++) {
            tmp += coeffs.get(i) * ( Math.pow(5, i) );
        }
        tmp = tmp % modulus;
        assertEquals(poly.call(5), BigInteger.valueOf(tmp));
    }
}
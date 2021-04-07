package com.company;import java.util.Scanner;
import java.math.BigInteger;
import java.util.ArrayList;

public class CRTBig {


        static BigInteger x = new BigInteger("0");
        static BigInteger y = new BigInteger("0");

        // iterative implementation of Extended Euclidean Algorithm
        // calculate b_i such that b_i*p + q*c = gcd(r, q) <=> p*b_i == 1%q
        public static BigInteger extended_euclidean_algortihm(BigInteger p, BigInteger q){
            BigInteger s = new BigInteger("0"); // quotient during algorithm
            BigInteger s_old = new BigInteger("1"); // Bézout coefficient
            BigInteger t = new BigInteger("1"); // quotient during algorithm
            BigInteger t_old = new BigInteger("0"); // Bézout coefficient
            BigInteger r = q;
            BigInteger r_old = p; // greatest common divisor
            BigInteger quotient;
            BigInteger tmp;

            while (r.compareTo(BigInteger.valueOf(0)) != 0){ // do while r != 0
                quotient = r_old.divide(r);

                tmp = r; // temporarily store to update r, r_old simultaneously
                r = r_old.subtract(quotient.multiply(r));
                r_old = tmp;

                tmp = s;
                s = s_old.subtract(quotient.multiply(s));
                s_old = tmp;

                tmp = t;
                t = t_old.subtract(quotient.multiply(t));
                t_old = tmp;
            }

            x = s_old; // x*p + y*q == gcd(p,q) ; this means x will be our b_i
            y = t_old;
            return x;
        }

        public BigInteger chinese_remainder_theorem(ArrayList<BigInteger> A, ArrayList<BigInteger> Q, int k) {

            BigInteger p, tmp;
            BigInteger prod = new BigInteger("1"); // stores product of all moduli
            BigInteger sum = new BigInteger("0"); // sum x of CRT, which is also the solution after x % prod

            for (int i = 0; i < k; i++)
                prod = prod.multiply(Q.get(i)); // multiply all moduli together

            for (int i = 0; i < k; i++) {
                p = prod.divide(Q.get(i));	// divide by current modulus to get product excluding said modulus
                tmp = extended_euclidean_algortihm(p, Q.get(i)); // calculate mod_inv b_i such that b_i*p == 1 % Q.get(i)
                sum = sum.add(A.get(i).multiply(tmp).multiply(p)); // sum up all products of integer a, product p, modulo inverse of p and Q.get(i)
            }

            return sum.mod(prod); // mod with product of all moduli to get smallest/unique integer
        }
}

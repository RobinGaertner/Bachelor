package com.company;

import java.math.BigInteger;
import java.util.List;

public class Polynomial {

    List<Integer> coeffs;
    BigInteger modulus;

    public void init(List<Integer> coeffs, int mod){
        //Initializes the polynomial.
        this.coeffs = coeffs;
        modulus = BigInteger.valueOf(mod);

    }

    public BigInteger call(int x){
        BigInteger X = BigInteger.valueOf(x);
        BigInteger fx = BigInteger.valueOf(0);
        for (int i = 0; i < coeffs.size(); i++) {
            BigInteger tmp = X.modPow(BigInteger.valueOf(i), modulus);
            BigInteger tmp2 = tmp.multiply(BigInteger.valueOf(coeffs.get(i))).mod(modulus);
            fx = tmp2.add(fx).mod(modulus);
        }


        return fx;
    }
}

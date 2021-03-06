package com.company;

import java.math.BigInteger;
import java.util.List;

public class Polynomial {

    List<BigInteger> coeffs;
    //sorted increasing:
    //starts with c0*x^0, c1*x^1, c2*x^2
    BigInteger modulus;


    public void init(List<BigInteger> coeffs, BigInteger mod){
        //Initializes the polynomial.
        this.coeffs = coeffs;
        modulus = mod;

    }


    public BigInteger call(int x){
        BigInteger X = BigInteger.valueOf(x);
        BigInteger fx = BigInteger.valueOf(0);
        for (int i = 0; i < coeffs.size(); i++) {
            BigInteger tmp = X.modPow(BigInteger.valueOf(i), modulus);
            BigInteger tmp2 = tmp.multiply(coeffs.get(i)).mod(modulus);
            fx = tmp2.add(fx).mod(modulus);
        }


        return fx;
    }

    public BigInteger call(BigInteger X){
        BigInteger fx = BigInteger.valueOf(0);
        for (int i = 0; i < coeffs.size(); i++) {
            BigInteger tmp = X.modPow(BigInteger.valueOf(i), modulus);
            BigInteger tmp2 = tmp.multiply(coeffs.get(i)).mod(modulus);
            fx = tmp2.add(fx).mod(modulus);
        }


        return fx;
    }

    public int degree(){
        return coeffs.size()-1;
    }
}

package com.company;

import java.math.BigInteger;
import java.util.List;

public class EncPolynomial {

    List<EncryptedNumber> coeffs;
    PublicKey pk;
    //sorted increasing:
    //starts with c0*x^0, c1*x^1, c2*x^2


    public void init(List<EncryptedNumber> coeffs){
        //Initializes the polynomial.
        this.coeffs = coeffs;
        pk = coeffs.get(0).publicKey;
    }


    public EncryptedNumber call(int x) throws Exception {

        BigInteger X = BigInteger.valueOf(x);
        EncryptedNumber fx = pk.encrypt(BigInteger.ZERO);
        for (int i = 0; i < coeffs.size(); i++) {
            BigInteger tmp = X.pow(i);
            EncryptedNumber tmp2 = coeffs.get(i).mul(tmp);
            fx = tmp2.add(fx);
        }


        return fx;
    }

    public EncryptedNumber call(BigInteger X) throws Exception {
        EncryptedNumber fx = pk.encrypt(BigInteger.ZERO);
        for (int i = 0; i < coeffs.size(); i++) {
            BigInteger tmp = X.pow(i);
            EncryptedNumber tmp2 = coeffs.get(i).mul(tmp);
            fx = tmp2.add(fx);
        }


        return fx;
    }

    public int degree(){
        return coeffs.size()-1;
    }
}

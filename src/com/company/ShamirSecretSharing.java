package com.company;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ShamirSecretSharing {

    public List<Share> shareSecret(BigInteger secret, BigInteger modulus, int threshold, int nShares){
            /* :param secret: The secret to be shared.
    :param modulus: The modulus used when sharing the secret.
    :param threshold: The minimum number of shares that will be needed to reconstruct the secret.
    :param n_shares: The number of shares to create.
    :return: A list of shares of the secret, where each share is the tuple (x, f(x)) and where f(0) is the secret.
    """
             */


        //ensure valid parameters

        if (secret.compareTo(BigInteger.ZERO)!=1){
            throw new Error("Secret must be >=0");
        }
        if (secret.compareTo(modulus)==1){
            throw new Error("Secret must be <modulus");
        }
        if (nShares< threshold){
            throw new Error("number of shares must be at least as large as threshold");
        }
        if (threshold<1){
            throw new Error("Threshold and number of shares must at least be 1");
        }


        //create the polynomial that will be used to share the secret
        //(f(0) = secret
        List<BigInteger> coeffs = new LinkedList<>();
        coeffs.add(secret);

        for (int i = 0; i < threshold - 1; i++) {
            coeffs.add(nextRandomBigInteger(modulus));
        }

        Polynomial polynomial = new Polynomial();
        polynomial.init(coeffs, modulus);


        List<Integer> X = new LinkedList<>();
        for (int i=1; i<=nShares; i++) {
            X.add(i);
        }

        List<Share> shares = new LinkedList<>();
        for (int i = 0; i < X.size(); i++) {
            shares.add(new Share(X.get(i), polynomial.call(X.get(i))));
        }

        return shares;
    }

    public BigInteger reconstruct(List<Share> shares, BigInteger modulus){
        //long secret = 0;
        BigInteger secret2 = BigInteger.valueOf(0);
        for (int i = 0; i < shares.size(); i++) {

            BigInteger product2 = BigInteger.valueOf(1);
            BigInteger mod = modulus;

            for (int j = 0; j < shares.size(); j++) {
                if(i!=j){
                    int xj = shares.get(j).X;
                    int xi = shares.get(i).X;

                    BigInteger inv = BigInteger.valueOf(xj-xi).modInverse(modulus);
                    product2 = product2.multiply(inv.multiply(BigInteger.valueOf(xj)));
                    product2 = product2.mod(mod);
                }
            }

            secret2 = (secret2.add((shares.get(i).fX.multiply(product2)).mod(mod)));
            secret2 = secret2.mod(mod);

        }

        return secret2;
    }

    public BigInteger nextRandomBigInteger(BigInteger n) {

        Random rand = new Random();
        BigInteger result = new BigInteger(n.bitLength(), rand);
        while( result.compareTo(n) >= 0 ) {
            result = new BigInteger(n.bitLength(), rand);
        }
        return result;
    }

}

package com.company;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ShamirSecretSharing {

    Utils utils = new Utils();

    public List<Share> shareSecret(int secret, int modulus, int threshold, int nShares){
            /* :param secret: The secret to be shared.
    :param modulus: The modulus used when sharing the secret.
    :param threshold: The minimum number of shares that will be needed to reconstruct the secret.
    :param n_shares: The number of shares to create.
    :return: A list of shares of the secret, where each share is the tuple (x, f(x)) and where f(0) is the secret.
    """
             */

        Random rnd = new Random();

        //ensure valid parameters
        if (secret<0){
            throw new Error("Secret must be >=0");
        }
        if (!(secret<=modulus)){
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
        List<Integer> coeffs = new LinkedList<>();
        coeffs.add(secret);

        for (int i = 0; i < threshold - 1; i++) {
            coeffs.add(rnd.nextInt(modulus-1));
        }

        Polynomial polynomial = new Polynomial();
        polynomial.init(coeffs, modulus);


        List<Integer> X = new LinkedList<>();
        for (int i=1; i<=10; i++) {
            X.add(i);
        }

        List<Share> shares = new LinkedList<Share>();
        for (int i = 0; i < X.size(); i++) {
            shares.add(new Share(X.get(i), polynomial.call(X.get(i))));
        }

        return shares;
    }

    public int reconstruct(List<Share> shares, int modulus){
        long secret = 0;
        for (int i = 0; i < shares.size(); i++) {
            /*
            long product = 1;

            for (int j = 0; j < shares.size(); j++) {
                if(i!=j){
                    int xj = shares.get(j).part1;
                    int xi = shares.get(i).part1;
                    product = (product * (xj * utils.invMod(xj-xi, modulus))%modulus) % modulus;
                }
            }

            secret = Math.addExact(secret, Math.multiplyExact(shares.get(i).part2.longValue(), product2) % modulus) % modulus;

             */



            BigInteger product2 = BigInteger.valueOf(1);

            for (int j = 0; j < shares.size(); j++) {
                if(i!=j){
                    int xj = shares.get(j).part1;
                    int xi = shares.get(i).part1;
                    BigInteger inv = BigInteger.valueOf(utils.invMod(xj-xi, modulus));
                    product2 = product2.multiply(inv.multiply(BigInteger.valueOf(xj).mod(BigInteger.valueOf(modulus)))).mod(BigInteger.valueOf(modulus));
                }
            }

            secret = Math.addExact(secret, Math.multiplyExact(shares.get(i).part2.longValue(), product2.longValue()) % modulus) % modulus;

        }
        return (int) secret;
    }
}

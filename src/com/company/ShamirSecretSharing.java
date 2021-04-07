package com.company;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ShamirSecretSharing {

    Utils utils = new Utils();

    public List<Share> shareSecret(BigInteger secret, BigInteger modulus, int threshold, int nShares){
            /* :param secret: The secret to be shared.
    :param modulus: The modulus used when sharing the secret.
    :param threshold: The minimum number of shares that will be needed to reconstruct the secret.
    :param n_shares: The number of shares to create.
    :return: A list of shares of the secret, where each share is the tuple (x, f(x)) and where f(0) is the secret.
    """
             */

        Random rnd = new Random();

        //ensure valid parameters
        //TODO: do this later
        /*
        if (secret.longValue()<0){
            throw new Error("Secret must be >=0");
        }
        if (!(secret.longValue()<=modulus.longValue())){
            throw new Error("Secret must be <modulus");
        }
        if (nShares< threshold){
            throw new Error("number of shares must be at least as large as threshold");
        }
        if (threshold<1){
            throw new Error("Threshold and number of shares must at least be 1");
        }

         */
        //create the polynomial that will be used to share the secret
        //(f(0) = secret
        List<BigInteger> coeffs = new LinkedList<>();
        coeffs.add(secret);

        for (int i = 0; i < threshold - 1; i++) {
            //TODO: here is no exact, but hopefully right
            int tmp = 0;

            //coeffs.add(BigInteger.valueOf(rnd.nextInt(modulus.intValueExact())));
            coeffs.add(nextRandomBigInteger(modulus));
        }

        Polynomial polynomial = new Polynomial();
        polynomial.init(coeffs, modulus);
        System.out.println("Coeffs of the polynomial" + coeffs);


        List<Integer> X = new LinkedList<>();
        for (int i=1; i<=nShares; i++) {
            X.add(i);
        }

        List<Share> shares = new LinkedList<Share>();
        for (int i = 0; i < X.size(); i++) {
            shares.add(new Share(X.get(i), polynomial.call(X.get(i))));
        }

        return shares;
    }

    public int reconstruct(List<Share> shares, int modulus){
        //long secret = 0;
        BigInteger secret2 = BigInteger.valueOf(0);
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
            BigInteger mod = BigInteger.valueOf(modulus);

            for (int j = 0; j < shares.size(); j++) {
                if(i!=j){
                    int xj = shares.get(j).X;
                    int xi = shares.get(i).X;
                    BigInteger inv = BigInteger.valueOf(utils.invMod(xj-xi, modulus));
                    product2 = product2.multiply(inv.multiply(BigInteger.valueOf(xj)));
                    product2 = product2.mod(mod);
                }
            }

            //secret = Math.addExact(secret, Math.multiplyExact(shares.get(i).fX.longValue(), product2.longValue()) % modulus) % modulus;
            secret2 = (secret2.add((shares.get(i).fX.multiply(product2)).mod(mod)));
            secret2 = secret2.mod(mod);

        }

        return secret2.intValueExact();
    }

    //TODO: copied
    public BigInteger nextRandomBigInteger(BigInteger n) {
        Random rand = new Random();
        BigInteger result = new BigInteger(n.bitLength(), rand);
        while( result.compareTo(n) >= 0 ) {
            result = new BigInteger(n.bitLength(), rand);
        }
        return result;
    }

}

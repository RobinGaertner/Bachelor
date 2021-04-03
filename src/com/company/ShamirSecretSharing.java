package com.company;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class ShamirSecretSharing {

    Utils utils;

    public List<int[]> shareSecret(int secret, int modulus, int threshold, int nShares){
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

        //create the polynomial that woll be used to share the secret
        //(f(0) = secret
        List<Integer> coeffs = new LinkedList<>();
        coeffs.add(secret);
        for (int i = 0; i < threshold - 1; i++) {
            coeffs.add(rnd.nextInt(modulus-1));
        }

        Polynomial polynomial = new Polynomial();
        polynomial.init(coeffs, modulus);

        List<Integer> X = (List<Integer>) IntStream.range(0, threshold-1);

        List<int[]> shares = new LinkedList<int[]>();
        for (int i = 0; i < X.size(); i++) {
            int[] tmp = new int[2];
            tmp[0] = X.get(i);
            tmp[1] = polynomial.call(X.get(i));
            shares.add(tmp);
        }

        return shares;
    }

    int reconstruct(List<int[]> shares, int modulus){
        int secret = 0;
        for (int i = 0; i < shares.size(); i++) {
            int product = 1;

            for (int j = 0; j < shares.size(); j++) {
                if(i!=j){
                    int xj = shares.get(j)[0];
                    int xi = shares.get(i)[0];
                    product = (product * xj * utils.invMod(xj-xi, modulus)) % modulus;
                }
            }
            secret = (secret + shares.get(i)[1] * product % modulus) % modulus;
        }
        return secret;
    }
}

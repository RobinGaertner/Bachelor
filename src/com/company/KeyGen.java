package com.company;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class KeyGen {

    static PrimeGen primeGen = new PrimeGen();
    static Utils utils = new Utils();
    static ShamirSecretSharing shamirSecretSharing = new ShamirSecretSharing();
    static CRTBig crt = new CRTBig();

    public static Containter keyGen(int nBits, int s, int threshold, int nShares) throws Exception {
        /*Generates a PublicKey and a PrivateKeyRing using the threshold variant of Damgard-Jurik.
    The PublicKey is a single key which can be used to encrypt numbers
    while the PrivateKeyRing contains a number of PrivateKeyShares which
    must be used together to decrypt encrypted numbers.

    :param n_bits: The number of bits to use in the public and private keys.
    :param s: The power to which n = p * q will be raised. Plaintexts live in Z_n^s.
    :param threshold: The minimum number of PrivateKeyShares needed to decrypt an encrypted number.
    :param n_shares: The number of PrivateKeyShares to generate.
    :return: A tuple containing the generated PublicKey and PrivateKeyRing.
    """


         */
        if (nBits<16) {
            //TODO: comment only for testing
            //throw new Error("Minimum number of Bits is 16");
        }
        if (s<1) {

            throw new Error("S must be greater than 1");
        }
        if (nShares<threshold) {
            throw new Error("Number of shares must be at least as large as the threshold");
        }
        if (threshold<1) {

            throw new Error("Threshold and number of shares must at least be 1");
        }
        //invalid inputs finished

        BigInteger[] primes = primeGen.getSafePrimePair(nBits);
        BigInteger p = primes[0];
        BigInteger q = primes[1];
        BigInteger pPrime = utils.floorDiv(p.subtract(BigInteger.ONE), BigInteger.valueOf(2));

        BigInteger qPrime = utils.floorDiv(q.subtract(BigInteger.ONE), BigInteger.valueOf(2));
        BigInteger n = p.multiply(q);
        BigInteger m = pPrime.multiply(qPrime);



        //precompute for convenience

        BigInteger ns = n.pow(s);
        BigInteger nsm = ns.multiply(m);
        ArrayList<BigInteger> list1 = new ArrayList<>();
        list1.add(BigInteger.valueOf(0));
        list1.add(BigInteger.valueOf(1));

        ArrayList<BigInteger> list2 = new ArrayList<>();
        list2.add(m);
        list2.add(ns);


        BigInteger D = crt.chinese_remainder_theorem(list1, list2, 2);



        List<Share> shares = shamirSecretSharing.shareSecret(D, nsm, threshold, nShares);

        //Create PublicKey and PrivateKeyShares
        BigInteger delta =  factorial(nShares);
        PublicKey publicKey = new PublicKey();
        publicKey.init(n, s, m, threshold, delta);

        List<PrivateKeyShare> privateKeyShares = new LinkedList<>();

        for (int i = 0; i < shares.size(); i++) {
            PrivateKeyShare tmp = new PrivateKeyShare();
            tmp.init(publicKey, shares.get(i).X, shares.get(i).fX);
            privateKeyShares.add(tmp);
        }

        PrivateKeyRing privateKeyRing = new PrivateKeyRing();
        privateKeyRing.init(privateKeyShares);

        return new Containter(publicKey, privateKeyRing);
    }


    public static BigInteger factorial(int number) {
        BigInteger factorial = BigInteger.ONE;

        for (int i = number; i > 0; i--) {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }

        return factorial;
    }


}

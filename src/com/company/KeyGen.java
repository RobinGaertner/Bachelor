package com.company;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import jdk.jshell.execution.Util;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class KeyGen {

    PrimeGen primeGen;
    Utils utils;

    void keyGen(int nBits, int s, int threshold, int nShares) {
        /*Generates a PublicKey and a PrivateKeyRing using the threshold variant of Damgard-Jurik.
    The PublicKey is a single key which can be used to encrypt numbers
    while the PrivateKeyRing contains a number of PrivateKeyShares which
    must be used together to decrypt encrypted numbers.

         */
        if (nBits<16) {

            throw new Error("Minimum number of Bits is 16");
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
        int pPrime = (int) Math.floorDiv(p.longValue()-1, 2);

        int qprime = (int) Math.floorDiv(q.longValue()-1, 2);
        BigInteger n = p.multiply(q);
        int m = pPrime * qprime;


        //precompute for convenience

        BigInteger ns = n.pow(s);
        BigInteger nsm = ns.pow(m);

        //find d such thst d=0 mod m and d=1 mod n^s
        List<Integer> list1 = new LinkedList<>();
        list1.add(0);
        list1.add(1);

        List<Integer> list2 = new LinkedList<>();
        list2.add(m);
        list2.add(ns.intValue());

        int d = utils.crm(list1, list2);

        //TODO:  Use Shamir secret sharing to share_secret d


        //Create PublicKey and PrivateKeyShares
        int delta = (int) CombinatoricsUtils.factorial(nShares);
        PublicKey publicKey = new PublicKey();
        publicKey.init(n.intValue(), s, m, threshold, delta);

        List<PrivateKeyShare> privateKeyShares;
        //TODO: add correctly when share secret is implemented
        //TODO DO THIS RIGHT
        //privateKeyShares.init(publicKey, );

        PrivateKeyRing privateKeyRing = new PrivateKeyRing();
        //TODO DO THIS AS WELL
        //privateKeyRing.init(privateKeyShares);

        //TODO: ADD THIS AS WELL
        //return publicKey, privateKeyRing;
    }

}

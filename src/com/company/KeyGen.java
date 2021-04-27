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

        System.out.println("p: " + p );
        System.out.println("q: " + q );
        System.out.println("p1: " + pPrime );
        System.out.println("q1: " + qPrime );



        System.out.println("p*q" + n );
        System.out.println("euler of N"  + m );



        //precompute for convenience

        BigInteger ns = n.pow(s);
        BigInteger nsm = ns.multiply(m);
 /*
        //find d such that d=0 mod m and d=1 mod n^s
        List<Integer> list1 = new LinkedList<>();
        list1.add(0);
        list1.add(1);

        List<Integer> list2 = new LinkedList<>();
        list2.add(m);
        list2.add(ns.intValueExact());

        System.out.println("list1"+list1);
        System.out.println("list2" +list2);
        int d = utils.crm(list1, list2);

        System.out.println("crm return "+d);

 */


///*
        ArrayList<BigInteger> list1 = new ArrayList<BigInteger>();
        list1.add(BigInteger.valueOf(0));
        list1.add(BigInteger.valueOf(1));

        ArrayList<BigInteger> list2 = new ArrayList<BigInteger>();
        list2.add(m);
        list2.add(ns);



        System.out.println("list1"+list1);
        System.out.println("list2" +list2);

        BigInteger D = crt.chinese_remainder_theorem(list1, list2, 2);

        System.out.println("crm return "+D);


        List<Share> shares = shamirSecretSharing.shareSecret(D, nsm, threshold, nShares);

        System.out.println("shamirSecret sharing finished ");
        //Create PublicKey and PrivateKeyShares
        BigInteger delta =  factorial(nShares);
        System.out.println("factorial finished : " + delta);
        PublicKey publicKey = new PublicKey();
        publicKey.init(n, s, m, threshold, delta);
        System.out.println("PublicKey init finished ");

        List<PrivateKeyShare> privateKeyShares = new LinkedList<PrivateKeyShare>();

        for (int i = 0; i < shares.size(); i++) {
            PrivateKeyShare tmp = new PrivateKeyShare();
            tmp.init(publicKey, shares.get(i).X, shares.get(i).fX);
            privateKeyShares.add(tmp);
        }

        System.out.println("before PrivateKeyRing init ");
        PrivateKeyRing privateKeyRing = new PrivateKeyRing();
        privateKeyRing.init(privateKeyShares);

        System.out.println("after privatekeyring init ");
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

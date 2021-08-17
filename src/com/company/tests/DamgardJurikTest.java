package com.company.tests;

import com.company.*;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DamgardJurikTest {

    //TODO: change seed
    Random rnd = new Random();
    PrimeGen primeGen = new PrimeGen();


    @org.junit.jupiter.api.Test
    void call() throws Exception {
        for (int i = 0; i < 10; i++) {
            System.out.println("Test: " + i);

            int nBits = rnd.nextInt(32)+16;
            int s = rnd.nextInt(5)+1;
            int threshold = rnd.nextInt(10)+1;
            int nShares = 2* threshold + rnd.nextInt(10);

            System.out.println("nBits " + nBits);
            System.out.println("s: " + s);
            System.out.println("nShares: " + nShares);
            System.out.println("Threshold: " + threshold);

            Container container = KeyGen.keyGen(nBits, s, threshold, nShares);

            System.out.println("Container finished " + container);


            BigInteger m = BigInteger.valueOf(rnd.nextInt(100000));
            EncryptedNumber c = container.getPublicKey().encrypt(m);

            System.out.println("Encrpt finished " + c);

            BigInteger mPrime = container.getPrivateKeyRing().decrypt(c);

            System.out.println("decrypt finished");

            assertEquals(m, mPrime);

        }
    }

    @org.junit.jupiter.api.Test
    void primeTest() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Test: " + i);

            BigInteger tmp = primeGen.genPrime(100);
            System.out.println(tmp);

        }
    }

    @org.junit.jupiter.api.Test
    void primeTest2() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Test: " + i);

            BigInteger tmp = primeGen.genSafePrime(100);
            System.out.println(tmp);

        }
    }

    @org.junit.jupiter.api.Test
    void primeTest3() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Test: " + i);

            BigInteger[] tmp = primeGen.getSafePrimePair(100);
            System.out.println(tmp[0] + " " + tmp[1]);

        }
    }

}

package com.company.tests;

import com.company.Containter;
import com.company.EncryptedNumber;
import com.company.KeyGen;
import com.company.Polynomial;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DamgardJurikTest {

    Random rnd = new Random();
    KeyGen keyGen = new KeyGen();


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

            Containter containter = KeyGen.keyGen(nBits, s, threshold, nShares);

            System.out.println("Container finished " + containter);


            int m = rnd.nextInt(100000);
            EncryptedNumber c = containter.getPublicKey().encrypt(m);

            System.out.println("Encrpt finished " + c);

            int mPrime = containter.getPrivateKeyRing().decrypt(c);

            System.out.println("decrypt finished");

            assertEquals(m, mPrime);

        }
    }
}

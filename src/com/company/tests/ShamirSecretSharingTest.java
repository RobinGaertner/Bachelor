package com.company.tests;

import com.company.PrimeGen;
import com.company.ShamirSecretSharing;
import com.company.Share;
import com.company.Utils;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ShamirSecretSharingTest {

    Random rnd = new Random();
    PrimeGen primeGen = new PrimeGen();
    ShamirSecretSharing shamir = new ShamirSecretSharing();
    Utils utils = new Utils();
    @Test
    void shareSecret() {

        int nBits = 32;
        for (int i = 0; i < 100; i++) {
            int modulus = primeGen.genPrime(nBits);
            int secret = rnd.nextInt(modulus);
            int nShares = rnd.nextInt(20)+1;
            int threshold = rnd.nextInt(nShares) +1;


            System.out.println("Modulus: " + modulus);
            System.out.println("Secret: " + secret);
            System.out.println("nShares: " + nShares);
            System.out.println("Threshold: " + threshold);

            List<Share> shares = shamir.shareSecret(BigInteger.valueOf(secret), BigInteger.valueOf(modulus), threshold, nShares);

            System.out.println("Shares" + shares);
            int secretPrime = shamir.reconstruct(shares, modulus);

            assertEquals(secret, secretPrime);
        }
    }

    @Test
    void reconstruct() {
    }

    @Test
    void shareSecret2() {

        int nBits = 32;
        for (int i = 0; i < 10; i++) {
            int modulus = 59;
            int secret = 47;
            int nShares = 17;
            int threshold = 11;


            System.out.println("Modulus: " + modulus);
            System.out.println("Secret: " + secret);
            System.out.println("nShares: " + nShares);
            System.out.println("Threshold: " + threshold);

            List<Share> shares = shamir.shareSecret(BigInteger.valueOf(secret), BigInteger.valueOf(modulus), threshold, nShares);

            int secretPrime = shamir.reconstruct(shares, modulus);

            assertEquals(secret, secretPrime);
            System.out.println("WORKED ONCE");
        }
    }

    @Test
    void invModTest() {
           int myInt = utils.invMod(12356, 4567);
           int wanted = 1674;

            assertEquals(wanted, myInt);
        }

}
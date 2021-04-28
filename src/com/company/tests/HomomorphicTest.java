package com.company.tests;

import com.company.*;

import java.math.BigInteger;
import java.security.Key;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HomomorphicTest {

    //TODO: change seed
    Random rnd = new Random();
    PrimeGen primeGen = new PrimeGen();
    KeyGen keyGen = new KeyGen();
    Utils utils = new Utils();

    @org.junit.jupiter.api.Test
    void addTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            System.out.println("Test: " + i);

            Containter containter = KeyGen.keyGen(64, 3, 5, 9);

            BigInteger plain1 = BigInteger.valueOf(rnd.nextInt(100));
            BigInteger plain2 = BigInteger.valueOf(rnd.nextInt(100));

            EncryptedNumber c1 = containter.getPublicKey().encrypt(plain1);
            EncryptedNumber c2 = containter.getPublicKey().encrypt(plain2);
            EncryptedNumber cGes = c1.add(c2);

            BigInteger decrypted = containter.getPrivateKeyRing().decrypt(cGes);

            assertEquals(plain1, containter.getPrivateKeyRing().decrypt(c1));
            assertEquals(plain2, containter.getPrivateKeyRing().decrypt(c2));
            assertEquals(plain1.add(plain2), decrypted);

        }
    }

    @org.junit.jupiter.api.Test
    void subTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            System.out.println("Test: " + i);

            Containter containter = KeyGen.keyGen(64, 3, 5, 9);

            BigInteger plain1 = BigInteger.valueOf(rnd.nextInt(100));
            BigInteger plain2 = BigInteger.valueOf(rnd.nextInt(100));

            EncryptedNumber c1 = containter.getPublicKey().encrypt(plain1);
            EncryptedNumber c2 = containter.getPublicKey().encrypt(plain2);
            EncryptedNumber cGes = c1.sub(c2);

            BigInteger decrypted = containter.getPrivateKeyRing().decrypt(cGes);

            assertEquals(plain1, containter.getPrivateKeyRing().decrypt(c1));
            assertEquals(plain2, containter.getPrivateKeyRing().decrypt(c2));
            assertEquals(plain1.subtract(plain2), decrypted);

        }
    }

    @org.junit.jupiter.api.Test
    void mulTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            System.out.println("Test: " + i);

            Containter containter = KeyGen.keyGen(64, 3, 5, 9);

            BigInteger plain1 = BigInteger.valueOf(rnd.nextInt(100));
            BigInteger scalar = BigInteger.valueOf(rnd.nextInt(100));

            EncryptedNumber c1 = containter.getPublicKey().encrypt(plain1);
            EncryptedNumber cGes = c1.mul(scalar);

            BigInteger decrypted = containter.getPrivateKeyRing().decrypt(cGes);

            assertEquals(plain1, containter.getPrivateKeyRing().decrypt(c1));
            assertEquals(plain1.multiply(scalar), decrypted);

        }
    }

    @org.junit.jupiter.api.Test
    void divTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            System.out.println("Test: " + i);

            Containter containter = KeyGen.keyGen(64, 3, 5, 9);

            BigInteger scalar = BigInteger.valueOf(rnd.nextInt(100)).add(BigInteger.ONE);
            BigInteger multiple = BigInteger.valueOf(rnd.nextInt(100)).add(BigInteger.ONE);
            System.out.println("multiple: " + multiple);
            System.out.println("scalar: " + scalar);


            BigInteger plainText = scalar.multiply(multiple);
            System.out.println("plainText: " + plainText);

            EncryptedNumber cipher = containter.getPublicKey().encrypt(plainText);
            cipher.trueDiv(scalar);

            BigInteger decrypted = containter.getPrivateKeyRing().decrypt(cipher);

            assertNotEquals(plainText, cipher.value);
            assertEquals(utils.floorDiv(plainText, scalar), decrypted);

        }
    }


}

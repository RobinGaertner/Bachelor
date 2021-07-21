package com.company.tests;

import com.company.*;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ObliviousSupportTests {

    //TODO: change seed
    Random rnd = new Random();
    KeyGen keyGen = new KeyGen();
    int numParties = 5;
    Containter con = KeyGen.keyGen(16, 3, numParties,numParties);
    int x = 5;
    ObliviousAlgebraCoordinator coordinator = new ObliviousAlgebraCoordinator(5, con.getPublicKey(), con.getPrivateKeyRing());
    ObliviousAlgebra obliviousAlgebra = new ObliviousAlgebra(new PublicKey(), new PrivateKeyShare(),coordinator, 1);
    PublicKey pk = coordinator.publicKey;
    PrivateKeyRing keyRing = coordinator.privateKeyRing;
    BigInteger FMod = BigInteger.valueOf(1097);


    public ObliviousSupportTests() throws Exception {
    }


    @org.junit.jupiter.api.Test
    void secMultTest1() throws Exception {
        for (int i = 0; i < 3; i++) {



            IntMatrix A = IntMatrix.random(x,x);

            IntMatrix B = IntMatrix.random(x,x);

            EncMatrix AEnc = new EncMatrix(A, pk);
            EncMatrix BEnc = new EncMatrix(B, pk);

            System.out.println("got to multiplication of normal matrices");

            System.out.println(A.times(B));
            System.out.println("finished normal multiplication");
            EncMatrix multResult = coordinator.secMult(AEnc, BEnc, pk);

            System.out.println("Plain A.times B is  " + A.times(B));

            IntMatrix intResult = coordinator.privateKeyRing.decryptMatrix(multResult);

            System.out.println("secMult result is:  " + intResult);

            assertTrue(A.times(B).eq(intResult));



        }
    }



    @org.junit.jupiter.api.Test
    void numMultiplicationTest() throws Exception {
        for (int i = 0; i < 3; i++) {
            ObliviousAlgebraCoordinator coordinator2 = new ObliviousAlgebraCoordinator(numParties, con.getPublicKey(), con.getPrivateKeyRing());


            BigInteger[][] data1 = new BigInteger[1][1];
            BigInteger[][] data2 = new BigInteger[1][1];

            data1[0][0] = BigInteger.valueOf(i);
            data2[0][0] = BigInteger.valueOf(i+3);


            IntMatrix A = new IntMatrix(data1);

            IntMatrix B = new IntMatrix(data2);

            EncMatrix AEnc = new EncMatrix(A, coordinator2.publicKey);
            EncMatrix BEnc = new EncMatrix(B, coordinator2.publicKey);

            System.out.println("got to multiplication of normal matrices");

            System.out.println(A.times(B));
            System.out.println("finished normal multiplication");
            EncMatrix multResult = coordinator2.secMult(AEnc, BEnc, coordinator2.publicKey);

            System.out.println("Plain A.times B is  " + A.times(B));


            IntMatrix intResult = coordinator2.privateKeyRing.decryptMatrix(multResult);

            System.out.println("secMult result is:  " + intResult);

            assertTrue(A.times(B).eq(intResult));



        }
    }


    @org.junit.jupiter.api.Test
    void encMultiplyTest() throws Exception {

        CountingTestCoordinator cc = new CountingTestCoordinator(10, 10, FMod);
        for (int i = 0; i < 10; i++) {

            EncryptedNumber a = cc.publicKey.encrypt(BigInteger.valueOf(i));
            EncryptedNumber b = cc.publicKey.encrypt(BigInteger.valueOf(i+2));

            EncryptedNumber res = cc.multiplyEnc(a,b);

            assertEquals(cc.privateKeyRing.decrypt(res), BigInteger.valueOf(i*(i+2)));



        }
    }




    @org.junit.jupiter.api.Test
    void encDecMatrixTest() throws Exception {
        int x = 5;
        for (int i = 0; i < 10; i++) {


            IntMatrix rnd = IntMatrix.random(5,5);
            System.out.println(rnd);
            EncMatrix enc = new EncMatrix(rnd, pk);
            IntMatrix dec = keyRing.decryptMatrix(enc);
            System.out.println(dec);

            assertTrue(rnd.eq(dec));



        }
    }

    @org.junit.jupiter.api.Test
    void sumUpMatrixTest() throws Exception {
        int x = 5;
        for (int i = 0; i < 10; i++) {


            IntMatrix A = IntMatrix.random(x,x);
            IntMatrix B = IntMatrix.random(x,x);
            IntMatrix C = IntMatrix.random(x,x);
            IntMatrix D = IntMatrix.random(x,x);
            IntMatrix E = IntMatrix.random(x,x);

            IntMatrix intVal = A.plus(B);
            intVal = intVal.plus(C);
            intVal = intVal.plus(D);
            intVal = intVal.plus(E);

            EncMatrix EncA = new EncMatrix(A, pk);
            EncMatrix EncB = new EncMatrix(B, pk);
            EncMatrix EncC = new EncMatrix(C, pk);
            EncMatrix EncD = new EncMatrix(D, pk);
            EncMatrix EncE = new EncMatrix(E, pk);

            List<EncMatrix> encList = new LinkedList<>();
            encList.add(EncA);
            encList.add(EncB);
            encList.add(EncC);
            encList.add(EncD);
            encList.add(EncE);
            Utils utils = new Utils();
            EncMatrix enc = utils.addEncMatrices(encList);

            IntMatrix dec = keyRing.decryptMatrix(enc);

            System.out.println(intVal);
            System.out.println(dec);

            assertTrue(intVal.eq(dec));



        }
    }



    @org.junit.jupiter.api.Test
    void upperToelpitzTest() throws Exception {
        for (int i = 0; i < 10; i++) {

            System.out.println(obliviousAlgebra.upperToeplitz(5));


        }
    }

    @org.junit.jupiter.api.Test
    void lowerToelpitzTest() throws Exception {
        for (int i = 0; i < 10; i++) {

            System.out.println(obliviousAlgebra.lowerToeplitz(5));


        }
    }

    @org.junit.jupiter.api.Test
    void diagonalMatrixTest() throws Exception {
        for (int i = 0; i < 10; i++) {

            System.out.println(obliviousAlgebra.diagonalMatrix(5));


        }
    }
}

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
    ObliviousAlgebra obliviousAlgebra = new ObliviousAlgebra(new PublicKey(), new PrivateKeyShare(), 1);
    ObliviousAlgebraCoordinator coordinator = new ObliviousAlgebraCoordinator(10, 5);
    PublicKey pk = coordinator.publicKey;
    PrivateKeyRing keyRing = coordinator.privateKeyRing;


    public ObliviousSupportTests() throws Exception {
    }


    @org.junit.jupiter.api.Test
    void secMultTest1() throws Exception {
        int x = 5;
        for (int i = 0; i < 10; i++) {



            IntMatrix A = IntMatrix.random(x,x);

            IntMatrix B = IntMatrix.random(x,x);

            EncMatrix AEnc = new EncMatrix(A, pk);
            EncMatrix BEnc = new EncMatrix(B, pk);

            System.out.println("got to multiplication of normal matrices");
            System.out.println(A.times(B));
            System.out.println("finished normal multiplication");
            List<EncMatrix> multResult = coordinator.secMult(AEnc, BEnc, pk);

            for (int j = 0; j <multResult.size(); j++) {
                System.out.println("Party " + j + " returned " + coordinator.privateKeyRing.decryptMatrix(multResult.get(j)));
            }
            System.out.println("Plain A.times B is  " + A.times(B));

            IntMatrix intResult = coordinator.privateKeyRing.decryptMatrix(multResult.get(0));

            assertTrue(A.times(B).eq(intResult));



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

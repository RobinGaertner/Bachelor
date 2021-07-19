package com.company.tests;

import com.company.*;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class secInvTest {

    //TODO: change seed
    int numParties = 5;
    Containter con = KeyGen.keyGen(16, 3, numParties, numParties);
    ObliviousAlgebraCoordinator coordinator = new ObliviousAlgebraCoordinator(numParties, con.getPublicKey(), con.getPrivateKeyRing());
    ObliviousAlgebra obliviousAlgebra = new ObliviousAlgebra(new PublicKey(), new PrivateKeyShare(),coordinator, 1);
    PublicKey pk = coordinator.publicKey;
    PrivateKeyRing keyRing = coordinator.privateKeyRing;
    Utils utils = new Utils();

    public secInvTest() throws Exception {
    }


    @org.junit.jupiter.api.Test
    void singularTest() {
        int x = 5;
        for (int i = 0; i < 1; i++) {

            BigInteger[][] data = new BigInteger[2][2];

            data[0][0] = BigInteger.valueOf(6);
            data[0][1] = BigInteger.valueOf(2);
            data[1][0] = BigInteger.valueOf(5);
            data[1][1] = BigInteger.valueOf(3);

            System.out.println("IsSingular returns: " + utils.isSingular(new IntMatrix(data)));

            data[0][0] = BigInteger.valueOf(1);
            data[0][1] = BigInteger.valueOf(0);
            data[1][0] = BigInteger.valueOf(0);
            data[1][1] = BigInteger.valueOf(0);

            System.out.println("IsSingular returns: " + utils.isSingular(new IntMatrix(data)));

        }
    }


    @org.junit.jupiter.api.Test
    void inverseTest() throws Exception {
        int x = 5;
        for (int i = 0; i < 1; i++) {

            BigInteger[][] data = new BigInteger[2][2];

            data[0][0] = BigInteger.valueOf(0);
            data[0][1] = BigInteger.valueOf(-1);
            data[1][0] = BigInteger.valueOf(-1);
            data[1][1] = BigInteger.valueOf(3);

            System.out.println("Inverse returns: " + new IntMatrix(data).inverse());

        }
    }





    @org.junit.jupiter.api.Test
    void secInvTest1() throws Exception {
        int x = 5;
        for (int i = 0; i < 3; i++) {




            IntMatrix A = IntMatrix.random(x,x);
            System.out.println("Matrix a is: " + A);

            IntMatrix B = IntMatrix.random(x,x);

            EncMatrix AEnc = new EncMatrix(A, pk);

            EncMatrix AInv = coordinator.secInv(AEnc);
            System.out.println("after inv");
            System.out.println("inverted is: " + keyRing.decryptMatrix(AInv));

            IntMatrix AresInner = keyRing.decryptMatrix(coordinator.secMult(AEnc, AInv, pk));
            System.out.println("after decrypt");
            IntMatrix AresOuter = A.times(keyRing.decryptMatrix(AInv));
            System.out.println("after second decrypt");

            System.out.println("inner = " + AresInner);
            System.out.println("outer" + AresInner);

            assertEquals(utils.identityMatrixTimes(x, 1), AresInner);
            assertEquals( utils.identityMatrixTimes(x, 1),AresOuter);


        }
    }


    @org.junit.jupiter.api.Test
    void secInvTestSpecial() throws Exception {
        int x = 5;
        for (int i = 0; i < 3; i++) {




            BigInteger[][] data = new BigInteger[2][2];
            data[0][0] = BigInteger.ZERO;
            data[0][1] = BigInteger.valueOf(-1);
            data[1][0] = BigInteger.valueOf(-1);
            data[1][1] = BigInteger.valueOf(-3);

            IntMatrix A = new IntMatrix(data);
            System.out.println("Matrix a is: " + A);

            EncMatrix AEnc = new EncMatrix(A, pk);

            EncMatrix AInv = coordinator.secInv(AEnc);
            System.out.println("after inv");
            System.out.println("inverted is: " + keyRing.decryptMatrix(AInv));

            IntMatrix AresInner = keyRing.decryptMatrix(coordinator.secMult(AEnc, AInv, pk));
            System.out.println("after decrypt");
            IntMatrix AresOuter = A.times(keyRing.decryptMatrix(AInv));
            System.out.println("after second decrypt");

            System.out.println("inner = " + AresInner);
            System.out.println("outer" + AresInner);

            assertEquals(utils.identityMatrixTimes(x, 1), AresInner);
            assertEquals( utils.identityMatrixTimes(x, 1),AresOuter);


        }
    }
}

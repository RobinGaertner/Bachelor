package com.company;

import java.security.Key;
import java.util.LinkedList;
import java.util.List;

public class ObliviousAlgebraCoordinator {

    //N = number of parties
    int N;
    //t = matrixsize
    int t;
    List<ObliviousAlgebra> parties = new LinkedList<>();
    KeyGen keyGen = new KeyGen();
    PrivateKeyRing privateKeyRing;
    PublicKey publicKey;
    Utils utils = new Utils();

    public ObliviousAlgebraCoordinator(int numParties, int matrixSize) throws Exception {
        //setup
        //TODO: change to the right needed numbers
        Containter con = KeyGen.keyGen(16, 3, numParties, numParties);
        publicKey = con.getPublicKey();
        List<PrivateKeyShare> KeyShareList = con.getPrivateKeyRing().privateKeyShareList;
        //TODO: remove the privatekeyring
        privateKeyRing = con.getPrivateKeyRing();

        for (int i = 0; i < numParties; i++) {
            parties.add(new ObliviousAlgebra(publicKey, KeyShareList.get(i)));
        }
        t = matrixSize;
    }


    void secMult(EncMatrix Ml, EncMatrix Mr, PublicKey pK) throws Exception {

        List<EncMatrix> clList = new LinkedList<>();
        List<EncMatrix> crList = new LinkedList<>();
        List<EncMatrix> dlList = new LinkedList<>();
        List<EncMatrix> drList = new LinkedList<>();



        //lines 1-5
        for (int i = 0; i < parties.size(); i++) {
            List<EncMatrix> result1 = parties.get(i).secMultPart1(Ml, Mr, t, pK);
            clList.add(result1.get(0));
            crList.add(result1.get(1));
            dlList.add(result1.get(2));
            drList.add(result1.get(3));
        }

        //lines 6
        List<EncMatrix> cTildeList = new LinkedList<>();
        for (int i = 0; i < parties.size(); i++) {
            cTildeList.add(parties.get(i).secMultPart2(Ml, Mr, clList, crList));
        }
        //7

        EncMatrix MlPrimeEnc = Ml.plus(utils.addEncMatrices(clList));
        EncMatrix MrPrimeEnc = Mr.plus(utils.addEncMatrices(crList));

        IntMatrix MlPrime = privateKeyRing.decryptMatrix(MlPrimeEnc);
        IntMatrix MrPrime = privateKeyRing.decryptMatrix(MrPrimeEnc);

        //line 8-11
        for (int i = 0; i < parties.size(); i++) {
            parties.get(i).secMultpart3(dlList, drList, cTildeList, MlPrime, MrPrime);
        }


    }

}

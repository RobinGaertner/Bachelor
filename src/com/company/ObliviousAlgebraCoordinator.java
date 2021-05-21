package com.company;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class ObliviousAlgebraCoordinator {

    //N = number of parties
    int N;
    //t = matrixsize
    int t;
    List<ObliviousAlgebra> parties = new LinkedList<>();
    public PrivateKeyRing privateKeyRing;
    public PublicKey publicKey;
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
            parties.add(new ObliviousAlgebra(publicKey, KeyShareList.get(i), i));
        }
        t = matrixSize;
    }


    public EncMatrix secMult(EncMatrix Ml, EncMatrix Mr, PublicKey pK) throws Exception {

        List<EncMatrix> clList = new LinkedList<>();
        List<EncMatrix> crList = new LinkedList<>();
        List<EncMatrix> dlList = new LinkedList<>();
        List<EncMatrix> drList = new LinkedList<>();

        //lines 1-5
        for (ObliviousAlgebra party : parties) {
            List<EncMatrix> result1 = party.secMultPart1(Ml, Mr, t, pK);
            clList.add(result1.get(0));
            crList.add(result1.get(1));
            dlList.add(result1.get(2));
            drList.add(result1.get(3));
        }

        //lines 6
        List<EncMatrix> cTildeList = new LinkedList<>();
        for (ObliviousAlgebra party : parties) {
            cTildeList.add(party.secMultPart2(crList));
        }
        //7

        EncMatrix MlPrimeEnc = Ml.plus(utils.addEncMatrices(clList));
        EncMatrix MrPrimeEnc = Mr.plus(utils.addEncMatrices(crList));


        //This is the start of the test
        List<IntMatrix> MlPrimeParts = new LinkedList<>();
        for (int i = 0; i < parties.size(); i++) {
            MlPrimeParts.add(parties.get(i).getPartialDecryptionMatrix(MlPrimeEnc));
        }
        IntMatrix MlPrime = privateKeyRing.decryptMatrix(MlPrimeParts);

        List<IntMatrix> MrPrimeParts = new LinkedList<>();
        for (int i = 0; i < parties.size(); i++) {
            MrPrimeParts.add(parties.get(i).getPartialDecryptionMatrix(MrPrimeEnc));
        }
        IntMatrix MrPrime = privateKeyRing.decryptMatrix(MrPrimeParts);

        //this is the end of the Test

        //line 8-11
        List<EncMatrix> retVal = new LinkedList<>();

        for (int i = 0; i < parties.size(); i++) {
            retVal.add(parties.get(i).secMultpart3( MlPrime, MrPrime, dlList, drList, cTildeList));
        }

        //returns the result from the first party, but they are all equal, so it should not matter
        return retVal.get(0);

    }

}

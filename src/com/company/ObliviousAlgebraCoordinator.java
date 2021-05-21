package com.company;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ObliviousAlgebraCoordinator {

    //N = number of parties
    int N;
    //t = matrixsize
    int t;
    List<ObliviousAlgebra> parties = new LinkedList<>();
    public PrivateKeyRing privateKeyRing;
    public PublicKey publicKey;
    Utils utils = new Utils();
    Random rnd = new Random();

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




    int secRank(EncMatrix M) throws Exception {

        int t = M.M;
        if (M.M!=M.N){
            throw new Exception("not a square matrix");
        }

        List<EncMatrix> uList = new LinkedList<>();
        List<EncMatrix> lList = new LinkedList<>();
        List<EncMatrix> xList = new LinkedList<>();

        //1
        for (int i = 0; i < parties.size(); i++) {

            List<EncMatrix> tmp = parties.get(i).secRankPart1(t);
            uList.add(tmp.get(0));
            lList.add(tmp.get(1));
            xList.add(tmp.get(2));
        }

        //2


        //3
        //EncN = XUML
        //EncMatrix EncN = secMult(X, U, publicKey);
        //EncN = secMult(EncN, M, publicKey);
        //EncN = secMult(EncN, L, publicKey);


        //4
        BigInteger [][] data = new BigInteger[t][1];
        for (int i = 0; i < t; i++) {
            //TODO: check for boundary
            data[i][1] = BigInteger.valueOf(rnd.nextInt());
        }
        EncMatrix ui = new EncMatrix(new IntMatrix(data), publicKey);

        data = new BigInteger[t][1];
        for (int i = 0; i < t; i++) {
            //TODO: check for boundary
            data[i][1] = BigInteger.valueOf(rnd.nextInt());
        }
        EncMatrix vi = new EncMatrix(new IntMatrix(data), publicKey);


        //5

        List<EncMatrix> ujs = new LinkedList<>();
        List<EncMatrix> vjs = new LinkedList<>();

        //y is u in text
        //z is v in text
        EncMatrix y = utils.addEncMatrices(ujs);
        EncMatrix z = utils.addEncMatrices(vjs);


        //AAA is weird a in text
        List<EncryptedNumber> AAA = new LinkedList<>();

        for (int i = 0; i < 2*log2(t); i++) {
            //TODO: ask for this part
        }

        //6
        //TODO: missing


        return 1;
    }








}

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
            parties.add(new ObliviousAlgebra(publicKey, KeyShareList.get(i), this, i));
        }
        t = matrixSize;
    }



    public EncMatrix secInv(EncMatrix M) throws Exception {
        t = M.M;

        //line 1
        for (int i = 0; i < parties.size(); i++) {
            parties.get(i).secInvPart1(t);
        }

        //line 2
        EncMatrix MPrime = new EncMatrix(M);
        //System.out.println("Input M: " + privateKeyRing.decryptMatrix(M));
        //System.out.println(" MPrime before changing: " + privateKeyRing.decryptMatrix(MPrime));

        //line 3
        for (ObliviousAlgebra party : parties) {
            //line 4-5
            MPrime = party.secInvPart2(MPrime);
            //line 6
        }

        //only for Test:
        //System.out.println(" easy decryption: " + privateKeyRing.decryptMatrix(MPrime));


        //line 7
        //decryption without the secretkeys
        List<IntMatrix> MPrimeParts = new LinkedList<>();
        for (int i = 0; i < parties.size(); i++) {
            MPrimeParts.add(parties.get(i).getPartialDecryptionMatrix(MPrime));
        }
        IntMatrix decMPrime = privateKeyRing.decryptMatrix(MPrimeParts);
        //System.out.println("decMPrime is: " + decMPrime);


        //TODO: The error is here: inverse wants to give out floating numbers
        IntMatrix tmp = decMPrime.inverse();
        //System.out.println("DecPrime inverse" + tmp);


        EncMatrix NPrime = new EncMatrix(decMPrime.inverse(), publicKey);
        //System.out.println("start NPrime is: " + privateKeyRing.decryptMatrix(NPrime));


        //line 8
        for (int i = 0; i < parties.size(); i++) {
            //go from N to 1
            //line 9-10
            NPrime = parties.get(parties.size() - (i+1)).secInvPart3(NPrime);
            //System.out.println("new NPrime is: " + privateKeyRing.decryptMatrix(NPrime));
            //line 11
        }
        //line 12
        return NPrime;

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
        //decryption without the secretkeys
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




    EncryptedNumber secRank(EncMatrix M) throws Exception {
        //matrix needs to be square

        int t = M.M;
        if (M.M!=M.N){
            throw new Exception("not a square matrix");
        }

        List<EncMatrix> UList = new LinkedList<>();
        List<EncMatrix> LList = new LinkedList<>();
        List<EncMatrix> XList = new LinkedList<>();

        //1
        for (int i = 0; i < parties.size(); i++) {

            List<EncMatrix> tmp = parties.get(i).secRankPart1(t);
            UList.add(tmp.get(0));
            LList.add(tmp.get(1));
            XList.add(tmp.get(2));
        }

        //2
        for (ObliviousAlgebra party : parties) {
            party.secRankPart2(UList, LList, XList);
        }

        List<EncMatrix> uList = new LinkedList<>();
        List<EncMatrix> vList = new LinkedList<>();

        //line 3-4
        for (int i = 0; i < parties.size(); i++) {

            List<EncMatrix> tmp = parties.get(i).secRankPart3(M);
            uList.add(tmp.get(0));
            vList.add(tmp.get(1));
        }

        for (int i = 0; i < parties.size(); i++) {
            parties.get(i).secRankPart4(uList, vList, t);
        }


        //TODO: return right
        return publicKey.encrypt(BigInteger.ONE);
    }








}

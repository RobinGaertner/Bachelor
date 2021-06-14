package com.company;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CountingTestCoordinator {

    //N = number of parties
    int N;
    //t = matrixsize
    int t;
    List<CountingTest> parties = new LinkedList<>();
    public PrivateKeyRing privateKeyRing;
    public PublicKey publicKey;
    Utils utils = new Utils();
    Random rnd = new Random();
    ObliviousAlgebraCoordinator oCoordinator;



    public CountingTestCoordinator(int numParties, int treshold) throws Exception {
        //setup
        //TODO: change to the right needed numbers
        Containter con = KeyGen.keyGen(16, 3, numParties, numParties);
        publicKey = con.getPublicKey();
        List<PrivateKeyShare> KeyShareList = con.getPrivateKeyRing().privateKeyShareList;
        //TODO: remove the privatekeyring
        privateKeyRing = con.getPrivateKeyRing();

        oCoordinator = new ObliviousAlgebraCoordinator(numParties, t);



        for (int i = 0; i < numParties; i++) {
            parties.add(new CountingTest(treshold, KeyShareList.get(i), publicKey, this));
        }
        t = treshold;
    }

    void MPCT(List<BigInteger> inputAlphas, BigInteger setMod){

        List<List<EncryptedNumber>> encPointsList = new LinkedList<>();
        //line 1 already done in setup

        //line 2
        for (int i = 0; i < parties.size(); i++) {
            encPointsList.add(parties.get(i).MPCTpart1(inputAlphas, setMod));
        }

        //line 3



    }


    boolean SDT(List<EncryptedNumber[]> dList, List<BigInteger> alphaList, PrivateKeyShare privateKeyShare) throws Exception {

        //line 2
        //generate the system
        //TODO: init right
        EncMatrix Mr = new EncMatrix();
        EncMatrix y = new EncMatrix();

        EncryptedNumber part1 = oCoordinator.secRank(Mr);
        EncryptedNumber part2 = oCoordinator.secRank(Mr.plus(y));

        if(part1.sub(part2).decZero != 0) {
            return false;
        }

        //line 3


    }









}

package com.company;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CountingTest {

    int threshold;
    Polynomial poly = new Polynomial();
    List<BigInteger> evalPoints = new LinkedList<>();
    //TODO: secure random?
    Random rnd = new Random();
    List<EncryptedNumber> cList = new LinkedList<>();
    PrivateKeyShare pks;
    PublicKey publicKey;
    List<BigInteger> inputSet;
    CountingTestCoordinator coordinator;


    public CountingTest(int t, PrivateKeyShare privateKeyShare, PublicKey pk, CountingTestCoordinator coord){
        threshold = t;
        pks = privateKeyShare;
        publicKey = pk;
        //TODO: set the Set that we want to test
        for (int i = 0; i < t; i++) {
            inputSet.add(BigInteger.valueOf(rnd.nextInt()));
        }
        coordinator = coord;
    }

    //line 2
    List<EncryptedNumber> MPCTpart1(List<BigInteger> sharedAlphas, BigInteger setMod){

        //here we need the input set
        List<BigInteger> negativeInputSet = new LinkedList<>(inputSet);
        for (int i = 0; i < negativeInputSet.size(); i++) {
            negativeInputSet.set(i, BigInteger.ZERO.subtract(negativeInputSet.get(i)));
        }

        poly.init(multiplyOut(negativeInputSet), setMod);

        //empty the last thing out
        evalPoints.clear();

        //call the polynomial at the values 1 to 4t+3
        for (int i = 0; i < (4*threshold + 2); i++) {
            evalPoints.add(poly.call(sharedAlphas.get(i)));
        }

        //empty out the cList
        cList.clear();

        //get r
        //TODO: rnd bound?
        BigInteger r = BigInteger.valueOf(rnd.nextInt());

        //encrypt the points
        for (int i = 0; i < evalPoints.size(); i++) {
            cList.add(publicKey.encrypt(r.multiply(evalPoints.get(i))));
        }

        return cList;
    }

    //line 3-4
    boolean MPCTpart2(List<List<EncryptedNumber>> cList, List<BigInteger> inputAlphas) throws Exception {

        //line 3
        List<EncryptedNumber []> dList = new LinkedList<>();

        for (int j = 0; j < cList.size(); j++) {
            EncryptedNumber part1 = publicKey.encrypt(BigInteger.ZERO);
            for (int i = 0; i < cList.get(0).size(); i++) {
                part1 = part1.add(cList.get(j).get(i));
            }
            EncryptedNumber part2 = publicKey.encrypt(poly.call(inputAlphas.get(j)));

            EncryptedNumber[] newD = new EncryptedNumber[3];

            newD[1] = part1;
            newD[2] = part2;
            dList.add(newD);
        }

        //line 4
        //return !coordinator.SDTtemp();
        return true;
    }



    List<BigInteger> multiplyOut (List<BigInteger> inputList){
        List<BigInteger> resList = new LinkedList<>();
        for (int i = 0; i < inputList.size(); i++) {
            List<BigInteger> tmpList = new LinkedList<>();
            tmpList.add(BigInteger.ONE);
            for (int j = 0; j < resList.size(); j++) {
                BigInteger tmpInt = resList.remove(j);
                tmpInt = tmpInt.multiply(inputList.get(i)).add(resList.get(j+1));
                tmpList.add(tmpInt);
            }
            resList = new LinkedList<>(tmpList);
        }
        return resList;
    }
}

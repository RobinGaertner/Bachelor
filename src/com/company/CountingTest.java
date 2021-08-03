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
    PublicKey publicKey;
    List<BigInteger> inputSet;
    CountingTestCoordinator coordinator;


    public CountingTest(int t, PublicKey pk, CountingTestCoordinator coord) {
        threshold = t;
        publicKey = pk;
        coordinator = coord;
    }

    public void setInputSet(List<BigInteger> input) {
        inputSet = input;
    }

    //line 2
    List<EncryptedNumber> MPCTpart1(List<BigInteger> sharedAlphas, BigInteger setMod) {

        //here we need the input set
        List<BigInteger> negativeInputSet = new LinkedList<>(inputSet);
        for (int i = 0; i < negativeInputSet.size(); i++) {
            negativeInputSet.set(i, BigInteger.ZERO.subtract(negativeInputSet.get(i)));
        }

        poly.init(multiplyOut(negativeInputSet), setMod);


        //call the polynomial at the values 1 to 4t+3
        for (int i = 0; i < (4 * threshold + 2); i++) {
            evalPoints.add(poly.call(sharedAlphas.get(i)));
        }

        //empty out the cList
        cList.clear();

        //get r
        //TODO: rnd bound?
        BigInteger r = BigInteger.valueOf(rnd.nextInt()).mod(setMod);

        //encrypt the points
        for (BigInteger evalPoint : evalPoints) {
            cList.add(publicKey.encrypt(r.multiply(evalPoint)));
        }

        return cList;
    }

    //line 3-4
    boolean MPCTpart2(List<List<EncryptedNumber>> cList, List<BigInteger> inputAlphas, int threshold) throws Exception {

        FModular.FModularFactory factory = FModular.FACTORY(coordinator.FModularModulo);
        //line 3
        List<EncryptedNumber> dList = new LinkedList<>();
        //System.out.println("cList: " + cList);

        for (int j = 0; j < cList.get(0).size(); j++) {

            EncryptedNumber part1 = publicKey.encrypt(BigInteger.ZERO);
            for (List<EncryptedNumber> encryptedNumbers : cList) {
                part1 = part1.add(encryptedNumbers.get(j));
            }
            FModular part2 = factory.get(poly.call(inputAlphas.get(j)));

            dList.add(part1.mul(part2.invert().value));
        }

        //line 4

        System.out.println("got to SDT");
        return coordinator.SDT(dList, inputAlphas, threshold);
    }


    public List<BigInteger> multiplyOut(List<BigInteger> inputList) {

        //erst spalte, dann zeile

        BigInteger[][] bigArray = new BigInteger[inputList.size() + 1][inputList.size() + 1];
        for (int i = 0; i < inputList.size(); i++) {
            bigArray[i][0] = BigInteger.ZERO;
        }
        bigArray[inputList.size()][0] = BigInteger.ONE;
        //i is Zeile
        for (int i = 0; i < inputList.size(); i++) {

            //j is spalte
            for (int j = 0; j < inputList.size() + 1; j++) {

                //every field is field above * input + field above-right
                if (j == inputList.size()) {
                    bigArray[j][i + 1] = bigArray[j][i].multiply(inputList.get(i));
                } else {
                    //now we are not in the rightest line
                    bigArray[j][i + 1] = bigArray[j][i].multiply(inputList.get(i)).add(bigArray[j + 1][i]);

                }
            }
        }


        List<BigInteger> resList = new LinkedList<>();
        for (int i = 0; i < inputList.size() + 1; i++) {
            resList.add(bigArray[inputList.size() - i][(inputList.size())]);
        }
        return resList;
    }
}

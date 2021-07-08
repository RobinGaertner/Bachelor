package com.company;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

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
    List<BigInteger> cList = new LinkedList<>();
    PrivateKeyShare pks;
    PublicKey publicKey;
    List<BigInteger> inputSet;
    CountingTestCoordinator coordinator;


    public CountingTest(int t, PublicKey pk, CountingTestCoordinator coord){
        threshold = t;
        publicKey = pk;
        coordinator = coord;
    }

    public void setInputSet(List<BigInteger> input){
            inputSet = input;
    }

    //line 2
    List<BigInteger> MPCTpart1(List<BigInteger> sharedAlphas, BigInteger setMod){

        //here we need the input set
        List<BigInteger> negativeInputSet = new LinkedList<>(inputSet);
        for (int i = 0; i < negativeInputSet.size(); i++) {
            negativeInputSet.set(i, BigInteger.ZERO.subtract(negativeInputSet.get(i)));
        }
        System.out.println("input set: " + inputSet);
        System.out.println("negative input set: "+ negativeInputSet);

        poly.init(multiplyOut(negativeInputSet), setMod);

        //PolynomialFunction polyTest = new PolynomialFunction();
        //polyTest.
        ////empty the last thing out
        //evalPoints.clear();

        //call the polynomial at the values 1 to 4t+3
        for (int i = 0; i < (4*threshold + 2); i++) {
            evalPoints.add(poly.call(sharedAlphas.get(i)));
        }

        //empty out the cList
        cList.clear();

        //get r
        //TODO: rnd bound?
        BigInteger r = BigInteger.valueOf(rnd.nextInt()).mod(setMod);

        //encrypt the points
        for (int i = 0; i < evalPoints.size(); i++) {
            cList.add((r.multiply(evalPoints.get(i))));
        }

        System.out.println("shared alphas: " + sharedAlphas);
        System.out.println("Ciphertext list: " + cList);
        return cList;
    }

    //line 3-4
    boolean MPCTpart2(List<List<BigInteger>> cList, List<BigInteger> inputAlphas, BigInteger modulo, int threshold) throws Exception {

        FModular.FModularFactory factory = FModular.FACTORY;
        //line 3
        List<FModular> dList = new LinkedList<>();
        System.out.println("cList: " + cList);

        for (int j = 0; j < cList.get(0).size(); j++) {

            System.out.println("j is: " + j);
            FModular part1 = factory.get(0);
            for (int i = 0; i < cList.size(); i++) {
                System.out.println("i is: " + i);
                part1 = part1.add(factory.get(cList.get(i).get(j)));
            }
            FModular part2 = factory.get(poly.call(inputAlphas.get(j)));
            System.out.println("poly wird gecallt auf: " + inputAlphas.get(j) + "und retrurnt: " + poly.call(inputAlphas.get(j) ));


            dList.add(part1.divide(part2));
        }





        //line 4
        return coordinator.SDT(dList, inputAlphas, threshold , modulo);
    }



    public List<BigInteger> multiplyOut (List<BigInteger> inputList){

        //erst spalte, dann zeile

        BigInteger[][] bigArray = new BigInteger[inputList.size()+1][inputList.size()+1];
        for (int i = 0; i < inputList.size(); i++) {
            bigArray[i][0] = BigInteger.ZERO;
        }
        bigArray[inputList.size()][0] = BigInteger.ONE;
        //i is Zeile
        for (int i = 0; i < inputList.size(); i++) {

            //j is spalte
            for (int j = 0; j < inputList.size()+1; j++) {

                //every field is field above * input + field abote-right
                if(j==inputList.size()){
                    bigArray[j][i+1] = bigArray[j][i].multiply(inputList.get(i));
                }else {
                    //now we are not in the rightest line
                    bigArray[j][i+1] = bigArray[j][i].multiply(inputList.get(i)).add(bigArray[j+1][i]);

                }
            }
        }



        List<BigInteger> resList = new LinkedList<>();
        for (int i = 0; i < inputList.size() + 1; i++) {
            resList.add(bigArray[inputList.size()-i][(inputList.size())]);
        }
        return resList;
    }
}

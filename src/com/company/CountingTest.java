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
    public List<BigInteger> inputSet;
    CountingTestCoordinator coordinator;
    //this is for SDT
    EncMatrix[] MrPlain = new EncMatrix[2];
    EncMatrix[] y = new EncMatrix[2];
    EncMatrix[] Mry = new EncMatrix[2];


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


    public void SDTPart1(int t, List<BigInteger> alphaList, List<EncryptedNumber> cList) throws Exception {

        //generate the system

        //do first half

        for (int k = 0; k < 2; k++) {

            EncryptedNumber[][] data = new EncryptedNumber[2*t+1][2*t+1];

            //dimensions: 2t+1 to right, 2t+1 down
            //y coordinate
            for (int j = 0; j < 2*t+1; j++) {
                EncryptedNumber tmp = publicKey.encrypt(BigInteger.ONE);
                //x coordinate
                for (int i = 0; i < t+1; i++) {

                    data[(t)-i][j] = tmp;
                    tmp = tmp.mul(alphaList.get(j+((2*t+1)*k)));

                }
            }
            //left half should be filled now


            //now to t+1 to 2t+1
            //y coordinate
            for (int j = 0; j < 2*t+1; j++) {

                EncryptedNumber tmp = publicKey.encrypt(BigInteger.ZERO).sub(cList.get(j+((2*t+1)*k)));
                //x coordinate
                for (int i = 0; i < t; i++) {

                    data[2*t-i][j] = tmp;
                    tmp = tmp.mul(alphaList.get(j+((2*t+1)*k)));
                }

            }

            //second half filled as well

            //going for y now
            MrPlain[k] = new EncMatrix(data, publicKey);


            EncryptedNumber[][] data2 = new EncryptedNumber[2*t+1][1];
            for (int i = 0; i < 2 * t + 1; i++) {
                data2[i][0] = cList.get(i+((2*t+1)*k)).mul(((alphaList.get(i+((2*t+1)*k))).pow(t)));
            }

            y[k] = new EncMatrix(data2, publicKey);

            //one half of r done
            //second coordinate of y is always 0
        }


        //combine them for Mr||y
        for (int k = 0; k < 2; k++) {
            EncryptedNumber[][] newData = new EncryptedNumber[2*t+2][2*t+1];

            for (int i = 0; i < 2 * t + 1; i++) {
                for (int j = 0; j < 2 * t + 1; j++) {
                    newData[j][i] = MrPlain[k].getData()[j][i];
                }
                newData[2*t+1][i] = y[k].getData()[i][0];
            }
            Mry[k] = new EncMatrix(newData, publicKey);
        }

        //combination done
    }

    public EncryptedNumber SDTPart2(int t, List<BigInteger> alphaList, List<EncryptedNumber> cv, List<EncryptedNumber> cw) throws Exception {

        //from 0-t
        List<EncryptedNumber> cv1 = new LinkedList<>();
        for (int i = 0; i < t+1; i++) {
            cv1.add(cv.get(t-i));
        }
        EncPolynomial polynomialCv1 = new EncPolynomial();
        polynomialCv1.init(cv1);

        //from t+1 to 2t+1
        List<EncryptedNumber> cv2 = new LinkedList<>();
        cv2.add(publicKey.encrypt(BigInteger.ONE));
        for (int i = 0; i < t; i++) {
            cv2.add(cv.get(2*t-i));
        }
        EncPolynomial polynomialCv2 = new EncPolynomial();
        polynomialCv2.init(cv2);



        //do this again for w
        //from 0-t
        //from 0-t
        List<EncryptedNumber> cw1 = new LinkedList<>();
        for (int i = 0; i < t+1; i++) {
            cw1.add(cw.get(t-i));
        }
        EncPolynomial polynomialCw1 = new EncPolynomial();
        polynomialCw1.init(cw1);

        //from t+1 to 2t+1
        List<EncryptedNumber> cw2 = new LinkedList<>();
        cw2.add(publicKey.encrypt(BigInteger.ONE));
        for (int i = 0; i < t; i++) {
            cw2.add(cw.get(2*t-i));
        }
        EncPolynomial polynomialCw2 = new EncPolynomial();
        polynomialCw2.init(cw2);



        //compute the endresult
        EncryptedNumber result11 = polynomialCv1.call(alphaList.get(0));
        EncryptedNumber result12 = polynomialCw2.call(alphaList.get(0));
        EncryptedNumber result1 = coordinator.multiplyEnc(result11, result12);

        EncryptedNumber result21 = polynomialCw1.call(alphaList.get(0));
        EncryptedNumber result22 = polynomialCv2.call(alphaList.get(0));
        EncryptedNumber result2 = coordinator.multiplyEnc(result21, result22);

        return result1.sub(result2);
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

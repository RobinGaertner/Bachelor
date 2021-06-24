package com.company;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

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
    DummyFunctions dummy = new DummyFunctions();



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


    boolean SDT(List<BigInteger> fList, List<BigInteger> alphaList, PrivateKeyShare privateKeyShare, int t) throws Exception {

        //line 1
        //generate the system

        //do first half
        //TODO: init right
        IntMatrix[] MrPlain = new IntMatrix[2];
        IntMatrix[] y = new IntMatrix[2];

        for (int k = 0; k < 2; k++) {

            BigInteger[][] data = new BigInteger[2*t+1][2*t+2];


            //dimensions: 2t+1 to right, 2t+1 down
            for (int j = 0; j < t+1; j++) {
                BigInteger tmp = BigInteger.ONE;
                //get the first row
                for (int i = 0; i < 2*t+1; i++) {

                    data[t-i][j] = tmp;
                    tmp = tmp.multiply(alphaList.get(j+((t+1)*k)));
                }
            }
            //left half should be filled now
            //now to t+1 to 2t+1

            for (int j = 0; j < t; j++) {
                BigInteger tmp = BigInteger.ZERO.subtract(fList.get(j+((t+1)*k)));
                //get the first row
                for (int i = 0; i < 2*t+1; i++) {

                    data[t-i][j] = tmp;
                    tmp = tmp.multiply(alphaList.get(j+((t+1)*k)));
                }

            }

            MrPlain[k] = new IntMatrix(data);


            BigInteger[][] data2 = new BigInteger[2*t+1][1];
            for (int i = 0; i < 2 * t + 1; i++) {
                data2[i][0] = fList.get(i+((t+1)*k)).multiply((alphaList.get(i+((t+1)*k)).pow(t)));
            }
            y[k] = new IntMatrix(data2);

            //one half of r done
            //second coordinate of y is always 0
        }


        IntMatrix[] Mry = new IntMatrix[2];
        //combine them for Mr||y
        for (int k = 0; k < 2; k++) {
            BigInteger[][] newData = new BigInteger[2*t+2][2*t+1];

            for (int i = 0; i < 2 * t + 2; i++) {
                for (int j = 0; j < 2 * t + 1; j++) {
                    newData[i][j] = MrPlain[k].getData()[i][j];
                }
                newData[i][2*t+1] = y[k].getData()[i][0];
            }
            Mry[k] = new IntMatrix(newData);
        }

        //combination done

        //line 2
        //TODO: change
        int part1 = dummy.rankOfMatrix(MrPlain[0]);
        int part2 = dummy.rankOfMatrix(Mry[0]);

        //if not zero, abort
        if(part1 - part2 != 0){
            return false;
        }

        //line 3
        //returning value is going to the right, so first coordinate is always 0
        List<Double> cv = dummy.OLS(MrPlain[0], y[0]);
        List<Double> cw = dummy.OLS(MrPlain[1], y[1]);


        List<Double> cv1 = cv.subList(0, t+1);
        List<Double> cv2 = cv.subList(t+1, (2*t)+1);

        List<Double> cw1 = cw.subList(0, t+1);
        List<Double> cw2 = cw.subList(t+1, (2*t)+1);

         //line 4
        //compute the polynomials

        //from 0-t
        double[] Cv1Array = new double[t+1];
        for (int i = 0; i < t+1; i++) {
            Cv1Array[i] = cv1.get(i);
        }
        PolynomialFunction Cv1 = new PolynomialFunction(Cv1Array);

        //from 1-t
        double[] Cv2Array = new double[t];
        for (int i = 0; i < t+1; i++) {
            Cv2Array[i] = cv2.get(i+t+1);
        }
        PolynomialFunction Cv2 = new PolynomialFunction(Cv2Array);


        //do this again for w
        //from 0-t

        double[] Cw1Array = new double[t+1];
        for (int i = 0; i < t+1; i++) {
            Cw1Array[i] = cw1.get(i);
        }
        PolynomialFunction Cw1 = new PolynomialFunction(Cw1Array);

        //from 1-t
        double[] Cw2Array = new double[t];
        for (int i = 0; i < t+1; i++) {
            Cw2Array[i] = cw2.get(i+t+1);
        }
        PolynomialFunction Cw2 = new PolynomialFunction(Cw2Array);



        //compute the endresult
        double Z = Cv1.value(alphaList.get(0).doubleValue()) * Cw2.value((alphaList.get(0).doubleValue()));
        Z -= Cw1.value(alphaList.get(0).doubleValue() * Cv2.value(alphaList.get(0).doubleValue()));


        //line 5
        return Z == 0;
    }


    boolean SDTtemp(Polynomial p1, Polynomial p2, int threshold) throws Exception {
        if(p1.degree()<threshold){
            if (p2.degree() == p1.degree()){
                System.out.println("degree is equal");
                return true;
            }
            System.out.println("degree is not equal");
        }
        return false;
    }









}

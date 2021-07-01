package com.company;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.pow;

public class CountingTestCoordinator {

    //N = number of parties
    //t = matrixsize
    int t;
    List<CountingTest> parties = new LinkedList<>();
    public PrivateKeyRing privateKeyRing;
    public PublicKey publicKey;
    ObliviousAlgebraCoordinator oCoordinator;
    DummyFunctions dummy = new DummyFunctions();
    Utils utils = new Utils();



    public CountingTestCoordinator(int numParties, int treshold) throws Exception {
        //setup
        //TODO: change to the right needed numbers
        Containter con = KeyGen.keyGen(16, 3, numParties, numParties);
        publicKey = con.getPublicKey();
        //TODO: remove the privatekeyring
        privateKeyRing = con.getPrivateKeyRing();

        oCoordinator = new ObliviousAlgebraCoordinator(numParties, t);


        //TODO: maybe add this again
        /*
        for (int i = 0; i < numParties; i++) {
            parties.add(new CountingTest(treshold, KeyShareList.get(i), publicKey, this));
        }
        */
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


    public boolean SDT(List<Double> fList, List<Double> alphaList, int t){

        //line 1
        //generate the system

        //do first half
        //TODO: init right
        RealMatrix[] MrPlain = new RealMatrix[2];
        RealVector[] y = new RealVector[2];


        System.out.println("treshold is: " + t);
        System.out.println("fList: " + fList);
        System.out.println("alphaList: " + alphaList);

        for (int k = 0; k < 2; k++) {

            double[][] data = new double[2*t+1][2*t+1];

            System.out.println("matrix size: " + (2*t+1));

            //dimensions: 2t+1 to right, 2t+1 down
            //y coordinate
            for (int j = 0; j < 2*t+1; j++) {
                double tmp = 1;
                //x coordinate
                for (int i = 0; i < t+1; i++) {

                    System.out.println("Data " + tmp +" is written to " + ((t)-i) + " " + j);

                    data[(t)-i][j] = tmp;
                    tmp = tmp * (alphaList.get(j+((2*t+1)*k)));
                }
            }
            //left half should be filled now


            //now to t+1 to 2t+1
            //y coordinate
            for (int j = 0; j < 2*t+1; j++) {
                double tmp = 0 - (fList.get(j+((2*t+1)*k)));
                //x coordinate
                for (int i = 0; i < t; i++) {

                    System.out.println("Part 2 gets written " + tmp + " to " + (2*t-i) + " " + j);
                    data[2*t-i][j] = tmp;
                    tmp = tmp * alphaList.get(j+((2*t+1)*k));
                }

            }

            //second half filled as well

            //going for y now
            MrPlain[k] = new Array2DRowRealMatrix(data, false);
            System.out.println("Matrix right after finishing " + MrPlain[k]);


            double [] data2 = new double[2*t+1];
            for (int i = 0; i < 2 * t + 1; i++) {
                data2[i] = fList.get(i+((2*t+1)*k)) * pow((alphaList.get(i+((2*t+1)*k))),t);
            }
            y[k] = new ArrayRealVector(data2);
            System.out.println("y is: " + y[k]);

            //one half of r done
            //second coordinate of y is always 0
        }


        RealMatrix[] Mry = new RealMatrix[2];
        //combine them for Mr||y
        for (int k = 0; k < 2; k++) {
            double[][] newData = new double[2*t+2][2*t+1];

            for (int i = 0; i < 2 * t + 1; i++) {
                for (int j = 0; j < 2 * t + 1; j++) {
                    newData[j][i] = MrPlain[k].getData()[j][i];
                }
                newData[2*t+1][i] = y[k].getEntry(i);
            }
            Mry[k] = new Array2DRowRealMatrix(newData, false);
        }

        //combination done

        //line 2
        //fine now
        for (int i = 0; i < 2; i++) {
            int part1 = utils.rankOfMatrix(MrPlain[i]);
            int part2 = utils.rankOfMatrix(Mry[i]);

            System.out.println("MrY: " + Mry[i]);
            System.out.println("rank of matrix 1: " + part1);
            System.out.println("rank of matrix 2: " + part2);

            //if not zero, abort
            if(part1 - part2 != 0){
                return false;
            }
        }


        System.out.println("directly in front of OLS");


        //line 3
        //returning value is going to the right, so first coordinate is always 0
        List<Double> cv = dummy.OLS(MrPlain[0], y[0]);
        List<Double> cw = dummy.OLS(MrPlain[1], y[1]);


        System.out.println("CvSize = " + cv.size());
        System.out.println("2t+1: " + ((2*t)+1));
        System.out.println("t+1: " + (t+1));
        List<Double> cv1 = cv.subList(0, t+1);
        List<Double> cv2 = cv.subList(t+1, (2*t)+1);

        List<Double> cw1 = cw.subList(0, t+1);
        List<Double> cw2 = cw.subList(t+1, (2*t)+1);

         //line 4
        //compute the polynomials

        //from 0-t
        double[] Cv1Array = new double[t+1];
        for (int i = 0; i < t+1; i++) {
            Cv1Array[t-i] = cv1.get(i);
        }
        PolynomialFunction Cv1 = new PolynomialFunction(Cv1Array);

        //from 1-t
        double[] Cv2Array = new double[t+1];
        //first coefficient will be 1
        Cv2Array[t] = 1;
        for (int i = 0; i < t; i++) {
            //do from the back, because first part of solution needs the highest exponent
            Cv2Array[t-i-1] = cv2.get(i);
        }
        PolynomialFunction Cv2 = new PolynomialFunction(Cv2Array);


        //do this again for w
        //from 0-t

        double[] Cw1Array = new double[t+1];
        for (int i = 0; i < t+1; i++) {
            //do from the back, because first part of solution needs the highest exponent
            Cw1Array[t-i] = cw1.get(i);
        }
        PolynomialFunction Cw1 = new PolynomialFunction(Cw1Array);

        //from 1-t
        double[] Cw2Array = new double[t+1];
        Cw2Array[t] = 1;
        for (int i = 0; i < t; i++) {
            //do from the back, because first part of solution needs the highest exponent
            Cw2Array[t-i-1] = cw2.get(i);
        }
        PolynomialFunction Cw2 = new PolynomialFunction(Cw2Array);

        System.out.println("Poly v1: " + Cv1);
        System.out.println("Poly v2: " + Cv2);
        System.out.println("Poly w1: " + Cw1);
        System.out.println("Poly w2: " + Cw2);


        //compute the endresult
        //get on point t?
        PolynomialFunction PZ = Cv1.multiply(Cw2).subtract(Cw1.multiply(Cv2));


        System.out.println("retVal in PZ: " + PZ.value(alphaList.get(0))) ;


        double Z = Cv1.value(alphaList.get(0)) * Cw2.value((alphaList.get(0)));
        Z -= Cw1.value(alphaList.get(0) * Cv2.value(alphaList.get(0)));


        System.out.println("retVal of SDT would be: " + Z);
        //line 5
        return Z == 0;
    }


    boolean SDTtemp(Polynomial p1, Polynomial p2, int threshold){
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

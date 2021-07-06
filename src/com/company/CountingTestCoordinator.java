package com.company;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.jlinalg.LinSysSolver;
import org.jlinalg.Matrix;
import org.jlinalg.Vector;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

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


    public boolean SDT(List<FModular> fList, List<BigInteger> alphaList, int t, BigInteger modulus){

        //line 1
        //generate the system
        FModular.FModularFactory factory = FModular.FACTORY;

        //do first half
        //TODO: init right
        IntMatrix[] MrPlain = new IntMatrix[2];
        IntMatrix[] y = new IntMatrix[2];


        System.out.println("treshold is: " + t);
        System.out.println("fList: " + fList);
        System.out.println("alphaList: " + alphaList);

        for (int k = 0; k < 2; k++) {

            BigInteger[][] data = new BigInteger[2*t+1][2*t+1];

            System.out.println("matrix size: " + (2*t+1));

            //dimensions: 2t+1 to right, 2t+1 down
            //y coordinate
            for (int j = 0; j < 2*t+1; j++) {
                BigInteger tmp = BigInteger.ONE;
                //x coordinate
                for (int i = 0; i < t+1; i++) {

                    System.out.println("Data " + tmp +" is written to " + ((t)-i) + " " + j);

                    data[(t)-i][j] = tmp;
                    tmp = tmp.multiply(alphaList.get(j+((2*t+1)*k)));

                }
            }
            //left half should be filled now


            //now to t+1 to 2t+1
            //y coordinate
            for (int j = 0; j < 2*t+1; j++) {
                BigInteger tmp = BigInteger.ZERO.subtract(fList.get(j+((2*t+1)*k)).value);
                //x coordinate
                for (int i = 0; i < t; i++) {

                    System.out.println("Part 2 gets written " + tmp + " to " + (2*t-i) + " " + j);
                    data[2*t-i][j] = tmp;
                    tmp = tmp.multiply(alphaList.get(j+((2*t+1)*k)));
                }

            }

            //second half filled as well

            //going for y now
            MrPlain[k] = new IntMatrix(data);
            System.out.println("Matrix right after finishing " + MrPlain[k]);


            BigInteger[][] data2 = new BigInteger[2*t+1][1];
            for (int i = 0; i < 2 * t + 1; i++) {
                data2[i][0] = fList.get(i+((2*t+1)*k)).value.multiply((alphaList.get(i+((2*t+1)*k))).pow(t));
            }
            y[k] = new IntMatrix(data2);
            System.out.println("y is: " + y[k]);

            //one half of r done
            //second coordinate of y is always 0
        }


        IntMatrix[] Mry = new IntMatrix[2];
        //combine them for Mr||y
        for (int k = 0; k < 2; k++) {
            BigInteger[][] newData = new BigInteger[2*t+2][2*t+1];

            for (int i = 0; i < 2 * t + 1; i++) {
                for (int j = 0; j < 2 * t + 1; j++) {
                    newData[j][i] = MrPlain[k].getData()[j][i];
                }
                newData[2*t+1][i] = y[k].getData()[i][0];
            }
            Mry[k] = new IntMatrix(newData);
        }

        //combination done

        //line 2
        //fine now
        for (int i = 0; i < 2; i++) {
            int part1 = rankOfMatrix(MrPlain[i]);
            int part2 = rankOfMatrix(Mry[i]);

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
        Vector<FModular> cv = OLS(MrPlain[0], y[0]);
        Vector<FModular> cw = OLS(MrPlain[1], y[1]);


        //System.out.println("CvSize = " + cv.size());
        //System.out.println("2t+1: " + ((2*t)+1));
        //System.out.println("t+1: " + (t+1));

        //List<Double> cv2 = cv.subList(t+1, (2*t)+1);

        //List<Double> cw1 = cw.subList(0, t+1);
        //List<Double> cw2 = cw.subList(t+1, (2*t)+1);

         //line 4
        //compute the polynomials

        //from 0-t
        List<BigInteger> cv1 = new LinkedList<>();
        for (int i = 0; i < t+1; i++) {
            cv1.add(cv.getEntry(t-i+1).value);
        }
        Polynomial polynomialCv1 = new Polynomial();
        polynomialCv1.init(cv1, modulus);

        //from t+1 to 2t+1
        List<BigInteger> cv2 = new LinkedList<>();
        cv2.add(BigInteger.ONE);
        for (int i = 0; i < t+1; i++) {
            cv2.add(cv.getEntry(2*t-i+1).value);
        }
        Polynomial polynomialCv2 = new Polynomial();
        polynomialCv2.init(cv2, modulus);

        /*
        //from 1-t
        double[] Cv2Array = new double[t+1];
        //first coefficient will be 1
        Cv2Array[t] = 1;
        for (int i = 0; i < t; i++) {
            //do from the back, because first part of solution needs the highest exponent
            Cv2Array[t-i-1] = cv2.get(i);
        }
        PolynomialFunction Cv2 = new PolynomialFunction(Cv2Array);


         */


        //do this again for w
        //from 0-t
        //from 0-t
        List<BigInteger> cw1 = new LinkedList<>();
        for (int i = 0; i < t+1; i++) {
            cw1.add(cw.getEntry(t-i+1).value);
        }
        Polynomial polynomialCw1 = new Polynomial();
        polynomialCw1.init(cw1, modulus);

        //from t+1 to 2t+1
        List<BigInteger> cw2 = new LinkedList<>();
        cw2.add(BigInteger.ONE);
        for (int i = 0; i < t+1; i++) {
            cw2.add(cw.getEntry(2*t-i+1).value);
        }
        Polynomial polynomialCw2 = new Polynomial();
        polynomialCw2.init(cw2, modulus);




        /*
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

         */

        //System.out.println("Poly v1: " + Cv1);
        //System.out.println("Poly v2: " + Cv2);
        //System.out.println("Poly w1: " + Cw1);
        //System.out.println("Poly w2: " + Cw2);


        //compute the endresult
        //get on point t?
        FModular result1 = factory.get(polynomialCv1.call(alphaList.get(0))).multiply(factory.get(polynomialCw2.call(alphaList.get(0))));
        FModular result2 = factory.get(polynomialCw1.call(alphaList.get(0))).multiply(factory.get(polynomialCv2.call(alphaList.get(0))));

        System.out.println("res1 is: " + result1);
        System.out.println("res2 is: " + result2);

        FModular res = result1.subtract(result2);
        System.out.println("res is: " + res);

        return res.isZero();

    }


    private Vector<FModular> OLS (IntMatrix matrix, IntMatrix vector){

        BigInteger[][] Data = matrix.getData();
        FModular [][] fData = new FModular[matrix.getM()][matrix.getN()];
        FModular.FModularFactory factory = FModular.FACTORY;

        for (int i = 0; i < matrix.getM(); i++) {
            for (int j = 0; j < matrix.getN(); j++) {
                fData[i][j] = factory.get(Data[i][j]);
            }
        }
        Matrix<FModular> fMatrix = new Matrix<FModular>(fData);

        // create a vector

        BigInteger[][] vectorData = vector.getData();
        FModular[] fVectorData = new FModular[vector.getM()];
        for (int i = 0; i < vector.getM(); i++) {
                fVectorData[i] = factory.get(vectorData[i][0]);
        }
        Vector<FModular> fVector = new Vector<FModular>(fVectorData);

        // calculate the solution and print it
        Vector<FModular> solution = LinSysSolver.solve(fMatrix, fVector);
        return solution;
    }


    private int rankOfMatrix(IntMatrix intMatrix){

        BigInteger[][] Data = intMatrix.getData();
        FModular [][] fData = new FModular[intMatrix.getM()][intMatrix.getN()];
        FModular.FModularFactory factory = FModular.FACTORY;

        for (int i = 0; i < intMatrix.getM(); i++) {
            for (int j = 0; j < intMatrix.getN(); j++) {
                fData[i][j] = factory.get(Data[i][j]);
            }
        }

        Matrix<FModular> matrix = new Matrix<FModular>(fData);
        return matrix.rank();
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

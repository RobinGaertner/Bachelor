package com.company;

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
    public List<CountingTest> parties = new LinkedList<>();
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

        for (int i = 0; i < numParties; i++) {
            parties.add(new CountingTest(treshold, publicKey, this));
        }
        t = treshold;


    }

    public boolean MPCT(List<BigInteger> inputAlphas, BigInteger setMod) throws Exception {

        List<List<BigInteger>> encPointsList = new LinkedList<>();
        //line 1 already done in setup

        //line 2
        for (int i = 0; i < parties.size(); i++) {
            List<EncryptedNumber> tmpList = parties.get(i).MPCTpart1(inputAlphas, setMod);
            encPointsList.add(new LinkedList<>());
            for (int j = 0; j < tmpList.size(); j++) {
                encPointsList.get(i).add(privateKeyRing.decrypt(tmpList.get(j)));
            }
            //encPointsList.add(parties.get(i).MPCTpart1(inputAlphas, setMod));
        }
        System.out.println("encPointsList:" + encPointsList);
        System.out.println("aphasList: " + inputAlphas);
        //line 3
        return parties.get(0).MPCTpart2(encPointsList, inputAlphas, setMod, t);
    }


    public boolean SDT(List<EncryptedNumber> cList, List<BigInteger> alphaList, int t, BigInteger modulus){


        FModular.FModularFactory factory = FModular.FACTORY;
        List<FModular> fList = new LinkedList<>();
        for (int i = 0; i < cList.size(); i++) {
            fList.add(factory.get(privateKeyRing.decrypt(cList.get(i))));
        }




        //line 1
        //generate the system

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
                if(k==1){
                    System.out.println("y check first part: " + fList.get(i+((2*t+1)*k)).value);
                    System.out.println("y check second part: " + (alphaList.get(i+((2*t+1)*k))).pow(t));
                    System.out.println("y check multiplication: " + fList.get(i+((2*t+1)*k)).value.multiply((alphaList.get(i+((2*t+1)*k))).pow(t)));

                }
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



        //TESTING:

        FModular [][] fData = new FModular[MrPlain[0].getM()][MrPlain[0].getN()];
        for (int i = 0; i < MrPlain[0].getM(); i++) {
            for (int j = 0; j < MrPlain[0].getN(); j++) {
                fData[i][j] = factory.get(MrPlain[0].getData()[j][i]);
            }
        }
        Matrix<FModular> fMatrix = new Matrix<FModular>(fData);


        System.out.println("vector solution test: " + fMatrix.multiply(cv));



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
        for (int i = 0; i < t; i++) {
            cv2.add(cv.getEntry(2*t-i+1).value);
        }
        Polynomial polynomialCv2 = new Polynomial();
        polynomialCv2.init(cv2, modulus);



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
        for (int i = 0; i < t; i++) {
            cw2.add(cw.getEntry(2*t-i+1).value);
        }
        Polynomial polynomialCw2 = new Polynomial();
        polynomialCw2.init(cw2, modulus);


        System.out.println("coeffs of CV1: " + polynomialCv1.coeffs);
        System.out.println("coeffs of CV2: " + polynomialCv2.coeffs);
        System.out.println("coeffs of CW1: " + polynomialCw1.coeffs);
        System.out.println("coeffs of CW2: " + polynomialCw2.coeffs);


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
                fData[i][j] = factory.get(Data[j][i]);
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


        System.out.println("Ols gets: " + fMatrix);
        System.out.println("Ols vector plain: " + vector);
        System.out.println("and vector: " + fVector);
        // calculate the solution and print it
        Vector<FModular> solution = LinSysSolver.solve(fMatrix, fVector);
        System.out.println("OLS returns: " + solution);
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

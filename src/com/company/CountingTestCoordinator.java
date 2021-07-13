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

        List<List<EncryptedNumber>> encPointsList = new LinkedList<>();
        //line 1 already done in setup

        //line 2
        for (int i = 0; i < parties.size(); i++) {
            encPointsList.add(parties.get(i).MPCTpart1(inputAlphas, setMod));
        }
        //line 3
        return parties.get(0).MPCTpart2(encPointsList, inputAlphas, setMod, t);
    }


    public boolean SDT(List<EncryptedNumber> cList, List<BigInteger> alphaList, int t, BigInteger modulus) throws Exception {


        FModular.FModularFactory factory = FModular.FACTORY;
        List<FModular> fList = new LinkedList<>();
        for (int i = 0; i < cList.size(); i++) {
            fList.add(factory.get(privateKeyRing.decrypt(cList.get(i))));
        }




        //line 1
        //generate the system

        //do first half
        //TODO: init right
        EncMatrix[] MrPlain = new EncMatrix[2];
        EncMatrix[] y = new EncMatrix[2];


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


        EncMatrix[] Mry = new EncMatrix[2];
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

        //line 2
        //fine now
        for (int i = 0; i < 2; i++) {
            int part1 = rankOfMatrix(MrPlain[i]);
            int part2 = rankOfMatrix(Mry[i]);

            //if not zero, abort
            if(part1 - part2 != 0){
                return false;
            }
        }


        //line 3
        //returning value is going to the right, so first coordinate is always 0
        Vector<FModular> cv = OLS(MrPlain[0], y[0]);
        Vector<FModular> cw = OLS(MrPlain[1], y[1]);



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


    private Vector<FModular> OLS (EncMatrix matrix, EncMatrix vector){

        BigInteger[][] Data = privateKeyRing.decryptMatrix(matrix).getData();
        FModular [][] fData = new FModular[matrix.M][matrix.N];
        FModular.FModularFactory factory = FModular.FACTORY;

        for (int i = 0; i < matrix.M; i++) {
            for (int j = 0; j < matrix.N; j++) {
                fData[i][j] = factory.get(Data[j][i]);
            }
        }
        Matrix<FModular> fMatrix = new Matrix<>(fData);

        // create a vector

        BigInteger[][] vectorData = privateKeyRing.decryptMatrix(vector).getData();
        FModular[] fVectorData = new FModular[vector.M];
        for (int i = 0; i < vector.M; i++) {
            fVectorData[i] = factory.get(vectorData[i][0]);
        }
        Vector<FModular> fVector = new Vector<FModular>(fVectorData);


        // calculate the solution and print it
        Vector<FModular> solution = LinSysSolver.solve(fMatrix, fVector);
        return solution;
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

    private int rankOfMatrix(EncMatrix encMatrix){

        EncryptedNumber[][] Data = encMatrix.getData();
        FModular [][] fData = new FModular[encMatrix.M][encMatrix.N];
        FModular.FModularFactory factory = FModular.FACTORY;

        for (int i = 0; i < encMatrix.M; i++) {
            for (int j = 0; j < encMatrix.N; j++) {
                fData[i][j] = factory.get(privateKeyRing.decrypt(Data[i][j]));
            }
        }

        Matrix<FModular> matrix = new Matrix<FModular>(fData);
        return matrix.rank();
    }







}

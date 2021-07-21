package com.company;

import org.jlinalg.LinSysSolver;
import org.jlinalg.Matrix;
import org.jlinalg.Vector;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class CountingTestCoordinator {

    //N = number of parties
    int t;
    public List<CountingTest> parties = new LinkedList<>();
    public PrivateKeyRing privateKeyRing;
    public PublicKey publicKey;
    ObliviousAlgebraCoordinator oCoordinator;
    public int SDTCounter = 0;
    public int MPCTCounter = 0;
    public int OLSCounter =0;
    public BigInteger FModularModulo;



    public CountingTestCoordinator(int numParties, int treshold, BigInteger FModularMod) throws Exception {
        //setup
        //TODO: change to the right needed numbers
        Containter con = KeyGen.keyGen(16, 3, numParties, numParties);
        publicKey = con.getPublicKey();
        //TODO: remove the privatekeyring
        privateKeyRing = con.getPrivateKeyRing();

        oCoordinator = new ObliviousAlgebraCoordinator(numParties, con.getPublicKey(), con.getPrivateKeyRing());

        for (int i = 0; i < numParties; i++) {
            parties.add(new CountingTest(treshold, publicKey, this));
        }
        t = treshold;
        FModularModulo = FModularMod;


    }

    public void printStats(){
        System.out.println("Number that MPCT has been called: " + MPCTCounter);
        System.out.println("Number that SDT has been called: " + SDTCounter);
        System.out.println("Number that OLS has been called: " + OLSCounter);
        System.out.println("Decryptions in the relevant protocols: " + privateKeyRing.decryptCounter);
        System.out.println("Encryptions in the relevant protocols: " + publicKey.encryptionCounter);
    }

    public void resetStats(){
        MPCTCounter =0;
        SDTCounter =0;
        OLSCounter=0;
        privateKeyRing.decryptCounter=0;
        publicKey.encryptionCounter =0;
    }


    public boolean MPCT(List<BigInteger> inputAlphas, BigInteger setMod) throws Exception {
        MPCTCounter++;

        List<List<EncryptedNumber>> encPointsList = new LinkedList<>();
        //line 1 already done in setup

        //line 2
        for (int i = 0; i < parties.size(); i++) {
            encPointsList.add(parties.get(i).MPCTpart1(inputAlphas, setMod));
        }
        //line 3
        return parties.get(0).MPCTpart2(encPointsList, inputAlphas, t);
    }


    public boolean SDT(List<EncryptedNumber> cList, List<BigInteger> alphaList, int t) throws Exception {

        SDTCounter++;
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
            EncryptedNumber part1 = rankOfMatrix(MrPlain[i]);
            EncryptedNumber part2 = rankOfMatrix(Mry[i]);

            //if not zero, abort
            if(!decZero(part1.sub(part2))){
                return false;
            }
        }


        //line 3
        //returning value is going to the right, so first coordinate is always 0
        List<EncryptedNumber> cv = OLS(MrPlain[0], y[0]);
        List<EncryptedNumber> cw = OLS(MrPlain[1], y[1]);



         //line 4
        //compute the polynomials

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


        //System.out.println("coeffs of CV1: " + polynomialCv1.coeffs);
        //System.out.println("coeffs of CV2: " + polynomialCv2.coeffs);
        //System.out.println("coeffs of CW1: " + polynomialCw1.coeffs);
        //System.out.println("coeffs of CW2: " + polynomialCw2.coeffs);


        //compute the endresult
        //get on point t?
        EncryptedNumber result11 = polynomialCv1.call(alphaList.get(0));
        EncryptedNumber result12 = polynomialCw2.call(alphaList.get(0));
        EncryptedNumber result1 = multiplyEnc(result11, result12);

        EncryptedNumber result21 = polynomialCw1.call(alphaList.get(0));
        EncryptedNumber result22 = polynomialCv2.call(alphaList.get(0));
        EncryptedNumber result2 = multiplyEnc(result21, result22);

        return decZero(result1.sub(result2));

    }


    private Boolean decZero(EncryptedNumber input){

        BigInteger plain = privateKeyRing.decrypt(input);

        FModular.FModularFactory factory = FModular.FACTORY(FModularModulo);
        return plain.mod(factory.get(0).modulus).equals(BigInteger.ZERO);

    }





    private List<EncryptedNumber> OLS (EncMatrix matrix, EncMatrix vector){
        OLSCounter++;

        BigInteger[][] Data = privateKeyRing.decryptMatrix(matrix).getData();
        FModular [][] fData = new FModular[matrix.M][matrix.N];
        FModular.FModularFactory factory = FModular.FACTORY(FModularModulo);

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
        Vector<FModular> fVector = new Vector<>(fVectorData);


        // calculate the solution and print it
        Vector<FModular> res = LinSysSolver.solve(fMatrix, fVector);

        List<EncryptedNumber> resList = new LinkedList<>();

        for (int i = 0; i < fVector.length(); i++) {
            resList.add(publicKey.encrypt(res.getEntry(i+1).getValue()));
            //this will not be in the final product, so the encryption does not count for the analysis
            publicKey.encryptionCounter--;
        }

        return resList;
    }


    public EncryptedNumber multiplyEnc(EncryptedNumber a, EncryptedNumber b) throws Exception {
        EncryptedNumber[][] data1 = new EncryptedNumber[1][1];
        EncryptedNumber[][] data2 = new EncryptedNumber[1][1];

        data1[0][0] = a;
        data2[0][0] = b;

        EncMatrix AEnc = new EncMatrix(data1, publicKey);
        EncMatrix BEnc = new EncMatrix(data2, publicKey);

        EncMatrix resMatrix = oCoordinator.secMult(AEnc, BEnc, AEnc.getPublicKey());

        return resMatrix.getData()[0][0];
    }




    private Vector<FModular> OLS (IntMatrix matrix, IntMatrix vector){

        BigInteger[][] Data = matrix.getData();
        FModular [][] fData = new FModular[matrix.getM()][matrix.getN()];
        FModular.FModularFactory factory = FModular.FACTORY(FModularModulo);

        for (int i = 0; i < matrix.getM(); i++) {
            for (int j = 0; j < matrix.getN(); j++) {
                fData[i][j] = factory.get(Data[j][i]);
            }
        }
        Matrix<FModular> fMatrix = new Matrix<>(fData);

        // create a vector

        BigInteger[][] vectorData = vector.getData();
        FModular[] fVectorData = new FModular[vector.getM()];
        for (int i = 0; i < vector.getM(); i++) {
                fVectorData[i] = factory.get(vectorData[i][0]);
        }
        Vector<FModular> fVector = new Vector<>(fVectorData);


        // calculate the solution and print it
        return LinSysSolver.solve(fMatrix, fVector);
    }


    private int rankOfMatrix(IntMatrix intMatrix){

        BigInteger[][] Data = intMatrix.getData();
        FModular [][] fData = new FModular[intMatrix.getM()][intMatrix.getN()];
        FModular.FModularFactory factory = FModular.FACTORY(FModularModulo);

        for (int i = 0; i < intMatrix.getM(); i++) {
            for (int j = 0; j < intMatrix.getN(); j++) {
                fData[i][j] = factory.get(Data[i][j]);
            }
        }

        Matrix<FModular> matrix = new Matrix<>(fData);
        return matrix.rank();
    }

    private EncryptedNumber rankOfMatrix(EncMatrix encMatrix){

        EncryptedNumber[][] Data = encMatrix.getData();
        FModular [][] fData = new FModular[encMatrix.M][encMatrix.N];
        FModular.FModularFactory factory = FModular.FACTORY(FModularModulo);

        for (int i = 0; i < encMatrix.M; i++) {
            for (int j = 0; j < encMatrix.N; j++) {
                fData[i][j] = factory.get(privateKeyRing.decrypt(Data[i][j]));
            }
        }

        Matrix<FModular> matrix = new Matrix<>(fData);
        //this does not count towards the encryption analysis, because it is in a dummy protocol
        publicKey.encryptionCounter--;
        return publicKey.encrypt(BigInteger.valueOf(matrix.rank()));
    }







}

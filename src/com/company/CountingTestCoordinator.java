package com.company;

import org.jlinalg.LinSysSolver;
import org.jlinalg.Matrix;
import org.jlinalg.Vector;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
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
    public int secRankCounter =0;
    public BigInteger FModularModulo;


    public CountingTestCoordinator(int numParties, int treshold, BigInteger FModularMod) throws Exception {
        //setup
        //TODO: change to the right needed numbers
        Containter con = KeyGen.keyGen(32, 3, numParties, numParties);
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
        System.out.println("Number that secRank has been called: " + secRankCounter);
        System.out.println("Decryptions in the relevant protocols: " + privateKeyRing.decryptCounter);
        System.out.println("Encryptions in the relevant protocols: " + publicKey.encryptionCounter);
    }

    public void resetStats(){
        MPCTCounter =0;
        SDTCounter =0;
        OLSCounter=0;
        privateKeyRing.decryptCounter=0;
        publicKey.encryptionCounter =0;
        secRankCounter =0;
    }


    public boolean MPCT(List<BigInteger> inputAlphas, BigInteger setMod) throws Exception {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        System.out.println("MPCT start: " + ts);
        MPCTCounter++;

        List<List<EncryptedNumber>> encPointsList = new LinkedList<>();
        //line 1 already done in setup

        //line 2
        for (int i = 0; i < parties.size(); i++) {
            encPointsList.add(parties.get(i).MPCTpart1(inputAlphas, setMod));
        }
        //line 3
        date = new Date();
        ts = new Timestamp(date.getTime());
        System.out.println("MPCT end: " + ts);
        return parties.get(0).MPCTpart2(encPointsList, inputAlphas, t);
    }


    public boolean SDT(List<EncryptedNumber> cList, List<BigInteger> alphaList, int t) throws Exception {

        System.out.println("SDT inputs: " + cList.size() + " " + t);
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        System.out.println("SDT start: " + ts);
        SDTCounter++;

        //line 1
        EncMatrix[] MrPlain;
        EncMatrix[] y;
        EncMatrix[] Mry;

        //do computations in party1
        parties.get(0).SDTPart1(t, alphaList, cList);
        //get the results from party1
        MrPlain = parties.get(0).MrPlain;
        y = parties.get(0).y;
        Mry = parties.get(0).Mry;

        //line 2
        //check for the rank of the matrices
        for (int i = 0; i < 2; i++) {
            EncryptedNumber part1 = rankOfMatrix(MrPlain[i]);
            EncryptedNumber part2 = rankOfMatrix(Mry[i]);

            //if not zero, abort
            if(!decZero(part1.sub(part2))){
                System.out.println("rank of matrices different");
                return false;

            }
        }

        //line 3
        //returning value is going to the right, so first coordinate is always 0
        List<EncryptedNumber> cv = OLS(MrPlain[0], y[0]);
        List<EncryptedNumber> cw = OLS(MrPlain[1], y[1]);


         //line 4
        //compute the polynomials
        //in the parties
        EncryptedNumber partiesResult = null;
        for (int i = 0; i < parties.size(); i++) {
             partiesResult = parties.get(i).SDTPart2(t, alphaList, cv, cw);
        }

        //line 5
        date = new Date();
        ts = new Timestamp(date.getTime());
        System.out.println("SDT end: " + ts);
        return decZero(partiesResult);

    }


    public Boolean decZero(EncryptedNumber input){

        BigInteger plain = privateKeyRing.decrypt(input);
        return plain.mod(FModularModulo).equals(BigInteger.ZERO);

    }





    private List<EncryptedNumber> OLS (EncMatrix matrix, EncMatrix vector){

        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        System.out.println("OLS start: " + ts);
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


        date = new Date();
        ts = new Timestamp(date.getTime());
        System.out.println("OLS end: " + ts);
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


        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        System.out.println("getRank start: " + ts);
        secRankCounter++;

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
        date = new Date();
        ts = new Timestamp(date.getTime());
        //System.out.println("Rank is: " + matrix.rank());
        System.out.println("getRank end: " + ts);
        return publicKey.encrypt(BigInteger.valueOf(matrix.rank()));
    }







}

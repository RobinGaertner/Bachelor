package com.company;

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

        //line 2
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

        BigInteger part1 = oCoordinator.secRank(MrPlain[0]);
        BigInteger part2 = oCoordinator.secRank(Mry[0]);

        //if not zero, abort
        if(!part1.subtract(part2).equals(BigInteger.ZERO)){
            return false;
        }

        //line 3
         IntMatrix cv = OLS(MrPlain[0]);
         IntMatrix cw = OLS(MrPlain[1]);

            BigInteger[][] oldData = cv.getData();
            BigInteger[][] newData1 = new BigInteger[cv.getM()][cv.getN()/2];
            for (int j = 0; j < cv.getM(); j++) {
                for (int k = 0; k < cv.getN()/2; k++) {
                    newData1[j][k] = oldData[j][k];
                }
            }
        IntMatrix cv1 = new IntMatrix(newData1);

        BigInteger[][] newData2 = new BigInteger[cv.getM()][cv.getN()/2];
        for (int j = 0; j < cv.getM(); j++) {
            for (int k = 0; k < cv.getN()/2; k++) {
                newData2[j][k] = oldData[j][k+cv.getN()/2];
            }
        }
        IntMatrix cv2 = new IntMatrix(newData2);



        BigInteger[][] oldDataw = cw.getData();
        BigInteger[][] newData1w = new BigInteger[cw.getM()][cw.getN()/2];
        for (int j = 0; j < cw.getM(); j++) {
            for (int k = 0; k < cv.getN()/2; k++) {
                newData1w[j][k] = oldDataw[j][k];
            }
        }
        IntMatrix cw1 = new IntMatrix(newData1w);

        BigInteger[][] newData2w = new BigInteger[cw.getM()][cw.getN()/2];
        for (int j = 0; j < cw.getM(); j++) {
            for (int k = 0; k < cw.getN()/2; k++) {
                newData2w[j][k] = oldDataw[j][k+cw.getN()/2];
            }
        }
        IntMatrix cw2 = new IntMatrix(newData2w);



         //line 4
        //compute the polynomials

        Polynomial Cv1 =;
        Polynomial Cv2 =;
        Polynomial Cw1 =;
        Polynomial Cw2 =;


        //compute the endresult
        int z;



        return z==0;

    }



    public IntMatrix OLS(IntMatrix intMatrix){
        return new IntMatrix(2,2);
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

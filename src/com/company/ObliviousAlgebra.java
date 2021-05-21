package com.company;


import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ObliviousAlgebra {

    Utils utils = new Utils();
    Random rnd = new Random();
    PublicKey publicKey;
    PrivateKeyShare privateKeyShare;
    //a is the number this party has for this computation
    int a;
    private IntMatrix Rl;
    private IntMatrix Rr;

    public ObliviousAlgebra(PublicKey pK, PrivateKeyShare keyShare, int num){
        a = num;
        publicKey = pK;
        privateKeyShare = keyShare;
    }




    List<EncMatrix> secMultPart1(EncMatrix Ml, EncMatrix Mr, int size, PublicKey pK) throws Exception {

            //2.
            BigInteger[][] rndArray = new BigInteger[size][size];
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    //TODO: nextint?? what bound?
                    rndArray[x][y] = BigInteger.valueOf(rnd.nextInt());
                }
            }

            Rl = new IntMatrix(rndArray);

            BigInteger[][] rndArray2 = new BigInteger[size][size];
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    //TODO: nextint?? what bound?
                    rndArray[x][y] = BigInteger.valueOf(rnd.nextInt());
                }
            }
            Rr = new IntMatrix(rndArray2);

            //3.
            EncMatrix cl = new EncMatrix(Rl, pK);
            EncMatrix cr = new EncMatrix(Rr, pK);

            //TODO: do this right
            EncMatrix dr = Ml.times(Rr);
            EncMatrix dl = Rl.timesEnc(Mr);

            List<EncMatrix> res = new LinkedList<>();
            res.add(cl);
            res.add(cr);
            res.add(dl);
            res.add(dr);

            return res;
    }


    EncMatrix secMultPart2(List<EncMatrix> crList) throws Exception {
        //6
        //make list with right matrices
        List<EncMatrix> cTildeParts = new LinkedList<>();
        for (int i = 0; i < crList.size(); i++) {
            //TODO: remove the matrix, where i=j
            if( i != a ){
                cTildeParts.add(Rl.timesEnc(crList.get(i)));
            }
        }
        EncMatrix cTilde = utils.addEncMatrices(cTildeParts);

        //broadcast
        //broadcast cTilde
        return cTilde;
    }


    EncMatrix secMultpart3(IntMatrix MlPrime, IntMatrix MrPrime, List<EncMatrix> dlList, List<EncMatrix> drList, List<EncMatrix> cTildeList) throws Exception {
        //8
        //9
        EncMatrix dTilde = new EncMatrix(MlPrime.times(MrPrime), publicKey);
        //10
        EncMatrix e = ((dTilde.minus(utils.addEncMatrices(dlList))).minus(utils.addEncMatrices(drList))).minus(utils.addEncMatrices(cTildeList));
        //11
        return e;

    }

    BigInteger getPartialDecryption(EncryptedNumber e){
        return privateKeyShare.decrypt(e);
    }


    IntMatrix getPartialDecryptionMatrix(EncMatrix e){
        return privateKeyShare.decryptMatrix(e);
    }


    int secRank(EncMatrix M) throws Exception {

        int t = M.M;
        if (M.M!=t){
            throw new Exception("not a square matrix");
        }

        //1
        EncMatrix u = new EncMatrix(upperToeplitz(t), publicKey);
        EncMatrix l = new EncMatrix(lowerToeplitz(t), publicKey);
        EncMatrix x = new EncMatrix(diagonalMatrix(t), publicKey);


        //2
        List<EncMatrix> Xis = new LinkedList<>();
        List<EncMatrix> Lis = new LinkedList<>();
        List<EncMatrix> Uis = new LinkedList<>();


        //TODO: do this right
        EncMatrix X = utils.addEncMatrices(Xis);
        EncMatrix L = utils.addEncMatrices(Lis);
        EncMatrix U = utils.addEncMatrices(Uis);


        //3
        //EncN = XUML
        //EncMatrix EncN = secMult(X, U, publicKey);
        //EncN = secMult(EncN, M, publicKey);
        //EncN = secMult(EncN, L, publicKey);


        //4
        BigInteger [][] data = new BigInteger[t][1];
        for (int i = 0; i < t; i++) {
            //TODO: check for boundary
            data[i][1] = BigInteger.valueOf(rnd.nextInt());
        }
        EncMatrix ui = new EncMatrix(new IntMatrix(data), publicKey);

        data = new BigInteger[t][1];
        for (int i = 0; i < t; i++) {
            //TODO: check for boundary
            data[i][1] = BigInteger.valueOf(rnd.nextInt());
        }
        EncMatrix vi = new EncMatrix(new IntMatrix(data), publicKey);


        //5

        List<EncMatrix> ujs = new LinkedList<>();
        List<EncMatrix> vjs = new LinkedList<>();

        //y is u in text
        //z is v in text
        EncMatrix y = utils.addEncMatrices(ujs);
        EncMatrix z = utils.addEncMatrices(vjs);


        //AAA is weird a in text
        List<EncryptedNumber> AAA = new LinkedList<>();

        for (int i = 0; i < 2*log2(t); i++) {
            //TODO: ask for this part
        }

        //6
        //TODO: missing


        return 1;
    }

    public IntMatrix identityMatrixTimes(int size, int mul){
        BigInteger[][] data = new BigInteger[size][size];
        for (int i = 0; i < size; i++) {
            data[i][i] = BigInteger.valueOf(mul);
        }
        return new IntMatrix(data);
    }


    public IntMatrix lowerToeplitz(int t){

        BigInteger [][] data = new BigInteger [t][t];

        for (int i = 0; i < t; i++) {
            //after every diagonal get a new number
            //TODO: check for boundary
            int tmp = rnd.nextInt();
            for (int j = 0; j < t; j++) {
                //check if inbound
                if(1+j+i<t) {
                    //insert at the whole diagonal
                    data[1+j+i][0+j] = BigInteger.valueOf(tmp);
                }
            }
        }
        System.out.println(data);
        IntMatrix ret = new IntMatrix(data);
        return ret;

    }




    public IntMatrix upperToeplitz(int t){

        BigInteger [][] data = new BigInteger [t][t];

        for (int i = 0; i < t; i++) {
            //after every diagonal get a new number
            //TODO: check for boundary
            int tmp = rnd.nextInt();
            for (int j = 0; j < t; j++) {
                //check if inbound
                if(1+j+i<t) {
                    //insert at the whole diagonal
                    data[0+j][1+j+i] = BigInteger.valueOf(tmp);
                }
            }
        }
        System.out.println(data);
        IntMatrix ret = new IntMatrix(data);
        return ret;

    }

    public IntMatrix diagonalMatrix(int t){

        BigInteger [][] data = new BigInteger [t][t];

        for (int i = 0; i < t; i++) {
            //every place on the diagonal gets a random number
            //TODO: check for boundary
            data[i][i] = BigInteger.valueOf(rnd.nextInt());
        }
        System.out.println(data);
        IntMatrix ret = new IntMatrix(data);
        return ret;

    }

    //stackoverflow
    public static int log2(int n){
        if(n <= 0) throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros(n);
    }

}

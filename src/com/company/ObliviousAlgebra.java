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
    ObliviousAlgebraCoordinator coordinator;

    //stuff for secMult
    //a is the number this party has for this computation
    int a;
    private IntMatrix Rl;
    private IntMatrix Rr;

    //stuff for secRank
    EncMatrix U,L,X, EncN;

    //stuff for secInv
    private IntMatrix R;




    public ObliviousAlgebra(PublicKey pK, PrivateKeyShare keyShare,ObliviousAlgebraCoordinator oAC, int num){
        a = num;
        publicKey = pK;
        privateKeyShare = keyShare;
        coordinator = oAC;
    }




    List<EncMatrix> secMultPart1(EncMatrix Ml, EncMatrix Mr, PublicKey pK) throws Exception {

        int size = Ml.M;
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
            if( i != a ){
                cTildeParts.add(Rl.timesEnc(crList.get(i)));
            }
        }

        //broadcast
        //broadcast cTilde
        return utils.addEncMatrices(cTildeParts);
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



    public IntMatrix lowerToeplitz(int t){

        BigInteger [][] data = new BigInteger [t][t];

        for (int i = 0; i < t; i++) {
            //after every diagonal get a new number

            int tmp = rnd.nextInt();
            for (int j = 0; j < t; j++) {
                //check if inbound
                if(1+j+i<t) {
                    //insert at the whole diagonal
                    data[1+j+i][0+j] = BigInteger.valueOf(tmp);
                }
            }
        }
        //System.out.println(data);
        return new IntMatrix(data);

    }




    public IntMatrix upperToeplitz(int t){

        BigInteger [][] data = new BigInteger [t][t];

        for (int i = 0; i < t; i++) {
            //after every diagonal get a new number

            int tmp = rnd.nextInt();
            for (int j = 0; j < t; j++) {
                //check if inbound
                if(1+j+i<t) {
                    //insert at the whole diagonal
                    data[j][1+j+i] = BigInteger.valueOf(tmp);
                }
            }
        }
        //System.out.println(data);
        return new IntMatrix(data);

    }

    List<EncMatrix> secRankPart1(int t){
        //t = matrix size

        EncMatrix u = new EncMatrix(upperToeplitz(t), publicKey);
        EncMatrix l = new EncMatrix(lowerToeplitz(t), publicKey);
        EncMatrix x = new EncMatrix(diagonalMatrix(t), publicKey);

        List<EncMatrix> resList = new LinkedList<>();
        resList.add(u);
        resList.add(l);
        resList.add(x);

        return resList;
    }

    void secRankPart2(List<EncMatrix> uList, List<EncMatrix> lList, List<EncMatrix> xList) throws Exception {


        X = utils.addEncMatrices(xList);

        EncMatrix tmp = utils.addEncMatrices(uList);
        EncMatrix tmp2 = new EncMatrix(utils.identityMatrixTimes(uList.get(0).M, uList.size()-1), publicKey);
        U = tmp.minus(tmp2);

        tmp = utils.addEncMatrices(lList);
        tmp2 = new EncMatrix(utils.identityMatrixTimes(lList.get(0).M, lList.size()-1), publicKey);
        L = tmp.minus(tmp2);
    }

    List<EncMatrix> secRankPart3(EncMatrix M) throws Exception {
        //matrix has to be square
        int t = M.N;

        //line 3
        //EncMatrix EncN = XUML
        EncN = coordinator.secMult(X,U,publicKey);
        EncN = coordinator.secMult(EncN, M, publicKey);
        EncN = coordinator.secMult(EncN, L, publicKey);


        //line 4

        BigInteger [][] data = new BigInteger[t][1];
        for (int i = 0; i < t; i++) {
            data[i][1] = BigInteger.valueOf(rnd.nextInt());
        }
        EncMatrix u = new EncMatrix(new IntMatrix(data), publicKey);

        for (int i = 0; i < t; i++) {
            data[i][1] = BigInteger.valueOf(rnd.nextInt());
        }
        EncMatrix v = new EncMatrix(new IntMatrix(data), publicKey);

        List<EncMatrix> resList = new LinkedList<>();
        resList.add(u);
        resList.add(v);

        return resList;
    }

    void secRankPart4(List<EncMatrix> uList, List<EncMatrix> vList, int t) throws Exception {

        //t is again size of the matrices

        //5

        EncMatrix Encu = utils.addEncMatrices(uList);
        EncMatrix Encv = utils.addEncMatrices(vList);


        //AAA is weird a in text
        List<EncMatrix> AAA = new LinkedList<>();

        int limit = 2*log2(t);

        EncMatrix Npow = new EncMatrix(utils.identityMatrixTimes(t,1), publicKey);

        for (int i = 0; i < limit; i++) {

            EncMatrix tmp = coordinator.secMult(Encu, Npow, publicKey);
            tmp = coordinator.secMult(tmp, Encv, publicKey);
            //tmp = u*N^j*v
            AAA.add(tmp);
            Npow = coordinator.secMult(Npow, EncN, publicKey);
        }

        //6
        //TODO: missing


    }




    void secInvPart1(int t){
        //line 1

        BigInteger[][] rndArray = new BigInteger[t][t];
        for (int x = 0; x < t; x++) {
            for (int y = 0; y < t; y++) {
                //TODO: nextint?? what bound?
                rndArray[x][y] = BigInteger.valueOf(rnd.nextInt());
            }
        }

        IntMatrix res = new IntMatrix(rndArray);

        while(utils.isSingular(res)){
            //as long as the matrix is not singular
            //make a new random matrix

            for (int x = 0; x < t; x++) {
                for (int y = 0; y < t; y++) {
                    //TODO: nextint?? what bound?
                    rndArray[x][y] = BigInteger.valueOf(rnd.nextInt());
                }
            }

            res = new IntMatrix(rndArray);


        }

        R = res;
    }

    EncMatrix secInvPart2(EncMatrix MPrime) throws Exception {

        //line 4

        //line 5
        return R.timesEnc(MPrime);
    }


    public EncMatrix secInvPart3(EncMatrix NPrime) throws Exception {

        //line 9
        //IntMatrix RInv = R.inverse();

        //line 10
        return NPrime.times(R.inverse());
    }


    public IntMatrix diagonalMatrix(int t){

        BigInteger [][] data = new BigInteger [t][t];

        for (int i = 0; i < t; i++) {
            //every place on the diagonal gets a random number
            data[i][i] = BigInteger.valueOf(rnd.nextInt());
        }
        //System.out.println(data);
        return new IntMatrix(data);

    }

    //stackoverflow
    public static int log2(int n){
        if(n <= 0) throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros(n);
    }


}

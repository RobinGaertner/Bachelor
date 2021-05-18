package com.company;


import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ObliviousAlgebra {


    Random rnd = new Random();
    List parties = new LinkedList();
    PublicKey publicKey = new PublicKey();
    //N = number of parties
    int N = 10;
    // t = matrixsize
    //TODO: change
    int t = 5;


    EncMatrix secMult(EncMatrix Ml, EncMatrix Mr, PublicKey pK) throws Exception {

        int size = Ml.M;
        this.publicKey = pK;
        //1.
        for (int i = 0; i < parties.size(); i++) {

            //2.
            long rndArray [][] = new long[size][size];
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    //TODO: nextint?? what bound?
                    rndArray[x][y] = rnd.nextInt();
                }
            }

            IntMatrix Rl = new IntMatrix(rndArray);

            long rndArray2 [][] = new long[size][size];
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    //TODO: nextint?? what bound?
                    rndArray[x][y] = rnd.nextInt();
                }
            }
            IntMatrix Rr = new IntMatrix(rndArray2);

            //3.
            EncMatrix cl = new EncMatrix(Rl, pK);
            EncMatrix cr = new EncMatrix(Rr, pK);

            //TODO: do this right
            //EncMatrix dr = new EncMatrix(Ml.times(Rr), pK);
            //EncMatrix dl = new EncMatrix(Mr.times(Rl), pK);

            //4

        //5.
        }
        //6
        //TODO: make right sized matrix
        IntMatrix mt = new IntMatrix(2,2);
        //sum up
        for (int i = 0; i < N; i++) {
            //IntMatrix tmp = new IntMatrix();
            //mt.plus(tmp);
        }
        //encrypt

        //broadcast

        //7
        //sum up the cls
        EncMatrix sum = new EncMatrix(t, t, publicKey);

        for (int i = 0; i < N; i++) {

            //sum.plus();
        }

        EncMatrix Mlp = new EncMatrix(Ml, pK);
        Mlp.plus(sum);

        //TODO: same for right


        return sum;
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
        EncMatrix X = addEncMatrices(Xis);
        EncMatrix L = addEncMatrices(Lis);
        EncMatrix U = addEncMatrices(Uis);


        //3
        //EncN = XUML
        EncMatrix EncN = secMult(X, U, publicKey);
        EncN = secMult(EncN, M, publicKey);
        EncN = secMult(EncN, L, publicKey);


        //4
        long [][] data = new long[t][1];
        for (int i = 0; i < t; i++) {
            data[i][1] = rnd.nextInt();
        }
        EncMatrix ui = new EncMatrix(new IntMatrix(data), publicKey);

        data = new long[t][1];
        for (int i = 0; i < t; i++) {
            data[i][1] = rnd.nextInt();
        }
        EncMatrix vi = new EncMatrix(new IntMatrix(data), publicKey);


        //5

        List<EncMatrix> ujs = new LinkedList<>();
        List<EncMatrix> vjs = new LinkedList<>();

        //y is u in text
        //z is v in text
        EncMatrix y = addEncMatrices(ujs);
        EncMatrix z = addEncMatrices(vjs);


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
        long[][] data = new long[size][size];
        for (int i = 0; i < size; i++) {
            data[i][i] = mul;
        }
        return new IntMatrix(data);
    }


    public IntMatrix lowerToeplitz(int t){

        long [][] data = new long [t][t];

        for (int i = 0; i < t; i++) {
            //after every diagonal get a new number
            int tmp = rnd.nextInt();
            for (int j = 0; j < t; j++) {
                //check if inbound
                if(1+j+i<t) {
                    //insert at the whole diagonal
                    data[1+j+i][0+j] = tmp;
                }
            }
        }
        System.out.println(data);
        IntMatrix ret = new IntMatrix(data);
        return ret;

    }

    public EncMatrix addEncMatrices(List<EncMatrix> matrixList) throws Exception {

        EncMatrix start = matrixList.get(0);
        matrixList.remove(0);
        for ( EncMatrix matrix: matrixList) {
            start.plus(matrix);
        }
        return start;
    }


    public IntMatrix upperToeplitz(int t){

        long [][] data = new long [t][t];

        for (int i = 0; i < t; i++) {
            //after every diagonal get a new number
            int tmp = rnd.nextInt();
            for (int j = 0; j < t; j++) {
                //check if inbound
                if(1+j+i<t) {
                    //insert at the whole diagonal
                    data[0+j][1+j+i] = tmp;
                }
            }
        }
        System.out.println(data);
        IntMatrix ret = new IntMatrix(data);
        return ret;

    }

    public IntMatrix diagonalMatrix(int t){

        long [][] data = new long [t][t];

        for (int i = 0; i < t; i++) {
            //every place on the diagonal gets a random number
            data[i][i] = rnd.nextInt();
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

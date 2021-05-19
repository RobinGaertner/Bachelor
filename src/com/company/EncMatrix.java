package com.company;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class EncMatrix {

        public final int M;             // number of rows
        public final int N;             // number of columns

    public PublicKey getPublicKey() {
        return publicKey;
    }

    private PublicKey publicKey;

    public EncryptedNumber[][] getData() {
        return data;
    }

    private final EncryptedNumber[][] data;   // M-by-N array

        // create M-by-N matrix of 0's
        public EncMatrix(int M, int N, PublicKey pk) {
            this.M = M;
            this.N = N;
            data = new EncryptedNumber[M][N];
            publicKey = pk;
        }

    public EncMatrix(EncryptedNumber[][] data, PublicKey pK) {
        publicKey = pK;
        M = data.length;
        N = data[0].length;
        this.data = new EncryptedNumber[M][N];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                this.data[i][j] = data[i][j];
    }


        // create matrix based on 2d array
        public EncMatrix(IntMatrix plainMatrix, PublicKey pK) {
            long[][] inputData = plainMatrix.getData();
            M = plainMatrix.getM();
            N = plainMatrix.getN();
            this.data = new EncryptedNumber[M][N];
            this.publicKey = pK;
            for (int i = 0; i < M; i++) {
                List<BigInteger> row = new LinkedList<>();
                for (int x = 0; x < N; x++) {
                    row.add(BigInteger.valueOf( inputData[i][x]));
                }
                EncryptedNumber[] encryptedRow = publicKey.encryptList(row);
                this.data[i] = encryptedRow;
            }
        }


    public EncMatrix plus(EncMatrix B) throws Exception {
        if (B.M != M || B.N != N) throw new RuntimeException("Illegal matrix dimensions.");
        if(!B.publicKey.equals(publicKey)) throw new RuntimeException("Different publickeys");
        EncMatrix C = new EncMatrix(M, N, publicKey);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                C.data[i][j] = data[i][j].add(B.data[i][j]);
        return C;
    }


    //made by me
    public EncMatrix minus(EncMatrix B) throws Exception {
        if (B.M != M || B.N != N) throw new RuntimeException("Illegal matrix dimensions.");
        if(!B.publicKey.equals(publicKey)) throw new RuntimeException("Different publickeys");
        EncMatrix C = new EncMatrix(M, N, publicKey);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                C.data[i][j] = data[i][j].sub(B.data[i][j]);
        return C;
    }

    public EncMatrix times(IntMatrix B) throws Exception {
        EncMatrix A = this;
        if (A.N != B.getM()) throw new RuntimeException("Illegal matrix dimensions.");
        EncryptedNumber[][] data = new EncryptedNumber[A.M][B.getN()];
        //IntMatrix C = new IntMatrix(A.M, B.getN());
        for (int i = 0; i < A.M; i++)
            for (int j = 0; j < B.getN(); j++)
                for (int k = 0; k < A.N; k++) {
                    data[i][j] = data[i][j].add(A.data[i][k].mul(BigInteger.valueOf(B.getData()[k][j])));
                }
        EncMatrix C = new EncMatrix(data, publicKey);
        return C;
    }

    // copy constructor
        //private EncMatrix(com.company.EncMatrix A) { this(A.data); }


}
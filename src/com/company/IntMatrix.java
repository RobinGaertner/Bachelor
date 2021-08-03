package com.company;

import java.math.BigInteger;
import java.util.Random;

public class IntMatrix {

    private static final Utils utils = new Utils();
    private final int M;             // number of rows
    private final int N;             // number of columns
    private final BigInteger[][] data;   // M-by-N array

    //from https://introcs.cs.princeton.edu/java/95linear/Matrix.java.html


    public BigInteger[][] getData() {
        return data;
    }

    public int getM() {
        return M;
    }

    public int getN() {
        return N;
    }

    // create M-by-N matrix of 0's
    public IntMatrix(int M, int N) {
        this.M = M;
        this.N = N;
        BigInteger[][] tmp = new BigInteger[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                tmp[i][j] = BigInteger.ZERO;
            }
        }
        data = tmp;


    }

    // create matrix based on 2d array
    public IntMatrix(BigInteger[][] data) {
        M = data.length;
        N = data[0].length;
        this.data = new BigInteger[M][N];
        for (int i = 0; i < M; i++){
            for (int j = 0; j < N; j++){
                this.data[i][j] = data[i][j];
            }
        }
        for (int i = 0; i < M; i++){
            for (int j = 0; j < N; j++){
                if(this.data[i][j] == null){
                    //remove the nulls and input 0s
                    this.data[i][j] = BigInteger.ZERO;
                }
            }
        }
    }

    // copy constructor
    private IntMatrix(IntMatrix A) {
        this(A.data);
    }

    // create and return a random M-by-N matrix with values between 0 and 1
    public static IntMatrix random(int M, int N) {
        Random rnd = new Random();
        IntMatrix A = new IntMatrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                A.data[i][j] = BigInteger.valueOf(rnd.nextInt());
        return A;
    }

    // create and return the N-by-N identity matrix
    public static IntMatrix identity(int N) {
        IntMatrix I = new IntMatrix(N, N);
        for (int i = 0; i < N; i++)
            I.data[i][i] = BigInteger.ONE;
        return I;
    }

    // swap rows i and j
    private void swap(int i, int j) {
        BigInteger[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // create and return the transpose of the invoking matrix
    public IntMatrix transpose() {
        IntMatrix A = new IntMatrix(N, M);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                A.data[j][i] = this.data[i][j];
        return A;
    }

    // return C = A + B
    public IntMatrix plus(IntMatrix B) {
        IntMatrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        IntMatrix C = new IntMatrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                C.data[i][j] = A.data[i][j].add(B.data[i][j]);
        return C;
    }




    // return C = A - B
    public IntMatrix minus(IntMatrix B) {
        IntMatrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        IntMatrix C = new IntMatrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                C.data[i][j] = A.data[i][j].subtract(B.data[i][j]);
        return C;
    }

    // does A = B exactly?
    public boolean eq(IntMatrix B) {
        IntMatrix A = this;
        if (B.M != A.M || B.N != A.N) throw new RuntimeException("Illegal matrix dimensions.");
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
            {
                if (!A.data[i][j].equals(B.data[i][j])) return false;
            }
        return true;
    }

    // return C = A * B
    public IntMatrix times(IntMatrix B) {
        IntMatrix A = this;
        if (A.N != B.M) throw new RuntimeException("Illegal matrix dimensions.");
        IntMatrix C = new IntMatrix(A.M, B.N);
        for (int i = 0; i < C.M; i++)
            for (int j = 0; j < C.N; j++)
                for (int k = 0; k < A.N; k++)
                    C.data[i][j] =  C.data[i][j].add(A.data[i][k].multiply(B.data[k][j]) );
        return C;
    }



    //made by me
    // return C = A * B
    public EncMatrix timesEnc(EncMatrix B) throws Exception {
        IntMatrix A = this;
        if (A.N != B.M) throw new RuntimeException("Illegal matrix dimensions.");
        EncryptedNumber[][] data = new EncryptedNumber[A.M][B.N];
        //IntMatrix C = new IntMatrix(A.M, B.getN());
        for (int i = 0; i < A.M; i++)
            for (int j = 0; j < B.N; j++)
                for (int k = 0; k < A.N; k++) {
                    if(data[i][j] == null){
                        data[i][j] = B.getData()[k][j].mul(A.data[i][k]);
                    }else {
                        data[i][j] = data[i][j].add(B.getData()[k][j].mul(A.data[i][k]));
                    }
                }
        EncMatrix C = new EncMatrix(data, B.getPublicKey());
        return C;
    }




    //https://www.geeksforgeeks.org/adjoint-inverse-matrix/
    IntMatrix getCofactor(IntMatrix A, int p, int q, int n)
    {
        int i = 0, j = 0;

        BigInteger[][] tempData = new BigInteger[n][n];

        // Looping for each element of the matrix
        for (int row = 0; row < n; row++)
        {
            for (int col = 0; col < n; col++)
            {
                // Copying into temporary matrix only those element
                // which are not in given row and column
                if (row != p && col != q)
                {
                    tempData[i][j++] = A.data[row][col];

                    // Row is filled, so increase row index and
                    // reset col index
                    if (j == n - 1)
                    {
                        j = 0;
                        i++;
                    }
                }
            }
        }
        return new IntMatrix(tempData);
    }

    /* Recursive function for finding determinant of matrix.
    n is current dimension of A[][]. */
    BigInteger determinant(IntMatrix A, int n)
    {
        BigInteger D = BigInteger.ZERO; // Initialize result


        // Base case : if matrix contains single element
        if (n == 1)
            return A.data[0][0];

        BigInteger [][]temp; // To store cofactors

        int sign = 1; // To store sign multiplier

        // Iterate for each element of first row
        for (int f = 0; f < n; f++)
        {
            // Getting Cofactor of A[0][f]
            temp = getCofactor(A, 0, f, n).data;
            D = D.add(BigInteger.valueOf(sign).multiply( A.data[0][f]).multiply( determinant(new IntMatrix(temp), n - 1)));

            // terms are to be added with alternate sign
            sign = -sign;
        }

        return D;
    }

    // Function to get adjoint of A[N][N] in adj[N][N].
    public IntMatrix adjoint(IntMatrix A)
    {
        int t = A.getM();
        BigInteger[][] adjData = new BigInteger[t][t];
        if (t == 1)
        {
            adjData[0][0] = BigInteger.ONE;
            return new IntMatrix(adjData);
        }

        // temp is used to store cofactors of A[][]
        int sign;
        BigInteger [][]temp;

        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
            {
                // Get cofactor of A[i][j]
                temp = getCofactor(A, i, j, t).data;

                // sign of adj[j][i] positive if sum of row
                // and column indexes is even.
                sign = ((i + j) % 2 == 0)? 1: -1;

                // Interchanging rows and columns to get the
                // transpose of the cofactor matrix
                adjData[j][i] = BigInteger.valueOf(sign).multiply(determinant(new IntMatrix(temp), N-1));
            }
        }
        return new IntMatrix(adjData);
    }

    // Function to calculate and store inverse, returns false if
    // matrix is singular
    public IntMatrix inverse() throws Exception {
        IntMatrix A = this;
        // Find determinant of A[][]

        BigInteger det = determinant(A, A.getN());
        if(det.equals(BigInteger.ZERO)){
            throw new Exception("Matrix is singular");
        }

        BigInteger[][] inverse = new BigInteger[A.getM()][A.getN()];


        // Find adjoint
        IntMatrix adj = adjoint(A);

        // Find Inverse using formula "inverse(A) = adj(A)/det(A)"
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                inverse[i][j] = adj.data[i][j].divide(det);

        return new IntMatrix(inverse);
    }





    public String toString(){
        String res = "";
        for (int i = 0; i < N; i++) {
            String tmp = "";
            for (int j = 0; j < M; j++) {
                tmp += data[j][i] +" ";
            }
            res += tmp +  "\n";
        }
        return res;
    }


}

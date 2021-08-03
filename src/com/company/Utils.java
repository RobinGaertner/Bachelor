package com.company;

import org.apache.commons.math3.linear.RealMatrix;

import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;

public class Utils {

    Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public long[] xgcd(long a, long b) {
        //returns gcd(a,b), x, y
        //where ax + by = gcd(a,b)
        long[] retvals = {0, 0, 0};
        long[] aa = {1, 0};
        long[] bb = {0, 1};
        long q = 0;
        while (true) {
            q = a / b;
            a = a % b;
            aa[0] = aa[0] - q * aa[1];
            bb[0] = bb[0] - q * bb[1];
            if (a == 0) {
                retvals[0] = b;
                retvals[1] = aa[1];
                retvals[2] = bb[1];
                return retvals;
            }
            q = b / a;
            b = b % a;
            aa[1] = aa[1] - q * aa[0];
            bb[1] = bb[1] - q * bb[0];
            if (b == 0) {
                retvals[0] = a;
                retvals[1] = aa[0];
                retvals[2] = bb[0];
                return retvals;
            }
        }
    }

    public int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }


    private static class Triple {
        public final BigInteger d;
        public final BigInteger s;
        public final BigInteger t;
        private Triple(final BigInteger d, final BigInteger s, final BigInteger t) {
            this.d = d;
            this.s = s;
            this.t = t;
        }
        @Override
        public String toString() {
            return "Triple{" +
                    "d=" + d +
                    ", s=" + s +
                    ", t=" + t +
                    '}';
        }
    }

    public int invMod(int a, int mod) {
        //Finds the inverse of a modulo m ( b s.t. a*b = 1 (mod m))

        if (a < 0) {
            a = mod + a;
        }

        // a and m must be coprime to find an inverse
        if (gcd(a, mod) != 1) {
            //should hopefully not happen
            logger.warning("inv mod got bad numbers");
        }

        long inverse = xgcd(a, mod)[1];
        return (int) (inverse % mod);
    }


    //TODO: Make this nice
    public BigInteger floorDiv(final BigInteger x, final BigInteger y) {
        if (x.signum() * y.signum() >= 0) {
            return x.divide(y);
        }
        final BigInteger[] qr = x.divideAndRemainder(y);
        return qr[1].signum() == 0 ? qr[0] : qr[0].subtract(BigInteger.ONE);
    }


    public EncMatrix addEncMatrices(List<EncMatrix> matrixList) throws Exception {

        EncMatrix start = matrixList.get(0);

        for (int i = 1; i < matrixList.size(); i++) {
            start = start.plus(matrixList.get(i));
        }
        return start;
    }

    public EncryptedNumber addEncNumbers(List<EncryptedNumber> numberList) throws Exception {

        EncryptedNumber start = numberList.get(0);

        for (int i = 1; i < numberList.size(); i++) {
            start = start.add(numberList.get(i));
        }
        return start;
    }


    //from https://www.geeksforgeeks.org/program-check-matrix-singular-not/

    static void getCofactor(IntMatrix M, BigInteger[][] temp, int p,
                            int q, int n)
    {
        int i = 0, j = 0;

        // Looping for each element of the matrix
        for (int row = 0; row < n; row++)
        {
            for (int col = 0; col < n; col++)
            {

                // Copying into temporary matrix only
                // those element which are not in given
                // row and column
                if (row != p && col != q)
                {
                    temp[i][j++] = M.getData()[row][col];

                    // Row is filled, so increase row
                    // index and reset col index
                    if (j == n - 1)
                    {
                        j = 0;
                        i++;
                    }
                }
            }
        }
    }





    public Boolean isSingular(IntMatrix A){
        return isSingular(A, A.getM()).equals(BigInteger.ZERO);

    }

    //code from: https://www.geeksforgeeks.org/program-for-rank-of-matrix/
    // Java program to find rank of a matrix

        // function for exchanging two rows
        // of a matrix
        static void swap(double[][] mat,
                         int row1, int row2, int col)
        {
            for (int i = 0; i < col; i++)
            {
                double temp = mat[row1][i];
                mat[row1][i] = mat[row2][i];
                mat[row2][i] = temp;
            }
        }

        // function for finding rank of matrix
        public int rankOfMatrix(RealMatrix A)
        {
            int C = A.getColumnDimension();
            int R = A.getRowDimension();

            double [][] mat = A.getData();
            int rank = C;

            for (int row = 0; row < rank; row++)
            {

                // Before we visit current row
                // 'row', we make sure that
                // mat[row][0],....mat[row][row-1]
                // are 0.

                // Diagonal element is not zero
                if (mat[row][row] != 0.0)
                {
                    for (int col = 0; col < R; col++)
                    {
                        if (col != row)
                        {
                            // This makes all entries
                            // of current column
                            // as 0 except entry
                            // 'mat[row][row]'
                            double mult =
                                    mat[col][row] /
                                            mat[row][row];

                            for (int i = 0; i < rank; i++)

                                mat[col][i] -= mult
                                        * mat[row][i];
                        }
                    }
                }

                // Diagonal element is already zero.
                // Two cases arise:
                // 1) If there is a row below it
                // with non-zero entry, then swap
                // this row with that row and process
                // that row
                // 2) If all elements in current
                // column below mat[r][row] are 0,
                // then remvoe this column by
                // swapping it with last column and
                // reducing number of columns by 1.
                else
                {
                    boolean reduce = true;

                    // Find the non-zero element
                    // in current column
                    for (int i = row + 1; i < R; i++)
                    {
                        // Swap the row with non-zero
                        // element with this row.
                        if (mat[i][row] != 0.0)
                        {
                            swap(mat, row, i, rank);
                            reduce = false;
                            break ;
                        }
                    }

                    // If we did not find any row with
                    // non-zero element in current
                    // columnm, then all values in
                    // this column are 0.
                    if (reduce)
                    {
                        // Reduce number of columns
                        rank--;

                        // Copy the last column here
                        for (int i = 0; i < R; i ++)
                            mat[i][row] = mat[i][rank];
                    }

                    // Process this row again
                    row--;
                }

                // Uncomment these lines to see
                // intermediate results display(mat, R, C);
                // printf("\n");
            }

            return rank;
        }

// This code is contributed by Anant Agarwal.





    //from https://www.geeksforgeeks.org/program-check-matrix-singular-not/

    /* Recursive function to check if mat[][] is
    singular or not. */
    public BigInteger isSingular(IntMatrix M, int n)
    {
        //works only for square matrices
        BigInteger D = BigInteger.ZERO; // Initialize result

        // Base case : if matrix contains single element
        if (n == 1)
        {
            return M.getData()[0][0];
        }

        BigInteger[][] temp = new BigInteger[n][n]; // To store cofactors

        BigInteger sign = BigInteger.ONE; // To store sign multiplier

        // Iterate for each element of first row
        for (int f = 0; f < n; f++)
        {

            // Getting Cofactor of mat[0][f]
            getCofactor(M, temp, 0, f, n);
            D = D.add(sign.multiply(M.getData()[0][f]).multiply(isSingular(new IntMatrix(temp), n - 1)));

            // terms are to be added with alternate sign
            sign = BigInteger.ZERO.subtract(sign);
        }

        return D;
    }


    //mie
    public IntMatrix identityMatrixTimes(int size, int mul){
        BigInteger[][] data = new BigInteger[size][size];
        for (int i = 0; i < size; i++) {
            data[i][i] = BigInteger.valueOf(mul);
        }
        return new IntMatrix(data);
    }
}

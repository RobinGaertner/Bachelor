package com.company;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.linear.*;

public class DummyFunctions {





    public List<Double> OLS(RealMatrix matrix, RealVector vector){

        System.out.println("OLS gets the matrices: " + matrix);
        System.out.println("OLS gets the vector: " + vector);


        /*
        //bring the input into the right format
        BigInteger[][] data = matrix.getData();
        double[][] newData = new double[matrix.getM()][matrix.getN()];
        for (int i = 0; i < matrix.getM(); i++) {
            for (int j = 0; j < matrix.getN(); j++) {
                //TODO: casting here
                newData[i][j] = data[i][j].doubleValue();
            }
        }

         */
        RealMatrix coefficients = matrix;
        DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();

        System.out.println("solver sais matrix is nonsingular: " + solver.isNonSingular());



/*
        BigInteger[][] vectorData = vector.getData();

        double[] newVectorData = new double[vector.getN()];
        for (int i = 0; i < vector.getN(); i++) {
            newVectorData[i] = vectorData[0][i].doubleValue();
        }
        //print newVectorData
        System.out.println(newVectorData[0]);
        System.out.println(newVectorData[1]);
        System.out.println(newVectorData[2]);


 */

        RealVector constants = vector;
        RealVector solution = solver.solve(constants);


        System.out.println("Solution: " + solution);

        System.out.println("solution dimension: " + solution.getDimension());
        List<Double> resultList = new LinkedList<>();
        for (int i = 0; i < solution.getDimension(); i++) {
            //TODO: casting here
            resultList.add( solution.getEntry(i));
        }

        System.out.println("Size: " + resultList.size());
        System.out.println("Size: " + resultList.get(0));
        System.out.println("Size: " + resultList.get(1));
        System.out.println("Size: " + resultList.get(2));
        return resultList;
    }










    //from https://www.geeksforgeeks.org/program-for-rank-of-matrix/
    // function for exchanging two rows
    // of a matrix
    static void swap(IntMatrix A,
                     int row1, int row2, int col)
    {
        for (int i = 0; i < col; i++)
        {
            BigInteger temp = A.getData()[row1][i];
            A.getData()[row1][i] = A.getData()[row2][i];
            A.getData()[row2][i] = temp;
        }
    }


    public int rankOfMatrix(RealMatrix A){

        //TODO: change this
        return 0;
    }


    // function for finding rank of matrix
    public int rankOfMatrix(IntMatrix A)
    {
        int rank = A.getN();

        for (int row = 0; row < rank; row++)
        {

            // Before we visit current row
            // 'row', we make sure that
            // mat[row][0],....mat[row][row-1]
            // are 0.

            // Diagonal element is not zero
            if (!A.getData()[row][row].equals(BigInteger.ZERO) )
            {
                for (int col = 0; col < A.getM(); col++)
                {
                    if (col != row)
                    {
                        // This makes all entries
                        // of current column
                        // as 0 except entry
                        // 'mat[row][row]'
                        BigInteger mult = A.getData()[col][row].divide(A.getData()[row][row]);

                        for (int i = 0; i < rank; i++)

                            A.getData()[col][i] = A.getData()[col][i].subtract(mult.multiply( A.getData()[row][i]));
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
                for (int i = row + 1; i < A.getM(); i++)
                {
                    // Swap the row with non-zero
                    // element with this row.
                    if (!A.getData()[i][row].equals(BigInteger.ZERO))
                    {
                        swap(A, row, i, rank);
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
                    for (int i = 0; i < A.getM(); i ++)
                        A.getData()[i][row] = A.getData()[i][rank];
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

}

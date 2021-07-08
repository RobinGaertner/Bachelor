package com.company.tests;

import com.company.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DummyTest {

    DummyFunctions dummyFunctions = new DummyFunctions();


    @Test
    void getRankDummyTest() {


        BigInteger[][] data = new BigInteger[3][3];
        data[0][0] = BigInteger.valueOf(10);
        data[0][1] = BigInteger.valueOf(20);
        data[0][1] = BigInteger.valueOf(20);
        data[0][2] = BigInteger.valueOf(10);

        data[1][0] = BigInteger.valueOf(-20);
        data[1][1] = BigInteger.valueOf(-30);
        data[1][2] = BigInteger.valueOf(10);

        data[2][0] = BigInteger.valueOf(30);
        data[2][1] = BigInteger.valueOf(50);
        data[2][2] = BigInteger.valueOf(0);

        IntMatrix matrix = new IntMatrix(data);


        assertEquals(2, dummyFunctions.rankOfMatrix(matrix));


    }

    @Test
    void getRankDummyTest2() {


        BigInteger[][] data = new BigInteger[4][4];
        data[0][0] = BigInteger.valueOf(1);
        data[0][1] = BigInteger.valueOf(2);
        data[0][2] = BigInteger.valueOf(3);
        data[0][3] = BigInteger.valueOf(4);

        data[1][0] = BigInteger.valueOf(5);
        data[1][1] = BigInteger.valueOf(7);
        data[1][2] = BigInteger.valueOf(9);
        data[1][3] = BigInteger.valueOf(2);

        data[2][0] = BigInteger.valueOf(4);
        data[2][1] = BigInteger.valueOf(7);
        data[2][2] = BigInteger.valueOf(9);
        data[2][3] = BigInteger.valueOf(2);


        data[3][0] = BigInteger.valueOf(4);
        data[3][1] = BigInteger.valueOf(6);
        data[3][2] = BigInteger.valueOf(5);
        data[3][3] = BigInteger.valueOf(8);

        IntMatrix matrix = new IntMatrix(data);


        assertEquals(4, dummyFunctions.rankOfMatrix(matrix));
    }


    @Test
    void OLSTest(){


         double[][] matrix1 =  { {  2,3, -2 },
                                     {  -1, 7, 6},
                                     {  4, -3, 5}
         };

         RealMatrix M = new Array2DRowRealMatrix(matrix1);

        double[] matrix2 =  {  1,-2, 1 };


        RealVector y = new ArrayRealVector(matrix2);

        System.out.println(dummyFunctions.OLS(M, y));
        //assertEquals(, dummyFunctions.OLS(M, y));



    }


    @Test
    void multiplyoutTest(){

        CountingTest countingTest = new CountingTest(5, null, null);


        List<BigInteger> testList = new LinkedList<>();

        testList.add(BigInteger.valueOf(2));
        testList.add(BigInteger.valueOf(3));
        testList.add(BigInteger.valueOf(4));
        testList.add(BigInteger.valueOf(5));


        System.out.println("resList" + countingTest.multiplyOut(testList));


    }


}
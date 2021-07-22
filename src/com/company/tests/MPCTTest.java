package com.company.tests;

import com.company.CountingTestCoordinator;
import com.company.FModular;
import com.company.Polynomial;
import com.company.Utils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.jlinalg.LinSysSolver;
import org.jlinalg.Matrix;
import org.jlinalg.Vector;
import org.jlinalg.rational.Rational;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class MPCTTest {

    //TODO: change seed
    CountingTestCoordinator counting;




    int numparties = 2;
    int treshold = 2;
    BigInteger FMod = BigInteger.valueOf(1097);


    MPCTTest() throws Exception {

        counting = new CountingTestCoordinator(numparties, treshold, FMod );
    }


    @Test
    void MPCTTest() throws Exception {

        //t is important
        int t = treshold;
        //TODO: change to p

        List<BigInteger> inputList1 = new LinkedList<>();
        inputList1.add(BigInteger.valueOf(17));
        inputList1.add(BigInteger.valueOf(13));
        inputList1.add(BigInteger.valueOf(12));
        inputList1.add(BigInteger.valueOf(11));
        inputList1.add(BigInteger.valueOf(19));

        counting.parties.get(0).setInputSet(inputList1);

        List<BigInteger> inputList2 = new LinkedList<>();
        inputList2.add(BigInteger.valueOf(29));
        inputList2.add(BigInteger.valueOf(13));
        inputList2.add(BigInteger.valueOf(12));
        inputList2.add(BigInteger.valueOf(14));
        inputList2.add(BigInteger.valueOf(19));

        counting.parties.get(1).setInputSet(inputList2);


        List<BigInteger> alphaList = new LinkedList<>();

        for (int i = 1; i < 4*t+3; i++) {
            alphaList.add(BigInteger.valueOf(i));
        }



        counting.resetStats();
        assertEquals( true,counting.MPCT( alphaList , FMod));
        counting.printStats();


    }






}
package com.company.tests;

import com.company.*;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SDTTests {

    //TODO: change seed
    CountingTestCoordinator counting;




    int numparties = 10;
    int treshold = 5;


    SDTTests() throws Exception {

        counting = new CountingTestCoordinator(numparties, treshold);
    }


    @Test
    void getRankDummyTest() throws Exception {

        //t is important
        int t = treshold;

        //degree of the Polynomials
        int d = 5;


        List<Double> inputList = new LinkedList<>();

        for (int i = 0; i < 4*t+2; i++) {
            inputList.add((double) 2*i+2);
        }


        double[] p1Array = new double[t+1];
        for (int i = 0; i < t-1; i++) {
            p1Array[i] = i+2;
        }
        PolynomialFunction p1 = new PolynomialFunction(p1Array);


        double[] p2Array = new double[t+1];
        for (int i = 0; i < t-1; i++) {
            p2Array[i] = 6+i;
        }
        PolynomialFunction p2 = new PolynomialFunction(p2Array);


        List<Double> fList = new LinkedList<>();

        for (int i = 0; i < inputList.size(); i++) {
            fList.add(p1.value(i) / p2.value(i));
        }


        assertEquals(true, counting.SDT(fList, inputList, t));



        /*
       new Array2DRowRealMatrix(
        {{8.0,27.0,64.0,125.0,216.0,343.0,512.0},{4.0,9.0,16.0,25.0,36.0,49.0,64.0},{2.0,3.0,4.0,5.0,6.0,7.0,8.0},{1.0,1.0,1.0,1.0,1.0,1.0,1.0},{-1.3333333333,-3.4615384615,-6.4,-10.1851851852,-14.8235294118,-20.3170731707,-26.6666666667},{-0.6666666667,-1.1538461538,-1.6,-2.037037037,-2.4705882353,-2.9024390244,-3.3333333333},{-0.3333333333,-0.3846153846,-0.4,-0.4074074074,-0.4117647059,-0.4146341463,-0.4166666667}}
       )


        */
    }

}
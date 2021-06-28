package com.company.tests;

import com.company.*;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SDTTests {

    //TODO: change seed
    CountingTestCoordinator counting;




    int numparties = 10;
    int treshold = 3;


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
            inputList.add((double) i+1);
        }



        double[] p1Array = new double[t+1];
        for (int i = 0; i < t+1; i++) {
            p1Array[i] = i+1;
        }
        PolynomialFunction p1 = new PolynomialFunction(p1Array);


        double[] p2Array = new double[t+1];
        for (int i = 0; i < t+1; i++) {
            p2Array[i] = 6+i;
        }
        PolynomialFunction p2 = new PolynomialFunction(p2Array);


        List<Double> fList = new LinkedList<>();

        for (int i = 0; i < inputList.size(); i++) {
            fList.add(p1.value(i) / p2.value(i));
        }


        assertEquals(true, counting.SDT(fList, inputList, t));





    }

}
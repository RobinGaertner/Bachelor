package com.company.tests;

import com.company.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.jlinalg.Matrix;
import org.jlinalg.Vector;
import org.jlinalg.rational.Rational;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import org.jlinalg.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SDTTests {

    //TODO: change seed
    CountingTestCoordinator counting;




    int numparties = 10;
    int treshold = 3;
    Utils utils = new Utils();


    SDTTests() throws Exception {

        counting = new CountingTestCoordinator(numparties, treshold);
    }


    @Test
    void SDT() throws Exception {

        //t is important
        int t = treshold;
        //TODO: change to p
        BigInteger modulus = BigInteger.valueOf(1097);

        //degree of the Polynomials
        int d = 5;


        FModular.FModularFactory factory = FModular.FACTORY;



        List<BigInteger> inputList = new LinkedList<>();

        for (int i = 0; i < 4*t+2; i++) {
            inputList.add(BigInteger.valueOf(7*i+17));
        }

        //make the polynomials
        List<BigInteger> poly1List = new LinkedList<>();
        //for (int i = 0; i < t - 1; i++) {
        //    poly1List.add(BigInteger.valueOf(i+1));
        //}
        poly1List.add(BigInteger.valueOf(18));
        poly1List.add(BigInteger.valueOf(27));
        poly1List.add(BigInteger.valueOf(10));
        poly1List.add(BigInteger.valueOf(1));

        Polynomial polynomial1 = new Polynomial();
        polynomial1.init(poly1List, modulus);

        //make second polynomial
        List<BigInteger> poly2List = new LinkedList<>();
        //poly2List.add(BigInteger.ONE);
        //for (int i = 0; i < t - 2; i++) {
        //    poly2List.add(BigInteger.valueOf(3*i+37));
        //}

        poly2List.add(BigInteger.valueOf(70));
        poly2List.add(BigInteger.valueOf(59));
        poly2List.add(BigInteger.valueOf(14));
        poly2List.add(BigInteger.valueOf(1));



        Polynomial polynomial2 = new Polynomial();
        polynomial2.init(poly2List, modulus);


        //divide them
        List<FModular> fList = new LinkedList<>();
        for (int i = 0; i < inputList.size(); i++) {
            FModular tmp1 = factory.get(polynomial1.call(inputList.get(i)));
            FModular tmp2 = factory.get(polynomial2.call(inputList.get(i)));

            System.out.println("input list: " + inputList.get(i));
            System.out.println("tmp1: " + tmp1);
            System.out.println("tmp1 should be: " + polynomial2.call(inputList.get(i)));
            System.out.println("tmp2: " + tmp2);
            fList.add(tmp1.divide(tmp2));
        }

        List<EncryptedNumber> encList = new LinkedList<>();
        for (int i = 0; i < fList.size(); i++) {
            encList.add(counting.publicKey.encrypt(fList.get(i).getValue()));
        }




        assertEquals(true, counting.SDT(encList, inputList, t, modulus));



    }


    @Test
    void matrixRankTest(){

        double[][] data = new double[5][5];

        data[0] = new double[]{5, 6, 7, 8, 9};
        data[1] = new double[]{4, 6, 7, 8, 10};
        data[2] = new double[]{5, 6, 7, 9, 12};
        data[3] = new double[]{2, 6, 4, 7, 9};
        data[4] = new double[]{5, 17, 7, 8, 9};



        RealMatrix m = new Array2DRowRealMatrix(data);

        assertEquals(5, utils.rankOfMatrix(m));

    }

    @Test
    void otherTest(){

        // Create some rational numbers from the default (singelton) factory.
        Rational r1, r2, r3, r4, r5, r6;
        r1 = Rational.FACTORY.get(1);
        r2 = Rational.FACTORY.get(2);
        r3 = Rational.FACTORY.get(3);
        r4 = Rational.FACTORY.get(4);
        r5 = Rational.FACTORY.get(5);
        r6 = Rational.FACTORY.get(6);


        FModular.FModularFactory factory = FModular.FACTORY;

        // create a matrix
        Matrix<FModular> a = new Matrix<FModular>(new FModular[][]
                {
                        {
                                factory.get(1), factory.get(2), factory.get(3)
                        },
                        {
                               factory.get(4), factory.get(5), factory.get(6)
                        }
                });

        // create a vector
        Vector<FModular> b = new Vector<FModular>(new FModular[]
                {
                        factory.get(1), factory.get(2)
                });

        // calculate the solution and print it
        Vector<FModular> solution = LinSysSolver.solve(a, b);

        System.out.println("x = " + solution);

    }



    @Test
    void TestFModular(){

        FModular.FModularFactory factory = FModular.FACTORY;

        FModular ten = factory.get(10);
        FModular twenty = factory.get(20);

        System.out.println(twenty.compareTo(ten));
        System.out.println(BigInteger.valueOf(20).compareTo(BigInteger.valueOf(10)));

        System.out.println(factory.get(-20));

    }

    @Test
    void TestFModular2(){

        FModular.FModularFactory factory = FModular.FACTORY;

        FModular ten = factory.get(10);
        FModular twenty = factory.get(20);

        FModular res = ten.add(twenty);
        FModular sub = ten.subtract(twenty);

        System.out.println("add:" + res);

        System.out.println("sub: " +sub);

        FModular mul = ten.multiply(twenty);
        System.out.println("mul: " + mul);

        FModular div = twenty.divide(ten);
        System.out.println("div: " + div);



    }






}
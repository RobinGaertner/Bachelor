package com.company.tests;

import com.company.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObliviousSupportTests {

    //TODO: change seed
    Random rnd = new Random();
    KeyGen keyGen = new KeyGen();
    PrimeGen primeGen = new PrimeGen();
    ObliviousAlgebra obliviousAlgebra = new ObliviousAlgebra();


    @org.junit.jupiter.api.Test
    void upperToelpitzTest() throws Exception {
        for (int i = 0; i < 10; i++) {

            System.out.println(obliviousAlgebra.upperToeplitz(5));


        }
    }

    @org.junit.jupiter.api.Test
    void lowerToelpitzTest() throws Exception {
        for (int i = 0; i < 10; i++) {

            System.out.println(obliviousAlgebra.lowerToeplitz(5));


        }
    }

    @org.junit.jupiter.api.Test
    void diagonalMatrixTest() throws Exception {
        for (int i = 0; i < 10; i++) {

            System.out.println(obliviousAlgebra.diagonalMatrix(5));


        }
    }
}

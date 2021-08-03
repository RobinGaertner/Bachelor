package com.company.tests;

import com.company.CountingTestCoordinator;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;


class MPCTTest {
    CountingTestCoordinator counting;



    MPCTTest() throws Exception {

    }


    @Test
    void MPCTTest() throws Exception {

        int numparties = 2;
        int treshold = 2;
        BigInteger FMod = BigInteger.valueOf(19937);


        counting = new CountingTestCoordinator(numparties, treshold, FMod );

        //t is important
        int t = treshold;

        List<BigInteger> inputList1 = new LinkedList<>();
        inputList1.add(BigInteger.valueOf(137));
        inputList1.add(BigInteger.valueOf(276));
        inputList1.add(BigInteger.valueOf(317));
        inputList1.add(BigInteger.valueOf(912));
        inputList1.add(BigInteger.valueOf(713));

        counting.parties.get(0).setInputSet(inputList1);

        List<BigInteger> inputList2 = new LinkedList<>();
        inputList2.add(BigInteger.valueOf(137));
        inputList2.add(BigInteger.valueOf(276));
        inputList2.add(BigInteger.valueOf(317));
        inputList2.add(BigInteger.valueOf(219));
        inputList2.add(BigInteger.valueOf(358));

        counting.parties.get(1).setInputSet(inputList2);


        List<BigInteger> alphaList = new LinkedList<>();

        for (int i = 1; i < 4*t+3; i++) {
            alphaList.add(BigInteger.valueOf(i));
        }

        counting.resetStats();
        assertTrue(counting.MPCT(alphaList, FMod));
        counting.printStats();
    }


    @Test
    void MPCTTest4() throws Exception {

        int numparties = 2;
        int treshold = 4;
        BigInteger FMod = BigInteger.valueOf(19937);


        counting = new CountingTestCoordinator(numparties, treshold, FMod );

        //t is important
        int t = treshold;

        List<BigInteger> inputList1 = new LinkedList<>();
        inputList1.add(BigInteger.valueOf(137));
        inputList1.add(BigInteger.valueOf(276));
        inputList1.add(BigInteger.valueOf(717));
        inputList1.add(BigInteger.valueOf(912));
        inputList1.add(BigInteger.valueOf(713));

        counting.parties.get(0).setInputSet(inputList1);

        List<BigInteger> inputList2 = new LinkedList<>();
        inputList2.add(BigInteger.valueOf(137));
        inputList2.add(BigInteger.valueOf(276));
        inputList2.add(BigInteger.valueOf(317));
        inputList2.add(BigInteger.valueOf(219));
        inputList2.add(BigInteger.valueOf(358));

        counting.parties.get(1).setInputSet(inputList2);


        List<BigInteger> alphaList = new LinkedList<>();

        for (int i = 1; i < 4*t+3; i++) {
            alphaList.add(BigInteger.valueOf(i));
        }

        counting.resetStats();
        assertTrue(counting.MPCT(alphaList, FMod));
        counting.printStats();
    }

    @Test
    void MPCTTest10Parties() throws Exception {


        int numparties = 10;
        int treshold = 2;
        BigInteger FMod = BigInteger.valueOf(1097);


        counting = new CountingTestCoordinator(numparties, treshold, FMod );

        //t is important

        List<BigInteger> inputList1 = new LinkedList<>();
        inputList1.add(BigInteger.valueOf(17));
        inputList1.add(BigInteger.valueOf(13));
        inputList1.add(BigInteger.valueOf(12));
        inputList1.add(BigInteger.valueOf(33));
        inputList1.add(BigInteger.valueOf(22));

        counting.parties.get(0).setInputSet(inputList1);

        List<BigInteger> inputList2 = new LinkedList<>();
        inputList2.add(BigInteger.valueOf(17));
        inputList2.add(BigInteger.valueOf(13));
        inputList2.add(BigInteger.valueOf(12));
        inputList2.add(BigInteger.valueOf(67));
        inputList2.add(BigInteger.valueOf(89));

        counting.parties.get(1).setInputSet(inputList2);


        List<BigInteger> inputList3 = new LinkedList<>();
        inputList3.add(BigInteger.valueOf(17));
        inputList3.add(BigInteger.valueOf(13));
        inputList3.add(BigInteger.valueOf(12));
        inputList3.add(BigInteger.valueOf(45));
        inputList3.add(BigInteger.valueOf(34));

        counting.parties.get(2).setInputSet(inputList3);

        List<BigInteger> inputList4 = new LinkedList<>();
        inputList4.add(BigInteger.valueOf(17));
        inputList4.add(BigInteger.valueOf(13));
        inputList4.add(BigInteger.valueOf(12));
        inputList4.add(BigInteger.valueOf(98));
        inputList4.add(BigInteger.valueOf(53));

        counting.parties.get(3).setInputSet(inputList4);

        List<BigInteger> inputList5 = new LinkedList<>();
        inputList5.add(BigInteger.valueOf(17));
        inputList5.add(BigInteger.valueOf(13));
        inputList5.add(BigInteger.valueOf(12));
        inputList5.add(BigInteger.valueOf(97));
        inputList5.add(BigInteger.valueOf(63));

        counting.parties.get(4).setInputSet(inputList5);

        List<BigInteger> inputList6 = new LinkedList<>();
        inputList6.add(BigInteger.valueOf(17));
        inputList6.add(BigInteger.valueOf(13));
        inputList6.add(BigInteger.valueOf(12));
        inputList6.add(BigInteger.valueOf(33));
        inputList6.add(BigInteger.valueOf(22));

        counting.parties.get(5).setInputSet(inputList6);

        List<BigInteger> inputList7 = new LinkedList<>();
        inputList7.add(BigInteger.valueOf(17));
        inputList7.add(BigInteger.valueOf(13));
        inputList7.add(BigInteger.valueOf(12));
        inputList7.add(BigInteger.valueOf(33));
        inputList7.add(BigInteger.valueOf(22));

        counting.parties.get(6).setInputSet(inputList7);

        List<BigInteger> inputList8 = new LinkedList<>();
        inputList8.add(BigInteger.valueOf(17));
        inputList8.add(BigInteger.valueOf(13));
        inputList8.add(BigInteger.valueOf(12));
        inputList8.add(BigInteger.valueOf(33));
        inputList8.add(BigInteger.valueOf(22));


        counting.parties.get(7).setInputSet(inputList8);

        List<BigInteger> inputList9 = new LinkedList<>();
        inputList9.add(BigInteger.valueOf(17));
        inputList9.add(BigInteger.valueOf(13));
        inputList9.add(BigInteger.valueOf(12));
        inputList9.add(BigInteger.valueOf(33));
        inputList9.add(BigInteger.valueOf(22));

        counting.parties.get(8).setInputSet(inputList9);

        List<BigInteger> inputList10 = new LinkedList<>();
        inputList10.add(BigInteger.valueOf(17));
        inputList10.add(BigInteger.valueOf(13));
        inputList10.add(BigInteger.valueOf(12));
        inputList10.add(BigInteger.valueOf(33));
        inputList10.add(BigInteger.valueOf(22));


        counting.parties.get(9).setInputSet(inputList10);

        List<BigInteger> alphaList = new LinkedList<>();

        for (int i = 1; i < 4* treshold +3; i++) {
            alphaList.add(BigInteger.valueOf(i));
        }



        counting.resetStats();
        assertTrue(counting.MPCT(alphaList, FMod));
        counting.printStats();


    }

    @Test
    void MPCTTest10Numbers() throws Exception {

        int numparties = 2;
        int treshold = 2;
        BigInteger FMod = BigInteger.valueOf(1097);


        counting = new CountingTestCoordinator(numparties, treshold, FMod );

        //t is important

        List<BigInteger> inputList1 = new LinkedList<>();
        inputList1.add(BigInteger.valueOf(47));
        inputList1.add(BigInteger.valueOf(59));
        inputList1.add(BigInteger.valueOf(12));
        inputList1.add(BigInteger.valueOf(11));
        inputList1.add(BigInteger.valueOf(19));
        inputList1.add(BigInteger.valueOf(22));
        inputList1.add(BigInteger.valueOf(45));
        inputList1.add(BigInteger.valueOf(56));
        inputList1.add(BigInteger.valueOf(73));
        inputList1.add(BigInteger.valueOf(90));

        counting.parties.get(0).setInputSet(inputList1);

        List<BigInteger> inputList2 = new LinkedList<>();
        inputList2.add(BigInteger.valueOf(47));
        inputList2.add(BigInteger.valueOf(59));
        inputList2.add(BigInteger.valueOf(12));
        inputList2.add(BigInteger.valueOf(11));
        inputList2.add(BigInteger.valueOf(19));
        inputList2.add(BigInteger.valueOf(22));
        inputList2.add(BigInteger.valueOf(45));
        inputList2.add(BigInteger.valueOf(56));
        inputList2.add(BigInteger.valueOf(67));
        inputList2.add(BigInteger.valueOf(78));

        counting.parties.get(1).setInputSet(inputList2);


        List<BigInteger> alphaList = new LinkedList<>();

        for (int i = 1; i < 4* treshold +3; i++) {
            alphaList.add(BigInteger.valueOf(i));
        }



        counting.resetStats();
        assertTrue(counting.MPCT(alphaList, FMod));
        counting.printStats();


    }


    @Test
    void MPCTTestDifferentInputLength() throws Exception {

        int numparties = 2;
        int treshold = 3;
        BigInteger FMod = BigInteger.valueOf(1097);


        counting = new CountingTestCoordinator(numparties, treshold, FMod );


        List<BigInteger> inputList1 = new LinkedList<>();
        inputList1.add(BigInteger.valueOf(137));
        inputList1.add(BigInteger.valueOf(276));
        inputList1.add(BigInteger.valueOf(317));
        inputList1.add(BigInteger.valueOf(912));
        inputList1.add(BigInteger.valueOf(942));
        inputList1.add(BigInteger.valueOf(634));
        inputList1.add(BigInteger.valueOf(831));

        counting.parties.get(0).setInputSet(inputList1);

        List<BigInteger> inputList2 = new LinkedList<>();
        inputList2.add(BigInteger.valueOf(137));
        inputList2.add(BigInteger.valueOf(276));
        inputList2.add(BigInteger.valueOf(317));
        inputList2.add(BigInteger.valueOf(912));
        inputList2.add(BigInteger.valueOf(713));

        counting.parties.get(1).setInputSet(inputList2);


        List<BigInteger> alphaList = new LinkedList<>();

        for (int i = 1; i < 4* treshold +3; i++) {
            alphaList.add(BigInteger.valueOf(i));
        }

        counting.resetStats();
        assertTrue(counting.MPCT(alphaList, FMod));
        counting.printStats();


    }


    @Test
    void MPCTTestBigTreshold() throws Exception {

        int numparties = 2;
        int treshold = 9;
        BigInteger FMod = BigInteger.valueOf(19937);


        counting = new CountingTestCoordinator(numparties, treshold, FMod );

        //t is important

        List<BigInteger> inputList1 = new LinkedList<>();
        inputList1.add(BigInteger.valueOf(47));
        inputList1.add(BigInteger.valueOf(631));
        inputList1.add(BigInteger.valueOf(734));
        inputList1.add(BigInteger.valueOf(97));
        inputList1.add(BigInteger.valueOf(63));
        inputList1.add(BigInteger.valueOf(472));
        inputList1.add(BigInteger.valueOf(852));
        inputList1.add(BigInteger.valueOf(952));
        inputList1.add(BigInteger.valueOf(653));
        inputList1.add(BigInteger.valueOf(942));

        counting.parties.get(0).setInputSet(inputList1);

        List<BigInteger> inputList2 = new LinkedList<>();
        inputList2.add(BigInteger.valueOf(47));
        inputList2.add(BigInteger.valueOf(392));
        inputList2.add(BigInteger.valueOf(740));
        inputList2.add(BigInteger.valueOf(302));
        inputList2.add(BigInteger.valueOf(596));
        inputList2.add(BigInteger.valueOf(128));
        inputList2.add(BigInteger.valueOf(843));
        inputList2.add(BigInteger.valueOf(760));
        inputList2.add(BigInteger.valueOf(288));
        inputList2.add(BigInteger.valueOf(773));

        counting.parties.get(1).setInputSet(inputList2);


        List<BigInteger> alphaList = new LinkedList<>();

        for (int i = 1; i < 4* treshold +3; i++) {
            alphaList.add(BigInteger.valueOf(i));
        }

        counting.resetStats();
        assertTrue(counting.MPCT(alphaList, FMod));
        counting.printStats();
    }

    @Test
    void MPCTTestBig() throws Exception {

        int numparties = 40;
        int treshold = 10;
        BigInteger FMod = BigInteger.valueOf(19937);


        counting = new CountingTestCoordinator(numparties, treshold, FMod );

        //generate numbers under 19937
        List<BigInteger> bigInputs = new LinkedList<>();
        for (int i = 0; i < 90; i++) {
            bigInputs.add(BigInteger.valueOf((78*(i+13) + (29*i+3))%19937));
        }

        for (int i = 0; i < numparties; i++) {
            List<BigInteger> finishedInputs = new LinkedList<>(bigInputs);
            //9 differences are allowed
            for (int j = 0; j < 9; j++) {
                finishedInputs.add(BigInteger.valueOf(i*(i+2)*j*j%19937));
            }
            counting.parties.get(i).setInputSet(finishedInputs);
        }

        List<BigInteger> alphaList = new LinkedList<>();

        for (int i = 1; i < 4* treshold +3; i++) {
            alphaList.add(BigInteger.valueOf(i));
        }

        counting.resetStats();
        assertTrue(counting.MPCT(alphaList, FMod));
        counting.printStats();
    }



}
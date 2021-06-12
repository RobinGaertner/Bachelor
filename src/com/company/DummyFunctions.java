package com.company;

import java.math.BigInteger;

public class DummyFunctions {

    int encryptCounter;




    BigInteger encrypt (BigInteger input){
        encryptCounter++;
        return input;
    }

}

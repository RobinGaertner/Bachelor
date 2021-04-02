package com.company;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Random;

import org.apache.commons.math3.geometry.partitioning.utilities.OrderedTuple;
import org.apache.commons.math3.primes.Primes;


public class PrimeGen {

    Random rnd = new Random();

    BigInteger genPrime(int primeBits){
        while(true) {
            BigInteger probRandom = BigInteger.probablePrime(primeBits, rnd);
            //BigInteger candidate = new BigInteger(primeBits, rnd);
            if(Primes.isPrime(probRandom.intValue())){
                return probRandom;
            }
        }
    }

    BigInteger genSafePrime(int primeBits){
        while(true){
            BigInteger q = genPrime(primeBits-1);
            BigInteger p = q.multiply(BigInteger.valueOf(2));
            BigInteger res = p.add(BigInteger.valueOf(1));
        }
    }

    BigInteger[] getSafePrimePair(int primebits){
        BigInteger p = genSafePrime(primebits);
        BigInteger q = genSafePrime(primebits);

        while(p.equals(q)){
            q = genSafePrime(primebits);
        }

        BigInteger[] primes = new BigInteger[2];
        primes[0] = p;
        primes[1] = q;
        return primes;
    }
}


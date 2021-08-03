package com.company;

import java.math.BigInteger;
import java.util.Random;


public class PrimeGen {

    Random rnd = new Random();

    public BigInteger genPrime(int primeBits){
        return BigInteger.probablePrime(primeBits, rnd);
    }



    public BigInteger genSafePrime(int primeBits){
        while (true) {
            BigInteger q = genPrime(primeBits - 1);
            BigInteger p = q.multiply(BigInteger.valueOf(2));
            BigInteger res = p.add(BigInteger.valueOf(1));
            if(res.isProbablePrime(1)) {
                return res;
            }
        }

    }

    public BigInteger[] getSafePrimePair(int primebits){
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


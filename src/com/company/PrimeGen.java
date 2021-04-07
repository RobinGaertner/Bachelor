package com.company;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Random;

import org.apache.commons.math3.geometry.partitioning.utilities.OrderedTuple;
import org.apache.commons.math3.primes.Primes;


public class PrimeGen {

    Random rnd = new Random();

    public int genPrime(int primeBits){
            int base = rnd.nextInt(primeBits);
            if(base<Math.pow(primeBits, 2)/2){
                base += 2^(primeBits-1);
            }

            int p = Primes.nextPrime(base);
            return p;
    }

    BigInteger genSafePrime(int primeBits){
            BigInteger q = BigInteger.valueOf(genPrime(primeBits-1));
            BigInteger p = q.multiply(BigInteger.valueOf(2));
            BigInteger res = p.add(BigInteger.valueOf(1));

            return res;

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


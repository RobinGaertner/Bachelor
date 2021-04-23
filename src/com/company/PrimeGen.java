package com.company;

import java.math.BigInteger;
import java.util.Random;

import org.apache.commons.math3.primes.Primes;


public class PrimeGen {

    //TODO: change back to random
    Random rnd = new Random(1);

    public BigInteger genPrime(int primeBits){
            /*int base = rnd.nextInt(primeBits);
            if(base<Math.pow(primeBits, 2)/2){
                base += 2^(primeBits-1);
            }

            int p = Primes.nextPrime(base);
            return p;

             */
        return BigInteger.probablePrime(160, rnd);
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


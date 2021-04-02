package com.company;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

public class PublicKey {

    int n;
    int s;
    int m;
    int threshold;
    int delta;
    int ns;
    int ns1;
    int nsm;
    Random rand = new Random();

    void init(int n,int  s,int  m,int  t,int  d) {

        this.n =n;
        this.s =s;
        this.m =m;
        threshold = t;
        delta = d;

        ns = n^s;
        ns1 = ns*n;
        nsm = ns*m;
    }


    EncryptedNumber encrypt (int plain){

        BigInteger r = BigInteger.valueOf(rand.nextInt(n -1) +1);
        BigInteger bigMod = BigInteger.valueOf(ns1);
        BigInteger bigNPlus1 = BigInteger.valueOf(n +1);
        BigInteger bigPlain = BigInteger.valueOf(plain);

        BigInteger c1 = bigNPlus1.modPow(bigPlain, bigMod);
        BigInteger c2 = r.modPow(BigInteger.valueOf(ns), BigInteger.valueOf(ns1));

        BigInteger c = (c1.multiply(c2)).mod(BigInteger.valueOf(ns1));

        EncryptedNumber res = new EncryptedNumber();

        res.init(c.intValue(), this);
        return res;
    }

    EncryptedNumber[] encryptList (List<Integer> list){

        EncryptedNumber[] resList = new EncryptedNumber[list.size()];
        for( int i=0; i<list.size(); i++){
            resList[i] = encrypt(list.get(i));
        }
        return resList;
    }

    boolean equal(PublicKey other){

        return other.delta == this.delta
            && other.n == this.n
            && other.s == this.s
            && other.threshold == this.threshold
            && other.m == this.m;

    }

    byte[] pkHash() throws NoSuchAlgorithmException {

        String original = "" + n+ s + m + threshold + delta;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashbytes = digest.digest(original.getBytes(StandardCharsets.UTF_8));
        return hashbytes;
    }

}

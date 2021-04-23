package com.company;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

public class PublicKey {

    BigInteger n;
    int s;
    BigInteger m;
    int threshold;
    BigInteger delta;
    public BigInteger ns;
    BigInteger ns1;
    BigInteger nsm;
    Random rand = new Random();
    public BigInteger invFourDeltaSquared;

    void init(BigInteger n, int  s, BigInteger  m, int  t, BigInteger d) {

        this.n =n;
        this.s =s;
        this.m =m;
        threshold = t;
        delta = d;

        //ns = n^s;
        ns = n.pow(s);
        ns1 = ns.multiply(n);
        nsm = ns.multiply(m);

        System.out.println("In public Key init ");
        BigInteger tmp = (delta.pow(2)).multiply(BigInteger.valueOf(4));
        System.out.println("delta is " + delta);
        System.out.println("tmp is: " + tmp);
        invFourDeltaSquared = tmp.modInverse(ns);
    }

    public BigInteger nextRandomBigInteger(BigInteger n) {
        //TODO: change seed
        Random rand = new Random(1);
        BigInteger result = new BigInteger(n.bitLength(), rand);
        while( result.compareTo(n) >= 0 ) {
            result = new BigInteger(n.bitLength(), rand);
        }
        return result;
    }

    public EncryptedNumber encrypt (BigInteger plain){

        //BigInteger r = BigInteger.valueOf(rand.nextInt(n.subtract(BigInteger.ONE).intValue()) +1);
        BigInteger r = nextRandomBigInteger(n.subtract(BigInteger.ONE)).add(BigInteger.ONE);

        BigInteger c1 = n.add(BigInteger.ONE).modPow(m, ns1);
        BigInteger c2 = r.modPow(ns, ns1);
        BigInteger c = (c1.multiply(c2)).mod(ns1);


        EncryptedNumber res = new EncryptedNumber();

        res.init(c, this);
        System.out.println("PublicKey finished encryption");
        return res;
    }

    EncryptedNumber[] encryptList (List<BigInteger> list){

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

    String pkHash() throws NoSuchAlgorithmException {

        String original = "" + n+ s + m + threshold + delta;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashbytes = digest.digest(original.getBytes(StandardCharsets.UTF_8));
        return original;
    }

    @Override
    public String toString() {
        return "PublicKey{" +
                "n=" + n +
                ", s=" + s +
                ", m=" + m +
                ", threshold=" + threshold +
                ", delta=" + delta +
                '}';
    }
}

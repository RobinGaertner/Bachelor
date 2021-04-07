package com.company;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PrivateKeyShare {

    PublicKey publicKey;
    int i;
    BigInteger si;
    BigInteger twoDeltaSI;

    void init(PublicKey pk, int i, BigInteger si){
        /*
        :param public_key: The PublicKey corresponding to this PrivateKeyShare.
        :param i: The x value of this share generated using a polynomial via Shamir secret sharing.
        :param s_i: The y=f(i) value of this share generated using a polynomial via Shamir secret sharing.
        */
        this.publicKey = pk;
        this.i = i;
        this.si = si;

        this.twoDeltaSI = si.multiply(BigInteger.valueOf(publicKey.delta)).multiply(BigInteger.valueOf(2));
    }


    int decrypt(EncryptedNumber c){
    //:return: An integer containing this PrivateKeyShare's portion of the decryption of `c`.
        BigInteger bigVal = BigInteger.valueOf(c.value);
        BigInteger bigRes = bigVal.modPow(twoDeltaSI, this.publicKey.ns1);

        return bigRes.intValue();
    }

    boolean equal(PrivateKeyShare other){
        return this.publicKey == other.publicKey
                && this.i == other.i
                && this.si == other.si;
    }

    byte[] publicKeyShareHash() throws NoSuchAlgorithmException {

        String original = "" + publicKey.pkHash() + i+si;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashbytes = digest.digest(original.getBytes(StandardCharsets.UTF_8));
        return hashbytes;
    }

}

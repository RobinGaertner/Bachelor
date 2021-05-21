package com.company;

import java.math.BigInteger;

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

        this.twoDeltaSI = si.multiply(publicKey.delta).multiply(BigInteger.valueOf(2));
    }


    BigInteger decrypt(EncryptedNumber c){
    //:return: An integer containing this PrivateKeyShare's portion of the decryption of `c`.
        BigInteger bigVal = c.value;
        BigInteger bigRes = c.value.modPow(twoDeltaSI, this.publicKey.ns1);

        return bigRes;
    }


    IntMatrix decryptMatrix(EncMatrix e){
        BigInteger[][] data = new BigInteger[e.M][e.N];
        EncryptedNumber[][] encData = e.getData();
        for (int i = 0; i < e.M; i++) {
            for (int j = 0; j < e.N; j++) {
                data[i][j] = decrypt(encData[i][j]);
            }
        }
        System.out.println("Decrypt matrix returns: " + new IntMatrix(data));
        return new IntMatrix(data);
    }

    boolean equal(PrivateKeyShare other){
        return this.publicKey == other.publicKey
                && this.i == other.i
                && this.si.equals(other.si);
    }


    @Override
    public String toString() {
        return "PrivateKeyShare{" +
                "publicKey=" + publicKey +
                ", i=" + i +
                ", si=" + si +
                '}';
    }
}

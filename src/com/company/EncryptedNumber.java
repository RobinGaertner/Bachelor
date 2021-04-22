package com.company;

import java.math.BigInteger;

public class EncryptedNumber {

    public BigInteger value;
    public PublicKey publicKey;
    private Utils utils;

    void init(BigInteger val, PublicKey pk){
        value = val;
        publicKey = pk;
    }

    EncryptedNumber add(EncryptedNumber other) {
    /*
    Applies the appropriate operations such that the result
    is an EncryptedNumber that decrypts to the sum of the
    the decryption of this number and the decryption of `other`.

    */
        EncryptedNumber res = new EncryptedNumber();

        BigInteger val = (value.multiply(other.value)).mod(publicKey.ns1);

        res.init(val, publicKey);

        return res;
    }


    EncryptedNumber rAdd(EncryptedNumber other){
        //same as add, but order reversed (still same)
        return add(other);
    }


    //TODO: DO ALL THIS SHIT
    EncryptedNumber sub(EncryptedNumber other) {
        /*Applies the appropriate operations such that the result
        is an EncryptedNumber that decrypts to the difference of the
        the decryption of this number and the decryption of `other`.
         */

        EncryptedNumber otherInverse = new EncryptedNumber();
        //int val = utils.invMod(other.value, other.publicKey.ns1.intValueExact());
        //otherInverse.init(val, other.publicKey);

        return add(otherInverse);
    }

    EncryptedNumber rSub(EncryptedNumber other) {
        //like sub, but order reversed

        EncryptedNumber selfInverse = new EncryptedNumber();
        //int val = utils.invMod(value, publicKey.ns1.intValueExact());
        //selfInverse.init(val, publicKey);

        return selfInverse.add(other);
    }

    EncryptedNumber mul(int a){
        //Multiplies an EncryptedNumber by a scalar.


        /*Applies the appropriate operations such that the result
        is an EncryptedNumber that decrypts to the product of the
        the decryption of this number and `other`.
        */

        EncryptedNumber res = new EncryptedNumber();


        BigInteger bigVal = value;
        BigInteger bigA = BigInteger.valueOf(a);
        BigInteger bigNs1 = BigInteger.valueOf(publicKey.ns1.intValueExact());

        BigInteger newVal = bigVal.modPow(bigA, bigNs1);

        int val = newVal.intValue();
        //res.init(val, publicKey);

        return res;
    }

    EncryptedNumber rMul(int a){
        //like mul, but order reversed, so the same
        return mul(a);
    }


    EncryptedNumber trueDiv(int d){
        EncryptedNumber res = new EncryptedNumber();
        //int val = value * utils.invMod(d, publicKey.ns1.intValueExact());

        //res.init(val, publicKey);
        return res;
    }

    boolean equal(EncryptedNumber other){
    /*
        Two EncryptedNumbers are equal when their values and PublicKeys are the same.
        Note: Two EncryptedNumbers containing encryptions of the same plaintext which
        were encrypted using the same PublicKey can still be not equal due to
        randomness in the encryption process.
     */
        return ((this.value == other.value) & (this.publicKey.equals(other.publicKey)));
    }

    @Override
    public String toString() {
        return "EncryptedNumber{" +
                "value=" + value +
                '}';
    }
}


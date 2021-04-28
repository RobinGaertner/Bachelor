package com.company;

import java.math.BigInteger;

public class EncryptedNumber {

    public BigInteger value;
    public PublicKey publicKey;

    void init(BigInteger val, PublicKey pk){
        value = val;
        publicKey = pk;
    }

    public EncryptedNumber add(EncryptedNumber other) throws Exception {
    /*
    Applies the appropriate operations such that the result
    is an EncryptedNumber that decrypts to the sum of the
    the decryption of this number and the decryption of `other`.

    */
        if(!this.publicKey.equals(other.publicKey)){
            throw new Exception("Can only add for numbers encrypted with the same PublicKey");
        }
        EncryptedNumber res = new EncryptedNumber();

        BigInteger val = (value.multiply(other.value)).mod(publicKey.ns1);

        res.init(val, publicKey);

        return res;
    }


    public EncryptedNumber rAdd(EncryptedNumber other) throws Exception {
        //same as add, but order reversed (still same)
        return add(other);
    }


    public EncryptedNumber sub(EncryptedNumber other) throws Exception {
        /*Applies the appropriate operations such that the result
        is an EncryptedNumber that decrypts to the difference of the
        the decryption of this number and the decryption of `other`.
         */

        EncryptedNumber otherInverse = new EncryptedNumber();
        otherInverse.init(other.value.modInverse(other.publicKey.ns1), other.publicKey);
        return add(otherInverse);
    }

    public EncryptedNumber rSub(EncryptedNumber other) throws Exception {
        //like sub, but order reversed

        EncryptedNumber selfInverse = new EncryptedNumber();
        selfInverse.init(value.modInverse(publicKey.ns1), publicKey);

        return selfInverse.add(other);
    }

    public EncryptedNumber mul(BigInteger a){
        //Multiplies an EncryptedNumber by a scalar.


        /*Applies the appropriate operations such that the result
        is an EncryptedNumber that decrypts to the product of the
        the decryption of this number and `other`.
        */

        EncryptedNumber res = new EncryptedNumber();
        BigInteger newVal = value.modPow(a, publicKey.ns1);
        res.init(value.modPow(a, publicKey.ns1), publicKey);

        return res;
    }

    public EncryptedNumber rMul(BigInteger a){
        //like mul, but order reversed, so the same
        return mul(a);
    }


    public EncryptedNumber trueDiv(BigInteger d){
        System.out.println("Input in Div: " + d);
        EncryptedNumber res = new EncryptedNumber();
        System.out.println("d Inverse: " + d.modInverse(publicKey.ns1));
        res.init(value.multiply(d.modInverse(publicKey.ns1)), publicKey);
        return res;
    }

    public boolean equal(EncryptedNumber other){
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


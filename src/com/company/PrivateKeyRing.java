package com.company;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class PrivateKeyRing {

    Utils utils = new Utils();
    PublicKey publicKey;
    List<PrivateKeyShare> privateKeyShareList;
    List<Integer> iList = new LinkedList<>();
    Set<Integer> S;
    BigInteger invFourDeltaSquared;

    void init(List<PrivateKeyShare> keyShareList) throws Exception {
        //Initializes the PrivateKeyRing,
        // checks that enough PrivateKeyShares are provided,
        // and performs pre-computations.

        if (keyShareList.size() == 0) {
            throw new Exception("Must have at least one PrivateKeyShare");
        }
        List<String> test = new LinkedList<String>();
        for (int i = 0; i < keyShareList.size(); i++) {
            if (!test.contains(keyShareList.get(i).publicKey.pkHash())) {
                test.add(keyShareList.get(i).publicKey.pkHash());
            }
        }
        if (test.size() > 1) {
            throw new Exception("PrivateKeyShares do not have the same public key");
        }

        this.publicKey = keyShareList.get(0).publicKey;
        this.privateKeyShareList = keyShareList;

        if (privateKeyShareList.size() < publicKey.threshold) {
            throw new Exception("Number of unique PrivateKeyShares is less than the threshold to decrypt");
        }
        //testing finished

        this.privateKeyShareList = privateKeyShareList.subList(0, publicKey.threshold);

        for (int j = 0; j < privateKeyShareList.size(); j++) {
            iList.add(privateKeyShareList.get(j).i);
        }

        S = new HashSet<>(iList);
        BigInteger tmp = (publicKey.delta.pow(2)).multiply(BigInteger.valueOf(4));
        invFourDeltaSquared = utils.invModBig(tmp, publicKey.ns);

    }

    //TODO: here is so much casted stuff

    BigInteger lambda(int i) {

        Set<Integer> sPrime = new HashSet<>(S);
        sPrime.remove(i);

        //int l = (int) (publicKey.delta % publicKey.nsm);
        BigInteger l =  publicKey.delta.mod(publicKey.nsm);

        for (int temp : sPrime) {
            l = (l.multiply( BigInteger.valueOf(temp).multiply(utils.invModBig(BigInteger.valueOf(temp - i), publicKey.nsm)))).mod( publicKey.nsm);
        }
        return l;
    }

    BigInteger Lfunc(BigInteger b, int n) {
        return utils.floorDiv((b.subtract(BigInteger.ONE)), BigInteger.valueOf(n));
    }

    BigInteger nPow(int p, int n) {
        return BigInteger.valueOf(n).pow(p);
    }

    long fact(int k) {
        return CombinatoricsUtils.factorial(k);
    }


    BigInteger damgardJurikReduce(BigInteger a, int s, int n) {
        //Computes i given a = (1 + n)^i (mod n^(s+1)).

        BigInteger i = BigInteger.ZERO;
        for (int j = 1; j < s + 1; j++) {
            BigInteger t1 = Lfunc(a.mod(nPow(j+1, n)), n);
            BigInteger t2 = i;

            for (int k = 2; k < j + 1; k++) {
                i = i.subtract(BigInteger.ONE);
                t2 = (t2.multiply(i)).mod(nPow(j, n));
                //t1 = t1 - (t2 * nPow(k - 1, n) * utils.invModBig(BigInteger.valueOf(fact(k)),  nPow(j, n)) % nPow(j, n));
                BigInteger tmp = t2.multiply(nPow(k-1, n));
                BigInteger tmp2 = utils.invModBig(BigInteger.valueOf(fact(k)), nPow(j, n));
                t1 = t1.subtract(tmp.multiply(tmp2).mod(nPow(j,n)));

            }
            i = t1;
        }
        return i;
    }


    public int decrypt(EncryptedNumber c) {
      /*:param c: An EncryptedNumber.
      :return: An integer containing the decryption of `c`.
     """
       # Use PrivateKeyShares to decrypt
         */
        List<Integer> cList = new LinkedList();
        for (int i = 0; i < privateKeyShareList.size(); i++) {
            cList.add(privateKeyShareList.get(i).decrypt(c));
        }

        //decrypt the whole thing
        BigInteger cPrime = BigInteger.valueOf(1);

        for (int j = 0; j < iList.size(); j++) {
            //preparation
            BigInteger cJ = BigInteger.valueOf(cList.get(j));
            BigInteger lam2 = lambda(iList.get(j)).multiply(BigInteger.valueOf(2));

            cPrime = (cPrime.multiply(cJ.modPow(lam2, publicKey.ns1))).mod(publicKey.ns1);
        }

        cPrime = damgardJurikReduce(cPrime, publicKey.s, publicKey.n.intValue());

        BigInteger m = cPrime.multiply(invFourDeltaSquared).mod(publicKey.ns);

        return m.intValue();
    }

    @Override
    public String toString() {
        return "PrivateKeyRing{" +
                "publicKey=" + publicKey +
                ", privateKeyShareList=" + privateKeyShareList +
                '}';
    }
}

package com.company;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class PrivateKeyRing {

    Utils utils;
    PublicKey publicKey;
    List<PrivateKeyShare> privateKeyShareList;
    List<Integer> iList = new LinkedList<>();
    Set<Integer> S;
    int invFourDeltaSquared;

    void init(List<PrivateKeyShare> keyShareList) throws Exception {
        //Initializes the PrivateKeyRing,
        // checks that enough PrivateKeyShares are provided,
        // and performs pre-computations.

        if (keyShareList.size() == 0) {
            throw new Exception("Must have at least one PrivateKeyShare");
        }
        List<byte[]> test = new LinkedList<byte[]>();
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

        int tmp = (int) (4 * Math.pow(publicKey.delta, 2));
        invFourDeltaSquared = utils.invMod(tmp, publicKey.ns);

    }

    int lambda(int i) {

        Set<Integer> sPrime = new HashSet<>(S);
        sPrime.remove(i);

        int l = publicKey.delta % publicKey.nsm;

        for (int temp : sPrime) {
            l = l * temp * utils.invMod(temp - i, publicKey.nsm) % publicKey.nsm;
        }
        return l;
    }

    int Lfunc(int b, int n) {
        return Math.floorDiv(b - 1, n);
    }

    int nPow(int p, int n) {
        return (int) Math.pow(p, n);
    }

    int fact(int k) {
        return (int) CombinatoricsUtils.factorial(k);
    }


    int damgardJurikReduce(int a, int s, int n) {
        //Computes i given a = (1 + n)^i (mod n^(s+1)).

        int i = 0;
        for (int j = 1; j < s + 1; j++) {
            int t1 = Lfunc(a % nPow(j + 1, n), n);
            int t2 = i;

            for (int k = 2; k < j + 1; k++) {
                i = i - 1;
                t2 = t2 * i % nPow(j, n);
                t1 = t1 - (t2 * nPow(k - 1, n) * utils.invMod(fact(k), nPow(j, n)) % nPow(j, n));

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
            BigInteger lam2 = BigInteger.valueOf(2 * lambda(iList.get(j)));

            cPrime = (cPrime.multiply(cJ.modPow(lam2, BigInteger.valueOf(publicKey.ns1)))).mod(BigInteger.valueOf(publicKey.ns1));
        }

        cPrime = BigInteger.valueOf(damgardJurikReduce(cPrime.intValue(), publicKey.s, publicKey.n));

        BigInteger m = cPrime.multiply(BigInteger.valueOf(invFourDeltaSquared)).mod(BigInteger.valueOf(publicKey.ns));

        return m.intValue();
    }
}

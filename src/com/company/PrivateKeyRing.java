package com.company;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class PrivateKeyRing {

    Utils utils = new Utils();
    PublicKey publicKey;
    List<PrivateKeyShare> privateKeyShareList;
    List<Integer> iList = new LinkedList<>();
    public Set<Integer> S;
    BigInteger invFourDeltaSquared;

    void init(List<PrivateKeyShare> keyShareList) throws Exception {
        //Initializes the PrivateKeyRing,
        // checks that enough PrivateKeyShares are provided,
        // and performs pre-computations.

        if (keyShareList.size() == 0) {
            throw new Exception("Must have at least one PrivateKeyShare");
        }
        List<String> test = new LinkedList<>();
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
        //System.out.println("delta is " + publicKey.delta);
        //System.out.println("tmp is: " + tmp);
        //invFourDeltaSquared = utils.invModBig(tmp, publicKey.ns);
        invFourDeltaSquared = tmp.modInverse(publicKey.ns);
        //System.out.println("directly after invmod ");

        //System.out.println("iList: " + iList);
    }

    //TODO: here is so much casted stuff

    public BigInteger lambda(int i) {

        List<Integer> sPrime = new LinkedList<>(S);
        sPrime.remove(i-1);

        BigInteger l =  publicKey.delta.mod(publicKey.nsm);

        for (int j = 0; j < sPrime.size(); j++) {
            BigInteger iPrime = BigInteger.valueOf(sPrime.get(j));
            BigInteger tmp = iPrime.subtract(BigInteger.valueOf(i)).modInverse(publicKey.nsm);
            l = l.multiply(iPrime).multiply(tmp).mod(publicKey.nsm);
        }
        return l;
    }

    BigInteger Lfunc(BigInteger b, BigInteger n) {
        //System.out.println("L Inputs: " + b + " " + n);
        if(!(b.subtract(BigInteger.ONE).mod(n)).equals(BigInteger.ZERO)){
            throw new Error("L reported error");
        }
        return utils.floorDiv((b.subtract(BigInteger.ONE)), n);
    }

    BigInteger nPow(int p, BigInteger n) {
        return n.pow(p);
    }


    public static BigInteger factorial(int number) {
        BigInteger factorial = BigInteger.ONE;

        for (int i = number; i > 0; i--) {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }

        return factorial;
    }


    public BigInteger damgardJurikReduce(BigInteger a, int s, BigInteger n) {
        //Computes i given a = (1 + n)^i (mod n^(s+1)).

        BigInteger i = BigInteger.ZERO;
        for (int j = 1; j < s + 1; j++) {
            BigInteger t1 = Lfunc(a.mod(nPow(j+1, n)), n);
            BigInteger t2 = i;

            for (int k = 2; k < j + 1; k++) {
                i = i.subtract(BigInteger.ONE);
                t2 = (t2.multiply(i)).mod(nPow(j, n));
                BigInteger tmp = t2.multiply(nPow(k-1, n));
                BigInteger tmp3 = factorial(k).modInverse(nPow(j, n));
                //t1 = t1.subtract(tmp.multiply(tmp2).mod(nPow(j,n)));
                t1 = t1.subtract(tmp.multiply(tmp3)).mod(nPow(j,n));

            }
            i = t1;
        }
        return i;
    }


    public BigInteger decrypt(EncryptedNumber c) {
      /*:param c: An EncryptedNumber.
      :return: An integer containing the decryption of `c`.
     """
       # Use PrivateKeyShares to decrypt
         */
        List<BigInteger> cList = new LinkedList();
        for (int i = 0; i < privateKeyShareList.size(); i++) {
            cList.add(privateKeyShareList.get(i).decrypt(c));
        }

        //System.out.println("cList: " +cList);
        //decrypt the whole thing
        BigInteger cPrime = BigInteger.ONE;

        for (int j = 0; j < iList.size(); j++) {
            //preparation
            BigInteger cJ = cList.get(j);
            BigInteger lam2 = lambda(iList.get(j)).multiply(BigInteger.valueOf(2));

            cPrime = (cPrime.multiply(cJ.modPow(lam2, publicKey.ns1))).mod(publicKey.ns1);
            //System.out.println("cPrime after changing is: " + cPrime);
        }

        cPrime = damgardJurikReduce(cPrime, publicKey.s, publicKey.n);

        BigInteger m = cPrime.multiply(publicKey.invFourDeltaSquared).mod(publicKey.ns);

        //TODO: THIS IS TESTING FOR NEGATIVE NUMBERS:

        if (m.compareTo(publicKey.ns.divide(BigInteger.valueOf(2)))==1){
            m = m.subtract(publicKey.ns);
            //System.out.println("special minus case triggered");
            //System.out.println("ns is: " + publicKey.ns);
        }

        //TODO: TEST END

        return m;
    }

    public BigInteger decrypt(List<BigInteger> cList) {
        //this one is using the partial decryptions as an input
      /*:param c: An EncryptedNumber.
      :return: An integer containing the decryption of `c`.
     """
       # Use PrivateKeyShares to decrypt
         */

        //System.out.println("cList: " +cList);
        //decrypt the whole thing
        BigInteger cPrime = BigInteger.ONE;

        for (int j = 0; j < iList.size(); j++) {
            //preparation
            BigInteger cJ = cList.get(j);
            BigInteger lam2 = lambda(iList.get(j)).multiply(BigInteger.valueOf(2));

            cPrime = (cPrime.multiply(cJ.modPow(lam2, publicKey.ns1))).mod(publicKey.ns1);
            //System.out.println("cPrime after changing is: " + cPrime);
        }

        cPrime = damgardJurikReduce(cPrime, publicKey.s, publicKey.n);

        BigInteger m = cPrime.multiply(publicKey.invFourDeltaSquared).mod(publicKey.ns);

        //TODO: THIS IS TESTING FOR NEGATIVE NUMBERS:

        if (m.compareTo(publicKey.ns.divide(BigInteger.valueOf(2)))==1){
            m = m.subtract(publicKey.ns);
            //System.out.println("special minus case triggered");
            //System.out.println("ns is: " + publicKey.ns);
        }

        //TODO: TEST END

        return m;
    }


    public IntMatrix decryptMatrix(List<IntMatrix> inputMatrices) {


        //TODO: check if x and y are swapped
        BigInteger[][] data = new BigInteger[inputMatrices.get(0).getM()][inputMatrices.get(0).getN()];
        for (int i = 0; i < inputMatrices.get(0).getM(); i++) {
            for (int j = 0; j < inputMatrices.get(0).getN(); j++) {
                List<BigInteger> tmp = new LinkedList<>();
                for (int k = 0; k < privateKeyShareList.size(); k++) {
                    tmp.add(inputMatrices.get(k).getData()[i][j]);
                }
                data[i][j] = decrypt(tmp);
                //data[i][j] = decrypt(inputMatrix.getData()[i][j]);
            }
        }
        return new IntMatrix(data);
    }


    @Override
    public String toString() {
        return "PrivateKeyRing{" +
                "publicKey=" + publicKey +
                ", privateKeyShareList=" + privateKeyShareList +
                '}';
    }

    public IntMatrix decryptMatrix(EncMatrix inputMatrix) {


        List<IntMatrix> parts = new LinkedList<>();
        //TODO: check if x and y are swapped
        BigInteger[][] data = new BigInteger[inputMatrix.M][inputMatrix.N];
        for (int i = 0; i < inputMatrix.M; i++) {
            for (int j = 0; j < inputMatrix.N; j++) {
                data[i][j] = decrypt(inputMatrix.getData()[i][j]);
            }
        }
        return new IntMatrix(data);
    }
}

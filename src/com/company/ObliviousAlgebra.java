package com.company;


import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ObliviousAlgebra {


    Random rnd = new Random();
    List parties = new LinkedList();
    PublicKey publicKey = new PublicKey();


    void secMult(IntMatrix Ml, IntMatrix Mr, int size, PublicKey pK){

        this.publicKey = pK;
        //1.
        for (int i = 0; i < parties.size(); i++) {

            //2.
            long rndArray [][] = new long[size][size];
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    //TODO: nextint?? what bound?
                    rndArray[x][y] = rnd.nextInt();
                }
            }

            IntMatrix Rl = new IntMatrix(rndArray);

            long rndArray2 [][] = new long[size][size];
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    //TODO: nextint?? what bound?
                    rndArray[x][y] = rnd.nextInt();
                }
            }
            IntMatrix Rr = new IntMatrix(rndArray2);

            //3.
            EncMatrix cl = new EncMatrix(Rl, pK);
            EncMatrix cr = new EncMatrix(Rr, pK);

            EncMatrix dr = new EncMatrix(Ml.times(Rr), pK);
            EncMatrix dl = new EncMatrix(Mr.times(Rl), pK);

        //5.
        }

    }

}

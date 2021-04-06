package com.company;

import java.math.BigInteger;

public class Share {

    int X = 0;
    BigInteger fX;


    public Share(Integer pt1, BigInteger pt2) {
        X = pt1;
        fX = pt2;
    }

    @Override
    public String toString() {
        return "Share{" +
                "part1=" + X +
                ", part2=" + fX +
                '}';
    }
}

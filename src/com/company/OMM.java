package com.company;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class OMM {

    int t = 2;
    RealMatrix Mr = new Array2DRowRealMatrix(t,t);
    RealMatrix Ml = new Array2DRowRealMatrix(t,t);

    RealMatrix Rr = new Array2DRowRealMatrix(t,t);
    RealMatrix Rl = new Array2DRowRealMatrix(t,t);
}

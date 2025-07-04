/*
 * Copyright (c) 2010, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.javafx.experiments.importers.maya.values.impl;

import com.javafx.experiments.importers.maya.types.MNurbsCurveType;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.MNurbsCurve;

import java.util.Iterator;

public class MNurbsCurveImpl extends MDataImpl implements MNurbsCurve {
    float[] cvs;
    int degree;
    int dimension;
    int form;
    float[] knots;
    int numCvs;
    int numKnots;
    boolean rational;
    int spans;

    public MNurbsCurveImpl(MNurbsCurveType type) {
        super(type);
    }

    @Override
    public float[] getCVs() {
        return cvs;
    }

    @Override
    public MData getData(int start, int end) {
        return this; // hack?
    }

    @Override
    public int getDegree() {
        return degree;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public int getForm() {
        return form;
    }

    @Override
    public float[] getKnots() {
        return knots;
    }

    @Override
    public int getNumCVs() {
        return numCvs;
    }

    @Override
    public int getNumKnots() {
        return numKnots;
    }

    @Override
    public int getSpans() {
        return spans;
    }

    @Override
    public boolean isRational() {
        return rational;
    }

    @Override
    public void parse(Iterator<String> values) {
        degree = Integer.parseInt(values.next());
        //        System.out.println("degree="+degree);
        spans = Integer.parseInt(values.next());
        //        System.out.println("spans="+spans);
        form = Integer.parseInt(values.next());
        values.next();
        //        rational = tok.equals("yes");
        //        System.out.println("rational="+rational);
        dimension = Integer.parseInt(values.next());
        //        System.out.println("dimension="+dimension);
        numKnots = Integer.parseInt(values.next());
        //        System.out.println("numKnots="+numKnots);
        knots = new float[numKnots];
        for (int i = 0; i < numKnots; i++) {
            knots[i] = Float.parseFloat(values.next());
            //            System.out.println("knot="+knots[i]);
        }
        numCvs = Integer.parseInt(values.next());
        cvs = new float[numCvs * dimension];
        for (int i = 0; i < cvs.length; i++) {
            cvs[i] = Float.parseFloat(values.next());
        }
    }

}

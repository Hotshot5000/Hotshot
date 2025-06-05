/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 7/27/21, 9:32 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource;

import java.util.ArrayList;

/**
 * Created by sebas on 30.11.2015.
 */
public class ComparatorNode {
    public ArrayList<ComparatorNode> leaves;
    public String s;
    public ComparatorOperator op;

    public void set(ComparatorNode comparatorNode) {
        this.leaves = comparatorNode.leaves;
        this.s = comparatorNode.s;
        this.op = comparatorNode.op;
    }
}

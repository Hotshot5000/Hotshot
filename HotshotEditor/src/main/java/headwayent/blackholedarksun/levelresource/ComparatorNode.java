package headwayent.blackholedarksun.levelresource;

import java.util.ArrayList;

/**
 * Created by sebas on 30.11.2015.
 */
public class ComparatorNode {
    public ArrayList<ComparatorNode> leaves;
    public String s;
    public ComparatorOperator op;

    public ComparatorNode() {
    }

    public ArrayList<ComparatorNode> getLeaves() {
        return leaves;
    }

    public String getS() {
        return s;
    }

    public ComparatorOperator getOp() {
        return op;
    }
}

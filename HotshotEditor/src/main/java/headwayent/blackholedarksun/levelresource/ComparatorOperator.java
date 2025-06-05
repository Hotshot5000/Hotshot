package headwayent.blackholedarksun.levelresource;

import java.util.Locale;

/**
 * Created by sebas on 30.11.2015.
 */
public enum ComparatorOperator {
    AND, OR, XOR, NOT;

    public static ComparatorOperator getType(String op) {
        switch (op) {
            case "AND" -> {
                return AND;
            }
            case "OR" -> {
                return OR;
            }
            case "XOR" -> {
                return XOR;
            }
            case "NOT" -> {
                return NOT;
            }
            default -> throw new IllegalStateException("Unexpected value: " + op);
        }
    }
}

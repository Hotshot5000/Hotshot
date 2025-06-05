/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

public class ENG_InputStack {

    private final String name;
    private final String input;
    private final String inputListener;
    private final String inputConvertor;
    private final String inputConvertorListener;

    public ENG_InputStack(String name, String input, String inputListener,
                          String inputConvertor, String inputConvertorListener) {
        this.name = name;
        this.input = input;
        this.inputListener = inputListener;
        this.inputConvertor = inputConvertor;
        this.inputConvertorListener = inputConvertorListener;
    }

    public String getInput() {
        return input;
    }

    public String getInputListener() {
        return inputListener;
    }

    public String getInputConvertor() {
        return inputConvertor;
    }

    public String getInputConvertorListener() {
        return inputConvertorListener;
    }

    public String getName() {
        return name;
    }
}

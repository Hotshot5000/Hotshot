/*
 * Created by Sebastian Bugiu on 6/19/23, 3:46 AM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/19/23, 3:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.exception;

public class ENG_DivisionByZeroException extends ArithmeticException {

    public ENG_DivisionByZeroException() {
    }

    public ENG_DivisionByZeroException(String detailMessage) {
        super(detailMessage);
    }
}

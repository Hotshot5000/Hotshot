/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.exception;

public class ENG_UnsupportedPixelFormatException extends RuntimeException {

    public ENG_UnsupportedPixelFormatException() {

    }

    public ENG_UnsupportedPixelFormatException(String detailMessage) {
        super(detailMessage);

    }

    public ENG_UnsupportedPixelFormatException(Throwable throwable) {
        super(throwable);

    }

    public ENG_UnsupportedPixelFormatException(String detailMessage,
                                               Throwable throwable) {
        super(detailMessage, throwable);

    }

}

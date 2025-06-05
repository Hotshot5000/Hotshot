/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.exception;

public class GameDataException extends RuntimeException {

    public GameDataException() {
        
    }

    public GameDataException(String detailMessage) {
        super(detailMessage);
        
    }

    public GameDataException(Throwable throwable) {
        super(throwable);
        
    }

    public GameDataException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        
    }

}

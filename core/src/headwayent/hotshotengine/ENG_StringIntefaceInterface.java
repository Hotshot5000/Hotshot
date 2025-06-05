/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

/**
 * Instead of using multiple inheritance which we can't we create a getter interface
 * that returns a StringInterface object so that we can have an abstract
 * StringInterface definition. This way we avoid rewriting code by transforming
 * the StringInterface in an Interface when StringInterface actually has
 * callable methods
 *
 * @author sebi
 */
public interface ENG_StringIntefaceInterface {

    ENG_StringInterface getStringInterface();
}

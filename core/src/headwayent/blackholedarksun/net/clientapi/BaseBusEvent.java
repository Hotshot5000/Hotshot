/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:12 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi;

import headwayent.hotshotengine.Bundle;

import java.util.Set;

/**
 * Created by Sebastian on 18.04.2015.
 */
public class BaseBusEvent {

    /** @noinspection deprecation */
    private Bundle extras = new Bundle();
    private String replyTo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BaseBusEvent) {
            BaseBusEvent event = (BaseBusEvent) o;
            return extras == event.getExtras() || !((extras == null && event.getExtras() != null) || (extras != null && event.getExtras() == null)) && (extras != null && event.getExtras() != null && equalBundles(extras, event.getExtras()));
        }
        return false;
    }

    /** @noinspection deprecation */
    public static boolean equalBundles(Bundle one, Bundle two) {
        if (one.size() != two.size())
            return false;

        Set<String> setOne = one.keySet();
        Object valueOne;
        Object valueTwo;

        for (String key : setOne) {
            valueOne = one.get(key);
            valueTwo = two.get(key);
            if (valueOne instanceof Bundle && valueTwo instanceof Bundle &&
                    !equalBundles((Bundle) valueOne, (Bundle) valueTwo)) {
                return false;
            } else if (valueOne == null) {
                if (valueTwo != null || !two.containsKey(key))
                    return false;
            } else if (!valueOne.equals(valueTwo))
                return false;
        }

        return true;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public boolean hasReplyTo() {
        return replyTo != null;
    }

    /** @noinspection deprecation*/
    public Bundle getExtras() {
        return extras;
    }

    /** @noinspection deprecation*/
    public void setExtras(Bundle extras) {
        this.extras = extras;
    }
}

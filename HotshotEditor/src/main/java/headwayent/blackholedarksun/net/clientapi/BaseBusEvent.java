package headwayent.blackholedarksun.net.clientapi;

import headwayent.hotshotengine.Bundle;

import java.util.Set;

/**
 * Created by Sebastian on 18.04.2015.
 */
public class BaseBusEvent {

    private Bundle extras = new Bundle();
    private String replyTo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BaseBusEvent) {
            BaseBusEvent event = (BaseBusEvent) o;
            return extras == event.getExtras() || !((extras == null && event.getExtras() != null) || (extras != null && event.getExtras() == null)) && equalBundles(extras, event.getExtras());
        }
        return false;
    }

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

    public Bundle getExtras() {
        return extras;
    }

    public void setExtras(Bundle extras) {
        this.extras = extras;
    }
}

/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

public class ENG_Controller<T extends ENG_IControllerType<?>> {

    private ENG_IControllerValue<T> source;
    private ENG_IControllerValue<T> dest;
    private ENG_ControllerFunction<T> func;
    private boolean enabled = true;

    public ENG_Controller(ENG_IControllerValue source2,
                          ENG_IControllerValue dest2,
                          ENG_ControllerFunction<T> func) {
        this.source = source2;
        this.dest = dest2;
        this.func = func;
    }

    public void update() {
        if (enabled) {
            dest.setValue(func.calculate(source.getValue()));
        }
    }

    /**
     * @return the source
     */
    public ENG_IControllerValue<?> getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(ENG_IControllerValue<T> source) {
        this.source = source;
    }

    /**
     * @return the dest
     */
    public ENG_IControllerValue<?> getDest() {
        return dest;
    }

    /**
     * @param dest the dest to set
     */
    public void setDest(ENG_IControllerValue<T> dest) {
        this.dest = dest;
    }

    /**
     * @return the func
     */
    public ENG_ControllerFunction<?> getFunc() {
        return func;
    }

    /**
     * @param func the func to set
     */
    public void setFunc(ENG_ControllerFunction<T> func) {
        this.func = func;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

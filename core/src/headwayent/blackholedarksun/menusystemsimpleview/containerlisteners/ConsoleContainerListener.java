/*
 * Created by Sebastian Bugiu on 14/04/2025, 13:56
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 14/04/2025, 13:56
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import headwayent.blackholedarksun.ConsoleCmdHandler;
import headwayent.blackholedarksun.menus.Console;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_Log;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

public class ConsoleContainerListener extends ENG_Container.ContainerListener {

    public static class ConsoleContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "ConsoleMenu";

        @Override
        public ENG_Container.ContainerListener createContainerListener(ENG_Container container, Bundle bundle) {
            return new ConsoleContainerListener(TYPE, container, bundle);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    public ConsoleContainerListener(String type, ENG_Container container, Bundle bundle) {
        super(type, container, bundle);
    }

    @Override
    public void preContainerUpdate() {

    }

    @Override
    public void postContainerUpdate() {
        Console console = (Console) getParentContainer();
        console.forceConsoleUpdate();
    }

    @Override
    public void onActivation() {
        Console console = (Console) getParentContainer();
        console.setConsoleText();
        ConsoleCmdHandler.getInstance().addListener(console.getConsoleListener());
//        ENG_Log.getInstance().log("ConsoleContainerListener onActivation()");
    }

    @Override
    public void onDestruction() {
        Console console = (Console) getParentContainer();
        ConsoleCmdHandler.getInstance().removeListener(console.getConsoleListener());
//        ENG_Log.getInstance().log("ConsoleContainerListener onDestruction()");
    }
}

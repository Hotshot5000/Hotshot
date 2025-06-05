/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/17/21, 12:13 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource.levelevents;

import java.util.ArrayList;

import headwayent.blackholedarksun.HudManager;
import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.levelresource.LevelEventValidator;
import headwayent.blackholedarksun.levelresource.LevelEventValidatorFactory;
import headwayent.hotshotengine.gui.ENG_ScrollOverlayContainer;

public class SpeedPercentageValidator implements LevelEventValidator {

    public static class SpeedPercentageValidatorFactory extends LevelEventValidatorFactory {

        public static final String TYPE = "SpeedPercentageValidator";

        @Override
        public LevelEventValidator createLevelEventValidator(ArrayList<String> paramList) {
            return new SpeedPercentageValidator(paramList);
        }

        @Override
        public int readAhead() {
            return 2;
        }
    }

    private enum Comparison {
        EQUAL, LESS, MORE, EQUAL_LESS, EQUAL_MORE
    }

    private final int percentage;
    private Comparison comparisonOp = Comparison.EQUAL;
    private boolean sticky;

    public SpeedPercentageValidator(ArrayList<String> paramList) {
        percentage = Integer.parseInt(paramList.get(0));
        if (paramList.size() == 2) {
            switch (paramList.get(1)) {
                case "equal":
                    comparisonOp = Comparison.EQUAL;
                    break;
                case "less":
                    comparisonOp = Comparison.LESS;
                    break;
                case "more":
                    comparisonOp = Comparison.MORE;
                    break;
                case "equal_less":
                    comparisonOp = Comparison.EQUAL_LESS;
                    break;
                case "equal_more":
                    comparisonOp = Comparison.EQUAL_MORE;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + paramList.get(1));
            }
        }
    }

    @Override
    public boolean validate(LevelEvent levelEvent) {
        if (sticky) {
            return true;
        }
        HudManager hudManager = HudManager.getSingleton();
        if (hudManager != null) {
            ENG_ScrollOverlayContainer speedScrollOverlay = hudManager.getSpeedScrollOverlay();
            if (speedScrollOverlay != null) {
                int currentPercentage = speedScrollOverlay.getPercentage();
                switch (comparisonOp) {
                    case EQUAL:
                        sticky = percentage == currentPercentage;
                        break;
                    case LESS:
                        sticky = currentPercentage < percentage;
                        break;
                    case MORE:
                        sticky = currentPercentage > percentage;
                        break;
                    case EQUAL_LESS:
                        sticky = currentPercentage <= percentage;
                        break;
                    case EQUAL_MORE:
                        sticky = currentPercentage >= percentage;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + comparisonOp);
                }

            }
        }
        return sticky;
    }
}

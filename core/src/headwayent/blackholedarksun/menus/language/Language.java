/*
 * Created by Sebastian Bugiu on 02/03/2025, 14:37
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 02/03/2025, 14:37
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus.language;

import headwayent.blackholedarksun.MainApp;

public class Language {

    public enum SupportedLanguages {
        ENGLISH("english"), ITALIAN("italiano");

        private final String language;

        SupportedLanguages(String language) {
            this.language = language;
        }

        public static SupportedLanguages getLanguage(String str) {
            if (str.equalsIgnoreCase(ENGLISH.getLanguage())) {
                return ENGLISH;
            } else if (str.equalsIgnoreCase(ITALIAN.getLanguage())) {
                return ITALIAN;
            }
            throw new IllegalArgumentException(str + " is not a valid language");
        }

        public String getLanguage() {
            return language;
        }
    }

    private static final Language language = new Language();
    private SupportedLanguages currentLanguage;

    private Language() {

    }

    public void loadCurrentLanguage() {
        currentLanguage = SupportedLanguages.getLanguage(MainApp.getGame().getPreferences().getLanguage());
        updateLanguageTexts();
    }

    public SupportedLanguages getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(SupportedLanguages currentLanguage) {
        if (currentLanguage == null) {
            throw new NullPointerException("language cannot be null");
        }
        if (this.currentLanguage != currentLanguage) {
            this.currentLanguage = currentLanguage;
            MainApp.getGame().getPreferences().setLanguage(currentLanguage.getLanguage());
            updateLanguageTexts();
        }
    }

    private void updateLanguageTexts() {
        // Update MenuTexts, WeaponData, ShipData, GUI text (??? is that possible with HudManager??).
        MenuTexts.updateLanguage();
    }

    public static Language getSingleton() {
        return language;
    }
}

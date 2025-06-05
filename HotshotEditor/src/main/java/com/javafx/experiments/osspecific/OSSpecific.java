package com.javafx.experiments.osspecific;

public class OSSpecific {

    public enum Platform {
        WIN32,
        MACOSX
    }

    public static final String RESOURCE_PATH_WIN32 = "C:\\Sebi\\projects\\BlackholeDarksunOnline6\\HotshotEditor\\src\\main\\resources\\com\\javafx\\experiments\\jfx3dviewer\\";
    public static final String RESOURCE_PATH_MACOS = "/Users/sebastian/Downloads/BlackholeDarksunOnline6/HotshotEditor/src/main/resources/com/javafx/experiments/jfx3dviewer/";
    private static final Platform platform = Platform.WIN32;

    public static String getSrcResourcePath() {
        switch (platform) {
            case WIN32 -> {
                return "src\\main\\resources\\com\\javafx\\experiments\\jfx3dviewer\\";
            }
            case MACOSX -> {
                return "src/main/resources/com/javafx/experiments/jfx3dviewer/";
            }
            default -> throw new IllegalStateException("Unexpected value: " + platform);
        }
    }

    public static String getResourcePath() {
        switch (platform) {
            case WIN32 -> {
                return "resources\\com\\javafx\\experiments\\jfx3dviewer\\";
            }
            case MACOSX -> {
                return "resources/com/javafx/experiments/jfx3dviewer/";
            }
            default -> throw new IllegalStateException("Unexpected value: " + platform);
        }
    }

    public static String getAbsoluteResourcePath() {
        switch (platform) {
            case WIN32 -> {
                return RESOURCE_PATH_WIN32;
            }
            case MACOSX -> {
                return RESOURCE_PATH_MACOS;
            }
            default -> throw new IllegalStateException("Unexpected value: " + platform);
        }
    }
}

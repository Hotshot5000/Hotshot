/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/24/21, 12:31 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import com.google.common.base.CharMatcher;

public class ENG_StringUtility {

    public static boolean isASCII(char c) {
        return CharMatcher.ascii().matches(c);
    }

    public static boolean isWordCharacter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') ||
                (c >= '0' && c <= '9');
    }

    public static boolean isWordCharacterOrSpace(char c) {
        return isWordCharacter(c) || (c == ' ');
    }

    public static boolean isWordCharacterOrBackspace(char c) {
        return isWordCharacter(c) || (c == '\b');
    }

    public static boolean isWordCharacterOrSpaceOrBackspace(char c) {
        return isWordCharacter(c) || (c == ' ') || (c == '\b');
    }

    public static boolean isSpace(char c) {
        return (c == ' ');
    }

    public static boolean isBackspace(char c) {
        return (c == '\b');
    }

    public static String getStringBetweenChars(String s, String beginChar, String endChar) {
        return s.substring(s.indexOf(beginChar) + 1, s.indexOf(endChar));
    }

    public static String wrapWithNewLines(String s) {
        return wrapWithString(s, "\n", "\n");
    }

    public static String wrapWithString(String s, String begin, String end) {
        return begin + s + end;
    }
}

package org.example.paperreview.util;

import java.util.*;
import java.util.regex.Pattern;

public class PatternMatcher {

    public static boolean matchesPattern(String text, String pattern) {
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text).find();
    }

    public static List<String> extractAllMatches(String text, String pattern) {
        List<String> matches = new ArrayList<>();
        var matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    public static String extractFirstMatch(String text, String pattern) {
        var matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
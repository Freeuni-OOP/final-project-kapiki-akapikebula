package com.kapiki_akapikebula.app.service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MatchKeyBuilder {

    private static final Set<String> STOP_WORDS = Set.of(
            "with", "only", "version", "edition", "new", "original",
            "eu", "us", "uk", "cis", "international", "global",
            "free", "dos", "and", "the", "for",
            "ლეპტოპი", "სმარტფონი", "პლანშეტი", "მაცივარი"
    );

    private static final Map<String, String> SYNONYMS = Map.of(
            "grey", "gray",
            "controllers", "controller",
            "dualsense", "controller",
            "two", "2"
    );

    private static final Set<String> QUALIFIER_WORDS = Set.of(
            "pro", "max", "plus", "mini", "ultra", "se", "lite", "air"
    );

    private static final Pattern CAPACITY_PATTERN = Pattern.compile("^\\d+(gb|tb)$");

    private static final String SPLIT_REGEX = "[\\s/|,._]+";

    private static final double MATCH_THRESHOLD = 0.75;
    private static final int MIN_MATCHED_TOKENS = 3;

    // --- Public API ------------------------------------------------------

    public static List<String> tokenize(String raw) {
        if (raw == null || raw.isBlank()) return List.of();

        String cleaned = raw.toLowerCase()
                .replaceAll("e-sim|e sim", "esim");

        return Arrays.stream(cleaned.split(SPLIT_REGEX))
                .map(t -> SYNONYMS.getOrDefault(t, t))
                .filter(t -> t.length() > 1)
                .filter(t -> !STOP_WORDS.contains(t))
                .toList();
    }

    public static String pickSearchToken(String raw) {
        List<String> tokens = tokenize(raw);

        String longest = tokens.stream()
                .filter(t -> !CAPACITY_PATTERN.matcher(t).matches())
                .filter(t -> t.chars().anyMatch(Character::isLetter))
                .max(Comparator.comparingInt(String::length))
                .orElseGet(() -> tokens.stream()
                        .max(Comparator.comparingInt(String::length))
                        .orElse(""));

        return longest.length() > 8 ? longest.substring(0, 8) : longest;
    }

    public static double similarityScore(String nameA, String nameB) {
        List<String> tokensA = tokenize(nameA);
        List<String> tokensB = tokenize(nameB);

        if (tokensA.isEmpty() || tokensB.isEmpty()) return 0.0;

        Set<String> capA = capacityTokens(tokensA);
        Set<String> capB = capacityTokens(tokensB);
        if (!capA.isEmpty() && !capB.isEmpty() && capA.stream().noneMatch(capB::contains)) {
            return 0.0;
        }

        if (!qualifierTokens(tokensA).equals(qualifierTokens(tokensB))) {
            return 0.0;
        }

        int matches = countMatches(tokensA, tokensB);

        int smallerSide = Math.min(tokensA.size(), tokensB.size());
        return smallerSide == 0 ? 0.0 : (double) matches / smallerSide;
    }

    public static boolean isMatch(String nameA, String nameB) {
        int matches = countMatches(tokenize(nameA), tokenize(nameB));
        return matches >= MIN_MATCHED_TOKENS && similarityScore(nameA, nameB) >= MATCH_THRESHOLD;
    }

    // --- Internal helpers --------------------------------------------------

    private static Set<String> capacityTokens(List<String> tokens) {
        return tokens.stream()
                .filter(t -> CAPACITY_PATTERN.matcher(t).matches())
                .collect(Collectors.toSet());
    }

    private static Set<String> qualifierTokens(List<String> tokens) {
        return tokens.stream().filter(QUALIFIER_WORDS::contains).collect(Collectors.toSet());
    }

    private static boolean tokensMatch(String a, String b) {
        if (a.equals(b)) return true;
        boolean eitherHasDigit = a.chars().anyMatch(Character::isDigit)
                || b.chars().anyMatch(Character::isDigit);
        if (!eitherHasDigit) return false;
        return a.contains(b) || b.contains(a);
    }

    private static int countMatches(List<String> tokensA, List<String> tokensB) {
        List<String> remainingB = new ArrayList<>(tokensB);
        int matches = 0;

        for (String a : tokensA) {
            int idx = -1;
            for (int i = 0; i < remainingB.size(); i++) {
                if (tokensMatch(a, remainingB.get(i))) { idx = i; break; }
            }
            if (idx >= 0) {
                matches++;
                remainingB.remove(idx);
            }
        }
        return matches;
    }
}
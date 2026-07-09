package com.kapiki_akapikebula.app.service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// Decides whether two raw product names (scraped from different stores)
// most likely refer to the same physical product.
//
// Core idea: split both names into normalized tokens, then measure overlap
// relative to the SHORTER name's token count (not the combined total —
// see comment on similarityScore for why). Two hard gates override
// everything else, even a high overlap score:
//   1. Conflicting storage/RAM size  -> different variant, not a match
//   2. Conflicting tier word (Pro/Max/Mini...) -> different product, not a match
public class MatchKeyBuilder {

    // Words that show up in one store's name but carry no identifying info
    private static final Set<String> STOP_WORDS = Set.of(
            "with", "only", "version", "edition", "new", "original",
            "eu", "us", "uk", "cis", "international", "global",
            "free", "dos", "and", "the", "for",
            "ლეპტოპი", "სმარტფონი", "პლანშეტი", "მაცივარი"
    );

    // Different spellings/words that mean the same thing
    private static final Map<String, String> SYNONYMS = Map.of(
            "grey", "gray",
            "controllers", "controller",
            "dualsense", "controller",
            "two", "2"
    );

    // Words that mark a genuinely different product tier — if only one
    // side has one of these, they are NOT the same product
    private static final Set<String> QUALIFIER_WORDS = Set.of(
            "pro", "max", "plus", "mini", "ultra", "se", "lite", "air"
    );

    // Matches storage/RAM sizes like "256gb", "1tb", "12gb"
    private static final Pattern CAPACITY_PATTERN = Pattern.compile("^\\d+(gb|tb)$");

    // Split on whitespace, slash, pipe, comma, underscore, period.
    // Hyphens are NOT split on — this keeps model codes like
    // "SM-R390NZAACIS" or "FX607VJB-RL103" as one token.
    private static final String SPLIT_REGEX = "[\\s/|,._]+";

    private static final double MATCH_THRESHOLD = 0.75;
    private static final int MIN_MATCHED_TOKENS = 3; // guards against 1-2 word coincidental matches

    // --- Public API ------------------------------------------------------

    // "Apple iPhone 17 Pro e-SIM Only | 256GB Cosmic Orange"
    //   -> [apple, iphone, 17, pro, esim, 256gb, cosmic, orange]
    public static List<String> tokenize(String raw) {
        if (raw == null || raw.isBlank()) return List.of();

        String cleaned = raw.toLowerCase()
                .replaceAll("e-sim|e sim", "esim"); // fold "e-SIM"/"e sim" into one word first

        return Arrays.stream(cleaned.split(SPLIT_REGEX))
                .map(t -> SYNONYMS.getOrDefault(t, t))
                .filter(t -> t.length() > 1)
                .filter(t -> !STOP_WORDS.contains(t))
                .toList();
    }

    // Picks one word to use as a DB search filter, so we don't have to
    // compare a new listing against every row in the products table.
    // Prefers the longest letter-containing word (skipping pure numbers
    // and storage sizes). Long model codes get truncated to 8 characters
    // so the search still finds a match even if one store appends extra
    // characters to the code (e.g. "fx607vjb-rl103" vs "fx607vjb-rl10316" —
    // both truncate to "fx607vjb").
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

    // Score from 0.0 (nothing in common) to 1.0 (essentially identical)
    public static double similarityScore(String nameA, String nameB) {
        List<String> tokensA = tokenize(nameA);
        List<String> tokensB = tokenize(nameB);

        if (tokensA.isEmpty() || tokensB.isEmpty()) return 0.0;

        // Gate 1: conflicting storage/RAM size = different variant
        Set<String> capA = capacityTokens(tokensA);
        Set<String> capB = capacityTokens(tokensB);
        if (!capA.isEmpty() && !capB.isEmpty() && capA.stream().noneMatch(capB::contains)) {
            return 0.0;
        }

        // Gate 2: conflicting tier word (Pro vs Pro Max) = different product
        if (!qualifierTokens(tokensA).equals(qualifierTokens(tokensB))) {
            return 0.0;
        }

        int matches = countMatches(tokensA, tokensB);

        // Score against the SHORTER side's token count, not the combined total.
        // Reason: one store sometimes packs a full spec sheet into the name
        // (CPU, GPU, RAM...) while the other keeps it short. Comparing against
        // the combined total would unfairly punish that length difference even
        // when every word from the short side is genuinely present in the long one.
        int smallerSide = Math.min(tokensA.size(), tokensB.size());
        return smallerSide == 0 ? 0.0 : (double) matches / smallerSide;
    }

    // Final yes/no — score above threshold AND enough tokens actually matched
    // (the count guard stops two very short, generic names from scoring 100%
    // by coincidence)
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

    // Two tokens count as the same if identical, or if one contains the other
    // AND both include a digit — this catches "r390" being folded into a
    // longer code like "sm-r390nzaacis"
    private static boolean tokensMatch(String a, String b) {
        if (a.equals(b)) return true;
        boolean eitherHasDigit = a.chars().anyMatch(Character::isDigit)
                || b.chars().anyMatch(Character::isDigit);
        if (!eitherHasDigit) return false;
        return a.contains(b) || b.contains(a);
    }

    // Greedily pairs tokens between the two lists, each one used at most once
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
package com.example.jeffrey.springcloudtask.computation;

import com.example.jeffrey.springcloudtask.SpringCloudTaskApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class LengthyWork {
    private static Logger LOGGER = LoggerFactory.getLogger(SpringCloudTaskApplication.class);

    public static void testSherlockAndAnagrams() {
        List<String> sInputList = new ArrayList<>();
//        sInputList.add("kkkk:10");
//        sInputList.add("abba:4");
//        sInputList.add("abcd:0");
//        sInputList.add("ifailuhkqq:3");
//        sInputList.add("cdcd:5");
        sInputList.add("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa:166650");
        sInputList.add("bbcaadacaacbdddcdbddaddabcccdaaadcadcbddadababdaaabcccdcdaacadcababbabbdbacabbdcbbbbbddacdbbcdddbaaa:4832");
        sInputList.add("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa:166650");
        sInputList.add("cacccbbcaaccbaacbbbcaaaababcacbbababbaacabccccaaaacbcababcbaaaaaacbacbccabcabbaaacabccbabccabbabcbba:13022");
        sInputList.add("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa:166650");
        sInputList.add("bbcbacaabacacaaacbbcaabccacbaaaabbcaaaaaaaccaccabcacabbbbabbbbacaaccbabbccccaacccccabcabaacaabbcbaca:9644");
        sInputList.add("cbaacdbaadbabbdbbaabddbdabbbccbdaccdbbdacdcabdbacbcadbbbbacbdabddcaccbbacbcadcdcabaabdbaacdccbbabbbc:6346");
        sInputList.add("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa:166650");
        sInputList.add("babacaccaaabaaaaaaaccaaaccaaccabcbbbabccbbabababccaabcccacccaaabaccbccccbaacbcaacbcaaaaaaabacbcbbbcc:8640");
        sInputList.add("bcbabbaccacbacaacbbaccbcbccbaaaabbbcaccaacaccbabcbabccacbaabbaaaabbbcbbbbbaababacacbcaabbcbcbcabbaba:11577");

        List<Integer> sOutputList = new ArrayList<>();
        Map<String, Integer> cachedResultMap = new HashMap<>();

        sInputList.forEach(string -> {
            String key = string.split(":")[0];

            Integer cachedResult = cachedResultMap.get(key);
            int result;
            if (cachedResult == null) {
                result = sherlockAndAnagrams(key);
                cachedResultMap.put(key,result);
            } else {
                result = cachedResult.intValue();
            }
            sOutputList.add(result);
        });

        for (int i=0; i<sOutputList.size(); i++) {
            Integer count = sOutputList.get(i);
            Integer expectedCount = Integer.valueOf(sInputList.get(i).split(":")[1]);
            LOGGER.info("count: {}", Integer.valueOf(count));
            LOGGER.info("expected count: {}", Integer.valueOf(expectedCount));
        }
    }

    // Complete the sherlockAndAnagrams function below.
    static int sherlockAndAnagrams(String s) {
        List<String> stringLetterList = s.toLowerCase().chars().mapToObj(c -> Character.toString((char)c)).collect(Collectors.toList());

        Patterns patterns = scanPattern(s);
        Map<String, Set<Integer>> patternMap = patterns.patternMap;
        Map<String, char[]> patternLetterArrayMap = patterns.patternLetterArrayMap;

        Map<String, Set<String>> anagramsPairMap = new HashMap<>();

        Map<String, char[]> cachedCharArrayMap = new HashMap<>();

        patternMap.forEach((searchPattern, searchPatternPositionSet) -> {
            //System.out.println("searching for pattern: " + searchPattern);

            char [] searchPatternCharArray = patternLetterArrayMap.get(searchPattern);

            searchPatternPositionSet.forEach(searchPatternPosition -> {
                Map<String, Set<String>> tempAnagramsPairMap = searchAnagram(
                        s,
                        stringLetterList,
                        searchPattern,
                        searchPatternPosition,
                        searchPatternCharArray,
                        cachedCharArrayMap);

                // validate position set to remove duplicate
                tempAnagramsPairMap.forEach((key, value) -> {
                    String tempAnagramPair = key;
                    Set<String> tempAnagramPairPositionSet = value;

                    Set<String> anagramPairPositionSet = anagramsPairMap.get(key);

                    if (anagramPairPositionSet == null) {
                        // retry with reversed key
                        String key1 = key.split(",")[0];
                        String key2 = key.split(",")[1];
                        tempAnagramPair = key2 + "," + key1;
                        anagramPairPositionSet = anagramsPairMap.get(tempAnagramPair);
                        if (anagramPairPositionSet == null) {
                            tempAnagramPair = key;
                        }
                    }

                    for (String tempAnagramPairPosition:tempAnagramPairPositionSet) {
                        if (anagramPairPositionSet == null) {
                            anagramPairPositionSet = new HashSet<>();
                            anagramPairPositionSet.add(tempAnagramPairPosition);
                        } else {
                            // search if the position already exist
                            if (!anagramPairPositionSet.contains(tempAnagramPairPosition)) {
                                // retrying the search by reversing the position
                                int index1 = Integer.parseInt(tempAnagramPairPosition.split(",")[0]);
                                int index2 = Integer.parseInt(tempAnagramPairPosition.split(",")[1]);
                                String tempAnagramPairPositionReversed = index2 + "," + index1;
                                if (!anagramPairPositionSet.contains(tempAnagramPairPositionReversed)) {
                                    anagramPairPositionSet.add(tempAnagramPairPosition);
                                }
                            }
                        }
                    }

                    anagramsPairMap.put(tempAnagramPair, anagramPairPositionSet);
                    tempAnagramPairPositionSet.clear();
                });

                tempAnagramsPairMap.clear();
            });
        });

        long anagramPairsCount = anagramsPairMap.entrySet().stream().map(entrySet -> entrySet.getValue().size()).reduce(0, (lastCount, pairs) -> {
            lastCount = lastCount + pairs;
            return lastCount;
        });

        anagramsPairMap.clear();
        return Long.valueOf(anagramPairsCount).intValue();
    }

    private static Patterns scanPattern(String s) {
        // generate all possible patterns in the string and their appearing position in the string
        Map<String, Set<Integer>> patternMap = new HashMap<>();
        Map<String, char[]> patternLetterArrayMap = new HashMap<>();

        for (int i=1; i<s.length(); i++) {
            for (int j=0; j<s.length(); j++) {
                if (j+i <= s.length()) {
                    String pattern = s.substring(j,j+i);
                    //System.out.println("pattern " + pattern + " found: at (" + String.format("%s,%s",j,j+pattern.length()) + ")");

                    Set <Integer> patternPositionSet = patternMap.get(pattern);
                    if (patternPositionSet == null) {
                        patternPositionSet = new HashSet<>();
                        patternPositionSet.add(Integer.valueOf(j));
                        patternMap.put(pattern, patternPositionSet);

                        char[] patternCharsArray = pattern.toCharArray();
                        Arrays.sort(patternCharsArray);
                        patternLetterArrayMap.put(pattern, patternCharsArray);

                    } else {
                        patternPositionSet.add(Integer.valueOf(j));
                        patternMap.put(pattern, patternPositionSet);
                    }
                } else {
                    break;
                }
            }
        }

        Patterns patterns = new Patterns();
        patterns.patternMap = patternMap;
        patterns.patternLetterArrayMap = patternLetterArrayMap;
        return patterns;
    }

    private static Map<String, Set<String>> searchAnagram(
            String s,
            List<String> stringLetterList,
            String searchPattern,
            Integer searchPatternPosition,
            char[] searchPatternCharsArray,
            Map<String, char[]> cachedCharArrayMap
    ) {
        Map<String, Set<String>> anagramsPairMap = new HashMap<>();

        stringLetterList.stream().reduce(s, (lastString, string) -> {
            int position = s.length() - lastString.length();
            //System.out.println("position: " + position);

            if (lastString.length()>= searchPattern.length()) {
                String candidatePattern = lastString.substring(0, searchPattern.length());

                int searchPatternStartIndex = searchPatternPosition.intValue();
                if (searchPatternStartIndex == position) {
                    // skip if the current position is the search pattern start position
                } else {
                    boolean match = false;

                    char[] candidatePatternCharsArray = cachedCharArrayMap.get(candidatePattern);
                    if (candidatePatternCharsArray==null) {
                        candidatePatternCharsArray = candidatePattern.toCharArray();
                        Arrays.sort(candidatePatternCharsArray);
                        cachedCharArrayMap.put(candidatePattern, candidatePatternCharsArray);
                    }
                    match = Arrays.equals(searchPatternCharsArray, candidatePatternCharsArray);

                    if (match) {
                        //System.out.println("anagram pair found at position: " + position);
                        //System.out.println("pattern: " + searchPattern);
                        //System.out.println("anagram: " + candidatePattern);

                        // generate the anagram pair string
                        String anagramPair = searchPattern + "," + candidatePattern;
                        String anagramPairReversed = candidatePattern + "," + searchPattern;

                        // generate the anagram pair position
                        String anagramPairsPosition = searchPatternPosition + "," + position;
                        String anagramPairsPositionReversed = position + "," + searchPatternPosition;

                        Set<String> positionSet = anagramsPairMap.get(anagramPair);
                        if (positionSet != null) {
                            // validate if reversed anagram position already exist
                            if (!positionSet.contains(anagramPairsPosition)) {
                                positionSet.add(anagramPairsPosition);
                                anagramsPairMap.put(anagramPair, positionSet);
                            } else {
                                // reversed position is considered duplicated
                                if (!positionSet.contains(anagramPairsPositionReversed)) {
                                    positionSet.add(anagramPairsPosition);
                                    anagramsPairMap.put(anagramPair, positionSet);
                                }
                            }

                        } else {
                            // validate if reversed anagram pair already exist
                            positionSet = anagramsPairMap.get(anagramPairReversed);
                            if (positionSet == null) {
                                positionSet = new HashSet<>();
                                positionSet.add(anagramPairsPosition);
                                anagramsPairMap.put(anagramPair, positionSet);
                            }
                        }
                    }
                }
            }

            lastString = lastString.substring(1);
            return lastString;
        });

        return anagramsPairMap;
    }

    private static class Patterns {
        Map<String, Set<Integer>> patternMap;
        Map<String, char[]> patternLetterArrayMap;
    }
}

package com.ttp.client.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class Utils {

    private Utils() {}

    public static String beautifyText(String text, int limit) {

        List<String> lines = Arrays.stream(text.split(System.lineSeparator())).toList();
        List<String> newLines = new LinkedList<>();

        for (String line : lines) {

            if (line.length() < limit) {
                newLines.add(line);
            } else {
                List<String> words = Arrays.stream(line.split(" ")).toList();
                List<String> newWords = new LinkedList<>();
                int totalLength = 0;

                for (String word : words) {

                    totalLength += word.length();

                    if (totalLength < limit) {
                        newWords.add(word);
                    } else {

                        newLines.add(
                                String.join(" ", newWords)
                        );

                        totalLength = 0;
                        newWords.clear();
                        newWords.add(word);
                    }

                }

                newLines.add(
                        String.join(" ", newWords) + " "
                );
            }

        }

        return String.join(System.lineSeparator(), newLines);
    }

}

package ru.fmtk.hlystov.repl.stage6.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenaizer {
    private Pattern pattern;
    private Class[] expTypes;

    public Tokenaizer(String[] tokenPatterns, Class[] expTypes) {
        if (tokenPatterns != null && tokenPatterns.length > 0) {
            String stringPattern = "(" + String.join(")|(", tokenPatterns) + ")|(\\s+)";
            pattern = Pattern.compile(stringPattern);
            if (expTypes != null && expTypes.length >= tokenPatterns.length) {
                this.expTypes = expTypes;
            }
        }
    }

    /**
     * Returns null if there is an error character in the line.
     * Returns Token list otherwise.
     **/
    public List<Token> tokenize(String line) {
        if (expTypes != null && line != null && !line.isEmpty() && pattern != null) {
            List<Token> result = new ArrayList<>();
            Matcher matcher = pattern.matcher(line);
            int end = 0;
            while (matcher.find(end)) {
                String text = matcher.group(0);
                if (text == null || matcher.end() - end > text.length()) {
                    return null; // there are unwaiting characters
                }
                int groupIndex = groupIndex(matcher);
                if(groupIndex >= 0) {
                    result.add(new Token(matcher.group(0),
                            expTypes[groupIndex]));
                }
                end = matcher.end();
            }
            return result;
        }
        return null;
    }

    private int groupIndex(Matcher matcher) {
        int count = matcher.groupCount();
        // count is the spaces group
        for (int i = 1; i < count; ++i) {
            if (matcher.group(i) != null) {
                return i - 1;
            }
        }
        return -1;
    }
}

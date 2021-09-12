package com.company;

import java.util.Arrays;
import java.util.HashSet;

public enum TokenType {
    KEYWORD,
    SYMBOL,
    IDENTIFIER,
    INT_CONST,
    STRING_CONST;

    public static HashSet<String> KEYWORDS_SET =
            new HashSet<>(Arrays.asList(getKeywords(Keyword.class)));
    public static HashSet<String> SYMBOL_SET = new HashSet<>(Arrays.asList("{", "}", "(", ")", "[", "]", "-", ",", ";", "+","-","*","/","&","|","<",">","=","-",".","~"));

    public static String[] getKeywords(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name)
                .map(String::toLowerCase)
                .toArray(String[]::new);
    }

}

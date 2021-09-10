package com.company;

import java.util.Arrays;
import java.util.HashSet;

public enum Keyword {
    CLASS,
    METHOD,
    FUNCTION,
    CONSTRUCTOR,
    INT,
    BOOLEAN,
    CHAR,
    VOID,
    VAR,
    STATIC,
    FIELD,
    LET,
    DO,
    IF,
    ELSE,
    WHILE,
    RETURN,
    TRUE,
    FALSE,
    NULL,
    THIS;

    public static HashSet<String> TYPE = new HashSet<>(Arrays.asList("int","char","boolean"));

}

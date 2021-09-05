package com.company;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegex {

        public static void main(String[] args) {
            final String regex = "([().]|[;.]|[\\{\\}.]|[\\[\\].]|['\\w]+|[+.]|[-.]|[\\/.]|[\\\\.]|[*.]|[&.]|[|.]|[=.]|[<.]|[>.]|[,.]|\\\"[^\\\"]*\\\")";
            final String string = "keyboard.readIn(\"salut joni buna tu\") \n\n"
                    + "public static void main(String[] args) {\n\n\n"
                    + "}";

            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(string);

            while (matcher.find()) {
                System.out.println("Full match: " + matcher.group(0));

                for (int i = 1; i <= matcher.groupCount(); i++) {
                    System.out.println("Group " + i + ": " + matcher.group(i));
                }
            }
        }
    }



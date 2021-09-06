package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.company.TokenType.*;

public class JackTokenizer {

    private Scanner scanner;
    private String currentLine;
    private ArrayList<String> tokenList;
    private String currentToken;
    private int positionInTokenList = 0;
    private Writer writer;



    public JackTokenizer(File file) throws IOException {
        scanner = new Scanner(file);
        int pos = file.getName().indexOf('.');
        writer = new FileWriter(file.getName().substring(0,pos) +"T.xml");
        tokenList = new ArrayList<>();
        advance();
    }

    public boolean hasMoreTokens() {
        return scanner.hasNextLine();
    }

    public void advance() {
        currentLine = scanner.nextLine();
        skipComments();
        currentLine = filterInlineComments(currentLine);
        tokenizeString(currentLine);
    }

    private void skipComments() {
        boolean hasWhitespace = true;

        while(hasWhitespace) {
            if(currentLine.trim().startsWith("//") && hasMoreTokens()) {
                currentLine = scanner.nextLine();
            } else if((currentLine.trim().startsWith("/*") || currentLine.trim().startsWith("/**"))
                    && currentLine.contains("*/") && hasMoreTokens()) {

                currentLine = scanner.nextLine();

            } else if(currentLine.trim().startsWith("/*") || currentLine.trim().startsWith("/**")) {
                while(!currentLine.trim().startsWith("*/") && hasMoreTokens()) {
                    currentLine = scanner.nextLine();
                }
                if(hasMoreTokens()) {
                    currentLine = scanner.nextLine();
                }
            } else if(currentLine.trim().equals("") && hasMoreTokens()) {
                currentLine = scanner.nextLine();
            }
            else {
                hasWhitespace = false;
            }
        }

    }

    private void tokenizeString(String str) {

        final String regex = "([().]|[;.]|[\\{\\}.]|[\\[\\].]|['\\w]+|[+.]|[-.]|[\\/.]|[\\\\.]|[*.]|[&.]|[|.]|[=.]|[<.]|[>.]|[,.]|\\\"[^\\\"]*\\\")";

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(str);
        tokenList = new ArrayList<>();
        positionInTokenList = 0;
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                tokenList.add(matcher.group(i));
            }
        }

        System.out.println(tokenList);
    }

    public TokenType tokenType() {
        if (KEYWORDS_SET.contains(currentToken)) {
            return KEYWORD;
        } else if(SYMBOL_SET.contains(currentToken)) {
            return SYMBOL;
        } else if(currentToken.startsWith("\"")) {
            return STRING_CONST;
        } else if(isNumeric(currentToken)) {
            return INT_CONST;
        } else return IDENTIFIER;

    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public String keyword() {
        return currentToken;
    }

    public String symbol() {

        if(currentToken.equals("<")) {
            return "&lt;";
        }
        if(currentToken.equals(">")) {
            return "&gt;";
        }
        if(currentToken.equals("&")) {
            return "&amp;";
        }
        return currentToken;
    }

    public String identifier() {
        if(tokenType() == IDENTIFIER) {
            return currentToken;
        }
        return null;
    }

    public Integer intVal() {
        if(tokenType() == INT_CONST) {
            return Integer.parseInt(currentToken);
        }
        return null;
    }

    public String stringVal() {
        return currentToken.substring(1, currentToken.length() - 1);
    }

    private String filterInlineComments(String command) {

        String filteredCommand = command.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)",
                "");


        return filteredCommand.trim();

    }


    public void setNextToken() {

        if((positionInTokenList > tokenList.size() - 1) && hasMoreTokens()) {
            advance();
        }
        if(tokenList.isEmpty()) {
            currentToken = null;
        } else {
            currentToken = tokenList.get(positionInTokenList);
            positionInTokenList++;
        }
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public String peekAtNextToken() {
        return tokenList.get(positionInTokenList + 1);
    }

    public void testTokenizer() throws IOException {
        writer.write("<tokens>" + "\n  ");
        while(hasMoreTokens()) {
                setNextToken();
                        switch (tokenType()) {
                            case SYMBOL:
                                writer.write("<symbol> " + symbol() + " </symbol>\n  ");
                                break;
                            case IDENTIFIER:
                                writer.write("<identifier> " + identifier() + " </identifier>\n  ");
                                break;
                            case KEYWORD:
                                writer.write("<keyword> " + keyword() + " </keyword>\n  ");
                                break;
                            case INT_CONST:
                                writer.write("<integerConstant> " + intVal() + " </integerConstant>\n  ");
                                break;
                            case STRING_CONST:
                                writer.write("<stringConstant> " + stringVal() + " </stringConstant>\n  ");
                                break;
                        }


        }
        writer.write("</tokens>".trim());
        scanner.close();
        writer.close();

    }

}

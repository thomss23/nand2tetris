package com.company;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import static com.company.TokenType.IDENTIFIER;

public class CompilationEngine {

    private final JackTokenizer jackTokenizer;
    private final Writer writer;

    public CompilationEngine(JackTokenizer input, File file) throws IOException {
        this.jackTokenizer = input;
        int pos = file.getName().indexOf('.');
        writer = new FileWriter(file.getName().substring(0,pos) +".xml");
        jackTokenizer.setNextToken();
    }

    public void compileClass() throws Exception {
        eat("class");
        eatShallowNonTerminalRule();
        eat("{");
        eatShallowNonTerminalRule();

        while(jackTokenizer.hasMoreTokens()) {
            if((jackTokenizer.getCurrentToken().equals("static")
                    || jackTokenizer.getCurrentToken().equals("field")) && jackTokenizer.hasMoreTokens()) {
                compileClassVarDec();
            }
        }

        while(jackTokenizer.hasMoreTokens()) {
            if(jackTokenizer.getCurrentToken().equals("constructor")
                    || jackTokenizer.getCurrentToken().equals("function")
                    || jackTokenizer.getCurrentToken().equals("method")
                    || jackTokenizer.getCurrentToken().equals("void")
                    || jackTokenizer.getCurrentToken().equals("int")
                    || jackTokenizer.getCurrentToken().equals("char")
                    || jackTokenizer.getCurrentToken().equals("boolean")
                    || Character.isUpperCase(jackTokenizer.getCurrentToken().charAt(0))) {// trebuie regandit asta

                compileSubroutine();
            }
        }

        eat("}");

    }
    private void eatShallowNonTerminalRule() throws Exception {
        switch (jackTokenizer.tokenType()) {
            case SYMBOL:
                writer.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
                break;
            case IDENTIFIER:
                writer.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                break;
            case KEYWORD:
                writer.write("<keyword> " + jackTokenizer.keyword() + " </keyword>\n");
                break;
            default:
                throw new Exception("Expected shallow non terminal rule but got " + jackTokenizer.getCurrentToken());
        }
        if(jackTokenizer.hasMoreTokens()) {
            jackTokenizer.setNextToken();
        }
    }
    private void eat(String str) throws Exception {
        if(!Objects.equals(jackTokenizer.getCurrentToken(), str)) {
            throw new Exception("Expected " + jackTokenizer.getCurrentToken() +" but got " + str);
        } else {
            switch (jackTokenizer.tokenType()) {
                case SYMBOL:
                    writer.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
                    break;
                case KEYWORD:
                    writer.write("<keyword> " + jackTokenizer.keyword() + " </keyword>\n");
                    break;
            }
            if(jackTokenizer.hasMoreTokens())
                jackTokenizer.setNextToken();
        }
    }

    public void compileClassVarDec() throws Exception {
        if(jackTokenizer.getCurrentToken().equals("static")) {
            eat("static");
        } else if(jackTokenizer.getCurrentToken().equals("field")) {
            eat("field");
        } else {
            eat("wrongInput");
        }
        eatShallowNonTerminalRule();
        eatShallowNonTerminalRule();
        if(jackTokenizer.getCurrentToken().equals(",")) {
            eat(",");
            while(jackTokenizer.tokenType().equals(IDENTIFIER)) {
                eatShallowNonTerminalRule();
                eat(",");
            }
        }
        eat(";");

    }

    public void compileSubroutine() throws Exception {
        eat(jackTokenizer.getCurrentToken());
        eat(jackTokenizer.getCurrentToken());
        eat("(");
        while(jackTokenizer.getCurrentToken().equals("int")
                || jackTokenizer.getCurrentToken().equals("char")
                || jackTokenizer.getCurrentToken().equals("boolean")
                || Character.isUpperCase(jackTokenizer.getCurrentToken().charAt(0))) {

            compileParameterList();

        }
        eat(")");

        while(jackTokenizer.getCurrentToken().equals("var")) {
            compileVarDec();
        }

        eat(";");

        while (jackTokenizer.getCurrentToken().equals("let")
                || jackTokenizer.getCurrentToken().equals("if")
                || jackTokenizer.getCurrentToken().equals("while")
                || jackTokenizer.getCurrentToken().equals("do")
                || jackTokenizer.getCurrentToken().equals("return")) {

            compileStatements();

        }

        eat("}");


    }

    public void compileParameterList() throws Exception {
        if(!Keyword.TYPE.contains(jackTokenizer.getCurrentToken())) {
            throw new Exception(jackTokenizer.getCurrentToken() + " isn't a valid type");
        }
        eatShallowNonTerminalRule();
        eatShallowNonTerminalRule();
        if (!jackTokenizer.peekAtNextToken().equals(")")) {
            eat(",");
        }
    }

    public void compileVarDec() throws Exception {
        eat("var");

        if(!Keyword.TYPE.contains(jackTokenizer.getCurrentToken())) {
            throw new Exception(jackTokenizer.getCurrentToken() + " isn't a valid type");
        }

        eatShallowNonTerminalRule();
        eatShallowNonTerminalRule();

        if (!jackTokenizer.peekAtNextToken().equals(";")) {
            eat(",");
        }

    }

    public void compileStatements() throws Exception {
        switch (jackTokenizer.getCurrentToken()) {
            case "if":
                compileIf();
                break;
            case "let" :
                compileLet();
                break;
            case "while":
                compileWhile();
                break;
            case "do":
                compileDo();
                break;
            case "return" :
                compileReturn();
                break;
            default:
                throw new Exception(jackTokenizer.getCurrentToken() + " is not a valid statement");
        }
    }

    public void compileDo() throws Exception {
        eat("do");
        eatShallowNonTerminalRule();
        eat(";");

    }

    public void compileLet() {

    }

    public void compileWhile() {

    }

    public void compileReturn() throws Exception {
        eat("return");
        if(!jackTokenizer.peekAtNextToken().equals(";")) {
            compileExpression();
        }
        eat(";");
    }

    public void compileIf() throws Exception {
        eat("if");
        eat("(");


    }

    public void compileExpression() {

    }

    public void compileTerm() {

    }

}

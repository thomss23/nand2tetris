package com.company;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import static com.company.TokenType.IDENTIFIER;

public class CompilationEngine {

    private JackTokenizer jackTokenizer;
    private Writer writer;

    public CompilationEngine(JackTokenizer input, File file) throws IOException {
        this.jackTokenizer = input;
        int pos = file.getName().indexOf('.');
        writer = new FileWriter(file.getName().substring(0,pos) +".xml");
        jackTokenizer.setNextToken();
    }

    public void compileClass() throws Exception {
        eat("class");
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
                    || jackTokenizer.getCurrentToken().equals("boolean")) {// trebuie regandit asta

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

    public void compileSubroutine() {
        //TODO next
    }

    public void compileParameterList() {

    }

    public void compileVarDec() {

    }

    public void compileStatements() {

    }

    public void compileDo() {

    }

    public void compileLet() {

    }

    public void compileWhile() {

    }

    public void compileReturn() {

    }

    public void compileIf() {

    }

    public void compileExpression() {

    }

    public void compileTerm() {

    }

}

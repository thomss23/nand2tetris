package com.company;


import java.io.*;

import static com.company.Keyword.*;

public class CompilationEngine {

    private final JackTokenizer jackTokenizer;
    private final Writer writer;

    public CompilationEngine(JackTokenizer input, String file) throws IOException {
        this.jackTokenizer = input;
        writer = new BufferedWriter(new FileWriter(file.substring(0, file.indexOf(".")) + ".xml"));
        jackTokenizer.setNextToken();
    }

    public void compileClass() throws Exception {
        eat("class");
        eatShallowNonTerminalRule();
        eat("{");
        while(jackTokenizer.hasMoreTokens()) {
            if((jackTokenizer.getCurrentToken().equals("static")
                    || jackTokenizer.getCurrentToken().equals("field")) && jackTokenizer.hasMoreTokens()) {
                compileClassVarDec();
            } else {
                break;
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
                    || TYPE.contains(jackTokenizer.getCurrentToken())) {

                compileSubroutine();
            } else {
                break;
            }
        }

        eat("}");
        close();
        jackTokenizer.close();
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
            case INT_CONST:
                writer.write("<integerConstant> " + jackTokenizer.intVal() + " </integerConstant>\n");
                break;
            case STRING_CONST:
                writer.write("<stringConstant> " + jackTokenizer.stringVal() + " </stringConstant>\n");
                break;
            default:
                throw new Exception("Expected shallow non terminal rule but got " + jackTokenizer.getCurrentToken());
        }
        writer.flush();
        if(jackTokenizer.hasMoreTokens()) {
            jackTokenizer.setNextToken();
        }
    }
    private void eat(String str) throws Exception {
        if(!jackTokenizer.getCurrentToken().equals(str)) {
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
            writer.flush();
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
            while(!jackTokenizer.getCurrentToken().equals(";")) {
                eat(",");
                eatShallowNonTerminalRule();
            }
        }
        eat(";");

    }

    public void compileSubroutine() throws Exception {

        eatShallowNonTerminalRule();
        eatShallowNonTerminalRule();
        eatShallowNonTerminalRule();

        eat("(");

        while(TYPE.contains(jackTokenizer.getCurrentToken())) {

            compileParameterList();

        }

        if(!TYPE.contains(jackTokenizer.getCurrentToken()) && !jackTokenizer.getCurrentToken().equals(")")) {
            throw new Exception(jackTokenizer.getCurrentToken() + "is not a valid type");
        }

        eat(")");

        eat("{");
        compileVarDec();
        compileStatements();
        eat("}");


    }

    public void compileParameterList() throws Exception {
        eatShallowNonTerminalRule();
        eatShallowNonTerminalRule();
        if (!jackTokenizer.getCurrentToken().equals(")")) {
            eat(",");
        }
    }

    //rethink
    public void compileVarDec() throws Exception {
        while(jackTokenizer.getCurrentToken().equals("var")) {
            eat("var");

            if(!TYPE.contains(jackTokenizer.getCurrentToken())) {
                throw new Exception(jackTokenizer.getCurrentToken() + " isn't a valid type");
            }

            eatShallowNonTerminalRule();
            eatShallowNonTerminalRule();

            while (jackTokenizer.getCurrentToken().equals(",")) {
                eat(",");
                eatShallowNonTerminalRule();
            }
            eat(";");
        }

    }

    public void compileStatements() throws Exception {
        while (!jackTokenizer.getCurrentToken().equals("}")) {
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

    }

    public void compileDo() throws Exception {
        eat("do");
        eatShallowNonTerminalRule();

        if(jackTokenizer.getCurrentToken().equals("(")) {
            eat("(");
            compileExpressionList();
            eat(")");
        } else if (jackTokenizer.getCurrentToken().equals(".")) {
            eat(".");
            eatShallowNonTerminalRule();
            eat("(");
            compileExpressionList();
            eat(")");
        }


        eat(";");

    }

    public void compileLet() throws Exception {
        eat("let");
        eatShallowNonTerminalRule();
        if(jackTokenizer.getCurrentToken().equals("[")) {
            eat("[");
            compileExpression();
            eat("]");
        }
        eat("=");
        compileExpression();
        eat(";");
    }

    public void compileWhile() throws Exception {
        eat("while");
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
    }

    public void compileReturn() throws Exception {
        eat("return");
        if(!jackTokenizer.getCurrentToken().equals(";")) {
            compileExpression();
        }
        eat(";");
    }

    public void compileIf() throws Exception {
        eat("if");
        eat("(");
        compileExpression();
        eat(")");
        eat("{");
        compileStatements();
        eat("}");
        if(jackTokenizer.getCurrentToken().equals("else")) {
            eat("else");
            eat("{");
            compileStatements();
            eat("}");
        }

    }

    public void compileExpression() throws Exception {
        compileTerm();
        while(OP.contains(jackTokenizer.getCurrentToken())) {
            eatShallowNonTerminalRule();
            compileTerm();
        }
    }

    public void compileTerm() throws Exception {
        if(jackTokenizer.getCurrentToken().equals("(")) {
            eat("(");
            compileExpression();
            eat(")");
        } else {

            if(UNARY_OP.contains(jackTokenizer.getCurrentToken())) {
                eatShallowNonTerminalRule();
                compileTerm();

            } else {
                eatShallowNonTerminalRule();

                switch (jackTokenizer.getCurrentToken()) {
                    case "[":
                        eat("[");
                        compileExpression();
                        eat("]");
                        break;
                    case ".":
                        eat(".");
                        eatShallowNonTerminalRule();
                        eat("(");
                        compileExpressionList();
                        eat(")");
                        break;
                    case "(":
                        eat("(");
                        compileExpressionList();
                        eat(")");
                        break;
                }
            }

        }
    }

    private void compileExpressionList() throws Exception {
        if(!jackTokenizer.getCurrentToken().equals(")")) {

            compileExpression();

            while(jackTokenizer.getCurrentToken().equals(",")) {
                eat(",");
                compileExpression();
            }
        }

    }

    public void close() throws IOException {
        writer.close();
    }


}

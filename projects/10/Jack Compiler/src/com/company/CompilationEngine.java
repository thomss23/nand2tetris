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
        writer.write("<class>\n");
        eat("class");
        eatShallowNonTerminalRule();
        eat("{");
        while(jackTokenizer.hasMoreTokens()) {
            if((jackTokenizer.getCurrentToken().equals("static")
                    || jackTokenizer.getCurrentToken().equals("field")) && jackTokenizer.hasMoreTokens()) {

                writer.write("<classVarDec>\n");
                compileClassVarDec();
                writer.write("</classVarDec>\n");

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

                writer.write("<subroutineDec>\n");
                compileSubroutine();
                writer.write("</subroutineDec>\n");

            } else {
                break;
            }
        }

        eat("}");

        writer.write("</class>");
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
        
        boolean hasStatements = false;

        eatShallowNonTerminalRule();
        eatShallowNonTerminalRule();
        eatShallowNonTerminalRule();

        eat("(");

        writer.write("<parameterList>\n");
        while(TYPE.contains(jackTokenizer.getCurrentToken())) {

            compileParameterList();

        }
        writer.write("</parameterList>\n");

        if(!TYPE.contains(jackTokenizer.getCurrentToken()) && !jackTokenizer.getCurrentToken().equals(")")) {
            throw new Exception(jackTokenizer.getCurrentToken() + "is not a valid type");
        }

        eat(")");

        writer.write("<subroutineBody>\n");
        eat("{");


        compileVarDec();


        if(jackTokenizer.getCurrentToken().equals("if")
                || jackTokenizer.getCurrentToken().equals("let")
                || jackTokenizer.getCurrentToken().equals("while")
                || jackTokenizer.getCurrentToken().equals("do")
                || jackTokenizer.getCurrentToken().equals("return")) {
            hasStatements = true;
            writer.write("<statements>\n");
        }

        compileStatements();

        if(hasStatements) {
            writer.write("</statements>\n");

        }

        eat("}");
        writer.write("</subroutineBody>\n");


    }

    public void compileParameterList() throws Exception {
        eatShallowNonTerminalRule();
        eatShallowNonTerminalRule();
        if (!jackTokenizer.getCurrentToken().equals(")")) {
            eat(",");
        }
    }

    public void compileVarDec() throws Exception {
        while(jackTokenizer.getCurrentToken().equals("var")) {
            writer.write("<varDec>\n");
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
            writer.write("</varDec>\n");
        }

    }

    public void compileStatements() throws Exception {
        while (!jackTokenizer.getCurrentToken().equals("}")) {
            switch (jackTokenizer.getCurrentToken()) {
                case "if":
                    writer.write("<ifStatement>\n");
                    compileIf();
                    writer.write("</ifStatement>\n");
                    break;
                case "let" :
                    writer.write("<letStatement>\n");
                    compileLet();
                    writer.write("</letStatement>\n");
                    break;
                case "while":
                    writer.write("<whileStatement>\n");
                    compileWhile();
                    writer.write("</whileStatement>\n");
                    break;
                case "do":
                    writer.write("<doStatement>\n");
                    compileDo();
                    writer.write("</doStatement>\n");
                    break;
                case "return" :
                    writer.write("<returnStatement>\n");
                    compileReturn();
                    writer.write("</returnStatement>\n");
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
            writer.write("<expressionList>\n");
            compileExpressionList();
            writer.write("</expressionList>\n");
            eat(")");
        } else if (jackTokenizer.getCurrentToken().equals(".")) {
            eat(".");
            eatShallowNonTerminalRule();
            eat("(");
            writer.write("<expressionList>\n");
            compileExpressionList();
            writer.write("</expressionList>\n");
            eat(")");
        }


        eat(";");

    }

    public void compileLet() throws Exception {
        eat("let");
        eatShallowNonTerminalRule();
        if(jackTokenizer.getCurrentToken().equals("[")) {
            eat("[");

            writer.write("<expression>\n");
            compileExpression();
            writer.write("</expression>\n");

            eat("]");
        }
        eat("=");

        writer.write("<expression>\n");
        compileExpression();
        writer.write("</expression>\n");

        eat(";");
    }

    public void compileWhile() throws Exception {
        eat("while");
        eat("(");


        writer.write("<expression>\n");
        compileExpression();
        writer.write("</expression>\n");

        eat(")");
        eat("{");

        writer.write("<statements>\n");
        compileStatements();
        writer.write("</statements>\n");


        eat("}");
    }

    public void compileReturn() throws Exception {
        eat("return");
        if(!jackTokenizer.getCurrentToken().equals(";")) {

            writer.write("<expression>\n");
            compileExpression();
            writer.write("</expression>\n");

        }

        eat(";");
    }

    public void compileIf() throws Exception {
        eat("if");
        eat("(");

        writer.write("<expression>\n");
        compileExpression();
        writer.write("</expression>\n");

        eat(")");
        eat("{");

        writer.write("<statements>\n");
        compileStatements();
        writer.write("</statements>\n");

        eat("}");

        if(jackTokenizer.getCurrentToken().equals("else")) {
            eat("else");
            eat("{");

            writer.write("<statements>\n");
            compileStatements();
            writer.write("</statements>\n");

            eat("}");
        }

    }

    public void compileExpression() throws Exception {
        writer.write("<term>");
        compileTerm();
        writer.write("</term>");

        while(OP.contains(jackTokenizer.getCurrentToken())) {

            eatShallowNonTerminalRule();

            writer.write("<term>");
            compileTerm();
            writer.write("</term>");


        }

    }

    public void compileTerm() throws Exception {
        if(jackTokenizer.getCurrentToken().equals("(")) {

            eat("(");

            writer.write("<expression>\n");
            compileExpression();
            writer.write("</expression>\n");

            eat(")");

        } else {

            if(UNARY_OP.contains(jackTokenizer.getCurrentToken())) {
                eatShallowNonTerminalRule();

                writer.write("<term>");
                compileTerm();
                writer.write("</term>");


            } else {
                eatShallowNonTerminalRule();

                switch (jackTokenizer.getCurrentToken()) {
                    case "[":
                        eat("[");

                        writer.write("<expression>\n");
                        compileExpression();
                        writer.write("</expression>\n");

                        eat("]");
                        break;
                    case ".":
                        eat(".");
                        eatShallowNonTerminalRule();
                        eat("(");

                        writer.write("<expressionList>");
                        compileExpressionList();
                        writer.write("</expressionList>");

                        eat(")");
                        break;
                    case "(":
                        eat("(");

                        writer.write("<expressionList>");
                        compileExpressionList();
                        writer.write("</expressionList>");


                        eat(")");
                        break;
                }
            }

        }
    }

    private void compileExpressionList() throws Exception {
        if(!jackTokenizer.getCurrentToken().equals(")")) {

            writer.write("<expression>\n");
            compileExpression();
            writer.write("</expression>\n");

            while(jackTokenizer.getCurrentToken().equals(",")) {
                eat(",");

                writer.write("<expression>\n");
                compileExpression();
                writer.write("</expression>\n");

            }
        }

    }

    public void close() throws IOException {
        writer.close();
    }


}

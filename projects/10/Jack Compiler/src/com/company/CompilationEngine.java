package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class CompilationEngine {

    private JackTokenizer jackTokenizer;
    private Writer writer;

    public CompilationEngine(JackTokenizer input, File file) throws IOException {
        this.jackTokenizer = input;
        int pos = file.getName().indexOf('.');
        writer = new FileWriter(file.getName().substring(0,pos) +"T.xml");
    }

    public void compileClass() {
    
    }

    public void compileClassVarDec() {

    }

    public void compileSubroutine() {

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

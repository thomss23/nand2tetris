package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class VMWriter {

    private PrintWriter out;

    public VMWriter(File output) throws FileNotFoundException {
        out = new PrintWriter(output);
    }

    public void writePush(String seg, int idx) {
        out.println("push " + seg + " " + idx);
    }

    public void writePop(String seg, int idx) {
        out.println("pop " + seg + " " + idx);
    }

    public void writeArithmetic(String cmd) {
        out.println(cmd);
    }

    public void writeLabel(String label) {
        out.println("label " + label);
    }

    public void writeGoto(String label) {
        out.println("goto " + label);
    }

    public void writeIf(String label) {
        out.println("if-goto " + label);
    }

    public void writeCall(String name, int nArgs) {
        out.println("call " + name + " " + nArgs);
    }

    public void writeFunction(String name, int nLocals) {
        out.println("function " + name + " " + nLocals);
    }

    public void writeReturn() {
        out.println("return");
    }

    public void close() {
        out.close();
    }
}
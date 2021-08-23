package com.tomsoft;

import java.io.*;
import java.util.Locale;

import static com.tomsoft.CommandType.C_PUSH;
import static com.tomsoft.TemplateFormatBranchingFunctions.*;

public class CodeWriter {

    private BufferedWriter writer;

    private String fileName;

    private int currentLine = 0;

    private static int COUNT_VAR = 1;

    public CodeWriter(File fileName) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(fileName));
        this.fileName = fileName.getName();
    }

    // writes to the output file the assembly code that implements the given arithmetic command
    public void writeArithmetic(String command) throws IOException {

        String format = "@SP\n" +
                "M=M-1\n" +
                "@SP\n" +
                "A=M\n" +
                "D=M\n" +
                "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "%s" +  // add,sub,and,or
                "@SP\n" +
                "M=M+1";

        String formatEqGtLt = "@SP\n" +
                "M=M-1\n" +
                "@SP\n" +
                "A=M\n" +
                "D=M\n" +
                "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "%s" +  // eq,gt,lt
                "(END" + COUNT_VAR + ")\n" +
                "@SP\n" +
                "M=M+1";

        String formatNotNeg = "@SP\n" +
                "M=M-1\n" +
                "@SP\n" +
                "A=M\n" +
                "%s" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1";

        String asmCommand;

        switch (command) {

            case "add":
                asmCommand = String.format(format, addAssembly());
                break;
            case "sub":
                asmCommand = String.format(format, subAssembly());
                break;
            case "eq":
                asmCommand = String.format(formatEqGtLt, eqAssembly());
                break;
            case "gt":
                asmCommand = String.format(formatEqGtLt, gtAssembly());
                break;
            case "lt" :
                asmCommand = String.format(formatEqGtLt, ltAssembly());
                break;
            case "and" :
                asmCommand = String.format(format, andFormat());
                break;
            case "or":
                asmCommand = String.format(format, orFormat());
                break;
            case "neg":
                asmCommand = String.format(formatNotNeg, negFormat());
                break;
            case "not" :
                asmCommand = String.format(formatNotNeg, notFormat());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + command);
        }
        writer.write("//" + command + "\n");
        writer.write(asmCommand + "\n");

    }

    private String negFormat() {
        return "D=-M\n";
    }

    private String notFormat() {
        return "D=!M\n";
    }

    private String orFormat() {
        return "D=D|M\n" +
                "M=D\n";
    }

    private String andFormat() {
        return "D=D&M\n" +
                "M=D\n";
    }

    private String ltAssembly() {
        String result = "D=M-D\n" +
                "@TRUE" + COUNT_VAR +"\n" +
                "D;JLT\n" +
                "@SP\n" +
                "A=M\n" +
                "M=0\n" +
                "@END" + COUNT_VAR +"\n" +
                "0;JMP\n" +
                "(TRUE" + COUNT_VAR + ")\n" +
                "@SP\n" +
                "A=M\n" +
                "M=-1\n";

        COUNT_VAR++;
        return result;
    }

    private String gtAssembly() {
        String result = "D=M-D\n" +
                "@TRUE" + COUNT_VAR +"\n" +
                "D;JGT\n" +
                "@SP\n" +
                "A=M\n" +
                "M=0\n" +
                "@END" + COUNT_VAR +"\n" +
                "0;JMP\n" +
                "(TRUE" + COUNT_VAR + ")\n" +
                "@SP\n" +
                "A=M\n" +
                "M=-1\n";
        COUNT_VAR++;
        return result;
    }

    private String eqAssembly() {
        String result = "D=D-M\n" +
                "@TRUE" + COUNT_VAR +"\n" +
                "D;JEQ\n" +
                "@SP\n" +
                "A=M\n" +
                "M=0\n" +
                "@END" + COUNT_VAR +"\n" +
                "0;JMP\n" +
                "(TRUE" + COUNT_VAR + ")\n" +
                "@SP\n" +
                "A=M\n" +
                "M=-1\n";

        COUNT_VAR++;

        return result;
    }

    private String subAssembly() {
        return "D=M-D\n" +
                "M=D\n";
    }

    private String addAssembly() {
        return "D=D+M\n" +
                "M=D\n";
    }

    // writes to the output file the assembly code that implements the given command,
    // where command is either C_PUSH or C_POP
    public void writePushPop(CommandType commandType, String segment, int index) throws IOException {

        String formatPush = "@" + "%d\n" +
                "D=A\n" +
                "@" + "%s\n" +
                "A=D+M\n" +
                "D=M\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1";

        String formatPushTemp = "@" + "%d\n" +
                "D=A\n" +
                "@" + "%s\n" +
                "A=D+A\n" +
                "D=M\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1";

        String formatPushConst = "@" + "%d\n" +
                "D=A\n" +
                "@" + "%s\n" +
                "D=D+A\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1";

        String formatPushPointer = "@" + "%s\n" +
                "D=M\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1";

        String formatPop = "@" + "%d\n" +
                "D=A\n" +
                "@" + "%s\n" +
                "D=D+M\n" +
                "@addr_" + COUNT_VAR + "\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@addr_" + COUNT_VAR + "\n" +
                "A=M\n" +
                "M=D";

        String formatPopPointer = "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@" + "%s\n" +
                "M=D";

        String formatPopTemp = "@" + "%d\n" +
                "D=A\n" +
                "@" + "%s\n" +
                "D=D+A\n" +
                "@addr_" + COUNT_VAR + "\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@addr_" + COUNT_VAR + "\n" +
                "A=M\n" +
                "M=D";

        String formatPopStatic = "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@" + "%s\n" +
                "M=D";

        String formatPushStatic = "@" + "%s\n" +
                "D=M\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1";

        if(commandType.equals(C_PUSH)) {

            switch (segment) {
                case "constant":
                    writeAsmCommand(segment, index, formatPushConst, commandType);
                    break;
                case "temp":
                    writeAsmCommand(segment, index, formatPushTemp, commandType);
                    break;
                case "pointer":
                    writeAsmCommand(segment, index, formatPushPointer, commandType);
                    break;
                case "static" :
                    writeAsmCommand(segment, index, formatPushStatic, commandType);
                    break;
                default:
                    writeAsmCommand(segment, index, formatPush, commandType);
                    break;
            }



        } else if(commandType.equals(CommandType.C_POP)) {

            switch (segment) {
                case "temp":
                    writeAsmCommand(segment, index, formatPopTemp, commandType);
                    COUNT_VAR++;
                    break;
                case "pointer":
                    writeAsmCommand(segment, index, formatPopPointer, commandType);
                    break;
                case "static":
                    writeAsmCommand(segment, index, formatPopStatic, commandType);
                    break;
                default:
                    writeAsmCommand(segment, index, formatPop, commandType);
                    COUNT_VAR++;
                    break;
            }


        }

    }

    private void writeAsmCommand(String segment, int index, String format, CommandType commandType) throws IOException {
        String asmCommand;

        switch (segment) {

            case "local" :

                asmCommand = String.format(format, index, "LCL");
                break;
            case "argument" :

                asmCommand = String.format(format, index, "ARG");
                break;
            case "this":

                asmCommand = String.format(format, index, "THIS");
                break;
            case "that":

                asmCommand = String.format(format, index, "THAT");
                break;
            case "constant":

                asmCommand = String.format(format, index, "0");
                break;
            case "temp":

                asmCommand = String.format(format, index, "5");
                break;
            case "pointer":

                if(index == 0) {
                    asmCommand = String.format(format, "THIS");
                } else {
                    asmCommand = String.format(format, "THAT");
                }
                break;
            case "static":

                String variable = fileName.replaceFirst("[.][^.]+$", ".") + index;

                asmCommand = String.format(format, variable);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + segment);
        }
        writer.write("//" + commandType.toString().substring(2).toLowerCase(Locale.ROOT)  + " " + segment + " " + index + "\n");
        writer.write(asmCommand + "\n");
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void writeInit() throws IOException {
        writer.write(FORMAT_INIT);
        writeCall("Sys.init", 0);
    }

    public void writeLabel(String label) throws IOException {
        writer.write("// C_LABEL " + label + '\n');
        writer.write("(" + label + ")\n");
    }

    public void writeGoTo(String label) throws IOException {
        writer.write("//  C_GOTO " + label + '\n');
        String asmCommand = String.format(FORMAT_GO_TO, label);
        writer.write(asmCommand);
    }

    public void writeIf(String label) throws IOException {
        writer.write("// C_IF " + label + '\n');
        String asmCommand = String.format(FORMAT_IF, label);
        writer.write(asmCommand);
    }

    public void writeFunction(String functionName, int numArgs) throws IOException {
        writer.write("// function " + functionName + " " + numArgs + '\n');
        writeLabel(functionName);
        for(int i = 0; i < numArgs; i++) {
            writePushPop(C_PUSH, "constant", 0);
        }
    }

    public void writeCall(String functionName, int numArgs) throws IOException {
        writer.write("// call " + functionName + " " + numArgs + '\n');

        String asmCommand = String.format(FORMAT_CALL, COUNT_VAR, numArgs);
        writer.write(asmCommand);
        writeGoTo(functionName);
        writeLabel("return-address" + COUNT_VAR);
        COUNT_VAR++;


    }

    public void writeReturn() throws IOException {
        writer.write("// return\n");
        writer.write(RETURN);
    }

    public void close() throws IOException {
        writer.close();
    }

}

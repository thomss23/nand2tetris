package com.tomsoft;

public class TemplateFormatBranchingFunctions {
    public static String FORMAT_INIT = "@256\n" +
                                        "D=A\n" +
                                        "@SP\n" +
                                        "M=D\n";

    public static String FORMAT_GO_TO =
            "@%s\n" +
            "0;JMP\n";

    public static String FORMAT_IF =
            "@SP\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "@%s\n" +
            "D;JNE\n";

    public static String RETURN = "@LCL\n" + // set frame
            "D=M\n" +
            "@FRAME\n" +
            "M=D\n" +
            // set ret  = frame - 5
            "@5\n" +
            "A=D-A\n" +
            "D=M\n" +
            "@RET\n" +
            "M=D\n" +
            //set ARG=pop()
            "@SP\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "@ARG\n" +
            "A=M\n" +
            "M=D\n" +
            // set SP = ARG + 1
            "@ARG\n" +
            "D=M+1\n" +
            "@SP\n" +
            "M=D\n" +
            // restore THAT
            "@FRAME\n" +
            "A=M-1\n" +
            "D=M\n" +
            "@THAT\n" +
            "M=D\n" +
            // restore THIS
            "@FRAME\n" +
            "D=M\n" +
            "@2\n" +
            "A=D-A\n" +
            "D=M\n" +
            "@THIS\n" +
            "M=D\n" +
            // restore ARG
            "@FRAME\n" +
            "D=M\n" +
            "@3\n" +
            "A=D-A\n" +
            "D=M\n" +
            "@ARG\n" +
            "M=D\n" +
            // restore LCL
            "@FRAME\n" +
            "D=M\n" +
            "@4\n" +
            "A=D-A\n" +
            "D=M\n" +
            "@LCL\n" +
            "M=D\n" +
            //goto RET
            "@RET\n" +
            "A=M\n" +
            "0;JMP\n";

    private static String FINISH_PUSH ="@SP\n" +
            "A=M\n" +
            "M=D\n" +
            "M=M+1\n";

    public static String FORMAT_CALL = "@return-address" + "%d\n" +
            "D=A\n" +
            FINISH_PUSH +
            "@LCL\n" +
            "D=M\n" +
            FINISH_PUSH +
            "@THIS\n" +
            "D=M\n" +
            FINISH_PUSH +
            "@THAT\n" +
            "D=M\n" +
            FINISH_PUSH +
            "@SP\n" +
            "D=M\n" +
            "@" + "%d\n" +
            "D=D-A\n" +
            "@5\n" +
            "D=D-A\n" +
            "@ARG\n" +
            "M=D\n" +
            "@SP\n" +
            "D=M\n" +
            "@LCL\n" +
            "M=D\n";

}

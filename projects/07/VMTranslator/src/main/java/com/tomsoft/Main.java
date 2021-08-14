package com.tomsoft;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.tomsoft.CommandType.*;

public class Main {

    public static void main(String[] args) {

        if (args[0] == null || args[0].trim().isEmpty()) {

            System.out.println("You need to specify a file!");
            return;

        }

        String vmFile = args[0];

        String inputFileNameWithOutExt = vmFile.replaceFirst("[.][^.]+$", "");

        String outputFileName = inputFileNameWithOutExt + ".asm";

        try {

            Parser parser = new Parser(vmFile);

            CodeWriter codeWriter = new CodeWriter(outputFileName);

            while(parser.hasMoreCommands()) {

                parser.advance();

                if(parser.getCurrentCommand() != null) {

                    if(parser.commandType() == C_ARITHEMTIC) {
                        codeWriter.writeArithmetic(parser.getCurrentCommand());
                    }

                    if(parser.commandType() == C_PUSH || parser.commandType() == C_POP) {
                        codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                    }
                }
            }

            parser.closeFile();
            codeWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}

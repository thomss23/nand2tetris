package com.company;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {


        if (args[0] == null || args[0].trim().isEmpty()) {

            System.out.println("You need to specify a file!");

        } else {

            String asmFile = args[0];

            String inputFileNameWithOutExt = asmFile.replaceFirst("[.][^.]+$", "");

            String outputFileName= inputFileNameWithOutExt + ".hack";

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));

            Parser parser = new Parser(asmFile);

            Code code = new Code();

            SymbolTable symbolTable = new SymbolTable();

            symbolTable.firstRun(new File(asmFile));

            code.setSymbolTable(symbolTable);

            while(parser.hasMoreCommands()) {

                parser.advance();

                if(parser.getCurrentCommand() != null) {

                    if(parser.hasAInstruction()) {

                        String aIns = parser.getCurrentCommand();

                        String translatedAInstruction = code.translateAInstruction(aIns.substring(1));

                        String output = "0" + translatedAInstruction;

                        writer.write(output + "\n");

                    } else {

                        String dest = parser.dest();
                        String comp = parser.comp();
                        String jump = parser.jump();

                        String destToBinary = code.dest(dest);
                        String compToBinary = code.comp(comp);
                        String jumpToBinary = code.jump(jump);


                        String output = "111" + compToBinary + destToBinary + jumpToBinary;

                        writer.write(output + "\n");

                    }
                }
            }

            parser.closeFile();
            writer.close();

        }

    }

}

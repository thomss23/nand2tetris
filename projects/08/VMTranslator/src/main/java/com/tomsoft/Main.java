package com.tomsoft;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;

import static com.tomsoft.CommandType.*;


public class Main {

    public static void main(String[] args) {
        if(args.length > 0) {
            try {
                ArrayList<File> files = new ArrayList<>();

                File input = new File(args[0]);
                getFiles(input, files);

                if(!files.isEmpty()) {
                    String outputName = input.getName();
                    if(outputName.indexOf('.') > 0) {
                        outputName = outputName.substring(0, outputName.indexOf('.'));
                    } else if(outputName.indexOf('/') > 0) {
                        outputName = outputName.substring(0, outputName.indexOf('/'));
                    }

                    File output;
                    if(input.isFile()) {
                        output = new File(input.getParent(), outputName + ".asm");
                    } else {
                        output = new File(input, outputName + ".asm");
                    }

                    CodeWriter cw = new CodeWriter(output);

                    cw.writeInit();

                    for(File f : files) {
                        String name = f.getName();
                        name = name.substring(0, name.indexOf('.'));
                        cw.setFileName(name);

                        Parser p = new Parser(f);
                        while(p.hasMoreCommands()) {
                            p.advance();

                            if(p.getCurrentCommand() != null) {

                                if(p.commandType() == C_ARITHMETIC) {
                                    cw.writeArithmetic(p.getCurrentCommand());
                                } else if(p.commandType() == C_PUSH || p.commandType() == C_POP) {
                                    cw.writePushPop(p.commandType(), p.arg1(), p.arg2());
                                } else if(p.commandType() == C_LABEL) {
                                    cw.writeLabel(p.arg1());
                                } else if(p.commandType() == C_GOTO) {
                                    cw.writeGoTo(p.arg1());
                                } else if(p.commandType() == C_IF) {
                                    cw.writeIf(p.arg1());
                                } else if(p.commandType() == C_FUNCTION) {
                                    cw.writeFunction(p.arg1(), p.arg2());
                                } else if(p.commandType() == C_CALL) {
                                    cw.writeCall(p.arg1(), p.arg2());
                                } else if(p.commandType() == C_RETURN) {
                                    cw.writeReturn();
                                }
                            }

                        }
                    }

                    cw.close();
                } else {
                    System.out.println("No .vm files found.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No source entered.");
        }


    }

    private static void getFiles(File input, ArrayList<File> files) throws FileNotFoundException {
        if(input.isFile()) {
            // check for .vm extension before adding to list of files
            String filename = input.getName();
            int extension = filename.indexOf('.');
            if(extension > 0) {
                String fileExtension = filename.substring(extension + 1);
                if(fileExtension.equalsIgnoreCase("vm")) {
                    files.add(input);
                }
            }
        } else if(input.isDirectory()) {
            File[] innerFiles = input.listFiles();
            for(File f : innerFiles) {
                getFiles(f, files);
            }
        } else {
            throw new FileNotFoundException("Could not find file or directory.");
        }
    }


}

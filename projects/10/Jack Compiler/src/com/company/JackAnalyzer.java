package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class JackAnalyzer {

    public static void main(String[] args) {
        if (args.length > 0) {

            try {
                ArrayList<File> files = new ArrayList<>();

                File input = new File(args[0]);
                getFiles(input, files);

                if (!files.isEmpty()) {
                    String outputName = input.getName();
                    if (outputName.indexOf('.') > 0) {
                        outputName = outputName.substring(0, outputName.indexOf('.'));
                    } else if (outputName.indexOf('/') > 0) {
                        outputName = outputName.substring(0, outputName.indexOf('/'));
                    }

                    File output;
                    if (input.isFile()) {
                        output = new File(input.getParent(), outputName + ".xml");
                    } else {
                        output = new File(input, outputName + ".xml");
                    }
                    System.out.println("Type list " + Keyword.TYPE.toString());
                    System.out.println("File list " + files);
                    for(File file : files) {
                        JackTokenizer jackTokenizer = new JackTokenizer(file);
                        CompilationEngine compilationEngine = new CompilationEngine(jackTokenizer, file.getName());
                        compilationEngine.compileClass();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void getFiles(File input, ArrayList<File> files) throws FileNotFoundException {
        if(input.isFile()) {
            String filename = input.getName();
            int extension = filename.indexOf('.');
            if(extension > 0) {
                String fileExtension = filename.substring(extension + 1);
                if(fileExtension.equalsIgnoreCase("jack")) {
                    files.add(input);
                    Keyword.TYPE.add(filename.substring(0, extension));
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

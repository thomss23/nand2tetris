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

                    for (File file : files) {

                        JackTokenizer jackTokenizer = new JackTokenizer(file);
                        File outFile = new File(file.getName().substring(0, file.getName().indexOf('.')) + ".vm");
                        CompilationEngine compilationEngine = new CompilationEngine(jackTokenizer, outFile);
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

            if (innerFiles != null) {
                for(File f : innerFiles) {
                    getFiles(f, files);
                }
            }

        } else {
            throw new FileNotFoundException("Could not find file or directory.");
        }
    }

}

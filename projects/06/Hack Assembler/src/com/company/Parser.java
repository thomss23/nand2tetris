package com.company;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {

    private String currentCommand;

    private final Scanner reader;

    public Parser(String fileName) throws FileNotFoundException {

        File file = new File(fileName);

        reader = new Scanner(file);

    }


    private String filterInlineComments(String command) {

        String filteredCommand = command.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");

        return filteredCommand.trim();

    }


    public boolean hasMoreCommands() {

        return reader.hasNextLine();

    }

    public void advance() {

        String command = reader.nextLine().trim();

        if(command.isEmpty() || command.startsWith("//") || command.startsWith("(")) {

            setCurrentCommand(null);

        } else {

            String filteredCommand = filterInlineComments(command);

            setCurrentCommand(filteredCommand);
        }


    }

    public boolean hasAInstruction() {

        return currentCommand.startsWith("@");

    }

    public void closeFile() {
        reader.close();
    }

    public String getCurrentCommand() {
        return currentCommand;
    }

    public void setCurrentCommand(String currentCommand) {
        this.currentCommand = currentCommand;
    }

    public String dest() {

        if(!currentCommand.contains("=")) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();

        int pos = 0;
        while(currentCommand.charAt(pos) != '=') {
            sb.append(currentCommand.charAt(pos));
            pos++;
        }

        return sb.toString();


    }

    public String comp() {
        //M;JMP
        if(!currentCommand.contains("=")) {

            StringBuilder sb = new StringBuilder();

            int pos = 0;
            while(currentCommand.charAt(pos) != ';') {
                sb.append(currentCommand.charAt(pos));
                pos++;
            }
            return sb.toString();

        }
        //A=M
        else if(!currentCommand.contains(";")) {

            int equalsPos = currentCommand.indexOf("=");
            return currentCommand.substring(equalsPos + 1);

        }
        //A=M;JMP
        else {

            int offsetLeft = currentCommand.indexOf("=");
            int offsetRight = currentCommand.indexOf(";");

            return currentCommand.substring(offsetLeft, offsetRight - 1);
        }

    }

    public String jump() {

        if(!currentCommand.contains(";")) {
            return "null";
        }

        int offset = currentCommand.indexOf(";");

        return currentCommand.substring(offset + 1);

    }

}

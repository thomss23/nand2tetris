package com.tomsoft;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static com.tomsoft.CommandType.*;

public class Parser {

    private String currentCommand;

    private final Scanner reader;

    public Parser(String fileName) throws FileNotFoundException {

        File file = new File(fileName);

        reader = new Scanner(file);

    }


    public boolean hasMoreCommands() {

        return reader.hasNextLine();

    }

    public void advance() {

        String command = reader.nextLine().trim();

        if(lineIsComment(command)) {

            setCurrentCommand(null);

        } else {

            String filteredCommand = filterInlineComments(command);

            setCurrentCommand(filteredCommand);
        }

    }

    private boolean lineIsComment(String command) {
        return command.isEmpty() || command.startsWith("//") || command.startsWith("(");
    }

    private String filterInlineComments(String command) {

        String filteredCommand = command.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)",
                "");

        return filteredCommand.trim();

    }

    public void closeFile() {
        reader.close();
    }

    public void setCurrentCommand(String currentCommand) {
        this.currentCommand = currentCommand;
    }

    // returns a constant representing the type of the  current command.
    // C_ARITHMETIC is returned for all the arithmetic/logical commands
    public CommandType commandType() {
        String[] commandTokens = currentCommand.split(" ");

        if(commandTokens.length == 1) {
            return C_ARITHEMTIC;
        }

        switch (commandTokens[0]) {
            case "push":
                return C_PUSH;
            case "pop":
                return C_POP;
            default:
                return null;
        }
    }

    // return the first argument of the current command(in case of C_ARITHMETIC, the command itself(add,sub) is returned.
    // Should not be called if current command IS C_RETURN
    public String arg1() {

        if(commandType() != C_RETURN) {
            return currentCommand.split(" ")[1];
        }
        return null;
    }

    // Returns the second argument of the current command.
    // Should be called only if the current command is C_PUSH, C_POP, C_FUNCTION, or C_CALL
    public int arg2() {

        if(commandType() == C_PUSH || commandType() == C_POP || commandType() == C_FUNCTION || commandType() == C_CALL) {
            return Integer.parseInt(currentCommand.split(" ")[2]);
        }

        return -1;
    }

    public String getCurrentCommand() {
        return currentCommand;
    }
}

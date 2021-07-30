package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class SymbolTable {

    private HashMap<String, String> symbolTable;

    private int variableIndex;

    public SymbolTable() {
        initializePredefinedSymbols();
        variableIndex = 17;
    }

    private void initializePredefinedSymbols() {
        symbolTable = new HashMap<>();

        symbolTable.put("R0", "0");
        symbolTable.put("R1", "1");
        symbolTable.put("R2", "2");
        symbolTable.put("R3", "3");
        symbolTable.put("R4", "4");
        symbolTable.put("R5", "5");
        symbolTable.put("R6", "6");
        symbolTable.put("R7", "7");
        symbolTable.put("R8", "8");
        symbolTable.put("R9", "9");
        symbolTable.put("R10", "10");
        symbolTable.put("R11", "11");
        symbolTable.put("R12", "12");
        symbolTable.put("R13", "13");
        symbolTable.put("R14", "14");
        symbolTable.put("R15", "15");

        symbolTable.put("SCREEN", "16384");
        symbolTable.put("KBD", "24576");
        symbolTable.put("SP", "0");
        symbolTable.put("LCL", "1");
        symbolTable.put("ARG", "2");
        symbolTable.put("THIS", "3");
        symbolTable.put("THAT", "4");

    }

    public void addVariable(String key) {
        symbolTable.put(key, Integer.toString(variableIndex));
        variableIndex++;
    }

    public String getValue(String key) {
        return symbolTable.get(key);
    }

    public void firstRun(File file) throws FileNotFoundException {

        Scanner scanner = new Scanner(file);
        int lineNumber = 0;

        while(scanner.hasNextLine()) {

            String line = scanner.nextLine().trim();

            if(!line.isEmpty() && !line.startsWith("//") && !line.startsWith("(")) {
                lineNumber++;
            }

            if(line.startsWith("(")) {
                String symbol = line.substring(1, line.length() - 1);
                symbolTable.put(symbol, Integer.toString(lineNumber));

            }
        }
        scanner.close();
    }

}

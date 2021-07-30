package com.company;

public class Code {

    private final InstructionMapping instructionMapping;

    private SymbolTable symbolTable;


    public Code() {

        this.instructionMapping = new InstructionMapping();

        symbolTable = new SymbolTable();

    }

    public String translateAInstruction(String filteredCommand) {

        if(isSymbol(filteredCommand)) {

            if(isPresentInTable(filteredCommand)) {

                int num = Integer.parseInt(symbolTable.getValue(filteredCommand));

                return Integer.toBinaryString( 0x10000 | num).substring(2);

            } else {

               symbolTable.addVariable(filteredCommand);

                int num = Integer.parseInt(symbolTable.getValue(filteredCommand));

                return Integer.toBinaryString( 0x10000 | num).substring(2);

            }

        } else {

            int num = Integer.parseInt(filteredCommand);

            return Integer.toBinaryString( 0x10000 | num).substring(2);
        }

    }

    private boolean isPresentInTable(String filteredCommand) {
        return symbolTable.getValue(filteredCommand) != null;
    }

    private boolean isSymbol(String filteredCommand) {

        try {
            Integer.parseInt(filteredCommand);
            return false;
        } catch(NumberFormatException e){
            return true;
        }

    }

    public String comp(String comp) {
        return instructionMapping.getCompInstruction(comp);
    }

    public String dest(String dest) {
        return instructionMapping.getDestInstruction(dest);
    }

    public String jump(String jump) {
        return instructionMapping.getJumpInstruction(jump);
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
}

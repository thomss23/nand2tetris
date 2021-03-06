package com.company;

import java.util.Hashtable;

public class SymbolTable {

    private int staticCounter;
    private int fieldCounter;
    private int argCounter;
    private int varCounter;

    private Hashtable<String, Symbol> classScope;
    private Hashtable<String, Symbol> subroutineScope;

    public SymbolTable() {
        classScope = new Hashtable<>();
        subroutineScope = new Hashtable<>();
        staticCounter = 0;
        fieldCounter = 0;
        startSubroutine();
    }

    public void startSubroutine() {
        subroutineScope.clear();
        argCounter = 0;
        varCounter = 0;
    }

    public void addImplicitArg() {
        argCounter++;
    }

    public void define(String name, String type, String kind) {
        switch (kind) {
            case "static":
                classScope.put(name, new Symbol(type, kind, staticCounter));
                staticCounter++;
                break;
            case "field":
                classScope.put(name, new Symbol(type, "this", fieldCounter));
                fieldCounter++;
                break;
            case "arg":
                subroutineScope.put(name, new Symbol(type, "argument", argCounter));
                argCounter++;
                break;
            case "var":
                subroutineScope.put(name, new Symbol(type, "local", varCounter));
                varCounter++;
                break;
        }
    }

    public int varCount(String kind) {
        if(kind.equals("static")) return staticCounter;
        if(kind.equals("field")) return fieldCounter;
        if(kind.equals("arg")) return argCounter;
        if(kind.equals("var")) return varCounter;
        return -1;
    }

    public String kindOf(String name) {
        if(subroutineScope.containsKey(name)) {
            return subroutineScope.get(name).getKind();
        } else if(classScope.containsKey(name)) {
            return classScope.get(name).getKind();
        } else {
            return null;
        }
    }

    public String typeOf(String name) {
        if(subroutineScope.containsKey(name)) {
            return subroutineScope.get(name).getType();
        } else if(classScope.containsKey(name)) {
            return classScope.get(name).getType();
        } else {
            return null;
        }
    }

    public int indexOf(String name) {
        if(subroutineScope.containsKey(name)) {
            return subroutineScope.get(name).getIndex();
        } else if(classScope.containsKey(name)) {
            return classScope.get(name).getIndex();
        } else {
            return -1;
        }
    }

}

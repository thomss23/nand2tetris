package com.company;


import java.io.*;
import java.util.ArrayList;

import static com.company.TokenType.*;

public class CompilationEngine {

    private final JackTokenizer jt;
    private final VMWriter vm;
    private final SymbolTable st;
    private String className;
    int whileCount;
    int ifCount;

    public CompilationEngine(JackTokenizer input, File outfile) throws IOException {
        this.jt = input;
        vm = new VMWriter(outfile);
        st = new SymbolTable();
        whileCount = -1;
        ifCount = -1;
    }

    public void compileClass() {

        if(jt.hasMoreTokens()) jt.setNextToken();

        getKeyword();

        className = getIdentifier();

        getSymbol();

        compileClassVarDec();
        while(!(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals("}"))) {
            compileSubroutine();
        }

        getSymbol();

        jt.close();
        close();

    }


    public void compileClassVarDec() {
        while(jt.tokenType() == KEYWORD
                && (jt.keyword().equalsIgnoreCase("static")
                || jt.keyword().equalsIgnoreCase("field"))) {

            String kind = getKeyword();

            String type = getType();

            ArrayList<String> varNames = getVarNames();

            for(String varName : varNames) {
                st.define(varName, type, kind);
            }
        }

    }

    private void compileSubroutine() {

        st.startSubroutine();

        String funcType = getKeyword();
        getType();
        String funcName = getIdentifier();
        getSymbol();

        ArrayList<String[]> params = compileParameterList();
        if(funcType.equals("method")) {
            st.addImplicitArg();
        }
        for(String[] param : params) {
            st.define(param[0], param[1], "arg");
        }

        getSymbol();
        getSymbol();

        ArrayList<String[]> localVars = compileVarDec();

        for(String[] localVar : localVars) {
            st.define(localVar[0], localVar[1], "var");
        }

        vm.writeFunction(className + "." + funcName, st.varCount("var"));

        if(funcType.equals("constructor")) {

            vm.writePush("constant", st.varCount("field"));
            vm.writeCall("Memory.alloc", 1);
            vm.writePop("pointer", 0);

        } else if(funcType.equals("method")) {
            vm.writePush("argument", 0);
            vm.writePop("pointer", 0);
        }
        compileStatements();

        getSymbol();
    }

    private ArrayList<String[]> compileParameterList() {

        ArrayList<String[]> params = new ArrayList<>();
        String type;
        String name;

        while(!(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals(")"))) {

            if(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals(",")) getSymbol();

            type = getType();

            name = getIdentifier();
            String[] temp = new String[2];
            temp[0] = name;
            temp[1] = type;
            params.add(temp);

        }
        return params;
    }

    private ArrayList<String[]> compileVarDec() {
        String type;
        ArrayList<String> names;
        ArrayList<String[]> vars = new ArrayList<>();
        while(jt.tokenType() == KEYWORD && jt.keyword().equalsIgnoreCase("var")) {

            getKeyword();
            type = getType();
            names = getVarNames();

            for(String name : names) {

                String[] temp = new String[2];
                temp[0] = name;
                temp[1] = type;
                vars.add(temp);

            }
        }
        return vars;
    }

    private void compileStatements() {
        while(!(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals("}"))) {
            if(jt.tokenType() == KEYWORD && jt.keyword().equalsIgnoreCase("let")) {
                compileLet();
            } else if(jt.tokenType() == KEYWORD && jt.keyword().equalsIgnoreCase("if")) {
                compileIf();
            } else if(jt.tokenType() == KEYWORD && jt.keyword().equalsIgnoreCase("while")) {
                compileWhile();
            } else if(jt.tokenType() == KEYWORD && jt.keyword().equalsIgnoreCase("do")) {
                compileDo();
            } else if(jt.tokenType() == KEYWORD && jt.keyword().equalsIgnoreCase("return")) {
                compileReturn();
            }
        }
    }

    private void compileDo() {

        getKeyword();
        compileTerm();

        getSymbol();
        vm.writePop("temp", 0);
    }

    private void compileLet() {

        getKeyword();

        String var = getIdentifier();

        if(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals("[")) {

            vm.writePush(st.kindOf(var), st.indexOf(var));

            getSymbol();
            compileExpression();

            getSymbol();
            vm.writeArithmetic("add");
            vm.writePop("pointer", 1);

            getSymbol();
            compileExpression();

            getSymbol();
            vm.writePop("that", 0);

        } else if(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals("=")) {

            getSymbol();
            compileExpression();

            getSymbol();
            vm.writePop(st.kindOf(var), st.indexOf(var));

        }
    }

    private void compileWhile() {

        whileCount++;
        int thisWhileCount = whileCount;

        getKeyword();

        vm.writeLabel("WHILE_BEGIN" + thisWhileCount);

        getSymbol();
        compileExpression();

        getSymbol();
        vm.writeArithmetic("not");
        vm.writeIf("WHILE_END" + thisWhileCount);

        getSymbol();
        compileStatements();


        getSymbol();
        vm.writeGoto("WHILE_BEGIN" + thisWhileCount);
        vm.writeLabel("WHILE_END" + thisWhileCount);

    }

    private void compileReturn() {

        getKeyword();

        if(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals(";")) {

            vm.writePush("constant", 0);

        } else {
            compileExpression();

        }

        vm.writeReturn();
        getSymbol();

    }

    private void compileIf() {
        ifCount++;
        int thisIfCount = ifCount;

        getKeyword();

        getSymbol();
        compileExpression();

        getSymbol();

        getSymbol();
        vm.writeIf("IF_TRUE" + thisIfCount);
        vm.writeGoto("IF_FALSE" + thisIfCount);
        vm.writeLabel("IF_TRUE" + thisIfCount);
        compileStatements();

        getSymbol();
        vm.writeGoto("IF_END" + thisIfCount);
        vm.writeLabel("IF_FALSE" + thisIfCount);
        if(jt.tokenType() == KEYWORD && jt.keyword().equalsIgnoreCase("else")) {


            getKeyword();

            getSymbol();
            compileStatements();

            getSymbol();
        }
        vm.writeLabel("IF_END" + thisIfCount);
    }

    private int compileExpression() {
        if(isStartOfTerm()) {

            compileTerm();

            while(isOperator()) {

                char op = getSymbol();
                compileTerm();

                if(op == '+') vm.writeArithmetic("add");
                else if(op == '-') vm.writeArithmetic("sub");
                else if(op == '*') vm.writeCall("Math.multiply", 2);
                else if(op == '/') vm.writeCall("Math.divide", 2);
                else if(op == '&') vm.writeArithmetic("and");
                else if(op == '|') vm.writeArithmetic("or");
                else if(op == '<') vm.writeArithmetic("lt");
                else if(op == '>') vm.writeArithmetic("gt");
                else if(op == '=') vm.writeArithmetic("eq");
            }

            return 1;
        }

        return 0;
    }

    private void compileTerm() {

        if(jt.tokenType() == INT_CONST) {

            int i = getIntConstant();
            vm.writePush("constant", i);

        } else if(jt.tokenType() == STRING_CONST) {

            String str = getStrConstant();

            vm.writePush("constant", str.length());
            vm.writeCall("String.new", 1);

            for(int c = 0; c < str.length(); c++) {
                vm.writePush("constant", str.charAt(c));
                vm.writeCall("String.appendChar", 2);
            }

        } else if(isKeywordConstant()) {

            String keyword = getKeyword();
            if(keyword.equals("null") || keyword.equals("false")) {

                vm.writePush("constant", 0);
            } else if(keyword.equals("true")) {

                vm.writePush("constant", 0);
                vm.writeArithmetic("not");

            } else {
                vm.writePush("pointer", 0);
            }

        } else if(jt.tokenType() == IDENTIFIER) {

            String first = getIdentifier();

            if(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals("[")) {

                vm.writePush(st.kindOf(first), st.indexOf(first));

                getSymbol();
                compileExpression();

                getSymbol();

                vm.writeArithmetic("add");
                vm.writePop("pointer", 1);
                vm.writePush("that", 0);

            } else if(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals("(")) {

                vm.writePush("pointer", 0);

                getSymbol();
                int nArgs = compileExpressionList();

                getSymbol();
                vm.writeCall(className + "." + first, nArgs + 1);

            } else if(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals(".")) {

                getSymbol();
                String second = getIdentifier();
                if(st.indexOf(first) < 0) { // if pre-dot is not a symbol, must be function call from another class

                    getSymbol();
                    int nArgs = compileExpressionList();

                    getSymbol();
                    vm.writeCall(first + "." + second, nArgs);

                } else {// if pre-dot is a symbol, must be a method call on an object
                    vm.writePush(st.kindOf(first), st.indexOf(first));

                    getSymbol();
                    int nArgs = compileExpressionList();

                    getSymbol();
                    vm.writeCall(st.typeOf(first) + "." + second, nArgs + 1);
                }
            } else {
                vm.writePush(st.kindOf(first), st.indexOf(first));
            }
        } else if(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals("(")) {

            getSymbol();
            compileExpression();

            getSymbol();

        } else if(isUnaryOp()) {

            char op = getSymbol();
            compileTerm();
            if(op == '-') vm.writeArithmetic("neg");
            else if(op == '~') vm.writeArithmetic("not");

        }
    }

    private int compileExpressionList() {   // prints compiled expression list and returns
        int numExps = 0;                    // how many expressions were compiled
        numExps += compileExpression();
        while(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals(",")) {

            getSymbol();
            numExps += compileExpression();

        }

        return numExps;

    }

    private String getKeyword() {
        String keyword = jt.getCurrentToken();
        if(jt.hasMoreTokens()) jt.setNextToken();
        return keyword;
    }

    private String getIdentifier() {
        String id = jt.getCurrentToken();
        if(jt.hasMoreTokens()) jt.setNextToken();
        return id;
    }

    private char getSymbol() {
        String symbol = jt.getCurrentToken();
        if(jt.hasMoreTokens()) jt.setNextToken();
        return symbol.charAt(0);
    }

    private int getIntConstant() {
        int intConst = jt.intVal();
        if(jt.hasMoreTokens()) jt.setNextToken();
        return intConst;
    }

    private String getStrConstant() {
        String strConst = jt.getCurrentToken();
        if(jt.hasMoreTokens()) jt.setNextToken();
        return strConst;
    }

    private String getType() {
        if(jt.tokenType() == KEYWORD && (jt.keyword().equalsIgnoreCase("int")
                || jt.keyword().equalsIgnoreCase("char") || jt.keyword().equalsIgnoreCase("boolean")
                || jt.keyword().equalsIgnoreCase("void"))) {
            return getKeyword();
        } else if(jt.tokenType() == IDENTIFIER) {
            return getIdentifier();
        }
        return null;
    }

    private ArrayList<String> getVarNames() {
        ArrayList<String> varNames = new ArrayList<String>();
        varNames.add(getIdentifier());
        while(!(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals(";"))) {
            getSymbol();
            varNames.add(getIdentifier());
        }
        getSymbol();

        return varNames;
    }

    private boolean isOperator() {
        if(jt.tokenType() == SYMBOL) {
            return (jt.getCurrentToken().equals("+") || jt.getCurrentToken().equals("-") || jt.getCurrentToken().equals("*")
                    || jt.getCurrentToken().equals("/") || jt.getCurrentToken().equals("&") || jt.getCurrentToken().equals("|") || jt.getCurrentToken().equals("<")
                    || jt.getCurrentToken().equals(">") || jt.getCurrentToken().equals("="));
        } else {
            return false;
        }

    }

    private boolean isKeywordConstant() {
        if(jt.tokenType() == KEYWORD) {
            return jt.keyword().equalsIgnoreCase("true")
                    || jt.keyword().equalsIgnoreCase("false") || jt.keyword().equalsIgnoreCase("null")
                    || jt.keyword().equalsIgnoreCase("this");
        } else {
            return false;
        }

    }

    private boolean isUnaryOp() {
        return (jt.tokenType() == SYMBOL && (jt.getCurrentToken().equals("-") || jt.getCurrentToken().equals("~")));
    }

    private boolean isStartOfTerm() {
        if(jt.tokenType() == INT_CONST) return true;
        if(jt.tokenType() == STRING_CONST) return true;
        if(isKeywordConstant()) return true;
        if(jt.tokenType() == IDENTIFIER) return true;
        if(jt.tokenType() == SYMBOL && jt.getCurrentToken().equals("(")) return true;
        return isUnaryOp();
    }

    public void close() {
        vm.close();
    }


}

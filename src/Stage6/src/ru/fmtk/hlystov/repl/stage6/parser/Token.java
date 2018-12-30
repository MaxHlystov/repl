package ru.fmtk.hlystov.repl.stage6.parser;


public class Token {
    private String text;
    private Class expType;

    public Token(String text, Class expType) {
        this.text = text;
        this.expType = expType;
    }

    public String getText() {
        return text;
    }

    public Class getExpType() {
        return expType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("t(");
        if (text != null) {
            sb.append(text);
        }
        sb.append(", ");
        if (expType != null) {
            sb.append(expType.getName());
        }
        sb.append(")");
        return sb.toString();
    }
}

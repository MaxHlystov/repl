package ru.fmtk.hlystov.repl.stage6.model;

import ru.fmtk.hlystov.repl.stage6.processor.IStringConsumer;
import ru.fmtk.hlystov.repl.stage6.processor.Processor;

public abstract class AbstractClause {
    private String error = null;

    public boolean hasError() {
        return error != null;
    }

    public String getError() {
        if (error == null) {
            return "";
        }
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public abstract boolean process(Processor processor,
                                    IStringConsumer consumer);

    @Override
    public String toString() {
        if (hasError()) {
            return getError();
        }
        return "";
    }
}
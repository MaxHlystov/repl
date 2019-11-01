package ru.fmtk.hlystov.repl.stage6.model;

import ru.fmtk.hlystov.repl.stage6.processor.IStringConsumer;
import ru.fmtk.hlystov.repl.stage6.processor.Processor;

import java.util.Objects;

public class Command extends AbstractClause {
    private final String commandName;

    public Command(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public boolean process(Processor processor, IStringConsumer consumer) {
        return processor.process((Command) this, consumer);
    }

    public boolean equals(Object o) {
        if (hasError()) {
            return false;
        }
        if (o instanceof Command) {
            return Objects.equals(commandName, ((Command) o).commandName);
        }
        if (o instanceof String) {
            return Objects.equals(commandName, o);
        }
        return false;
    }
}
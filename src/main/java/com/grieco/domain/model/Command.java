package com.grieco.domain.model;

public enum Command
{
    POLL_LOCAL("local"),
    POLL_REMOTE("remote");

    private final String value;

    Command(String value)
    {
        this.value = value;
    }

    public static Command fromString(String value)
    {
        for (Command cmd : values())
        {
            if (cmd.value.equalsIgnoreCase(value))
            {
                return cmd;
            }
        }
        throw new IllegalArgumentException("No command with value: " + value + " found");
    }
}

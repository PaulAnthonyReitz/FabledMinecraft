package com.fabledclan.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

// CommandClass is the base abstract class used for all custom commands. It implements the CommandExecutor which is used
// by the plugin. The abstract onCommand() method needs to be filled out with the code of what the command does
// when executed.

public abstract class CommandClass implements CommandExecutor {
    private final String NAME;

    public CommandClass(String name) {
        this.NAME = name;
    }

    public String getName() {
        return NAME;
    }

    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}

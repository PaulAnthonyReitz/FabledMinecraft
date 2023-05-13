package com.fabledclan.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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

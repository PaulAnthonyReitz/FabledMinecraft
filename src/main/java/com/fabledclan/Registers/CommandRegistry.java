package com.fabledclan.Registers;

import java.util.ArrayList;
import java.util.Arrays;

import com.fabledclan.Commands.*;

public class CommandRegistry {
    private static ArrayList<CommandClass> commands = null;

    public static void init() {
        if (commands != null) return;
        ArrayList<CommandClass> ret = new ArrayList<CommandClass>(Arrays.asList(
            // ADD COMMANDS HERE:
            new UnenchantCommand(),
            new EnchantCommand(),
            new BookCommand(),
            new HomeCommand(),
            new LockCommand(),
            new MenuCommand(),
            new RemoveLockCommand(),
            new SetAttributesCommand(),
            new SettingsCommand(),
            new SpellsCommand(),
            new UnlockLockCommand(),
            new UpdateEnemyPagesCommand(),
            new ViewEnemiesCommand(),
            new ViewStatsCommand(),
            new LeaderboardCommand(),
            new SummonCommand(),
            new PartyCreateCommand(),
            new PartyDisbandCommand(),
            new PartyInviteCommand(),
            new PartyLeaveCommand()
        ));
        commands = ret;
    }

    public static ArrayList<CommandClass> getCommands() {
        return commands;
    }
}

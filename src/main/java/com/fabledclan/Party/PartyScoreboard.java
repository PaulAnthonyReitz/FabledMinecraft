package com.fabledclan.Party;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import com.fabledclan.Main;

public class PartyScoreboard {
    private Scoreboard scoreboard;
    private Objective objective;
    private int taskId;

    public PartyScoreboard(Party party) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("party", "dummy", ChatColor.GOLD + "Party");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);


        for (Player player : party.getMembers()) {
            updatePlayer(player);
        }

        startUpdating();
    }

    public void updatePlayer(Player player) {
        Party party = PartyManager.getParty(player);
        if (party != null) {
            for (Player member : party.getMembers()) {
                String entry = ChatColor.GREEN + member.getName();
                Score score = objective.getScore(entry);
                score.setScore((int) member.getHealth());
            }
        }
    }
    

    public void removePlayer(Player player) {
        scoreboard.resetScores(player.getName());
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    private void startUpdating() {
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PartyManager.getParty(player) != null) {
                    updatePlayer(player);
                } else {
                    removePlayer(player);
                }
            }
        }, 0L, 20L);
    }

    public void stopUpdating() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }
}

package com.fabledclan;

import java.util.ArrayList;
import java.util.Arrays;

import com.fabledclan.abilities.*;

public class AbilityRegistry {
    private static ArrayList<Ability> abilityList = null;

    public static void initializeAbilities() {
        if (abilityList != null) return;
        ArrayList<Ability> abilities = new ArrayList<Ability>(Arrays.asList(
            // ADD ABILITIES HERE:
            new DragonBreath("dragon_breath", 1, 50),
            new SummonGiant("summon_giant", 1, 50),
            new PartySpell("party", 1, 100),
            new MagicMissile("magic_missile", 1, 25),
            new YeetBoat("yeet_boat", 1, 30),
            new Wrangle("wrangle", 1, 50),
            new Dash("dash", 1, 25),
            new IceShard("ice_shard", 1, 20),
            new PowerStrike("power_strike", 1, 20),
            new FireballSpell("fireball", 1, 25),
            new DarkVortex("dark_vortex", 1, 75),
            new PlagueSwarm("plague_swarm", 1, 60),
            new VaderChoke("vader_choke", 1, 50),
            new UndeadArmy("undead_army", 1, 75),
            new Feather("feather", 1, 40)
        ));
        abilityList = abilities;
    }

    public static ArrayList<Ability> getAbilities() {
        return abilityList;
    }
}

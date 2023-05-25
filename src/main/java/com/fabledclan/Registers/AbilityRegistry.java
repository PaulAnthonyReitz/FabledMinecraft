package com.fabledclan.Registers;

import java.util.ArrayList;
import java.util.Arrays;

import com.fabledclan.CustomAbilities.*;

public class AbilityRegistry {
    private static ArrayList<Ability> abilityList = null;

    public static void initializeAbilities() {
        if (abilityList != null) return;
        ArrayList<Ability> abilities = new ArrayList<Ability>(Arrays.asList(
            // ADD ABILITIES HERE:
            new DragonBreath(),
            new SummonGiant(),
            new PartySpell(),
            new MagicMissile(),
            new YeetBoat(),
            new Wrangle(),
            new Dash(),
            new IceShard(),
            new PowerStrike(),
            new FireballSpell(),
            new DarkVortex(),
            new PlagueSwarm(),
            new VaderChoke(),
            new UndeadArmy(),
            new Feather(),
            new Feed(),
            new Heal()
        ));
        abilityList = abilities;
    }

    public static ArrayList<Ability> getAbilities() {
        return abilityList;
    }
}

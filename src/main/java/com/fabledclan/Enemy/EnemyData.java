package com.fabledclan.Enemy;

public class EnemyData {
    public String entityName;
    public int hp;
    public int hpScale;
    public int baseAttack;
    public int attackScale;
    public int baseExp;
    public int expScale;
    public int defense;
    public int defense_scale;

    public EnemyData(String entityName, int hp, int hpScale, int baseAttack, int attackScale, int baseExp, int expScale, int defense, int defense_scale) {
        this.entityName = entityName;
        this.hp = hp;
        this.hpScale = hpScale;
        this.baseAttack = baseAttack;
        this.attackScale = attackScale;
        this.baseExp = baseExp;
        this.expScale = expScale;
        this.defense = defense;
        this.defense_scale = defense_scale;
    }
}

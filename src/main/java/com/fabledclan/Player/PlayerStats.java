package com.fabledclan.Player;

public class PlayerStats {
    private  double movementSpeed;
    private  int attack;
    private  int defense;
    private  int maxHealth;
    private  int exp;
    private  int level;
    private  String name;
    private  int magic;
    private  int stamina;

    public PlayerStats(double movementSpeed, int attack, int defense, int maxHealth, int exp, int level,
            String name, int magic, int stamina) {
        this.movementSpeed = movementSpeed;
        this.attack = attack;
        this.defense = defense;
        this.maxHealth = maxHealth;
        this.exp = exp;
        this.level = level;
        this.name = name;
        this.magic = magic;
        this.stamina = stamina;
    }

    // Add getter methods for each attribute
    public double getMovementSpeed() {
        return movementSpeed;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public int getMagic() {
        return magic;
    }

    public int getStamina() {
        return stamina;
    }

        public void setMovementSpeed(double movementSpeed) {
            this.movementSpeed = movementSpeed;
        }
    
        public void setAttack(int attack) {
            this.attack = attack;
        }
    
        public void setDefense(int defense) {
            this.defense = defense;
        }
    
        public void setMaxHealth(int maxHealth) {
            this.maxHealth = maxHealth;
        }
    
        public void setExp(int exp) {
            this.exp = exp;
        }
    
        public void setLevel(int level) {
            this.level = level;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    
        public void setMagic(int magic) {
            this.magic = magic;
        }
    
        public void setStamina(int stamina) {
            this.stamina = stamina;
        }

        public String getUuid() {
            return null;
        }
    
    

}

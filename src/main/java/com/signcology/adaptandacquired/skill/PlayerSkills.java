package com.signcology.adaptandacquired.skill;

import net.minecraft.nbt.CompoundTag;

public class PlayerSkills {
    private String skill_offense = "None";
    private String skill_defense = "None";
    private String skill_support = "None";

    public String getOffSkill() {
        return skill_offense;
    }
    public String getDefSkill() {
        return skill_defense;
    }
    public String getSupSkill() {
        return skill_support;
    }

    public void setOffSkill(String skill) {
        this.skill_offense = skill;
    }
    public void setDefSkill(String skill) {
        this.skill_defense = skill;
    }
    public void setSupSkill(String skill) {
        this.skill_support = skill;
    }

    public void copyFrom(PlayerSkills source) {
        this.skill_offense = source.skill_offense;
        this.skill_defense = source.skill_defense;
        this.skill_support = source.skill_support;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putString("skill_offense", skill_offense);
        nbt.putString("skill_defense", skill_defense);
        nbt.putString("skill_support", skill_support);
    }

    public void loadNBTData(CompoundTag nbt) {
        skill_offense = nbt.getString("skill_offense");
        skill_defense = nbt.getString("skill_defense");
        skill_support = nbt.getString("skill_support");
    }

}

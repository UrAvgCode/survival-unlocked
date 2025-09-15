package com.uravgcode.survivalunlocked.mobheads.variant;

import org.bukkit.entity.Villager;

public class VillagerProfession {
    public static String name(Villager.Profession profession) {
        if (profession == Villager.Profession.ARMORER) {
            return "armorer";
        } else if (profession == Villager.Profession.BUTCHER) {
            return "butcher";
        } else if (profession == Villager.Profession.CARTOGRAPHER) {
            return "cartographer";
        } else if (profession == Villager.Profession.CLERIC) {
            return "cleric";
        } else if (profession == Villager.Profession.FARMER) {
            return "farmer";
        } else if (profession == Villager.Profession.FISHERMAN) {
            return "fisherman";
        } else if (profession == Villager.Profession.FLETCHER) {
            return "fletcher";
        } else if (profession == Villager.Profession.LEATHERWORKER) {
            return "leatherworker";
        } else if (profession == Villager.Profession.LIBRARIAN) {
            return "librarian";
        } else if (profession == Villager.Profession.MASON) {
            return "mason";
        } else if (profession == Villager.Profession.NITWIT) {
            return "nitwit";
        } else if (profession == Villager.Profession.NONE) {
            return "none";
        } else if (profession == Villager.Profession.SHEPHERD) {
            return "shepherd";
        } else if (profession == Villager.Profession.TOOLSMITH) {
            return "toolsmith";
        } else if (profession == Villager.Profession.WEAPONSMITH) {
            return "weaponsmith";
        } else {
            return null;
        }
    }
}

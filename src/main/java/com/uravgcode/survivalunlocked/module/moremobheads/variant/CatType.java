package com.uravgcode.survivalunlocked.module.moremobheads.variant;

import org.bukkit.entity.Cat;

public class CatType {
    public static String name(Cat.Type type) {
        if (type == Cat.Type.ALL_BLACK) {
            return "all_black";
        } else if (type == Cat.Type.BLACK) {
            return "black";
        } else if (type == Cat.Type.BRITISH_SHORTHAIR) {
            return "british_shorthair";
        } else if (type == Cat.Type.CALICO) {
            return "calico";
        } else if (type == Cat.Type.JELLIE) {
            return "jellie";
        } else if (type == Cat.Type.PERSIAN) {
            return "persian";
        } else if (type == Cat.Type.RAGDOLL) {
            return "ragdoll";
        } else if (type == Cat.Type.RED) {
            return "red";
        } else if (type == Cat.Type.SIAMESE) {
            return "siamese";
        } else if (type == Cat.Type.TABBY) {
            return "tabby";
        } else if (type == Cat.Type.WHITE) {
            return "white";
        } else {
            return null;
        }
    }
}

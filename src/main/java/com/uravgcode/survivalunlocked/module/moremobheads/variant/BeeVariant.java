package com.uravgcode.survivalunlocked.module.moremobheads.variant;

public class BeeVariant {
    public static String name(boolean pollinated, boolean angry) {
        if (pollinated && angry) {
            return "nectar_angry";
        } else if (pollinated) {
            return "nectar";
        } else if (angry) {
            return "angry";
        } else {
            return "plain";
        }
    }
}

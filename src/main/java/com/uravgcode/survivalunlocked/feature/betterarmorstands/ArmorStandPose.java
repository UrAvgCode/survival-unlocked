package com.uravgcode.survivalunlocked.feature.betterarmorstands;

import org.bukkit.util.EulerAngle;

public enum ArmorStandPose {
    DEFAULT(
        eulerAngle(0, 0, 0),
        eulerAngle(0, 0, 0),
        eulerAngle(-10, 0, -10),
        eulerAngle(-1, 0, -1),
        eulerAngle(-15, 0, 10),
        eulerAngle(1, 0, 1)
    ),
    NONE(
        eulerAngle(0, 0, 0),
        eulerAngle(0, 0, 0),
        eulerAngle(0, 0, 0),
        eulerAngle(0, 0, 0),
        eulerAngle(0, 0, 0),
        eulerAngle(0, 0, 0)
    ),
    SOLEMN(
        eulerAngle(0, 0, 2),
        eulerAngle(15, 0, 0),
        eulerAngle(-30, 15, 15),
        eulerAngle(-1, 0, -1),
        eulerAngle(-60, -20, -10),
        eulerAngle(1, 0, 1)
    ),
    ATHENA(
        eulerAngle(0, 0, 2),
        eulerAngle(-5, 0, 0),
        eulerAngle(10, 0, -5),
        eulerAngle(-3, -3, -3),
        eulerAngle(-60, 20, -10),
        eulerAngle(3, 3, 3)
    ),
    BRANDISH(
        eulerAngle(0, 0, -2),
        eulerAngle(-15, 0, 0),
        eulerAngle(20, 0, -10),
        eulerAngle(5, -3, -3),
        eulerAngle(-110, 50, 0),
        eulerAngle(-5, 3, 3)
    ),
    HONOR(
        eulerAngle(0, 0, 0),
        eulerAngle(-15, 0, 0),
        eulerAngle(-110, 35, 0),
        eulerAngle(5, -3, -3),
        eulerAngle(-110, -35, 0),
        eulerAngle(-5, 3, 3)
    ),
    ENTERTAIN(
        eulerAngle(0, 0, 0),
        eulerAngle(-15, 0, 0),
        eulerAngle(-110, -35, 0),
        eulerAngle(5, -3, -3),
        eulerAngle(-110, 35, 0),
        eulerAngle(-5, 3, 3)
    ),
    SALUTE(
        eulerAngle(0, 0, 0),
        eulerAngle(0, 0, 0),
        eulerAngle(10, 0, -5),
        eulerAngle(-1, 0, -1),
        eulerAngle(-70, -40, 0),
        eulerAngle(1, 0, 1)
    ),
    HERO(
        eulerAngle(0, 8, 0),
        eulerAngle(-4, 67, 0),
        eulerAngle(16, 32, -8),
        eulerAngle(0, -75, -8),
        eulerAngle(-99, 63, 0),
        eulerAngle(4, 63, 8)
    ),
    RIPOSTE(
        eulerAngle(0, 0, 0),
        eulerAngle(16, 20, 0),
        eulerAngle(4, 8, 237),
        eulerAngle(-14, -18, -16),
        eulerAngle(246, 0, 89),
        eulerAngle(8, 20, 4)
    ),
    ZOMBIE(
        eulerAngle(0, 0, 0),
        eulerAngle(-10, 0, -5),
        eulerAngle(-105, 0, 0),
        eulerAngle(7, 0, 0),
        eulerAngle(-100, 0, 0),
        eulerAngle(-46, 0, 0)
    ),
    CANCAN_A(
        eulerAngle(0, 22, 0),
        eulerAngle(-5, 18, 0),
        eulerAngle(8, 0, -114),
        eulerAngle(-111, 55, 0),
        eulerAngle(0, 84, 111),
        eulerAngle(0, 23, -13)
    ),
    CANCAN_B(
        eulerAngle(0, -18, 0),
        eulerAngle(-10, -20, 0),
        eulerAngle(0, 0, -112),
        eulerAngle(0, 0, 13),
        eulerAngle(8, 90, 111),
        eulerAngle(-119, -42, 0)
    );

    public final EulerAngle body;
    public final EulerAngle head;
    public final EulerAngle leftArm;
    public final EulerAngle leftLeg;
    public final EulerAngle rightArm;
    public final EulerAngle rightLeg;

    ArmorStandPose(
        EulerAngle body,
        EulerAngle head,
        EulerAngle leftArm,
        EulerAngle leftLeg,
        EulerAngle rightArm,
        EulerAngle rightLeg
    ) {
        this.body = body;
        this.head = head;
        this.leftArm = leftArm;
        this.leftLeg = leftLeg;
        this.rightArm = rightArm;
        this.rightLeg = rightLeg;
    }

    private static EulerAngle eulerAngle(double x, double y, double z) {
        return new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
    }
}

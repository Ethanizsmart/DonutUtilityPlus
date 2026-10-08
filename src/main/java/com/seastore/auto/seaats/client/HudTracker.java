/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  org.joml.Matrix3x2fStack
 */
package com.seastore.auto.seaats.client;

import com.seastore.auto.seaats.Config;
import com.seastore.auto.seaats.client.AutoSellLogic;
import com.seastore.auto.seaats.client.SeaSettingsScreen;
import com.seastore.auto.seaats.client.SellCycleScheduler;
import java.util.Locale;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import org.joml.Matrix3x2fStack;

public class HudTracker {
    public static void register() {
        HudRenderCallback.EVENT.register((class_3322, class_97792) -> HudTracker.render(class_3322));
    }

    public static void render(class_332 class_3322) {
        class_310 class_3102 = class_310.method_1551();
        if (!Config.showTracker || class_3102.field_1724 == null || class_3102.field_1687 == null || class_3102.field_1755 instanceof SeaSettingsScreen || class_3102.field_1690.field_1842 || class_3102.method_53526().method_53536()) {
            return;
        }
        HudTracker.renderCard(class_3322, class_3102.field_1772, 8, 8);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void renderCard(class_332 class_3322, class_327 class_3272, int n, int n2) {
        float f = (float)Config.clampTrackerScale(Config.trackerScale);
        if ((f = Math.min(f, Math.min(((float)class_3322.method_51421() - 16.0f) / 180.0f, ((float)class_3322.method_51443() - 16.0f) / 100.0f))) <= 0.0f) {
            return;
        }
        Matrix3x2fStack matrix3x2fStack = class_3322.method_51448();
        matrix3x2fStack.pushMatrix();
        matrix3x2fStack.translate((float)n, (float)n2);
        matrix3x2fStack.scale(f, f);
        try {
            int n3;
            class_3322.method_25294(0, 0, 180, 1, -431826486);
            class_3322.method_25294(0, 99, 180, 100, -431826486);
            class_3322.method_25294(0, 1, 1, 99, -431826486);
            class_3322.method_25294(179, 1, 180, 99, -431826486);
            HudTracker.text(class_3322, class_3272, "\u00a7lSEA SELL", 10, 8, -1379076);
            String string = AutoSellLogic.trackerStatus();
            int n4 = n3 = Config.isAutoSellEnabled ? -10362188 : -4931376;
            if (!string.equals("\u0110ANG B\u00c1N") && !string.equals("\u0110\u00c3 T\u1eaeT")) {
                n3 = -10879;
            }
            HudTracker.text(class_3322, class_3272, string, 170 - class_3272.method_1727(string), 8, n3);
            long l = Config.startTimeMs == 0L ? 0L : Math.max(0L, (System.currentTimeMillis() - Config.startTimeMs) / 1000L);
            int n5 = Math.max(12, 13);
            HudTracker.row(class_3322, class_3272, "Thu nh\u1eadp", HudTracker.money(Config.totalMoney), 180, 29, -10362188);
            HudTracker.row(class_3322, class_3272, "T\u1ed1c \u0111\u1ed9", HudTracker.money(l > 0L ? Config.totalMoney * 3600.0 / (double)l : 0.0) + "/h", 180, 29 + n5, -7681793);
            HudTracker.row(class_3322, class_3272, "\u0110\u00e3 b\u00e1n", String.format(Locale.ROOT, "%,d l\u01b0\u1ee3t", Config.totalSellCount), 180, 29 + n5 * 2, -1379076);
            HudTracker.row(class_3322, class_3272, "Th\u1eddi gian", String.format(Locale.ROOT, "%02d:%02d:%02d", l / 3600L, l / 60L % 60L, l % 60L), 180, 29 + n5 * 3, -1379076);
            String string2 = SellCycleScheduler.currentCycle() == 0 ? "Ch\u1edd l\u01b0\u1ee3t b\u00e1n" : "C" + SellCycleScheduler.currentCycle() + " \u00b7 " + SellCycleScheduler.selectedDelay() + "t \u00b7 " + SellCycleScheduler.secondsRemaining() + "s";
            HudTracker.row(class_3322, class_3272, "Chu k\u1ef3", string2, 180, 29 + n5 * 4, -10879);
        }
        finally {
            matrix3x2fStack.popMatrix();
        }
    }

    private static void row(class_332 class_3322, class_327 class_3272, String string, String string2, int n, int n2, int n3) {
        HudTracker.text(class_3322, class_3272, string, 10, n2, -7298118);
        int n4 = n - 20 - class_3272.method_1727(string) - 10;
        string2 = class_3272.method_27523(string2, Math.max(20, n4));
        HudTracker.text(class_3322, class_3272, string2, n - 10 - class_3272.method_1727(string2), n2, n3);
    }

    private static void text(class_332 class_3322, class_327 class_3272, String string, int n, int n2, int n3) {
        class_3322.method_51433(class_3272, string, n, n2, n3, true);
    }

    private static String money(double d) {
        if (d >= 1.0E9) {
            return String.format(Locale.ROOT, "$%.2fB", d / 1.0E9);
        }
        if (d >= 1000000.0) {
            return String.format(Locale.ROOT, "$%.2fM", d / 1000000.0);
        }
        if (d >= 1000.0) {
            return String.format(Locale.ROOT, "$%.2fK", d / 1000.0);
        }
        return String.format(Locale.ROOT, "$%.0f", d);
    }
}


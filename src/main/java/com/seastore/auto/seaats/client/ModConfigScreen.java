/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 */
package com.seastore.auto.seaats.client;

import com.seastore.auto.seaats.client.AutoSellLogic;
import com.seastore.auto.seaats.client.SeaSettingsScreen;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_465;

public class ModConfigScreen {
    public static class_437 createScreen(class_437 class_4372) {
        class_310 class_3102 = class_310.method_1551();
        if (class_4372 instanceof class_465 || AutoSellLogic.hasActiveMenu(class_3102)) {
            AutoSellLogic.closeAllGui(class_3102);
            class_4372 = null;
        }
        return new SeaSettingsScreen(class_4372);
    }
}


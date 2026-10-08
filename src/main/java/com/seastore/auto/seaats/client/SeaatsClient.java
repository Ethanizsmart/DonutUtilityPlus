/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
 *  net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_304$class_11900
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_746
 */
package com.seastore.auto.seaats.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.seastore.auto.seaats.Config;
import com.seastore.auto.seaats.ModConfig;
import com.seastore.auto.seaats.client.AutoSellLogic;
import com.seastore.auto.seaats.client.ChatMoneyListener;
import com.seastore.auto.seaats.client.HudTracker;
import com.seastore.auto.seaats.client.ScreenHotkeys;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_3675;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public class SeaatsClient
implements ClientModInitializer {
    private static class_304 toggleKey;
    private static class_304 settingsKey;
    private static final class_304.class_11900 SEAATS_CATEGORY;

    public void onInitializeClient() {
        ModConfig.load();
        System.out.println("[SeaATS] V1.08.5 BackgroundGui + Tracker 0.3-1.0 loaded (Fabric 1.21.11)");
        toggleKey = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.seaats.toggle", class_3675.class_307.field_1668, 297, SEAATS_CATEGORY));
        settingsKey = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.seaats.settings", class_3675.class_307.field_1668, 298, SEAATS_CATEGORY));
        ScreenHotkeys.register(toggleKey, settingsKey);
        ClientLifecycleEvents.CLIENT_STOPPING.register(class_3102 -> AutoSellLogic.resetState());
        ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, class_71572) -> commandDispatcher.register((LiteralArgumentBuilder)ClientCommandManager.literal((String)"homeset").executes(commandContext -> {
            class_746 class_7462 = ((FabricClientCommandSource)commandContext.getSource()).getPlayer();
            Config.homeX = class_7462.method_23317();
            Config.homeY = class_7462.method_23318();
            Config.homeZ = class_7462.method_23321();
            Config.isHomeSet = true;
            ModConfig.save();
            ((FabricClientCommandSource)commandContext.getSource()).sendFeedback((class_2561)class_2561.method_43470((String)("\u00a7a[SeaATS] \u0110\u00e3 l\u01b0u to\u1ea1 \u0111\u1ed9 HomeSet t\u1ea1i: " + String.format("%.2f, %.2f, %.2f", Config.homeX, Config.homeY, Config.homeZ))));
            return 1;
        })));
        ClientTickEvents.END_CLIENT_TICK.register(class_3102 -> {
            ScreenHotkeys.tick(class_3102);
            AutoSellLogic.tick(class_3102);
        });
        ChatMoneyListener.register();
        HudTracker.register();
    }

    static {
        SEAATS_CATEGORY = class_304.class_11900.method_74698((class_2960)class_2960.method_60655((String)"seaats", (String)"category"));
    }
}


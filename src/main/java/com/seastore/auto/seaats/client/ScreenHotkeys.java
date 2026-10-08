/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents
 *  net.minecraft.class_1041
 *  net.minecraft.class_11908
 *  net.minecraft.class_2561
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_3675
 *  net.minecraft.class_3675$class_306
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_437
 */
package com.seastore.auto.seaats.client;

import com.seastore.auto.seaats.client.AutoSellLogic;
import com.seastore.auto.seaats.client.ModConfigScreen;
import com.seastore.auto.seaats.client.SeaSettingsScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.class_1041;
import net.minecraft.class_11908;
import net.minecraft.class_2561;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_437;

public final class ScreenHotkeys {
    private static Press toggle;
    private static Press settings;
    private static boolean registered;

    private ScreenHotkeys() {
    }

    public static void register(class_304 class_3042, class_304 class_3043) {
        toggle = new Press(class_3042);
        settings = new Press(class_3043);
        if (registered) {
            return;
        }
        registered = true;
        ScreenEvents.AFTER_INIT.register((class_3102, class_4373, n, n2) -> {
            ScreenKeyboardEvents.allowKeyPress((class_437)class_4373).register((class_4372, class_119082) -> ScreenHotkeys.allowScreenKey(class_3102, class_119082));
            ScreenKeyboardEvents.afterKeyRelease((class_437)class_4373).register((class_4372, class_119082) -> {
                if (ScreenHotkeys.toggle.binding.method_1417(class_119082)) {
                    ScreenHotkeys.toggle.held = false;
                }
                if (ScreenHotkeys.settings.binding.method_1417(class_119082)) {
                    ScreenHotkeys.settings.held = false;
                }
            });
        });
    }

    static boolean allowScreenKey(class_310 class_3102, class_11908 class_119082) {
        if (class_3102.field_1724 == null || class_3102.field_1687 == null || class_3102.method_1562() == null) {
            return true;
        }
        if (ScreenHotkeys.toggle.binding.method_1417(class_119082)) {
            toggle.request();
            return false;
        }
        if (ScreenHotkeys.settings.binding.method_1417(class_119082)) {
            settings.request();
            return false;
        }
        return true;
    }

    public static void tick(class_310 class_3102) {
        if (toggle == null) {
            return;
        }
        toggle.poll(class_3102);
        settings.poll(class_3102);
        if (class_3102.field_1724 == null || class_3102.field_1687 == null || class_3102.method_1562() == null) {
            toggle.clear();
            settings.clear();
            return;
        }
        if (toggle.take()) {
            AutoSellLogic.toggleFromKey(class_3102);
        }
        if (settings.take()) {
            try {
                class_437 class_4372 = class_3102.field_1755;
                if (class_4372 instanceof SeaSettingsScreen) {
                    SeaSettingsScreen seaSettingsScreen = (SeaSettingsScreen)class_4372;
                    seaSettingsScreen.method_25419();
                } else {
                    class_3102.method_1507(ModConfigScreen.createScreen(class_3102.field_1755));
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
                class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)("\u00a7cL\u1ed7i m\u1edf GUI c\u00e0i \u0111\u1eb7t: " + exception.getMessage())), false);
            }
        }
    }

    private static final class Press {
        final class_304 binding;
        boolean held;
        boolean pending;

        Press(class_304 class_3042) {
            this.binding = class_3042;
        }

        void request() {
            if (!this.held) {
                this.held = true;
                this.pending = true;
            }
        }

        void poll(class_310 class_3102) {
            boolean bl;
            class_3675.class_306 class_3062 = KeyBindingHelper.getBoundKeyOf((class_304)this.binding);
            boolean bl2 = bl = class_3062.method_1442() == class_3675.class_307.field_1668 && class_3062.method_1444() >= 0 ? class_3675.method_15987((class_1041)class_3102.method_22683(), (int)class_3062.method_1444()) : this.binding.method_1434();
            if (!bl) {
                this.held = false;
            }
            boolean bl3 = false;
            while (this.binding.method_1436()) {
                bl3 = true;
            }
            if (bl3) {
                this.request();
            }
        }

        boolean take() {
            boolean bl = this.pending;
            this.pending = false;
            return bl;
        }

        void clear() {
            this.held = false;
            this.pending = false;
        }
    }
}


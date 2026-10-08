/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_408
 */
package com.seastore.auto.seaats.client;

import net.minecraft.class_408;

final class ChatCommandSender {
    private ChatCommandSender() {
    }

    static void send(String string) {
        new class_408("", false).method_44056("/" + string, true);
    }
}


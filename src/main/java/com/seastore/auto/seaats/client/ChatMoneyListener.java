/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
 */
package com.seastore.auto.seaats.client;

import com.seastore.auto.seaats.Config;
import com.seastore.auto.seaats.client.AutoSellLogic;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

@Environment(value=EnvType.CLIENT)
public final class ChatMoneyListener {
    private static final Pattern MONEY_PATTERN = Pattern.compile("(?i)\\$\\s*([0-9][0-9,.]*)\\s*([KMB])?");
    private static final Pattern SELL_WORD = Pattern.compile("(?:^|[^\\p{L}])(sold|sell|b\u00e1n)(?:[^\\p{L}]|$)", 66);
    private static final Pattern AMOUNT_ONLY = Pattern.compile("(?i)\\$\\s*[0-9][0-9,.]*\\s*[KMB]?");

    private ChatMoneyListener() {
    }

    public static void register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((class_25612, bl) -> {
            if (AutoSellLogic.handlePayConfirmation(class_25612.getString())) {
                return true;
            }
            if (AutoSellLogic.handlePayBalanceMessage(class_25612.getString())) {
                return true;
            }
            ChatMoneyListener.recordIfConfirmedSell(class_25612.getString());
            return true;
        });
    }

    static boolean recordIfConfirmedSell(String string) {
        if (string == null) {
            return false;
        }
        String string2 = ChatMoneyListener.normalize(string);
        if (!ChatMoneyListener.isSellReceipt(string2)) {
            return false;
        }
        double d = ChatMoneyListener.parseMoney(string2);
        if (!(d > 0.0) || !Double.isFinite(d)) {
            return false;
        }
        if (!AutoSellLogic.consumeExpectedSellReceipt()) {
            return false;
        }
        Config.totalMoney += d;
        ++Config.totalSellCount;
        Config.lastMoneyReceivedMs = System.currentTimeMillis();
        System.out.println("[SeaATS] Confirmed /sell income: " + d + " from: " + string);
        return true;
    }

    private static boolean isSellReceipt(String string) {
        return AMOUNT_ONLY.matcher(string).matches() || SELL_WORD.matcher(string).find();
    }

    static String normalize(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFKC).replaceAll("(?i)\u00a7[0-9a-fk-orx]", "").replaceAll("\\p{Cf}", "").strip();
    }

    static double parseMoney(String string) {
        Matcher matcher = MONEY_PATTERN.matcher(string);
        if (!matcher.find()) {
            return 0.0;
        }
        String string2 = matcher.group(1);
        String string3 = matcher.group(2);
        if (string3 != null) {
            if (string2.indexOf(46) >= 0) {
                string2 = string2.replace(",", "");
            } else if (string2.indexOf(44) >= 0) {
                string2 = string2.replace(',', '.');
            }
        } else {
            string2 = string2.replace(",", "");
        }
        try {
            double d = Double.parseDouble(string2);
            if (string3 != null) {
                switch (string3.toUpperCase(Locale.ROOT)) {
                    case "K": {
                        d *= 1000.0;
                        break;
                    }
                    case "M": {
                        d *= 1000000.0;
                        break;
                    }
                    case "B": {
                        d *= 1.0E9;
                    }
                }
            }
            return d;
        }
        catch (NumberFormatException numberFormatException) {
            return 0.0;
        }
    }
}


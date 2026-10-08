/*
 * Decompiled with CFR 0.152.
 */
package com.seastore.auto.seaats;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static boolean isAutoSellEnabled = false;
    public static long startTimeMs = 0L;
    public static int totalSellCount = 0;
    public static double totalMoney = 0.0;
    public static double delayMin = 1.0;
    public static double delayMax = 3.0;
    public static int trackerWidth = 180;
    public static boolean showTracker = true;
    public static double trackerScale = 1.0;
    public static int cycle1MinTicks = 20;
    public static int cycle1MaxTicks = 60;
    public static int cycle2MinTicks = 20;
    public static int cycle2MaxTicks = 60;
    public static int cycle3MinTicks = 20;
    public static int cycle3MaxTicks = 60;
    public static int cycle4MinTicks = 20;
    public static int cycle4MaxTicks = 60;
    public static int trackerHeight = 80;
    public static double textScale = 1.0;
    public static boolean antiPlayerEnabled = true;
    public static boolean enableAutoPay = false;
    public static int payIntervalMinutes = 60;
    public static String payRecipient = "";
    public static double payAmount = 0.0;
    public static boolean payAll = false;
    public static double payRandomMinMillions = 0.0;
    public static double payRandomMaxMillions = 0.0;
    public static String payWebhookUrl = "";
    public static boolean enableGhostFix = true;
    public static int ghostTimeoutMinutes = 1;
    public static String ghostHomeCommand = "home 1";
    public static boolean enableHome = true;
    public static boolean isHomeSet = false;
    public static double homeX;
    public static double homeY;
    public static double homeZ;
    public static int homeCommandIntervalMin;
    public static String homeCommand1;
    public static String homeCommand2;
    public static String webhookUrlProfit;
    public static String webhookUrlAlert;
    public static int profitIntervalMinutes;
    public static int noSellIntervalMinutes;
    public static String discordId;
    public static List<String> friendList;
    public static long lastMoneyReceivedMs;

    public static double clampTrackerScale(double d) {
        return Double.isFinite(d) ? Math.max(0.3, Math.min(1.0, d)) : 1.0;
    }

    static {
        homeCommandIntervalMin = 5;
        homeCommand1 = "home 1";
        homeCommand2 = "home 2";
        webhookUrlProfit = "";
        webhookUrlAlert = "";
        profitIntervalMinutes = 30;
        noSellIntervalMinutes = 15;
        discordId = "";
        friendList = new ArrayList<String>();
        lastMoneyReceivedMs = System.currentTimeMillis();
    }
}


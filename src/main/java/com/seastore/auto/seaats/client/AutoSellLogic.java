/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1703
 *  net.minecraft.class_1713
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_2561
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_433
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_634
 *  net.minecraft.class_746
 */
package com.seastore.auto.seaats.client;

import com.seastore.auto.seaats.Config;
import com.seastore.auto.seaats.client.ChatCommandSender;
import com.seastore.auto.seaats.client.PayBalanceReply;
import com.seastore.auto.seaats.client.PayReceipt;
import com.seastore.auto.seaats.client.SellCycleScheduler;
import com.seastore.auto.seaats.client.WebhookHelper;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_433;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_634;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public class AutoSellLogic {
    private static int tickDelay = 0;
    private static State currentState;
    private static final Random RANDOM;
    private static AntiPlayerState antiState;
    private static int antiTimer;
    private static class_1657 antiTarget;
    private static int pearlThrowCount;
    private static int pearlFailCount;
    private static boolean killCommandSent;
    private static boolean confirmClicked;
    private static boolean isPaying;
    private static long lastPayTimeMs;
    private static boolean payCommandSent;
    private static boolean wasAutoSellOnBeforePay;
    private static boolean isGhostFixing;
    private static int ghostTimer;
    private static int ghostStep;
    private static int ghostPearlCount;
    private static long lastSellCommandSentMs;
    private static int sellCommandRetryCount;
    private static final int MAX_SELL_RETRY = 3;
    private static final long SELL_OPEN_TIMEOUT_NANOS = 8000000000L;
    private static long sellOpenDeadlineNanos;
    private static long sellRetryNotBeforeNanos;
    private static int sellFailureRounds;
    private static class_634 sellConnection;
    private static class_746 sellPlayer;
    private static class_1703 knownSellHandler;
    private static long sellContentsDeadlineNanos;
    private static long lastSellTimeMs;
    private static long lastHomeCheckMs;
    private static boolean isHomeRecovering;
    private static boolean toggleHomeCmd;
    private static long lastProfitReportMs;
    private static class_304 ghostSneakKey;
    private static boolean wasAutoSellOnBeforeAnti;
    private static int expectedSellReceiptTicks;
    private static String lastPayAmountLabel;
    private static boolean payAllRequest;
    private static String payRecipientSnapshot;
    private static BigDecimal payBalance;
    private static long payBalanceDeadlineNanos;
    private static class_634 payConnection;
    private static BigDecimal payExpectedAmount;
    private static long payConfirmationDeadlineNanos;
    private static PayReceipt payReceipt;
    private static String payFailure;
    private static class_315 backgroundOptions;
    private static boolean savedPauseOnLostFocus;
    private static boolean pausedForScreen;
    private static long lastScreenResumeMs;

    private static void restoreBackgroundOptions() {
        if (backgroundOptions != null) {
            AutoSellLogic.backgroundOptions.field_1837 = savedPauseOnLostFocus;
            backgroundOptions = null;
        }
    }

    private static boolean automationSessionActive() {
        return Config.isAutoSellEnabled || isGhostFixing || isPaying && wasAutoSellOnBeforePay || antiState != AntiPlayerState.IDLE && wasAutoSellOnBeforeAnti;
    }

    private static void updateBackgroundOptions(class_310 class_3102) {
        if (!AutoSellLogic.automationSessionActive()) {
            AutoSellLogic.restoreBackgroundOptions();
            return;
        }
        if (backgroundOptions != class_3102.field_1690) {
            AutoSellLogic.restoreBackgroundOptions();
            backgroundOptions = class_3102.field_1690;
            savedPauseOnLostFocus = AutoSellLogic.backgroundOptions.field_1837;
        }
        AutoSellLogic.backgroundOptions.field_1837 = false;
    }

    public static void resetState() {
        AutoSellLogic.restoreBackgroundOptions();
        pausedForScreen = false;
        lastScreenResumeMs = 0L;
        AutoSellLogic.releaseGhostSneak();
        AutoSellLogic.resetSellControl();
        sellConnection = null;
        sellPlayer = null;
        SellCycleScheduler.reset();
        currentState = State.IDLE;
        tickDelay = 0;
        antiState = AntiPlayerState.IDLE;
        antiTimer = 0;
        antiTarget = null;
        wasAutoSellOnBeforeAnti = false;
        expectedSellReceiptTicks = 0;
        lastPayAmountLabel = "";
        AutoSellLogic.clearPayBalanceRequest();
        pearlThrowCount = 0;
        pearlFailCount = 0;
        killCommandSent = false;
        confirmClicked = false;
        isPaying = false;
        payCommandSent = false;
        wasAutoSellOnBeforePay = false;
        isGhostFixing = false;
        ghostTimer = 0;
        ghostStep = 0;
        ghostPearlCount = 0;
        lastSellCommandSentMs = 0L;
        sellCommandRetryCount = 0;
        isHomeRecovering = false;
        lastHomeCheckMs = 0L;
        toggleHomeCmd = false;
        Config.lastMoneyReceivedMs = System.currentTimeMillis();
    }

    public static boolean hasInventoryItems(class_310 class_3102) {
        if (class_3102.field_1724 == null) {
            return false;
        }
        class_1661 class_16612 = class_3102.field_1724.method_31548();
        for (int i = 0; i < 36; ++i) {
            if (class_16612.method_5438(i).method_7960()) continue;
            return true;
        }
        return false;
    }

    private static boolean isSellGuiOpen(class_310 class_3102) {
        class_437 class_4372 = class_3102.field_1755;
        if (!(class_4372 instanceof class_465)) {
            return false;
        }
        class_465 class_4652 = (class_465)class_4372;
        class_1703 class_17032 = class_4652.method_17577();
        if (class_3102.field_1724 == null || class_17032 != class_3102.field_1724.field_7512) {
            return false;
        }
        if (class_17032 == knownSellHandler) {
            return true;
        }
        int n = class_17032.field_7761.size() - 36;
        if (n < 0) {
            n = class_17032.field_7761.size();
        }
        for (int i = 0; i < n; ++i) {
            if (!class_17032.method_7611(i).method_7677().method_31574(class_1802.field_8581)) continue;
            knownSellHandler = class_17032;
            return true;
        }
        return false;
    }

    private static List<class_1657> getNearbyPlayers(class_310 class_3102, double d) {
        return class_3102.field_1687.method_8390(class_1657.class, class_3102.field_1724.method_5829().method_1014(d), class_16572 -> class_16572 != class_3102.field_1724 && !class_16572.method_7325());
    }

    private static boolean isFriend(String string) {
        if (Config.friendList == null) {
            return false;
        }
        for (String string2 : Config.friendList) {
            if (!string2.trim().equalsIgnoreCase(string.trim())) continue;
            return true;
        }
        return false;
    }

    private static void sendSellCommand(class_310 class_3102) {
        if (class_3102.method_1562() == null || System.nanoTime() - sellRetryNotBeforeNanos < 0L) {
            return;
        }
        if (AutoSellLogic.hasActiveMenu(class_3102) || class_3102.field_1755 instanceof class_465) {
            AutoSellLogic.closeAllGui(class_3102);
            sellRetryNotBeforeNanos = System.nanoTime() + 500000000L;
            return;
        }
        currentState = State.WAITING_GUI_OPEN;
        tickDelay = 0;
        lastSellCommandSentMs = System.currentTimeMillis();
        sellOpenDeadlineNanos = System.nanoTime() + 8000000000L;
        ++sellCommandRetryCount;
        try {
            ChatCommandSender.send("sell");
            System.out.println("[SeaATS] Requested /sell via chat; attempt=" + sellCommandRetryCount + ", waiting up to 8s for menu contents");
        }
        catch (RuntimeException runtimeException) {
            AutoSellLogic.scheduleSellRetry(class_3102, "command submission failed: " + runtimeException.getClass().getSimpleName());
        }
    }

    static boolean hasActiveMenu(class_310 class_3102) {
        return class_3102.field_1724 != null && class_3102.field_1724.field_7512 != class_3102.field_1724.field_7498;
    }

    static void closeAllGui(class_310 class_3102) {
        knownSellHandler = null;
        sellContentsDeadlineNanos = 0L;
        if (AutoSellLogic.hasActiveMenu(class_3102)) {
            int n = class_3102.field_1724.field_7512.field_7763;
            class_3102.field_1724.method_7346();
            System.out.println("[SeaATS] Closed active server menu; syncId=" + n);
        }
        if (class_3102.field_1755 != null) {
            class_3102.method_1507(null);
        }
    }

    private static void scheduleSellRetry(class_310 class_3102, String string) {
        int n;
        int n2 = class_3102.field_1724.field_7512.field_7763;
        class_437 class_4372 = class_3102.field_1755;
        if (class_4372 instanceof class_465) {
            class_465<?> handledScreen = (class_465<?>)class_4372;
            n = handledScreen.method_17577().field_7763;
        } else {
            n = -1;
        }
        int n3 = n;
        String screenName = class_3102.field_1755 == null ? "none" : class_3102.field_1755.getClass().getSimpleName();
        System.out.println("[SeaATS] Sell GUI recovery: " + string + ", attempt=" + sellCommandRetryCount + ", screen=" + screenName + ", activeSyncId=" + n2 + ", visibleSyncId=" + n3);
        AutoSellLogic.closeAllGui(class_3102);
        currentState = State.IDLE;
        tickDelay = 0;
        sellOpenDeadlineNanos = 0L;
        long l = 2L;
        if (sellCommandRetryCount >= 3) {
            l = Math.min(60L, 10L << Math.min(++sellFailureRounds - 1, 3));
            sellCommandRetryCount = 0;
            class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)("\u00a7e[SeaATS] GUI sell ch\u01b0a s\u1eb5n s\u00e0ng. \u0110\u00e3 d\u1ecdn phi\u00ean menu; th\u1eed l\u1ea1i sau " + l + " gi\u00e2y.")), false);
        }
        sellRetryNotBeforeNanos = System.nanoTime() + l * 1000000000L;
    }

    private static void resetSellControl() {
        currentState = State.IDLE;
        tickDelay = 0;
        lastSellCommandSentMs = 0L;
        sellCommandRetryCount = 0;
        sellOpenDeadlineNanos = 0L;
        sellRetryNotBeforeNanos = 0L;
        sellFailureRounds = 0;
        expectedSellReceiptTicks = 0;
        knownSellHandler = null;
        sellContentsDeadlineNanos = 0L;
    }

    private static boolean waitForSellContents(class_310 class_3102) {
        if (!AutoSellLogic.isSellGuiOpen(class_3102)) {
            return false;
        }
        class_1703 class_17032 = ((class_465)class_3102.field_1755).method_17577();
        int n = Math.max(0, class_17032.field_7761.size() - 36);
        for (int i = 0; i < n; ++i) {
            if (!class_17032.method_7611(i).method_7677().method_31574(class_1802.field_8581)) continue;
            sellContentsDeadlineNanos = 0L;
            return false;
        }
        if (sellContentsDeadlineNanos == 0L) {
            sellContentsDeadlineNanos = System.nanoTime() + 8000000000L;
        }
        if (System.nanoTime() - sellContentsDeadlineNanos >= 0L) {
            AutoSellLogic.scheduleSellRetry(class_3102, "confirmed sell menu contents stayed unavailable for 8s");
        }
        return true;
    }

    public static void toggleFromKey(class_310 class_3102) {
        boolean bl = !AutoSellLogic.automationSessionActive();
        AutoSellLogic.resetState();
        Config.isAutoSellEnabled = bl;
        if (class_3102.field_1724 != null && class_3102.field_1687 != null) {
            AutoSellLogic.updateBackgroundOptions(class_3102);
        }
        AutoSellLogic.closeAllGui(class_3102);
        sellConnection = class_3102.method_1562();
        sellPlayer = class_3102.field_1724;
        sellRetryNotBeforeNanos = System.nanoTime() + 500000000L;
        if (bl) {
            Config.startTimeMs = System.currentTimeMillis();
            Config.totalSellCount = 0;
            Config.totalMoney = 0.0;
        }
        if (class_3102.field_1724 != null) {
            if (bl) {
                class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)"\u00a7b[SeaATS] \u00a7a\u0110\u00e3 reset th\u1ed1ng k\u00ea!"), false);
            }
            class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)("\u00a7b[SeaATS] \u00a7fAuto Sell: " + (bl ? "\u00a7aB\u1eacT" : "\u00a7cT\u1eaeT"))), false);
        }
    }

    public static void tick(class_310 class_3102) {
        if (expectedSellReceiptTicks > 0) {
            --expectedSellReceiptTicks;
        }
        if (class_3102.field_1724 == null || class_3102.field_1687 == null || class_3102.method_1562() == null) {
            AutoSellLogic.restoreBackgroundOptions();
            pausedForScreen = false;
            lastScreenResumeMs = 0L;
            if (isPaying) {
                AutoSellLogic.finishAutoPay(class_3102, "");
            }
            if (isGhostFixing) {
                AutoSellLogic.resetState();
                Config.isAutoSellEnabled = true;
            }
            AutoSellLogic.resetSellControl();
            sellConnection = null;
            sellPlayer = null;
            return;
        }
        if (sellConnection != class_3102.method_1562() || sellPlayer != class_3102.field_1724) {
            pausedForScreen = false;
            lastScreenResumeMs = 0L;
            AutoSellLogic.resetSellControl();
            sellConnection = class_3102.method_1562();
            sellPlayer = class_3102.field_1724;
        }
        AutoSellLogic.updateBackgroundOptions(class_3102);
        if (!(!isPaying || Config.enableAutoPay && Config.payAll == payAllRequest && payRecipientSnapshot.equals(Config.payRecipient.trim()) && class_3102.method_1562() == payConnection)) {
            AutoSellLogic.finishAutoPay(class_3102, payCommandSent ? "D\u1eebng ch\u1edd Auto Pay v\u00ec c\u1ea5u h\u00ecnh ho\u1eb7c k\u1ebft n\u1ed1i thay \u0111\u1ed5i; l\u1ec7nh \u0111\u00e3 g\u1eedi c\u00f3 th\u1ec3 v\u1eabn \u0111\u01b0\u1ee3c server x\u1eed l\u00fd." : "\u0110\u00e3 h\u1ee7y l\u01b0\u1ee3t Auto Pay v\u00ec c\u1ea5u h\u00ecnh ho\u1eb7c k\u1ebft n\u1ed1i thay \u0111\u1ed5i.");
        }
        if (isPaying && payCommandSent) {
            AutoSellLogic.checkPayConfirmation(class_3102);
        }
        if (isPaying && payAllRequest && !payCommandSent && System.nanoTime() - payBalanceDeadlineNanos >= 0L) {
            AutoSellLogic.finishAutoPay(class_3102, "Kh\u00f4ng nh\u1eadn \u0111\u01b0\u1ee3c s\u1ed1 d\u01b0 /bal h\u1ee3p l\u1ec7 trong 10 gi\u00e2y; b\u1ecf qua l\u01b0\u1ee3t chuy\u1ec3n.");
        }
        if (!Config.enableHome || !Config.isHomeSet) {
            isHomeRecovering = false;
        }
        if (class_3102.field_1755 instanceof class_433 && AutoSellLogic.automationSessionActive()) {
            AutoSellLogic.closeAllGui(class_3102);
            AutoSellLogic.resetSellControl();
            sellRetryNotBeforeNanos = System.nanoTime() + 500000000L;
        }
        if (class_3102.field_1755 != null && !(class_3102.field_1755 instanceof class_465)) {
            if (isGhostFixing) {
                AutoSellLogic.finishGhostFix(class_3102);
            }
            pausedForScreen = true;
            return;
        }
        if (pausedForScreen) {
            pausedForScreen = false;
            lastScreenResumeMs = System.currentTimeMillis();
            AutoSellLogic.resetSellControl();
        }
        SellCycleScheduler.tick(Config.isAutoSellEnabled && !isPaying && !isGhostFixing && antiState == AntiPlayerState.IDLE && !isHomeRecovering);
        if (class_3102.field_1724 != null && class_3102.field_1687 != null) {
            long l;
            if (Config.enableGhostFix && Config.isAutoSellEnabled && !isGhostFixing) {
                l = (long)Math.max(1, Config.ghostTimeoutMinutes) * 60L * 1000L;
                if (System.currentTimeMillis() - Math.max(Config.lastMoneyReceivedMs, lastScreenResumeMs) > l) {
                    isGhostFixing = true;
                    ghostTimer = 0;
                    ghostStep = 0;
                    ghostPearlCount = 0;
                    Config.isAutoSellEnabled = false;
                    AutoSellLogic.closeAllGui(class_3102);
                    ghostSneakKey = class_3102.field_1690.field_1832;
                    AutoSellLogic.setGhostSneak(false);
                    String string = class_3102.field_1724.method_5477().getString();
                    WebhookHelper.sendAlert("**\ud83e\uddf9 Ch\u1ed1ng Ghost Item**\n" + string + " \u0111ang th\u1ef1c hi\u1ec7n ch\u1ed1ng ghost item...");
                    class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)"\u00a7e[SeaATS] Kh\u00f4ng nh\u1eadn \u0111\u01b0\u1ee3c ti\u1ec1n b\u00e1n. \u0110ang nh\u1ea5n Shift 2 l\u1ea7n \u0111\u1ec3 \u0111\u1ed3ng b\u1ed9 l\u1ea1i..."), false);
                    System.out.println("[SeaATS] Ghost fix triggered (no money received for " + Config.ghostTimeoutMinutes + " min)");
                    return;
                }
            }
            if (isGhostFixing) {
                AutoSellLogic.tickGhostFix(class_3102);
            } else {
                if (Config.enableAutoPay && !isPaying && antiState == AntiPlayerState.IDLE) {
                    l = (long)Config.payIntervalMinutes * 60L * 1000L;
                    if (System.currentTimeMillis() - lastPayTimeMs > l) {
                        AutoSellLogic.startAutoPay(class_3102);
                    }
                }
                if (isPaying) {
                    AutoSellLogic.tickAutoPay(class_3102);
                } else {
                    List<class_1657> list;
                    if (Config.antiPlayerEnabled && antiState == AntiPlayerState.IDLE && !(list = AutoSellLogic.getNearbyPlayers(class_3102, 5.0)).isEmpty() && !list.stream().anyMatch(class_16572 -> AutoSellLogic.isFriend(class_16572.method_5477().getString()))) {
                        AutoSellLogic.triggerAntiPlayer(class_3102, list.get(0));
                    }
                    if (antiState != AntiPlayerState.IDLE) {
                        AutoSellLogic.tickAntiPlayer(class_3102);
                    } else if (Config.isAutoSellEnabled && isHomeRecovering) {
                        AutoSellLogic.processHomeRecovery(class_3102);
                    } else if (Config.isAutoSellEnabled && Config.isHomeSet && Config.enableHome && class_3102.field_1724.method_5649(Config.homeX, Config.homeY, Config.homeZ) > 4.0) {
                        AutoSellLogic.handleMovedAway(class_3102);
                    } else if (!Config.isAutoSellEnabled) {
                        if (currentState != State.IDLE) {
                            AutoSellLogic.resetSellControl();
                        }
                    } else {
                        if (currentState != State.WAITING_GUI_OPEN && AutoSellLogic.waitForSellContents(class_3102)) {
                            return;
                        }
                        if (tickDelay > 0) {
                            --tickDelay;
                        } else {
                            if (currentState == State.WAITING_GUI_OPEN) {
                                if (!AutoSellLogic.isSellGuiOpen(class_3102)) {
                                    if (System.nanoTime() - sellOpenDeadlineNanos >= 0L) {
                                        AutoSellLogic.scheduleSellRetry(class_3102, "menu/confirm item did not become ready in 8s");
                                    }
                                    return;
                                }
                                sellCommandRetryCount = 0;
                                sellFailureRounds = 0;
                                sellRetryNotBeforeNanos = 0L;
                                sellOpenDeadlineNanos = 0L;
                                if (currentState == State.WAITING_GUI_OPEN) {
                                    currentState = State.MOVING_ITEMS;
                                    tickDelay = 2;
                                }
                            }
                            switch (currentState.ordinal()) {
                                case 0: {
                                    if (!AutoSellLogic.isSellGuiOpen(class_3102)) {
                                        AutoSellLogic.sendSellCommand(class_3102);
                                        break;
                                    }
                                    sellCommandRetryCount = 0;
                                    sellFailureRounds = 0;
                                    sellRetryNotBeforeNanos = 0L;
                                    sellOpenDeadlineNanos = 0L;
                                    currentState = State.MOVING_ITEMS;
                                    tickDelay = 2;
                                }
                                default: {
                                    break;
                                }
                                case 2: {
                                    int n;
                                    class_1703 class_17032;
                                    if (AutoSellLogic.isSellGuiOpen(class_3102)) {
                                        int n2;
                                        class_17032 = ((class_465)class_3102.field_1755).method_17577();
                                        int n3 = class_17032.field_7761.size() - 36;
                                        if (n3 < 0) {
                                            n3 = class_17032.field_7761.size();
                                        }
                                        n = 0;
                                        for (n2 = 0; n2 < n3; ++n2) {
                                            class_1799 class_17992 = class_17032.method_7611(n2).method_7677();
                                            if (class_17992.method_7960() || class_17992.method_31574(class_1802.field_8581)) continue;
                                            ++n;
                                        }
                                        if (n < 45 && n3 - n > 1) {
                                            n2 = class_17032.field_7761.size() - 36;
                                            if (n2 < 0) {
                                                n2 = 0;
                                            }
                                            boolean bl = false;
                                            for (int i = n2; i < class_17032.field_7761.size(); ++i) {
                                                if (class_17032.method_7611(i).method_7677().method_7960()) continue;
                                                class_3102.field_1761.method_2906(class_17032.field_7763, i, 0, class_1713.field_7794, (class_1657)class_3102.field_1724);
                                                bl = true;
                                            }
                                            if (bl) {
                                                tickDelay = 2;
                                                break;
                                            }
                                            currentState = State.CLICK_CONFIRM;
                                            tickDelay = 3;
                                            break;
                                        }
                                        currentState = State.CLICK_CONFIRM;
                                        tickDelay = 3;
                                        break;
                                    }
                                    currentState = State.IDLE;
                                    tickDelay = 10;
                                    sellCommandRetryCount = 0;
                                    break;
                                }
                                case 3: {
                                    int n;
                                    class_1703 class_17032;
                                    if (AutoSellLogic.isSellGuiOpen(class_3102)) {
                                        class_17032 = ((class_465)class_3102.field_1755).method_17577();
                                        int n4 = -1;
                                        n = class_17032.field_7761.size() - 36;
                                        if (n < 0) {
                                            n = class_17032.field_7761.size();
                                        }
                                        for (int i = 0; i < n; ++i) {
                                            if (!class_17032.method_7611(i).method_7677().method_31574(class_1802.field_8581)) continue;
                                            n4 = i;
                                            break;
                                        }
                                        if (n4 != -1) {
                                            expectedSellReceiptTicks = 200;
                                            class_3102.field_1761.method_2906(class_17032.field_7763, n4, 0, class_1713.field_7790, (class_1657)class_3102.field_1724);
                                            lastSellTimeMs = System.currentTimeMillis();
                                            sellCommandRetryCount = 0;
                                            tickDelay = SellCycleScheduler.nextDelayTicks();
                                            currentState = State.CLICK_CONFIRM_DELAYED;
                                            break;
                                        }
                                        currentState = State.IDLE;
                                        tickDelay = 10;
                                        break;
                                    }
                                    currentState = State.IDLE;
                                    break;
                                }
                                case 4: {
                                    currentState = State.MOVING_ITEMS;
                                    tickDelay = 2;
                                }
                            }
                            if (Config.isAutoSellEnabled) {
                                long l2 = (long)Config.profitIntervalMinutes * 60L * 1000L;
                                if (System.currentTimeMillis() - lastProfitReportMs > l2) {
                                    lastProfitReportMs = System.currentTimeMillis();
                                    long l3 = System.currentTimeMillis() - Config.startTimeMs;
                                    WebhookHelper.sendReport(AutoSellLogic.buildReport(class_3102, l3));
                                }
                            }
                            if (Config.isAutoSellEnabled && System.currentTimeMillis() - lastSellTimeMs > (long)Config.noSellIntervalMinutes * 60L * 1000L) {
                                String string = class_3102.field_1724.method_5477().getString();
                                WebhookHelper.sendAlert("**C\u1ea3nh b\u00e1o:** " + string + " \u0111\u00e3 " + Config.noSellIntervalMinutes + " ph\u00fat kh\u00f4ng b\u00e1n \u0111\u01b0\u1ee3c item n\u00e0o!");
                                lastSellTimeMs = System.currentTimeMillis();
                            }
                        }
                    }
                }
            }
        }
    }

    private static void startAutoPay(class_310 class_3102) {
        lastPayTimeMs = System.currentTimeMillis();
        AutoSellLogic.clearPayBalanceRequest();
        isPaying = true;
        payCommandSent = false;
        lastPayAmountLabel = "";
        wasAutoSellOnBeforePay = Config.isAutoSellEnabled;
        Config.isAutoSellEnabled = false;
        payAllRequest = Config.payAll;
        payRecipientSnapshot = Config.payRecipient.trim();
        payBalance = null;
        payBalanceDeadlineNanos = 0L;
        payConnection = class_3102.method_1562();
        AutoSellLogic.closeAllGui(class_3102);
        if (payRecipientSnapshot.isEmpty() || payRecipientSnapshot.matches(".*\\s.*")) {
            AutoSellLogic.finishAutoPay(class_3102, "Ch\u01b0a c\u00f3 t\u00ean ng\u01b0\u1eddi nh\u1eadn h\u1ee3p l\u1ec7; b\u1ecf qua l\u01b0\u1ee3t chuy\u1ec3n.");
            return;
        }
        if (payAllRequest) {
            payBalanceDeadlineNanos = System.nanoTime() + 10000000000L;
            AutoSellLogic.payMessage(class_3102, "\u0110ang \u0111\u1ecdc s\u1ed1 d\u01b0 /bal \u0111\u1ec3 chuy\u1ec3n to\u00e0n b\u1ed9 money...");
            try {
                ChatCommandSender.send("bal");
            }
            catch (RuntimeException runtimeException) {
                AutoSellLogic.finishAutoPay(class_3102, "Kh\u00f4ng g\u1eedi \u0111\u01b0\u1ee3c /bal; b\u1ecf qua l\u01b0\u1ee3t chuy\u1ec3n.");
            }
            return;
        }
        double d = Config.payRandomMinMillions;
        double d2 = Config.payRandomMaxMillions;
        if (!Double.isFinite(d) || !Double.isFinite(d2) || d < 0.0 || d2 < d || d2 <= 0.0) {
            AutoSellLogic.finishAutoPay(class_3102, "Kho\u1ea3ng ti\u1ec1n ng\u1eabu nhi\u00ean kh\u00f4ng h\u1ee3p l\u1ec7; b\u1ecf qua l\u01b0\u1ee3t chuy\u1ec3n.");
            return;
        }
        double d3 = d == d2 ? d : ThreadLocalRandom.current().nextDouble(d, d2);
        if ((d3 = Math.max(d, Math.min(d2, (double)Math.round(d3 * 100.0) / 100.0))) <= 0.0) {
            AutoSellLogic.finishAutoPay(class_3102, "S\u1ed1 ti\u1ec1n \u0111\u01b0\u1ee3c ch\u1ecdn b\u1eb1ng 0; b\u1ecf qua l\u01b0\u1ee3t chuy\u1ec3n.");
            return;
        }
        AutoSellLogic.sendAutoPay(class_3102, BigDecimal.valueOf(d3).movePointRight(6));
    }

    static boolean handlePayBalanceMessage(String string) {
        BigDecimal bigDecimal = PayBalanceReply.parse(string);
        if (bigDecimal == null) {
            return false;
        }
        class_310 class_3102 = class_310.method_1551();
        if (AutoSellLogic.isCurrentPayRequest(class_3102) && payAllRequest && !payCommandSent && payBalance == null && System.nanoTime() - payBalanceDeadlineNanos < 0L) {
            payBalance = bigDecimal;
        }
        return true;
    }

    private static void tickAutoPay(class_310 class_3102) {
        if (payAllRequest && !payCommandSent) {
            if (payBalance == null) {
                return;
            }
            if (payBalance.signum() <= 0) {
                AutoSellLogic.finishAutoPay(class_3102, "S\u1ed1 d\u01b0 /bal kh\u00f4ng l\u1edbn h\u01a1n 0; b\u1ecf qua l\u01b0\u1ee3t chuy\u1ec3n.");
                return;
            }
            AutoSellLogic.sendAutoPay(class_3102, payBalance);
        }
    }

    private static void sendAutoPay(class_310 class_3102, BigDecimal bigDecimal) {
        if (payCommandSent || !AutoSellLogic.isCurrentPayRequest(class_3102)) {
            return;
        }
        String string = bigDecimal.stripTrailingZeros().toPlainString();
        payExpectedAmount = bigDecimal;
        lastPayAmountLabel = "$" + string;
        payCommandSent = true;
        payConfirmationDeadlineNanos = System.nanoTime() + 15000000000L;
        String string2 = "pay " + payRecipientSnapshot + " " + string;
        AutoSellLogic.payMessage(class_3102, "\u0110ang g\u1eedi /" + string2 + " qua chat; ch\u1edd server x\u00e1c nh\u1eadn...");
        try {
            ChatCommandSender.send(string2);
        }
        catch (RuntimeException runtimeException) {
            AutoSellLogic.finishAutoPay(class_3102, "G\u1eedi Auto Pay g\u1eb7p l\u1ed7i; ch\u01b0a x\u00e1c nh\u1eadn chuy\u1ec3n ti\u1ec1n. Kh\u00f4ng t\u1ef1 g\u1eedi l\u1ea1i l\u01b0\u1ee3t n\u00e0y.");
        }
    }

    private static boolean isCurrentPayRequest(class_310 class_3102) {
        return isPaying && Config.enableAutoPay && Config.payAll == payAllRequest && payRecipientSnapshot.equals(Config.payRecipient.trim()) && class_3102.field_1724 != null && class_3102.field_1687 != null && class_3102.method_1562() == payConnection;
    }

    static boolean handlePayConfirmation(String string) {
        PayReceipt payReceipt = PayReceipt.parse(string);
        boolean bl = PayReceipt.isInvalidNumber(string);
        if (payReceipt == null && !bl) {
            return false;
        }
        class_310 class_3102 = class_310.method_1551();
        if (AutoSellLogic.isCurrentPayRequest(class_3102) && payCommandSent && AutoSellLogic.payReceipt == null && payFailure == null && System.nanoTime() - payConfirmationDeadlineNanos < 0L) {
            if (payReceipt != null && payReceipt.matches(payRecipientSnapshot, payExpectedAmount)) {
                AutoSellLogic.payReceipt = payReceipt;
            } else if (bl) {
                payFailure = "Server t\u1eeb ch\u1ed1i s\u1ed1 ti\u1ec1n: That number is invalid.";
            }
        }
        return true;
    }

    private static void checkPayConfirmation(class_310 class_3102) {
        if (payReceipt != null) {
            String string = "Server \u0111\u00e3 x\u00e1c nh\u1eadn chuy\u1ec3n " + payReceipt.displayAmount() + " cho " + payReceipt.recipient() + ".";
            String string2 = String.format("**Auto Pay \u0111\u01b0\u1ee3c server x\u00e1c nh\u1eadn**\n**Ng\u01b0\u1eddi g\u1eedi:** %s\n**Ng\u01b0\u1eddi nh\u1eadn:** %s\n**S\u1ed1 ti\u1ec1n y\u00eau c\u1ea7u:** %s\n**Server x\u00e1c nh\u1eadn:** %s\n**Th\u1eddi gian:** %s", class_3102.field_1724.method_5477().getString(), payRecipientSnapshot, lastPayAmountLabel, payReceipt.displayAmount(), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            AutoSellLogic.finishAutoPay(class_3102, string);
            WebhookHelper.sendPay(string2);
        } else if (payFailure != null) {
            AutoSellLogic.finishAutoPay(class_3102, payFailure + " Ch\u01b0a x\u00e1c nh\u1eadn chuy\u1ec3n ti\u1ec1n; kh\u00f4ng t\u1ef1 g\u1eedi l\u1ea1i l\u01b0\u1ee3t n\u00e0y.");
        } else if (System.nanoTime() - payConfirmationDeadlineNanos >= 0L) {
            AutoSellLogic.finishAutoPay(class_3102, "Ch\u01b0a nh\u1eadn \u0111\u01b0\u1ee3c x\u00e1c nh\u1eadn Auto Pay t\u1eeb server sau 15 gi\u00e2y. Kh\u00f4ng t\u1ef1 g\u1eedi l\u1ea1i l\u01b0\u1ee3t n\u00e0y \u0111\u1ec3 tr\u00e1nh chuy\u1ec3n tr\u00f9ng.");
        }
    }

    private static void finishAutoPay(class_310 class_3102, String string) {
        if (wasAutoSellOnBeforePay) {
            Config.isAutoSellEnabled = true;
        }
        isPaying = false;
        payCommandSent = false;
        wasAutoSellOnBeforePay = false;
        lastPayTimeMs = System.currentTimeMillis();
        AutoSellLogic.clearPayBalanceRequest();
        currentState = State.IDLE;
        tickDelay = 10;
        lastSellCommandSentMs = 0L;
        sellCommandRetryCount = 0;
        Config.lastMoneyReceivedMs = System.currentTimeMillis();
        if (!string.isEmpty()) {
            AutoSellLogic.payMessage(class_3102, string);
        }
    }

    private static void clearPayBalanceRequest() {
        payAllRequest = false;
        payRecipientSnapshot = "";
        payBalance = null;
        payBalanceDeadlineNanos = 0L;
        payConnection = null;
        payExpectedAmount = null;
        payConfirmationDeadlineNanos = 0L;
        payReceipt = null;
        payFailure = null;
    }

    private static void payMessage(class_310 class_3102, String string) {
        if (class_3102.field_1724 != null) {
            class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)("\u00a7b[SeaATS] \u00a7e" + string)), false);
        }
    }

    private static void tickGhostFix(class_310 class_3102) {
        if (!Config.enableGhostFix) {
            AutoSellLogic.finishGhostFix(class_3102);
            return;
        }
        if (++ghostTimer == 5 || ghostTimer == 15) {
            AutoSellLogic.setGhostSneak(true);
        }
        if (ghostTimer == 10 || ghostTimer == 20) {
            AutoSellLogic.setGhostSneak(false);
        }
        if (ghostTimer >= 25) {
            AutoSellLogic.finishGhostFix(class_3102);
        }
    }

    private static void setGhostSneak(boolean bl) {
        if (ghostSneakKey == null || ghostSneakKey.method_1434() == bl) {
            return;
        }
        ghostSneakKey.method_23481(bl);
        if (!bl && ghostSneakKey.method_1434()) {
            ghostSneakKey.method_23481(true);
        }
    }

    private static void releaseGhostSneak() {
        AutoSellLogic.setGhostSneak(false);
        ghostSneakKey = null;
    }

    private static void finishGhostFix(class_310 class_3102) {
        AutoSellLogic.releaseGhostSneak();
        AutoSellLogic.resetSellControl();
        isGhostFixing = false;
        ghostTimer = 0;
        ghostStep = 0;
        ghostPearlCount = 0;
        Config.lastMoneyReceivedMs = System.currentTimeMillis();
        currentState = State.IDLE;
        tickDelay = 5;
        lastSellCommandSentMs = 0L;
        sellCommandRetryCount = 0;
        Config.isAutoSellEnabled = true;
        class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)"\u00a7a[SeaATS] \u0110\u00e3 \u0111\u1ed3ng b\u1ed9 l\u1ea1i, ti\u1ebfp t\u1ee5c Auto Sell."), false);
        System.out.println("[SeaATS] Ghost fix finished: two sneak cycles; auto sell resumed");
    }

    public static String trackerStatus() {
        if (pausedForScreen && AutoSellLogic.automationSessionActive()) {
            return "T\u1ea0M D\u1eeaNG GUI";
        }
        if (isGhostFixing) {
            return "\u0110\u1ed2NG B\u1ed8";
        }
        if (isPaying) {
            return payAllRequest && !payCommandSent ? "\u0110\u1eccC S\u1ed0 D\u01af" : "CH\u1edc PAY";
        }
        if (antiState != AntiPlayerState.IDLE) {
            return "B\u1ea2O V\u1ec6";
        }
        if (isHomeRecovering) {
            return "V\u1ec0 HOME";
        }
        if (Config.isAutoSellEnabled && (currentState == State.WAITING_GUI_OPEN || sellContentsDeadlineNanos != 0L)) {
            return "CH\u1edc GUI";
        }
        if (Config.isAutoSellEnabled && System.nanoTime() - sellRetryNotBeforeNanos < 0L) {
            return "TH\u1eec L\u1ea0I";
        }
        return Config.isAutoSellEnabled ? "\u0110ANG B\u00c1N" : "\u0110\u00c3 T\u1eaeT";
    }

    static boolean consumeExpectedSellReceipt() {
        if (expectedSellReceiptTicks <= 0) {
            return false;
        }
        expectedSellReceiptTicks = 0;
        return true;
    }

    private static void triggerAntiPlayer(class_310 class_3102, class_1657 class_16572) {
        wasAutoSellOnBeforeAnti = Config.isAutoSellEnabled;
        Config.isAutoSellEnabled = false;
        if (class_3102.field_1755 != null) {
            class_3102.field_1724.method_7346();
        }
        antiTarget = class_16572;
        antiState = AntiPlayerState.THROWING_PEARL;
        antiTimer = 0;
        pearlThrowCount = 0;
        pearlFailCount = 0;
        killCommandSent = false;
        confirmClicked = false;
        String string = class_3102.field_1724.method_5477().getString();
        String string2 = class_16572.method_5477().getString();
        String string3 = String.format("**%s** ph\u00e1t hi\u1ec7n ng\u01b0\u1eddi l\u1ea1 **%s** trong v\u00f2ng 5 block! \u0110ang k\u00edch ho\u1ea1t b\u1ea3o v\u1ec7...", string, string2);
        WebhookHelper.sendAlert(string3);
        class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)"\u00a7c[SeaATS] Ph\u00e1t hi\u1ec7n ng\u01b0\u1eddi l\u1ea1! K\u00edch ho\u1ea1t b\u1ea3o v\u1ec7..."), false);
    }

    private static void tickAntiPlayer(class_310 class_3102) {
        if (antiState == AntiPlayerState.WAITING_CLEAR) {
            boolean bl;
            boolean bl2 = bl = Config.antiPlayerEnabled && AutoSellLogic.getNearbyPlayers(class_3102, 5.0).stream().anyMatch(class_16572 -> !AutoSellLogic.isFriend(class_16572.method_5477().getString()));
            if (!bl) {
                antiState = AntiPlayerState.IDLE;
                if (wasAutoSellOnBeforeAnti) {
                    Config.isAutoSellEnabled = true;
                    currentState = State.IDLE;
                    tickDelay = 10;
                    sellCommandRetryCount = 0;
                    lastSellCommandSentMs = 0L;
                    Config.lastMoneyReceivedMs = System.currentTimeMillis();
                }
                wasAutoSellOnBeforeAnti = false;
            }
            return;
        }
        if (antiTarget == null) {
            antiState = AntiPlayerState.IDLE;
        } else {
            ++antiTimer;
            switch (antiState.ordinal()) {
                case 1: {
                    if (antiTimer == 0 || antiTimer == 5) {
                        class_1799 class_17992 = class_3102.field_1724.method_31548().method_5438(40);
                        if (class_17992.method_7909() == class_1802.field_8634) {
                            class_3102.field_1761.method_2919((class_1657)class_3102.field_1724, class_1268.field_5810);
                            class_746 class_7462 = class_3102.field_1724;
                            int n = ++pearlThrowCount;
                            class_7462.method_7353((class_2561)class_2561.method_43470((String)("\u00a7a[SeaATS] \u0110\u00e3 n\u00e9m ender pearl l\u1ea7n " + n)), false);
                        } else {
                            class_746 class_7463 = class_3102.field_1724;
                            int n = ++pearlFailCount;
                            class_7463.method_7353((class_2561)class_2561.method_43470((String)("\u00a7c[SeaATS] Kh\u00f4ng c\u00f3 ender pearl trong tay tr\u00e1i! (l\u1ea7n th\u1eed " + n + ")")), false);
                            if (pearlFailCount >= 2) {
                                class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)"\u00a7c[SeaATS] Kh\u00f4ng c\u00f3 ender pearl sau 2 l\u1ea7n th\u1eed, d\u1eebng b\u1ea3o v\u1ec7."), false);
                                antiState = AntiPlayerState.DONE;
                                break;
                            }
                        }
                    }
                    if (antiTimer < 10 || pearlFailCount >= 2) break;
                    antiState = AntiPlayerState.WAITING_KILL;
                    antiTimer = 0;
                    class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)"\u00a7a[SeaATS] Ch\u1edd 2 gi\u00e2y \u0111\u1ec3 /kill..."), false);
                    break;
                }
                case 2: {
                    if (antiTimer < 40) break;
                    if (class_3102.method_1562() != null) {
                        class_3102.method_1562().method_45730("kill");
                        killCommandSent = true;
                        class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)"\u00a7a[SeaATS] \u0110\u00e3 g\u1eedi l\u1ec7nh /kill!"), false);
                    }
                    antiState = AntiPlayerState.CLICK_CONFIRM;
                    antiTimer = 0;
                    break;
                }
                case 3: {
                    if (antiTimer < 20) break;
                    class_437 class_4372 = class_3102.field_1755;
                    if (class_4372 instanceof class_465) {
                        class_465 class_4652 = (class_465)class_4372;
                        class_1703 class_17032 = class_4652.method_17577();
                        int n = -1;
                        int n2 = class_17032.field_7761.size() - 36;
                        if (n2 < 0) {
                            n2 = class_17032.field_7761.size();
                        }
                        for (int i = 0; i < n2; ++i) {
                            if (!class_17032.method_7611(i).method_7677().method_31574(class_1802.field_8581)) continue;
                            n = i;
                            break;
                        }
                        if (n == -1 && class_17032.field_7761.size() > 44) {
                            n = 44;
                        }
                        if (n != -1) {
                            class_3102.field_1761.method_2906(class_17032.field_7763, n, 0, class_1713.field_7790, (class_1657)class_3102.field_1724);
                            confirmClicked = true;
                            class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)"\u00a7a[SeaATS] \u0110\u00e3 click confirm!"), false);
                            antiState = AntiPlayerState.DONE;
                            break;
                        }
                        if (++antiTimer <= 100) break;
                        class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)"\u00a7c[SeaATS] Kh\u00f4ng t\u00ecm th\u1ea5y GUI confirm!"), false);
                        antiState = AntiPlayerState.DONE;
                        break;
                    }
                    if (antiTimer <= 100) break;
                    class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)"\u00a7c[SeaATS] GUI confirm kh\u00f4ng xu\u1ea5t hi\u1ec7n!"), false);
                    antiState = AntiPlayerState.DONE;
                    break;
                }
                case 4: {
                    class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)"\u00a7a[SeaATS] Ho\u00e0n th\u00e0nh b\u1ea3o v\u1ec7."), false);
                    antiState = wasAutoSellOnBeforeAnti ? AntiPlayerState.WAITING_CLEAR : AntiPlayerState.IDLE;
                    antiTarget = null;
                    antiTimer = 0;
                    pearlThrowCount = 0;
                    pearlFailCount = 0;
                    killCommandSent = false;
                    confirmClicked = false;
                }
            }
        }
    }

    private static void handleMovedAway(class_310 class_3102) {
        if (class_3102.field_1755 != null) {
            class_3102.field_1724.method_7346();
        }
        tickDelay = 100;
        isHomeRecovering = true;
        lastHomeCheckMs = System.currentTimeMillis();
        String string = class_3102.field_1724.method_5477().getString();
        WebhookHelper.sendAlert("**C\u1ea3nh b\u00e1o:** " + string + " \u0111\u00e3 di chuy\u1ec3n ra kh\u1ecfi khu v\u1ef1c HomeSet!");
    }

    private static void processHomeRecovery(class_310 class_3102) {
        if (class_3102.field_1724.method_5649(Config.homeX, Config.homeY, Config.homeZ) <= 4.0) {
            isHomeRecovering = false;
            currentState = State.IDLE;
            tickDelay = 5;
            class_3102.field_1724.method_7353((class_2561)class_2561.method_43470((String)"\u00a7a[SeaATS] \u0110\u00e3 v\u1ec1 \u0111\u00fang v\u1ecb tr\u00ed HomeSet."), false);
        } else {
            long l = System.currentTimeMillis();
            if (l - lastHomeCheckMs > (long)Config.homeCommandIntervalMin * 60L * 1000L) {
                lastHomeCheckMs = l;
                String string = toggleHomeCmd ? Config.homeCommand2 : Config.homeCommand1;
                boolean bl = toggleHomeCmd = !toggleHomeCmd;
                if (class_3102.method_1562() != null) {
                    class_3102.method_1562().method_45730(string.replace("/", ""));
                }
            }
        }
    }

    private static String buildReport(class_310 class_3102, long l) {
        double d = (double)l / 3600000.0;
        double d2 = Config.totalMoney;
        String string = class_3102.field_1724.method_5477().getString();
        String string2 = class_3102.method_1558() != null ? class_3102.method_1558().field_3761 : "Unknown";
        String string3 = AutoSellLogic.formatTime(l);
        double d3 = d > 0.0 ? d2 / d : 0.0;
        return String.format("**\u26cf\ufe0f B\u00e1o C\u00e1o Treo M\u00e1y \u0110\u1ecbnh K\u1ef3**\n**\ud83d\udc64 Ng\u01b0\u1eddi ch\u01a1i:** %s\n**\ud83d\udda5\ufe0f Server:** %s\n**\u23f1\ufe0f Th\u1eddi gian treo:** %s\n**\ud83d\udcb0 T\u1ed5ng thu nh\u1eadp:** %s\n**\ud83d\udcc8 T\u1ed1c \u0111\u1ed9 trung b\u00ecnh:** %s\n**\ud83d\udce6 S\u1ed1 l\u01b0\u1ee3t b\u00e1n:** %d\n**\ud83d\udfe2 Tr\u1ea1ng th\u00e1i:** \u0110ang treo m\u00e1y...", string, string2, string3, AutoSellLogic.formatMoney(d2), AutoSellLogic.formatMoney(d3) + "/h", Config.totalSellCount);
    }

    private static String formatMoney(double d) {
        if (d >= 1.0E9) {
            return String.format("$%.2fB", d / 1.0E9);
        }
        if (d >= 1000000.0) {
            return String.format("$%.2fM", d / 1000000.0);
        }
        return d >= 1000.0 ? String.format("$%.2fK", d / 1000.0) : String.format("$%.0f", d);
    }

    private static String formatTime(long l) {
        long l2 = l / 1000L;
        long l3 = l2 / 3600L;
        long l4 = l2 % 3600L / 60L;
        long l5 = l2 % 60L;
        return String.format("%02d:%02d:%02d", l3, l4, l5);
    }

    static {
        lastPayAmountLabel = "";
        payRecipientSnapshot = "";
        currentState = State.IDLE;
        RANDOM = new Random();
        antiState = AntiPlayerState.IDLE;
        antiTimer = 0;
        antiTarget = null;
        pearlThrowCount = 0;
        pearlFailCount = 0;
        killCommandSent = false;
        confirmClicked = false;
        isPaying = false;
        lastPayTimeMs = System.currentTimeMillis();
        payCommandSent = false;
        wasAutoSellOnBeforePay = false;
        isGhostFixing = false;
        ghostTimer = 0;
        ghostStep = 0;
        ghostPearlCount = 0;
        lastSellCommandSentMs = 0L;
        sellCommandRetryCount = 0;
        lastSellTimeMs = System.currentTimeMillis();
        lastHomeCheckMs = 0L;
        isHomeRecovering = false;
        toggleHomeCmd = false;
        lastProfitReportMs = System.currentTimeMillis();
    }

    @Environment(value=EnvType.CLIENT)
    private static enum AntiPlayerState {
        IDLE,
        THROWING_PEARL,
        WAITING_KILL,
        CLICK_CONFIRM,
        DONE,
        WAITING_CLEAR;

    }

    @Environment(value=EnvType.CLIENT)
    private static enum State {
        IDLE,
        WAITING_GUI_OPEN,
        MOVING_ITEMS,
        CLICK_CONFIRM,
        CLICK_CONFIRM_DELAYED;

    }
}


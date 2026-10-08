/*
 * Decompiled with CFR 0.152.
 */
package com.seastore.auto.seaats.client;

import com.seastore.auto.seaats.Config;
import java.util.Random;

public final class SellCycleScheduler {
    static final int PERIOD_TICKS = 1200;
    private static final Random RANDOM = new Random();
    private static final int[] order = new int[]{0, 1, 2, 3};
    private static int cursor = 4;
    private static int currentCycle = -1;
    private static int selectedDelay = 0;
    private static int round = 0;
    private static int periodTicks = 0;

    private SellCycleScheduler() {
    }

    public static void reset() {
        cursor = 4;
        currentCycle = -1;
        selectedDelay = 0;
        round = 0;
        periodTicks = 0;
    }

    public static void tick(boolean bl) {
        if (!bl) {
            return;
        }
        SellCycleScheduler.ensureCycle();
        if (++periodTicks >= 1200) {
            SellCycleScheduler.selectNextCycle();
        }
    }

    public static int nextDelayTicks() {
        SellCycleScheduler.ensureCycle();
        int[] nArray = new int[]{Config.cycle1MinTicks, Config.cycle2MinTicks, Config.cycle3MinTicks, Config.cycle4MinTicks};
        int[] nArray2 = new int[]{Config.cycle1MaxTicks, Config.cycle2MaxTicks, Config.cycle3MaxTicks, Config.cycle4MaxTicks};
        int n = Math.max(1, Math.min(72000, nArray[currentCycle]));
        int n2 = Math.max(n, Math.min(72000, nArray2[currentCycle]));
        selectedDelay = n + RANDOM.nextInt(n2 - n + 1);
        return selectedDelay;
    }

    private static void ensureCycle() {
        if (currentCycle < 0) {
            SellCycleScheduler.selectNextCycle();
        }
    }

    private static void selectNextCycle() {
        if (cursor == 4) {
            int n;
            for (n = 0; n < 4; ++n) {
                SellCycleScheduler.order[n] = n;
            }
            for (n = 3; n > 0; --n) {
                int n2 = RANDOM.nextInt(n + 1);
                int n3 = order[n];
                SellCycleScheduler.order[n] = order[n2];
                SellCycleScheduler.order[n2] = n3;
            }
            cursor = 0;
            ++round;
        }
        currentCycle = order[cursor++];
        selectedDelay = 0;
        periodTicks = 0;
    }

    public static int currentCycle() {
        return currentCycle + 1;
    }

    public static int position() {
        return currentCycle < 0 ? 0 : cursor;
    }

    public static int selectedDelay() {
        return selectedDelay;
    }

    public static int secondsRemaining() {
        return currentCycle < 0 ? 60 : Math.max(0, (1200 - periodTicks + 19) / 20);
    }

    public static int round() {
        return round;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_342
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 */
package com.seastore.auto.seaats.client;

import com.seastore.auto.seaats.Config;
import com.seastore.auto.seaats.ModConfig;
import com.seastore.auto.seaats.client.SeaButton;
import com.seastore.auto.seaats.client.SellCycleScheduler;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_465;

public final class SeaSettingsScreen
extends class_437 {
    private static final String[] TABS = new String[]{"Auto Sell", "Tracker", "B\u1ea3o v\u1ec7", "Auto Pay", "Discord"};
    private static final String[] HELP = new String[]{"MIN / MAX (tick) \u00b7 \u0110\u1ee7 4 chu k\u1ef3, x\u00e1o tr\u1ed9n m\u1ed7i v\u00f2ng.", "N\u1ec1n trong su\u1ed1t \u00b7 K\u00edch th\u01b0\u1edbc \u0111\u1ed5i \u0111\u1ed3ng b\u1ed9 c\u1ea3 b\u1ea3ng v\u00e0 ch\u1eef.", "Ghost Fix: t\u1eaft b\u00e1n \u2192 \u0111\u00f3ng GUI \u2192 nh\u1ea5n/nh\u1ea3 Shift 2 l\u1ea7n \u2192 b\u00e1n l\u1ea1i.", "Pay All: \u0111\u1ecdc /bal r\u1ed3i chuy\u1ec3n \u0111\u00fang s\u1ed1 d\u01b0. Ho\u1eb7c ch\u1ecdn MIN\u2013MAX tri\u1ec7u.", "B\u00e1o c\u00e1o v\u00e0 c\u1ea3nh b\u00e1o. \u0110\u1ec3 tr\u1ed1ng webhook n\u1ebfu kh\u00f4ng s\u1eed d\u1ee5ng."};
    private final class_437 parent;
    private final List<List<Entry>> groups = new ArrayList<List<Entry>>();
    private final Map<String, String> draft = new LinkedHashMap<String, String>();
    private int tab;
    private int offset;
    private int x;
    private int y;
    private int w;
    private int h;
    private int side;
    private int contentX;
    private int contentW;
    private int rows;
    private int startY;
    private int rowHeight;
    private String error = "";

    public SeaSettingsScreen(class_437 class_4372) {
        super((class_2561)class_2561.method_43470((String)"Sea Sell \u00b7 C\u00e0i \u0111\u1eb7t"));
        int n;
        this.parent = class_4372;
        for (n = 0; n < TABS.length; ++n) {
            this.groups.add(new ArrayList());
        }
        for (n = 1; n <= 4; ++n) {
            this.add(0, "Chu k\u1ef3 " + n, "cycle" + n + "MinTicks", "cycle" + n + "MaxTicks", 1.0, 72000.0, "MIN / MAX \u00b7 tick");
        }
        this.add(1, "Hi\u1ec7n tracker", "showTracker", null, 0.0, 0.0, "B\u1eadt / t\u1eaft b\u1ea3ng theo d\u00f5i");
        this.add(1, "K\u00edch th\u01b0\u1edbc", "trackerScale", null, 0.3, 1.0, "0.3\u20131.0 \u00b7 30%\u2013100%");
        this.add(2, "Ch\u1ed1ng ghost", "enableGhostFix", null, 0.0, 0.0, "\u0110\u1ed3ng b\u1ed9 b\u1eb1ng Shift 2 l\u1ea7n");
        this.add(2, "Ch\u1edd m\u1ea5t thu nh\u1eadp", "ghostTimeoutMinutes", null, 1.0, 10.0, "1\u201310 ph\u00fat tr\u01b0\u1edbc khi x\u1eed l\u00fd");
        this.add(2, "Ch\u1ed1ng ng\u01b0\u1eddi l\u1ea1", "antiPlayerEnabled", null, 0.0, 0.0, "B\u00e1n l\u1ea1i khi ng\u01b0\u1eddi l\u1ea1 r\u1eddi \u0111i");
        this.add(2, "B\u1ea1n b\u00e8", "friendList", null, 0.0, 0.0, "T\u00ean c\u00e1ch nhau b\u1eb1ng d\u1ea5u ph\u1ea9y");
        this.add(2, "T\u1ef1 v\u1ec1 home", "enableHome", null, 0.0, 0.0, "S\u1eed d\u1ee5ng v\u1ecb tr\u00ed HomeSet \u0111\u00e3 l\u01b0u");
        this.add(2, "Th\u1eed l\u1ea1i home", "homeCommandIntervalMin", null, 1.0, 30.0, "1\u201330 ph\u00fat");
        this.add(2, "Home ch\u00ednh", "homeCommand1", null, 0.0, 0.0, "V\u00ed d\u1ee5: home 1");
        this.add(2, "Home d\u1ef1 ph\u00f2ng", "homeCommand2", null, 0.0, 0.0, "V\u00ed d\u1ee5: home 2");
        this.add(3, "T\u1ef1 chuy\u1ec3n ti\u1ec1n", "enableAutoPay", null, 0.0, 0.0, "B\u1eadt / t\u1eaft Auto Pay");
        this.add(3, "Chu k\u1ef3 chuy\u1ec3n", "payIntervalMinutes", null, 1.0, 1440.0, "1\u20131440 ph\u00fat");
        this.add(3, "Ng\u01b0\u1eddi nh\u1eadn", "payRecipient", null, 0.0, 0.0, "T\u00ean nh\u00e2n v\u1eadt trong game");
        this.add(3, "Ch\u1ebf \u0111\u1ed9 Pay All", "payAll", null, 0.0, 0.0, "B\u1eacT: \u0111\u1ecdc /bal r\u1ed3i chuy\u1ec3n s\u1ed1 d\u01b0");
        this.add(3, "Pay ng\u1eabu nhi\u00ean", "payRandomMinMillions", "payRandomMaxMillions", 0.0, 999999.0, "MIN / MAX \u00b7 \u0111\u01a1n v\u1ecb tri\u1ec7u (M)");
        this.add(3, "Webhook chuy\u1ec3n", "payWebhookUrl", null, 0.0, 0.0, "URL th\u00f4ng b\u00e1o Auto Pay");
        this.add(4, "Webhook thu nh\u1eadp", "webhookUrlProfit", null, 0.0, 0.0, "URL b\u00e1o c\u00e1o l\u1ee3i nhu\u1eadn");
        this.add(4, "B\u00e1o c\u00e1o m\u1ed7i", "profitIntervalMinutes", null, 1.0, 120.0, "1\u2013120 ph\u00fat");
        this.add(4, "Webhook c\u1ea3nh b\u00e1o", "webhookUrlAlert", null, 0.0, 0.0, "URL th\u00f4ng b\u00e1o s\u1ef1 c\u1ed1");
        this.add(4, "C\u1ea3nh b\u00e1o sau", "noSellIntervalMinutes", null, 1.0, 60.0, "1\u201360 ph\u00fat kh\u00f4ng b\u00e1n");
        this.add(4, "Discord ID", "discordId", null, 0.0, 0.0, "ID nh\u1eadn th\u00f4ng b\u00e1o");
        this.draft.put("trackerScale", Double.toString(Config.clampTrackerScale(Config.trackerScale)));
    }

    private void add(int n, String string, String string2, String string3, double d, double d2, String string4) {
        this.groups.get(n).add(new Entry(string, string2, string3, d, d2, string4));
        this.read(string2);
        if (string3 != null) {
            this.read(string3);
        }
    }

    private void read(String string) {
        try {
            String string2;
            Object object = Config.class.getField(string).get(null);
            if (object instanceof List) {
                List list = (List)object;
                string2 = String.join((CharSequence)", ", list.stream().map(Object::toString).toList());
            } else {
                string2 = Objects.toString(object, "");
            }
            this.draft.put(string, string2);
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            throw new IllegalStateException(reflectiveOperationException);
        }
    }

    protected void method_25426() {
        this.rebuild();
    }

    private void rebuild() {
        int n;
        this.method_37067();
        this.w = Math.min(640, this.field_22789 - 20);
        this.h = Math.min(358, this.field_22790 - 20);
        this.x = (this.field_22789 - this.w) / 2;
        this.y = (this.field_22790 - this.h) / 2;
        this.side = this.w < 440 ? 88 : 112;
        this.contentX = this.x + this.side + 16;
        this.contentW = this.w - this.side - 30;
        this.startY = this.y + 76;
        this.rowHeight = this.tab == 0 ? 26 : 32;
        this.rows = Math.max(1, (this.h - 114) / this.rowHeight);
        this.offset = Math.max(0, Math.min(this.offset, Math.max(0, this.groups.get(this.tab).size() - this.rows)));
        for (int i = 0; i < TABS.length; ++i) {
            int tabIndex = i;
            this.button((i == this.tab ? "\u00a7b" : "\u00a77") + TABS[i], this.x + 8, this.y + 49 + i * 25, this.side - 8, 20, () -> {
                this.tab = tabIndex;
                this.offset = 0;
                this.error = "";
                this.rebuild();
            });
        }
        List<Entry> list = this.groups.get(this.tab);
        for (n = this.offset; n < Math.min(list.size(), this.offset + this.rows); ++n) {
            int n2;
            Entry entry = list.get(n);
            int n3 = this.startY + (n - this.offset) * this.rowHeight;
            int n4 = Math.max(80, Math.min(156, this.contentW / 2));
            int n5 = this.contentX + this.contentW;
            if (entry.maxKey() != null) {
                n2 = (n4 - 8) / 2;
                this.field(entry.key(), entry.label() + " t\u1ed1i thi\u1ec3u", n5 - n4, n3 + 5, n2);
                this.field(entry.maxKey(), entry.label() + " t\u1ed1i \u0111a", n5 - n2, n3 + 5, n2);
                continue;
            }
            if (SeaSettingsScreen.isBoolean(entry.key())) {
                n2 = Boolean.parseBoolean(this.draft.get(entry.key())) ? 1 : 0;
                this.button(n2 != 0 ? "\u00a7aB\u1eacT" : "\u00a77T\u1eaeT", n5 - 64, n3 + 4, 64, 20, () -> {
                    this.draft.put(entry.key(), Boolean.toString(!Boolean.parseBoolean(this.draft.get(entry.key()))));
                    this.rebuild();
                });
                continue;
            }
            this.field(entry.key(), entry.label(), n5 - n4, n3 + 5, n4);
        }
        n = this.y + this.h - 28;
        if (list.size() > this.rows) {
            class_4185 previousPage = this.button("\u2039", this.contentX, n, 22, 20, () -> {
                this.offset = Math.max(0, this.offset - this.rows);
                this.rebuild();
            });
            previousPage.field_22763 = this.offset > 0;
            class_4185 class_41852 = this.button("\u203a", this.contentX + 25, n, 22, 20, () -> {
                this.offset = Math.min(Math.max(0, list.size() - this.rows), this.offset + this.rows);
                this.rebuild();
            });
            class_41852.field_22763 = this.offset + this.rows < list.size();
        }
        this.button("H\u1ee7y", this.x + this.w - 148, n, 56, 20, this::method_25419);
        this.button("\u00a7bL\u01b0u & \u0111\u00f3ng", this.x + this.w - 86, n, 76, 20, this::save);
    }

    private class_4185 button(String string, int n, int n2, int n3, int n4, Runnable runnable) {
        return this.method_37063(new SeaButton(n, n2, n3, n4, string, class_41852 -> runnable.run()));
    }

    private void field(String string, String string3, int n, int n2, int n3) {
        class_342 class_3422 = new class_342(this.field_22793, n, n2, n3, 18, (class_2561)class_2561.method_43470((String)string3));
        class_3422.method_1880(string.toLowerCase(Locale.ROOT).contains("webhook") ? 2048 : 512);
        class_3422.method_1852(this.draft.get(string));
        class_3422.method_1863(string2 -> {
            this.draft.put(string, (String)string2);
            this.error = "";
        });
        this.method_37063(class_3422);
    }

    private static boolean isBoolean(String string) {
        try {
            return Config.class.getField(string).getType() == Boolean.TYPE;
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            throw new IllegalStateException(reflectiveOperationException);
        }
    }

    private Object parse(Entry entry, String string2) throws ReflectiveOperationException {
        Class<?> clazz = Config.class.getField(string2).getType();
        String string3 = this.draft.get(string2).trim();
        if (clazz == Boolean.TYPE) {
            return Boolean.parseBoolean(string3);
        }
        if (clazz == String.class) {
            return string3;
        }
        if (clazz == List.class) {
            return new ArrayList<String>(Arrays.stream(string3.split(",")).map(String::trim).filter(string -> !string.isEmpty()).distinct().toList());
        }
        try {
            double d = Double.parseDouble(string3.replace(',', '.'));
            if (!Double.isFinite(d) || d < entry.min() || d > entry.max() || clazz == Integer.TYPE && d != Math.rint(d)) {
                throw new NumberFormatException();
            }
            if (clazz == Integer.TYPE) {
                return (int)d;
            }
            return d;
        }
        catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException(entry.label() + ": nh\u1eadp " + entry.min() + "\u2013" + entry.max() + (clazz == Integer.TYPE ? " (s\u1ed1 nguy\u00ean)" : ""));
        }
    }

    private void save() {
        LinkedHashMap<Field, Object> linkedHashMap = new LinkedHashMap<Field, Object>();
        LinkedHashMap<Field, Object> linkedHashMap2 = new LinkedHashMap<Field, Object>();
        try {
            for (int i = 0; i < this.groups.size(); ++i) {
                for (Entry entry : this.groups.get(i)) {
                    try {
                        Object illegalAccessException = this.parse(entry, entry.key());
                        linkedHashMap.put(Config.class.getField(entry.key()), illegalAccessException);
                        if (entry.maxKey() == null) continue;
                        Object object = this.parse(entry, entry.maxKey());
                        if (((Number)illegalAccessException).doubleValue() > ((Number)object).doubleValue()) {
                            throw new IllegalArgumentException(entry.label() + ": Min ph\u1ea3i nh\u1ecf h\u01a1n ho\u1eb7c b\u1eb1ng Max.");
                        }
                        linkedHashMap.put(Config.class.getField(entry.maxKey()), object);
                    }
                    catch (IllegalArgumentException illegalArgumentException) {
                        this.tab = i;
                        this.offset = this.groups.get(i).indexOf(entry);
                        this.rebuild();
                        throw illegalArgumentException;
                    }
                }
            }
            for (Map.Entry entry : linkedHashMap.entrySet()) {
                linkedHashMap2.put((Field)entry.getKey(), ((Field)entry.getKey()).get(null));
                ((Field)entry.getKey()).set(null, entry.getValue());
            }
            ModConfig.save();
            boolean bl = linkedHashMap.keySet().stream().filter(field -> field.getName().startsWith("cycle")).anyMatch(field -> !Objects.equals(linkedHashMap2.get(field), linkedHashMap.get(field)));
            if (bl) {
                SellCycleScheduler.reset();
            }
            this.method_25419();
        }
        catch (Exception exception) {
            for (Map.Entry entry : linkedHashMap2.entrySet()) {
                try {
                    ((Field)entry.getKey()).set(null, entry.getValue());
                }
                catch (IllegalAccessException illegalAccessException) {}
            }
            this.error = exception.getMessage() == null ? "Kh\u00f4ng l\u01b0u \u0111\u01b0\u1ee3c c\u00e0i \u0111\u1eb7t." : exception.getMessage();
        }
    }

    public void method_25419() {
        class_437 class_4372 = this.parent;
        if (class_4372 instanceof class_465) {
            class_465 class_4652 = (class_465)class_4372;
            if (this.field_22787.field_1724 == null || class_4652.method_17577() != this.field_22787.field_1724.field_7512) {
                class_4372 = null;
            }
        }
        this.field_22787.method_1507(class_4372);
    }

    public boolean method_25421() {
        return false;
    }

    public boolean method_25401(double d, double d2, double d3, double d4) {
        if (d >= (double)this.contentX && d < (double)(this.contentX + this.contentW) && d2 >= (double)this.startY && d2 < (double)(this.y + this.h - 35) && d4 != 0.0) {
            this.offset += d4 < 0.0 ? 1 : -1;
            this.rebuild();
            return true;
        }
        return super.method_25401(d, d2, d3, d4);
    }

    public void method_25394(class_332 class_3322, int n, int n2, float f) {
        class_3322.method_25294(0, 0, this.field_22789, this.field_22790, -1207431656);
        class_3322.method_25294(this.x + 3, this.y + 4, this.x + this.w + 3, this.y + this.h + 4, 0x60000000);
        class_3322.method_25294(this.x, this.y, this.x + this.w, this.y + this.h, -15721685);
        class_3322.method_25294(this.x, this.y, this.x + this.w, this.y + 2, -12396086);
        class_3322.method_25294(this.x, this.y + 2, this.x + this.side + 5, this.y + this.h, -15457738);
        this.text(class_3322, "\u00a7lSEA SELL", this.x + 13, this.y + 15, -1379076);
        this.text(class_3322, "F9 \u00b7 C\u00c0I \u0110\u1eb6T", this.x + 13, this.y + 29, -8415824);
        this.text(class_3322, "\u00a7l" + TABS[this.tab], this.contentX, this.y + 15, -1379076);
        this.text(class_3322, this.fit(this.tab == 0 ? "1 tick \u2248 0,05 gi\u00e2y \u00b7 20 tick \u2248 1 gi\u00e2y" : "T\u00f9y ch\u1ec9nh v\u00e0 l\u01b0u khi ho\u00e0n t\u1ea5t", this.contentW), this.contentX, this.y + 30, -7298118);
        class_3322.method_25294(this.contentX, this.y + 48, this.contentX + this.contentW, this.y + 71, -15060925);
        String string = this.error.isEmpty() ? HELP[this.tab] : this.error;
        this.text(class_3322, this.fit(string, this.contentW - 12), this.contentX + 6, this.y + 56, this.error.isEmpty() ? -5973784 : -25187);
        List<Entry> list = this.groups.get(this.tab);
        for (int i = this.offset; i < Math.min(list.size(), this.offset + this.rows); ++i) {
            Entry entry = list.get(i);
            int n3 = this.startY + (i - this.offset) * this.rowHeight;
            class_3322.method_25294(this.contentX - 3, n3, this.contentX + this.contentW + 3, n3 + this.rowHeight - 2, -15260104);
            int n4 = Math.max(80, Math.min(156, this.contentW / 2));
            int n5 = this.contentW - n4 - 12;
            this.text(class_3322, this.fit(entry.label(), n5), this.contentX + 4, n3 + (this.tab == 0 ? 9 : 5), -2102282);
            if (this.tab == 0) continue;
            this.text(class_3322, this.fit(entry.hint(), n5), this.contentX + 4, n3 + 18, -8217933);
        }
        class_3322.method_25294(this.contentX - 3, this.y + this.h - 35, this.contentX + this.contentW + 3, this.y + this.h - 34, -14073770);
        if (this.contentW > 295) {
            this.text(class_3322, this.offset + 1 + "\u2013" + Math.min(list.size(), this.offset + this.rows) + " / " + list.size(), this.contentX + 54, this.y + this.h - 22, -7298118);
        }
        super.method_25394(class_3322, n, n2, f);
    }

    private String fit(String string, int n) {
        return this.field_22793.method_27523(string, Math.max(10, n));
    }

    private void text(class_332 class_3322, String string, int n, int n2, int n3) {
        class_3322.method_51433(this.field_22793, string, n, n2, n3, false);
    }

    private record Entry(String label, String key, String maxKey, double min, double max, String hint) {
    }
}


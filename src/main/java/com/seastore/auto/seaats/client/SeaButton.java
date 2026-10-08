/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_4185
 *  net.minecraft.class_4185$class_4241
 */
package com.seastore.auto.seaats.client;

import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4185;

final class SeaButton
extends class_4185 {
    private final int bx;
    private final int by;
    private final int bw;
    private final int bh;
    private final String caption;
    private final boolean accent;
    private final boolean enabledToggle;

    SeaButton(int n, int n2, int n3, int n4, String string, class_4185.class_4241 class_42412) {
        super(n, n2, n3, n4, (class_2561)class_2561.method_43470((String)string), class_42412, class_4185.field_40754);
        this.bx = n;
        this.by = n2;
        this.bw = n3;
        this.bh = n4;
        this.caption = string.replaceAll("\u00a7.", "");
        this.accent = string.startsWith("\u00a7b");
        this.enabledToggle = string.startsWith("\u00a7a");
    }

    protected void method_75752(class_332 class_3322, int n, int n2, float f) {
        boolean bl;
        boolean bl2 = bl = this.field_22763 && (n >= this.bx && n < this.bx + this.bw && n2 >= this.by && n2 < this.by + this.bh || this.method_25370());
        int n3 = !this.field_22763 ? -15194568 : (bl ? -14005920 : (this.accent ? -14924211 : -14535093));
        class_3322.method_25294(this.bx, this.by, this.bx + this.bw, this.by + this.bh, n3);
        if (this.accent || bl) {
            class_3322.method_25294(this.bx, this.by, this.bx + 2, this.by + this.bh, -12396086);
        }
        int n4 = !this.field_22763 ? -10785407 : (this.enabledToggle ? -10362188 : (this.accent ? -8918561 : -2299402));
        class_327 class_3272 = class_310.method_1551().field_1772;
        String string = class_3272.method_27523(this.caption, this.bw - 8);
        class_3322.method_51433(class_3272, string, this.bx + (this.bw - class_3272.method_1727(string)) / 2, this.by + (this.bh - 8) / 2, n4, false);
    }
}


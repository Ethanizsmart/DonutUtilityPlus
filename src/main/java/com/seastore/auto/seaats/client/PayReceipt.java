/*
 * Decompiled with CFR 0.152.
 */
package com.seastore.auto.seaats.client;

import com.seastore.auto.seaats.client.ChatMoneyListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

record PayReceipt(String recipient, BigDecimal value, int unitPower, String displayAmount) {
    private static final String NUMBER = "(?:[0-9]{1,3}(?:,[0-9]{3})+|[0-9]+)(?:\\.[0-9]+)?";
    private static final Pattern REPLY = Pattern.compile("^You\\s+paid\\s+(\\S+)\\s+\\$\\s*((?:[0-9]{1,3}(?:,[0-9]{3})+|[0-9]+)(?:\\.[0-9]+)?)\\s*([KMB])?\\s*[.!]?$", 2);

    static PayReceipt parse(String string) {
        String string2;
        if (string == null) {
            return null;
        }
        Matcher matcher = REPLY.matcher(ChatMoneyListener.normalize(string));
        if (!matcher.matches()) {
            return null;
        }
        BigDecimal bigDecimal = new BigDecimal(matcher.group(2).replace(",", ""));
        if (bigDecimal.signum() <= 0) {
            return null;
        }
        int n = switch (string2 = matcher.group(3) == null ? "" : matcher.group(3).toUpperCase(Locale.ROOT)) {
            case "K" -> 3;
            case "M" -> 6;
            case "B" -> 9;
            default -> 0;
        };
        return new PayReceipt(matcher.group(1), bigDecimal, n, "$" + matcher.group(2) + string2);
    }

    boolean matches(String string, BigDecimal bigDecimal) {
        if (!this.recipient.equalsIgnoreCase(string) || bigDecimal == null) {
            return false;
        }
        if (this.unitPower == 0) {
            return this.value.compareTo(bigDecimal) == 0;
        }
        BigDecimal bigDecimal2 = bigDecimal.movePointLeft(this.unitPower);
        return bigDecimal2.setScale(this.value.scale(), RoundingMode.HALF_UP).compareTo(this.value) == 0 || bigDecimal2.setScale(this.value.scale(), RoundingMode.DOWN).compareTo(this.value) == 0;
    }

    static boolean isInvalidNumber(String string) {
        return string != null && ChatMoneyListener.normalize(string).equalsIgnoreCase("That number is invalid.");
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.seastore.auto.seaats.client;

import com.seastore.auto.seaats.client.ChatMoneyListener;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PayBalanceReply {
    private static final String NUMBER = "-?(?:[0-9]{1,3}(?:,[0-9]{3})+|[0-9]+)(?:\\.[0-9]+)?";
    private static final Pattern REPLY = Pattern.compile("^You\\s+have\\s+\\$\\s*(-?(?:[0-9]{1,3}(?:,[0-9]{3})+|[0-9]+)(?:\\.[0-9]+)?)\\s*[.!]?$", 2);

    private PayBalanceReply() {
    }

    static BigDecimal parse(String string) {
        if (string == null) {
            return null;
        }
        Matcher matcher = REPLY.matcher(ChatMoneyListener.normalize(string));
        if (!matcher.matches()) {
            return null;
        }
        return new BigDecimal(matcher.group(1).replace(",", ""));
    }
}


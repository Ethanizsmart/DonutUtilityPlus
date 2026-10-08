/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.seastore.auto.seaats.client;

import com.seastore.auto.seaats.Config;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class WebhookHelper {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static void sendProfit(String message) {
    }

    public static void sendReport(String message) {
        System.out.println("[SeaATS] Sending report to profit webhook. URL: " + Config.webhookUrlProfit);
        WebhookHelper.send(Config.webhookUrlProfit, message);
    }

    public static void sendAlert(String message) {
        Object content = message;
        if (Config.discordId != null && !Config.discordId.trim().isEmpty()) {
            content = (String)content + "\n<@" + Config.discordId.trim() + ">";
        }
        System.out.println("[SeaATS] Sending alert to alert webhook. URL: " + Config.webhookUrlAlert);
        WebhookHelper.send(Config.webhookUrlAlert, (String)content);
    }

    public static void sendPay(String message) {
        System.out.println("[SeaATS] Sending pay notification to pay webhook. URL: " + Config.payWebhookUrl);
        String finalMessage = "**\ud83e\ude99 Sea Auto Pay**\n" + message;
        WebhookHelper.send(Config.payWebhookUrl, finalMessage);
    }

    private static void send(String url, String content) {
        if (url == null || url.trim().isEmpty()) {
            System.out.println("[SeaATS] Webhook URL empty, skipping.");
            return;
        }
        try {
            String json = "{\"content\": " + WebhookHelper.jsonEscape(content) + "}";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
            HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
                int code = response.statusCode();
                if (code >= 200 && code < 300) {
                    System.out.println("[SeaATS] Webhook sent successfully.");
                } else {
                    System.err.println("[SeaATS] Webhook error: " + code + " - " + (String)response.body());
                }
            }).exceptionally(e -> {
                System.err.println("[SeaATS] Webhook send failed: " + e.getMessage());
                return null;
            });
        }
        catch (Exception e2) {
            System.err.println("[SeaATS] Webhook exception: " + e2.getMessage());
        }
    }

    private static String jsonEscape(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.seastore.auto.seaats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seastore.auto.seaats.Config;
import com.seastore.auto.seaats.ConfigPathCompat;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = ConfigPathCompat.getConfigDirCompat().resolve("seaats.json");

    public static void load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (FileReader fileReader = new FileReader(CONFIG_PATH.toFile());){
                ConfigData configData = (ConfigData)GSON.fromJson((Reader)fileReader, ConfigData.class);
                ModConfig.applyData(configData);
            }
            catch (IOException | RuntimeException exception) {
                exception.printStackTrace();
            }
        } else {
            ModConfig.save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent(), new FileAttribute[0]);
            Path path = CONFIG_PATH.resolveSibling("seaats.json.tmp");
            Files.writeString(path, (CharSequence)GSON.toJson((Object)ModConfig.gatherData()), StandardCharsets.UTF_8, new OpenOption[0]);
            try {
                Files.move(path, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException atomicMoveNotSupportedException) {
                Files.move(path, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException iOException) {
            throw new IllegalStateException("Kh\u00f4ng ghi \u0111\u01b0\u1ee3c seaats.json: " + iOException.getMessage(), iOException);
        }
    }

    private static void applyData(ConfigData configData) {
        if (configData == null) {
            return;
        }
        Config.showTracker = configData.showTracker;
        int n = Math.max(1, Math.min(72000, (int)Math.round(configData.delayMin * 20.0)));
        int n2 = Math.max(n, Math.min(72000, (int)Math.round(configData.delayMax * 20.0)));
        Config.cycle1MinTicks = configData.cycle1MinTicks == null ? n : configData.cycle1MinTicks;
        Config.cycle1MaxTicks = configData.cycle1MaxTicks == null ? n2 : configData.cycle1MaxTicks;
        Config.cycle2MinTicks = configData.cycle2MinTicks == null ? n : configData.cycle2MinTicks;
        Config.cycle2MaxTicks = configData.cycle2MaxTicks == null ? n2 : configData.cycle2MaxTicks;
        Config.cycle3MinTicks = configData.cycle3MinTicks == null ? n : configData.cycle3MinTicks;
        Config.cycle3MaxTicks = configData.cycle3MaxTicks == null ? n2 : configData.cycle3MaxTicks;
        Config.cycle4MinTicks = configData.cycle4MinTicks == null ? n : configData.cycle4MinTicks;
        Config.cycle4MaxTicks = configData.cycle4MaxTicks == null ? n2 : configData.cycle4MaxTicks;
        Config.enableAutoPay = configData.enableAutoPay;
        Config.payIntervalMinutes = configData.payIntervalMinutes;
        Config.payRecipient = configData.payRecipient;
        Config.payAmount = configData.payAmount;
        Config.payAll = configData.payAll;
        Config.payRandomMinMillions = configData.payRandomMinMillions == null ? configData.payAmount : configData.payRandomMinMillions;
        Config.payRandomMaxMillions = configData.payRandomMaxMillions == null ? configData.payAmount : configData.payRandomMaxMillions;
        Config.payWebhookUrl = configData.payWebhookUrl;
        Config.delayMin = configData.delayMin;
        Config.delayMax = configData.delayMax;
        Config.trackerWidth = configData.trackerWidth;
        Config.trackerHeight = configData.trackerHeight;
        Config.textScale = configData.textScale;
        double d = configData.textScale * Math.max(1.0, Math.min(2.0, (double)configData.trackerWidth / 180.0));
        double d2 = configData.trackerScale == null ? d : configData.trackerScale;
        Config.trackerScale = Config.clampTrackerScale(d2);
        Config.antiPlayerEnabled = configData.antiPlayerEnabled;
        Config.enableGhostFix = configData.enableGhostFix;
        Config.ghostTimeoutMinutes = configData.ghostTimeoutMinutes;
        Config.ghostHomeCommand = configData.ghostHomeCommand;
        Config.enableHome = configData.enableHome;
        Config.homeCommandIntervalMin = configData.homeCommandIntervalMin;
        Config.homeCommand1 = configData.homeCommand1;
        Config.homeCommand2 = configData.homeCommand2;
        Config.webhookUrlProfit = configData.webhookUrlProfit;
        Config.webhookUrlAlert = configData.webhookUrlAlert;
        Config.profitIntervalMinutes = configData.profitIntervalMinutes;
        Config.noSellIntervalMinutes = configData.noSellIntervalMinutes;
        Config.discordId = configData.discordId;
        Config.friendList = configData.friendList != null ? configData.friendList : new ArrayList();
    }

    private static ConfigData gatherData() {
        ConfigData configData = new ConfigData();
        configData.showTracker = Config.showTracker;
        configData.cycle1MinTicks = Config.cycle1MinTicks;
        configData.cycle1MaxTicks = Config.cycle1MaxTicks;
        configData.cycle2MinTicks = Config.cycle2MinTicks;
        configData.cycle2MaxTicks = Config.cycle2MaxTicks;
        configData.cycle3MinTicks = Config.cycle3MinTicks;
        configData.cycle3MaxTicks = Config.cycle3MaxTicks;
        configData.cycle4MinTicks = Config.cycle4MinTicks;
        configData.cycle4MaxTicks = Config.cycle4MaxTicks;
        configData.enableAutoPay = Config.enableAutoPay;
        configData.payIntervalMinutes = Config.payIntervalMinutes;
        configData.payRecipient = Config.payRecipient;
        configData.payAmount = Config.payAmount;
        configData.payAll = Config.payAll;
        configData.payRandomMinMillions = Config.payRandomMinMillions;
        configData.payRandomMaxMillions = Config.payRandomMaxMillions;
        configData.payWebhookUrl = Config.payWebhookUrl;
        configData.delayMin = Config.delayMin;
        configData.delayMax = Config.delayMax;
        configData.trackerWidth = Config.trackerWidth;
        configData.trackerHeight = Config.trackerHeight;
        configData.textScale = Config.textScale;
        configData.trackerScale = Config.clampTrackerScale(Config.trackerScale);
        configData.antiPlayerEnabled = Config.antiPlayerEnabled;
        configData.enableGhostFix = Config.enableGhostFix;
        configData.ghostTimeoutMinutes = Config.ghostTimeoutMinutes;
        configData.ghostHomeCommand = Config.ghostHomeCommand;
        configData.enableHome = Config.enableHome;
        configData.homeCommandIntervalMin = Config.homeCommandIntervalMin;
        configData.homeCommand1 = Config.homeCommand1;
        configData.homeCommand2 = Config.homeCommand2;
        configData.webhookUrlProfit = Config.webhookUrlProfit;
        configData.webhookUrlAlert = Config.webhookUrlAlert;
        configData.profitIntervalMinutes = Config.profitIntervalMinutes;
        configData.noSellIntervalMinutes = Config.noSellIntervalMinutes;
        configData.discordId = Config.discordId;
        configData.friendList = new ArrayList<String>(Config.friendList);
        return configData;
    }

    @Environment(value=EnvType.CLIENT)
    static class ConfigData {
        Integer cycle1MinTicks;
        Integer cycle1MaxTicks;
        Integer cycle2MinTicks;
        Integer cycle2MaxTicks;
        Integer cycle3MinTicks;
        Integer cycle3MaxTicks;
        Integer cycle4MinTicks;
        Integer cycle4MaxTicks;
        boolean showTracker = true;
        Double trackerScale;
        boolean enableAutoPay = false;
        int payIntervalMinutes = 60;
        String payRecipient = "";
        double payAmount = 0.0;
        boolean payAll = false;
        Double payRandomMinMillions;
        Double payRandomMaxMillions;
        String payWebhookUrl = "";
        double delayMin = 0.1;
        double delayMax = 0.5;
        int trackerWidth = 180;
        int trackerHeight = 80;
        double textScale = 1.0;
        boolean antiPlayerEnabled = true;
        boolean enableGhostFix = true;
        int ghostTimeoutMinutes = 1;
        String ghostHomeCommand = "home 1";
        boolean enableHome = true;
        int homeCommandIntervalMin = 5;
        String homeCommand1 = "home 1";
        String homeCommand2 = "home 2";
        String webhookUrlProfit = "";
        String webhookUrlAlert = "";
        int profitIntervalMinutes = 30;
        int noSellIntervalMinutes = 15;
        String discordId = "";
        List friendList = new ArrayList();

        ConfigData() {
        }
    }
}


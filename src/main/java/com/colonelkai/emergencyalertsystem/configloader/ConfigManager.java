package com.colonelkai.emergencyalertsystem.configloader;

import com.colonelkai.emergencyalertsystem.EmergencyAlertSystem;
import com.colonelkai.emergencyalertsystem.eas_type.EASType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    YamlConfiguration config;
    Set<EASType> easTypeSet;

    public ConfigManager() {
        onEnable();
    }

    private void onEnable() {
        File configFile = new File(EmergencyAlertSystem.getPlugin().getDataFolder(), "config.yml");

        if(!configFile.exists()) { // if config does not exist, thou shall create
            configFile.getParentFile().mkdir();
            InputStream defaultConfigInputStream = EmergencyAlertSystem.getPlugin().getResource("config.yml");

            try {
                java.nio.file.Files.copy(
                        defaultConfigInputStream,
                        configFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                EmergencyAlertSystem.getPlugin().getLogger().warning("Failed to create default config.");
                throw new RuntimeException(e);
            }

            try {
                defaultConfigInputStream.close();
            } catch (IOException e) {
                EmergencyAlertSystem.getPlugin().getLogger().warning("Failed to close default config InputStream.");
                throw new RuntimeException(e);
            }

        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
        EmergencyAlertSystem.getPlugin().getLogger().info("Loaded Config.");
    }

    public Set<EASType> getAllEASTypesFromConfig() {
        Set<EASType> easTypeSet = new HashSet<>();

        Set<String> keys = this.config.getKeys(false);

        EmergencyAlertSystem.getPlugin().getLogger().info("DEBUG: " + keys.toString());

        keys.parallelStream().forEach(k -> {
            easTypeSet.add(new EASType(
                    k,
                    this.config.getString(k+".permission"),
                    this.config.getString(k+".sound"),
                    this.config.getString(k+".short-message"),
                    this.config.getStringList(k+".long-message"),
                    this.config.getInt(k+".volume"),
                    this.config.getInt(k+".pitch"),
                    this.config.getInt(k+".sound-length")
            ));
        });

        this.easTypeSet = easTypeSet;

        return easTypeSet;
    }

    public Set<EASType> getAllEASTypesFromCache() {
        return this.easTypeSet;
    }

}
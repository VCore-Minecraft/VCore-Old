/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.files;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.files.config.VCoreYAMLConfig;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 06.07.2021 16:08
 */
public class DebugConfig extends VCoreYAMLConfig {
    public DebugConfig(VCorePlugin<?, ?> plugin) {
        super(plugin, "debug.yml", "//settings");
    }

    @Override
    public void onInit() {

    }

    @Override
    public void setupConfig() {
        config.addDefault("isDebugMode",true);
        config.options().copyDefaults(true);
        save();
    }

    public void setDebugMode(boolean value){
        config.set("isDebugMode",value);
        save();
    }

    public boolean debugMode(){
        return config.getBoolean("isDebugMode");
    }
}

/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.plugin.language;

import de.verdox.vcore.plugin.VCorePlugin;
import de.verdox.vcore.plugin.files.config.VCoreYAMLConfig;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 27.06.2021 23:59
 */
public class LanguageConfig extends VCoreYAMLConfig {
    private Language language;

    public LanguageConfig(VCorePlugin<?, ?> plugin, String fileName, String pluginDirectory, Language language) {
        super(plugin, fileName, pluginDirectory);
        this.language = language;
    }

    @Override
    public void onInit() {

    }

    @Override
    public void setupConfig() {

    }
}

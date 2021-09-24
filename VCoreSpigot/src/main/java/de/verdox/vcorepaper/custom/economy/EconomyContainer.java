/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcorepaper.custom.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @version 1.0
 * @Author: Lukas Jonsson (Verdox)
 * @date 24.09.2021 21:27
 */
public class EconomyContainer {

    private Economy economy;

    public EconomyContainer() {
        init();
    }

    private void init() {
        RegisteredServiceProvider<Economy> rsp = org.bukkit.Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }

    public Economy getEconomy() {
        if (economy == null)
            init();
        return economy;
    }

    public String getMoneyFormat(double amount) {
        String formatted = "§6" + getEconomy().format(amount) + " §e";
        if (amount == 1)
            formatted += getEconomy().currencyNameSingular();
        else
            formatted += getEconomy().currencyNamePlural();
        return formatted;
    }
}

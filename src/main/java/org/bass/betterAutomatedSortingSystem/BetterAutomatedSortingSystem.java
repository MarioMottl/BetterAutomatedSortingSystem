package org.bass.betterAutomatedSortingSystem;

import org.bass.betterAutomatedSortingSystem.handler.BetterAutomatedSortingSystemHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

public final class BetterAutomatedSortingSystem extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("BetterAutomatedSortingSystem enabled.");
        new BetterAutomatedSortingSystemHandler(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("BetterAutomatedSortingSystem disabled.");
    }
}

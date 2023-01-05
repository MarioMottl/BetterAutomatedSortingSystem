package betterautomatedsortingsystem.betterautomatedsortingsystem;

import betterautomatedsortingsystem.betterautomatedsortingsystem.handler.BetterAutomatedSortingSystemHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public final class BetterAutomatedSortingSystem extends JavaPlugin implements Listener {

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

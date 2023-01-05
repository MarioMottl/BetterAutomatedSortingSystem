package betterautomatedsortingsystem.betterautomatedsortingsystem.handler;

import betterautomatedsortingsystem.betterautomatedsortingsystem.BetterAutomatedSortingSystem;
import org.bukkit.Bukkit;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.Arrays;

public class BetterAutomatedSortingSystemHandler implements Listener {

    public BetterAutomatedSortingSystemHandler(BetterAutomatedSortingSystem plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    String getItemName(String translationKey) {
        if(translationKey == null) return null;
        String[] names = translationKey.split("\\.");
        return names[names.length-1];
    }

    boolean filterMatch(String filterString, String fullItemName) {
        String itemName = getItemName(fullItemName);
        String[] filter = filterString.split(",");

        return Arrays.asList(filter).stream().anyMatch((filter_i) -> {
            if (filter_i.startsWith("*") && filter_i.endsWith("*")){
                //e.g. *cobble* substring has to start at [1] and end with [length()-1]
              return itemName.contains(filter_i.substring(1, filter_i.length()-1));
            }
            else if(filter_i.endsWith("*")) {
                //e.g. cobble* substring has to start at [0] and end with [length()-1]
                return itemName.startsWith(filter_i.substring(0, filter_i.length()-1));
            }
            else if (filter_i.startsWith("*")) {
                //e.g. *cobble substring has to start at [1] and end with [length()]
                return itemName.endsWith(filter_i.substring(1));
            }
            else {
                return filter_i.equalsIgnoreCase(itemName);
            }

        });
    }

    @EventHandler
    void onInventoryMoveItemEvent(InventoryMoveItemEvent event){
        if(event.getDestination().getType().equals(InventoryType.HOPPER) &&
                event.getDestination().getHolder() instanceof Container) {
            String customName = ((Container) event.getDestination().getHolder()).getCustomName();
            if(customName != null) {
                String itemName = event.getItem().getTranslationKey();
                if(!filterMatch(customName, itemName)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    void onInventoryPickupItemEvent(InventoryPickupItemEvent event) {
        if(event.getInventory().getHolder() instanceof Container) {
            String customName = ((Container) event.getInventory().getHolder()).getCustomName();
            if(customName != null) {
                String itemName = event.getItem().getItemStack().getTranslationKey();
                if(!filterMatch(customName, itemName)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}

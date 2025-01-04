package org.bass.betterAutomatedSortingSystem.handler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bass.betterAutomatedSortingSystem.BetterAutomatedSortingSystem;
import org.bukkit.Bukkit;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.Arrays;
import java.util.logging.Logger;

public class BetterAutomatedSortingSystemHandler implements Listener {
    private final Logger logger;
    private final PlainTextComponentSerializer textSerializer = PlainTextComponentSerializer.plainText();

    public BetterAutomatedSortingSystemHandler(BetterAutomatedSortingSystem plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.logger = plugin.getLogger();
    }

    String getItemName(String translationKey) {
        if (translationKey == null) {
            logger.warning("getItemName received null translationKey");
            return null;
        }
        String[] names = translationKey.split("\\.");
        String result = names[names.length - 1];
        logger.fine(String.format("getItemName: translationKey='%s' -> extracted name='%s'", translationKey, result));
        return result;
    }

    String extractCustomName(Component customName) {
        if (customName == null) {
            logger.fine("extractCustomName: received null component");
            return null;
        }

        // Use the proper serializer to get just the text content
        String content = textSerializer.serialize(customName);
        logger.fine(String.format("extractCustomName: component text='%s'", content));
        return content;
    }

    boolean filterMatch(String filterString, String fullItemName) {
        String itemName = getItemName(fullItemName);
        if (itemName == null || filterString == null) {
            logger.warning(String.format("filterMatch: null values - itemName=%s, filterString=%s", itemName, filterString));
            return false;
        }

        // Split the filter string and clean each filter
        String[] filters = Arrays.stream(filterString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        logger.fine(String.format("filterMatch: itemName='%s', filters=%s", itemName, Arrays.toString(filters)));

        // For any filter that matches, we want to let the item through (return false)
        // So we return true (block the item) if NO filter matches
        return Arrays.stream(filters).noneMatch(filter -> {
            boolean matches;

            if (filter.startsWith("*") && filter.endsWith("*")) {
                String substring = filter.substring(1, filter.length() - 1);
                matches = itemName.toLowerCase().contains(substring.toLowerCase());
                logger.fine(String.format("Filter *X*: filter='%s', substring='%s', itemName='%s', matches=%b",
                        filter, substring, itemName, matches));
            } else if (filter.endsWith("*")) {
                String substring = filter.substring(0, filter.length() - 1);
                matches = itemName.toLowerCase().startsWith(substring.toLowerCase());
                logger.fine(String.format("Filter X*: filter='%s', substring='%s', itemName='%s', matches=%b",
                        filter, substring, itemName, matches));
            } else if (filter.startsWith("*")) {
                String substring = filter.substring(1);
                matches = itemName.toLowerCase().endsWith(substring.toLowerCase());
                logger.fine(String.format("Filter *X: filter='%s', substring='%s', itemName='%s', matches=%b",
                        filter, substring, itemName, matches));
            } else {
                matches = filter.equalsIgnoreCase(itemName);
                logger.fine(String.format("Filter exact: filter='%s', itemName='%s', matches=%b",
                        filter, itemName, matches));
            }
            return matches;
        });
    }

    @EventHandler
    void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
        logger.fine("Processing InventoryMoveItemEvent...");
        if (!event.getDestination().getType().equals(InventoryType.HOPPER)) {
            logger.fine("Move Event - Destination is not a hopper");
            return;
        }

        if (!(event.getDestination().getHolder() instanceof Container container)) {
            logger.fine("Move Event - Destination holder is not a Container");
            return;
        }

        Component nameComponent = container.customName();
        String customName = extractCustomName(nameComponent);
        String itemName = event.getItem().getType().translationKey();

        logger.fine(String.format("Move Event - Container: customName='%s', itemName='%s'",
                customName, itemName));

        if (customName != null && !customName.isEmpty()) {
            boolean shouldCancel = filterMatch(customName, itemName);
            logger.fine(String.format("Move Event - Filter result: shouldCancel=%b", shouldCancel));
            event.setCancelled(shouldCancel);
        } else {
            logger.fine("Move Event - No valid custom name found on container");
        }
    }

    @EventHandler
    void onInventoryPickupItemEvent(InventoryPickupItemEvent event) {
        logger.fine("Processing InventoryPickupItemEvent...");
        if (!(event.getInventory().getHolder() instanceof Container container)) {
            logger.fine("Pickup Event - Inventory holder is not a Container");
            return;
        }

        Component nameComponent = container.customName();
        String customName = extractCustomName(nameComponent);
        String itemName = event.getItem().getItemStack().getType().translationKey();

        logger.fine(String.format("Pickup Event - Container: customName='%s', itemName='%s'",
                customName, itemName));

        if (customName != null && !customName.isEmpty()) {
            boolean shouldCancel = filterMatch(customName, itemName);
            logger.fine(String.format("Pickup Event - Filter result: shouldCancel=%b", shouldCancel));
            event.setCancelled(shouldCancel);
        } else {
            logger.fine("Pickup Event - No valid custom name found on container");
        }
    }
}

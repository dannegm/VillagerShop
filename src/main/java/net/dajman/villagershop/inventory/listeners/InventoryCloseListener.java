package net.dajman.villagershop.inventory.listeners;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    private final Main plugin;

    public InventoryCloseListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent e){
        final Category category = this.plugin.getCategories().get(e.getInventory());
        if (category == null){
            return;
        }
        this.plugin.getCategoryData().save(category);
    }
}

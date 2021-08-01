package net.dajman.villagershop.inventory.listeners;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
        final Inventory inventory = category.getConfigInventory();
        if (inventory.getViewers().size() == 1){
            for(int j = 0; j < 28; j += 27){
                for(int i = j; i < 9; i++){
                    final ItemStack item1 = inventory.getItem(i);
                    final ItemStack item2 = inventory.getItem(i + 9);
                    final ItemStack item3 = inventory.getItem(i + 18);
                    if ((!this.isAirOrNull(item1) || !this.isAirOrNull(item2) || !this.isAirOrNull(item3)) && (this.isAirOrNull(item1) || this.isAirOrNull(item3))){
                        final ItemStack air = new ItemStack(Material.AIR);
                        inventory.setItem(i, air);
                        inventory.setItem(i + 9, air);
                        inventory.setItem(i + 18, air);
                    }
                }
            }
        }
        this.plugin.getCategoryData().save(category);
    }

    private boolean isAirOrNull(final ItemStack itemStack){
        return itemStack == null || itemStack.getType() == Material.AIR;
    }
}

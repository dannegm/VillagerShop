package net.dajman.villagershop.inventory.inventories;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MainInventory {

    private final Main plugin;

    public MainInventory(Main plugin) {
        this.plugin = plugin;
    }

    public void open(final Player player){
        player.openInventory(this.build());
    }

    public Inventory build(){
        final Inventory inv = Bukkit.createInventory(null, this.plugin.getConfiguration().guiRows *  9, this.plugin.getConfiguration().guiName);
        for (Category category : this.plugin.getCategories()) {
            inv.setItem(category.getSlot(), category.getItem().build());
        }
        final ItemStack itemStack = this.plugin.getConfiguration().fillItem.build();
        if (itemStack != null && itemStack.getType() != Material.AIR){
            for(int i = 0; i < inv.getSize(); i++){
                final ItemStack is = inv.getItem(i);
                if (is != null && is.getType() != Material.AIR){
                    continue;
                }
                inv.setItem(i, itemStack);
            }
        }
        return inv;
    }

}

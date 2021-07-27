package net.dajman.villagershop.inventory.listeners;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    private final Main plugin;

    public InventoryClickListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(final InventoryClickEvent e){
        if (!(e.getWhoClicked() instanceof Player)){
            return;
        }
        final Player player = (Player) e.getWhoClicked();
        if (!e.getView().getTitle().equals(this.plugin.getConfiguration().guiName)){
            return;
        }
        e.setCancelled(true);
        final Category category = this.plugin.getCategories().get(e.getRawSlot());
        if (category == null){
            return;
        }
        this.plugin.getTradeInventoryBuilder().open(player, category);
    }
}

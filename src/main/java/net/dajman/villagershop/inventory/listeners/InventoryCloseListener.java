package net.dajman.villagershop.inventory.listeners;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.data.category.Category;
import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.inventory.listeners.actionservice.config.ConfigInventoryActionService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Optional;

public class InventoryCloseListener implements Listener {

    private static final Logger LOGGER = Logger.getLogger(InventoryCloseListener.class);

    private final Main plugin;
    private final ConfigInventoryActionService configInventoryActionService;

    public InventoryCloseListener(Main plugin, ConfigInventoryActionService configInventoryActionService) {
        this.plugin = plugin;
        this.configInventoryActionService = configInventoryActionService;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHighPriorityClose(final InventoryCloseEvent e){

        LOGGER.debug("onHighPriorityClose() Received close inventory for player={}", e.getPlayer().getName());

        if (!(e.getPlayer() instanceof Player)){

            LOGGER.debug("onHighPriorityClose() Received HumanEntity(getPlayer())={} is not a Player",
                    e.getPlayer().getName());
            return;
        }

        this.configInventoryActionService.onHighPriorityCloseInventory((Player) e.getPlayer());
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent e){

        LOGGER.debug("onClose() Received close inventory for player={}", e.getPlayer().getName());

        if (!(e.getPlayer() instanceof Player)){

            LOGGER.debug("onClose() Received HumanEntity(getPlayer())={} is not a Player",
                    e.getPlayer().getName());
            return;
        }

        final Player player = (Player) e.getPlayer();

        final Optional<Category> optionalCategory = this.plugin.getCategories().getByConfigInventory(e.getInventory());

        if (!optionalCategory.isPresent()){

            LOGGER.debug("onClose() Category for closed inventory not found. player={}",  player.getName());
            return;
        }

        final Category category = optionalCategory.get();

        LOGGER.debug("onClose() Closing config inventory for category={} and player={}", category.getName(),  player.getName());

        this.configInventoryActionService.onCloseConfig(e.getInventory(), player, category);
    }

}

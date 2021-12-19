package net.dajman.villagershop.inventory.listeners;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import net.dajman.villagershop.util.Messages;
import net.dajman.villagershop.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class InventoryClickListener implements Listener {

    private final Logger LOGGER = Logger.getLogger(InventoryClickListener.class);

    private final Main plugin;

    public InventoryClickListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(final InventoryClickEvent e){

        LOGGER.debug("onClick() Received inventory click from whoClicked={}", e.getWhoClicked().getName());

        if (!(e.getWhoClicked() instanceof Player)){

            LOGGER.debug("onClick() whoClicked={} is not a player", e.getWhoClicked().getName());
            return;
        }

        final Player player = (Player) e.getWhoClicked();



        if (!e.getView().getTitle().equals(this.plugin.getConfiguration().guiName)){

            LOGGER.debug("onClick() title={} is not equals to configGuiName={}", e.getView().getTitle(),
                    this.plugin.getConfiguration().guiName);
            return;
        }

        e.setCancelled(true);

        LOGGER.debug("onClick() event is cancelled, looking for category by slot");

        final Optional<Category> optionalCategory = this.plugin.getCategories().getBySlot(e.getRawSlot());

        if (!optionalCategory.isPresent()){

            LOGGER.debug("onClick() category for slot={} not found", Integer.toString(e.getRawSlot()));
            return;
        }

        final Category category = optionalCategory.get();

        LOGGER.debug("onClick() Category={} found for slot={}", category.getName(), Integer.toString(e.getRawSlot()));

        if (!player.hasPermission(category.getPermission())){

            LOGGER.debug("onClick() Player={} do not have permission={} for open category={}",
                    player.getName(), category.getPermission(), category.getName());

            Messages.sendMessageIfNotEmpty(player, this.plugin.getConfiguration().categoryPermissionMessage,
                    "{CATEGORY}", category.getName());

            return;
        }

        LOGGER.debug("onClick() Opening category={} for player={}", category.getName(), player.getName());
        this.plugin.getTradeInventoryBuilder().open(player, category);
    }
}

package net.dajman.villagershop.inventory.listeners;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.data.category.Category;
import net.dajman.villagershop.common.Pair;
import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.inventory.listeners.actionservice.config.ConfigInventoryActionService;
import net.dajman.villagershop.inventory.listeners.actionservice.shop.ShopInventoryActionService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class InventoryClickListener implements Listener {

    private final Logger LOGGER = Logger.getLogger(InventoryClickListener.class);

    private final Main plugin;
    private final ShopInventoryActionService shopInventoryActionService;
    private final ConfigInventoryActionService configInventoryActionService;

    public InventoryClickListener(Main plugin, ShopInventoryActionService shopInventoryActionService,
                                  ConfigInventoryActionService configInventoryActionService) {
        this.plugin = plugin;
        this.shopInventoryActionService = shopInventoryActionService;
        this.configInventoryActionService = configInventoryActionService;
    }

    @EventHandler
    public void onClick(final InventoryClickEvent e){

        LOGGER.debug("onClick() Received inventory click from whoClicked={}", e.getWhoClicked().getName());

        if (!(e.getWhoClicked() instanceof Player)){

            LOGGER.debug("onClick() whoClicked={} is not a player", e.getWhoClicked().getName());
            return;
        }

        final Player player = (Player) e.getWhoClicked();

        if (e.getView().getTitle().equals(this.plugin.getConfiguration().guiName)){

            e.setCancelled(true);

            LOGGER.debug("onClick() title={} is equal to configGuiName={}, event is cancelled, " +
                    "looking for category by slot", e.getView().getTitle(), this.plugin.getConfiguration().guiName);


            this.shopInventoryActionService.onClickShop(player, e.getRawSlot());

            return;
        }

        final Optional<Pair<Category, Integer>> optionalCategoryWithCurrentPage = this.plugin.getCategories()
                .getCategoryAndPageByConfigInventory(e.getInventory());

        if (!optionalCategoryWithCurrentPage.isPresent()){
            LOGGER.debug("onClick() Category for open config inventory not found.");
            return;
        }

        final Pair<Category, Integer> categoryWithCurrentPage = optionalCategoryWithCurrentPage.get();

        final Category category = categoryWithCurrentPage.getKey();
        final Integer currentPage = categoryWithCurrentPage.getValue();

        final boolean cancel = this.configInventoryActionService.onClickConfig(player, category, currentPage, e.getRawSlot());

        e.setCancelled(cancel);
    }
}

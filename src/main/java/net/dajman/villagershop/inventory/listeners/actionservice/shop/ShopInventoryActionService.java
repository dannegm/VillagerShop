package net.dajman.villagershop.inventory.listeners.actionservice.shop;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.data.category.Category;
import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.util.Messages;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ShopInventoryActionService {

    private static final Logger LOGGER = Logger.getLogger(ShopInventoryActionService.class);

    private final Main plugin;

    public ShopInventoryActionService(Main plugin) {
        this.plugin = plugin;
    }

    public void onClickShop(final Player player, final Integer rawSlot){

        final Optional<Category> optionalCategory = this.plugin.getCategories().getBySlot(rawSlot);

        if (!optionalCategory.isPresent()){

            LOGGER.debug("onClickShop() category for slot={} not found", rawSlot);
            return;
        }

        final Category category = optionalCategory.get();

        LOGGER.debug("onClickShop() Category={} found for slot={}", category.getName(), rawSlot);

        if (!player.hasPermission(category.getPermission())){

            LOGGER.debug("onClickShop() Player={} do not have permission={} for open category={}",
                    player.getName(), category.getPermission(), category.getName());

            Messages.sendMessageIfNotEmpty(player, this.plugin.getConfiguration().categoryPermissionMessage,
                    "{CATEGORY}", category.getName());

            return;
        }

        LOGGER.debug("onClickShop() Opening category={} for player={}", category.getName(), player.getName());
        this.plugin.getTradeInventoryService().open(player, category);
    }

}

package net.dajman.villagershop.inventory.service.shop;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.data.configuration.Config;
import net.dajman.villagershop.inventory.builder.inventory.ShopInventoryBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static java.util.Objects.isNull;

public class ShopInventoryService {

    private static final Logger LOGGER = Logger.getLogger(ShopInventoryService.class);
    
    private final Main plugin;

    public ShopInventoryService(Main plugin) {
        this.plugin = plugin;
    }

    public void openShop(final Player player){
        final Config config = this.plugin.getConfiguration();

        final Inventory shopInventory = new ShopInventoryBuilder(config.guiName, config.guiRows,
                this.plugin.getCategories(), config.fillItem)
                .build();

        if (isNull(shopInventory)){
            return;
        }

        player.openInventory(shopInventory);
    }

}

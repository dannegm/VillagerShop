package net.dajman.villagershop.inventory.service.config;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.data.category.Category;
import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.inventory.builder.inventory.ConfigInventoryBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

import static java.util.Objects.isNull;


public class ConfigInventoryService {

    private static final Logger LOGGER = Logger.getLogger(ConfigInventoryService.class);

    private final Main plugin;

    public ConfigInventoryService(Main plugin) {
        this.plugin = plugin;
    }

    public void openConfigInventory(final Player player, final Category category){
        this.openConfigInventory(player, category, 0);
    }

    public void openConfigInventory(final Player player, final Category category, final Integer page){

        LOGGER.debug("openConfigInventory() Trying to open config inventory for player={}, category={} and page={}",
                player.getName(), category, page);

        final Optional<Inventory> optionalConfigInventory = category.getConfigInventory(page);

        final Inventory configInventory;

        if (optionalConfigInventory.isPresent()){
            configInventory = optionalConfigInventory.get();

        } else {

            LOGGER.debug("openConfigInventory() Config inventory witch given page={} not found for category={} " +
                    "trying to create new inventory", page, category);

            configInventory = new ConfigInventoryBuilder(page).build();

            if (isNull(configInventory)){
                LOGGER.error("openConfigInventory() Created new config inventory for category={} is null, " +
                        "skipping the opening of inventory", category.getName());
                return;
            }

            category.addConfigInventory(configInventory);

            LOGGER.debug("openConfigInventory() Created new config inventory with page={} for category={}",
                    page, category);

        }

        LOGGER.debug("openConfigInventory() Opening config inventory for player={}, category={} and page={}",
                player.getName(), category, page);

        player.openInventory(configInventory);
    }

}

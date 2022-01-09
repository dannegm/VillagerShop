package net.dajman.villagershop.inventory.listeners.actionservice.config;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.data.category.Category;
import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.inventory.listeners.actionservice.config.cache.ConfigInventoryChangingPageCache;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

public class ConfigInventoryActionService {

    private static final Logger LOGGER = Logger.getLogger(ConfigInventoryActionService.class);

    private final Main plugin;

    private final ConfigInventoryChangingPageCache configInventoryChangingPageCache;

    public ConfigInventoryActionService(Main plugin) {
        this.plugin = plugin;
        this.configInventoryChangingPageCache = new ConfigInventoryChangingPageCache();
    }

    public void onHighPriorityCloseInventory(final Player player){
        this.configInventoryChangingPageCache.invalidate(player);
    }

    // return value tells if the event should be cancelled
    public boolean onClickConfig(final Player player, final Category category, final Integer currentPage, final Integer rawSlot) {

        LOGGER.debug("onClickConfig() Config inventory click received for player={}, category={}, currentPage={} and rawSlot={}",
                player.getName(), category, currentPage, rawSlot);

        if (rawSlot > 27 && rawSlot < 35){
            LOGGER.debug("onClickConfig() rawSlot={} is greater than 27 and less than 35, event is cancelled");
            return true;
        }

        if (Objects.equals(27, rawSlot)){

            LOGGER.debug("onClickConfig() Trying to click previous page item.");

            if (Objects.equals(0, currentPage)){
                LOGGER.debug("onClickConfig() currentPage is equal 0, previous inventory cannot be opened.");
                return true;
            }

            final Integer previousPage = currentPage - 1;

            LOGGER.debug("onClickConfig() Opening previous page={} for player={} and category={}",
                    previousPage, player.getName(), category);

            this.configInventoryChangingPageCache.cachePlayer(player);
            this.plugin.getConfigInventoryService().openConfigInventory(player, category, previousPage);
            return true;
        }

        if (Objects.equals(35, rawSlot)){

            final Integer nextPage = currentPage + 1;

            LOGGER.debug("onClickConfig() Opening next page={} for player={} and category={}",
                    nextPage, player.getName(), category);

            this.configInventoryChangingPageCache.cachePlayer(player);
            this.plugin.getConfigInventoryService().openConfigInventory(player, category, nextPage);
            return true;
        }

        return false;
    }

    public void onCloseConfig(final Inventory inventory, final Player player, final Category category){

        LOGGER.debug("onCloseConfig() Config inventory close received for player={} and category={}",
                player.getName(), category);

        final int inventoryViewers = inventory.getViewers().size();

        if (inventoryViewers > 1){

            LOGGER.debug("onCloseConfig() Number of viewers={} of this inventory is greater than 1, " +
                    "no action is executed", inventoryViewers);
            return;
        }

        for(int i = 0; i < 9; i++){

            final ItemStack recipe1 = inventory.getItem(i),
                    recipe2 = inventory.getItem(i + 9),
                    result = inventory.getItem(i + 18);

            if ((this.isNullOrAir(recipe1) && (!this.isNullOrAir(recipe2) || !this.isNullOrAir(result)))
                || (this.isNullOrAir(result) && (!this.isNullOrAir(recipe1) || !this.isNullOrAir(recipe2)))){

                LOGGER.debug("onCloseConfig() Incorrect setting of the offer in category={}, recipe1={}, recipe2={}, result={}",
                        category, recipe1, recipe2, result);

                inventory.setItem(i, new ItemStack(Material.AIR));
                inventory.setItem(i + 9, new ItemStack(Material.AIR));
                inventory.setItem(i + 18, new ItemStack(Material.AIR));
            }

        }

        if (this.configInventoryChangingPageCache.isCached(player)){

            LOGGER.debug("onCloseConfig() Player={} is changing inventory, config not saving",
                    player.getName());

            this.configInventoryChangingPageCache.invalidate(player);
            return;
        }

        final Integer allConfigInventoryViewers = category.getAllConfigInventoryViewers();

        if (allConfigInventoryViewers > 1){

            LOGGER.debug("onCloseConfig() Number of viewers={} for all config inventories of category={} " +
                    "is greater than 1, config is not saving.", allConfigInventoryViewers, category);
            return;
        }

        // delete empty ending inventories
        final List<Inventory> configInventories = category.getConfigInventories();

        for(int i = configInventories.size() - 1; i > 0; i--){

            final Inventory configInventory = configInventories.get(i);

            if (this.isEmpty(configInventory)){

                LOGGER.debug("onCloseConfig() Page={} is empty for category={} and will be removed",
                        i, category);

                category.removeConfigInventory(configInventory);
            }
        }

        this.plugin.getCategoryDataService().save(category);
    }

    private boolean isNullOrAir(final ItemStack itemStack){
        return isNull(itemStack) || Objects.equals(itemStack.getType(), Material.AIR);
    }

    // checking if config inventory is empty, except last row (next / previous page items)
    private boolean isEmpty(final Inventory configInventory){

        for(int i = 0; i < 27; i++){
            if (!isNullOrAir(configInventory.getItem(i))){
                return false;
            }
        }

        return true;
    }
}

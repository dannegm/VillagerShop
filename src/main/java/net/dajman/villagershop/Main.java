package net.dajman.villagershop;

import net.dajman.villagershop.data.category.Category;
import net.dajman.villagershop.command.manager.CommandManager;
import net.dajman.villagershop.data.configuration.Config;
import net.dajman.villagershop.data.category.CategoryList;
import net.dajman.villagershop.data.service.CategoryDataService;
import net.dajman.villagershop.inventory.listeners.actionservice.config.ConfigInventoryActionService;
import net.dajman.villagershop.inventory.listeners.actionservice.shop.ShopInventoryActionService;
import net.dajman.villagershop.inventory.service.config.ConfigInventoryService;
import net.dajman.villagershop.inventory.service.shop.ShopInventoryService;
import net.dajman.villagershop.inventory.service.trade.TradeInventoryService;
import net.dajman.villagershop.inventory.listeners.InventoryClickListener;
import net.dajman.villagershop.inventory.listeners.InventoryCloseListener;
import net.dajman.villagershop.common.logging.Logger;
import net.dajman.villagershop.data.serialization.itemstack.ItemStackSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static java.util.Objects.nonNull;

public class Main extends JavaPlugin{

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static final String PERMISSION_PREFIX = "villagershop.";

    private Config configuration;
    private CategoryList categories;
    private CategoryDataService categoryDataService;
    private ShopInventoryService shopInventoryService;
    private TradeInventoryService tradeInventoryService;
    private ConfigInventoryService configInventoryService;
    private CommandManager commandManager;

    private ItemStackSerializer itemStackSerializer;


    public Config getConfiguration() {
        return this.configuration;
    }

    public CategoryList getCategories() {
        return this.categories;
    }

    public CategoryDataService getCategoryDataService() {
        return categoryDataService;
    }

    public ShopInventoryService getShopInventoryService() {
        return shopInventoryService;
    }

    public TradeInventoryService getTradeInventoryService() {
        return this.tradeInventoryService;
    }

    public ConfigInventoryService getConfigInventoryService() {
        return configInventoryService;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ItemStackSerializer getItemStackSerializer() {
        return itemStackSerializer;
    }

    @Override
    public void onEnable() {
        Logger.init(this);

        LOGGER.info("Loading components...");

        this.itemStackSerializer = new ItemStackSerializer();
        this.categories = new CategoryList();
        this.commandManager = new CommandManager();
        (this.configuration = new Config(this)).load();
        (this.categoryDataService = new CategoryDataService(this)).load();

        this.shopInventoryService = new ShopInventoryService(this);
        this.tradeInventoryService =  new TradeInventoryService();
        this.configInventoryService = new ConfigInventoryService(this);

        final ConfigInventoryActionService configInventoryActionService = new ConfigInventoryActionService(this);
        final ShopInventoryActionService shopInventoryActionService = new ShopInventoryActionService(this);

        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(
                this, configInventoryActionService), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(
                this, shopInventoryActionService, configInventoryActionService), this);


        LOGGER.info("Plugin enabled.");
    }

    @Override
    public void onDisable() {

        LOGGER.info("disabling...");

        if (nonNull(this.categories)){

            for (Category category : this.categories) {
                this.categoryDataService.save(category);
            }
        }

        LOGGER.info("Plugin disabled.");

    }


}

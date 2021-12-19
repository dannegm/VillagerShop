package net.dajman.villagershop;

import net.dajman.villagershop.category.Category;
import net.dajman.villagershop.command.manager.CommandManager;
import net.dajman.villagershop.configuration.Config;
import net.dajman.villagershop.category.list.CategoryList;
import net.dajman.villagershop.category.data.CategoryData;
import net.dajman.villagershop.inventory.inventories.TradeInventory;
import net.dajman.villagershop.inventory.inventories.MainInventory;
import net.dajman.villagershop.inventory.listeners.InventoryClickListener;
import net.dajman.villagershop.inventory.listeners.InventoryCloseListener;
import net.dajman.villagershop.util.logging.Logger;
import net.dajman.villagershop.util.serializer.itemstack.ItemStackSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static java.util.Objects.nonNull;

public class Main extends JavaPlugin{

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static final String PERMISSION_PREFIX = "villagershop.";

    private Config configuration;
    private CategoryData categoryData;
    private CategoryList categories;
    private MainInventory mainInventory;
    private TradeInventory tradeInventory;
    private CommandManager commandManager;

    private ItemStackSerializer itemStackSerializer;


    public Config getConfiguration() {
        return this.configuration;
    }

    public CategoryList getCategories() {
        return this.categories;
    }

    public MainInventory getMainInventoryBuilder() {
        return this.mainInventory;
    }

    public TradeInventory getTradeInventoryBuilder() {
        return this.tradeInventory;
    }

    public CategoryData getCategoryData() {
        return this.categoryData;
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
        (this.categoryData = new CategoryData(this)).load();

        this.mainInventory = new MainInventory(this);
        this.tradeInventory =  new TradeInventory();

        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);


        LOGGER.info("Plugin enabled.");
    }

    @Override
    public void onDisable() {

        LOGGER.info("disabling...");

        if (nonNull(this.getCategories())){

            for (Category category : this.getCategories()) {
                this.categoryData.save(category);
            }
        }

        LOGGER.info("Plugin disabled.");

    }


}

package net.dajman.villagershop;

import net.dajman.villagershop.command.shop.ShopCommand;
import net.dajman.villagershop.configuration.Config;
import net.dajman.villagershop.category.list.CategoryList;
import net.dajman.villagershop.category.data.CategoryData;
import net.dajman.villagershop.inventory.inventories.TradeInventory;
import net.dajman.villagershop.inventory.inventories.MainInventory;
import net.dajman.villagershop.inventory.listeners.InventoryClickListener;
import net.dajman.villagershop.inventory.listeners.InventoryCloseListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private Config configuration;
    private CategoryData categoryData;
    private CategoryList categories;
    private MainInventory mainInventory;
    private TradeInventory tradeInventory;


    public Config getConfiguration() {
        return configuration;
    }

    public CategoryList getCategories() {
        return categories;
    }

    public MainInventory getMainInventoryBuilder() {
        return mainInventory;
    }

    public TradeInventory getTradeInventoryBuilder() {
        return tradeInventory;
    }

    public CategoryData getCategoryData() {
        return categoryData;
    }

    @Override
    public void onEnable() {
        this.mainInventory = new MainInventory(this);
        this.tradeInventory =  new TradeInventory();
        this.categories = new CategoryList();
        this.saveDefaultConfig();
        (this.configuration = new Config(this)).load();
        (this.categoryData = new CategoryData(this)).load();

        new ShopCommand(this).register();
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }


}

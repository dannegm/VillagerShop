package net.dajman.villagershop.category.data;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import net.dajman.villagershop.util.ItemSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class CategoryData {

    private final Main plugin;

    public CategoryData(Main plugin) {
        this.plugin = plugin;
    }

    public void load(){
        final File dataFolder = this.plugin.getDataFolder();
        if (!dataFolder.exists()){
            return;
        }
        final File file = new File(this.plugin.getDataFolder(), "shop.dat");
        if (!file.exists()){
            this.plugin.saveResource("shop.dat", false);
        }
        final YamlConfiguration c = YamlConfiguration.loadConfiguration(file);
        for (Category category : this.plugin.getCategories()) {
            final String data = c.getString(category.getPath());
            if (data == null){
                return;
            }
            final ItemStack[] contents = ItemSerializer.stringToItems(data);
            category.getConfigInventory().setContents(contents);
        }
    }


    public void save(final Category category){
        final File dataFolder = this.plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdir()){
            System.out.println("[VillagerShop] Error while creating plugin data folder." );
            return;
        }
        final File file = new File(this.plugin.getDataFolder(), "shop.dat");
        try{
            if (!file.exists() && !file.createNewFile()){
                System.out.println("[VillagerShop] Error while creating data file.");
                return;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        final YamlConfiguration c = YamlConfiguration.loadConfiguration(file);
        final String data = ItemSerializer.itemsToString(category.getConfigInventory().getContents());
        c.set(category.getPath(), data);
        try{
            c.save(file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}

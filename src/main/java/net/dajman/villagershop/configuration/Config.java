package net.dajman.villagershop.configuration;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import net.dajman.villagershop.inventory.itemstack.ItemBuilder;
import net.dajman.villagershop.category.list.CategoryList;
import net.dajman.villagershop.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class Config {

    private final Main plugin;

    public String guiName;
    public int guiRows;
    public ItemBuilder fillItem;

    public Config(Main plugin) {
        this.plugin = plugin;
    }

    public void load(){
        final FileConfiguration c = this.plugin.getConfig();
        this.guiName = ColorUtil.fixColors(Optional.ofNullable(c.getString("gui-properties.name")).orElse("&2&lSklep"));
        this.guiRows = Optional.of(c.getInt("gui-properties.rows")).filter(integer -> integer > 0).orElse(1);

        this.fillItem = new ItemBuilder(Optional.ofNullable(c.getString("fill-item.material")).orElse("AIR"));
        this.fillItem.setName(ColorUtil.fixColors(Optional.ofNullable(c.getString("fill-item.name")).orElse("")));
        this.fillItem.setLore(ColorUtil.fixColors(c.getStringList("fill-item.lore")));

        final CategoryList categories = this.plugin.getCategories();
        final ConfigurationSection categoriesSection = c.getConfigurationSection("categories");
        if (categoriesSection == null){
            return;
        }
        for (String key : categoriesSection.getKeys(false)) {
            final ConfigurationSection categorySection = categoriesSection.getConfigurationSection(key);
            if (categorySection == null){
                continue;
            }
            final String name = ColorUtil.fixColors(Optional.ofNullable(categorySection.getString("name")).orElse("name"));
            final ConfigurationSection itemSection = categorySection.getConfigurationSection("item");
            if (itemSection == null){
                continue;
            }
            final int itemSlot = Optional.of(itemSection.getInt("slot")).filter(integer -> integer > -1 && integer < this.guiRows * 54 - 1).orElse(0);
            final String material = Optional.ofNullable(itemSection.getString("material")).orElse("STONE");
            final String itemName = ColorUtil.fixColors(Optional.ofNullable(itemSection.getString("name")).orElse(key));
            final List<String> itemLore = ColorUtil.fixColors(itemSection.getStringList("lore"));
            final ItemBuilder itemBuilder = new ItemBuilder(material).setName(itemName).setLore(itemLore);
            final ItemStack itemStack = itemBuilder.build();
            if (itemStack == null || itemStack.getType() == Material.AIR){
                continue;
            }
            categories.add(new Category(key, name, itemSlot, itemBuilder));
        }
    }

}

package net.dajman.villagershop.configuration;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import net.dajman.villagershop.command.Command;
import net.dajman.villagershop.command.commands.shop.ShopCommand;
import net.dajman.villagershop.command.commands.shop.subcommand.EditCommand;
import net.dajman.villagershop.command.commands.shop.subcommand.OpenCommand;
import net.dajman.villagershop.command.commands.shop.subcommand.ReloadCommand;
import net.dajman.villagershop.inventory.itemstack.ItemBuilder;
import net.dajman.villagershop.category.list.CategoryList;
import net.dajman.villagershop.util.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;

public class Config {

    private static final String CONFIG_FILE_EXTENSION = ".yml";
    private static final String CONFIG_FILE_NAME = "config" + CONFIG_FILE_EXTENSION;
    private static final String OLD_CONFIG_FILE_NAME = "config_old" + CONFIG_FILE_EXTENSION;
    private static final double CONFIG_VERSION = 1.1D;

    private final Main plugin;
    private final File configFile;
    private YamlConfiguration yamlConfiguration;

    public boolean debug;

    public String guiName;
    public int guiRows;
    public ItemBuilder fillItem;

    public String commandUsageMessage;
    public String commandPermissionMessage;
    public String categoryPermissionMessage;
    public String categoryOthersPermissionMessage;
    public String shopOpenOthersPermissionMessage;
    public String categoryNotFound;
    public String playerNotFound;

    public Config(Main plugin) {
        this.plugin = plugin;
        this.configFile = new File(this.plugin.getDataFolder(), CONFIG_FILE_NAME);
    }

    public void load(){
        this.plugin.getLogger().info("Loading configuration...");

        if (this.configFile.exists()){
            this.loadConfigurationFile();

            final double configVersion = yamlConfiguration.getDouble("config-version");

            if (!Objects.equals(CONFIG_VERSION, configVersion)){
                if (!this.configFile.renameTo(new File(this.plugin.getDataFolder(), OLD_CONFIG_FILE_NAME))
                        && !this.configFile.delete()){
                    this.plugin.getLogger().warning(
                            "Cannot rename and delete the configuration file to create a new one," +
                                    " try manually delete the file and restart the server.");

                    Bukkit.getPluginManager().disablePlugin(this.plugin);
                    return;
                }
                this.createConfigurationFile();
            }

        }else{
            this.createConfigurationFile();
        }

        this.loadDefaults();
        this.loadCommands();
        this.loadCategories();

        this.plugin.getLogger().info("The configuration has been loaded");
    }

    private void loadDefaults(){
        this.debug = yamlConfiguration.getBoolean("debug", false);

        this.guiName = Colors.fixColors(Optional.ofNullable(
                yamlConfiguration.getString("gui-properties.name")).orElse("&2&lShop"));
        this.guiRows = Optional.of(yamlConfiguration.getInt("gui-properties.rows"))
                .filter(integer -> integer > 0).orElse(1);

        this.fillItem = new ItemBuilder(Optional.ofNullable(
                yamlConfiguration.getString("fill-item.material")).orElse("AIR"));
        this.fillItem.setName(loadColoredString("fill-item.name"));
        this.fillItem.setLore(loadColoredStringList("fill-item.lore"));

        this.commandUsageMessage = loadColoredString("message.command-usage");
        this.commandPermissionMessage = loadColoredString("message.command-permission");
        this.categoryPermissionMessage = loadColoredString("message.category-permission");
        this.categoryOthersPermissionMessage = loadColoredString("message.category-others-permission");
        this.shopOpenOthersPermissionMessage = loadColoredString("message.shop-open-others-permission");
        this.categoryNotFound = loadColoredString("message.category-not-found");
        this.playerNotFound = loadColoredString("message.player-not-found");
    }

    private void loadCommands(){
        this.plugin.getCommandManager().unregisterAll();
        final ConfigurationSection commandsSection = this.yamlConfiguration.getConfigurationSection("command");

        if (isNull(commandsSection)){
            return;
        }

        // shop command
        final Object[] shopCommandData = this.loadCommand(commandsSection, "shop");
        final Command shopCommand = new ShopCommand(this.plugin, (String) shopCommandData[0], (String[]) shopCommandData[1]);

        this.plugin.getCommandManager().register(shopCommand);

        // open sub command
        final Object[] openCommandData = this.loadCommand(commandsSection, "open");
        final Command openCommand = new OpenCommand(this.plugin, (String) openCommandData[0], (String[]) openCommandData[1]);

        shopCommand.addSubCommand(openCommand);

        // edit sub command
        final Object[] editCommandData = this.loadCommand(commandsSection, "edit");
        final Command editCommand = new EditCommand(this.plugin, (String) editCommandData[0], (String[]) editCommandData[1]);

        shopCommand.addSubCommand(editCommand);

        // reload sub command
        final Object[] reloadCommandData = this.loadCommand(commandsSection, "reload");
        final Command reloadCommand = new ReloadCommand(this.plugin, (String) reloadCommandData[0], (String[]) reloadCommandData[1]);

        shopCommand.addSubCommand(reloadCommand);
    }

    private void loadCategories(){
        final CategoryList categories = this.plugin.getCategories();
        categories.clear();

        final ConfigurationSection categoriesSection = yamlConfiguration.getConfigurationSection("categories");

        if (isNull(categoriesSection)){
            return;
        }

        for (String key : categoriesSection.getKeys(false)) {

            final ConfigurationSection categorySection = categoriesSection.getConfigurationSection(key);

            if (isNull(categorySection)){
                continue;
            }

            final String name = Colors.fixColors(Optional.ofNullable(categorySection.getString("name"))
                    .orElse("name"));

            final ConfigurationSection itemSection = categorySection.getConfigurationSection("item");

            if (isNull(itemSection)){
                continue;
            }

            final Integer itemSlot = Optional.of(itemSection.getInt("slot"))
                    .filter(integer -> integer > -1 && integer < this.guiRows * 54 - 1).orElse(0);

            final String material = Optional.ofNullable(itemSection.getString("material")).orElse("STONE");
            final String itemName = Colors.fixColors(Optional.ofNullable(itemSection.getString("name")).orElse(key));
            final List<String> itemLore = Colors.fixColors(itemSection.getStringList("lore"));

            final Integer customModelData = itemSection.isInt("custom-model-data") ? itemSection.getInt("custom-model-data") : null;

            final ItemBuilder itemBuilder = new ItemBuilder(material).
                    setName(itemName)
                    .setLore(itemLore)
                    .setCustomModelData(customModelData);

            final ItemStack itemStack = itemBuilder.build();

            if (itemStack == null || itemStack.getType() == Material.AIR){
                continue;
            }

            categories.add(new Category(key, name, itemSlot, itemBuilder));
        }
    }

    private void createConfigurationFile(){
        this.plugin.saveResource(CONFIG_FILE_NAME, true);
        this.loadConfigurationFile();
    }

    private void loadConfigurationFile(){
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.configFile);
    }

    private String loadColoredString(final String path){
        return Colors.fixColors(this.yamlConfiguration.getString(path));
    }

    private List<String> loadColoredStringList(final String path){
        return Colors.fixColors(this.yamlConfiguration.getStringList(path));
    }

    private Object[] loadCommand(final ConfigurationSection mainSection, final String command){

        final ConfigurationSection section = mainSection.getConfigurationSection(command);

        if (isNull(section)){
            return new Object[]{command, new String[0]};
        }

        final String label = Optional.ofNullable(section.getString("label")).orElse(command);
        final List<String> aliases = Optional.of(section.getStringList("aliases")).orElse(Collections.emptyList());

        return new Object[]{label, aliases.toArray(new String[0])};
    }

}

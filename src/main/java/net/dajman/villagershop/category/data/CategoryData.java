package net.dajman.villagershop.category.data;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import net.dajman.villagershop.util.ItemSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import java.io.*;

import static java.util.Objects.isNull;

public class CategoryData {

    private static final String CATEGORY_DATA_FILE_EXTENSION = ".dat";
    private static final String OLD_DATA_FILE_NAME = "shop" + CATEGORY_DATA_FILE_EXTENSION;
    private static final String CATEGORIES_DATA_FOLDER = "categories";
    private static final String BLOCKS_RESOURCE_PATH = "categories/blocks.dat";
    private static final String RESOURCES_RESOURCE_PATH = "categories/resources.dat";
    private static final String TOOLS_RESOURCE_PATH = "categories/tools.dat";

    private final Main plugin;

    public CategoryData(Main plugin) {
        this.plugin = plugin;
    }

    private void deleteFile(final File file){
        if (!file.delete()){
            System.out.println("[VillagerShop] Error while deleting file " + file.getName());
        }
    }

    public void load(){

        final File dataFolder = this.plugin.getDataFolder();
        if (!dataFolder.exists()){
            return;
        }

        final File oldDataFile = new File(dataFolder, OLD_DATA_FILE_NAME);
        final File categoryFolder = new File(dataFolder, CATEGORIES_DATA_FOLDER);

        if (oldDataFile.exists()){

            if (!categoryFolder.exists()){

                this.loadOldDataFile();
                this.plugin.getCategories().forEach(this::save);

                this.deleteFile(oldDataFile);
                return;
            }

            this.deleteFile(oldDataFile);
        }

        if (!categoryFolder.exists()){

            if (!categoryFolder.mkdir()){
                System.out.println("[VillagerShop] Error while creating categories data folder.");
                return;
            }

            this.plugin.saveResource(BLOCKS_RESOURCE_PATH, false);
            this.plugin.saveResource(RESOURCES_RESOURCE_PATH, false);
            this.plugin.saveResource(TOOLS_RESOURCE_PATH, false);
        }

        for (Category category : this.plugin.getCategories()) {
            final File categoryFile = new File(categoryFolder, category.getPath() + CATEGORY_DATA_FILE_EXTENSION);

            if (!categoryFile.exists()){
                continue;
            }

            try{
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(categoryFile));

                final String data = bufferedReader.readLine();

                bufferedReader.close();

                if (isNull(data)){
                    System.out.println("[VillagerShop] Error while reading file " + category.getPath()  + CATEGORY_DATA_FILE_EXTENSION);
                    continue;
                }

                final ItemStack[] contents = ItemSerializer.stringToItems(data);
                category.getConfigInventory().setContents(contents);

            }catch (IOException e){
                e.printStackTrace();
                System.out.println("[VillagerShop] Error while reading file " + category.getPath()  + CATEGORY_DATA_FILE_EXTENSION);
            }

        }

    }

    @Deprecated
    public void loadOldDataFile(){
        final File dataFolder = this.plugin.getDataFolder();
        if (!dataFolder.exists()){
            return;
        }

        final File file = new File(this.plugin.getDataFolder(), OLD_DATA_FILE_NAME);
        if (!file.exists()){
            return;
        }

        final YamlConfiguration c = YamlConfiguration.loadConfiguration(file);

        for (Category category : this.plugin.getCategories()) {

            final String data = c.getString(category.getPath());

            if (data == null){
                continue;
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

        final File categoryFolder = new File(dataFolder, CATEGORIES_DATA_FOLDER);

        if (!categoryFolder.exists() && !categoryFolder.mkdir()){
            System.out.println("[VillagerShop] Error while creating categories data folder." );
            return;
        }

        final String data = ItemSerializer.itemsToString(category.getConfigInventory().getContents());

        final File categoryFile = new File(categoryFolder, category.getPath() + CATEGORY_DATA_FILE_EXTENSION);

        if (categoryFile.exists()){
            try{
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(categoryFile));

                final String dataFromFile = bufferedReader.readLine();

                bufferedReader.close();

                if (data.equals(dataFromFile)){
                    return;
                }

            }catch (IOException e){
                e.printStackTrace();
            }
        }

        try{
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(categoryFile));

            bufferedWriter.write(data);
            bufferedWriter.close();

        }catch (IOException e){
            System.out.println("[VillagerShop] Error while saving file " + category.getName());
            e.printStackTrace();
        }

    }

}

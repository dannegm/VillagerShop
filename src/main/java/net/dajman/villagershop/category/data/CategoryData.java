package net.dajman.villagershop.category.data;

import net.dajman.villagershop.Main;
import net.dajman.villagershop.category.Category;
import net.dajman.villagershop.util.logging.Logger;
import java.io.*;
import java.util.Optional;

import static java.util.Objects.isNull;

public class CategoryData {

    private static final Logger LOGGER = Logger.getLogger(CategoryData.class);

    private static final String CATEGORY_DATA_FILE_EXTENSION = ".dat";
    private static final String CATEGORIES_DATA_FOLDER = "categories";
    private static final String BLOCKS_RESOURCE_PATH = "categories/blocks.dat";
    private static final String RESOURCES_RESOURCE_PATH = "categories/resources.dat";
    private static final String TOOLS_RESOURCE_PATH = "categories/tools.dat";

    private final Main plugin;

    public CategoryData(Main plugin) {
        this.plugin = plugin;
    }

    public void load(){

        LOGGER.info("load() Loading data of categories");

        final File dataFolder = this.plugin.getDataFolder();
        if (!dataFolder.exists()){

            LOGGER.info("load() Data folder of this plugin doesn't exist. Loaded 0 categories");
            return;
        }

        final File categoryFolder = new File(dataFolder, CATEGORIES_DATA_FOLDER);

        if (!categoryFolder.exists()){

            LOGGER.debug("load() category folder does not exist.");

            if (!categoryFolder.mkdir()){

                LOGGER.error("load() Error while creating categories data folder.");
                return;
            }

            LOGGER.debug("load() Saving resource files...");

            this.plugin.saveResource(BLOCKS_RESOURCE_PATH, false);
            this.plugin.saveResource(RESOURCES_RESOURCE_PATH, false);
            this.plugin.saveResource(TOOLS_RESOURCE_PATH, false);

            LOGGER.debug("load() Resources saved.");
        }

        LOGGER.debug("load() Loading categories...");

        for (Category category : this.plugin.getCategories()) {

            LOGGER.debug("load() Loading category={}", category.getName());

            final File categoryFile = new File(categoryFolder, category.getPath() + CATEGORY_DATA_FILE_EXTENSION);

            if (!categoryFile.exists()){

                LOGGER.warn("load() Data file for category={} not found", category.getName());
                continue;
            }

            try{
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(categoryFile));

                final String data = bufferedReader.readLine();

                bufferedReader.close();

                if (isNull(data)){

                    LOGGER.error("load() Error while reading file={} for category={}, data is null",
                            category.getPath() + CATEGORY_DATA_FILE_EXTENSION, category.getName());
                    continue;
                }

                try{
                    this.plugin.getItemStackSerializer().deserialize(data).ifPresent(
                            items -> category.getConfigInventory().setContents(items));

                    LOGGER.debug("load() Data of category={} loaded.", category.getName());

                }catch (Exception e){
                    LOGGER.error("Error while deserializing items from file {}. {}",
                            category.getName(), Logger.getMessage(e));
                }

            }catch (IOException e){
                LOGGER.error("load() Error while reading file={} for category={}. {}",
                        category.getPath() + CATEGORY_DATA_FILE_EXTENSION,
                        category.getName(), Logger.getMessage(e));
            }

        }

    }

    public void save(final Category category){

        LOGGER.debug("save() Saving category={}...", category.getName());

        final File dataFolder = this.plugin.getDataFolder();

        if (!dataFolder.exists() && !dataFolder.mkdir()){

            LOGGER.error("save() Error while creating plugin data folder.");
            return;
        }

        final File categoryFolder = new File(dataFolder, CATEGORIES_DATA_FOLDER);

        if (!categoryFolder.exists() && !categoryFolder.mkdir()){

            LOGGER.error("save() Error while creating categories data folder.");
            return;
        }

        final String data;
        try{
            data = this.plugin.getItemStackSerializer().serialize(category.getConfigInventory().getContents())
                    .orElse("");

        } catch (Exception e) {

            LOGGER.error("Error while serializing items of category={}. {}",
                    category.getName(), Logger.getMessage(e));
            return;
        }

        LOGGER.debug("save() Serialized items={} for category={}", data, category.getName());

        final File categoryFile = new File(categoryFolder, category.getPath() + CATEGORY_DATA_FILE_EXTENSION);

        if (categoryFile.exists()){

            LOGGER.debug("save() categoryFile={} for category={} already exist, trying to read file",
                    categoryFile.getName(), category.getName());

            try{
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(categoryFile));

                final String dataFromFile = bufferedReader.readLine();

                LOGGER.debug("save() Read data={} from file={}", Optional.ofNullable(dataFromFile).orElse(""),
                        categoryFile.getName());

                bufferedReader.close();

                if (data.equals(dataFromFile)){

                    LOGGER.debug("save() oldData is equals to new data, writing is not necessary.",
                            dataFromFile, data);
                    return;
                }

            }catch (IOException e){
                LOGGER.error("save() Error while reading file={}. {}", categoryFile.getName(), Logger.getMessage(e));
            }
        }


        LOGGER.debug("save() Trying to save data of category={} to file={}", category.getName(), categoryFile.getName());
        try{
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(categoryFile));

            bufferedWriter.write(data);
            bufferedWriter.close();

            LOGGER.debug("save() Data of category={} saved.", category.getName());

        }catch (IOException e){
            LOGGER.error("save() Error while saving file={}. {}", categoryFile.getName(), Logger.getMessage(e));
        }

    }

}

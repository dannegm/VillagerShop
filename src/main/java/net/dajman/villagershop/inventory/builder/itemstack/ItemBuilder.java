package net.dajman.villagershop.inventory.builder.itemstack;

import net.dajman.villagershop.common.Builder;
import net.dajman.villagershop.util.Colors;
import net.dajman.villagershop.util.Numbers;
import net.dajman.villagershop.common.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class ItemBuilder implements Builder<ItemStack> {

    private static final Logger LOGGER = Logger.getLogger(ItemBuilder.class);

    private Material material;
    private Integer amount;
    private Short damage;
    private Integer customModelData;
    private String name;
    private List<String> lore;

    public ItemBuilder(final Material material){
        this.material = material;
        this.amount = 1;
        this.damage = 0;
        this.lore = new ArrayList<>();
    }

    public ItemBuilder(final Material material, final int amount){
        this.material = material;
        this.amount = amount;
        this.damage = 0;
        this.lore = new ArrayList<>();
    }

    public ItemBuilder(final Material material, final int amount, final short damage){
        this.material = material;
        this.amount = amount;
        this.damage = damage;
        this.lore = new ArrayList<>();
    }


    public ItemBuilder(final String item){
        this.lore = new ArrayList<>();
        this.material = Material.AIR;
        this.damage = 0;
        this.amount = 1;
        String itemName = item;
        if (item.contains(" ")){
            final String[] splited = item.split(" ");

            Numbers.parseInt(splited[1]).ifPresent(integer -> this.amount = integer);
            itemName = splited[0];

            if (splited.length > 2 && splited[2].toLowerCase().startsWith("custommodeldata:")){
                Numbers.parseInt(splited[2].substring(16)).ifPresent(integer -> this.customModelData = integer);
            }
        }
        if (itemName.contains(":")){
            final String[] splited = itemName.split(":");

            itemName = splited[0];

            Numbers.parseShort(splited[1]).ifPresent(shortVal -> this.damage = shortVal);
        }

        final Optional<Integer> optionalMaterialInt = Numbers.parseInt(itemName);

        if (optionalMaterialInt.isPresent()){
            try{
                final Method method = Material.class.getMethod("getMaterial", int.class);
                this.material = (Material) method.invoke(Material.class, optionalMaterialInt.get());

                return;
            }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.debug("ItemBuilder() Getting material by id failed. {}", e);
            }
        }

        this.material = getMaterialFromString(itemName);
    }

    public ItemBuilder setAmount(final Integer amount){
        this.amount = amount;
        return this;
    }

    public ItemBuilder setCustomModelData(final Integer customModelData){
        this.customModelData = customModelData;
        return this;
    }

    public ItemBuilder setName(final String name) {
        this.name = nonNull(name) ? Colors.fixColors(name) : "";
        return this;
    }

    public ItemBuilder setLore(final List<String> lore) {
        this.lore = nonNull(lore) ? Colors.fixColors(lore) : Collections.emptyList();
        return this;
    }

    public ItemBuilder setLore(final String... lore) {
        this.lore = nonNull(lore) ? Colors.fixColors(Arrays.asList(lore)) : Collections.emptyList();
        return this;
    }

    public ItemBuilder addLore(final String text){
        this.lore.add(text != null ? Colors.fixColors(text) : "");
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public short getDamage() {
        return damage;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    @Override
    public ItemStack build() {
        return this.build(new String[0]);
    }

    public ItemStack build(final String... replacements){
        final ItemStack itemStack = new ItemStack(this.material, this.amount, this.damage);
        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (isNull(itemMeta)){
            return itemStack;
        }

        if (nonNull(this.name)){

            String name = this.name;

            for(int i = 1; i < replacements.length; i += 2){
                name = name.replace(replacements[i - 1], replacements[i]);
            }

            itemMeta.setDisplayName(name);
        }

        if (nonNull(this.lore) && !this.lore.isEmpty()){

            itemMeta.setLore(this.lore.stream().map(line -> {

                for(int i = 1; i < replacements.length; i += 2){
                    line = line.replace(replacements[i - 1], replacements[i]);
                }

                return line;
            }).collect(toList()));
        }

        if (nonNull(this.customModelData)){

            try{
                final Method method = itemMeta.getClass().getMethod("setCustomModelData", Integer.class);
                method.invoke(itemMeta, this.customModelData);

            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                LOGGER.debug("Setting custom model data failed. {}", e);
            }
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    private static Material getMaterialFromString(String mat){
        return  Optional.ofNullable(Material.getMaterial(mat.toUpperCase())).orElse(Material.AIR);
    }

}
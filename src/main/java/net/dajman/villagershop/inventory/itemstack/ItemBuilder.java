package net.dajman.villagershop.inventory.itemstack;

import com.sun.istack.internal.NotNull;
import net.dajman.villagershop.util.ColorUtil;
import net.dajman.villagershop.util.NumberUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ItemBuilder{

    private Material material;
    private int amount;
    private short damage;
    private String name;
    private List<String> lore;

    public ItemBuilder(@NotNull final Material material){
        this.material = material;
        this.amount = 1;
        this.damage = 0;
        this.lore = new ArrayList<>();
    }

    public ItemBuilder(@NotNull final Material material, final int amount){
        this.material = material;
        this.amount = amount;
        this.damage = 0;
        this.lore = new ArrayList<>();
    }

    public ItemBuilder(@NotNull final Material material, final int amount, final short damage){
        this.material = material;
        this.amount = amount;
        this.damage = damage;
        this.lore = new ArrayList<>();
    }


    public ItemBuilder(@NotNull final String item){
        this.lore = new ArrayList<>();
        this.material = Material.AIR;
        this.damage = 0;
        this.amount = 1;
        String itemName = item;
        if (item.contains(" ")){
            final String[] splited = item.split(" ");
            if (NumberUtil.isInteger(splited[1])){
                this.amount = Integer.parseInt(splited[1]);
            }
            itemName = splited[0];
        }
        if (itemName.contains(":")){
            final String[] splited = itemName.split(":");
            itemName = splited[0];
            if (NumberUtil.isShort(splited[1])) {
                this.damage = Short.parseShort(splited[1]);
            }
        }
        if (NumberUtil.isInteger(itemName)){
            try{
                final Method method = Material.class.getMethod("getMaterial", int.class);
                this.material = (Material) method.invoke(Material.class, Integer.parseInt(itemName));
                return;
            }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            }
        }
        this.material = getMaterialfromString(itemName);
    }

    public ItemBuilder setAmount(final int amount){
        this.amount = amount;
        return this;
    }

    public ItemBuilder setName(String name) {
        this.name = name != null ? ColorUtil.fixColors(name) : "";
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore != null ? ColorUtil.fixColors(lore) : Collections.emptyList();
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        this.lore = ColorUtil.fixColors(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLore(final String text){
        this.lore.add(text != null ? ColorUtil.fixColors(text) : "");
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

    public ItemStack build(){
        final ItemStack itemStack = new ItemStack(this.material, this.amount, this.damage);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null){
            return itemStack;
        }
        if (this.name != null){
            itemMeta.setDisplayName(this.name);
        }
        if (this.lore != null && !this.lore.isEmpty()){
            itemMeta.setLore(this.lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public ItemStack build(final Map<String, String> replaces){
        final ItemStack itemStack = new ItemStack(this.material, this.amount, this.damage);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null){
            return itemStack;
        }
        if (this.name != null){
            String name = this.name;
            for(Map.Entry<String, String> entry : replaces.entrySet()){
                name = name.replace(entry.getKey(), entry.getValue());
            }
            itemMeta.setDisplayName(name);
        }
        if (this.lore != null && !this.lore.isEmpty()){
            final List<String> lore = new ArrayList<>();
            this.lore.forEach(line -> {
                for(Map.Entry<String, String> entry : replaces.entrySet()){
                    line = line.replace(entry.getKey(), entry.getValue());
                }
                lore.add(line);
            });
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    private static Material getMaterialfromString(String mat){
        Material m = Material.getMaterial(mat.toUpperCase());
        if (m != null){
            return m;
        }
        return Material.AIR;
    }
}
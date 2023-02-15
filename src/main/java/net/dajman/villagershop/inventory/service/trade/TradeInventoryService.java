package net.dajman.villagershop.inventory.service.trade;

import net.dajman.villagershop.data.category.Category;
import net.dajman.villagershop.hook.placeholder.PlaceholderHook;
import net.dajman.villagershop.util.Reflection;
import net.dajman.villagershop.common.logging.Logger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TradeInventoryService {

    private static final Logger LOGGER = Logger.getLogger(TradeInventoryService.class);

    private final int version = Reflection.getVersionNumber();

    private Class<?> entityVillagerAbstract;
    private Class<?> entityVillager;
    private Method setCustomName;
    private Constructor<?> evConstructor;

    private Field recipeListField;
    private Method openTradeMethod;
    private Method setTradingPlayer;

    private Class<?> craftWorld;
    private Method getHandleCraftWorld;
    private Class<?> nmsWorld;
    private Class<?> entityTypes;
    private Object entityTypesVillager;

    private Class<?> craftPlayer;
    private Method getHandleCraftPlayer;
    private Class<?> entityPlayer;
    private Class<?> entityHuman;

    private Class<?> iChatBaseComponent;
    private Class<?> craftChatMessage;
    private Method fromStringOrNull;

    private Class<?> merchantRecipeList;
    private Class<?> merchantRecipe;
    private Constructor<?> merchantRecipeConstructor;
    private Class<?> craftItemStack;
    private Method asNMSCopy;
    private Class<?> nmsItemStack;

    public TradeInventoryService() {

        LOGGER.debug("TradeInventory() Initialization for version: {} ", Integer.toString(this.version));

        try {

            if (this.version >= 1170){

                this.nmsItemStack = Reflection.getClass("net.minecraft.world.item.ItemStack");
                this.merchantRecipe = Reflection.getClass("net.minecraft.world.item.trading.MerchantRecipe");
                this.merchantRecipeList = Reflection.getClass("net.minecraft.world.item.trading.MerchantRecipeList");
                this.entityPlayer = Reflection.getClass("net.minecraft.server.level.EntityPlayer");
                this.nmsWorld = Reflection.getClass("net.minecraft.world.level.World");
                this.entityVillagerAbstract = Reflection.getClass("net.minecraft.world.entity.npc.EntityVillagerAbstract");
                this.entityVillager = Reflection.getClass("net.minecraft.world.entity.npc.EntityVillager");
                this.entityHuman = Reflection.getClass("net.minecraft.world.entity.player.EntityHuman");
                this.entityTypes = Reflection.getClass("net.minecraft.world.entity.EntityTypes");
                this.entityTypesVillager = Reflection.getFieldVal(this.entityTypes, "aV");
                this.iChatBaseComponent = Reflection.getClass("net.minecraft.network.chat.IChatBaseComponent");
                this.craftChatMessage = Reflection.getCBClass("util.CraftChatMessage");
                this.fromStringOrNull = this.craftChatMessage.getMethod("fromStringOrNull", String.class);
                this.setTradingPlayer = this.entityVillager.getDeclaredMethod(this.version >= 1180 ? "f" : "setTradingPlayer", this.entityHuman);
                this.setCustomName = this.entityVillager.getMethod(this.version >= 1180 ? "a": "setCustomName", this.iChatBaseComponent);

                if (this.version >= 1192) {
                    this.entityTypesVillager = Reflection.getFieldVal(this.entityTypes, "ba");
                }
            } else {

                this.entityVillager = Reflection.getNmsClass("EntityVillager");
                this.iChatBaseComponent = Reflection.getNmsClass("IChatBaseComponent");
                this.entityHuman = Reflection.getNmsClass("EntityHuman");
                this.nmsItemStack = Reflection.getNmsClass("ItemStack");
                this.merchantRecipe = Reflection.getNmsClass("MerchantRecipe");
                this.merchantRecipeList = Reflection.getNmsClass("MerchantRecipeList");
                this.entityPlayer = Reflection.getNmsClass("EntityPlayer");
                this.nmsWorld = Reflection.getNmsClass("World");

                this.setTradingPlayer = this.entityVillager.getDeclaredMethod(this.version >= 190 ? "setTradingPlayer" : "a_", this.entityHuman);
                this.setCustomName = this.entityVillager.getMethod("setCustomName", this.version >= 1130 ? this.iChatBaseComponent : String.class);

                if (this.version >= 1130){
                    this.craftChatMessage = Reflection.getCBClass("util.CraftChatMessage");
                    this.fromStringOrNull = this.craftChatMessage.getMethod("fromStringOrNull", String.class);
                }
                if (this.version >= 1140){
                    this.entityVillagerAbstract = Reflection.getNmsClass("EntityVillagerAbstract");
                    this.entityTypes = Reflection.getNmsClass("EntityTypes");
                    this.entityTypesVillager = Reflection.getFieldVal(this.entityTypes, "VILLAGER");
                }
            }

            this.craftItemStack = Reflection.getCBClass("inventory.CraftItemStack");
            this.asNMSCopy = this.craftItemStack.getMethod("asNMSCopy", ItemStack.class);
            this.merchantRecipeConstructor = this.version >= 1140 ? this.merchantRecipe.getConstructor(nmsItemStack, nmsItemStack, nmsItemStack, int.class, int.class, float.class) : this.merchantRecipe.getConstructor(nmsItemStack, nmsItemStack, nmsItemStack, int.class, int.class);
            this.craftPlayer = Reflection.getCBClass("entity.CraftPlayer");
            this.getHandleCraftPlayer = this.craftPlayer.getMethod("getHandle");
            this.craftWorld = Reflection.getCBClass("CraftWorld");
            this.getHandleCraftWorld = this.craftWorld.getMethod("getHandle");


            this.evConstructor = this.version >= 1140 ? this.entityVillager.getConstructor(this.entityTypes, this.nmsWorld) : this.entityVillager.getConstructor(this.nmsWorld, int.class);

            if (this.version >= 1140){

                this.recipeListField = Reflection.getFieldByType(this.entityVillagerAbstract, this.merchantRecipeList);
                this.openTradeMethod = this.entityVillager.getDeclaredMethod("h", this.entityHuman);

            } else if (this.version >= 180){

                this.recipeListField = Reflection.getFieldByType(this.entityVillager, this.merchantRecipeList);
                final Class<?> iMerchant = Reflection.getNmsClass("IMerchant");
                this.openTradeMethod = entityHuman.getDeclaredMethod("openTrade", iMerchant);

            }

            if (this.openTradeMethod != null){
                this.openTradeMethod.setAccessible(true);
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public void open(Player player, Category category){
        try {
            final String categoryNameWithPlaceholders = PlaceholderHook.setAllPlaceholders(player, category.getName());
            final Object entityVillager = this.createEntityVillager(player.getWorld());
            this.setCustomName(entityVillager, categoryNameWithPlaceholders);
            recipeListField.set(entityVillager, this.getRecipes(category.getConfigInventories()));
            this.openTrade(this.getHandleCraftPlayer.invoke(this.craftPlayer.cast(player)), entityVillager);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    private Object convert(final ItemStack itemStack) throws InvocationTargetException, IllegalAccessException {
        if (itemStack == null){
            return null;
        }
        return this.asNMSCopy.invoke(this.craftItemStack, itemStack);
    }

    private Object getRecipes(final List<Inventory> inventories) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        final Object list = this.merchantRecipeList.newInstance();

        for (Inventory inventory : inventories) {
            for (int i = 0; i < 9; i++) {
                if (this.addMerchantRecipe(list, inventory, i)) {
                    continue;
                }
                return list;
            }
        }

        return list;
    }


    private boolean addMerchantRecipe(final Object merchantRecipes, final Inventory inventory, final int i) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final ItemStack item1 = inventory.getItem(i),
                item2 = inventory.getItem(i + 9),
                item3 = inventory.getItem(i + 18);

        if (item1 == null || item1.getType() == Material.AIR || item3 == null || item3.getType() == Material.AIR){
            return false;
        }
        ((ArrayList)merchantRecipes).add(this.createMerchantRecipe(convert(item1), convert(Optional.ofNullable(item2).orElse(new ItemStack(Material.AIR))), convert(item3)));
        return true;
    }

    private Object createMerchantRecipe(final Object item1, final Object item2, final Object item3) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (this.version >= 1140)
            return this.merchantRecipeConstructor.newInstance(item1, item2, item3, 999, 99999, 0);
        return this.merchantRecipeConstructor.newInstance(item1, item2, item3, 999, 99999);
    }

    private void openTrade(final Object nmsPlayer, final Object entityVillager) throws InvocationTargetException, IllegalAccessException {
        if (this.version >= 1140)
            this.openTradeMethod.invoke(entityVillager, nmsPlayer);
        else {
            this.setTradingPlayer.invoke(entityVillager, nmsPlayer);
            this.openTradeMethod.invoke(nmsPlayer, entityVillager);
        }
    }

    private void setCustomName(final Object entityVillager, final String name) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (this.version >= 1130)
            this.setCustomName.invoke(entityVillager, this.fromStringOrNull.invoke(this.craftChatMessage, name));
        else
            this.setCustomName.invoke(entityVillager, name);
    }

    private Object createEntityVillager(final World world) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (this.version >= 1140)
            return this.evConstructor.newInstance(this.entityTypesVillager, this.getHandleCraftWorld.invoke(this.craftWorld.cast(world)));
        return this.evConstructor.newInstance(this.getHandleCraftWorld.invoke(this.craftWorld.cast(world)), 0);
    }
}

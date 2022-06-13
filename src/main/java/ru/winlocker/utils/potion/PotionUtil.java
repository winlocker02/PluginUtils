package ru.winlocker.utils.potion;

import com.cryptomorin.xseries.*;
import lombok.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.*;

import java.lang.reflect.*;
import java.util.*;

import static com.cryptomorin.xseries.ReflectionUtils.supports;

public class PotionUtil {

    private static final Class<?> MOB_EFFECT, POTION_REGISTRY, CRAFT_POTION_UTIL;

    static {
        CRAFT_POTION_UTIL = ReflectionUtils.getCraftClass("potion.CraftPotionUtil");

        if(supports(17)) {
            POTION_REGISTRY = ReflectionUtils.getNMSClass("world.item.alchemy.PotionRegistry");
            MOB_EFFECT = ReflectionUtils.getNMSClass("world.effect.MobEffect");
        } else {
            POTION_REGISTRY = ReflectionUtils.getNMSClass("PotionRegistry");
            MOB_EFFECT = ReflectionUtils.getNMSClass("MobEffect");
        }
    }

    public static List<PotionEffect> getPotionEffects(@NonNull PotionData potionData) {
        try {
            Method fromBukkit = CRAFT_POTION_UTIL.getMethod("fromBukkit", PotionData.class);
            String potionDataObj = (String) fromBukkit.invoke(null, new PotionData(potionData.getType(), potionData.isExtended(), potionData.isUpgraded()));

            Method potionRegistryStaticMethod = POTION_REGISTRY.getMethod("a", String.class);
            Object potionRegistry = potionRegistryStaticMethod.invoke(null, potionDataObj);

            Method getMobEffects = POTION_REGISTRY.getMethod("a");

            @SuppressWarnings("rawtypes")
            List mobEffects = (List) getMobEffects.invoke(potionRegistry);

            List<PotionEffect> effects = new ArrayList<>();

            Method toBukkit = CRAFT_POTION_UTIL.getMethod("toBukkit", MOB_EFFECT);

            for (Object mobEffect : mobEffects) {
                PotionEffect potionEffect = (PotionEffect) toBukkit.invoke(null, mobEffect);

                effects.add(potionEffect);
            }

            return effects;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("Error get potion effects", e);
        }
    }

    public static List<PotionEffect> getPotionEffectsFromItemStack(ItemStack itemStack) {
        if(isInvalidPotion(itemStack))
            return null;

        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();

        List<PotionEffect> potionEffects = new ArrayList<>();
        PotionData potionData = potionMeta.getBasePotionData();

        if(potionData != null)
            potionEffects.addAll(getPotionEffects(potionData));

        if(potionMeta.hasCustomEffects())
            potionEffects.addAll(potionMeta.getCustomEffects());

        return potionEffects;
    }

    public static List<ItemStack> getPotionsFromInventory(@NonNull Inventory inventory) {
        List<ItemStack> itemStacks = new ArrayList<>();

        for (int index = 0; index < inventory.getSize(); index++) {
            val itemStack = inventory.getItem(index);

            if(itemStack != null && itemStack.getType().name().endsWith("POTION")) {
                itemStacks.add(itemStack);
            }
        }

        itemStacks.sort(Comparator.comparing(ItemStack::getType));

        return itemStacks;
    }

    public static boolean isInvalidPotion(ItemStack itemStack) {
        return itemStack == null || !itemStack.hasItemMeta() || !(itemStack.getItemMeta() instanceof PotionMeta);
    }
}

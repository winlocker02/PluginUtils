package ru.winlocker.utils.item;

import com.cryptomorin.xseries.*;
import de.tr7zw.changeme.nbtapi.*;
import lombok.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.*;
import ru.winlocker.utils.item.attribute.*;

import java.util.*;

@AllArgsConstructor
public class ItemBuilder {

    public static ItemBuilder builder(XMaterial xMaterial) {
        return new ItemBuilder(xMaterial);
    }
    public static ItemBuilder builder(Material material) {
        return new ItemBuilder(material);
    }
    public static ItemBuilder builder(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    private ItemStack itemStack;

    public ItemBuilder(@NonNull XMaterial xMaterial) {
        this(Objects.requireNonNull(xMaterial.parseMaterial()));
    }

    public ItemBuilder(@NonNull Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder durability(int durability) {
        return durability((short) durability);
    }

    public ItemBuilder durability(short durability) {
        this.itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setUnbreakable(unbreakable);

        this.itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder name(String displayName) {

        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);

        this.itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setLore(lore);

        this.itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder clearLore() {
        return lore(null);
    }

    public ItemBuilder skullTexture(@NonNull String texture) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();

        if(itemMeta instanceof SkullMeta)
            this.itemStack.setItemMeta(SkullUtils.applySkin(itemMeta, texture));

        return this;
    }

    public ItemBuilder enchant(@NonNull Enchantment enchantment, int level) {
        this.itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantAll(int level) {
        for (Enchantment enchantment : Enchantment.values()) {
            if(!enchantment.isCursed()) {
                this.itemStack.addUnsafeEnchantment(enchantment, level);
            }
        }
        return this;
    }

    public ItemBuilder clearEnchants() {
        this.itemStack.getEnchantments().keySet().forEach(this.itemStack::removeEnchantment);
        return this;
    }

    public ItemBuilder flags(@NonNull ItemFlag...itemFlags) {

        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addItemFlags(itemFlags);

        this.itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder flagsAll() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());

        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder clearFlags() {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.getItemFlags().forEach(itemMeta::removeItemFlags);

        this.itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder attribute(@NonNull ItemAttribute attribute, int amount) {
        return attribute(attribute, null, amount);
    }

    public ItemBuilder attribute(@NonNull ItemAttribute attribute, ItemAttribute.Hand hand, int amount) {
        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompoundList compoundList = nbtItem.getCompoundList("AttributeModifiers");

        String attributeName = attribute.getMinecraftName();

        boolean find = false;

        for (NBTListCompound compound : compoundList) {
            if(compound.hasKey("AttributeName")) {
                String name = compound.getString("AttributeName").toLowerCase(Locale.ROOT);

                if(name.equalsIgnoreCase(attributeName)) {
                    compound.setInteger("Amount", amount);

                    if(hand != null) {
                        compound.setString("Slot", hand.getMinecraftName());
                    }

                    find = true;
                }
            }
        }

        if(!find) {
            NBTListCompound compound = compoundList.addCompound();

            compound.setInteger("Amount", amount);
            compound.setString("AttributeName", attributeName);
            compound.setString("Name", attributeName);
            compound.setInteger("Operation", 0);
            compound.setInteger("UUIDLeast", 894654);
            compound.setInteger("UUIDMost", 2872);

            if(hand != null) {
                compound.setString("Slot", hand.getMinecraftName());
            }
        }

        this.itemStack = nbtItem.getItem();

        return this;
    }

    public ItemBuilder clearAttributes() {
        NBTItem nbtItem = new NBTItem(this.itemStack);
        nbtItem.getCompoundList("AttributeModifiers").clear();

        this.itemStack = nbtItem.getItem();

        return this;
    }

    public ItemBuilder potionEffects(@NonNull List<PotionEffect> effects) {
        if(this.itemStack.getItemMeta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) this.itemStack.getItemMeta();
            effects.forEach(effect -> potionMeta.addCustomEffect(effect, true));

            this.itemStack.setItemMeta(potionMeta);
        }
        return this;
    }

    public ItemBuilder clearPotionEffects() {
        if(this.itemStack.getItemMeta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) this.itemStack.getItemMeta();
            potionMeta.clearCustomEffects();

            this.itemStack.setItemMeta(potionMeta);
        }
        return this;
    }

    public ItemBuilder potionColor(@NonNull Color color) {
        if(this.itemStack.getItemMeta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) this.itemStack.getItemMeta();
            potionMeta.setColor(color);

            this.itemStack.setItemMeta(potionMeta);
        }
        return this;
    }

    public ItemBuilder potionBaseEffect(@NonNull PotionData potionData) {
        if(this.itemStack.getItemMeta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) this.itemStack.getItemMeta();
            potionMeta.setBasePotionData(potionData);

            this.itemStack.setItemMeta(potionMeta);
        }
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }
}

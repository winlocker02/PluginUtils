package ru.winlocker.utils.inventory.item;

import de.tr7zw.changeme.nbtapi.*;
import lombok.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Getter
public class GuiItem {

    private final UUID uniqueId = UUID.randomUUID();

    private final ItemStack itemStack;

    private @Setter Consumer<InventoryClickEvent> action;
    private @Setter Sound sound;

    public GuiItem(ItemStack itemStack) {
        this(itemStack, null);
    }

    public GuiItem(@NonNull ItemStack itemStack, Consumer<InventoryClickEvent> action) {
        if(itemStack.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(itemStack);
            nbtItem.setString("inventory-item", uniqueId.toString());

            this.itemStack = nbtItem.getItem();
        } else {
            this.itemStack = itemStack;
        }
        this.action = action;
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    public ItemStack applyPlaceholders(Map<String, Supplier<String>> placeholders) {
        ItemStack applyItem = getItemStack();

        if(applyItem.hasItemMeta()) {
            ItemMeta itemMeta = applyItem.getItemMeta();

            if(itemMeta.hasDisplayName()) {
                String displayName = itemMeta.getDisplayName();

                for (Map.Entry<String, Supplier<String>> entry : placeholders.entrySet()) {
                    displayName = displayName.replace(entry.getKey(), entry.getValue().get());
                }

                itemMeta.setDisplayName(displayName);
            }

            if(itemMeta.hasLore()) {
                itemMeta.setLore(itemMeta.getLore().stream().map(line -> {
                    for (Map.Entry<String, Supplier<String>> entry : placeholders.entrySet()) {
                        line = line.replace(entry.getKey(), entry.getValue().get());
                    }
                    return line;
                }).collect(Collectors.toList()));
            }

            applyItem.setItemMeta(itemMeta);
        }

        return applyItem;
    }
}

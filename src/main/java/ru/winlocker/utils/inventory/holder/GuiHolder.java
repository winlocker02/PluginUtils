package ru.winlocker.utils.inventory.holder;

import lombok.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import ru.winlocker.utils.*;
import ru.winlocker.utils.inventory.*;

import java.util.*;
import java.util.function.*;

@Getter
public class GuiHolder implements InventoryHolder {

    private final @NonNull GuiContents contents;
    private final @NonNull GuiInventory guiInventory;

    private final Inventory inventory;

    public GuiHolder(@NonNull GuiContents contents, @NonNull GuiInventory guiInventory) {
        this.contents = contents;
        this.guiInventory = guiInventory;

        String title = contents.getTitle();

        for (Map.Entry<String, Supplier<String>> entry : contents.getPlaceholders().entrySet()) {
            title = title.replace(entry.getKey(), entry.getValue().get());
        }

        title = Utils.color(title);

        this.inventory = Bukkit.createInventory(this, contents.getRows() * 9, title);
    }

    public void updateItems() {
        contents.getItems().forEach((slot, item) -> inventory.setItem(slot, item.applyPlaceholders(contents.getPlaceholders())));
    }

    public void updateInventory() {
        new ArrayList<>(this.inventory.getViewers()).forEach(viewer -> {
            GuiContents contents = new GuiContents(this.guiInventory, this.contents.getObjects());
            contents.setAggregates(this.contents.getAggregates());

            ItemStack cursor = viewer.getItemOnCursor();
            viewer.setItemOnCursor(new ItemStack(Material.AIR));

            this.guiInventory.showInventory((Player) viewer, contents);

            viewer.setItemOnCursor(cursor);
        });
    }

    public void closeInventory() {
        this.inventory.getViewers().forEach(HumanEntity::closeInventory);
    }
}

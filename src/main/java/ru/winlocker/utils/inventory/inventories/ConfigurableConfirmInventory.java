package ru.winlocker.utils.inventory.inventories;

import lombok.*;
import org.bukkit.configuration.*;
import org.bukkit.entity.*;
import ru.winlocker.utils.inventory.*;
import ru.winlocker.utils.inventory.item.*;

public abstract class ConfigurableConfirmInventory extends ConfigurableInventory {

    public ConfigurableConfirmInventory(@NonNull ConfigurationSection configuration) {
        super(configuration);
    }

    @Override
    protected boolean initItem(@NonNull Player player, @NonNull GuiContents contents, @NonNull ConfigurableItem configurableItem, @NonNull GuiItem item) {
        switch (configurableItem.getName()) {
            case "ACCEPT" :
                item.setAction(e -> onAccept(player, contents, configurableItem, item));
                break;
            case "DECLINE" :
                item.setAction(e -> onDecline(player, contents, configurableItem, item));
                break;
        }
        return true;
    }

    protected abstract void onAccept(@NonNull Player player, @NonNull GuiContents contents, @NonNull ConfigurableItem configurableItem, @NonNull GuiItem item);
    protected abstract void onDecline(@NonNull Player player, @NonNull GuiContents contents, @NonNull ConfigurableItem configurableItem, @NonNull GuiItem item);
}

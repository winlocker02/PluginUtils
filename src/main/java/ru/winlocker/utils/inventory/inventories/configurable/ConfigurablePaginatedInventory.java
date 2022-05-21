package ru.winlocker.utils.inventory.inventories.configurable;

import lombok.*;
import org.bukkit.configuration.*;
import org.bukkit.entity.*;
import ru.winlocker.utils.inventory.*;
import ru.winlocker.utils.inventory.aggregates.impl.*;
import ru.winlocker.utils.inventory.item.*;

public abstract class ConfigurablePaginatedInventory extends ConfigurableInventory {

    public ConfigurablePaginatedInventory(@NonNull ConfigurationSection configuration) {
        super(configuration);
    }

    @Override
    protected void init(@NonNull Player player, @NonNull GuiContents contents) {

        if(this.configuration.isConfigurationSection("paginated")) {
            val section = this.configuration.getConfigurationSection("paginated");

            GuiSlot slot = GuiUtil.parseSlot(section.getString("slot"));
            GuiSlot toSlot = GuiUtil.parseSlot(section.getString("to-slot"));

            Paginated paginated = contents.getOrAddAggregate(Paginated.ofPage(slot.getX(), slot.getY(), toSlot.getX(), toSlot.getY()));
            initPaginated(player, contents, paginated);
        }

        super.init(player, contents);
    }

    @Override
    protected boolean initItem(@NonNull Player player, @NonNull GuiContents contents, @NonNull ConfigurableItem configurableItem, @NonNull GuiItem item) {
        Paginated paginated = contents.getAggregate(Paginated.class);
        String name = configurableItem.getName();

        if("NEXT".equals(name) || "PREVIOUS".equals(name)) {

            configurableItem.getSlots().forEach(slot -> {
                Paginated.PaginatedItem paginatedItem = new Paginated.PaginatedItem(configurableItem.getItemStack(), Paginated.PaginatedItemType.valueOf(name));

                paginatedItem.removeIfCompleted(true).sound(configurableItem.getSound());

                //paginatedItem.setSoundCompleted(configurableItem.getSound());

                paginated.getRender().setItem(slot, paginatedItem);
            });

            return false;
        }
        return true;
    }

    protected abstract void initPaginated(@NonNull Player player, @NonNull GuiContents contents, @NonNull Paginated paginated);
}

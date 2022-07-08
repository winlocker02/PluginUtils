package ru.winlocker.utils.inventory;

import com.google.common.collect.*;
import lombok.*;
import org.bukkit.entity.*;
import ru.winlocker.utils.inventory.holder.*;

import java.util.*;

public abstract class GuiInventory {

    private final @Getter Map<Player, GuiHolder> viewers = Maps.newConcurrentMap();

    public void showInventory(@NonNull Player player, Object...objects) {
        this.showInventory(player, new GuiContents(this, objects));
    }

    public void showInventory(@NonNull Player player, @NonNull GuiContents contents) {
        init(player, contents);

        contents.getAggregates().forEach(aggregate -> aggregate.init(player, contents, this));

        GuiHolder holder = new GuiHolder(contents, this);

        if(contents.getActionHolder() != null) {
            contents.getActionHolder().accept(holder);
        }

        holder.updateItems();

        player.openInventory(holder.getInventory());
    }

    public void updateInventory() {
        this.viewers.values().forEach(GuiHolder::updateInventory);
    }

    protected abstract void init(@NonNull Player player, @NonNull GuiContents contents);
}

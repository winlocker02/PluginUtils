package ru.winlocker.utils.inventory.aggregates;

import lombok.*;
import org.bukkit.entity.*;
import ru.winlocker.utils.inventory.*;

public interface Aggregate {

    void init(@NonNull Player player, @NonNull GuiContents contents, @NonNull GuiInventory inventory);
}

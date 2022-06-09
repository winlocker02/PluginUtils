package ru.winlocker.utils.inventory.aggregates;

import lombok.*;
import org.bukkit.entity.*;
import ru.winlocker.utils.inventory.*;
import ru.winlocker.utils.inventory.holder.*;

public interface Aggregate {

    void init(@NonNull Player player, @NonNull GuiContents contents, @NonNull GuiInventory inventory);
}

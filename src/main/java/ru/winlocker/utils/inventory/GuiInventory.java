package ru.winlocker.utils.inventory;

import com.cryptomorin.xseries.*;
import com.google.common.collect.*;
import lombok.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.*;
import ru.winlocker.utils.inventory.holder.*;
import ru.winlocker.utils.inventory.listener.*;

import java.util.*;

public abstract class GuiInventory {

    static {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(GuiInventory.class);
        Bukkit.getPluginManager().registerEvents(new GuiListener(plugin), plugin);
    }

    private final @Getter Map<Player, GuiHolder> viewers = Maps.newConcurrentMap();

    public void showInventory(@NonNull Player player, Object...objects) {
        this.showInventory(player, new GuiContents(this, objects));
    }

    public void showInventory(@NonNull Player player, @NonNull GuiContents contents) {
        init(player, contents);

        contents.getAggregates().forEach(aggregate -> aggregate.init(player, contents, this));

        GuiHolder holder = new GuiHolder(player, contents, this);
        player.openInventory(holder.getInventory());
    }

    public void updateInventory() {
        this.viewers.values().forEach(GuiHolder::updateInventory);
    }

    public GuiHolder getHolder(@NonNull Player player) {
        return this.viewers.get(player);
    }

    protected abstract void init(@NonNull Player player, @NonNull GuiContents contents);
}

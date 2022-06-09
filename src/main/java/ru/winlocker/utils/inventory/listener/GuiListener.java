package ru.winlocker.utils.inventory.listener;

import com.google.common.collect.*;
import lombok.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.server.*;
import org.bukkit.plugin.*;
import ru.winlocker.utils.inventory.*;
import ru.winlocker.utils.inventory.holder.*;
import ru.winlocker.utils.inventory.item.*;

import java.util.*;

@RequiredArgsConstructor
public class GuiListener implements Listener {

    private final @NonNull Plugin plugin;
    private final Set<GuiInventory> viewers = Sets.newConcurrentHashSet();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        if(e.getInventory().getHolder() instanceof GuiHolder) {
            Player player = (Player) e.getWhoClicked();

            GuiHolder holder = (GuiHolder) e.getInventory().getHolder();

            GuiContents contents = holder.getContents();
            GuiItem item = contents.getInventoryItem(e.getCurrentItem());

            if(item != null) {
                if(item.getSound() != null) {
                    player.playSound(player.getLocation(), item.getSound(), 1f, 1f);
                }

                if(item.isCloseable()) {
                    player.closeInventory();
                } else {
                    if(item.getAction() != null) {
                        try {
                            item.getAction().accept(e);
                        } catch (Exception ex) {
                            player.closeInventory();
                            player.sendMessage(ChatColor.RED + "Произошла ошибка, обратитесь к администратору.");

                            ex.printStackTrace();
                        }
                    }
                }
            }

            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOpen(InventoryOpenEvent e) {
        if(e.getInventory().getHolder() instanceof GuiHolder) {
            GuiHolder holder = (GuiHolder) e.getInventory().getHolder();
            GuiContents contents = holder.getContents();

            if(contents.getActionOpen() != null) {
                contents.getActionOpen().accept(e);
            }

            if(!e.isCancelled()) {
                GuiInventory inventory = holder.getContents().getInventory();

                inventory.getViewers().put((Player) e.getPlayer(), holder);
                this.viewers.add(holder.getContents().getInventory());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent e) {
        if(e.getInventory().getHolder() instanceof GuiHolder) {
            Player player = (Player) e.getPlayer();

            GuiHolder holder = (GuiHolder) e.getInventory().getHolder();
            GuiContents contents = holder.getContents();

            if(contents.getActionClose() != null) {
                contents.getActionClose().accept(e);
            }

            GuiInventory inventory = holder.getContents().getInventory();

            inventory.getViewers().remove((Player) e.getPlayer());

            if(inventory.getViewers().isEmpty()) {
                this.viewers.remove(inventory);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent e) {
        if(e.getPlugin().equals(this.plugin)) {
            this.viewers.forEach(inventory -> inventory.getViewers().keySet().forEach(Player::closeInventory));
        }
    }
}

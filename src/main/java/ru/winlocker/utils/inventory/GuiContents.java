package ru.winlocker.utils.inventory;

import de.tr7zw.changeme.nbtapi.*;
import lombok.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import ru.winlocker.utils.inventory.aggregates.*;
import ru.winlocker.utils.inventory.item.*;

import java.util.*;
import java.util.function.*;

@Getter
@RequiredArgsConstructor
public class GuiContents {

    private final @NonNull GuiInventory inventory;
    private final Object[] objects;

    private @Setter Supplier<String> title;
    private @Setter int rows;

    private final Map<Integer, GuiItem> items = new HashMap<>();
    private final Map<String, Supplier<String>> placeholders = new HashMap<>();

    private @Setter Consumer<InventoryClickEvent> actionClick;
    private @Setter Consumer<InventoryOpenEvent> actionOpen;
    private @Setter Consumer<InventoryCloseEvent> actionClose;

    private @Setter @NonNull Set<Aggregate> aggregates = new HashSet<>();

    public void setItem(int x, int y, @NonNull GuiItem item) {
        setItem(GuiUtil.parseSlot(x, y), item);
    }

    public void setItem(int slot, @NonNull GuiItem item) {
        this.items.put(slot, item);
    }

    public GuiItem getInventoryItem(ItemStack itemStack) {
        if(itemStack != null && itemStack.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(itemStack);

            if(nbtItem.hasKey("inventory-item")) {
                UUID uniqueId = UUID.fromString(nbtItem.getString("inventory-item"));

                return this.items.values().stream().filter(item -> item.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
            }
        }
        return null;
    }

    public void registerPlaceholder(@NonNull String placeholder, @NonNull Supplier<String> apply) {
        this.placeholders.put(placeholder, apply);
    }

    public void fillBorders(@NonNull GuiItem item) {
        fillBorders(0, 0, 9, rows, item);
    }

    public void fillBorders(int fromSlot, int toSlot, @NonNull GuiItem item) {
        fillBorders(fromSlot % 9, fromSlot / 9, toSlot % 9, toSlot / 9, item);
    }

    public void fillBorders(int fromX, int fromY, int toX, int toY, @NonNull GuiItem item) {
        for (int y = fromY; y <= toY; y++) {
            for (int x = fromX; x <= toX; x++) {
                if(x == fromX || x == toX || y == fromY || y == toY) {
                    setItem(x, y, item);
                }
            }
        }
    }

    public Set<Aggregate> getAggregates() {
        return new HashSet<>(this.aggregates);
    }

    public boolean addAggregate(@NonNull Aggregate aggregate) {
        return this.aggregates.add(aggregate);
    }

    public <V extends Aggregate> V getAggregate(Class<V> clazz) {
        return getOrAddAggregate(clazz, null);
    }

    @SuppressWarnings("unchecked")
    public <V extends Aggregate> V getOrAddAggregate(Class<V> clazz, V def) {
        return (V) this.getAggregates().stream()
                .filter(aggregate -> clazz.isAssignableFrom(aggregate.getClass()))
                .findFirst()
                .orElseGet(() -> {
                    if(def != null) {
                        this.aggregates.add(def);
                    }
                    return def;
                });
    }

    @SuppressWarnings("unchecked")
    public <V> V readObject(int index) {
        if(index >= this.objects.length)
            throw new ArrayIndexOutOfBoundsException("Value of index " + index + " not found");

        Object object = this.objects[index];
        return (V) object;
    }
}

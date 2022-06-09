package ru.winlocker.utils.inventory;

import de.tr7zw.changeme.nbtapi.*;
import lombok.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import ru.winlocker.utils.inventory.aggregates.*;
import ru.winlocker.utils.inventory.holder.*;
import ru.winlocker.utils.inventory.item.*;

import java.util.*;
import java.util.function.*;

@Getter
@RequiredArgsConstructor
public class GuiContents {

    private final @NonNull GuiInventory inventory;
    private final Object[] objects;

    private @Setter String title;
    private @Setter int rows;

    private final Map<Integer, GuiItem> items = new HashMap<>();
    private final Map<String, Supplier<String>> placeholders = new HashMap<>();

    private @Setter Consumer<InventoryClickEvent> actionClick;
    private @Setter Consumer<InventoryOpenEvent> actionOpen;
    private @Setter Consumer<InventoryCloseEvent> actionClose;

    private @Setter Consumer<GuiHolder> actionHolder;

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

    public boolean addAggregate(@NonNull Aggregate aggregate) {
        return this.aggregates.add(aggregate);
    }

    @SuppressWarnings("unchecked")
    public <V extends Aggregate> V getAggregate(@NonNull Class<V> clazz) {
        return (V) this.aggregates.stream()
                .filter(aggregate -> aggregate.getClass().isAssignableFrom(clazz))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <V extends Aggregate> V getOrAddAggregate(@NonNull V value) {
        return (V) this.getAggregates().stream()
                .filter(aggregate -> aggregate.getClass().isAssignableFrom(value.getClass()))
                .findFirst()
                .orElseGet(() -> {
                    this.aggregates.add(value);

                    return value;
                });
    }

    @SuppressWarnings("unchecked")
    public <V> V readObject(int index) {
        if(!isReadableObject(index))
            throw new ArrayIndexOutOfBoundsException("Value of index " + index + " not found");

        Object object = this.objects[index];
        return (V) object;
    }

    public boolean isReadableObject(int index) {
        return !(index >= this.objects.length);
    }
}

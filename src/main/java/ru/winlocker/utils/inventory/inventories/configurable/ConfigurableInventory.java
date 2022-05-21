package ru.winlocker.utils.inventory.inventories.configurable;

import com.cryptomorin.xseries.*;
import lombok.*;
import lombok.experimental.*;
import org.bukkit.*;
import org.bukkit.configuration.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import ru.winlocker.utils.*;
import ru.winlocker.utils.inventory.*;
import ru.winlocker.utils.inventory.item.*;

import java.util.*;
import java.util.function.*;

@Getter
public abstract class ConfigurableInventory extends GuiInventory {

    protected final @NonNull ConfigurationSection configuration;

    private ConfigurableItem windowItem;
    private final List<ConfigurableItem> items = new ArrayList<>();

    public ConfigurableInventory(@NonNull ConfigurationSection configuration) {
        this.configuration = configuration;

        if(configuration.isConfigurationSection("window")) {
            val section = configuration.getConfigurationSection("window");

            int slot = GuiUtil.parseSlot(section.getString("slot")).getSlot();
            int toSlot = GuiUtil.parseSlot(section.getString("to-slot")).getSlot();

            ItemStack itemStack = XItemStack.deserialize(section.getConfigurationSection("item"), Utils::color);
            Sound sound = null;

            if(section.getString("sound") != null) {
                sound = XSound.matchXSound(section.getString("sound")).map(XSound::parseSound).orElse(null);
            }

            windowItem = new ConfigurableItem(itemStack, null, null, Arrays.asList(slot, toSlot), sound);
        }

        if (configuration.isConfigurationSection("items")) {

            configuration.getConfigurationSection("items").getKeys(false).forEach(key -> {
                val section = configuration.getConfigurationSection("items." + key);

                ItemStack itemStack = XItemStack.deserialize(section.getConfigurationSection("item"), Utils::color);

                String name = section.getString("type");
                Sound sound = null;

                if(section.getString("sound") != null) {
                    sound = XSound.matchXSound(section.getString("sound")).map(XSound::parseSound).orElse(null);
                }

                List<Integer> slots = new ArrayList<>();

                if(section.isList("slot")) {
                    section.getStringList("slot").forEach(str -> slots.add(GuiUtil.parseSlot(str).getSlot()));

                } else if(section.isString("slot")) {
                    slots.add(GuiUtil.parseSlot(section.getString("slot")).getSlot());
                }

                this.items.add(new ConfigurableItem(itemStack, name, null, slots, sound));
            });
        }
    }

    @Override
    protected void init(@NonNull Player player, @NonNull GuiContents contents) {
        contents.setTitle(configuration.getString("title"));
        contents.setRows(configuration.getInt("rows"));

        if(windowItem != null) {
            GuiItem item = new GuiItem(windowItem.getItemStack());
            item.setSound(windowItem.getSound());

            contents.fillBorders(item);
        }

        this.items.forEach(configurableItem -> {
            GuiItem item = new GuiItem(configurableItem.getItemStack(), configurableItem.getAction());
            item.setSound(configurableItem.getSound());

            if(initItem(player, contents, configurableItem, item)) {
                configurableItem.getSlots().forEach(slot -> contents.setItem(slot, item));
            }
        });
    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(makeFinal = true)
    public static class ConfigurableItem {

        private ItemStack itemStack;
        private String name;
        private Consumer<InventoryClickEvent> action;
        private List<Integer> slots;
        private Sound sound;

        public String getName() {
            return name != null ? name : "";
        }
    }

    protected abstract boolean initItem(@NonNull Player player, @NonNull GuiContents contents, @NonNull ConfigurableItem configurableItem, @NonNull GuiItem item);
}

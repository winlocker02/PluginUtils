package ru.winlocker.utils.config.conversion.inventory;

import lombok.*;
import org.bukkit.configuration.*;
import ru.winlocker.utils.config.conversion.*;
import ru.winlocker.utils.config.data.*;
import ru.winlocker.utils.inventory.inventories.*;

import java.lang.reflect.*;

public class ConfigurableInventoryConverter {

    public static TypeConverter<? extends ConfigurableInventory> create(@NonNull Class<?> clazz) {
        return new TypeConverter<ConfigurableInventory>() {
            @Override
            public ConfigurableInventory loadFrom(DataHolder section, String path, ConfigurableInventory currentValue) {
                if(section instanceof ConfigurationSectionDataHolder) {
                    val dataHolder = (ConfigurationSectionDataHolder) section;
                    val configuration = dataHolder.getSection();

                    if(configuration.isConfigurationSection(path)) {
                        try {
                            Constructor<?> constructor = clazz.getConstructor(ConfigurationSection.class);

                            return (ConfigurableInventory) constructor.newInstance(configuration.getConfigurationSection(path));
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            public void saveTo(ConfigurableInventory configurableInventory, DataHolder section, String path) {

            }
        };
    }
}

package ru.winlocker.utils.converters;

import com.cryptomorin.xseries.*;
import org.bukkit.inventory.*;
import ru.winlocker.utils.config.conversion.*;
import ru.winlocker.utils.config.data.*;

public class ItemStackConverter implements TypeConverter<ItemStack> {

    @Override
    public ItemStack loadFrom(DataHolder dataHolder, String path, ItemStack currentValue) {
        if(dataHolder instanceof ConfigurationSectionDataHolder) {
            ConfigurationSectionDataHolder configuration = (ConfigurationSectionDataHolder) dataHolder;

            return XItemStack.deserialize(configuration.getSection().getConfigurationSection(path));
        }
        return null;
    }

    @Override
    public void saveTo(ItemStack itemStack, DataHolder section, String path) {}
}
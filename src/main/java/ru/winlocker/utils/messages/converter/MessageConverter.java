package ru.winlocker.utils.messages.converter;

import lombok.*;
import ru.winlocker.utils.config.conversion.*;
import ru.winlocker.utils.config.data.*;
import ru.winlocker.utils.messages.*;

public class MessageConverter implements TypeConverter<Messages> {

    public static MessageConverter create() {
        return new MessageConverter();
    }

    @Override
    public Messages loadFrom(DataHolder section, String path, Messages currentValue) {
        if(section instanceof ConfigurationSectionDataHolder) {
            val configuration = ((ConfigurationSectionDataHolder) section).getSection().getConfigurationSection(path);

            return Messages.create(configuration);
        }
        return null;
    }

    @Override
    public void saveTo(Messages messages, DataHolder section, String path) {}
}

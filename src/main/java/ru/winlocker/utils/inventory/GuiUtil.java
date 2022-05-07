package ru.winlocker.utils.inventory;

import lombok.*;
import org.bukkit.configuration.*;

public class GuiUtil {

    public static GuiInventory fromConfiguration(@NonNull ConfigurationSection configuration) {
        return null;
    }

    public static GuiSlot parseSlot(@NonNull String string) {
        String[] args = string.split(",");

        try {
            if(args.length > 1) {
                int x = Integer.parseInt(args[0].trim());
                int y = Integer.parseInt(args[1].trim());

                return new GuiSlot(x, y);
            } else {
                int slot = Integer.parseInt(args[0].trim());

                return new GuiSlot(slot);
            }

        } catch (NumberFormatException e) {
            return GuiSlot.empty();
        }
    }

    public static int parseSlot(int x, int y) {
        return Math.max(y - 1, 0) * 9 + Math.max(x - 1, 0);
    }

}

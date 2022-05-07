package ru.winlocker.utils.inventory;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GuiSlot {

    public static GuiSlot of(int x, int y) {
        return new GuiSlot(x, y);
    }

    public static GuiSlot of(int slot) {
        return new GuiSlot(slot);
    }

    public static GuiSlot empty() {
        return new GuiSlot(0);
    }

    private int x, y;

    public GuiSlot(int slot) {
        if(slot > 0) {
            this.x = slot % 9;
            this.y = (slot / 9) + 1;
        } else {
            this.x = 0;
            this.y = 0;
        }
    }

    public int getSlot() {
        return GuiUtil.parseSlot(this.x, this.y);
    }
}

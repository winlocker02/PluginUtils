package ru.winlocker.utils.inventory.aggregates.impl;

import lombok.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import ru.winlocker.utils.*;
import ru.winlocker.utils.inventory.*;
import ru.winlocker.utils.inventory.aggregates.*;
import ru.winlocker.utils.inventory.holder.*;
import ru.winlocker.utils.inventory.item.*;

import java.util.*;

@Getter
public class Paginated implements Aggregate {

    public static Paginated ofPage(int fromX, int fromY, int toX, int toY) {
        return new Paginated(fromX, fromY, toX, toY);
    }

    private final int fromX, fromY, toX, toY, pageCountItems;

    private int page;
    private @Setter @NonNull List<GuiItem> items = new ArrayList<>();

    private final PaginatedRender render = new PaginatedRender();

    public Paginated(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.pageCountItems = ((toX - fromX + 1) * (toY - fromY + 1));
    }

    @Override
    public void init(@NonNull Player player, @NonNull GuiContents contents, @NonNull GuiInventory inventory) {

        if(this.page < 0 || this.page > getPages()) {
            this.page = 0;
        }

        contents.setTitle(contents.getTitle()
                .replace("{page}", Utils.numberFormat(this.page + 1))
                .replace("{max-page}", Utils.numberFormat(this.getPages() + 1)));

        PageIterator iterator = new PageIterator(contents, this.fromX, this.fromY, this.toX, this.toY);

        for (GuiItem pageItem : getPageItems()) {
            iterator.next().placeItem(pageItem);

            if(iterator.isFinished())
                break;
        }

        contents.setActionHolder(holder -> {

            render.getItems().forEach((slot, item) -> {
                GuiItem guiItem = new GuiItem(item.getItemStack());

                if(item.getType() == PaginatedItemType.NEXT) {

                    if(item.isRemoveIfCompleted() && (this.getPages() <= this.page))
                        return;

                    guiItem.setAction(e -> {
                        if(this.getPages() > this.page) {
                            this.page++;

                            if(item.getSound() != null) {
                                player.playSound(player.getLocation(), item.getSound(), 1f, 1f);
                            }

                            holder.updateInventory();
                        } else {
                            if(item.getSoundCompleted() != null) {
                                player.playSound(player.getLocation(), item.getSoundCompleted(), 1f, 1f);
                            }
                        }
                    });
                } else {

                    if(item.isRemoveIfCompleted() && (this.page <= 0))
                        return;

                    guiItem.setAction(e -> {
                        if(this.page > 0) {
                            this.page--;

                            if(item.getSound() != null) {
                                player.playSound(player.getLocation(), item.getSound(), 1f, 1f);
                            }

                            holder.updateInventory();
                        } else {
                            if(item.getSoundCompleted() != null) {
                                player.playSound(player.getLocation(), item.getSoundCompleted(), 1f, 1f);
                            }
                        }
                    });
                }

                contents.setItem(slot, guiItem);
            });
        });
    }

    public int getPages() {
        return Math.max(1, this.items.size()) / Math.max(1, this.pageCountItems);
    }

    public List<GuiItem> getPageItems() {
        int fromIndex = this.page * this.pageCountItems;
        int toIndex = Math.min((this.page + 1) * this.pageCountItems, this.items.size());

        return this.items.subList(fromIndex, toIndex);
    }

    public void setPage(int page) {
        if(page < 0)
            throw new IllegalArgumentException("page number cannot be below zero");
        if(getPages() < page)
            throw new IllegalArgumentException("page number cannot be higher than the maximum number of pages");
        this.page = page;
    }

    @Getter
    public static class PageIterator {

        private final @NonNull GuiContents contents;
        private final int fromX, fromY, toX, toY;

        private int currentX, currentY;
        private boolean started, finished;

        public PageIterator(@NonNull GuiContents contents, int fromX, int fromY, int toX, int toY) {
            this.contents = contents;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
            this.currentX = fromX;
            this.currentY = fromY;
        }

        public PageIterator next() {
            if(!this.finished) {
                if(!this.started) {
                    this.started = true;
                } else {
                    this.currentX = ++this.currentX % (this.toX + 1);

                    if(this.currentX == 0) {
                        this.currentX = this.fromX;
                        this.currentY++;
                    }

                    if(this.currentX == this.toX && this.currentY == this.toY) {
                        this.finished = true;
                    }
                }
            }
            return this;
        }

        public PageIterator previous() {
            if (this.started) {
                this.currentX = --this.currentX % (this.toX + 1);

                if (this.finished) {
                    this.finished = false;
                }

                if (this.currentX == 0) {
                    this.currentX = this.toX;
                    this.currentY--;
                }
            }
            return this;
        }

        public void placeItem(@NonNull GuiItem item) {
            this.contents.setItem(this.currentX, this.currentY, item);
        }

        public int getCurrentSlot() {
            return GuiUtil.parseSlot(currentX, currentY);
        }
    }


    @Getter
    public static class PaginatedRender {

        private final Map<Integer, PaginatedItem> items = new HashMap<>();
        private boolean removeIfCompleted;

        public void setItem(int x, int y, @NonNull PaginatedItem item) {
            this.items.put(GuiUtil.parseSlot(x, y), item);
        }

        public void setItem(int slot, @NonNull PaginatedItem item) {
            this.items.put(slot, item);
        }

        public PaginatedItem nextItem(int x, int y, @NonNull ItemStack itemStack) {
            return nextItem(GuiUtil.parseSlot(x, y), itemStack);
        }

        public PaginatedItem nextItem(int slot, @NonNull ItemStack itemStack) {
            PaginatedItem paginatedItem = new PaginatedItem(itemStack, PaginatedItemType.NEXT);
            this.items.put(slot, paginatedItem);

            return paginatedItem;
        }

        public PaginatedItem previousItem(int x, int y, @NonNull ItemStack itemStack) {
            return previousItem(GuiUtil.parseSlot(x, y), itemStack);
        }

        public PaginatedItem previousItem(int slot, @NonNull ItemStack itemStack) {
            PaginatedItem paginatedItem = new PaginatedItem(itemStack, PaginatedItemType.PREVIOUS);
            this.items.put(slot, paginatedItem);

            return paginatedItem;
        }
    }

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class PaginatedItem {

        private final @NonNull ItemStack itemStack;
        private final @NonNull PaginatedItemType type;

        private boolean removeIfCompleted;
        private Sound sound, soundCompleted;

        public PaginatedItem removeIfCompleted(boolean removeIfCompleted) {
            this.removeIfCompleted = removeIfCompleted;
            return this;
        }

        public PaginatedItem sound(Sound sound) {
            this.sound = sound;
            return this;
        }

        public PaginatedItem soundCompleted(Sound soundCompleted) {
            this.soundCompleted = soundCompleted;
            return this;
        }
    }

    public enum PaginatedItemType {
        NEXT, PREVIOUS
    }
}

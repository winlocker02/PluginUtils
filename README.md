# API for using bukkit plugins

### Maven dependency
```xml

    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.winlocker02</groupId>
            <artifactId>PluginUtils</artifactId>
            <version>LATEST</version>
        </dependency>
    </dependencies>

```

# Inventories

### Simple Inventory
```java

GuiInventory inventory = new GuiInventory() {
    
    @Override
    protected void init(@NonNull Player player, @NonNull GuiContents contents) {
        
        contents.setTitle("Player: " + player.getName());
        contents.setRows(6);

        // Fill borders
        contents.fillBorders(new GuiItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15))); 

        // Place item
        GuiItem item = new GuiItem(new ItemStack(Material.APPLE), e -> player.sendMessage("test"));
        contents.setItem(1, 1, item);
    
        // todo
    }
};

inventory.showInventory(player); // Open inventory

```

### Aggregates

```java

GuiInventory inventory = new GuiInventory() {
    
    @Override
    protected void init(@NonNull Player player, @NonNull GuiContents contents) {
        
        contents.setTitle("Player: " + player.getName());
        contents.setRows(6);

        // Fill borders
        contents.fillBorders(new GuiItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15))); 

        // Place item
        GuiItem item = new GuiItem(new ItemStack(Material.APPLE), e -> player.sendMessage("test"));
        contents.setItem(1, 1, item);
        
        
    
        // todo
    }
};

inventory.showInventory(player); // Open inventory

```

### Configurable

```yaml

inventory:
    title: 'Inventory'
    rows: 6
    window:  # Fill borders
        slot: 0, 0
        to-slot: 9, 6
        item:
            material: BLACK_STAINED_GLASS_PANE
            damage: 15
            name: ' '
  items:
      item1:
          item:
              material: APPLE
              name: '&aTest'
          slot: 1, 1
          type: TEST

```

```java

        ConfigurableInventory inventory = new ConfigurableInventory(configuration.getConfigurationSection("inventory")) {
            @Override
            protected boolean initItem(@NonNull Player player, @NonNull GuiContents contents, @NonNull ConfigurableItem configurableItem, @NonNull GuiItem item) {
                // On initialize item from inventory

                if ("TEST".equals(configurableItem.getName())) {
                    item.setAction(e -> player.sendMessage("test"));
                }

                return true;
            }
        };

        inventory.showInventory(player); // Open Inventory

```
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
        gamemode_creative:
            item:
                material: FEATHER
                name: '&6Enable/disable creative mode'
            slot: 3, 2
            type: GAME_MODE
        fly_mode:
            item:
                material: FEATHER
                name: '&aEnable/disable fly mode'
            slot: 2, 2
            type: FLY

```

```java

        ConfigurableInventory inventory = new ConfigurableInventory(configuration.getConfigurationSection("inventory")) {
            @Override
            protected boolean initItem(@NonNull Player player, @NonNull GuiContents contents, @NonNull ConfigurableItem configurableItem, @NonNull GuiItem item) {
                // Initialize item from configuration
                
                switch(configurableItem.getName()) {
                    case "GAME_MODE" : // The name of the item is taken from the configuration
                        
                        if(!player.hasPermission("example.gamemode")) 
                            return false; // If returned false, the item will not be placed in the inventory
                        
                        item.setAction(e -> {
                            if(player.getGameMode() != GameMode.CREATIVE) {
                                player.setGameMode(GameMode.CREATIVE);
                                player.sendMessage(ChatColor.GREEN + "Creative mode is enabled");
                            } else {
                                player.setGameMode(GameMode.SURVIVAL);
                                player.sendMessage(ChatColor.RED + "Creative mode is disabled");
                            }
                        });
                        
                        break;
                    case "FLY" :

                        if(!player.hasPermission("example.fly"))
                            return false; // If returned false, the item will not be placed in the inventory
                        
                        item.setAction(e -> {
                            
                            if(player.getAllowFlight()) {
                                player.setAllowFlight(false);
                                player.setFlying(false);
                                player.sendMessage(ChatColor.RED + "Fly mode is disabled");
                            } else {
                                player.setAllowFlight(true);
                                player.sendMessage(ChatColor.RED + "Fly mode is disabled");
                            }                  
                        });
                        
                        break;
                }
                
                return true;
            }
        };

        inventory.showInventory(player); // Open Inventory

```
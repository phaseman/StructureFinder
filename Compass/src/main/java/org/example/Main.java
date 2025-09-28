package org.example;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.StructureType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {
    private static final Set<Action> RIGHT_CLICK_ACTIONS = EnumSet.of(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);

    public void onEnable() {
        Bukkit.addRecipe((Recipe)(new ShapedRecipe(new NamespacedKey((Plugin)this, "bastion_compass"),
                modifyData(Material.COMPASS, meta -> {
                    CompassMeta compassMeta = (CompassMeta)meta;
                    compassMeta.setCustomModelData(Integer.valueOf(1));
                    compassMeta.setDisplayName(ChatColor.RESET + "Bastion Compass");
                }))).shape(new String[] { " S ", "SRS", " S " }).setIngredient('S', Material.NETHERITE_SCRAP)
                .setIngredient('R', Material.REDSTONE));
        Bukkit.getPluginManager().registerEvents(this, (Plugin)this);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent e) {
        if (RIGHT_CLICK_ACTIONS.contains(e.getAction())) {
            ItemStack item = e.getItem();
            if (item != null && item.getType() == Material.COMPASS) {
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                if (meta.hasCustomModelData()) {
                    Location location = e.getPlayer().getLocation();
                    assert location.getWorld() != null;
                    Location bastion = location.getWorld().locateNearestStructure(location, StructureType.END_CITY, 10, true);
                    e.getPlayer().sendMessage((bastion != null) ? (ChatColor.GREEN + "Nearest Bastion Tracked") : (ChatColor.RED + "Could not find Bastion"));
                    CompassMeta compassMeta = (CompassMeta)meta;
                    compassMeta.setLodestone(bastion);
                    compassMeta.setLodestoneTracked(false);
                    item.setItemMeta((ItemMeta)compassMeta);
                }
            }
        }
    }

    private static ItemStack modifyData(Material material, Consumer<ItemMeta> consumer) {
        ItemStack result = new ItemStack(material);
        ItemMeta meta = result.getItemMeta();
        consumer.accept(meta);
        result.setItemMeta(meta);
        return result;
    }
}

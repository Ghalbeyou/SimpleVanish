package org.ghalbeyou.moamelevanish;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public final class MoameleVanish extends JavaPlugin implements Listener {

    private boolean autoVanish;
    private boolean canSendMessage;
    private List<String> messages;

    @Override
    public void onEnable() {
        // Load configuration
        loadConfig();

        // Plugin startup logic
        getLogger().info("MoameleVanish has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("MoameleVanish has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("vanish")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "این دستور فقط برای بازیکنان قابل استفاده است.");
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("moamelevanish.vanish")) {
                String message = messages.get(2); // Index of the vanish_chat_blocked message
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                return true;
            }

            // تغییر وضعیت ونیش با کلیک روی دستور
            if (autoVanish) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                String message = messages.get(0); // Index of the vanish_chat_blocked message
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            } else {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                String message = messages.get(1); // Index of the vanish_chat_blocked message
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
            autoVanish = !autoVanish;

            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("moamelevanish.join") && autoVanish) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (autoVanish && !canSendMessage && player.hasPermission("moamelevanish.vanish")) {
            event.setCancelled(true);
            String message = messages.get(3); // Index of the vanish_chat_blocked message
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        autoVanish = getConfig().getBoolean("auto_vanish_on_join", true);
        canSendMessage = getConfig().getBoolean("can_send_messages", false);
        messages = getConfig().getStringList("messages");
    }
}

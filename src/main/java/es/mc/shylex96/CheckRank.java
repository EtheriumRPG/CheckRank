package es.mc.shylex96;

import es.mc.shylex96.CheckRankListener.CheckRankListener;
import es.mc.shylex96.TeleportRestrictionListener.TeleportRestrictionListener;
import nl.svenar.powerranks.bukkit.PowerRanks;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CheckRank extends JavaPlugin {
    public static String prefix = "§a[CheckRank]";
    private String version = getDescription().getVersion();
    private PowerRanks powerRanks;

    @Override
    public void onEnable() {
        // Verificar si PowerRanks está presente y disponible
        powerRanks = (PowerRanks) Bukkit.getPluginManager().getPlugin("PowerRanks");

        if (powerRanks == null) {
            Bukkit.getConsoleSender().sendMessage(prefix + " §cError: PowerRanks no está instalado o no se pudo encontrar.");
            // Desactivar plugin si PowerRanks no está presente
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getConsoleSender().sendMessage(prefix +
                " §7Plugin creado por §eShylex §7ha cargado correctamente en la versión: §c" + version);

        // Registrar los listeners de eventos solo una vez
        getServer().getPluginManager().registerEvents(new CheckRankListener(powerRanks), this);
        getServer().getPluginManager().registerEvents(new CheckRankListener(powerRanks), this);
        getServer().getPluginManager().registerEvents(new TeleportRestrictionListener(powerRanks), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(prefix + " §cPlugin desactivado.");
    }
}

package es.mc.shylex96.TeleportRestrictionListener;

import nl.svenar.powerranks.api.PlayersAPI;
import nl.svenar.powerranks.bukkit.PowerRanks;
import nl.svenar.powerranks.common.structure.PRPlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class TeleportRestrictionListener implements Listener {

    private final JavaPlugin plugin; // ✅ referencia al plugin principal
    private final PowerRanks powerRanks;
    private final PlayersAPI playersAPI;
    private final List<String> rankList;
    private final List<RestrictedZone> restrictedZones = new ArrayList<>();

    public TeleportRestrictionListener(JavaPlugin plugin, PowerRanks powerRanks) {
        this.plugin = plugin;
        this.powerRanks = powerRanks;
        this.playersAPI = new PlayersAPI();

        // Lista ordenada de menor a mayor
        rankList = Arrays.asList(
                "Aldeano", "Aprendiz", "Explorador", "Cazador", "Caballero",
                "Veterano", "Comandante", "Paladín", "Campeón", "Protector",
                "Guardián", "Héroe", "Ascendido", "Semidiós", "Dios_Menor",
                "Dios_Mayor", "Dios_Supremo", "Titán", "Protogenos",
                "Primordial", "Eternum"
        );

        registerZones();
    }

    private void registerZones() {
        restrictedZones.add(new RestrictedZone(
                "Abismo Venenoso",
                new Location(Bukkit.getWorld("world"), 15073.5, -21.0, -830.5, 120, 12),
                "Comandante",
                1.5, 1.5, 1.5
        ));

        // Agregar más zonas...
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();

        if (to == null) return;

        for (RestrictedZone zone : restrictedZones) {
            if (zone.isInside(to)) {
                if (!hasRankOrHigher(player.getUniqueId(), zone.requiredRank)) {
                    event.setCancelled(true);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        player.sendMessage("§cNo tienes el rango necesario para acceder a §6" + zone.name + "§c. " +
                                "Se requiere el rango §6" + zone.requiredRank + "§c o superior.");
                    }, 1L);

                    return;
                }
            }
        }
    }

    private boolean hasRankOrHigher(UUID playerUUID, String requiredRank) {
        Set<PRPlayerRank> playerRanks = playersAPI.getRanks(playerUUID);
        if (playerRanks.isEmpty()) return false;

        String playerRank = playerRanks.iterator().next().getName();
        int playerIndex = rankList.indexOf(playerRank);
        int requiredIndex = rankList.indexOf(requiredRank);

        return playerIndex >= requiredIndex;
    }
}

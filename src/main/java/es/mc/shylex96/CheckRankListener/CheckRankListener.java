package es.mc.shylex96.CheckRankListener;

import nl.svenar.powerranks.api.PlayersAPI;
import nl.svenar.powerranks.bukkit.PowerRanks;
import nl.svenar.powerranks.common.structure.PRPlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CheckRankListener implements Listener {

    private final PowerRanks powerRanks;
    private final PlayersAPI playersAPI;

    public CheckRankListener(PowerRanks powerRanks) {
        this.powerRanks = powerRanks;
        this.playersAPI = new PlayersAPI();
    }

    final String tier_2_combat_zone_name = "Abismo Venenoso";

    // Lista de rangos ordenados de menor a mayor
    private final List<String> rankList = Arrays.asList(
            "Aldeano", "Aprendiz", "Explorador", "Cazador", "Caballero",
            "Veterano", "Comandante", "Paladín", "Campeón", "Protector",
            "Guardián", "Héroe", "Ascendido", "Semidiós", "Dios_Menor",
            "Dios_Mayor", "Dios_Supremo", "Titán", "Protogenos",
            "Primordial", "Eternum"
    );

    private final double toleranceX = 1;
    private final double toleranceY = 0;
    private final double toleranceZ = 0.5;

    // Almacenar las coordenadas anteriores
    private double lastX = 0;
    private double lastY = 0;
    private double lastZ = 0;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Obtener el rango del jugador
        Set<PRPlayerRank> playerRanks = playersAPI.getRanks(playerUUID);

        if (playerRanks.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage("El jugador no tiene rangos asignados.");
            return;
        }

        PRPlayerRank playerRankObject = playerRanks.iterator().next();
        String playerRank = playerRankObject.getName();

        // Mapeo de rangos específicos con su número correspondiente
        String rankCommand = getRankCommand(playerRank, player);

        if (rankCommand != null) {
            // Ejecutar el comando con un retraso de 1 tick
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), rankCommand);
                    // Bukkit.getConsoleSender().sendMessage("Comando ejecutado: " + rankCommand);
                }
            }.runTaskLater(powerRanks, 100L); //> 1 tick (20ms) - 1000 tickets (1s)

        } else {
            // Bukkit.getConsoleSender().sendMessage("El jugador " + player.getName() + " no tiene un rango que requiera el comando.");
        }
    }


    private String getRankCommand(String playerRank, Player player) {
        // Lista de rangos específicos para permiso según logros
        return switch (playerRank) {
            case "Héroe" -> "ca grantimpossible " + player.getName() + " 1 server_quest.rank2";
            case "Semidiós" -> "ca grantimpossible " + player.getName() + " 1 server_quest.rank3";
            case "Dios_Mayor" -> "ca grantimpossible " + player.getName() + " 1 server_quest.rank4";
            case "Titán" -> "ca grantimpossible " + player.getName() + " 1 server_quest.rank5";
            case "Primordial" -> "ca grantimpossible " + player.getName() + " 1 server_quest.rank6";
            case "Eternum" -> "ca grantimpossible " + player.getName() + " 1 server_quest.rank7";
            default -> null;
        };
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();

        // Obtener las coordenadas del jugador
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        // Depuración: imprimir las coordenadas del jugador
        // Bukkit.getConsoleSender().sendMessage("Coordenadas del jugador: X=" + x + ", Y=" + y + ", Z=" + z);

        // Solo verificar si las coordenadas del jugador han cambiado significativamente
        if (hasMovedSignificantly(x, y, z)) {
            // Case para las coordenadas
            switch (getCoordinateCase(x, y, z)) {
                case "case1":
                    handleTeleportation(player, "Comandante", new Location(player.getWorld(), 5342.500, 170.000, -456.500), tier_2_combat_zone_name);
                    break;
                case "case2":
                    // handleTeleportation(player, "Comandante", new Location(player.getWorld(), 5342, 170, -457), tier_2_combat_zone_name);
                    break;
                // ...
                default:
                    break;
            }
        }
    }

    private boolean hasMovedSignificantly(double x, double y, double z) {
        // Verificar si el jugador se ha movido más allá de una tolerancia mínima (no movimiento de cámara únicamente)
        if (Math.abs(x - lastX) > toleranceX || Math.abs(y - lastY) > toleranceY || Math.abs(z - lastZ) > toleranceZ) {
            // Actualizar las coordenadas anteriores
            lastX = x;
            lastY = y;
            lastZ = z;
            return true;
        }
        return false;
    }

    private void handleTeleportation(Player player, String requiredRank, Location teleportLocation, String zoneName) {
        UUID playerUUID = player.getUniqueId();

        if (hasRankOrHigher(playerUUID, requiredRank)) {
            // Bukkit.getConsoleSender().sendMessage("Acceso concedido a " + zoneName);
            player.sendMessage("Has entrado en §6" + zoneName + ".");
            player.teleport(teleportLocation);
        } else {
            player.sendMessage("§cNo tienes el §3rango §csuficiente para acceder a §6" + zoneName + "." +
                    "§r Necesitas un §3rango §rde §6" + requiredRank + "§r o superior.");
        }
    }

    private boolean isPlayerOnSpecificCoordinates(double x, double y, double z, double targetX, double targetY, double targetZ) {
        return (x >= targetX - toleranceX && x <= targetX + toleranceX &&
                y >= targetY - toleranceY && y <= targetY &&
                z >= targetZ - toleranceZ && z <= targetZ);
    }

    private String getCoordinateCase(double x, double y, double z) {
        if (isPlayerOnSpecificCoordinates(x, y, z, 5342.500, 164.00, -456.500)) {
            // Portal de Fiery Abyss a Venomous Abyss
            return "case1";
        } else if (isPlayerOnSpecificCoordinates(x, y, z, 0, 70, 0)) {
            // Portal de Venomous Abyss a ..
            return "case2";
        } // ...
        return "default";
    }

    private boolean hasRankOrHigher(UUID playerUUID, String requiredRank) {
        // Obtener el rango del jugador
        Set<PRPlayerRank> playerRanks = playersAPI.getRanks(playerUUID);

        if (playerRanks.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage("El jugador no tiene rangos asignados.");
            return false;
        }

        PRPlayerRank playerRankObject = playerRanks.iterator().next();
        String playerRank = playerRankObject.getName();

        // Bukkit.getConsoleSender().sendMessage("Rango del jugador: " + playerRank);

        // Obtener los índices de los rangos
        int playerRankIndex = rankList.indexOf(playerRank);
        int requiredRankIndex = rankList.indexOf(requiredRank);

        // Bukkit.getConsoleSender().sendMessage("playerRankIndex a evaluar: " + playerRankIndex);
        // Bukkit.getConsoleSender().sendMessage("requiredRankIndex a evaluar: " + requiredRankIndex);

        if (playerRankIndex == -1) {
            Bukkit.getConsoleSender().sendMessage("El rango del jugador no se encontró en la lista.");
            return false;
        }

        // Comprobar si el jugador tiene el rango requerido o superior
        // Bukkit.getConsoleSender().sendMessage("Result: " + (playerRankIndex >= requiredRankIndex));
        return playerRankIndex >= requiredRankIndex;

    }
}

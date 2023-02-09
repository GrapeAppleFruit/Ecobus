package busch.economy;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomEconomyAPI extends JavaPlugin implements Listener {
    private HashMap<UUID, Double> playerBalances = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }
        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();
        if (command.getName().equalsIgnoreCase("balance")) {
            double balance = playerBalances.getOrDefault(playerId, 0.0);
            player.sendMessage("Your balance is: " + balance);
            return true;
        } else if (command.getName().equalsIgnoreCase("pay")) {
            if (!sender.hasPermission("ecobus.pay")) {
                sender.sendMessage("You don't have permission to use this command!");
                return true;
            }
            if (args.length != 2) {
                sender.sendMessage("Usage: /pay [player] [amount]");
                return true;
            }

            String targetPlayer = args[0];
            double amount = 0;
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid amount: " + args[1]);
                return true;
            }

            if (sender.getName().equals(targetPlayer)) {
                sender.sendMessage("You cannot pay yourself.");
                return true;
            }

            UUID senderId = ((Player) sender).getUniqueId();
            double senderBalance = playerBalances.get(senderId);

            if (senderBalance < amount) {
                sender.sendMessage("You do not have enough money to pay that amount.");
                return true;
            }

            UUID targetId = Bukkit.getPlayer(targetPlayer).getUniqueId();
            double targetBalance = playerBalances.get(targetId);

            playerBalances.put(senderId, senderBalance - amount);
            playerBalances.put(targetId, targetBalance + amount);

            sender.sendMessage("You have paid " + targetPlayer + " " + amount + " coins.");
            Bukkit.getPlayer(targetPlayer).sendMessage("You have received " + amount + " coins from " + sender.getName());

            return true;
        } else if (command.getName().equalsIgnoreCase("remove")) {
            if (!player.hasPermission("ecobus.remove")) {
                player.sendMessage("You do not have permission to use this command.");
                return false;
            }
            if (args.length != 2) {
                player.sendMessage("Usage: /remove <player> <amount>");
                return false;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("Error: player not found");
                return false;
            }
            UUID targetId = target.getUniqueId();
            double removeAmount;
            try {
                removeAmount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Error: invalid number format");
                return false;
            }
            double balance = playerBalances.getOrDefault(targetId, 0.0);
            if (balance < removeAmount) {
                player.sendMessage("Error: target does not have enough funds");
                return false;
            }
            playerBalances.put(targetId, balance - removeAmount);
            player.sendMessage("Removed " + removeAmount + " from " + target.getName() + "'s account.");
            target.sendMessage("Your balance was decreased by " + removeAmount + " by " + player.getName());
            return true;
            } else if (command.getName().equalsIgnoreCase("give")) {
                if (!player.hasPermission("ecobus.give")) {
                    player.sendMessage("You do not have permission to use this command.");
                    return false;
                }
                if (args.length != 2) {
                    player.sendMessage("Usage: /give <player> <amount>");
                    return false;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage("Error: player not found");
                    return false;
                }
                UUID targetId = target.getUniqueId();
                double giveAmount;
                try {
                    giveAmount = Double.parseDouble(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Error: invalid number format");
                    return false;
                }
                double balance = playerBalances.getOrDefault(targetId, 0.0);
                playerBalances.put(targetId, balance + giveAmount);
                player.sendMessage("Gave " + giveAmount + " to " + target.getName() + "'s account.");
                target.sendMessage("Your balance was increased by " + giveAmount + " by " + player.getName());
                return true;
            }
        return false;
        }
    }







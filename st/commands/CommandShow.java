package facemywrath.st.commands;

import java.io.File;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import facemywrath.st.storage.ShowManager;

public class CommandShow implements CommandExecutor {

	private ShowManager manager;

	public CommandShow(ShowManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			if(args.length == 0) {
				sender.sendMessage("/show start - start a show for a player");
				sender.sendMessage("/show stop - stop a show for a player");
				sender.sendMessage("/show list - list the show names");
				return true;
			}
			if(args[0].equalsIgnoreCase("start")) {
				if(args.length == 1) {
					sender.sendMessage("Must specify a player");
					return true;
				}
				Player player = Bukkit.getPlayer(args[1]);
				if(player == null) {
					sender.sendMessage("The player '" + args[1] + "' is not online.");
					return true;
				}
				if(args.length == 2) {
					sender.sendMessage("Must specify a show name");
					return true;
				}
				String showName = args[2];
				if(manager.startPlayer(player, showName))
					sender.sendMessage("Show " + showName + " started for " + player.getName());
				else 
					sender.sendMessage("Show " + showName + " could not be found.");
				return true;
			}
			if(args[0].equalsIgnoreCase("stop")) {
				if(args.length == 1) {
					sender.sendMessage("Must specify a player");
					return true;
				}
				Player player = Bukkit.getPlayer(args[1]);
				if(player == null) {
					sender.sendMessage("The player '" + args[1] + "' is not online.");
					return true;
				}
				if(args.length == 2) {
					sender.sendMessage("Must specify a show name");
					return true;
				}
				String showName = args[2];
				if(manager.stopPlayer(player, showName))
					sender.sendMessage("Show " + showName + " stopped for " + player.getName());
				else 
					sender.sendMessage("Show " + showName + " could not be found.");
				return true;
			}
			if(args[0].equalsIgnoreCase("list")){
				if(manager.showSet().isEmpty()){
					sender.sendMessage("No shows exist");
					return true;
				}
				sender.sendMessage("Show names: " + manager.showSet().stream().collect(Collectors.joining(", ")));
			}
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage("/show start - start a show");
			sender.sendMessage("/show stop - stop a show");
			sender.sendMessage("/show list - list the show names");
			if(sender.isOp())
				sender.sendMessage("/show create - to create the yml for a show file. (OP ONLY)");
			return true;
		}
		Player player = (Player) sender;
		if(args[0].equalsIgnoreCase("start")) {
			if(args.length == 1) {
				sender.sendMessage("Must specify a show name");
				return true;
			}
			String showName = args[1];
			manager.startPlayer(player, showName);
			return true;
		}
		if(args[0].equalsIgnoreCase("stop")) {
			if(args.length == 1) {
				sender.sendMessage("Must specify a show name");
				return true;
			}
			String showName = args[1];
			manager.stopPlayer(player, showName);
			return true;
		}
		if(args[0].equalsIgnoreCase("create")) {
			if(args.length == 1) {
				sender.sendMessage("Must specify a show name");
				return true;
			}
			if(manager.showExists(args[1])) {
				player.sendMessage("That show already exists.");
				return true;
			}
			File folder = manager.getFolder();
			File file = new File(folder, args[1].toLowerCase() + ".yml");
			if(file.exists()) {
				player.sendMessage("That show already exists.");
				return true;
			}
			try{
				file.createNewFile();
			}catch(Exception e) {}
			player.sendMessage("Show file for " + args[1].toLowerCase() + " created.");
		}
		if(args[0].equalsIgnoreCase("list")){
			if(manager.showSet().isEmpty()){
				sender.sendMessage("No shows exist");
				return true;
			}
			sender.sendMessage("Show names: " + manager.showSet().stream().collect(Collectors.joining(", ")));
		}
		return true;
	}

}

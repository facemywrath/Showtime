package facemywrath.st.storage;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import facemywrath.st.main.Showtime;
import net.md_5.bungee.api.ChatColor;

public class ShowManager {
	
	private HashMap<String, Show> shows = new HashMap<>();
	private File folder = null;
	
	public ShowManager(Showtime main) {
		folder = main.getDataFolder();
		if(!folder.exists()) {
			folder.mkdir();
			return;
		}
		folder = new File(main.getDataFolder(), "shows");
		if(!folder.exists()) {
			folder.mkdir();
			return;
		}
		for(File file : folder.listFiles()) {
			if(!file.getName().endsWith(".yml")) continue;
			shows.put(file.getName().toLowerCase().substring(0, file.getName().indexOf(".y")), new Show(main, YamlConfiguration.loadConfiguration(file)));
		}
	}
	
	public File getFolder() {
		return folder;
	}
	
	public boolean showExists(String showName) {
		return shows.containsKey(showName.toLowerCase());
	}
	
	public Set<String> showSet() {
		return shows.keySet();
	}
	
	public boolean startPlayer(Player player, String showName) {
		showName = showName.toLowerCase();
		if(!shows.containsKey(showName)) {
			player.sendMessage("That show doesn't exist.");
			return false;
		}
		Show show = shows.get(showName);
		if(show.isRunning(player)) {
			player.sendMessage("That show is already running for you.");
			return true;
		}
		show.animate(player);
		player.sendMessage(ChatColor.GRAY + ">> " + ChatColor.BOLD + "Starting show: " + ChatColor.AQUA + showName);
		player.sendMessage("Show duration: " + show.getDuration());
		return true;
	}
	
	public boolean stopPlayer(Player player, String showName) {
		showName = showName.toLowerCase();
		if(!shows.containsKey(showName)) {
			player.sendMessage("That show doesn't exist.");
			return false;
		}
		Show show = shows.get(showName);
		if(!show.isRunning(player)) {
			player.sendMessage("That show isn't running for you.");
			return true;
		}
		show.stop(player);
		player.sendMessage(ChatColor.GRAY + ">> " + ChatColor.BOLD + "Stopping show: " + ChatColor.AQUA + showName);
		return true;
	}

}

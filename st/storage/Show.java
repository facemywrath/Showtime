package facemywrath.st.storage;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import facemywrath.st.main.Showtime;
import facemywrath.st.util.Animation;
import me.clip.placeholderapi.PlaceholderAPI;

public class Show extends Animation<Player> {

	public Show(Showtime main, ConfigurationSection section) {
		super(main);
		for(String key : section.getKeys(false)) {
			if(!StringUtils.isNumeric(key)) {
				System.out.println("ERROR: Show " + section.getName() + " Frame '" + key + "' Must be a number title.");
				continue;
			}
			long time = Long.parseLong(key);
			for(String command : section.getStringList(key)) {
				if(command.startsWith("repeat")) {
					if(!command.contains(" ")) {
						System.out.println("ERROR: Show " + section.getName() + " Frame '" + key + "' Command '" + command + "' must specify a number for repetitions.");
						continue;						
					}
					String[] args = command.split(" ");
					if(!StringUtils.isNumeric(args[1])) {
						System.out.println("ERROR: Show " + section.getName() + " Frame '" + key + "' Command '" + command + "' must specify a number for repetitions.");
						continue;
					}
					if(args.length == 2 || !StringUtils.isNumeric(args[2])) {
						System.out.println("ERROR: Show " + section.getName() + " Frame '" + key + "' Command '" + command + "' must specify a tick-count for time between repetitions.");
						continue;
					}
					int repetitions = Integer.parseInt(args[1]);
					long delay = Long.parseLong(args[2]);
					String newCommand = command.substring(command.indexOf(" " + args[2] + " ") + args[2].length() + 2);
					for(int i = 0; i < repetitions; i++) {
						final int j = i;
						this.addFrame(player -> {
							main.getServer().dispatchCommand(main.getServer().getConsoleSender(), newCommand);
						}, time + (delay*i));
					}
					continue;
				}
				this.addFrame(player -> {
					main.getServer().dispatchCommand(main.getServer().getConsoleSender(), command);
				}, time);
			}
		}
		for(int i = 0; i < frames.keySet().size() / 10 + 1; i++) {
			final int j = i;
			main.getServer().getScheduler().runTaskLater(main, () -> {
				for(int k = j * 10; k < j * 10 + 10; k++) {
					if(frames.keySet().size() > k)
						Bukkit.broadcastMessage(keyList().get(k) + "");
				}
			}, 100L * i);
		}
	}

}

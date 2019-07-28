package facemywrath.st.main;

import org.bukkit.plugin.java.JavaPlugin;

import facemywrath.st.commands.CommandShow;
import facemywrath.st.storage.ShowManager;

public class Showtime extends JavaPlugin {
	
	public void onEnable() {
		getCommand("show").setExecutor(new CommandShow(new ShowManager(this)));
	}

}

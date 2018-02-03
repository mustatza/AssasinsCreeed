package ro.coderdojo.ac;

import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import ro.coderdojo.ac.CoderDojoCommand;
import ro.coderdojo.ac.EventsListener;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
		getServer().getPluginManager().registerEvents(new EventsListener(), this);
		
		this.getCommand("edit").setExecutor(new CoderDojoCommand());
    }

}

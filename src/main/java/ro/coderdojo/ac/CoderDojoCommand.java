package ro.coderdojo.ac;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CoderDojoCommand implements CommandExecutor {
        public static boolean isEnabled = false;
        
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		commandSender.sendMessage("Edit Enabled!");
                isEnabled = !isEnabled;
		return true;
	}
}

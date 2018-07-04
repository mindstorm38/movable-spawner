package fr.mindstorm38.movablespawner;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandListener implements CommandExecutor {

	// Constants \\
	
	public static final String TOOL_COMMAND = "mstool";
	
	// Class \\
	
	private final MovableSpawner plugin;
	
	private PluginCommand toolCommand;
	
	public CommandListener(MovableSpawner plugin) {
		
		this.plugin = plugin;
		
	}
	
	public void start() {
		
		this.toolCommand = this.plugin.getCommand( TOOL_COMMAND );
		this.toolCommand.setExecutor( this );
		
	}
	
	public void stop() {
		
		this.toolCommand.setExecutor( null );
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		
		if ( cmd == this.toolCommand ) {
			
			if ( sender instanceof Player ) {
				
				if ( PermissionManager.hasPermission( sender, PermissionManager.PERMISSION_TOOL ) ) {
					
					Player player = (Player) sender;
					
					player.getInventory().addItem( new ItemStack( this.plugin.getToolItem() ) );
					
				}
				
			} else {
				
				sender.sendMessage("§cVous devez êtes un joueur pour executer cette commande§r");
				
			}
			
			return true;
			
		}
		
		return false;
		
	}

}

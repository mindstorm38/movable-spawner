package fr.mindstorm38.movablespawner;

import org.bukkit.command.CommandSender;

public class PermissionManager {
	
	// Constants \\
	
	public static final String PERSMISSION_ALL				= "movablespawner.*";
	public static final String PERMISSION_USE				= "movablespawner.use";
	public static final String PERMISSION_TOOL				= "movablespawner.tool";
	public static final String PERMISSION_BYPASS_RADIUS		= "movablespawner.bypassradius";
	public static final String PERMISSION_BYPASS_DIM		= "movablespawner.bypassdim";
	
	// Static \\

	public static boolean hasPermission(CommandSender sender, String permission, boolean message) {
		
		if ( sender.isOp() ) return true;
		if ( sender.hasPermission( PERSMISSION_ALL ) ) return true;
		boolean perm = sender.hasPermission( permission );
		if ( message && !perm ) sender.sendMessage( "§cMissing permission '§r§e" + permission + "§r§c'§r" );
		return perm;
		
	}
	
	public static boolean hasPermission(CommandSender sender, String permission) {
		return hasPermission( sender, permission, true );
	}
	
}

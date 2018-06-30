package fr.mindstorm38.movablespawner;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class EventListener implements Listener {

	private final MovableSpawner plugin;
	
	public EventListener(MovableSpawner plugin) {
		
		this.plugin = plugin;
		
	}
	
	public void start() {
		
		this.plugin.getPluginManager().registerEvents( this, this.plugin );
		
	}
	
	public void stop() {
		
		HandlerList.unregisterAll( this );
		
	}
	
	@EventHandler( priority = EventPriority.LOW )
	public void blockBreakEvent(BlockBreakEvent e) {
		
		if ( e.isCancelled() ) return;
		
		Block block = e.getBlock();
		
		if ( block.getType() == Material.MOB_SPAWNER ) {
			
			boolean canBreak = this.plugin.canBreakSpawner( block.getLocation() );
			
			if ( canBreak ) {
				
				Player player = e.getPlayer();
				
				if ( player != null ) {
					
					PlayerInventory playerInv = player.getInventory();
					
					ItemStack mainHandItem = playerInv.getItemInMainHand();
					
					if ( mainHandItem != null && mainHandItem.getType() != Material.AIR ) {
						
						boolean validToolItem = this.plugin.isValidToolItem( mainHandItem );
						
						if ( validToolItem ) {
							
							e.setCancelled( true );
							
							if ( PermissionManager.hasPermission( player, PermissionManager.PERMISSION_USE ) ) {
								
								this.plugin.breakSpawner( (CreatureSpawner) block.getState() );
								if ( player.getGameMode() != GameMode.CREATIVE ) this.plugin.removeOneTool( playerInv, true );
								
							}
							
						}
						
					}
					
				}
				
			} else {
				
				e.setCancelled( true );
				
			}
			
		}
		
	}
	
	@EventHandler
	public void blockPlaceEvent(BlockPlaceEvent e) {
		
		Block spawnerBlock = e.getBlockPlaced();
		
		if ( spawnerBlock.getType() == Material.MOB_SPAWNER ) {
			
			ItemStack handItem = e.getItemInHand();
			
			if ( this.plugin.validSpawnerItem( handItem ) ) {
				
				Player player = e.getPlayer();
				
				if ( PermissionManager.hasPermission( player, PermissionManager.PERMISSION_USE ) ) {
					
					boolean dimension = PermissionManager.hasPermission( player, PermissionManager.PERMISSION_BYPASS_DIM, false );
					if ( !dimension ) dimension = this.plugin.getItemSpawnerType( handItem ).validDimension( spawnerBlock.getWorld() );
					
					if ( dimension ) {
						
						boolean radius = PermissionManager.hasPermission( player, PermissionManager.PERMISSION_BYPASS_RADIUS, false );
						if ( !radius ) radius = this.plugin.validSpawnerPosition( spawnerBlock.getLocation() );
						
						if ( radius ) {
							
							this.plugin.placeSpawner( (CreatureSpawner) spawnerBlock.getState(), handItem );
							
						} else {
							
							e.setCancelled( true );
							player.sendMessage( "§cYou must be§r §e" + MovableSpawner.MINIMUM_SPAWNER_SPACEMENT + "§r §cblocks away from other spawners§r" );
							
						}
						
					} else {
						
						e.setCancelled( true );
						player.sendMessage( "§cYou can't place this type of spawner in this dimension§r" );
						
					}
					
				}
				
			}
			
		}
		
	}
	
}

package fr.mindstorm38.movablespawner;

import org.bukkit.GameMode;
import org.bukkit.Location;
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
	
	@EventHandler( priority = EventPriority.HIGHEST )
	public void blockBreakEvent(BlockBreakEvent e) {
		
		if ( e.isCancelled() ) return;
		if ( e instanceof MovableSpawnerBlockBreakEvent ) return;
		
		Block block = e.getBlock();
		Location blockLocation = block.getLocation();
		
		if ( !this.plugin.canInterractAt( blockLocation ) ) {
			
			e.setCancelled( true );
			e.getPlayer().sendMessage("§cImpossible d'intéragir dans cette zone, destruction de spawner en cours§r");
			return;
			
		}
		
		if ( block.getType() == Material.SPAWNER ) {
			
			Player player = e.getPlayer();
			
			if ( player != null ) {
				
				PlayerInventory playerInv = player.getInventory();
				
				ItemStack mainHandItem = playerInv.getItemInMainHand();
				
				if ( mainHandItem != null && mainHandItem.getType() != Material.AIR ) {
					
					boolean validToolItem = this.plugin.isValidToolItem( mainHandItem );
					
					if ( validToolItem ) {
						
						e.setCancelled( true );
						
						if ( this.plugin.canBreakSpawner( blockLocation, player ) ) {
							
							if ( PermissionManager.hasPermission( player, PermissionManager.PERMISSION_USE ) ) {
								
								this.plugin.breakSpawner( (CreatureSpawner) block.getState() );
								if ( player.getGameMode() != GameMode.CREATIVE ) this.plugin.removeOneTool( playerInv, true );
								
							} else {
								player.sendMessage("§cVous n'avez pas la permission d'utiliser cet outil§r");
							}
						
						} else {
							player.sendMessage("§cImpossible de casser le spawner, une zone de 3x3 centré sur le spawner ne peut pas être détruite§r");
						}
						
					}
					
				}
				
			}
				
		}
		
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	public void blockPlaceEvent(BlockPlaceEvent e) {
		
		if ( e.isCancelled() ) return;
		
		Block spawnerBlock = e.getBlockPlaced();
		
		if ( !this.plugin.canInterractAt( spawnerBlock.getLocation() ) ) {
			
			e.setCancelled( true );
			e.getPlayer().sendMessage("§cImpossible d'intéragir dans cette zone, destruction de spawner en cours§r");
			return;
			
		}
		
		if ( spawnerBlock.getType() == Material.SPAWNER ) {
			
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
							player.sendMessage( new String[] {
									"§cVous spawner est trop proche des autre spawners§r",
									"§c  Il est impossible de poser des spawners dans une zone carrée de§r §e" + MovableSpawner.SPAWNER_ZONE_SIZE_XZ + "x" + MovableSpawner.SPAWNER_ZONE_SIZE_Y + "x" + MovableSpawner.SPAWNER_ZONE_SIZE_XZ + " (X/Y/Z)§r §ccentré sur le spawner§r"
							} );
							
						}
						
					} else {
						
						e.setCancelled( true );
						player.sendMessage( "§cLe type d'entitée contenue dans ce spawner est incompatible avec la dimension du monde actuel§r" );
						
					}
					
				} else {
					player.sendMessage("§cVous n'avez pas la permission d'utiliser cet outil§r");
				}
				
			}
			
		}
		
	}
	
}

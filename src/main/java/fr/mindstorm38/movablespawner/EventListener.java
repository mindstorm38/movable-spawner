package fr.mindstorm38.movablespawner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.TrapDoor;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class EventListener implements Listener {

	// Constants \\
	
	public static final BlockFace[] HORIZONTAL_FACES = {
			BlockFace.NORTH,
			BlockFace.SOUTH,
			BlockFace.EAST,
			BlockFace.WEST
	};
	
	public static final BlockFace[] VERTICAL_FACES = {
			BlockFace.UP,
			BlockFace.DOWN
	};
	
	public static final long MIN_USE_DELAY = 100;
	
	// Class \\
	
	private final MovableSpawner plugin;
	
	private final Map<UUID, Long> playerLastUses;
	
	public EventListener(MovableSpawner plugin) {
		
		this.plugin = plugin;
		
		this.playerLastUses = Collections.synchronizedMap( new HashMap<>() );
		
	}
	
	public void start() {
		
		this.plugin.getPluginManager().registerEvents( this, this.plugin );
		
	}
	
	public void stop() {
		
		HandlerList.unregisterAll( this );
		
	}
	
	private boolean canUse(UUID uuid) {
		Long lastUse = this.playerLastUses.get( uuid );
		if ( lastUse == null ) return true;
		return ( System.currentTimeMillis() - lastUse ) >= MIN_USE_DELAY;
	}
	
	private void setLastUse(UUID uuid) {
		this.playerLastUses.put( uuid, System.currentTimeMillis() );
	}
	
	@EventHandler
	public void playerDisconnectedEvent(PlayerQuitEvent e) {
		this.playerLastUses.remove( e.getPlayer().getUniqueId() );
	}
	
	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e) {
		
		if ( !e.hasBlock() || e.hasItem() ) return;
		if ( e.getAction() != Action.RIGHT_CLICK_BLOCK ) return;
		
		Block block = e.getClickedBlock();
		
		if ( block.getType() == Material.IRON_TRAPDOOR ) {
			
			UUID playerUuid = e.getPlayer().getUniqueId();
			if ( !this.canUse( playerUuid ) ) return;
			
			TrapDoor trapdoor = (TrapDoor) block.getState().getData();
			
			Block spawnerBlock;
			
			if ( trapdoor.isInverted() )spawnerBlock = block.getRelative( BlockFace.UP );
			else spawnerBlock = block.getRelative( BlockFace.DOWN );
			
			if ( spawnerBlock.getType() != Material.MOB_SPAWNER )
				spawnerBlock = block.getRelative( trapdoor.getAttachedFace() );
			
			if ( spawnerBlock.getType() != Material.MOB_SPAWNER ) return;
			
			World world = block.getWorld();
			
			if ( validTrapdoors( spawnerBlock, true ) ) {
				
				CreatureSpawner spawnerBlockState = (CreatureSpawner) spawnerBlock.getState();
				
				NBTTagCompound spawnerNbt = NBTUtils.getTileEntityNBT( spawnerBlockState );
				
				net.minecraft.server.v1_12_R1.ItemStack stackRaw = CraftItemStack.asNMSCopy( new ItemStack( Material.MOB_SPAWNER ) );
				
				NBTTagCompound stackNbt = stackRaw.getTag();
				if ( stackNbt == null ) stackNbt = new NBTTagCompound();
				
				NBTTagCompound stackMovableSpawnerNbt = new NBTTagCompound();
				stackNbt.set( "movable_spawner", stackMovableSpawnerNbt );
				
				List<String> stackLores = new ArrayList<>();
				
				NBTUtils.getNBTShort( spawnerNbt, "MaxNearbyEntities", maxNearbyEntities -> {
					
					stackMovableSpawnerNbt.setShort( "max_nearby_entities", maxNearbyEntities );
					stackLores.add( "§aMax nearby entities :§r §e" + maxNearbyEntities + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerNbt, "RequiredPlayerRange", requiredPlayerRange -> {
					
					stackMovableSpawnerNbt.setShort( "required_player_range", requiredPlayerRange );
					stackLores.add( "§aRequired player range :§r §e" + requiredPlayerRange + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerNbt, "SpawnCount", spawnCount -> {
					
					stackMovableSpawnerNbt.setShort( "spawn_count", spawnCount );
					stackLores.add( "§aSpawn count :§r §e" + spawnCount + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerNbt, "MinSpawnDelay", minSpawnDelay -> {
					
					stackMovableSpawnerNbt.setShort( "min_spawn_delay", minSpawnDelay );
					stackLores.add( "§aMin spawn delay :§r §e" + minSpawnDelay + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerNbt, "MaxSpawnDelay", maxSpawnDelay -> {
					
					stackMovableSpawnerNbt.setShort( "max_spawn_delay", maxSpawnDelay );
					stackLores.add( "§aMax spawn delay :§r §e" + maxSpawnDelay + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerNbt, "SpawnRange", spawnRange -> {
					
					stackMovableSpawnerNbt.setShort( "spawn_range", spawnRange );
					stackLores.add( "§aSpawn range :§r §e" + spawnRange + "§r" );
					
				} );
				
				NBTUtils.getNBTBase( spawnerNbt, "SpawnData", spawnData -> {
					
					stackMovableSpawnerNbt.set( "spawn_data", spawnData );
					
				} );
				
				NBTUtils.getNBTBase( spawnerNbt, "SpawnPotentials", spawnPotentials -> {
					
					stackMovableSpawnerNbt.set( "spawn_potentials", spawnPotentials );
					
				} );
				
				stackRaw.setTag( stackNbt );
				
				ItemStack stack = CraftItemStack.asCraftMirror( stackRaw );
				
				ItemMeta stackMeta = stack.getItemMeta();
				
				stackMeta.setDisplayName( "§6" + spawnerBlockState.getSpawnedType() + "§r §espawner§r" );
				stackMeta.setLore( stackLores );
				
				stack.setItemMeta( stackMeta );
				
				Location spawnerLocation = spawnerBlock.getLocation();
				
				world.dropItem( spawnerLocation, stack );
				world.spawnParticle( Particle.LAVA, spawnerLocation, 30 );
				world.playSound( spawnerLocation, Sound.BLOCK_LAVA_EXTINGUISH, 1.0f, 0.5f );
				
				destroySpawnerAndTrapdoor( spawnerBlock );
				
				this.setLastUse( playerUuid );
				
			} else if ( validTrapdoors( spawnerBlock, false ) ) {
				
				openHorizontalTrapdoor( spawnerBlock );
				
				this.setLastUse( playerUuid );
				
			}
			
		}
		
	}
	
	@EventHandler
	public void blockPlaceEvent(BlockPlaceEvent e) {
		
		Block spawnerBlock = e.getBlockPlaced();
		
		if ( spawnerBlock.getType() != Material.MOB_SPAWNER ) return;
		
		net.minecraft.server.v1_12_R1.ItemStack stackRaw = CraftItemStack.asNMSCopy( e.getItemInHand() );
		
		NBTTagCompound stackNbt = stackRaw.getTag();
		if ( stackNbt == null || !stackNbt.hasKey("movable_spawner") ) return;
		
		NBTTagCompound stackMovableSpawnerNbt = stackNbt.getCompound("movable_spawner");
		if ( stackMovableSpawnerNbt == null ) return;
		
		CreatureSpawner spawnerBlockState = (CreatureSpawner) spawnerBlock.getState();
		
		NBTTagCompound spawnerNbt = NBTUtils.getTileEntityNBT( spawnerBlockState );
		
		NBTUtils.getNBTShort( stackMovableSpawnerNbt, "max_nearby_entities", maxNearbyEntities -> {
			
			spawnerNbt.setShort( "MaxNearbyEntities", maxNearbyEntities );
			
		} );
		
		NBTUtils.getNBTShort( stackMovableSpawnerNbt, "required_player_range", requiredPlayerRange -> {
			
			spawnerNbt.setShort( "RequiredPlayerRange", requiredPlayerRange );
			
		} );
		
		NBTUtils.getNBTShort( stackMovableSpawnerNbt, "spawn_count", spawnCount -> {
			
			spawnerNbt.setShort( "SpawnCount", spawnCount );
			
		} );
		
		NBTUtils.getNBTShort( stackMovableSpawnerNbt, "min_spawn_delay", minSpawnDelay -> {
			
			spawnerNbt.setShort( "MinSpawnDelay", minSpawnDelay );
			
		} );
		
		NBTUtils.getNBTShort( stackMovableSpawnerNbt, "max_spawn_delay", maxSpawnDelay -> {
			
			spawnerNbt.setShort( "MaxSpawnDelay", maxSpawnDelay );
			
		} );
		
		NBTUtils.getNBTShort( stackMovableSpawnerNbt, "spawn_range", spawnRange -> {
			
			spawnerNbt.setShort( "SpawnRange", spawnRange );
			
		} );
		
		NBTUtils.getNBTBase( stackMovableSpawnerNbt, "spawn_data", spawnData -> {
			
			spawnerNbt.set( "SpawnData", spawnData );
			
		} );
		
		NBTUtils.getNBTBase( stackMovableSpawnerNbt, "spawn_potentials", spawnPotentials -> {
			
			spawnerNbt.set( "SpawnPotentials", spawnPotentials );
			
		} );
		
		NBTUtils.setTileEntityNBT( spawnerBlockState, spawnerNbt );
		
		// Don't update Bukkit BlockState because that re-update NMS entity with its non-updated datas
		
		Location spawnerLocation = spawnerBlockState.getLocation();
		World world = spawnerLocation.getWorld();
		
		world.spawnParticle( Particle.ENCHANTMENT_TABLE, spawnerLocation.getX() + 0.5, spawnerLocation.getY() + 2, spawnerLocation.getZ() + 0.5, 100 );
		world.playSound( spawnerLocation, Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 0.2f );
		// world.playSound( spawnerLocation, Sound.BLOCK_PORTAL_TRAVEL, 1.0f, 2.0f );
		
	}
	
	private static boolean validHorizontalTrapdoor(Block blockSpawner, boolean checkOpened) {
		
		for ( BlockFace face : HORIZONTAL_FACES ) {
			
			Block block = blockSpawner.getRelative( face );
			
			if ( block.getType() != Material.IRON_TRAPDOOR ) return false;
			
			TrapDoor trapdoor = (TrapDoor) block.getState().getData();
			
			if ( checkOpened && !trapdoor.isOpen() ) return false;
			
			if ( trapdoor.getAttachedFace() != face.getOppositeFace() ) return false;
			
		}
		
		return true;
		
	}
	
	private static boolean validVerticalTrapdoors(Block blockSpawner) {
		
		for ( BlockFace face : VERTICAL_FACES ) {
			
			Block block = blockSpawner.getRelative( face );
			
			if ( block.getType() != Material.IRON_TRAPDOOR ) return false;
			
			TrapDoor trapdoor = (TrapDoor) block.getState().getData();
			
			if ( trapdoor.isOpen() ) return false;
			
			if ( face == BlockFace.UP && trapdoor.isInverted() ) return false;
			else if ( face == BlockFace.DOWN && !trapdoor.isInverted() ) return false;
			
		}
		
		return true;
		
	}
	
	public static boolean validTrapdoors(Block blockSpawner, boolean checkOpened) {
		if ( !validHorizontalTrapdoor( blockSpawner, checkOpened ) ) return false;
		return validVerticalTrapdoors( blockSpawner );
	}
	
	private static void openHorizontalTrapdoor(Block blockSpawner) {
		
		boolean one = false;
		
		for ( BlockFace face : HORIZONTAL_FACES ) {
			
			Block block = blockSpawner.getRelative( face );
			
			if ( block.getType() != Material.IRON_TRAPDOOR ) continue;
			
			BlockState state = block.getState();
			TrapDoor trapdoor = (TrapDoor) state.getData();
			
			if ( trapdoor.isOpen() ) continue;
			
			trapdoor.setOpen( true );
			state.setData( trapdoor );
			state.update();
			
			one = true;
			
		}
		
		if ( one ) blockSpawner.getWorld().playSound( blockSpawner.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.0f, 1.0f );
		
	}
	
	public static boolean arrayContainsBlockFace(BlockFace[] arr, BlockFace elt) {
		for ( BlockFace f : arr ) if ( f == elt ) return true;
		return false;
	}
	
	public static boolean isHorizontalTrapdoor(TrapDoor trapdoor) {
		return arrayContainsBlockFace( HORIZONTAL_FACES, trapdoor.getAttachedFace() );
	}
	
	public static boolean isVerticalTrapdoor(TrapDoor trapdoor) {
		return arrayContainsBlockFace( VERTICAL_FACES, trapdoor.getAttachedFace() );
	}
	
	public static void destroySpawnerAndTrapdoor(Block blockSpawner) {
		
		for ( BlockFace face : HORIZONTAL_FACES )
			blockSpawner.getRelative( face ).setType( Material.AIR );
		
		for ( BlockFace face : VERTICAL_FACES )
			blockSpawner.getRelative( face ).setType( Material.AIR );
		
		blockSpawner.setType( Material.AIR );
		
	}
	
}

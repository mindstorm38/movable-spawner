package fr.mindstorm38.movablespawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class MovableSpawner extends JavaPlugin {
	
	// Constants \\
	
	public static final int SPAWNER_ZONE_RADIUS_XZ			= 16;
	public static final int SPAWNER_ZONE_RADIUS_Y			= 24;
	
	public static final int SPAWNER_ZONE_SIZE_XZ			= SPAWNER_ZONE_RADIUS_XZ * 2 + 1;
	public static final int SPAWNER_ZONE_SIZE_Y				= SPAWNER_ZONE_RADIUS_Y * 2 + 1;
	
	// - NBT Constants
	
	public static final String SPAWNER_TOOL_KEY				= "movable_spawner_tool";
	
	public static final String SPAWNER_ITEM_NBT_KEY			= "movable_spawner";
	public static final String MAX_NEARBY_ENTITY_MS			= "max_nearby_entities";
	public static final String MAX_NEARBY_ENTITY_RAW		= "MaxNearbyEntities";
	public static final String REQUIRED_PLAYER_RANGE_MS		= "required_player_range";
	public static final String REQUIRED_PLAYER_RANGE_RAW	= "RequiredPlayerRange";
	public static final String SPAWN_COUNT_MS				= "spawn_count";
	public static final String SPAWN_COUNT_RAW				= "SpawnCount";
	public static final String MIN_SPAWN_DELAY_MS			= "min_spawn_delay";
	public static final String MIN_SPAWN_DELAY_RAW			= "MinSpawnDelay";
	public static final String MAX_SPAWN_DELAY_MS			= "max_spawn_delay";
	public static final String MAX_SPAWN_DELAY_RAW			= "MaxSpawnDelay";
	public static final String SPAWN_RANGE_MS				= "spawn_range";
	public static final String SPAWN_RANGE_RAW				= "SpawnRange";
	public static final String SPAWN_DATA_MS				= "spawn_data";
	public static final String SPAWN_DATA_RAW				= "SpawnData";
	public static final String SPAWN_POTENTIALS_MS			= "spawn_potentials";
	public static final String SPAWN_POTENTIALS_RAW			= "SpawnPotentials";
	public static final String ENTITY_TYPE					= "entity_type";
	
	// Class \\
	
	private final EventListener eventListener;
	private final CommandListener commandListener;
	
	private final List<SpawnerDestroyingRunnable> spawnerDestroyingRunnables;
	private final Random random;
	
	private ItemStack toolItem;
	private PluginManager pluginManager;
	private BukkitScheduler scheduler;
	
	public MovableSpawner() {
		
		this.eventListener = new EventListener( this );
		this.commandListener = new CommandListener( this );
		
		this.spawnerDestroyingRunnables = new ArrayList<>();
		this.random = new Random();
		
		this.getToolItem();
		
	}
	
	@Override
	public void onLoad() {
		
		this.pluginManager = this.getServer().getPluginManager();
		this.scheduler = this.getServer().getScheduler();
		
	}
	
	@Override
	public void onEnable() {
		
		this.eventListener.start();
		this.commandListener.start();
		
	}
	
	@Override
	public void onDisable() {
		
		this.eventListener.stop();
		this.commandListener.stop();
		
	}
	
	public void runSync(Runnable run) {
		this.scheduler.runTask( this, run );
	}
	
	public PluginManager getPluginManager() {
		return this.pluginManager;
	}
	
	public ItemStack getToolItem() {
		
		if ( this.toolItem == null ) {
			
			this.toolItem = NBTUtils.editItemStackNBT( new ItemStack( Material.RECORD_5 ), nbt -> {
				
				nbt.setBoolean( SPAWNER_TOOL_KEY, true );
				return true;
				
			} );
			
			ItemMeta itemMeta = this.toolItem.getItemMeta();
			
			itemMeta.setDisplayName("§6Spawner tool§r");
			itemMeta.addItemFlags( ItemFlag.values() );
			itemMeta.addEnchant( Enchantment.LUCK, 1, true );
			
			this.toolItem.setItemMeta( itemMeta );
			
		}
		
		return this.toolItem;
		
	}
	
	public boolean isValidToolItem(ItemStack item) {
		
		if ( item == null ) return false;
		
		if ( item.getType() != this.toolItem.getType() ) return false;
		
		return NBTUtils.checkItemStackNBT( item, nbt -> {
			return nbt.hasKey( SPAWNER_TOOL_KEY );
		} );
		
	}
	
	public boolean canBreakSpawner(Location loc) {
		for ( SpawnerDestroyingRunnable runnable : this.spawnerDestroyingRunnables )
			if ( runnable.spawnerLocation.equals( loc ) )
				return false;
		return true;
	}
	
	public void removeOneTool(PlayerInventory inv, boolean usedMainHandItem) {
		
		boolean giveRareRecord = this.random.nextFloat() <= 0.1f;
		ItemStack rareRecordItem = new ItemStack( Material.RECORD_11 );
		
		ItemStack mainHandItem = inv.getItemInMainHand();
		
		if ( usedMainHandItem || this.isValidToolItem( mainHandItem ) ) {
			
			if ( mainHandItem.getAmount() <= 1 ) {
				
				if ( giveRareRecord ) inv.setItemInMainHand( rareRecordItem );
				else inv.setItemInMainHand( null );
				
			} else {
				
				mainHandItem.setAmount( mainHandItem.getAmount() - 1 );
				inv.addItem( rareRecordItem );
				
			}
			
		} else {
			
			int heldSlot = inv.getHeldItemSlot();
			ItemStack heldItem = inv.getItem( heldSlot );
			
			if ( this.isValidToolItem( heldItem ) ) {
				
				if ( heldItem.getAmount() <= 1 ) {
					
					if ( giveRareRecord ) inv.setItem( heldSlot, rareRecordItem );
					else inv.setItem( heldSlot, null );
					
				} else {
					
					heldItem.setAmount( heldItem.getAmount() - 1 );
					inv.addItem( rareRecordItem );
					
				}
				
			}
			
		}
		
	}
	
	public void breakSpawner(CreatureSpawner spawnerBlockState) {
		
		Location spawnerLocation = spawnerBlockState.getLocation();
		if ( !this.canBreakSpawner( spawnerLocation ) ) return;
		
		final List<String> spawnerItemLores = new ArrayList<>();
		
		AtomicReference<EntitySpawnerType> entityType = new AtomicReference<EntitySpawnerType>( EntitySpawnerType.UNKNOWN );
		AtomicInteger entityTypeWeight = new AtomicInteger( 0 );
		
		ItemStack spawnerItem = NBTUtils.editItemStackNBT( new ItemStack( Material.MOB_SPAWNER ), spawnerItemNbt -> {
			
			NBTTagCompound movableSpawnerNbt = new NBTTagCompound();
			spawnerItemNbt.set( SPAWNER_ITEM_NBT_KEY, movableSpawnerNbt );
			
			NBTUtils.editTileEntityNBT( spawnerBlockState, spawnerBlockNbt -> {
				
				NBTUtils.getNBTShort( spawnerBlockNbt, MAX_NEARBY_ENTITY_RAW, maxNearbyEntities -> {
					
					movableSpawnerNbt.setShort( MAX_NEARBY_ENTITY_MS, maxNearbyEntities );
					spawnerItemLores.add( "§aMax nearby entities :§r §e" + maxNearbyEntities + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerBlockNbt, REQUIRED_PLAYER_RANGE_RAW, requiredPlayerRange -> {
					
					movableSpawnerNbt.setShort( REQUIRED_PLAYER_RANGE_MS, requiredPlayerRange );
					spawnerItemLores.add( "§aRequired player range :§r §e" + requiredPlayerRange + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerBlockNbt, SPAWN_COUNT_RAW, spawnCount -> {
					
					movableSpawnerNbt.setShort( SPAWN_COUNT_MS, spawnCount );
					spawnerItemLores.add( "§aSpawn count :§r §e" + spawnCount + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerBlockNbt, MIN_SPAWN_DELAY_RAW, minSpawnDelay -> {
					
					movableSpawnerNbt.setShort( MIN_SPAWN_DELAY_MS, minSpawnDelay );
					spawnerItemLores.add( "§aMin spawn delay :§r §e" + minSpawnDelay + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerBlockNbt, MAX_SPAWN_DELAY_RAW, maxSpawnDelay -> {
					
					movableSpawnerNbt.setShort( MAX_SPAWN_DELAY_MS, maxSpawnDelay );
					spawnerItemLores.add( "§aMax spawn delay :§r §e" + maxSpawnDelay + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerBlockNbt, SPAWN_RANGE_RAW, spawnRange -> {
					
					movableSpawnerNbt.setShort( SPAWN_RANGE_MS, spawnRange );
					spawnerItemLores.add( "§aSpawn range :§r §e" + spawnRange + "§r" );
					
				} );
				
				NBTUtils.getNBTCompound( spawnerBlockNbt, SPAWN_DATA_RAW, spawnData -> {
					
					movableSpawnerNbt.set( SPAWN_DATA_MS, spawnData );
					
					NBTUtils.getNBTString( spawnData, "id", spawnEntityId -> {
						
						entityType.set( EntitySpawnerType.getEntitySpawnerType( spawnEntityId ) );
						
					} );
					
				} );
				
				NBTUtils.getNBTList( spawnerBlockNbt, SPAWN_POTENTIALS_RAW, spawnPotentials -> {
					
					movableSpawnerNbt.set( SPAWN_POTENTIALS_MS, spawnPotentials );
					
					for ( int i = 0; i < spawnPotentials.size(); i++ ) {
						
						NBTTagCompound spawnPotential = spawnPotentials.get( i );
						
						NBTUtils.getNBTCompound( spawnPotential, "Entity", potentialEntity -> {
							
							NBTUtils.getNBTString( potentialEntity, "id", spawnEntityId -> {
								
								NBTUtils.getNBTInteger( spawnPotential, "Weight", potentialWeight -> {
									
									if ( potentialWeight > entityTypeWeight.get() ) {
										
										entityTypeWeight.set( potentialWeight );
										entityType.set( EntitySpawnerType.getEntitySpawnerType( spawnEntityId ) );
										
									}
									
								} );
								
							} );
							
						} );
						
					}
						
				} );
				
				return false;
				
			} );
			
			movableSpawnerNbt.setString( ENTITY_TYPE, entityType.get().identifier );
			
			return true;
			
		} );
		
		ItemMeta spawnerItemMeta = spawnerItem.getItemMeta();
		
		spawnerItemMeta.setDisplayName( "§6" + entityType.get().name + "§r §espawner§r" );
		spawnerItemMeta.setLore( spawnerItemLores );
		
		spawnerItem.setItemMeta( spawnerItemMeta );
		
		SpawnerDestroyingRunnable runnable = new SpawnerDestroyingRunnable( this, spawnerBlockState, spawnerItem );
		this.scheduler.runTaskAsynchronously( this, runnable );
		this.spawnerDestroyingRunnables.add( runnable );
		
	}
	
	public boolean validSpawnerItem(ItemStack stack) {
		if ( stack == null || stack.getType() != Material.MOB_SPAWNER ) return false;
		return NBTUtils.checkItemStackNBT( stack, nbt -> nbt.hasKey( SPAWNER_ITEM_NBT_KEY ) && ( nbt.get( SPAWNER_ITEM_NBT_KEY ) instanceof NBTTagCompound ) );
	}
	
	public EntitySpawnerType getItemSpawnerType(ItemStack stack) {
		
		return NBTUtils.checkItemStackNBT( stack, spawnerItemNbt -> {
			
			NBTTagCompound movableSpawnerNbt = spawnerItemNbt.getCompound( SPAWNER_ITEM_NBT_KEY );
			if ( movableSpawnerNbt == null ) return null;
			
			AtomicReference<EntitySpawnerType> spawnerType = new AtomicReference<>( EntitySpawnerType.UNKNOWN );
			
			NBTUtils.getNBTString( movableSpawnerNbt, ENTITY_TYPE, spawnedType -> {
				
				spawnerType.set( EntitySpawnerType.getEntitySpawnerType( spawnedType ) );
				
			} );
			
			return spawnerType.get();
			
		} );
		
	}
	
	public boolean validSpawnerPosition(Location location) {
		
		World world = location.getWorld();
		int maxHeight = world.getMaxHeight();
		
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		
		Block block;
		
		for ( int offX = -SPAWNER_ZONE_RADIUS_XZ; offX <= SPAWNER_ZONE_RADIUS_XZ; offX++ ) {
			for ( int offY = Math.max( -SPAWNER_ZONE_RADIUS_Y, 0 - y ); offY <= Math.min( SPAWNER_ZONE_RADIUS_Y, maxHeight - y ); offY++ ) {
				for ( int offZ = -SPAWNER_ZONE_RADIUS_XZ; offZ <= SPAWNER_ZONE_RADIUS_XZ; offZ++ ) {
					
					if ( offX == 0 && offY == 0 && offZ == 0 ) continue;
					
					block = world.getBlockAt( x + offX, y + offY, z + offZ );
					
					if ( block.getType() == Material.MOB_SPAWNER ) return false;
					
				}
			}
		}
		
		return true;
		
	}
	
	public void placeSpawner(CreatureSpawner spawnerBlockState, ItemStack handItem) {
		
		NBTUtils.editTileEntityNBT( spawnerBlockState, spawnerBlockNbt -> {
			
			return NBTUtils.checkItemStackNBT( handItem, spawnerItemNbt -> {
				
				NBTTagCompound movableSpawnerNbt = spawnerItemNbt.getCompound( SPAWNER_ITEM_NBT_KEY );
				if ( movableSpawnerNbt == null ) return false;
				
				NBTUtils.getNBTShort( movableSpawnerNbt, MAX_NEARBY_ENTITY_MS, maxNearbyEntities -> {
					spawnerBlockNbt.setShort( MAX_NEARBY_ENTITY_RAW, maxNearbyEntities );
				} );
				
				NBTUtils.getNBTShort( movableSpawnerNbt, REQUIRED_PLAYER_RANGE_MS, requiredPlayerRange -> {
					spawnerBlockNbt.setShort( REQUIRED_PLAYER_RANGE_RAW, requiredPlayerRange );
				} );
				
				NBTUtils.getNBTShort( movableSpawnerNbt, SPAWN_COUNT_MS, spawnCount -> {
					spawnerBlockNbt.setShort( SPAWN_COUNT_RAW, spawnCount );
				} );
				
				NBTUtils.getNBTShort( movableSpawnerNbt, MIN_SPAWN_DELAY_MS, minSpawnDelay -> {
					spawnerBlockNbt.setShort( MIN_SPAWN_DELAY_RAW, minSpawnDelay );
				} );
				
				NBTUtils.getNBTShort( movableSpawnerNbt, MAX_SPAWN_DELAY_MS, maxSpawnDelay -> {
					spawnerBlockNbt.setShort( MAX_SPAWN_DELAY_RAW, maxSpawnDelay );
				} );
				
				NBTUtils.getNBTShort( movableSpawnerNbt, SPAWN_RANGE_MS, spawnRange -> {
					spawnerBlockNbt.setShort( SPAWN_RANGE_RAW, spawnRange );
				} );
				
				NBTUtils.getNBTCompound( movableSpawnerNbt, SPAWN_DATA_MS, spawnData -> {
					spawnerBlockNbt.set( SPAWN_DATA_RAW, spawnData );
				} );
				
				NBTUtils.getNBTList( movableSpawnerNbt, SPAWN_POTENTIALS_MS, spawnPotentials -> {
					spawnerBlockNbt.set( SPAWN_POTENTIALS_RAW, spawnPotentials );
				} );
				
				return true;
				
			} );
			
		} );
		
		Location spawnerLocation = spawnerBlockState.getLocation();
		World spawnerWorld = spawnerLocation.getWorld();
		
		spawnerWorld.spawnParticle( Particle.ENCHANTMENT_TABLE, spawnerLocation.getX() + 0.5, spawnerLocation.getY() + 2, spawnerLocation.getZ() + 0.5, 100 );
		spawnerWorld.playSound( spawnerLocation, Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 0.2f );
		
	}
	
	public void removeSpawnerDestroyingRunnable(SpawnerDestroyingRunnable runnable) {
		this.spawnerDestroyingRunnables.remove( runnable );
	}
	
}

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
	
	public static final String TOOL_NBT_KEY				= "movable_spawner_tool";
	public static final String SPAWNER_ITEM_NBT_KEY		= "movable_spawner";
	public static final int MINIMUM_SPAWNER_SPACEMENT	= 8;
	
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
				
				nbt.setBoolean( TOOL_NBT_KEY, true );
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
			return nbt.hasKey( TOOL_NBT_KEY );
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
		
		AtomicReference<EntitySpawnerType> spawnerType = new AtomicReference<EntitySpawnerType>( EntitySpawnerType.UNKNOWN );
		AtomicInteger spawnerTypeWeight = new AtomicInteger( 0 );
		
		ItemStack spawnerItem = NBTUtils.editItemStackNBT( new ItemStack( Material.MOB_SPAWNER ), spawnerItemNbt -> {
			
			NBTTagCompound movableSpawnerNbt = new NBTTagCompound();
			spawnerItemNbt.set( SPAWNER_ITEM_NBT_KEY, movableSpawnerNbt );
			
			NBTUtils.editTileEntityNBT( spawnerBlockState, spawnerBlockNbt -> {
				
				NBTUtils.getNBTShort( spawnerBlockNbt, "MaxNearbyEntities", maxNearbyEntities -> {
					
					movableSpawnerNbt.setShort( "max_nearby_entities", maxNearbyEntities );
					spawnerItemLores.add( "§aMax nearby entities :§r §e" + maxNearbyEntities + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerBlockNbt, "RequiredPlayerRange", requiredPlayerRange -> {
					
					movableSpawnerNbt.setShort( "required_player_range", requiredPlayerRange );
					spawnerItemLores.add( "§aRequired player range :§r §e" + requiredPlayerRange + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerBlockNbt, "SpawnCount", spawnCount -> {
					
					movableSpawnerNbt.setShort( "spawn_count", spawnCount );
					spawnerItemLores.add( "§aSpawn count :§r §e" + spawnCount + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerBlockNbt, "MinSpawnDelay", minSpawnDelay -> {
					
					movableSpawnerNbt.setShort( "min_spawn_delay", minSpawnDelay );
					spawnerItemLores.add( "§aMin spawn delay :§r §e" + minSpawnDelay + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerBlockNbt, "MaxSpawnDelay", maxSpawnDelay -> {
					
					movableSpawnerNbt.setShort( "max_spawn_delay", maxSpawnDelay );
					spawnerItemLores.add( "§aMax spawn delay :§r §e" + maxSpawnDelay + "§r" );
					
				} );
				
				NBTUtils.getNBTShort( spawnerBlockNbt, "SpawnRange", spawnRange -> {
					
					movableSpawnerNbt.setShort( "spawn_range", spawnRange );
					spawnerItemLores.add( "§aSpawn range :§r §e" + spawnRange + "§r" );
					
				} );
				
				NBTUtils.getNBTCompound( spawnerBlockNbt, "SpawnData", spawnData -> {
					
					movableSpawnerNbt.set( "spawn_data", spawnData );
					
					NBTUtils.getNBTString( spawnData, "id", spawnEntityId -> {
						
						spawnerType.set( EntitySpawnerType.getEntitySpawnerType( spawnEntityId ) );
						
					} );
					
				} );
				
				NBTUtils.getNBTList( spawnerBlockNbt, "SpawnPotentials", spawnPotentials -> {
					
					movableSpawnerNbt.set( "spawn_potentials", spawnPotentials );
					
					for ( int i = 0; i < spawnPotentials.size(); i++ ) {
						
						NBTTagCompound spawnPotential = spawnPotentials.get( i );
						
						NBTUtils.getNBTCompound( spawnPotential, "Entity", potentialEntity -> {
							
							NBTUtils.getNBTString( potentialEntity, "id", spawnEntityId -> {
								
								NBTUtils.getNBTInteger( spawnPotential, "Weight", potentialWeight -> {
									
									if ( potentialWeight > spawnerTypeWeight.get() ) {
										
										spawnerTypeWeight.set( potentialWeight );
										spawnerType.set( EntitySpawnerType.getEntitySpawnerType( spawnEntityId ) );
										
									}
									
								} );
								
							} );
							
						} );
						
					}
						
				} );
				
				return false;
				
			} );
			
			movableSpawnerNbt.setString( "spawned_type", spawnerType.get().identifier );
			
			return true;
			
		} );
		
		ItemMeta spawnerItemMeta = spawnerItem.getItemMeta();
		
		spawnerItemMeta.setDisplayName( "§6" + spawnerType.get().name + "§r §espawner§r" );
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
			
			NBTUtils.getNBTString( movableSpawnerNbt, "spawned_type", spawnedType -> {
				
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
		
		final Location loc = new Location( world, x, y, z );
		Block block;
		
		for ( int offX = -MINIMUM_SPAWNER_SPACEMENT; offX <= MINIMUM_SPAWNER_SPACEMENT; offX++ ) {
			for ( int offY = Math.max( -MINIMUM_SPAWNER_SPACEMENT, 0 - y ); offY <= Math.min( MINIMUM_SPAWNER_SPACEMENT, maxHeight - y ); offY++ ) {
				for ( int offZ = -MINIMUM_SPAWNER_SPACEMENT; offZ <= MINIMUM_SPAWNER_SPACEMENT; offZ++ ) {
					
					if ( offX == 0 && offY == 0 && offZ == 0 ) continue;
					
					loc.setX( x + offX );
					loc.setY( y + offY );
					loc.setZ( z + offZ );
					
					if ( loc.distance( location ) > MINIMUM_SPAWNER_SPACEMENT ) continue;
					
					block = world.getBlockAt( loc );
					
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
				
				NBTUtils.getNBTShort( movableSpawnerNbt, "max_nearby_entities", maxNearbyEntities -> {
					spawnerBlockNbt.setShort( "MaxNearbyEntities", maxNearbyEntities );
				} );
				
				NBTUtils.getNBTShort( movableSpawnerNbt, "required_player_range", requiredPlayerRange -> {
					spawnerBlockNbt.setShort( "RequiredPlayerRange", requiredPlayerRange );
				} );
				
				NBTUtils.getNBTShort( movableSpawnerNbt, "spawn_count", spawnCount -> {
					spawnerBlockNbt.setShort( "SpawnCount", spawnCount );
				} );
				
				NBTUtils.getNBTShort( movableSpawnerNbt, "min_spawn_delay", minSpawnDelay -> {
					spawnerBlockNbt.setShort( "MinSpawnDelay", minSpawnDelay );
				} );
				
				NBTUtils.getNBTShort( movableSpawnerNbt, "max_spawn_delay", maxSpawnDelay -> {
					spawnerBlockNbt.setShort( "MaxSpawnDelay", maxSpawnDelay );
				} );
				
				NBTUtils.getNBTShort( movableSpawnerNbt, "spawn_range", spawnRange -> {
					spawnerBlockNbt.setShort( "SpawnRange", spawnRange );
				} );
				
				NBTUtils.getNBTCompound( movableSpawnerNbt, "spawn_data", spawnData -> {
					spawnerBlockNbt.set( "SpawnData", spawnData );
				} );
				
				NBTUtils.getNBTList( movableSpawnerNbt, "spawn_potentials", spawnPotentials -> {
					spawnerBlockNbt.set( "SpawnPotentials", spawnPotentials );
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

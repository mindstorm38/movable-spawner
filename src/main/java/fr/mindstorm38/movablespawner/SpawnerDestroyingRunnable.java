package fr.mindstorm38.movablespawner;

import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.TrapDoor;
import org.bukkit.util.Vector;

public class SpawnerDestroyingRunnable implements Runnable {

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
	
	public static final BlockFace[] FACES = {
			BlockFace.NORTH,
			BlockFace.SOUTH,
			BlockFace.EAST,
			BlockFace.WEST,
			BlockFace.UP,
			BlockFace.DOWN
	};
	
	// Class \\
	
	private final MovableSpawner plugin;
	@SuppressWarnings("unused")
	private final CreatureSpawner spawnerBlockState;
	private final ItemStack spawnerItem;
	
	protected final Location spawnerLocation;
	
	private final World world;
	private final int x;
	private final int y;
	private final int z;
	
	private final Location centerLocation;
	
	public SpawnerDestroyingRunnable(MovableSpawner plugin, CreatureSpawner spawnerBlockState, ItemStack spawnerItem) {
		
		this.plugin = plugin;
		this.spawnerBlockState = spawnerBlockState;
		this.spawnerItem = spawnerItem;
		
		this.spawnerLocation = spawnerBlockState.getLocation();
		
		this.world = this.spawnerLocation.getWorld();
		this.x = this.spawnerLocation.getBlockX();
		this.y = this.spawnerLocation.getBlockY();
		this.z = this.spawnerLocation.getBlockZ();
		
		this.centerLocation = new Location( this.world, this.x + 0.5f, this.y + 0.5f, this.z + 0.5f );
		
	}
	
	@Override
	public void run() {
		
		this.plugin.runSync( () -> {
			
			this.world.playSound( this.spawnerLocation, Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 0.5f );
			
		} );
		
		int maxHeight = this.world.getMaxHeight();
		
		AtomicBoolean update = new AtomicBoolean( false );
		
		for ( int offX = -1; offX <= 1; offX++ ) {
			for ( int offY = -1; offY <= 1; offY++ ) {
				for ( int offZ = -1; offZ <= 1; offZ++ ) {
					
					if ( offX == 0 && offY == 0 && offZ == 0 ) continue;
					
					final int locY = this.y + offY;
					if ( locY < 0 || locY > maxHeight ) continue;
					
					final int locX = this.x + offX;
					final int locZ = this.z + offZ;
					
					update.set( false );
					
					this.plugin.runSync( () -> {
						
						Block block = this.world.getBlockAt( locX, locY, locZ );
						
						if ( block.isEmpty() ) return;
						
						update.set( true );
						block.breakNaturally();
						
					} );
					
					if ( update.get() ) safesleep( 100L );
					
				}
			}
		}
		
		safesleep( 600L );
		
		for ( BlockFace face : HORIZONTAL_FACES ) {
			
			safesleep( 300L );
			this.placeTrapdoor( face, false );
			
		}
		
		for ( BlockFace face : VERTICAL_FACES ) {
			
			safesleep( 300L );
			this.placeTrapdoor( face, true );
			
		}
		
		safesleep( 500L );
		
		this.plugin.runSync( () -> {
			
			this.world.strikeLightningEffect( this.spawnerLocation.clone().add( 0f, 1f, 0f ) );
			
		} );
		
		for ( int i = 0; i < 50; i++ ) {
			
			this.plugin.runSync( () -> {
				
				this.world.spawnParticle( Particle.LAVA, this.x + 0.5f, this.y + 1f, this.z + 0.5f, 2 );
				
			} );
			
			safesleep( 50L );
			
		}
		
		this.plugin.runSync( () -> {
			
			for ( BlockFace face : FACES ) {
				
				int locX = this.x + face.getModX();
				int locY = this.y + face.getModY();
				int locZ = this.z + face.getModZ();
				
				Block block = this.world.getBlockAt( locX, locY, locZ );
				block.setType( Material.AIR );
				
			}
			
			this.world.getBlockAt( this.spawnerLocation ).setType( Material.AIR );
			
			this.world.spawnParticle( Particle.CRIT, this.centerLocation, 10 );
			this.world.playSound( this.spawnerLocation, Sound.BLOCK_METAL_BREAK, 1.0f, 1.0f );
			
		} );
		
		safesleep( 100L );
		
		this.plugin.runSync( () -> {
			
			Item item = this.world.dropItem( this.centerLocation, this.spawnerItem );
			item.setGravity( false );
			item.setVelocity( new Vector( 0f, 0f, 0f ) );
			
			this.world.spawnParticle( Particle.PORTAL, this.centerLocation.clone().add( 0f, 0.3f, 0f ), 100 );
			this.world.playSound( this.spawnerLocation, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 0.5f );
			// this.world.playSound( this.spawnerLocation, Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 0.5f );
			// this.world.playSound( this.spawnerLocation, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 0.5f );
			// this.world.playSound( this.spawnerLocation, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f, 0.5f );
			
		} );
		
		this.plugin.removeSpawnerDestroyingRunnable( this );
		
		for ( int i = 0; i < 100; i++ ) {
			
			this.plugin.runSync( () -> {
				
				this.world.spawnParticle( Particle.END_ROD, this.centerLocation, 1, 0, 0, 0, 0 );
				
			} );
			
			safesleep( 100L );
			
		}
		
	}
	
	private void placeTrapdoor(BlockFace face, boolean vertical) {
		
		final int locX = this.x + face.getModX();
		final int locY = this.y + face.getModY();
		final int locZ = this.z + face.getModZ();
		
		this.plugin.runSync( () -> {
			
			Block block = this.world.getBlockAt( locX, locY, locZ );
			block.setType( Material.IRON_TRAPDOOR );
			
			BlockState blockState = block.getState();
			TrapDoor trapdoor = (TrapDoor) blockState.getData();
			
			trapdoor.setOpen( !vertical );
			
			if ( !vertical ) {
				trapdoor.setFacingDirection( face );
			} else if ( face == BlockFace.DOWN ) {
				trapdoor.setInverted( true );
			}
			
			blockState.setData( trapdoor );
			blockState.update();
			
			this.world.playSound( this.spawnerLocation, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f, 1.0f );
			
		} );
		
	}
	
	public static void safesleep(long time) {
		try { Thread.sleep( time ); } catch (InterruptedException e) {}
	}
	
}

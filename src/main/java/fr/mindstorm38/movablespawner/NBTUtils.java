package fr.mindstorm38.movablespawner;

import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagShort;
import net.minecraft.server.v1_12_R1.TileEntity;

public class NBTUtils {

	public static void getNBTShort(NBTTagCompound compound, String identifier, Consumer<Short> consumer) {
		NBTBase base = compound.get( identifier );
		if ( base == null || !( base instanceof NBTTagShort ) ) return;
		consumer.accept( ( (NBTTagShort) base ).f() );
	}

	public static void getNBTBase(NBTTagCompound compound, String identifier, Consumer<NBTBase> consumer) {
		NBTBase base = compound.get( identifier );
		if ( base == null ) return;
		consumer.accept( base );
	}

	public static void getNBTCompound(NBTTagCompound compound, String identifier, Consumer<NBTTagCompound> consumer) {
		NBTBase base = compound.get( identifier );
		if ( base == null || !( base instanceof NBTTagCompound ) ) return;
		consumer.accept( ( (NBTTagCompound) base ).g() );
	}

	public static NBTTagCompound getTileEntityNBT(BlockState state) {
		
		Location location = state.getLocation();
		CraftWorld worldRaw = (CraftWorld) location.getWorld();
		
		TileEntity tileEntity = worldRaw.getTileEntityAt( location.getBlockX(), location.getBlockY(), location.getBlockZ() );
		
		NBTTagCompound compound = new NBTTagCompound();
		tileEntity.save( compound );
		return compound;
		
	}

	public static void setTileEntityNBT(BlockState state, NBTTagCompound nbt) {
		
		Location location = state.getLocation();
		CraftWorld worldRaw = (CraftWorld) location.getWorld();
		
		TileEntity tileEntity = worldRaw.getTileEntityAt( location.getBlockX(), location.getBlockY(), location.getBlockZ() );
		
		tileEntity.load( nbt );
		tileEntity.update();
		
	}

}

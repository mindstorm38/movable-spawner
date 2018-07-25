package fr.mindstorm38.movablespawner;

import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;

import net.minecraft.server.v1_13_R1.ItemStack;
import net.minecraft.server.v1_13_R1.NBTBase;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import net.minecraft.server.v1_13_R1.NBTTagInt;
import net.minecraft.server.v1_13_R1.NBTTagList;
import net.minecraft.server.v1_13_R1.NBTTagShort;
import net.minecraft.server.v1_13_R1.NBTTagString;
import net.minecraft.server.v1_13_R1.TileEntity;

public class NBTUtils {

	public static void getNBTShort(NBTTagCompound compound, String identifier, Consumer<Short> consumer) {
		NBTBase base = compound.get( identifier );
		if ( base == null || !( base instanceof NBTTagShort ) ) return;
		consumer.accept( ( (NBTTagShort) base ).f() );
	}
	
	public static void getNBTInteger(NBTTagCompound compound, String identifier, Consumer<Integer> consumer) {
		NBTBase base = compound.get( identifier );
		if ( base == null || !( base instanceof NBTTagInt ) ) return;
		consumer.accept( ( (NBTTagInt) base ).e() );
	}
	
	public static void getNBTString(NBTTagCompound compound, String identifier, Consumer<String> consumer) {
		NBTBase base = compound.get( identifier );
		if ( base == null || !( base instanceof NBTTagString ) ) return;
		consumer.accept( ( (NBTTagString) base ).b_() );
	}
	
	public static void getNBTBase(NBTTagCompound compound, String identifier, Consumer<NBTBase> consumer) {
		NBTBase base = compound.get( identifier );
		if ( base == null ) return;
		consumer.accept( base );
	}

	public static void getNBTCompound(NBTTagCompound compound, String identifier, Consumer<NBTTagCompound> consumer) {
		NBTBase base = compound.get( identifier );
		if ( base == null || !( base instanceof NBTTagCompound ) ) return;
		consumer.accept( ( (NBTTagCompound) base ) );
	}
	
	public static void getNBTList(NBTTagCompound compound, String identifier, Consumer<NBTTagList> consumer) {
		NBTBase base = compound.get( identifier );
		if ( base == null || !( base instanceof NBTTagList ) ) return;
		consumer.accept( ( (NBTTagList) base ) );
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
	
	public static void editTileEntityNBT(BlockState state, Function<NBTTagCompound, Boolean> nbtProcessor) {
		
		Location location = state.getLocation();
		CraftWorld worldRaw = (CraftWorld) location.getWorld();
		
		TileEntity tileEntity = worldRaw.getTileEntityAt( location.getBlockX(), location.getBlockY(), location.getBlockZ() );
		
		NBTTagCompound tileEntityNbt = new NBTTagCompound();
		tileEntity.save( tileEntityNbt );
		
		if ( nbtProcessor.apply( tileEntityNbt ) ) {
			
			tileEntity.load( tileEntityNbt );
			tileEntity.update();
			
		}
		
	}
	
	public static org.bukkit.inventory.ItemStack editItemStackNBT(org.bukkit.inventory.ItemStack stack, Function<NBTTagCompound, Boolean> nbtProcessor) {
		
		ItemStack stackRaw = CraftItemStack.asNMSCopy( stack );
		
		NBTTagCompound stackNbt = stackRaw.getTag();
		if ( stackNbt == null ) stackNbt = new NBTTagCompound();
		
		if ( nbtProcessor.apply( stackNbt ) ) {
			
			stackRaw.setTag( stackNbt );
			
			return CraftItemStack.asCraftMirror( stackRaw );
			
		}
		
		return stack;
		
	}
	
	public static <E> E checkItemStackNBT(org.bukkit.inventory.ItemStack stack, Function<NBTTagCompound, E> nbtProcessor) {
		
		ItemStack stackRaw = CraftItemStack.asNMSCopy( stack );
		
		NBTTagCompound stackNbt = stackRaw.getTag();
		if ( stackNbt == null ) stackNbt = new NBTTagCompound();
		
		return nbtProcessor.apply( stackNbt );
		
	}

}

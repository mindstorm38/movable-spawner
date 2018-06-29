package fr.mindstorm38.movablespawner;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MovableSpawner extends JavaPlugin {
	
	private final EventListener eventListener;
	
	private PluginManager pluginManager;
	
	public MovableSpawner() {
		
		this.eventListener = new EventListener( this );
		
	}
	
	@Override
	public void onLoad() {
		
		this.pluginManager = this.getServer().getPluginManager();
		
	}
	
	@Override
	public void onEnable() {
		
		this.eventListener.start();
		
	}
	
	@Override
	public void onDisable() {
		
		this.eventListener.stop();
		
	}
	
	public PluginManager getPluginManager() {
		return this.pluginManager;
	}
	
}

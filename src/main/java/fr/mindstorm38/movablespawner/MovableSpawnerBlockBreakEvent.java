package fr.mindstorm38.movablespawner;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class MovableSpawnerBlockBreakEvent extends BlockBreakEvent {

	public MovableSpawnerBlockBreakEvent(Block theBlock, Player player) {
		super( theBlock, player );
	}

}

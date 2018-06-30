package fr.mindstorm38.movablespawner;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;

public enum EntitySpawnerType {
	
	AREA_EFFECT_CLOUD	( EntityType.AREA_EFFECT_CLOUD,		"Area Effect Cloud",	"minecraft:area_effect_cloud"		),
	ARMOR_STAND			( EntityType.ARMOR_STAND,			"Armor Stand",			"minecraft:armor_stand"				),
	ARROW				( EntityType.ARROW,					"Arrow",				"minecraft:arrow" 					),
	BAT					( EntityType.BAT,					"Bat",					"minecraft:bat", 					Environment.NORMAL, Environment.NETHER ),
	BLAZE				( EntityType.BLAZE,					"Blaze",				"minecraft:blaze",					Environment.NETHER ),
	BOAT				( EntityType.BOAT,					"Boat",					"minecraft:boat"					),
	CAVE_SPIDER			( EntityType.CAVE_SPIDER,			"Cave Spider",			"minecraft:cave_spider",			Environment.NORMAL ),
	CHICKEN				( EntityType.CHICKEN,				"Chicken",				"minecraft:chicken",				Environment.NORMAL ),
	COMPLEX_PART		( EntityType.COMPLEX_PART,			"Complex Part",			null								),
	COW					( EntityType.COW,					"Cow",					"minecraft:cow",					Environment.NORMAL ),
	CREEPER				( EntityType.CREEPER,				"Creeper",				"minecraft:creeper",				Environment.NORMAL ),
	DONKEY				( EntityType.DONKEY,				"Donkey",				"minecraft:donkey",					Environment.NORMAL ),
	DRAGON_FIREBALL		( EntityType.DRAGON_FIREBALL,		"Dragon Fireball",		"minecraft:dragon_fireball",		Environment.THE_END ),
	DROPPED_ITEM		( EntityType.DROPPED_ITEM,			"Dropped Item",			"minecraft:item"					),
	EGG					( EntityType.EGG,					"Egg",					"minecraft:egg"						),
	ELDER_GUARDIAN		( EntityType.ELDER_GUARDIAN,		"Elder Guardian",		"minecraft:elder_guardian",			Environment.NORMAL ),
	ENDER_CRYSTAL		( EntityType.ENDER_CRYSTAL,			"Ender Crystal",		"minecraft:ender_crystal",			Environment.THE_END ),
	ENDER_DRAGON		( EntityType.ENDER_DRAGON,			"Ender Dragon",			"minecraft:ender_dragon",			Environment.THE_END ),
	ENDER_PEARL			( EntityType.ENDER_PEARL,			"Ender Pearl",			"minecraft:ender_pearl"				),
	ENDER_SIGNAL		( EntityType.ENDER_SIGNAL,			"Ender Signal",			"minecraft:eye_of_ender_signal"		),
	ENDERMAN			( EntityType.ENDERMAN,				"Enderman",				"minecraft:enderman",				Environment.NORMAL, Environment.THE_END ),
	ENDERMITE			( EntityType.ENDERMITE,				"Endermite",			"minecraft:endermite"				),
	EVOKER				( EntityType.EVOKER,				"Evoker",				"minecraft:evocation_illager",		Environment.NORMAL ),
	EVOKER_FANGS		( EntityType.EVOKER_FANGS, 			"Evoker Fangs",			"minecraft:evocation_fangs"			),
	EXPERIENCE_ORB		( EntityType.EXPERIENCE_ORB,		"Experience Orb",		"minecraft:xp_orb"					),
	FALLING_BLOCK		( EntityType.FALLING_BLOCK,			"Falling Block",		"minecraft:falling_block"			),
	FIREBALL			( EntityType.FIREBALL,				"Fireball",				"minecraft:fireball"				),
	FIREWORK			( EntityType.FIREWORK,				"Firework",				"minecraft:fireworks_rocket"		),
	FISHING_HOOK		( EntityType.FISHING_HOOK,			"Fishing Hook",			null								),
	GHAST				( EntityType.GHAST,					"Ghast",				"minecraft:ghast",					Environment.NETHER ),
	GIANT				( EntityType.GIANT,					"Giant",				"minecraft:giant"					),
	GUARDIAN			( EntityType.GUARDIAN,				"Guardian",				"minecraft:guardian",				Environment.NORMAL ),
	HORSE				( EntityType.HORSE,					"Horse",				"minecraft:horse",					Environment.NORMAL ),
	HUSK				( EntityType.HUSK,					"Husk",					"minecraft:husk",					Environment.NORMAL ),
	ILLUSIONER			( EntityType.ILLUSIONER,			"Illusioner",			"minecraft:illusion_illager",		Environment.NORMAL ),
	IRON_GOLEM			( EntityType.IRON_GOLEM,			"Iron Golem",			"minecraft:villager_golem",			Environment.NORMAL ),
	ITEM_FRAME			( EntityType.ITEM_FRAME,			"Item Frame",			"minecraft:item_frame"				),
	LEASH_HITCH			( EntityType.LEASH_HITCH,			"Leash Hitch",			"minecraft:leash_knot"				),
	LIGHTNING			( EntityType.LIGHTNING,				"Lightning",			null								),
	LINGERING_POTION	( EntityType.LINGERING_POTION,		"Lithering Potion",		null								),
	LLAMA				( EntityType.LLAMA,					"Llama",				"minecraft:llama",					Environment.NORMAL ),
	LLAMA_SPIT			( EntityType.LLAMA_SPIT,			"Llama Spit",			"minecraft:llama_spit"				),
	MAGMA_CUBE			( EntityType.MAGMA_CUBE,			"Magma Cube",			"minecraft:magma_cube",				Environment.NETHER ),
	MINECART			( EntityType.MINECART,				"Minecart",				"minecraft:minecart"				),
	MINECART_CHEST		( EntityType.MINECART_CHEST,		"Minecart Chest",		"minecraft:chest_minecart"			),
	MINECART_COMMAND	( EntityType.MINECART_COMMAND,		"Minecart Command",		"minecraft:commandblock_minecart"	),
	MINECART_FURNACE	( EntityType.MINECART_FURNACE,		"Minecart Furnace",		"minecraft:furnace_minecart"		),
	MINECART_HOPPER		( EntityType.MINECART_HOPPER,		"Minecart Hopper",		"minecraft:hopper_minecart"			),
	MINECART_MOB_SPAWNER( EntityType.MINECART_MOB_SPAWNER,	"Minecart Mob Spawner",	"minecraft:spawner_minecart"		),
	MINECART_TNT		( EntityType.MINECART_TNT,			"Minecart TNT",			"minecraft:tnt_minecart"			),
	MULE				( EntityType.MULE,					"Mule",					"minecraft:mule",					Environment.NORMAL ),
	MUSHROOM_COW		( EntityType.MUSHROOM_COW,			"Mooshroom",			"minecraft:mooshroom",				Environment.NORMAL ),
	OCELOT				( EntityType.OCELOT,				"Ocelot",				"minecraft:ocelot",					Environment.NORMAL ),
	PAINTING			( EntityType.PAINTING,				"Painting",				"minecraft:painting"				),
	PARROT				( EntityType.PARROT,				"Parrot",				"minecraft:parrot"					),
	PIG					( EntityType.PIG,					"Pig",					"minecraft:pig",					Environment.NORMAL ),
	PIG_ZOMBIE			( EntityType.PIG_ZOMBIE,			"Zombie Pigman",		"minecraft:zombie_pigman",			Environment.NETHER ),
	PLAYER				( EntityType.PLAYER,				"Player",				null								),
	POLAR_BEAR			( EntityType.POLAR_BEAR,			"Polar Bear",			"minecraft:polar_bear",				Environment.NORMAL ),
	PRIMED_TNT			( EntityType.PRIMED_TNT,			"Primed TNT",			"minecraft:tnt"						),
	RABBIT				( EntityType.RABBIT,				"Rabbit",				"minecraft:rabbit",					Environment.NORMAL ),
	SHEEP				( EntityType.SHEEP,					"Sheep",				"minecraft:sheep",					Environment.NORMAL ),
	SHULKER				( EntityType.SHULKER,				"Shulker",				"minecraft:shulker",				Environment.THE_END ),
	SHULKER_BULLET		( EntityType.SHULKER_BULLET,		"Shulker Bullet",		"minecraft:shulker_bullet"			),
	SILVERFISH			( EntityType.SILVERFISH,			"Silverfish",			"minecraft:silverfish",				Environment.NORMAL ),
	SKELETON			( EntityType.SKELETON,				"Skeleton",				"minecraft:skeleton",				Environment.NORMAL ),
	SKELETON_HORSE		( EntityType.SKELETON_HORSE,		"Skeleton Horse",		"minecraft:skeleton_horse",			Environment.NORMAL ),
	SLIME				( EntityType.SLIME,					"Slime",				"minecraft:slime",					Environment.NORMAL ),
	SMALL_FIREBALL		( EntityType.SMALL_FIREBALL,		"Small Fireball",		"minecraft:small_fireball"			),
	SNOWBALL			( EntityType.SNOWBALL,				"Snowball",				"minecraft:snowball"				),
	SNOWMAN				( EntityType.SNOWMAN,				"Snowman",				"minecraft:snowman",				Environment.NORMAL ),
	SPECTRAL_ARROW		( EntityType.SPECTRAL_ARROW,		"Spectral Arrow",		"minecraft:spectral_arrow"			),
	SPIDER				( EntityType.SPIDER,				"Spider",				"minecraft:spider",					Environment.NORMAL ),
	SPLASH_POTION		( EntityType.SPLASH_POTION,			"Splash Potion",		"minecraft:potion"					),
	SQUID				( EntityType.SQUID,					"Squid",				"minecraft:squid",					Environment.NORMAL ),
	STRAY				( EntityType.STRAY,					"Stray",				"minecraft:stray",					Environment.NORMAL ),
	THROWN_EXP_BOTTLE	( EntityType.THROWN_EXP_BOTTLE,		"Thrown Exp Bottle",	"minecraft:xp_bottle"				),
	TIPPED_ARROW		( EntityType.TIPPED_ARROW,			"Tipped Arrow",			null								),
	UNKNOWN				( EntityType.UNKNOWN,				"Unknown",				null								),
	VEX					( EntityType.VEX,					"Vex",					"minecraft:vex",					Environment.NORMAL ),
	VILLAGER			( EntityType.VILLAGER,				"Villager",				"minecraft:villager",				Environment.NORMAL ),
	VINDICATOR			( EntityType.VINDICATOR,			"Vindicator",			"minecraft:vindication_illager",	Environment.NORMAL ),
	WEATHER				( EntityType.WEATHER,				"Weather (?!)",			null								),
	WITCH				( EntityType.WITCH,					"Witch",				"minecraft:witch",					Environment.NORMAL ),
	WITHER				( EntityType.WITHER,				"Wither",				"minecraft:wither"					),
	WITHER_SKELETON		( EntityType.WITHER_SKELETON,		"Wither Skeleton",		"minecraft:wither_skeleton",		Environment.NETHER ),
	WITHER_SKULL		( EntityType.WITHER_SKULL,			"Wither Skull",			"minecraft:wither_skull"			),
	WOLF				( EntityType.WOLF,					"Wolf",					"minecraft:wolf",					Environment.NORMAL ),
	ZOMBIE				( EntityType.ZOMBIE,				"Zombie",				"minecraft:zombie",					Environment.NORMAL ),
	ZOMBIE_HORSE		( EntityType.ZOMBIE_HORSE,			"Zombie Horse",			"minecraft:zombie_horse",			Environment.NORMAL ),
	ZOMBIE_VILLAGER		( EntityType.ZOMBIE_VILLAGER,		"Zombie Villager",		"minecraft:zombie_villager",		Environment.NORMAL );
	
	public final EntityType type;
	public final String name;
	public final String identifier;
	public final Environment[] dimensions;
	
	private EntitySpawnerType(EntityType type, String name, String identifier, Environment...dimensions) {
		
		this.type = type;
		this.name = name;
		this.identifier = identifier;
		this.dimensions = dimensions;
		
	}
	
	public boolean validDimension(World world) {
		
		if ( this.dimensions.length == 0 ) return true;
		
		Environment env = world.getEnvironment();
		
		for ( Environment e : this.dimensions )
			if ( e == env )
				return true;
		
		return false;
		
	}
	
	public static EntitySpawnerType getEntitySpawnerType(EntityType type) {
		EntitySpawnerType spawnerType = EntitySpawnerType.valueOf( type.name() );
		return spawnerType == null ? EntitySpawnerType.UNKNOWN : spawnerType;
	}
	
	public static EntitySpawnerType getEntitySpawnerType(String identifier) {
		for ( EntitySpawnerType spawnerType : EntitySpawnerType.values() )
			if ( identifier.equals( spawnerType.identifier ) )
				return spawnerType;
		return EntitySpawnerType.UNKNOWN;
	}
	
}

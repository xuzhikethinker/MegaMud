package pl.edu.agh.megamud.base;

import java.util.LinkedList;
import java.util.List;

import pl.edu.agh.megamud.GameServer;

/**
 * Abstraction of a in-server module. A module loads locations, NPCs, new items etc.
 * @todo Admin commands for loading/unloading modules.
 * @author Tomasz
 */
public abstract class Module {
	private List<Location> installedLocations=null;
	private List<NPCController> installedNpcs=null;
	private CommandCollector installedCommands=null;
	
	/**
	 * Returns module name.
	 * @return
	 */
	public abstract String getId();
	/**
	 * Returns human-readable module description.
	 * @return
	 */
	public abstract String getDescription();
	
	/**
	 * Use this to install a module into game server.
	 * Order of execution:
	 * - installCommands;
	 * - installLocations;
	 * - installNpcs.
	 * Then the module is loaded.
	 * @param gs
	 */
	public final void install(){
		GameServer gs=GameServer.getInstance();
		gs.addModule(this);
		
		this.installedCommands=new CommandCollector();
		this.installedLocations=new LinkedList<Location>();
		this.installedNpcs=new LinkedList<NPCController>();
		
		this.init();
	}
	
	/**
	 * Use this any time to uninstall the module.
	 */
	public final void uninstall(){
		GameServer gs=GameServer.getInstance();
		
		gs.removeModule(this);
		
		if(this.installedNpcs!=null){
			for(NPCController nc:this.installedNpcs){
				gs.killController(nc);
			}
		}
		
		if(this.installedCommands!=null){
			while(this.installedCommands.getAllCommands().size()>0){
				Command c=this.installedCommands.getAllCommands().get(0);
				c.uninstall();
			}
		}
		
		if(this.installedLocations!=null){
			for(Location loc:this.installedLocations){
				gs.removeLocation(loc);
			}
		}
		
		this.installedNpcs=null;
		this.installedCommands=null;
		this.installedLocations=null;
	}
	
	/**
	 * Use this to find a specific command from this module.
	 * @param id
	 * @return
	 */
	public final List<Command> findCommands(String id){
		return installedCommands.findCommands(id);
	}
	
	/**
	 * Use this to initialize:
	 * - commands - installCommand;
	 * - locations - installLocation;
	 * - NPCS - installNPC;
	 */
	protected abstract void init();
	
	protected final void installCommand(Command c){
		c.installTo(this.installedCommands);
		c.installTo(GameServer.getInstance().getCommands());
	}
	
	protected final void installLocation(Location loc){
		this.installedLocations.add(loc);
		GameServer.getInstance().addLocation(loc);
	}
	
	protected final void installNPC(NPCController bot,Creature creature,Location loc){
		GameServer.getInstance().initController(bot);
		GameServer.getInstance().initCreature(bot,creature);
		
		creature.setLocation(loc,null);
		
		this.installedNpcs.add(bot);
		
	}
	
	/**
	 * Executed after creation of a new controller (either new incoming client, or new NPC).
	 * @param c
	 */
	public void onNewController(Controller c){}
	
	/**
	 * Executed after a controller destroyed (client disconnected or NPC removed).
	 * @param c
	 */
	public void onKillController(Controller c){}
	
	/**
	 * Executed after creation of a new creature appeared in game.
	 * @param c
	 */
	public void onNewCreature(Creature c){}
	
	/**
	 * Executed after a creature destroyed.
	 * @param c
	 */
	public void onKillCreature(Creature c){}
}

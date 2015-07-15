package classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.ScoreboardManager;

public class Listeners
  implements Listener
{
  private main plugin;
  public Listeners(main main)
  {
    this.plugin = main;
    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
  }
  
  @EventHandler
  public void onFeed(FoodLevelChangeEvent e)
  {
    if ((e.getEntity() instanceof Player))
    {
      Player p = (Player)e.getEntity();
      if (this.plugin.inJump.contains(p.getName())) {
        e.setCancelled(true);
      }
    }
  }
  
  @EventHandler
  public void onItemDrop(PlayerDropItemEvent e)
  {
    if (this.plugin.inJump.contains(e.getPlayer().getName())) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onItemPickup(PlayerPickupItemEvent e)
  {
    if (this.plugin.inJump.contains(e.getPlayer().getName())) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onCommandDeny(PlayerCommandPreprocessEvent e)
  {
    if ((this.plugin.inJump.contains(e.getPlayer().getName())) && 
      (!e.getMessage().contains("/jumpgame")))
    {
      e.setCancelled(true);
      e.getPlayer().sendMessage(this.plugin.prefix + "That Command is not allowed in the arena!");
    }
  }
  
  @EventHandler
  public void onBlockBreak(BlockBreakEvent e)
  {
    if (this.plugin.inJump.contains(e.getPlayer().getName())) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onBlockPlace(BlockPlaceEvent e)
  {
    if (this.plugin.inJump.contains(e.getPlayer().getName())) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onQuit(PlayerQuitEvent e)
  {
    if (this.plugin.inJump.contains(e.getPlayer().getName())) {
    	sbclear(e.getPlayer());
      this.plugin.leaveArena(e.getPlayer());
    }
  }
  
  @EventHandler
  public void onDamage(EntityDamageEvent e)
  {
    if (e.getEntity() instanceof Player)
    {
      Player p = (Player)e.getEntity();
      if (this.plugin.inJump.contains(p.getName())) {
        e.setCancelled(true);
      }
    }
  }
  @EventHandler
  public void onSignChange(SignChangeEvent e){
	  Player p = e.getPlayer();
	  FileConfiguration cfg = this.plugin.getConfig();
	   if (!this.plugin.inJump.contains(e.getPlayer().getName())) {
		 Set<String>tmp = cfg.getConfigurationSection("spawns.").getKeys(false);
		    for(String s: tmp)
		   if(e.getLine(0).contains("[JumpGame]") &&
			  e.getLine(1).contains(s)){
			   e.setLine(0, "§8[§6Jump§3Game§8]");
			   e.setLine(2, "" );
			   e.setLine(3, "§2Join");
		   }
	   }else{
		   p.sendMessage(plugin.prefix + ChatColor.RED + "You cant do this now");
	   }
		   
  }
  
  @EventHandler
  public void onInteract(PlayerInteractEvent e)
  {
   if (this.plugin.inJump.contains(e.getPlayer().getName())) {
    Player p = e.getPlayer();
		if (e.getPlayer().getItemInHand().getType() == Material.STICK && e.getPlayer().getItemInHand().getItemMeta().getDisplayName() == "§2PlayerHider")
	      {
	        if (e.getAction() == Action.LEFT_CLICK_AIR ||
	            e.getAction() == Action.LEFT_CLICK_BLOCK ||
	            e.getAction() == Action.RIGHT_CLICK_AIR ||
	            e.getAction() == Action.RIGHT_CLICK_BLOCK)
		        {
		        	hideall(p);
		        }
	      }
		else if(e.getPlayer().getItemInHand().getType() == Material.BLAZE_ROD && e.getPlayer().getItemInHand().getItemMeta().getDisplayName() == "§2PlayerHider")
		{
	        if (e.getAction() == Action.LEFT_CLICK_AIR ||
		            e.getAction() == Action.LEFT_CLICK_BLOCK ||
		            e.getAction() == Action.RIGHT_CLICK_AIR ||
		            e.getAction() == Action.RIGHT_CLICK_BLOCK)
			        {
			        	showall(p);
			        }
			
		}
   }
	if(e.getClickedBlock().getType().equals(Material.WALL_SIGN)){
		 if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getState() instanceof Sign){
				Player p = e.getPlayer();
			    String ArenaPath = "PlayerInfo." + p.getName() + ".Arena";
			    String PlayerExpPath = "PlayerInfo."+ p.getPlayer().getName() + ".Experience";
				Sign s = (Sign) e.getClickedBlock().getState();
				FileConfiguration cfg = this.plugin.getConfig();
				
	    		  Set<String>tmp = cfg.getConfigurationSection("spawns.").getKeys(false);
	    		    for(String are: tmp)
	    		    	
				if(s.getLine(1).contains(are)){
					String Arena = s.getLine(1);
					if(cfg.getInt(PlayerExpPath) >= cfg.getInt("spawns." + Arena + ".experience")){
	  		              World w = Bukkit.getServer().getWorld(cfg.getString("spawns." + Arena + ".world"));
	  		              double x = cfg.getDouble("spawns." + Arena + ".x");
	  		              double y = cfg.getDouble("spawns." + Arena + ".y");
	  		              double z = cfg.getDouble("spawns." + Arena + ".z");
	  		              this.plugin.oldLoc.put(p.getName(), p.getLocation());
	  		              
	  		        	  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	  		        		  private Player player;
	  		        		  private World world;
	  		        		  private double xpos;
	  		        		  private double ypos;
	  		        		  private double zpos;
	  		        		  public void run(){
	  		        			player.teleport(new Location(world, xpos, ypos, zpos)); 
	  		        			  }
	  		            	  private Runnable init(Player p, World w, double x, double y, double z){
	  		            		  player = p;
	  		            		  world = w;
	  		            		  xpos = x;
	  		            		  ypos = y;
	  		            		  zpos = z;
	  		            		  return this;
	  		            	  }
	  		        	  }.init(p,w,x,y,z)
	  		        	  , 5L);
	  		              
	  		              cfg.set("PlayerInfo." + p.getPlayer().getName() + ".checkpoints." + Arena + ".world", p.getLocation().getWorld().getName());
	  		              cfg.set("PlayerInfo." + p.getPlayer().getName() + ".checkpoints." + Arena + ".x", Double.valueOf(p.getLocation().getX()));
	  		              cfg.set("PlayerInfo." + p.getPlayer().getName() + ".checkpoints." + Arena + ".y", Double.valueOf(p.getLocation().getY()));
	  		              cfg.set("PlayerInfo." + p.getPlayer().getName() + ".checkpoints." + Arena + ".z", Double.valueOf(p.getLocation().getZ()));
	  		              
	  		              this.plugin.inJump.add(p.getName());
	  		              cfg.set(ArenaPath, Arena);
	  		              
	  		              this.plugin.oldItems.put(p.getName(), p.getInventory().getContents());
	  		              p.getInventory().clear();
	  		              p.updateInventory();
	  		              ItemStack itemstack = new ItemStack(Material.STICK, 1);
	  		              ItemMeta im = itemstack.getItemMeta();
	  		              im.setDisplayName( "§2PlayerHider");
	  		              itemstack.setItemMeta(im);
	  		              p.getInventory().addItem(itemstack);
	  		              p.updateInventory();
	  		              
	  		              p.setGameMode(GameMode.ADVENTURE);
	  		              
	  		              plugin.failsact.put(p.getName(), 0);
	  		              
	  		              plugin.timemin.put(p.getName(), 0);
	  		          	  plugin.timer.put(p.getName(), 0);
	  		              plugin.Stopwatch(p);

	  		              
	  		              p.sendMessage(plugin.prefix + ChatColor.GREEN + "Teleported to " + Arena + "!");
	  		              
	  		              
	  		              this.plugin.saveConfig();
	  		              if(cfg.get("PlayerInfo." + p.getPlayer().getName() + ".Experience") == null){
	  		            	  cfg.set("PlayerInfo." + p.getPlayer().getName() + ".Experience", 0);
	  		              }
	  	              }
	  	            	else {
	  	            		p.sendMessage(plugin.prefix + ChatColor.AQUA + "You don´t have enough experience to start this parkour!");
	  	            		p.sendMessage(plugin.prefix + ChatColor.AQUA + "You need "+ ChatColor.GREEN + cfg.getInt("spawns." + Arena + ".experience") + " experience" + ChatColor.AQUA +  " to start this parkour.");
	  	            		p.sendMessage(plugin.prefix + ChatColor.AQUA + "You have " + ChatColor.GREEN + cfg.getInt(PlayerExpPath) +" experience"+ ChatColor.AQUA +".");
	  	            	}
					}
			 }
		}
	
     }else if(e.getClickedBlock().getType().equals(null)){
    	 System.out.println("§aTest");
     }
   }
  
  
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e)
  {
    if (this.plugin.inJump.contains(e.getPlayer().getName()))
    {
      FileConfiguration cfg = this.plugin.getConfig();
      Player p = e.getPlayer();
      String ArenaPath = "PlayerInfo." + p.getName() + ".Arena";
      String PlayerExpPath = "PlayerInfo."+ p.getPlayer().getName() + ".Experience";
      Material m = p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType();
      
      if ((m.equals(Material.WATER)) || (m.equals(Material.STATIONARY_WATER)) || (m.equals(Material.LAVA)) || (m.equals(Material.STATIONARY_LAVA)))
      {
    	  
        teleportToCheckpoint(p);
        p.sendMessage(ChatColor.AQUA + "Better luck next time!");
        
        Integer newfails = new Integer(plugin.failsact.get(p.getName()) + 1);
		plugin.failsact.put(p.getName(), newfails);
        
          plugin.setupScoreboard(p);
      }
      
      else if(m == Material.DIAMOND_BLOCK){
    	  if(!this.plugin.finished.contains(e.getPlayer().getName())){
        	  this.plugin.finished.add(e.getPlayer().getName());
        	  p.sendMessage(plugin.prefix + ChatColor.GREEN + "Congratulations! You have finished the arena.");
        	  RocketLauncher(e.getPlayer());
        	  if(cfg.get("PlayerInfo." + p.getName() + ".failsoverall") == null){
        	  cfg.set("PlayerInfo." + p.getName() + ".failsoverall", plugin.failsact.get(p.getName()) );
        	  }
        	  else{
        		  int failsoa = plugin.failsact.get(p.getName()) + cfg.getInt("PlayerInfo." + p.getName() + ".failsoverall");
        		  cfg.set("PlayerInfo." + p.getName() + ".failsoverall", failsoa);
        	  }
	  		  plugin.failsact.put(p.getName(), 0);
        	  
        	  p.sendMessage(plugin.prefix + ChatColor.GREEN + "You earned " + ChatColor.GOLD + cfg.getInt("spawns." + cfg.getString(ArenaPath) + ".reward") + " experience.");
        	  int newxpstand = cfg.getInt("spawns." + cfg.getString(ArenaPath) + ".reward") + cfg.getInt(PlayerExpPath);
        	  cfg.set(PlayerExpPath , newxpstand);
        	  
        	  
        	  
        	  int Timesek = this.plugin.timer.get(p.getName());
        	  int Timemin = this.plugin.timemin.get(p.getName());
        	  
    	  
	    	  if(cfg.getInt("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".min") != 0){
	    		if(cfg.getInt("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".sek") != 0){
		    	  if(Timemin< cfg.getInt("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".min")){
	    			  p.sendMessage(plugin.prefix + ChatColor.GREEN + "New Record! Time: " + ChatColor.GOLD + this.plugin.timemin.get(p.getName())+ "min" +this.plugin.timer.get(p.getName())+ "sek");
	            	  cfg.set("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".min", Timemin );
	            	  cfg.set("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".sek", Timesek );
	        	  }
		        	  else if(Timemin == cfg.getInt("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".min")){
		        		  if(Timesek< cfg.getInt("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".sek")){
		        			  p.sendMessage(plugin.prefix + ChatColor.GREEN + "New Record! Time: " + ChatColor.GOLD + this.plugin.timemin.get(p.getName())+ "min" +this.plugin.timer.get(p.getName())+ "sek");
		                	  cfg.set("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".min", Timemin );
		                	  cfg.set("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".sek", Timesek );
		        		  }else{
		            		  p.sendMessage(plugin.prefix + ChatColor.GREEN + "You finished the arena with a time of: " + ChatColor.GOLD + this.plugin.timemin.get(p.getName())+ "min" +this.plugin.timer.get(p.getName())+ "sek");
		            		  p.sendMessage(plugin.prefix + ChatColor.GRAY + "No new record.");
		        		  }
		    		  }
	    	     }else{
           		  p.sendMessage(plugin.prefix + ChatColor.GREEN + "You finished the arena with a time of: " + ChatColor.GOLD + this.plugin.timemin.get(p.getName())+ "min" +this.plugin.timer.get(p.getName())+ "sek");
           		  p.sendMessage(plugin.prefix + ChatColor.GRAY + "No new record.");
	    	     }
	    		
        	  

        	  }else{
        		  if(cfg.getInt("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".min") == 0){
        			  if(cfg.getInt("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".sek") == 0){
        	  			  p.sendMessage(plugin.prefix + ChatColor.GREEN + "Finished the arena for the first time! Time: " + ChatColor.GOLD + this.plugin.timemin.get(p.getName())+ "min" +this.plugin.timer.get(p.getName())+ "sek");
        	        	  cfg.set("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".min", Timemin );
        	        	  cfg.set("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".sek", Timesek );
        		      }else if(Timesek< cfg.getInt("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".sek")){
	        			  p.sendMessage(plugin.prefix + ChatColor.GREEN + "New Record! Time: " + ChatColor.GOLD + this.plugin.timemin.get(p.getName())+ "min" +this.plugin.timer.get(p.getName())+ "sek");
	                	  cfg.set("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".min", Timemin );
	                	  cfg.set("PlayerInfo." + p.getName() + ".Times." +cfg.getString(ArenaPath) + ".sek", Timesek );
	        		  }else{
	               		  p.sendMessage(plugin.prefix + ChatColor.GREEN + "You finished the arena with a time of: " + ChatColor.GOLD + this.plugin.timemin.get(p.getName())+ "min" +this.plugin.timer.get(p.getName())+ "sek");
	               		  p.sendMessage(plugin.prefix + ChatColor.GRAY + "No new record.");  
	        		  }
        	      }else{
               		  p.sendMessage(plugin.prefix + ChatColor.GREEN + "You finished the arena with a time of: " + ChatColor.GOLD + this.plugin.timemin.get(p.getName())+ "min" +this.plugin.timer.get(p.getName())+ "sek");
               		  p.sendMessage(plugin.prefix + ChatColor.GRAY + "No new record.");  
        	      }
        	  }
        	  

        	  this.plugin.saveConfig();
        	  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
        		  private Player player;
        		  public void run(){
        			  plugin.leaveArena(player);  
        			  }
            	  private Runnable init(Player p){
            		  player = p;
            		  return this;
            	  }
        	  }.init(p)
        	  , 5*20L);
          }
       }
    	  

      
      else if (m == Material.GOLD_BLOCK)
      {
    	  
    	 int XPosIntCP = (int) (cfg.getDouble("PlayerInfo." + p.getName()+".checkpoints." + cfg.getString(ArenaPath) + ".x"));
    	 double XPosAct1 = Double.valueOf(p.getLocation().getX());
    	 int XPosIntAct2 = (int) XPosAct1;
    	 
    	 if(XPosIntCP != XPosIntAct2){
        	 int YPosIntCP = (int) (cfg.getDouble("PlayerInfo." + p.getName()+".checkpoints." + cfg.getString(ArenaPath) + ".y"));
        	 double YPosAct1 = Double.valueOf(p.getLocation().getY());
        	 int YPosIntAct2 = (int) YPosAct1;
        	 
        	 if(YPosIntCP != YPosIntAct2){
            	 int ZPosIntCP = (int) (cfg.getDouble("PlayerInfo." + p.getName()+".checkpoints." + cfg.getString(ArenaPath) + ".z"));
            	 double ZPosAct1 = Double.valueOf(p.getLocation().getZ());
            	 int ZPosIntAct2 = (int) ZPosAct1;
            	 
            	 if(ZPosIntCP != ZPosIntAct2){
            	        cfg.set("PlayerInfo." + p.getName() + ".checkpoints." + cfg.getString(ArenaPath) + ".world" , p.getLocation().getWorld().getName());
            	        cfg.set("PlayerInfo." + p.getName() + ".checkpoints." + cfg.getString(ArenaPath) + ".x" , Double.valueOf(p.getLocation().getX()));
            	        cfg.set("PlayerInfo." + p.getName() + ".checkpoints." + cfg.getString(ArenaPath) + ".y" , Double.valueOf(p.getLocation().getY()));
            	        cfg.set("PlayerInfo." + p.getName() + ".checkpoints." + cfg.getString(ArenaPath) + ".z" , Double.valueOf(p.getLocation().getZ()));

            	        p.sendMessage(plugin.prefix + ChatColor.GREEN + "Checkpoint set!");
            	        this.plugin.saveConfig();
            	 }
        	 }
    	 }
      }
    }
  }

  public void teleportToCheckpoint(Player p)
  {
	  FileConfiguration cfg = this.plugin.getConfig();
	  String ArenaPath = "PlayerInfo." + p.getName() + ".Arena";
	  Set<String>tmp = cfg.getConfigurationSection("PlayerInfo." + p.getName() + ".checkpoints.").getKeys(false);
	    for(String s: tmp)if (cfg.getString(ArenaPath).equals(s)){	
	    String world = cfg.getString("PlayerInfo." + p.getName() + ".checkpoints." + cfg.get("PlayerInfo." + p.getName() + ".Arena") + ".world");
	    double x = cfg.getDouble("PlayerInfo." + p.getName() + ".checkpoints." + cfg.get("PlayerInfo." + p.getName() + ".Arena") + ".x");
	    double y = cfg.getDouble("PlayerInfo." + p.getName() + ".checkpoints." + cfg.get("PlayerInfo." + p.getName() + ".Arena") + ".y");
	    double z = cfg.getDouble("PlayerInfo." + p.getName() + ".checkpoints." + cfg.get("PlayerInfo." + p.getName() + ".Arena") + ".z");
	    Location loc = new Location(Bukkit.getWorld(world), x, y, z);
	    
	    p.teleport(loc);
    }
  }

		
		// Thanks to Minken for providing this code at the Bukkit forums
        private Random random = new Random();

  private void RocketLauncher(Player player)
	    {
	    int type = (int)(Math.random()*5)+1;
	 
	    Type typen = Type.BALL;
	    if (type == 1) typen = Type.BALL;
	    if (type == 2) typen = Type.BALL_LARGE;
	    if (type == 3) typen = Type.BURST;
	    if (type == 4) typen = Type.CREEPER;
	    if (type == 5) typen = Type.STAR;
	 
	    Firework fireworks = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
	    FireworkMeta fireworkmeta = fireworks.getFireworkMeta();
	    FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(colorchoose()).withFade(colorchoose()).with(typen).trail(random.nextBoolean()).build();
	    fireworkmeta.addEffect(effect);
	    fireworkmeta.setPower(1);
	    fireworks.setFireworkMeta(fireworkmeta);
	}
	 
	private List<Color> colorchoose()
	{
	    // Thanks Zomis and Tejpbit for the help with this function!
	 
	    int numberofcolors = random.nextInt(17) + 1;
	 
	    List<Color> allcolors = new ArrayList<Color>();
	    allcolors.add(Color.AQUA);
	    allcolors.add(Color.BLACK);
	    allcolors.add(Color.BLUE);
	    allcolors.add(Color.FUCHSIA);
	    allcolors.add(Color.GRAY);
	    allcolors.add(Color.GREEN);
	    allcolors.add(Color.LIME);
	    allcolors.add(Color.MAROON);
	    allcolors.add(Color.NAVY);
	    allcolors.add(Color.OLIVE);
	    allcolors.add(Color.ORANGE);
	    allcolors.add(Color.PURPLE);
	    allcolors.add(Color.RED);
	    allcolors.add(Color.SILVER);
	    allcolors.add(Color.TEAL);
	    allcolors.add(Color.WHITE);
	    allcolors.add(Color.YELLOW);
	 
	    List<Color> choosencolors = new ArrayList<Color>();
	 
	    for (int i = 0; i < numberofcolors; i++)
	    {
	        choosencolors.add(allcolors.remove(random.nextInt(allcolors.size())));
	    }
	    return choosencolors;            
	}

	public void showall(Player p){
		if(!plugin.spam.contains(p.getName())){
			if(plugin.hiders.contains(p.getName())){
	            for (Player players : Bukkit.getOnlinePlayers())
	            {
	            p.showPlayer(players);
	            }
		        this.plugin.hiders.remove(p.getName());
	            p.sendMessage(plugin.prefix + ChatColor.AQUA + "Other players are now visible!");
	            this.plugin.spam.add(p.getName());
	        	  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	        		  private Player player;
	        		  public void run(){
	        			  plugin.spam.remove(player.getName());
	        			  }
	            	  private Runnable init(Player p){
	            		  player = p;
	            		  return this;
	            	  }
	        	  }.init(p)
	        	  , 5*20L);
	              p.getInventory().clear();
	              ItemStack itemstack = new ItemStack(Material.STICK, 1);
	              ItemMeta im = itemstack.getItemMeta();
	              im.setDisplayName( "§2PlayerHider");
	              itemstack.setItemMeta(im);
	              p.getInventory().addItem(itemstack);
	              p.updateInventory();
			}
		}
		else
		{
			p.sendMessage(plugin.prefix + ChatColor.RED + "Do not spam this item!");
		}
	}
public void hideall(Player p){
	if(!plugin.spam.contains(p.getName())){
			{
		        for (Player players : Bukkit.getOnlinePlayers())
		        {
		        p.hidePlayer(players);
		        }
	            this.plugin.hiders.add(p.getName());
	            p.sendMessage(plugin.prefix + ChatColor.AQUA + "Other players are no longer visible!");
	            this.plugin.spam.add(p.getName());
	        	  plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	        		  private Player player;
	        		  private main pl;
	        		  public void run(){
	        			  pl.spam.remove(player.getName());  
	        			  }
	            	  private Runnable init(Player p, main plugin){
	            		  player = p;
	            		  pl = plugin;
	            		  return this;
	            	  }
	        	  }.init(p, plugin)
	        	  , 5*20L);
	              p.getInventory().clear();
	              ItemStack itemstack = new ItemStack(Material.BLAZE_ROD, 1);
	              ItemMeta im = itemstack.getItemMeta();
	              im.setDisplayName( "§2PlayerHider");
	              itemstack.setItemMeta(im);
	              p.getInventory().addItem(itemstack);
	              p.updateInventory();
	        }
		}
		else{
			p.sendMessage(plugin.prefix + ChatColor.RED + "Do not spam this item!");
		}
	}
	 public void sbclear(Player p){
		 ScoreboardManager manager = Bukkit.getScoreboardManager();
		    p.setScoreboard(manager.getNewScoreboard());
	 }
}
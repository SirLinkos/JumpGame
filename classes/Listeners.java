package classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
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
      if (m == Material.SPONGE)
      {
    	  
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 3));
        p.playSound(p.getLocation(), Sound.FIREWORK_BLAST2, 15.0F, 2.0F);
      }
      
      else if ((m.equals(Material.WATER)) || (m.equals(Material.STATIONARY_WATER)) || (m.equals(Material.LAVA)) || (m.equals(Material.STATIONARY_LAVA)))
      {
    	  
        teleportToCheckpoint(p);
        p.sendMessage(ChatColor.AQUA + "Better luck next time!");
        
	  	  if(cfg.get("PlayerInfo." + p.getName() + ".failsact") == null){
	  	  cfg.set("PlayerInfo." + p.getName() + ".failsact", 0);
	  	  }
	  	  else{
	  		  int fails = cfg.getInt("PlayerInfo." + p.getName() + ".failsact");
	  		  int failsnew = fails + 1;
	  		  cfg.set("PlayerInfo." + p.getName() + ".failsact", failsnew);
	  	  }
          setupScoreboard(p);
      }
      
      else if(m == Material.DIAMOND_BLOCK){
    	  if(!this.plugin.finished.contains(e.getPlayer().getName())){
        	  this.plugin.finished.add(e.getPlayer().getName());
        	  p.sendMessage(plugin.prefix + ChatColor.GREEN + "Congratulations! You have finished the arena.");
        	  RocketLauncher(e.getPlayer());
        	  if(cfg.get("PlayerInfo." + p.getName() + ".failsoverall") == null){
        	  cfg.set("PlayerInfo." + p.getName() + ".failsoverall", cfg.getInt("PlayerInfo." + p.getName() + ".failsact") );
        	  }
        	  else{
        		  int failsoa = cfg.getInt("PlayerInfo." + p.getName() + ".failsact") + cfg.getInt("PlayerInfo." + p.getName() + ".failsoverall");
        		  cfg.set("PlayerInfo." + p.getName() + ".failsoverall", failsoa);
        	  }
	  		  cfg.set("PlayerInfo." + p.getName() + ".failsact", 0);
        	  
        	  p.sendMessage(plugin.prefix + ChatColor.GREEN + "You earned " + ChatColor.GOLD + cfg.getInt("spawns." + cfg.getString(ArenaPath) + ".reward") + " experience.");
        	  int newxpstand = cfg.getInt("spawns." + cfg.getString(ArenaPath) + ".reward") + cfg.getInt(PlayerExpPath);
        	  cfg.set(PlayerExpPath , newxpstand);
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
	    int power = (int)(Math.random()*3)+1;
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
	    fireworkmeta.setPower(power);
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
	 
	   public void setupScoreboard(Player p){
			 FileConfiguration cfg = this.plugin.getConfig();
			    ScoreboardManager manager = Bukkit.getScoreboardManager();
			    Scoreboard board = manager.getNewScoreboard();
		 
			     String none = "§r";
			     String PluginName = ChatColor.GOLD + "Jump" + ChatColor.DARK_AQUA +"Game";
			     String ArenaName = ChatColor.RED + cfg.getString("PlayerInfo." + p.getName() + ".Arena");
			     String ArenaPre = ChatColor.DARK_AQUA + "You are in the arena:";
			     String FailsPre = ChatColor.DARK_AQUA + "Times you failed:";
			     String Fails = ChatColor.RED + "" + cfg.getInt("PlayerInfo." + p.getName() + ".failsact");
			     Objective objective = board.registerNewObjective("test", "SideBoard");
			     objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			     objective.setDisplayName(ChatColor.BOLD + "§2Zwergen§3Craft");
			     Score top = objective.getScore(none);
			     top.setScore(99);
			    Score name = objective.getScore(PluginName);
			    name.setScore(98);
			    Score none1 = objective.getScore(none);
			     none1.setScore(97);
			     Score arp1 = objective.getScore(ArenaPre);
			     arp1.setScore(96);
			    Score sited1 = objective.getScore(ArenaName);
			    sited1.setScore(95);
			    Score none2 = objective.getScore(none);
			     none2.setScore(94);
			    Score failspre1 = objective.getScore(FailsPre);
			    failspre1.setScore(93);
			    Score fails1 = objective.getScore(Fails);
			    fails1.setScore(92);
			     p.setScoreboard(board);
		}
}
package classes;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class main
  extends JavaPlugin
{
  public ArrayList<String> inJump = new ArrayList<String>();
  public ArrayList<String> finished = new ArrayList<String>();
  public ArrayList<String> hiders = new ArrayList<String>();
  public ArrayList<String> spam = new ArrayList<String>();
  public HashMap<String, Location> oldLoc = new HashMap<String, Location>();
  public HashMap<String, ItemStack[]> oldItems = new HashMap<String, ItemStack[]>();
  public HashMap<String, Integer> timer = new HashMap<String, Integer>();
  public HashMap<String, Integer>timemin = new HashMap<String, Integer>();
  public HashMap<String, Integer>failsact = new HashMap<String, Integer>();
  public String prefix = ChatColor.GRAY + "["+ ChatColor.BLUE +"JumpGame"+ ChatColor.GRAY + "] ";
  public String noperm = ChatColor.GRAY + "["+ ChatColor.BLUE +"JumpGame"+ ChatColor.GRAY + "] " + ChatColor.RED + "Seems like you dont have permission to do that!";
  public String help = ChatColor.GRAY + "["+ ChatColor.BLUE +"JumpGame"+ ChatColor.GRAY + "] " + ChatColor.BLUE + "Help: /jumpgame help";
  public String debug = ChatColor.RED + "Dubug message sent!";
  public int id = 1;
  
  
  public void onEnable()
  {
    System.out.println("[JumpGame] Plugin version " + getDescription().getVersion() + " loaded!");
    new Listeners(this);
    getCommand("jumpgame").setExecutor(new commands(this));
  }
  
  public void onDisable()
  {
    System.out.println("[JumpGame] Plugin disabled!");
    this.saveConfig();
  }
  
  public void leaveArena(Player p)
  {
    if (this.inJump.contains(p.getName()))
    {
      this.inJump.remove(p.getName());
      if(!this.finished.contains(p.getName())){
          this.finished.add(p.getName());
    	  this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
    		  private Player player;
    		  public void run(){
    			  finished.remove(player.getName()); 
    			  }
        	  private Runnable init(Player p){
        		  player = p;
        		  return this;
        	  }
    	  }.init(p)
    	  , 40L);
      }
      this.finished.remove(p.getName());
      this.spam.remove(p.getName());
      

      p.getInventory().clear();
      ItemStack[] old = (ItemStack[])this.oldItems.get(p.getName());
      p.getInventory().setContents(old);
      p.updateInventory();
      
      sbclear(p);
      
      Location loc = (Location)this.oldLoc.get(p.getName());
      p.teleport(loc);
      
      FileConfiguration cfg = getConfig();
      cfg.set("PlayerInfo." + p.getName() + ".Arena", "Is not playing!");
      saveConfig();

      p.sendMessage(this.prefix + "§3You have left the arena!");
    }
    else
    {
      p.sendMessage(this.prefix + "§cYou are not in a jump arena!");
    }
  }
	 public void sbclear(Player p){
		 ScoreboardManager manager = Bukkit.getScoreboardManager();
		    p.setScoreboard(manager.getNewScoreboard());
  }
public void Stopwatch(Player p){
	   id = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		  private Player player;
		  public void run(){
			if(inJump.contains(player.getName())){  
			  if(finished.contains(player.getName())){
				  Bukkit.getServer().getScheduler().cancelTask(id);
			  }else{
				  Integer timenewsek = new Integer(timer.get(player.getName()) + 1);
				  timer.put(player.getName(), timenewsek);
				  if(timenewsek>= 60){
					  Integer timenewmin = new Integer(timemin.get(player.getName()) + 1);
					  timemin.put(player.getName(), timenewmin);
					  timer.put(player.getName(), 0);
				  }
				  setupScoreboard(player);
			  }

		  }else{
			  Bukkit.getServer().getScheduler().cancelTask(id);
		  }
	}
  	  private Runnable init(Player p){
  		  player = p;
  		  return this;
  	  }
	  }.init(p)
	  ,0L ,20L);
}

public void setupScoreboard(Player p){
		 FileConfiguration cfg = this.getConfig();
		    ScoreboardManager manager = Bukkit.getScoreboardManager();
		    Scoreboard board = manager.getNewScoreboard();
	 
		     String none = "§r";
		     String ArenaName = ChatColor.RED + cfg.getString("PlayerInfo." + p.getName() + ".Arena");
		     String ArenaPre = ChatColor.DARK_AQUA + "You are in the arena:";
		     String FailsPre = ChatColor.DARK_AQUA + "Times you failed:";
		     String Fails = ChatColor.RED + "" + this.failsact.get(p.getName());
		     String TimerPre = ChatColor.DARK_AQUA + "Time:";
		     String Timer = ChatColor.GREEN + "" + this.timemin.get(p.getName()) + ":" + this.timer.get(p.getName()) + " sek";
		     Objective objective = board.registerNewObjective("test", "SideBoard");
		     objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		     objective.setDisplayName(ChatColor.BOLD + "§6JUMP§3GAME");
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
		    Score none3 = objective.getScore(none);
		    none3.setScore(91);
		    Score Timerpre1 = objective.getScore(TimerPre);
		    Timerpre1.setScore(90);
		    Score Timer1 = objective.getScore(Timer);
		    Timer1.setScore(89);
		     p.setScoreboard(board);
	}
}

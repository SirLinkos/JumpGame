package classes;

import java.util.ArrayList;
import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
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
  public String prefix = ChatColor.GRAY + "["+ ChatColor.BLUE +"JumpGame"+ ChatColor.GRAY + "] ";
  public String noperm = ChatColor.GRAY + "["+ ChatColor.BLUE +"JumpGame"+ ChatColor.GRAY + "] " + ChatColor.RED + "Seems like you dont have permission to do that!";
  public String help = ChatColor.GRAY + "["+ ChatColor.BLUE +"JumpGame"+ ChatColor.GRAY + "] " + ChatColor.BLUE + "Help: /jumpgame help";
  public String debug = ChatColor.RED + "Dubug message sent!";
  
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
	 
}

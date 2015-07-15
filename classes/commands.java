package classes;


import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class commands
  implements CommandExecutor
{
  private main plugin;
  
  public commands(main main)
  {
    this.plugin = main;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if ((sender instanceof Player))
    {
      Player p = (Player)sender;
      FileConfiguration cfg = this.plugin.getConfig();
      String ArenaPath = "PlayerInfo." + p.getName() + ".Arena";
      String PlayerExpPath = "PlayerInfo."+ p.getPlayer().getName() + ".Experience";
      if (args.length == 0)
      {
        sendHelp1(p);
      }
      else if (args[0].equalsIgnoreCase("help")){
	      if(p.hasPermission("jumpgame.user")){
		        sendHelp1(p);
		        return true;
		  }
	      else{
	    	  p.sendMessage(plugin.noperm);
	      }
      }
    
    else if (args[0].equalsIgnoreCase("debug")){
	      if(p.hasPermission("jumpgame.user")){
		        plugin.setupScoreboard(p);
		    	Bukkit.getServer().getScheduler().cancelTask(1);
		        return true;
		  }
	      else{
	    	  p.sendMessage(plugin.noperm);
	      }
    }
      else if(args[0].equalsIgnoreCase("info")){
    	  if(p.hasPermission("jumpgame.user")){
    		  sendinfo(p);
    	  }
      }
      else if (args[0].equalsIgnoreCase("join")){
      	if(p.hasPermission("jumpgame.user")){
	          if (args.length != 2)
	          {
	            p.sendMessage(plugin.prefix + ChatColor.RED + "Please say the name of the arena");
	          }
	          else if (!this.plugin.inJump.contains(p.getName()))
	          {
	            if (cfg.getConfigurationSection("spawns." + args[1]) == null)
	            {
	              p.sendMessage(plugin.prefix + ChatColor.RED + "Arena " + args[1] + " does not exist!");
	            }
	            else 
	            {
	            	if(cfg.getInt(PlayerExpPath) >= cfg.getInt("spawns." + args[1] + ".experience")){
		              World w = Bukkit.getServer().getWorld(cfg.getString("spawns." + args[1] + ".world"));
		              double x = cfg.getDouble("spawns." + args[1] + ".x");
		              double y = cfg.getDouble("spawns." + args[1] + ".y");
		              double z = cfg.getDouble("spawns." + args[1] + ".z");
		              this.plugin.oldLoc.put(p.getName(), p.getLocation());
		              p.teleport(new Location(w, x, y, z));
		              
		              cfg.set("PlayerInfo." + p.getPlayer().getName() + ".checkpoints." + args[1] + ".world", p.getLocation().getWorld().getName());
		              cfg.set("PlayerInfo." + p.getPlayer().getName() + ".checkpoints." + args[1] + ".x", Double.valueOf(p.getLocation().getX()));
		              cfg.set("PlayerInfo." + p.getPlayer().getName() + ".checkpoints." + args[1] + ".y", Double.valueOf(p.getLocation().getY()));
		              cfg.set("PlayerInfo." + p.getPlayer().getName() + ".checkpoints." + args[1] + ".z", Double.valueOf(p.getLocation().getZ()));
		              
		              this.plugin.inJump.add(p.getName());
		              cfg.set(ArenaPath, args[1]);
		              
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

		              
		              p.sendMessage(plugin.prefix + ChatColor.GREEN + "Teleported to " + args[1] + "!");
		              
		              
		              this.plugin.saveConfig();
		              if(cfg.get("PlayerInfo." + p.getPlayer().getName() + ".Experience") == null){
		            	  cfg.set("PlayerInfo." + p.getPlayer().getName() + ".Experience", 0);
		              }
	              }
	            	else {
	            		p.sendMessage(plugin.prefix + ChatColor.AQUA + "You don´t have enough experience to start this parkour!");
	            		p.sendMessage(plugin.prefix + ChatColor.AQUA + "You need "+ ChatColor.GREEN + cfg.getInt("spawns." + args[1] + ".experience") + " experience" + ChatColor.AQUA +  " to start this parkour.");
	            		p.sendMessage(plugin.prefix + ChatColor.AQUA + "You have " + ChatColor.GREEN + cfg.getInt(PlayerExpPath) +" experience"+ ChatColor.AQUA +".");
	            	}
	            }
	          }
	          else
	          {
	            p.sendMessage(this.plugin.prefix + "§cYou are already in a arena!");
	          }
	        }
      	else{
      		p.sendMessage(plugin.noperm);
      	}
      }
      else if (args[0].equalsIgnoreCase("leave")){
    	  if (this.plugin.inJump.contains(p.getPlayer().getName())){
	    	  if(p.hasPermission("jumpgame.user")){
		        this.plugin.leaveArena(p);
	    	  }
    	  else{
    		  p.sendMessage(plugin.noperm);
    	  }
        }
      }
      else if (args[0].equalsIgnoreCase("checkpoint")){  //teleports player to the last checkpoint
    	  if (this.plugin.inJump.contains(p.getPlayer().getName())){
	    	  if(p.hasPermission("jumpgame.user")){
	        	  teleportToCheckpoint(p);
	        	  
	              Integer newfails = new Integer(plugin.failsact.get(p.getName()) + 1);
	    		  plugin.failsact.put(p.getName(), newfails);
	        	  
	             plugin.setupScoreboard(p);
	    	  }
	    	  else{
	    		 p.sendMessage(plugin.noperm);
	    	  }
    	  }
      }
      else if (args[0].equalsIgnoreCase("xp")){    //shows the player the experience
    	  if(p.hasPermission("jumpgame.user")){
    		  p.sendMessage(plugin.prefix + ChatColor.AQUA + "You have " + ChatColor.GOLD + cfg.getInt(PlayerExpPath) + ChatColor.AQUA + " experience.");
    	  }
    	  else{
    		  p.sendMessage(plugin.noperm);
    	  }
      }
      else if (args[0].equalsIgnoreCase("fails")){    //shows the fails of the player
    	  if(p.hasPermission("jumpgame.user")){
    		  p.sendMessage(plugin.prefix + ChatColor.AQUA + "You failed " + ChatColor.GOLD + cfg.getInt("PlayerInfo." + p.getName() + ".failsoverall") + ChatColor.AQUA + " times.");
    	  }
    	  else{
    		  p.sendMessage(plugin.noperm);
    	  }
      }
      else if(args[0].equalsIgnoreCase("arenalist")){
    	  if(p.hasPermission("jumpgame.user")){
    		  Set<String>tmp = cfg.getConfigurationSection("PlayerInfo." + p.getName() + ".checkpoints.").getKeys(false);
    		  p.sendMessage(plugin.prefix + ChatColor.AQUA + "List of all arenas:");
    		    for(String s: tmp) p.sendMessage(plugin.prefix + ChatColor.GOLD + s);
    	  }
    	  else{
    		  p.sendMessage(plugin.noperm);
    	  }
      }
      else if(args[0].equalsIgnoreCase("arenainfo")){
    	  if (args.length ==2){
	    	  if(p.hasPermission("jumpgame.user")){
	    		  Set<String>tmp = cfg.getConfigurationSection("spawns.").getKeys(false);
	    		    for(String s: tmp)
	    		    if (args[1].equals(s)){	
	    		    	p.sendMessage(plugin.prefix + ChatColor.AQUA + "Arenaname: " + ChatColor.GOLD + args[1] );
	    		    	p.sendMessage(plugin.prefix + ChatColor.AQUA + "Experience required: " + ChatColor.GOLD + cfg.getInt("spawns." + args[1] + ".experience"));
	    		    	p.sendMessage(plugin.prefix + ChatColor.AQUA + "Experience reward: "+ ChatColor.GOLD + cfg.getInt("spawns." + args[1] + ".reward"));
	    		    	if(cfg.getInt("PlayerInfo." + p.getName() + ".Times." + args[1] + ".sek") == 0){
	    		    		if(cfg.getInt("PlayerInfo." + p.getName() + ".Times." + args[1] + ".min") ==0){
	    		    			p.sendMessage(plugin.prefix + ChatColor.AQUA + "No record yet.");
	    		    		}
	    		    		else{
	    		    			p.sendMessage(plugin.prefix + ChatColor.AQUA + "Your record: "+ ChatColor.GOLD + cfg.getInt("PlayerInfo." + p.getName() + ".Times." +args[1] + ".min")+ " min" +cfg.getInt("PlayerInfo." + p.getName() + ".Times." + args[1] + ".sek")+ " sek");
	    		    		}
	    		    	}else{
	    		    		p.sendMessage(plugin.prefix + ChatColor.AQUA + "Your record: "+ ChatColor.GOLD + cfg.getInt("PlayerInfo." + p.getName() + ".Times." +args[1] + ".min")+ " min" +cfg.getInt("PlayerInfo." + p.getName() + ".Times." + args[1] + ".sek")+ " sek");
	    		    	}
	    		    	
	    		    }
	    	  }
	    	  else{
	    		  p.sendMessage(plugin.noperm);
	    	  }
    	  }
    	  else{
    		  p.sendMessage(plugin.prefix + ChatColor.RED + "Please say the name of the arena");
    	  }
      }
      else if(args[0].equalsIgnoreCase("setxp")){
    	  if(p.hasPermission("jumpgame.admin")){
	    	if (args.length == 3)
	    	{
	    		int exp = Integer.parseInt(args[2]);
	    		cfg.set("spawns." + args[1] + ".experience", exp);
	    		p.sendMessage(plugin.prefix +  ChatColor.AQUA + "Experience for " + ChatColor.GREEN + args[1] + ChatColor.AQUA +" set to: " + ChatColor.GREEN + args[2]);
	    		this.plugin.saveConfig();
	    	}
	    	else{
	    		p.sendMessage(plugin.prefix + ChatColor.RED + "Usage: /jumpgame setxp <arena> <xp>");
	    	}
	      }
    	  else{
    		  p.sendMessage(plugin.noperm);
    	  }
      }
	  else if(args[0].equalsIgnoreCase("rewardxp")){
		  if(p.hasPermission("jumpgame.admin")){
	    	  if (args.length == 3){
	    		  int exp = Integer.parseInt(args[2]);
	    		  cfg.set("spawns." + args[1] + ".reward", exp);
	    		  p.sendMessage(plugin.prefix + ChatColor.AQUA + "Experience reward for "+ ChatColor.GREEN + args[1] + ChatColor.AQUA + " set to: " + ChatColor.GREEN + args[2] );
	    		  this.plugin.saveConfig();
	    	  }
	    	  else{
	    		  p.sendMessage(plugin.prefix + ChatColor.RED + "Usage: /jumpgame rewardxp <arena> <xp>");
	    	  }
	      }
		  p.sendMessage(plugin.noperm);
	  }
      else if (args[0].equalsIgnoreCase("setspawn")){
    	  if(p.hasPermission("jumpgame.admin")){
	          if (args.length == 2)
	          {
	            cfg.set("spawns." + args[1] + ".name", args[1] );
	            cfg.set("spawns." + args[1] + ".world", p.getLocation().getWorld().getName());
	            cfg.set("spawns." + args[1] + ".x", Double.valueOf(p.getLocation().getX()));
	            cfg.set("spawns." + args[1] + ".y", Double.valueOf(p.getLocation().getY()));
	            cfg.set("spawns." + args[1] + ".z", Double.valueOf(p.getLocation().getZ()));
	            
	            cfg.set("spawns." + args[1] + ".experience" , 0);
	            this.plugin.saveConfig();
	            p.sendMessage(ChatColor.GREEN + "Set arena spawn " + args[1] + "!");
	            return true;
	          }
	          p.sendMessage(ChatColor.RED + "Please specify a name!");
	          return true;
	        }
    	  else{
    		  p.sendMessage(plugin.noperm);
    	  }
      }
      else if(args[0].equalsIgnoreCase("adminhelp")){
    	  if(p.hasPermission("jumpgame.admin")){
    		  sendAdminhelp1(p);
    	  }
      }

        else if (args[0].equalsIgnoreCase("delspawn")){
        	if(p.hasPermission("jumpgame.admin")){
	          if (args.length == 0) {
	            p.sendMessage(ChatColor.RED + "Please specify a name!");
	          }
	          else if (cfg.getConfigurationSection("spawns." + args[0]) == null) {
	            p.sendMessage(ChatColor.RED + "Spawn " + args[0] + " does not exist!");
	          }
	          else{
	          cfg.set("spawns." + args[0], null);
	          this.plugin.saveConfig();
	          p.sendMessage(ChatColor.GREEN + "Removed spawn " + args[0] + "!");
	          }
	        }
	        else
	        {
	          p.sendMessage(plugin.noperm);
	        }
        
        }
    else
    {
      sender.sendMessage("You must be a Player!");
    }
   }
   return true;
    
  }
  public void sendinfo(Player p){
	    p.sendMessage(ChatColor.GRAY + "<->-<->-<->-<->(" + ChatColor.BOLD + ChatColor.GOLD + "JumpGame Info " + ChatColor.GRAY + ")<->-<->-<->-<->");
	    p.sendMessage(ChatColor.GRAY + "Version: " + ChatColor.GOLD + this.plugin.getDescription().getVersion());
	    p.sendMessage(ChatColor.GRAY + "Developer: " + ChatColor.GOLD + this.plugin.getDescription().getAuthors());
	    p.sendMessage(ChatColor.GRAY + "<->-<->-<->-<->-<->-<->-<->-<->-<->-<->-<->-<->");
  }
  
  public void sendHelp1(Player p)
  {
    p.sendMessage(ChatColor.GRAY + "[]<->-<->-<->-<->(" + ChatColor.BOLD + ChatColor.GOLD + "Page 1 " + ChatColor.GRAY + ")<->-<->-<->-<->[] ");
    p.sendMessage(ChatColor.GRAY + " /jumpgame join <arena> -" + ChatColor.AQUA + " For joining the jump arena");
    p.sendMessage(ChatColor.GRAY + " /jumpgame leave -" + ChatColor.AQUA + " To leave the arena");
    p.sendMessage(ChatColor.GRAY + " /jumpgame checkpoint - " + ChatColor.AQUA + "Teleports you to your last Checkpoint");
    p.sendMessage(ChatColor.GRAY + " /jumpgame xp - " + ChatColor.AQUA + "Shows your current experience");
    p.sendMessage(ChatColor.GRAY + " /jumpgame fails - " + ChatColor.AQUA + "Shows how often you have failed overall");
    p.sendMessage(ChatColor.GRAY + " /jumpgame info - " + ChatColor.AQUA + "Displays infos about this plugin");
    p.sendMessage(ChatColor.GRAY + " /jumpgame help -" + ChatColor.AQUA + " Displays the help message");
    p.sendMessage(ChatColor.GRAY + " /jumpgame adminhelp -" + ChatColor.AQUA + " Displays the admin-help message");
  }
  
  public void sendAdminhelp1(Player p){
	    p.sendMessage(ChatColor.GRAY + "[]<->-<->-<->-<->(" + ChatColor.BOLD + ChatColor.GOLD + "Page 1 " + ChatColor.GRAY + ")<->-<->-<->-<->[] ");
	    p.sendMessage(ChatColor.GRAY + " /jumpgame setspawn <arena> -" + ChatColor.AQUA + " Sets the spawn for a new arena");
	    p.sendMessage(ChatColor.GRAY + " /jumpgame setxp <arena> - " + ChatColor.AQUA + "Sets the required experience to join the arena");
	    p.sendMessage(ChatColor.GRAY + " /jumpgame rewardxp - " + ChatColor.AQUA + "Sets the experience granted to the players for winning");
	    p.sendMessage(ChatColor.GRAY + " /jumpgame delspawn - " + ChatColor.AQUA + "Delets a spawnpoint for an arena");
	    p.sendMessage(ChatColor.GRAY + "");
	    p.sendMessage(ChatColor.GRAY + "");
	    p.sendMessage(ChatColor.GRAY + "");
	    p.sendMessage("");
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
}

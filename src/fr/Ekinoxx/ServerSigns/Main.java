package fr.Ekinoxx.ServerSigns;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	 public ArrayList<ConnectionSign> signs;
     
     public void onEnable(){
    	    PluginManager pm = Bukkit.getPluginManager();
		    pm.registerEvents(this, this);
             signs = new ArrayList<ConnectionSign>();
            
             for (String str : getConfig().getKeys(false)) {
                     ConfigurationSection s = getConfig().getConfigurationSection(str);
                    
                     ConfigurationSection l = s.getConfigurationSection("loc");
                     World w = Bukkit.getServer().getWorld(l.getString("world"));
                     double x = l.getDouble("x"), y = l.getDouble("y"), z = l.getDouble("z");
                     Location loc = new Location(w, x, y, z);
                    
                     if (loc.getBlock() == null) {
                             getConfig().set(str, null);
                     } else {
                             signs.add(new ConnectionSign(loc, s.getString("name"), s.getString("ip"), s.getInt("port")));
                     }
             }
            
             Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                     public void run() {
                             for (ConnectionSign s : signs) {
                                     s.update();
                             }
                     }
             }, 0, 20);
             Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            
             saveDefaultConfig();
     }
     
     private void save(ConnectionSign sign) {
     int size = getConfig().getKeys(false).size() + 1;
     getConfig().set(size + ".loc.world", sign.getLocation().getWorld().getName());
     getConfig().set(size + ".loc.x", sign.getLocation().getX());
     getConfig().set(size + ".loc.y", sign.getLocation().getY());
     getConfig().set(size + ".loc.z", sign.getLocation().getZ());
     getConfig().set(size + ".name", sign.getName());
     getConfig().set(size + ".ip", sign.getIP());
     getConfig().set(size + ".port", sign.getPort());
     saveConfig();
     }
    
     @EventHandler
      public void onSignChange(SignChangeEvent e){
   
              if(e.getLine(0).equalsIgnoreCase("[TP]")){
            	  ConnectionSign connectionSign = new ConnectionSign(e.getBlock().getLocation(), e.getLine(1), e.getLine(2), Integer.parseInt(e.getLine(3)));
            	  signs.add(connectionSign);
                  save(connectionSign);
              }
     }
    
     @EventHandler
     public void onPlayerInteract(PlayerInteractEvent e) {
             if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
                     Block block = e.getClickedBlock();
                     if (block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){
                             for (ConnectionSign s : signs) {
                                     if (s.getLocation().equals(block.getLocation())) {
                                             try {
                                                     
                                            	     ByteArrayOutputStream b = new ByteArrayOutputStream();
                                                     DataOutputStream out = new DataOutputStream(b);

                                                     out.writeUTF("Connect");
                                                     out.writeUTF(s.getName());
                                                     e.getPlayer().sendMessage("Connection");
                                                    
                                                     e.getPlayer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
                                             } catch (Exception ex) {
                                            	 e.getPlayer().sendMessage("Erreur!");
                                                     ex.printStackTrace();
                                             }
                                     }
                             }
                     }
             }
     }
	
}

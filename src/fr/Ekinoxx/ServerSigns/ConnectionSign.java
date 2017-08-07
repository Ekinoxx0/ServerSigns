package fr.Ekinoxx.ServerSigns;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.bukkit.Location;
import org.bukkit.block.Sign;

public class ConnectionSign {
    
    private Location location;
    private Sign sign;
    private String name, ip;
    private int port;
   
    public ConnectionSign(Location location, String name, String ip, int port) {
            this.location = location;
            this.name = name;
            this.ip = ip;
            this.port = port;
            
            if(location.getBlock() instanceof Sign || location.getBlock().getState() instanceof Sign){
            	this.sign = (Sign) location.getBlock().getState();
            }
    }
   
    public Location getLocation() {
            return location;
    }
   
    public String getName() {
            return name;
    }
   
    public String getIP() {
            return ip;
    }
   
    public int getPort() {
            return port;
    }
   
    public void update() {
            try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), 1 * 1000);
                   
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                   
                    out.write(0xFE);
                   
                    StringBuilder str = new StringBuilder();
                   
                    int b;
                    while ((b = in.read()) != -1) {
                            if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24) {
                                    str.append((char) b);
                            }
                    }
                   
                    String[] data = str.toString().split("§");
                    int onlinePlayers = Integer.valueOf(data[1]);
                    int maxPlayers = Integer.valueOf(data[2]);
                   
                    sign.setLine(0, "" + name);
                    sign.setLine(1, onlinePlayers + "/" + maxPlayers);
                    sign.setLine(2, "");
                    sign.setLine(3, "");
                   
                    socket.close();
            } catch (Exception e) {
                    e.printStackTrace();
                   
                    sign.setLine(0, "");
                    sign.setLine(1, "");
                    sign.setLine(2, "");
                    sign.setLine(3, "");
            }
           
            sign.update();
    }
   
}

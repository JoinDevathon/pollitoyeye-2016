package org.devathon.contest2016;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.devathon.contest2016.utils.EconomyManager;
public class VendingMachines extends JavaPlugin{
	private static VendingMachines pl;
	private static MachineManager mManager;
	private static EconomyManager eManager;
	private final static String pluginPrefix = ChatColor.YELLOW + "[" + ChatColor.GREEN + "VendingMachines" + ChatColor.YELLOW + "] ";
	public static HashMap<String,Machine> machinesMap = new HashMap<String,Machine>();
	public static HashMap<Player,Machine> currentMachineMap = new HashMap<Player,Machine>();
	public static HashMap<String,HashMap<Integer,SlotData>> loadedSlotsData = new HashMap<String,HashMap<Integer,SlotData>>();
	public void onEnable(){
		pl = this;
		saveDefaultConfig();
		mManager = new MachineManager();
		eManager = new EconomyManager();
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		loadMachinesMap();
		
	}
	public void onDisable(){
		saveMachinesMap();
		saveConfig();
	}
	private void loadMachinesMap(){
		if(getConfig().contains("Machines")){
			for(String s : getConfig().getConfigurationSection("Machines").getKeys(false)){
				HashMap<Integer,SlotData> slotsData = new HashMap<Integer,SlotData>();
				for(String slot : getConfig().getConfigurationSection("Machines." + s + ".slots").getKeys(false)){
					int x = Integer.parseInt(slot);
					int price = getConfig().getInt("Machines." + s + ".slots." + slot + ".price");
					ItemStack displayItem = getConfig().getItemStack("Machines." + s + ".slots." + slot + ".itemstack");
					boolean empty = getConfig().getBoolean("Machines." + s + ".slots." + slot + ".empty");
					SlotData slotData = new SlotData(displayItem, price, empty);
					slotsData.put(x, slotData);
				}
				loadedSlotsData.put(s, slotsData);
			}
		}
	}
	private void saveMachinesMap() {
		for(String uuid : machinesMap.keySet()){
			Machine m = machinesMap.get(uuid);
			HashMap<Integer,SlotData> mData = m.getSlotsData();
			for(int v : mData.keySet()){
				SlotData toSet = mData.get((Integer) v);
				getConfig().set("Machines." + uuid + ".slots." + v + ".itemstack", toSet.getDisplayItem());
				getConfig().set("Machines." + uuid + ".slots." + v + ".price", toSet.getPrice());
				getConfig().set("Machines." + uuid + ".slots." + v + ".empty", toSet.isEmpty());
			}
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("createVendingMachine")){
			if(sender instanceof Player){
				mManager.placeVendingMachine(((Player) sender).getLocation().getBlock());
				sender.sendMessage(pluginPrefix + ChatColor.AQUA + "A machine was created at your location.");
			}else{
				sender.sendMessage(pluginPrefix + ChatColor.RED + "You must be a player to use this command.");
			}
			return true;
		}
		return false;
	}
	public static VendingMachines getPlugin(){
		return pl;
	}
	public static MachineManager getMachineManager(){
		return mManager;
	}
	public static EconomyManager getEconomyManager(){
		return eManager;
	}
	public static String getPluginPrefix(){
		return pluginPrefix;
	}
}

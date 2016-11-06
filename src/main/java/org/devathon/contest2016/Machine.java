package org.devathon.contest2016;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.devathon.contest2016.utils.ItemUtil;


public class Machine {
	//TODO - Remove Button
	private HashMap<Integer,SlotData> slotsData = new HashMap<Integer,SlotData>();
	private Inventory userInv;
	private Inventory adminInv;
	private ArmorStand mainStand;
	private ItemStack emptyItem;
	private ItemStack increasePrice;
	private ItemStack decreasePrice;
	private String uuid;
	public Machine(ArmorStand mainStand, String uuid){
		this.mainStand = mainStand;
		this.uuid = uuid;
		userInv = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Vending Machine");
		adminInv = Bukkit.createInventory(null, 45, ChatColor.BLUE + "Vending Machine Admin Menu");
		for(int x = 1; x < 7; x++){
			slotsData.put(x, new SlotData(new ItemStack(Material.AIR),0,true));
		}
		ItemStack emptySlot = ItemUtil.createItemStack(Material.STAINED_GLASS_PANE, (short) 0, " ", new String[]{});
		increasePrice = ItemUtil.createItemStack(Material.STAINED_GLASS_PANE, (short) 4, ChatColor.YELLOW + "" + ChatColor.BOLD + "Increase price", new String[]{});
		decreasePrice = ItemUtil.createItemStack(Material.STAINED_GLASS_PANE, (short) 5, ChatColor.GREEN + "" + ChatColor.BOLD + "Decrease price", new String[]{});
		emptyItem = ItemUtil.createItemStack(Material.BARRIER, (short) 0, ChatColor.RED + "" + ChatColor.BOLD + "Empty", new String[]{});
		for(int x = 0; x < 45; x+= 9){
			adminInv.setItem(x, emptySlot);
			adminInv.setItem(x + 4, emptySlot);
			adminInv.setItem(x + 8, emptySlot);
			if(x == 0 || x == 36){
				for(int y = 1; y < 8; y ++){
					adminInv.setItem(x + y, emptySlot);
				}
			}else{
				adminInv.setItem(x + 1, decreasePrice);
				adminInv.setItem(x + 2, emptyItem);
				adminInv.setItem(x + 3, increasePrice);
				adminInv.setItem(x + 5, decreasePrice);
				adminInv.setItem(x + 6, emptyItem);
				adminInv.setItem(x + 7, increasePrice);
			}
		}
		adminInv.setItem(40, ItemUtil.createItemStack(Material.FLINT_AND_STEEL, (short) 0, ChatColor.RED + "" + ChatColor.BOLD + "Remove Machine", new String[]{ChatColor.RED + "Click here to remove this machine."}));
		int x = 1;
		while(x < 8){
			userInv.setItem(x, emptyItem);
			x++;
			if(x == 4){
				x = 5;
			}
		}
		userInv.setItem(0, emptySlot);
		userInv.setItem(4, emptySlot);
		userInv.setItem(8, emptySlot);
	}
	public HashMap<Integer,SlotData> getSlotsData(){
		return this.slotsData;
	}
	public void setSlotsData(HashMap<Integer,SlotData> slotsData){
		this.slotsData = slotsData;
		updateInvs();
		updateDisplayItems();
	}
	public void open(Player p){
		p.openInventory(userInv);
	}
	public void openAdmin(Player p){
		p.openInventory(adminInv);
	}
	public boolean click(Player p, Inventory inv, ItemStack clickedItem, ItemStack cursorItem, int slot){
		if(inv.equals(adminInv)){
			if(clickedItem != null){
				if(slot != 40){
					if(slot < 36 && slot > 8){
						int r = slot % 9;
						if(r == 1 || r == 5){
							int actionSlot = 2  * ((int)((slot -9) / 9)) + (r > 1 ? 2:1) ;
							//Remove price on actionslot
							SlotData d = slotsData.get((Integer) actionSlot);
							if(!d.isEmpty()){
								d.setPrice(d.getPrice() - 1);
								if(d.getPrice() < 0){
									d.setPrice(0);
								}
								updateInvs();
							}
						}
						if(r == 3 || r == 7){
							int actionSlot = 2  * ((int)((slot -9) / 9)) + (r > 3 ? 2:1) ;
							//Add price on actionslot
							SlotData d = slotsData.get((Integer) actionSlot);
							if(!d.isEmpty()){
								d.setPrice(d.getPrice() + 1);
								updateInvs();
							}
						}
						if(r == 2 || r == 6){
							int actionSlot = 2  * ((int)((slot -9) / 9)) + (r > 2 ? 2:1) ;
							//Clicked item slot
							SlotData d = slotsData.get((Integer) actionSlot);
							if(cursorItem != null && cursorItem.getType() != Material.AIR){
								d.setDisplayItem(cursorItem);
								d.setEmpty(false);
							}else{
								d.setEmpty(true);
							}
							updateInvs();
							updateDisplayItems();
						}
					}
				}else{
					remove();
				}
			}
			return true;
		}else if(inv.equals(userInv)){
			if(slot  > 0 &&  slot != 4 && slot != 8){
				int actionSlot = slot - (slot > 4 ? 1 : 0);
				SlotData d = slotsData.get((Integer) actionSlot);
				int currentMoney = VendingMachines.getEconomyManager().getCurrentMoney(p);
				if(currentMoney > d.getPrice()){
					VendingMachines.getEconomyManager().withdrawMoney(p,d.getPrice());
					p.sendMessage(VendingMachines.getPluginPrefix() + ChatColor.GREEN + "You bought an item from the machine.");
					p.getInventory().addItem(d.getDisplayItem());
				}else{
					p.sendMessage(VendingMachines.getPluginPrefix() + ChatColor.RED + "You don't have enough money to buy this.");
				}
			}
			return true;
		}
		return false;
	}
	private void updateInvs(){
		int currentPos = 11;
		boolean nextRow = false;
		int x = 1;
		for(int r : slotsData.keySet()){
			SlotData d = slotsData.get((Integer)r);
			if(d.isEmpty()){
				adminInv.setItem(currentPos, emptyItem);
				adminInv.setItem(currentPos - 1, decreasePrice);
				adminInv.setItem(currentPos + 1, increasePrice);
				userInv.setItem(x, emptyItem);
			}else{
				adminInv.setItem(currentPos, d.getDisplayItem());
				adminInv.setItem(currentPos - 1, ItemUtil.createItemStack(Material.STAINED_GLASS_PANE, (short) 5, ChatColor.GREEN + "" + ChatColor.BOLD + "Decrease price", new String[]{ChatColor.AQUA + "Current Price: " + d.getPrice()}));
				adminInv.setItem(currentPos + 1, ItemUtil.createItemStack(Material.STAINED_GLASS_PANE, (short) 4, ChatColor.YELLOW + "" + ChatColor.BOLD + "Increase price", new String[]{ChatColor.AQUA + "Current Price: " + d.getPrice()}));
				ItemStack displayItem = d.getDisplayItem();
				ItemMeta meta = displayItem.getItemMeta();
				meta.setLore(Arrays.asList(ChatColor.AQUA + "Buy For: " + d.getPrice() + "$"));
				displayItem.setItemMeta(meta);
				userInv.setItem(x, displayItem);
			}
			if(nextRow){
				currentPos += 5;
				nextRow = false;
			}else{
				currentPos += 4;
				nextRow = true;
			}
			x++;
			if(x == 4){
				x = 5;
			}
		}
	}
	public void remove(){
		Location loc = mainStand.getLocation();
		for(Entity en : mainStand.getNearbyEntities(10, 10, 10)){
			if(en instanceof ArmorStand && en.getCustomName() != null && en.getCustomName().startsWith("VendingMachinePart;")){
				String[] toCheckData = en.getCustomName().split(";");
				if(toCheckData.length > 1 && toCheckData[1].equals(uuid)){
					en.remove();
				}
			}
		}
		for(double z = loc.getZ() + 1; z >= loc.getZ() - 1; z--){
			for(int y = 0; y <= 2; y++){
				Location blLoc = loc.clone().add(0,y,0);
				blLoc.setZ(z);
				blLoc.getBlock().setType(Material.AIR);
			}
		}
		for(Player p : VendingMachines.currentMachineMap.keySet()){
			if(VendingMachines.currentMachineMap.get(p) == this){
				p.closeInventory();
			}
		}
		mainStand.remove();
		VendingMachines.machinesMap.remove(uuid);
		if(VendingMachines.loadedSlotsData.containsKey(uuid)){
			VendingMachines.loadedSlotsData.remove(uuid);
		}
		FileConfiguration config = VendingMachines.getPlugin().getConfig();
		if(config.contains("Machines." + uuid)){
			config.set("Machines." + uuid, null);
		}
	}
	private void updateDisplayItems(){
		ArrayList<ItemStack> showStacks = new ArrayList<ItemStack>();
		for(SlotData d : slotsData.values()){
			if(!d.isEmpty()){
				ItemStack s = d.getDisplayItem();
				if(!s.getType().isBlock()){
					showStacks.add(d.getDisplayItem());
				}
			}
		}
		HashMap<Integer,ArmorStand> displayStands = new HashMap<Integer,ArmorStand>();
		for(Entity en : mainStand.getNearbyEntities(10, 10, 10)){
			if(en instanceof ArmorStand && en.getCustomName() != null && en.getCustomName().startsWith("VendingMachinePart;")){
				String[] toCheckData = en.getCustomName().split(";");
				if(toCheckData.length > 1 && toCheckData[1].equals(uuid)){
					String s = en.getCustomName();
					if(s.contains(";SLOT3")){
						displayStands.put(3, (ArmorStand) en);
					}
					if(s.contains(";SLOT2")){
						displayStands.put(2, (ArmorStand) en);
					}
					if(s.contains(";SLOT1")){
						displayStands.put(1, (ArmorStand) en);
					}
				}
			}
		}
		if(showStacks.size() > 0){
			if(displayStands.containsKey((Integer) 1)){
				displayStands.get((Integer)1).setHelmet(showStacks.get(0));
			}
		}else{
			if(displayStands.containsKey((Integer) 1)){
				displayStands.get((Integer)1).setHelmet(new ItemStack(Material.AIR));
			}
		}
		if(showStacks.size() > 1){
			if(displayStands.containsKey((Integer) 2)){
				displayStands.get((Integer)2).setHelmet(showStacks.get(1));
			}
		}else{
			if(displayStands.containsKey((Integer) 2)){
				displayStands.get((Integer)2).setHelmet(new ItemStack(Material.AIR));
			}
		}
		if(showStacks.size() > 2){
			if(displayStands.containsKey((Integer) 3)){
				displayStands.get((Integer)3).setHelmet(showStacks.get(2));
			}
		}else{
			if(displayStands.containsKey((Integer) 3)){
				displayStands.get((Integer)3).setHelmet(new ItemStack(Material.AIR));
			}
		}
	}
}

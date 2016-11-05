package org.devathon.contest2016;

import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;


public class EventListener implements Listener {
	@EventHandler
	public void onMachineDamage(EntityDamageEvent event){
		Entity en = event.getEntity();
		if(en instanceof ArmorStand && en.getCustomName() != null && (en.getCustomName().startsWith("VendingMachineCore;") || en.getCustomName().startsWith("VendingMachinePart;"))){
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onMachineInteract(PlayerInteractAtEntityEvent event){
		Entity en = event.getRightClicked();
		if(en instanceof ArmorStand && en.getCustomName() != null && (en.getCustomName().startsWith("VendingMachineCore;") || en.getCustomName().startsWith("VendingMachinePart;"))){
			event.setCancelled(true);
			String[] data = en.getCustomName().split(";");
			if(data.length > 1){
				String uuid = data[1];
				Player p = event.getPlayer();
				Machine m = null;
				if(VendingMachines.machinesMap.containsKey(uuid)){
					m = VendingMachines.machinesMap.get(uuid);
				}else{
					ArmorStand mainStand = null;
					if(en.getCustomName().startsWith("VendingMachineCore;")){
						mainStand = (ArmorStand) en;
					}else{
						for(Entity toCheck : en.getNearbyEntities(8, 8, 8)){
							if(toCheck instanceof ArmorStand && toCheck.getCustomName() != null && toCheck.getCustomName().startsWith("VendingMachineCore;")){
								String[] toCheckData = toCheck.getCustomName().split(";");
								if(toCheckData.length > 1 && toCheckData[1].equals(uuid)){
									mainStand = (ArmorStand) toCheck;
									break;	
								}
							}
						}
					}
					if(mainStand != null){
						m = new Machine(mainStand, uuid);
						if(VendingMachines.loadedSlotsData.containsKey(uuid)){
							m.setSlotsData(VendingMachines.loadedSlotsData.get(uuid));
						}
						VendingMachines.machinesMap.put(uuid, m);
					}
				}
				if(m != null){
					if(p.isSneaking() && p.hasPermission("VendingMachines.admin")){
						m.openAdmin(p);
					}else{
						m.open(p);
					}
					VendingMachines.currentMachineMap.put(p, m);
				}
			}
		}
	}
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		if(VendingMachines.currentMachineMap.containsKey(event.getPlayer())){
			VendingMachines.currentMachineMap.remove(event.getPlayer());
		}
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		HumanEntity whoClicked = event.getWhoClicked();
		if(event.getClickedInventory() != null && VendingMachines.currentMachineMap.containsKey(whoClicked)){
			event.setCancelled(VendingMachines.currentMachineMap.get(whoClicked).click((Player)whoClicked,event.getClickedInventory(),event.getCurrentItem(),event.getCursor(),event.getSlot()));
		}
	}
	@EventHandler
	public void onPistonRetract(BlockPistonRetractEvent event){
		Block bl = event.getBlock();
		for(Entity en : bl.getWorld().getEntities()){
			for(Block b : event.getBlocks()){
				if(en.getLocation().distance(b.getLocation().clone().add(0.5,0.5,0.5)) < 1.0){
					if(en instanceof ArmorStand && en.getCustomName() != null && (en.getCustomName().startsWith("VendingMachinePart;") || en.getCustomName().startsWith("VendingMachineCore;"))){
						event.setCancelled(true);
					}
				}
			}
			if(en.getLocation().distance(bl.getLocation().clone().add(0.5,0.5,0.5)) < 1.0){
				if(en instanceof ArmorStand && en.getCustomName() != null && (en.getCustomName().startsWith("VendingMachinePart;") || en.getCustomName().startsWith("VendingMachineCore;"))){
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void onPistonExtend(BlockPistonExtendEvent event){
		
	}
}

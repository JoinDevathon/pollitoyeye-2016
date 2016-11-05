package org.devathon.contest2016.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EconomyManager {
	//You may change this class to use your own economy, currently it uses a diamond base economy.
	public int getCurrentMoney(Player p){
		int x = 0;
		for(ItemStack s : p.getInventory().getContents()){
			if(s != null && s.getType() == Material.DIAMOND){
				x+= s.getAmount();
			}
		}
		return x;
	}
	public void withdrawMoney(Player p, int amount){
		int x = amount;
		for(ItemStack s : p.getInventory().getContents()){
			if(s != null && s.getType() == Material.DIAMOND){
				if(x > 0){
					if(s.getAmount() > x){
						s.setAmount(s.getAmount() - x);
						x = 0;
					}else{
						x-= s.getAmount();
						p.getInventory().removeItem(s);
					}
				}
			}
		}
		p.updateInventory();
	}
}

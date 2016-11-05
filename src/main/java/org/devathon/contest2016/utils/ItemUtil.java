package org.devathon.contest2016.utils;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {
	public static ItemStack createItemStack(Material mat, short damage, String displayName, String[] lore){
		ItemStack stack = new ItemStack(mat,1,damage);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(Arrays.asList(lore));
		stack.setItemMeta(meta);
		return stack;
	}
}

package org.devathon.contest2016;

import org.bukkit.inventory.ItemStack;

public class SlotData {
	private ItemStack displayItem;
	private int price;
	private boolean empty;
	private int itemAmount = 1;
	public SlotData(ItemStack displayItem, int price, boolean empty){
		this.displayItem = displayItem;
		this.price = price;
		this.empty = empty;
	}
	public void setDisplayItem(ItemStack s){
		this.displayItem = s;
		this.itemAmount = s.getAmount();
	}
	public void setPrice(int p){
		this.price = p;
	}
	public void setEmpty(boolean b){
		this.empty = b;
	}
	public ItemStack getDisplayItem(){
		ItemStack s = displayItem.clone();
		s.setAmount(itemAmount);
		return s;
	}
	public int getPrice(){
		return price;
	}
	public boolean isEmpty(){
		return empty;
	}
}

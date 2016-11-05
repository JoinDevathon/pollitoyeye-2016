package org.devathon.contest2016;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class MachineManager {
	public void placeVendingMachine(Block bl){
		@SuppressWarnings("deprecation")
		FileConfiguration config = YamlConfiguration.loadConfiguration(VendingMachines.getPlugin().getResource("Machine.schem"));
		UUID structureUUID = UUID.randomUUID();
		Location loc = bl.getLocation().clone().subtract(0.2,0,0);
		int currentPos = 0;
		for(double z = bl.getLocation().getZ() + 1; z >= bl.getLocation().getZ() - 1; z--){
			for(int y = 0; y <= 2; y++){
				Location blLoc = bl.getLocation().clone().add(0,y,0);
				blLoc.setZ(z);
				blLoc.getBlock().setType(Material.BARRIER);
			}
		}
		ArmorStand mainStand = (ArmorStand) bl.getWorld().spawnEntity(bl.getLocation().add(0.5,0,0.5), EntityType.ARMOR_STAND);
		mainStand.setCustomName("VendingMachineCore;" + structureUUID);
		mainStand.setGravity(false);
		mainStand.setVisible(false);
		mainStand.setInvulnerable(true);
		while(config.contains("" + currentPos)){
			Location spawnLoc = loc.clone().subtract(config.getDouble(currentPos + ".position.x"), config.getDouble(currentPos + ".position.y"), config.getDouble(currentPos + ".position.z"));
			spawnLoc.setYaw(Float.parseFloat(config.get(currentPos + ".yaw").toString()));
			spawnLoc.setPitch(Float.parseFloat(config.get(currentPos + ".pitch").toString()));
			ArmorStand a = (ArmorStand) loc.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
			a.setGravity(false);
			a.setHelmet(config.getItemStack(currentPos + ".helmet"));
			a.setChestplate(config.getItemStack(currentPos + ".chestplate"));
			a.setLeggings(config.getItemStack(currentPos + ".leggings"));
			a.setBoots(config.getItemStack(currentPos + ".boots"));
			a.setItemInHand(config.getItemStack(currentPos + ".hand"));
			a.setVisible(config.getBoolean(currentPos + ".visible"));
			a.setSmall(config.getBoolean(currentPos + ".small"));
			a.setArms(config.getBoolean(currentPos + ".arms"));
			a.setBasePlate(config.getBoolean(currentPos + ".plate"));
			a.setBodyPose(new EulerAngle(config.getDouble(currentPos + ".body.x"),config.getDouble(currentPos + ".body.y"),config.getDouble(currentPos + ".body.z")));
			a.setHeadPose(new EulerAngle(config.getDouble(currentPos + ".head.x"),config.getDouble(currentPos + ".head.y"),config.getDouble(currentPos + ".head.z")));
			a.setLeftArmPose(new EulerAngle(config.getDouble(currentPos + ".leftarm.x"),config.getDouble(currentPos + ".leftarm.y"),config.getDouble(currentPos + ".leftarm.z")));
			a.setLeftLegPose(new EulerAngle(config.getDouble(currentPos + ".leftleg.x"),config.getDouble(currentPos + ".leftleg.y"),config.getDouble(currentPos + ".leftleg.z")));
			a.setRightArmPose(new EulerAngle(config.getDouble(currentPos + ".rightarm.x"),config.getDouble(currentPos + ".rightarm.y"),config.getDouble(currentPos + ".rightarm.z")));
			a.setRightLegPose(new EulerAngle(config.getDouble(currentPos + ".rightleg.x"),config.getDouble(currentPos + ".rightleg.y"),config.getDouble(currentPos + ".rightleg.z")));
			a.setInvulnerable(true);
			String suffix = "";
			Material helmetMat = a.getHelmet().getType();
			if(helmetMat == Material.WOOD_AXE){
				suffix = ";SLOT3";
				a.setHelmet(new ItemStack(Material.AIR));
			}else if(helmetMat== Material.IRON_AXE){
				suffix = ";SLOT2";
				a.setHelmet(new ItemStack(Material.AIR));
			}else if(helmetMat== Material.DIAMOND_AXE){
				suffix = ";SLOT1";
				a.setHelmet(new ItemStack(Material.AIR));
			}
			a.setCustomName("VendingMachinePart;" + structureUUID + suffix);
			currentPos++;
		}
	}
}

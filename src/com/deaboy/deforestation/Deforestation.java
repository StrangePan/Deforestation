package com.deaboy.deforestation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Deforestation extends JavaPlugin implements Listener {
	private List<Block> checkedBlocks = new ArrayList<Block>();
	private Block sourceBlock;
	private boolean breakBlocks;
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.isCancelled()) return;
		if (e.getBlock().getTypeId() == 17) {
			switch (e.getBlock().getData()) {
				case 0:	breakOakTree(e.getBlock());
						break;
				case 1:	breakSpruceTree(e.getBlock());
						break;
				case 2:	breakBirchTree(e.getBlock());
						break;
				case 3: breakJungleTree(e.getBlock());
						break;
			}
			//for (Block block : checkedBlocks) {
				//block.breakNaturally();
			//}
			checkedBlocks.clear();
		}
	}
	
	public void breakTree(Block block, int max, int range, boolean adjacent, boolean ignoreLeaves) {
		//Break single tall tree
			List<Block> treeBlocks = new ArrayList<Block>();
			Location loc = block.getLocation();
			if (Math.abs(loc.getX() - sourceBlock.getLocation().getX()) > range
					|| Math.abs(loc.getZ() - sourceBlock.getLocation().getZ()) > range
					|| Math.abs(loc.getY() - sourceBlock.getLocation().getY()) > max){
				breakBlocks = false;
				return;
			}
			int max2 = max;
			while (max2 > 0
					&& block.getWorld().getBlockAt(loc).getTypeId() == block.getTypeId()
					&& block.getWorld().getBlockAt(loc).getData() == block.getData()
					&& !checkedBlocks.contains(block.getWorld().getBlockAt(loc))) {
				if (Math.abs(loc.getX() - sourceBlock.getLocation().getX()) > range
						|| Math.abs(loc.getZ() - sourceBlock.getLocation().getZ()) > range
						|| Math.abs(loc.getY() - sourceBlock.getLocation().getY()) > max){
					breakBlocks = false;
					return;
				}
				checkedBlocks.add(block.getWorld().getBlockAt(loc));
				treeBlocks.add(block.getWorld().getBlockAt(loc));
				loc.setY(loc.getY()+1);
				max2--;
			}
			if (ignoreLeaves || block.getWorld().getBlockAt(loc).getType() == Material.LEAVES) {
				if (adjacent) {
					loc = block.getLocation();
					max2 = max;
					while (max2 > 0
							&& block.getWorld().getBlockAt(loc).getTypeId() == block.getTypeId()
							&& block.getWorld().getBlockAt(loc).getData() == block.getData()) {
						for (Block b : getAdjacentLogs(block.getWorld().getBlockAt(loc), ((sourceBlock.getData()==3) ? 2 : 1), true)) {
							if (!checkedBlocks.contains(b)) {
								breakTree(b, max, range, true, true);
							}
						}
						loc.setY(loc.getY()+1);
						max2--;
					}
				}
				
				if (breakBlocks){
					for (Block log : treeBlocks) {
						log.breakNaturally();
					}
				}
				
				return;
			}
	}
	
	public void breakOakTree(Block block) {
		breakBlocks = true;
		sourceBlock = block;if (getAdjacentLogs(block, 1, false).isEmpty()) {
			sourceBlock = block;
			breakTree(block, 9, 5, true, false);
		}
	}
	
	public void breakSpruceTree(Block block) {
		breakBlocks = true;
		sourceBlock = block;
		breakTree(block, 9, 0, false, false);
	}
	
	public void breakBirchTree(Block block) {
		breakBlocks = true;
		sourceBlock = block;
		breakTree(block, 7, 0, false, false);
	}
	
	public void breakJungleTree(Block block) {
		breakBlocks = true;
		if (getAdjacentLogs(block, 1, false).isEmpty()) {
			sourceBlock = block;
			breakTree(block, 32, 5, true, false);
		}
	}
	
	public List<Block> getAdjacentLogs(Block block, int range, boolean above) {
		List<Block> blocks = new ArrayList<Block>();
		int x=-range,z=-range,y=0;
		int maxHeight;
		if (above) maxHeight=1; else maxHeight=0;
		
		while (y <= maxHeight) {
			while (x <= range) {
				while (z <= range) {
					if (!(x==0 && z==0 && y==0)) {
						if (block.getWorld().getBlockAt(block.getX()+x,block.getY()+y,block.getZ()+z).getTypeId() == block.getTypeId()
								&& block.getWorld().getBlockAt(block.getX()+x,block.getY()+y,block.getZ()+z).getData() == block.getData()) {
							blocks.add(block.getWorld().getBlockAt(block.getX()+x,block.getY()+y,block.getZ()+z));
						}
					}
					z++;
				}
				z=-range;
				x++;
			}
			z=-range;
			x=-range;
			y++;
		}
		
		return blocks;
	}
}

package k4unl.minecraft.Hydraulicraft.blocks.handlers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import k4unl.minecraft.Hydraulicraft.api.PressureTier;
import k4unl.minecraft.Hydraulicraft.api.IMultiTieredBlock;
import k4unl.minecraft.Hydraulicraft.lib.Localization;
import k4unl.minecraft.Hydraulicraft.lib.helperClasses.Name;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class HydraulicTieredBlockHandler extends HandlerHydraulicBlock {
	private Name[] tNames;
	
	
	public HydraulicTieredBlockHandler(Block block, Name[] names) {
		super(block);
		
		tNames = names;
		
		setHasSubtypes(true);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack){
		String unlocalizedName = tNames[itemStack.getItemDamage()].unlocalized;
		if(!unlocalizedName.startsWith("tile.")){
			unlocalizedName = "tile." + unlocalizedName;
		}
		return unlocalizedName;
	}
	
	@Override
	public int getMetadata(int damage){
		return damage;
	}

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4){
        super.addInformation(itemstack, player, list, par4);
        if(itemstack != null){
            Item theItem  = itemstack.getItem();
            Block btH = ((HandlerHydraulicBlock)theItem).blockToHandle;



        }
    }
	
}


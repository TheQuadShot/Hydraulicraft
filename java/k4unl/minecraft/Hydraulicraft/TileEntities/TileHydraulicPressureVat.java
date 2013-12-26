package k4unl.minecraft.Hydraulicraft.TileEntities;

import k4unl.minecraft.Hydraulicraft.baseClasses.entities.TileStorage;
import k4unl.minecraft.Hydraulicraft.fluids.Fluids;
import k4unl.minecraft.Hydraulicraft.lib.Functions;
import k4unl.minecraft.Hydraulicraft.lib.Log;
import k4unl.minecraft.Hydraulicraft.lib.config.Names;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;

public class TileHydraulicPressureVat extends TileStorage implements IInventory {
	private ItemStack inputInventory;
	private ItemStack outputInventory;
	
	private FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 16);
	
	
	public TileHydraulicPressureVat(){
		
	}
	
	public void setTier(){
		tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * (16 * (getTier()+1)));
	}
	
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet){
		NBTTagCompound tagCompound = packet.data;
		this.readFromNBT(tagCompound);
	}
	
	@Override
	public Packet getDescriptionPacket(){
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.writeToNBT(tagCompound);
		return new Packet132TileEntityData(xCoord,yCoord,zCoord,4,tagCompound);
	}
	
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound){
		super.readFromNBT(tagCompound);
		
		NBTTagCompound inventoryCompound = tagCompound.getCompoundTag("inputInventory");
		inputInventory = ItemStack.loadItemStackFromNBT(inventoryCompound);
		
		inventoryCompound = tagCompound.getCompoundTag("outputInventory");
		outputInventory = ItemStack.loadItemStackFromNBT(inventoryCompound);
		
		tank.readFromNBT(tagCompound.getCompoundTag("tank"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound){
		super.writeToNBT(tagCompound);
		
		if(inputInventory != null){
			NBTTagCompound inventoryCompound = new NBTTagCompound();
			inputInventory.writeToNBT(inventoryCompound);
			tagCompound.setCompoundTag("inputInventory", inventoryCompound);
		}
		if(outputInventory != null){
			NBTTagCompound inventoryCompound = new NBTTagCompound();
			outputInventory.writeToNBT(inventoryCompound);
			tagCompound.setCompoundTag("outputInventory", inventoryCompound);
		}
		NBTTagCompound tankCompound = new NBTTagCompound();
		tank.writeToNBT(tankCompound);
		tagCompound.setCompoundTag("tank", tankCompound);
	}


	@Override
	public int getSizeInventory() {
		return 2;
	}
	
	@Override
	public ItemStack getStackInSlot(int i) {
		switch(i){
		case 0:
			return inputInventory;
		case 1:
			return outputInventory;
		default:
			return null;
			
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack inventory = getStackInSlot(i);
		
		ItemStack ret = null;
		if(inventory.stackSize < j){
			ret = inventory;
			inventory = null;
			
		}else{
			ret = inventory.splitStack(j);
			if(inventory.stackSize == 0){
				inventory = null;
			}
		}
		
		return ret;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack stack = getStackInSlot(i);
		if(stack != null){
			setInventorySlotContents(i, null);
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {
		if(i == 0){
			inputInventory = itemStack;
		}else{
			//Err...
			
		}
	}

	@Override
	public String getInvName() {
		// TODO Localization
		return Names.blockHydraulicPressurevat[getTier()].localized;
	}

	@Override
	public boolean isInvNameLocalized() {
		// TODO Localization
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return ((worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this) && 
				player.getDistanceSq(xCoord, yCoord, zCoord) < 64);
	}

	@Override
	public void openChest() {
		
	}

	@Override
	public void closeChest() {
		
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemStack) {
		if(i == 0){
			//if(Config.canBeCrushed(itemStack)){
			//	return true;
			//}else{
				return false;
			//}
		}else{
			return false;
		}
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		int filled = tank.fill(resource, doFill);
		if(doFill && filled > 10){
			Functions.checkAndFillSideBlocks(worldObj, xCoord, yCoord, zCoord);
			//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}else if((getFluidInSystem() + resource.amount) < getTotalFluidCapacity()){
			if(doFill){
				Functions.checkAndSetSideBlocks(worldObj, xCoord, yCoord, zCoord, getFluidInSystem() + resource.amount, isOilStored());
			}
			filled = resource.amount;
		}else if(getFluidInSystem() < getTotalFluidCapacity()) {
			if(doFill){
				Functions.checkAndSetSideBlocks(worldObj, xCoord, yCoord, zCoord, getTotalFluidCapacity(), isOilStored());
			}
			filled = getTotalFluidCapacity() - getFluidInSystem();
		}else{
			filled = 0;
		}
		return filled;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		FluidStack drained = tank.drain(maxDrain, doDrain); 
		if(doDrain && drained.amount > 0){
			Functions.checkAndFillSideBlocks(worldObj, xCoord, yCoord, zCoord);
			//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		return drained;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if(fluid.getID() == FluidRegistry.WATER.getID() ||
				fluid.getID() == Fluids.fluidOil.getID()){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		FluidTankInfo[] tankInfo = {new FluidTankInfo(tank)};
		return tankInfo;
		
	}

	@Override
	public int getStorage() {
		return tank.getCapacity();
	}

	@Override
	public int getStored() {
		return tank.getFluidAmount();
	}

	@Override
	public void setStored(int i, boolean isOil) {
		if(isOil){
			//tank.setFluid(new FluidStack(fluid, amount));
		}else{
			tank.setFluid(new FluidStack(FluidRegistry.WATER, i));
			//Log.info("Fluid in tank: " + tank.getFluidAmount() + "x" + FluidRegistry.getFluidName(tank.getFluid().fluidID));
			//if(!worldObj.isRemote){
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			//}
		}
	}
}
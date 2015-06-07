package k4unl.minecraft.Hydraulicraft.lib.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidInventory extends IInventory {
    /**
     * Drains fluid when crafting
     *
     * @param fluidStack fluid stack to drain
     * @param pretend    pretend to drain (ie check for whether possible)
     * @return whether successful
     */
    boolean drainFluid(FluidStack fluidStack, boolean pretend);

    /**
     * Fills fluid when crafting
     *
     * @param fluidStack fluid stack to fill
     * @param pretend    pretend to fill (ie check for whether possible)
     * @return whether successful
     */
    boolean fillFluid(FluidStack fluidStack, boolean pretend);

    /**
     * Called when crafting matrix has changed
     */
    void onMatrixChanged();

    /**
     * Gets inventory crafting for checking recipes
     *
     * @return inventory crafting instance with slots
     */
    InventoryCrafting getInventoryCrafting();
}
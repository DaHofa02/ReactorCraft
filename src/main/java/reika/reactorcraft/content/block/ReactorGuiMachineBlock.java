/*******************************************************************************
 * @author Reika Kalseki (original ReactorCraft). Private NeoForge 1.21.1 port; not for distribution.
 ******************************************************************************/
package reika.reactorcraft.content.block;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;

import reika.reactorcraft.content.blockentity.ElectrolyzerBlockEntity;
import reika.reactorcraft.content.blockentity.HeavyPumpBlockEntity;
import reika.reactorcraft.content.blockentity.IsoCentrifugeBlockEntity;
import reika.reactorcraft.content.blockentity.SynthesizerBlockEntity;
import reika.reactorcraft.content.blockentity.UProcessorBlockEntity;
import reika.reactorcraft.content.menu.CpuMenu;
import reika.reactorcraft.content.menu.ElectrolyzerMenu;
import reika.reactorcraft.content.menu.IsoCentrifugeMenu;
import reika.reactorcraft.content.menu.SynthesizerMenu;
import reika.reactorcraft.content.menu.UProcessorMenu;
import reika.reactorcraft.content.menu.WasteMenus;
import reika.reactorcraft.reactor.CpuBlockEntity;
import reika.reactorcraft.registry.ReactorFluids;
import reika.reactorcraft.registry.ReactorItems;
import reika.reactorcraft.waste.WasteContainerBlockEntity;
import reika.reactorcraft.waste.WasteStorageBlockEntity;
import reika.rotarycraft.content.block.MachineBlock;
import reika.rotarycraft.content.blockentity.RotaryCraftBlockEntity;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * Original: BlockReactorTile / BlockReactorTileModelled with ReactorGuiHandler - a 4-sided RotaryCraft-style machine block that opens its
 * GUI; the synthesizer takes a water bucket by right-click, the fluid extractor fills an empty bucket with heavy water or an empty
 * canister with molten lithium (1000 mB each).
 */
public class ReactorGuiMachineBlock extends MachineBlock {

	private final MapCodec<ReactorGuiMachineBlock> codec;

	public ReactorGuiMachineBlock(Properties props, Supplier<? extends BlockEntityType<? extends RotaryCraftBlockEntity>> type) {
		super(props, type, Placement.FOUR_SIDED, false);
		codec = simpleCodec(p -> new ReactorGuiMachineBlock(p, type));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return codec;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack is, BlockState state, Level world, BlockPos pos, Player ep,
			InteractionHand hand, BlockHitResult hit) {
		if (is.getCount() == 1) {
			if (world.getBlockEntity(pos) instanceof ElectrolyzerBlockEntity te && is.is(ReactorItems.HEAVY_WATER_BUCKET.get())) {
				if (!world.isClientSide && te.addHeavyWater(1000) && !ep.isCreative())
					ep.setItemInHand(hand, new ItemStack(Items.BUCKET));
				return ItemInteractionResult.sidedSuccess(world.isClientSide);
			}
			if (world.getBlockEntity(pos) instanceof SynthesizerBlockEntity te && is.is(Items.WATER_BUCKET)) {
				if (!world.isClientSide && te.addWater(1000) && !ep.isCreative())
					ep.setItemInHand(hand, new ItemStack(Items.BUCKET));
				return ItemInteractionResult.sidedSuccess(world.isClientSide);
			}
			if (world.getBlockEntity(pos) instanceof HeavyPumpBlockEntity te && te.getTankLevel() >= 1000) {
				Fluid f = te.getTank().getFluid().getFluid();
				ItemStack out = null;
				if (is.is(Items.BUCKET) && f.isSame(ReactorFluids.get("heavy_water")))
					out = new ItemStack(ReactorItems.HEAVY_WATER_BUCKET.get());
				else if (is.is(ReactorItems.EMPTY_CANISTER.get()) && f.isSame(ReactorFluids.get("lithium")))
					out = new ItemStack(ReactorItems.canisterFor(f));
				if (out != null) {
					if (!world.isClientSide) {
						te.getTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
						te.setChanged();
						ep.setItemInHand(hand, out);
					}
					return ItemInteractionResult.sidedSuccess(world.isClientSide);
				}
			}
		}
		return super.useItemOn(is, state, world, pos, ep, hand, hit);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player ep, BlockHitResult hit) {
		if (ep.isShiftKeyDown())
			return InteractionResult.PASS;
		if (world.getBlockEntity(pos) instanceof UProcessorBlockEntity te) {
			if (!world.isClientSide && ep instanceof ServerPlayer sp && te.isPlayerAccessible(sp))
				sp.openMenu(new SimpleMenuProvider((id, inv, p) -> new UProcessorMenu(id, inv, te), state.getBlock().getName()), buf -> buf.writeBlockPos(pos));
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
		if (world.getBlockEntity(pos) instanceof IsoCentrifugeBlockEntity te) {
			if (!world.isClientSide && ep instanceof ServerPlayer sp && te.isPlayerAccessible(sp))
				sp.openMenu(new SimpleMenuProvider((id, inv, p) -> new IsoCentrifugeMenu(id, inv, te), state.getBlock().getName()),
						buf -> buf.writeBlockPos(pos));
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
		if (world.getBlockEntity(pos) instanceof ElectrolyzerBlockEntity te) {
			if (!world.isClientSide && ep instanceof ServerPlayer sp && te.isPlayerAccessible(sp))
				sp.openMenu(new SimpleMenuProvider((id, inv, p) -> new ElectrolyzerMenu(id, inv, te), state.getBlock().getName()),
						buf -> buf.writeBlockPos(pos));
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
		if (world.getBlockEntity(pos) instanceof SynthesizerBlockEntity te) {
			if (!world.isClientSide && ep instanceof ServerPlayer sp && te.isPlayerAccessible(sp))
				sp.openMenu(new SimpleMenuProvider((id, inv, p) -> new SynthesizerMenu(id, inv, te), state.getBlock().getName()),
						buf -> buf.writeBlockPos(pos));
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
		if (world.getBlockEntity(pos) instanceof WasteContainerBlockEntity te) {
			if (!world.isClientSide && ep instanceof ServerPlayer sp && te.isPlayerAccessible(sp))
				sp.openMenu(new SimpleMenuProvider((id, inv, p) -> new WasteMenus.Container(id, inv, te), state.getBlock().getName()),
						buf -> buf.writeBlockPos(pos));
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
		if (world.getBlockEntity(pos) instanceof WasteStorageBlockEntity te) {
			if (!world.isClientSide && ep instanceof ServerPlayer sp && te.isPlayerAccessible(sp))
				sp.openMenu(new SimpleMenuProvider((id, inv, p) -> new WasteMenus.Storage(id, inv, te), state.getBlock().getName()),
						buf -> buf.writeBlockPos(pos));
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
		if (world.getBlockEntity(pos) instanceof CpuBlockEntity te) {
			if (!world.isClientSide && ep instanceof ServerPlayer sp && te.isPlayerAccessible(sp))
				sp.openMenu(new SimpleMenuProvider((id, inv, p) -> new CpuMenu(id, inv, te), state.getBlock().getName()),
						buf -> buf.writeBlockPos(pos));
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
		return super.useWithoutItem(state, world, pos, ep, hit);
	}
}

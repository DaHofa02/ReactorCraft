package reika.reactorcraft;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.registries.DeferredRegister;

import reika.reactorcraft.content.item.CanisterItem;
import reika.reactorcraft.content.item.GeigerCounterItem;
import reika.reactorcraft.content.item.NuclearWasteItem;
import reika.reactorcraft.content.item.RadiationCleanerItem;
import reika.reactorcraft.content.item.RemoteControlItem;
import reika.reactorcraft.network.ReactorPayloads;
import reika.reactorcraft.radiation.WasteManager;
import reika.reactorcraft.reactor.ReactorChunkLoading;
import reika.reactorcraft.registry.FluoriteTypes;
import reika.reactorcraft.registry.ReactorBlockEntities;
import reika.reactorcraft.registry.ReactorBlocks;
import reika.reactorcraft.registry.ReactorComponents;
import reika.reactorcraft.registry.ReactorEffects;
import reika.reactorcraft.registry.ReactorEntities;
import reika.reactorcraft.registry.ReactorFluids;
import reika.reactorcraft.registry.ReactorItems;
import reika.reactorcraft.registry.ReactorMenus;
import reika.reactorcraft.registry.ReactorOres;
import reika.reactorcraft.registry.ReactorSounds;
import reika.reactorcraft.registry.ReactorWorldGen;

import reika.rotarycraft.content.blockentity.CompactorRecipes;
import reika.rotarycraft.content.blockentity.FuelEnhancerBlockEntity;

/** Original: Reika.ReactorCraft.ReactorCraft. Private NeoForge 1.21.1 port by way of the RotaryCraft port; not for distribution. */
@Mod(ReactorCraft.MOD_ID)
public final class ReactorCraft {

	public static final String MOD_ID = "reactorcraft";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

	/** Original tabRctrItems ("ReactorCraft Items"; its icon, nuclear waste, is not ported yet). */
	static {
		TABS.register("items", () -> CreativeModeTab.builder()
				.title(Component.translatable("itemGroup.reactorcraft_items"))
				.icon(() -> new ItemStack(ReactorItems.ingot(ReactorItems.Ingot.URANIUM).get()))
				.displayItems((params, out) -> {
					out.accept(ReactorItems.HANDBOOK.get());
					out.accept(ReactorBlocks.FUEL_ROD.get());
					out.accept(ReactorBlocks.WATER_CELL.get());
					out.accept(ReactorBlocks.CONTROL_ROD.get());
					out.accept(ReactorBlocks.CPU.get());
					out.accept(ReactorBlocks.BOILER.get());
					out.accept(ReactorBlocks.BREEDER_CORE.get());
					out.accept(ReactorBlocks.SODIUM_HEATER.get());
					out.accept(ReactorBlocks.PEBBLE_BED.get());
					out.accept(ReactorBlocks.CO2_HEATER.get());
					out.accept(ReactorBlocks.THORIUM_CORE.get());
					out.accept(ReactorBlocks.FUEL_DUMP.get());
					out.accept(ReactorBlocks.WASTE_PIPE.get());
					out.accept(ReactorBlocks.THORIUM_FUEL.get());
					out.accept(ReactorBlocks.STEAM_LINE.get());
					out.accept(ReactorBlocks.HEAT_PIPE.get());
					out.accept(ReactorBlocks.STEAM_GRATE.get());
					out.accept(ReactorBlocks.TURBINE_CORE.get());
					out.accept(ReactorBlocks.CONDENSER.get());
					out.accept(ReactorBlocks.HIP_TURBINE.get());
					out.accept(ReactorBlocks.TURBINE_METER.get());
					out.accept(ReactorBlocks.STEAM_DIFFUSER.get());
					out.accept(ReactorBlocks.PRESSURIZER.get());
					out.accept(ReactorBlocks.TURBINE_BLADES.get());
					out.accept(ReactorBlocks.TURBINE_HOUSING.get());
					out.accept(ReactorBlocks.STEAM_INJECTOR.get());
					out.accept(ReactorBlocks.TRITIZER.get());
					out.accept(ReactorBlocks.SOLAR_EXCHANGER.get());
					out.accept(ReactorBlocks.SOLAR_TOP.get());
					out.accept(ReactorBlocks.GENERATOR.get());
					out.accept(ReactorBlocks.GENERATOR_CORE.get());
					out.accept(ReactorBlocks.GENERATOR_WINDING.get());
					out.accept(ReactorBlocks.GENERATOR_HOUSING.get());
					out.accept(ReactorBlocks.STEAM_BYPASS.get());
					out.accept(ReactorBlocks.FLYWHEEL.get());
					out.accept(ReactorBlocks.FLYWHEEL_CORE.get());
					out.accept(ReactorBlocks.FLYWHEEL_DAMPENER.get());
					out.accept(ReactorBlocks.FLYWHEEL_FRAME.get());
					out.accept(ReactorBlocks.HEAT_EXCHANGER.get());
					out.accept(ReactorBlocks.WASTE_CONTAINER.get());
					out.accept(ReactorBlocks.WASTE_STORAGE.get());
					out.accept(ReactorBlocks.WASTE_DECAYER.get());
					out.accept(ReactorBlocks.FLUID_EXTRACTOR.get());
					out.accept(ReactorBlocks.PROCESSOR.get());
					out.accept(ReactorBlocks.SYNTHESIZER.get());
					out.accept(ReactorBlocks.ELECTROLYZER.get());
					out.accept(ReactorBlocks.ISO_CENTRIFUGE.get());
					out.accept(ReactorBlocks.TOROID_MAGNET.get());
					out.accept(ReactorBlocks.SOLENOID.get());
					out.accept(ReactorBlocks.FUSION_INJECTOR.get());
					out.accept(ReactorBlocks.FUSION_HEATER.get());
					out.accept(ReactorBlocks.NEUTRON_ABSORBER.get());
					out.accept(ReactorBlocks.NEUTRON_REFLECTOR.get());
					out.accept(ReactorBlocks.MAGNETIC_PIPE.get());
					out.accept(ReactorBlocks.GAS_DUCT.get());
					out.accept(ReactorBlocks.GAS_COLLECTOR.get());
					out.accept(ReactorBlocks.FUSION_MARKER.get());
					for (var b : ReactorBlocks.SOLENOID_MULTI)
						out.accept(b.get());
					for (var b : ReactorBlocks.INJECTOR_MULTI)
						out.accept(b.get());
					for (var b : ReactorBlocks.HEATER_MULTI)
						out.accept(b.get());
					for (ReactorOres ore : ReactorOres.oreList)
						if (ore != ReactorOres.FLUORITE)
							out.accept(ReactorBlocks.ore(ore).get());
					for (FluoriteTypes f : FluoriteTypes.colorList)
						out.accept(ReactorBlocks.fluoriteOre(f).get());
					for (FluoriteTypes f : FluoriteTypes.colorList)
						out.accept(ReactorBlocks.fluoriteBlock(f).get());
					out.accept(ReactorBlocks.CALCITE_BLOCK.get());
					out.accept(ReactorBlocks.CONCRETE.get());
					out.accept(ReactorBlocks.GRAPHITE_BLOCK.get());
					for (ReactorItems.Crafting c : ReactorItems.Crafting.values())
						out.accept(ReactorItems.crafting(c).get());
					for (ReactorItems.Raw r : ReactorItems.Raw.values())
						out.accept(ReactorItems.raw(r).get());
					for (FluoriteTypes f : FluoriteTypes.colorList)
						out.accept(ReactorItems.fluorite(f).get());
					for (ReactorItems.Ingot i : ReactorItems.Ingot.values())
						out.accept(ReactorItems.ingot(i).get());
					out.accept(ReactorItems.HEAVY_WATER_BUCKET.get());
					out.accept(ReactorItems.FUEL.get());
					out.accept(ReactorItems.DEPLETED.get());
					out.accept(ReactorItems.PLUTONIUM.get());
					out.accept(ReactorItems.BREEDER_FUEL.get());
					out.accept(ReactorItems.GOGGLES.get());
					out.accept(ReactorItems.HAZ_HELMET.get());
					out.accept(ReactorItems.HAZ_CHEST.get());
					out.accept(ReactorItems.HAZ_LEGS.get());
					out.accept(ReactorItems.HAZ_BOOTS.get());
					out.accept(ReactorItems.GEIGER.get()); //original getSubItems: uncharged and charged (32000 kJ)
					out.accept(GeigerCounterItem.charged(ReactorItems.GEIGER.get(), GeigerCounterItem.CREATIVE_CHARGE));
					out.accept(ReactorItems.CLEANER.get()); //original getSubItems: empty and charged with full water
					out.accept(RadiationCleanerItem.charged(ReactorItems.CLEANER.get()));
					out.accept(ReactorItems.IRON_FINDER.get());
					out.accept(ReactorItems.REMOTE.get()); //original getSubItems: uncharged and charged (32000 kJ)
					out.accept(GeigerCounterItem.charged(ReactorItems.REMOTE.get(), RemoteControlItem.CREATIVE_CHARGE));
					out.accept(ReactorItems.PELLET.get());
					out.accept(ReactorItems.OLD_PELLET.get());
					for (var iso : WasteManager.getWasteList()) //original getSubItems: the waste isotopes
						out.accept(NuclearWasteItem.of(iso));
					for (var m : ReactorItems.MAGNETS)
						out.accept(m.get());
					out.accept(ReactorItems.EMPTY_CANISTER.get());
					for (var c : ReactorItems.filledCanisters())
						out.accept(c.get());
				}).build());
	}

	public ReactorCraft(IEventBus modBus, ModContainer container) {
		ReactorBlocks.BLOCKS.register(modBus);
		ReactorItems.ITEMS.register(modBus);
		ReactorItems.ARMOR_MATERIALS.register(modBus);
		ReactorWorldGen.PLACEMENTS.register(modBus);
		ReactorBlockEntities.TYPES.register(modBus);
		ReactorComponents.COMPONENTS.register(modBus);
		ReactorEffects.EFFECTS.register(modBus);
		ReactorEntities.TYPES.register(modBus);
		ReactorMenus.MENUS.register(modBus);
		ReactorSounds.SOUNDS.register(modBus);
		modBus.addListener(ReactorPayloads::register);
		ReactorFluids.TYPES.register(modBus);
		ReactorFluids.FLUIDS.register(modBus);
		modBus.addListener(ReactorCraft::registerCapabilities);
		modBus.addListener(ReactorCraft::commonSetup);
		modBus.addListener(ReactorChunkLoading::register);
		TABS.register(modBus);
		container.registerConfig(ModConfig.Type.COMMON, ReactorConfig.SPEC);
		//original ReactorRecipes: TileEntityFuelConverter.addRecipe("rc lifbe", "rc lifbe fuel", 10, 1, 0.1, new ItemMatch("dustThorium"))
		FuelEnhancerBlockEntity.addRecipe(() -> ReactorFluids.get("lifbe"), () -> ReactorFluids.get("lifbe_fuel"), 10, 1, 0.1,
				Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dusts/thorium"))));
	}

	/** Original ReactorRecipes: RecipesCompactor lodestone -> 2 magnets (5000 kPa, 100 C), each magnet -> 2 of the next (10000*(1+level) kPa). */
	private static void commonSetup(FMLCommonSetupEvent e) {
		e.enqueueWork(() -> {
			CompactorRecipes.add(() -> new ItemStack(ReactorItems.raw(ReactorItems.Raw.MAGNETITE).get()),
					() -> new ItemStack(ReactorItems.MAGNETS.get(0).get(), 2), 5000, 100);
			for (int i = 0; i < ReactorItems.MAGNETS.size()-1; i++) {
				int lvl = i;
				CompactorRecipes.add(() -> new ItemStack(ReactorItems.MAGNETS.get(lvl).get()),
						() -> new ItemStack(ReactorItems.MAGNETS.get(lvl+1).get(), 2), 10000*(1+lvl), 100);
			}
		});
	}

	private static void registerCapabilities(RegisterCapabilitiesEvent e) {
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.TOROID_MAGNET.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.FUSION_HEATER.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.GAS_COLLECTOR.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new CanisterItem.Handler(stack),
				ReactorItems.EMPTY_CANISTER.get());
		for (var c : ReactorItems.filledCanisters())
			e.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new CanisterItem.Handler(stack), c.get());
		e.registerItem(Capabilities.FluidHandler.ITEM,
				(stack, ctx) -> new FluidBucketWrapper(stack), ReactorItems.HEAVY_WATER_BUCKET.get());
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.THORIUM_CORE.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.CO2_HEATER.get(),
				(te, side) -> te.getFluidHandler(side));
		//original TileEntityFuelDump is a fluid handler (TileEntityTankedReactorMachine) whose drains reach the core above; without the
		//capability RotaryCraft pipes never interact with it
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.FUEL_DUMP.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ReactorBlockEntities.PEBBLE_BED.get(),
				(te, side) -> te.getItemHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.SODIUM_HEATER.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ReactorBlockEntities.BREEDER_CORE.get(),
				(te, side) -> te.getItemHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.HEAT_EXCHANGER.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.CONDENSER.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.STEAM_DIFFUSER.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.PRESSURIZER.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.BOILER.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.SOLAR_EXCHANGER.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.TRITIZER.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ReactorBlockEntities.GENERATOR.get(),
				(te, side) -> te.getEnergyStorage(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.HIP_TURBINE.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.STEAM_INJECTOR.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.TURBINE_CORE.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.HEAVY_PUMP.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.PROCESSOR.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ReactorBlockEntities.WASTE_CONTAINER.get(),
				(te, side) -> te.getItemHandler(side));
		e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ReactorBlockEntities.WASTE_STORAGE.get(),
				(te, side) -> te.getItemHandler(side));
		e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ReactorBlockEntities.WASTE_DECAYER.get(),
				(te, side) -> te.getItemHandler(side));
		e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ReactorBlockEntities.PROCESSOR.get(),
				(te, side) -> te.getItemHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.ISO_CENTRIFUGE.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ReactorBlockEntities.ISO_CENTRIFUGE.get(),
				(te, side) -> te.getItemHandler(side));
		e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ReactorBlockEntities.FUEL_ROD.get(),
				(te, side) -> te.getItemHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.ELECTROLYZER.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ReactorBlockEntities.ELECTROLYZER.get(),
				(te, side) -> te.getItemHandler(side));
		e.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ReactorBlockEntities.SYNTHESIZER.get(),
				(te, side) -> te.getFluidHandler(side));
		e.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ReactorBlockEntities.SYNTHESIZER.get(),
				(te, side) -> te.getItemHandler(side));
	}
}

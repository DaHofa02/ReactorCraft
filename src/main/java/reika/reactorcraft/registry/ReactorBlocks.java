/*******************************************************************************
 * @author Reika Kalseki (original ReactorCraft). Private NeoForge 1.21.1 port; not for distribution.
 ******************************************************************************/
package reika.reactorcraft.registry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import reika.reactorcraft.ReactorCraft;
import reika.reactorcraft.content.block.FluoriteBlock;
import reika.reactorcraft.content.block.FlywheelMultiBlock;
import reika.reactorcraft.content.block.GasDuctBlock;
import reika.reactorcraft.content.block.GeneratorMultiBlock;
import reika.reactorcraft.content.block.HeatPipeBlock;
import reika.reactorcraft.content.block.MagneticPipeBlock;
import reika.reactorcraft.content.block.ReactorCoreBlock;
import reika.reactorcraft.content.block.ReactorGuiMachineBlock;
import reika.reactorcraft.content.block.ReactorOreBlock;
import reika.reactorcraft.content.block.SteamBlock;
import reika.reactorcraft.content.block.SteamLineBlock;
import reika.reactorcraft.content.block.ThoriumFuelBlock;
import reika.reactorcraft.content.block.TurbineMultiBlock;
import reika.reactorcraft.content.block.WastePipeBlock;
import reika.reactorcraft.fusion.FusionMachineBlock;
import reika.reactorcraft.fusion.FusionMultiBlock;

import reika.rotarycraft.content.block.MachineBlock;
import reika.rotarycraft.content.blockentity.RotaryCraftBlockEntity;
import reika.rotarycraft.content.item.MachineBlockItem;
import reika.rotarycraft.lib.LegacyResistance;
import reika.rotarycraft.registry.RotaryBlocks;

/** Material.rock in the original: all need a pickaxe. Original: ReactorBlocks ORE, FLUORITEORE, FLUORITE and MatBlocks.CALCITE (one block per former metadata value). */
public final class ReactorBlocks {

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ReactorCraft.MOD_ID);

	private static final Map<ReactorOres, DeferredBlock<ReactorOreBlock>> ORES = new EnumMap<>(ReactorOres.class);
	private static final Map<FluoriteTypes, DeferredBlock<FluoriteBlock>> FLUORITE_ORES = new EnumMap<>(FluoriteTypes.class);
	private static final Map<FluoriteTypes, DeferredBlock<FluoriteBlock>> FLUORITE_BLOCKS = new EnumMap<>(FluoriteTypes.class);

	static {
		for (ReactorOres ore : ReactorOres.oreList) {
			if (ore == ReactorOres.FLUORITE)
				continue;
			MapColor c = ore.dimensionID == -1 ? MapColor.NETHER : ore.dimensionID == 1 ? MapColor.SAND : MapColor.STONE;
			DeferredBlock<ReactorOreBlock> b = BLOCKS.register(ore.getName()+"_ore", () -> new ReactorOreBlock(BlockBehaviour.Properties.of().mapColor(c)
					.instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2F, LegacyResistance.of(5F)), ore));
			ReactorItems.ITEMS.registerSimpleBlockItem(b);
			ORES.put(ore, b);
		}
		for (FluoriteTypes f : FluoriteTypes.colorList) {
			DeferredBlock<FluoriteBlock> ore = BLOCKS.register(f.getColorName()+"_fluorite_ore", () -> new FluoriteBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.2F, LegacyResistance.of(4F)), f, true));
			ReactorItems.ITEMS.registerSimpleBlockItem(ore);
			FLUORITE_ORES.put(f, ore);
			DeferredBlock<FluoriteBlock> blk = BLOCKS.register(f.getColorName()+"_fluorite_block", () -> new FluoriteBlock(BlockBehaviour.Properties.of()
					.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.2F, LegacyResistance.of(4F)), f, false));
			ReactorItems.ITEMS.registerSimpleBlockItem(blk);
			FLUORITE_BLOCKS.put(f, blk);
		}
	}

	/** Original MatBlocks.CALCITE (9 calcite crystals). */
	public static final DeferredBlock<Block> CALCITE_BLOCK = BLOCKS.registerSimpleBlock("calcite_block", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).requiresCorrectToolForDrops().strength(1.5F, LegacyResistance.of(10F)));

	/** Original MatBlocks.CONCRETE and GRAPHITE (BlockReactorMat: hardness 1.5, resistance 10, rock). */
	public static final DeferredBlock<Block> CONCRETE = BLOCKS.registerSimpleBlock("concrete", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, LegacyResistance.of(10F)));
	public static final DeferredBlock<Block> GRAPHITE_BLOCK = BLOCKS.registerSimpleBlock("graphite_block", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(1.5F, LegacyResistance.of(10F)));

	static {
		ReactorItems.ITEMS.registerSimpleBlockItem(CALCITE_BLOCK);
		ReactorItems.ITEMS.registerSimpleBlockItem(CONCRETE);
		ReactorItems.ITEMS.registerSimpleBlockItem(GRAPHITE_BLOCK);
	}

	/** Original ReactorTiles.FLUIDEXTRACTOR (BlockReactorTileModelled; harvest and drops as RotaryCraft machines; 4-sided placement as all ReC machines). */
	public static final DeferredBlock<MachineBlock> FLUID_EXTRACTOR = machine("fluid_extractor", ReactorBlockEntities.HEAVY_PUMP);
	/** Original ReactorTiles.SYNTHESIZER (ReactorBlocks.MACHINE: a full block with its own textures). */
	public static final DeferredBlock<MachineBlock> SYNTHESIZER = machine("synthesizer", ReactorBlockEntities.SYNTHESIZER);
	/** Original ReactorTiles.CENTRIFUGE (Isotope Centrifuge). */
	public static final DeferredBlock<MachineBlock> ISO_CENTRIFUGE = machine("isocentrifuge", ReactorBlockEntities.ISO_CENTRIFUGE);
	/** Original ReactorTiles.PROCESSOR; opens its GUI on right-click. */
	public static final DeferredBlock<MachineBlock> PROCESSOR = BLOCKS.register("processor", () -> new ReactorGuiMachineBlock(
			RotaryBlocks.machineProperties().strength(2F, LegacyResistance.of(10F)), ReactorBlockEntities.PROCESSOR));

	static {
		ReactorItems.ITEMS.register("processor", () -> new MachineBlockItem(PROCESSOR.get(), new Item.Properties()));
	}

	private static DeferredBlock<MachineBlock> machine(String name,
			Supplier<? extends BlockEntityType<? extends RotaryCraftBlockEntity>> type) {
		DeferredBlock<MachineBlock> b = BLOCKS.register(name, () -> new ReactorGuiMachineBlock(
				RotaryBlocks.machineProperties().strength(2F, LegacyResistance.of(10F)), type));
		ReactorItems.ITEMS.register(name, () -> new MachineBlockItem(b.get(), new Item.Properties()));
		return b;
	}

	/** Original BlockCoriumFlowing (hardness 100, resistance 500, lets light through). */
	public static final DeferredBlock<LiquidBlock> CORIUM = BLOCKS.register("corium", () -> new LiquidBlock(
			(FlowingFluid)ReactorFluids.get("corium"), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).replaceable().noCollission()
			.strength(100F, LegacyResistance.of(500F)).pushReaction(PushReaction.DESTROY).noLootTable().liquid()
			.sound(SoundType.EMPTY)));
	/** Original ReactorTiles.FUEL and COOLANT (ReactorBlocks.REACTOR: textured blocks, hardness 2, resistance 10). */
	public static final DeferredBlock<ReactorCoreBlock> FUEL_ROD = core("fuel_rod", ReactorBlockEntities.FUEL_ROD);
	public static final DeferredBlock<ReactorCoreBlock> WATER_CELL = core("coolant_cell", ReactorBlockEntities.WATER_CELL);

	/** Original ReactorTiles.CONTROL (BlockReactorTileModelled: the model is drawn by its renderer). */
	public static final DeferredBlock<MachineBlock> CONTROL_ROD = machine("control_rod", ReactorBlockEntities.CONTROL_ROD);
	/** Original ReactorTiles.CPU (ReactorBlocks.MACHINE: a full block with its own texture); opens its GUI on right-click. */
	public static final DeferredBlock<MachineBlock> CPU = machine("cpu", ReactorBlockEntities.CPU);

	/** Original ReactorTiles.BOILER (ReactorBlocks.REACTOR: a textured block whose sides show its position in a boiler column). */
	public static final DeferredBlock<ReactorCoreBlock> BOILER = core("reactor_boiler", ReactorBlockEntities.BOILER);
	/** Original ReactorTiles.GRATE and TURBINECORE (BlockReactorTileModelled). */
	public static final DeferredBlock<MachineBlock> STEAM_GRATE = machine("steam_grate", ReactorBlockEntities.STEAM_GRATE);
	public static final DeferredBlock<MachineBlock> TURBINE_CORE = machine("turbine_core", ReactorBlockEntities.TURBINE_CORE);
	/** Original ReactorTiles.BREEDER and SODIUMBOILER (ReactorBlocks.REACTOR, column textures). */
	public static final DeferredBlock<ReactorCoreBlock> BREEDER_CORE = core("breeder_core", ReactorBlockEntities.BREEDER_CORE);
	public static final DeferredBlock<ReactorCoreBlock> SODIUM_HEATER = core("sodium_heater", ReactorBlockEntities.SODIUM_HEATER);
	/** Original ReactorTiles.PEBBLEBED and CO2HEATER (ReactorBlocks.REACTOR, column textures). */
	public static final DeferredBlock<ReactorCoreBlock> PEBBLE_BED = core("pebble_bed", ReactorBlockEntities.PEBBLE_BED);
	public static final DeferredBlock<ReactorCoreBlock> CO2_HEATER = core("co2_heater", ReactorBlockEntities.CO2_HEATER);
	/** Original ReactorTiles.THORIUM and FUELDUMP (ReactorBlocks.REACTOR) and WASTEPIPE (ReactorBlocks.DUCT). */
	public static final DeferredBlock<ReactorCoreBlock> THORIUM_CORE = core("thorium_core", ReactorBlockEntities.THORIUM_CORE);
	public static final DeferredBlock<ReactorCoreBlock> FUEL_DUMP = core("fuel_dump", ReactorBlockEntities.FUEL_DUMP);
	public static final DeferredBlock<WastePipeBlock> WASTE_PIPE = BLOCKS.register("waste_pipe",
			//original ReactorBlocks.DUCT (BlockDuct): hardness 1, setResistance(1)
			() -> new WastePipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1F, LegacyResistance.of(1F))
					.sound(SoundType.STONE).noOcclusion()));
	/** Original ReactorBlocks.THORIUM (BlockThoriumFuel). */
	public static final DeferredBlock<ThoriumFuelBlock> THORIUM_FUEL = BLOCKS.register("thorium_fuel",
			() -> new ThoriumFuelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(100F, LegacyResistance.of(500F))
					.noLootTable().pushReaction(PushReaction.DESTROY).noOcclusion()));

	static {
		ReactorItems.ITEMS.registerSimpleBlockItem(WASTE_PIPE);
		ReactorItems.ITEMS.registerSimpleBlockItem(THORIUM_FUEL);
	}

	/** Original ReactorTiles.WASTECONTAINER (ReactorBlocks.MACHINE, end textured), STORAGE (modelled) and WASTEDECAYER (ReactorBlocks.REACTOR, column texture). */
	public static final DeferredBlock<MachineBlock> WASTE_CONTAINER = machine("waste_container", ReactorBlockEntities.WASTE_CONTAINER);
	public static final DeferredBlock<MachineBlock> WASTE_STORAGE = machine("waste_storage", ReactorBlockEntities.WASTE_STORAGE);
	public static final DeferredBlock<ReactorCoreBlock> WASTE_DECAYER = core("waste_decayer", ReactorBlockEntities.WASTE_DECAYER);
	/** Original ReactorTiles.EXCHANGER (ReactorBlocks.MODELMACHINE). */
	public static final DeferredBlock<MachineBlock> HEAT_EXCHANGER = machine("heat_exchanger", ReactorBlockEntities.HEAT_EXCHANGER);
	/** Original ReactorTiles.BIGTURBINE (high pressure turbine core, modelled) and BlockTurbineMulti (blades, housing, steam injector). */
	public static final DeferredBlock<MachineBlock> HIP_TURBINE = machine("hip_turbine", ReactorBlockEntities.HIP_TURBINE);
	public static final DeferredBlock<TurbineMultiBlock> TURBINE_BLADES = turbineMulti("turbine_blades", 0);
	public static final DeferredBlock<TurbineMultiBlock> TURBINE_HOUSING = turbineMulti("turbine_housing", 1);
	public static final DeferredBlock<TurbineMultiBlock> STEAM_INJECTOR = turbineMulti("steam_injector", 2);

	/** Original BlockReCMultiBlock: hardness 2, resistance 10. */
	private static DeferredBlock<TurbineMultiBlock> turbineMulti(String name, int variant) {
		DeferredBlock<TurbineMultiBlock> b = BLOCKS.register(name, () -> new TurbineMultiBlock(
				BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2F, LegacyResistance.of(10F)).requiresCorrectToolForDrops().sound(SoundType.METAL)
						.noOcclusion(), variant));
		ReactorItems.ITEMS.registerSimpleBlockItem(b);
		return b;
	}

	/** Original ReactorTiles.GENERATOR (modelled) and BlockGeneratorMulti (rotor core, rotor windings, housing, steam bypass). */
	public static final DeferredBlock<MachineBlock> GENERATOR = machine("reactor_generator", ReactorBlockEntities.GENERATOR);
	public static final DeferredBlock<GeneratorMultiBlock> GENERATOR_CORE = generatorMulti("generator_core", 0);
	public static final DeferredBlock<GeneratorMultiBlock> GENERATOR_WINDING = generatorMulti("generator_winding", 1);
	public static final DeferredBlock<GeneratorMultiBlock> GENERATOR_HOUSING = generatorMulti("generator_housing", 2);
	public static final DeferredBlock<GeneratorMultiBlock> STEAM_BYPASS = generatorMulti("steam_bypass", 3);

	/** Original ReactorTiles.ELECTROLYZER (modelled). */
	public static final DeferredBlock<MachineBlock> ELECTROLYZER = machine("electrolyzer", ReactorBlockEntities.ELECTROLYZER);
	/** Original ReactorTiles.SOLAR (solar exchanger) and SOLARTOP (sodium receiver), both modelled. */
	public static final DeferredBlock<MachineBlock> SOLAR_EXCHANGER = machine("solar_exchanger", ReactorBlockEntities.SOLAR_EXCHANGER);
	public static final DeferredBlock<MachineBlock> SOLAR_TOP = machine("solar_top", ReactorBlockEntities.SOLAR_TOP);
	/** Original ReactorTiles.TRITIZER (ReactorBlocks.REACTOR, column textures, ends #4). */
	public static final DeferredBlock<ReactorCoreBlock> TRITIZER = core("tritizer", ReactorBlockEntities.TRITIZER);
	/** Original ReactorTiles.FLYWHEEL (modelled) and BlockFlywheelMulti (flywheel core, vibration dampeners, frame). */
	public static final DeferredBlock<MachineBlock> FLYWHEEL = machine("turbine_flywheel", ReactorBlockEntities.FLYWHEEL);
	public static final DeferredBlock<FlywheelMultiBlock> FLYWHEEL_CORE = flywheelMulti("flywheel_core", 0);
	public static final DeferredBlock<FlywheelMultiBlock> FLYWHEEL_DAMPENER = flywheelMulti("flywheel_dampener", 1);
	public static final DeferredBlock<FlywheelMultiBlock> FLYWHEEL_FRAME = flywheelMulti("flywheel_frame", 2);

	/** Original BlockReCMultiBlock: hardness 2, resistance 10. */
	private static DeferredBlock<FlywheelMultiBlock> flywheelMulti(String name, int variant) {
		DeferredBlock<FlywheelMultiBlock> b = BLOCKS.register(name, () -> new FlywheelMultiBlock(
				BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2F, LegacyResistance.of(10F)).requiresCorrectToolForDrops().sound(SoundType.METAL)
						.noOcclusion(), variant));
		ReactorItems.ITEMS.registerSimpleBlockItem(b);
		return b;
	}

	/** Original BlockReCMultiBlock: hardness 2, resistance 10. */
	private static DeferredBlock<GeneratorMultiBlock> generatorMulti(String name, int variant) {
		DeferredBlock<GeneratorMultiBlock> b = BLOCKS.register(name, () -> new GeneratorMultiBlock(
				BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2F, LegacyResistance.of(10F)).requiresCorrectToolForDrops().sound(SoundType.METAL)
						.noOcclusion(), variant));
		ReactorItems.ITEMS.registerSimpleBlockItem(b);
		return b;
	}

	/** Original ReactorTiles.CONDENSER (BlockReactorTileModelled). */
	public static final DeferredBlock<MachineBlock> CONDENSER = machine("condenser", ReactorBlockEntities.CONDENSER);
	/** Original ReactorTiles.DIFFUSER (BlockReactorTileModelled, 4-sided; no GUI). */
	public static final DeferredBlock<MachineBlock> STEAM_DIFFUSER = machine("steam_diffuser", ReactorBlockEntities.STEAM_DIFFUSER);
	/** Original ReactorTiles.PUMP, the Pressurizer (BlockReactorTileModelled; no GUI, no orientation of its own). */
	public static final DeferredBlock<MachineBlock> PRESSURIZER = machine("pressurizer", ReactorBlockEntities.PRESSURIZER);
	/** Original ReactorTiles.STEAMLINE (ReactorBlocks.LINE; the recipe makes 3). */
	public static final DeferredBlock<SteamLineBlock> STEAM_LINE = BLOCKS.register("steam_line",
			() -> new SteamLineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0F, LegacyResistance.of(1F))
					.sound(SoundType.WOOL).noOcclusion()));
	/** Original ReactorTiles.HEATPIPE (ReactorBlocks.LINE, BlockSteamLine like the steam line: hardness 0, resistance 1). */
	public static final DeferredBlock<HeatPipeBlock> HEAT_PIPE = BLOCKS.register("heat_pipe",
			() -> new HeatPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(0F, LegacyResistance.of(1F))
					.sound(SoundType.WOOL).noOcclusion()));
	/** Original ReactorBlocks.STEAM (BlockSteam): no collision, replaceable, translucent, resistance 3600000, no drops. */
	public static final DeferredBlock<SteamBlock> STEAM = BLOCKS.register("steam",
			() -> new SteamBlock(BlockBehaviour.Properties.of().replaceable().noCollission().noOcclusion().noLootTable()
					.strength(0F, 3600000F).randomTicks().sound(SoundType.WOOL).isViewBlocking((s, w, p) -> false)
					.isSuffocating((s, w, p) -> false).pushReaction(PushReaction.DESTROY)));

	static {
		ReactorItems.ITEMS.registerSimpleBlockItem(STEAM_LINE);
		ReactorItems.ITEMS.registerSimpleBlockItem(HEAT_PIPE);
	}

	private static DeferredBlock<ReactorCoreBlock> core(String name,
			Supplier<? extends BlockEntityType<? extends RotaryCraftBlockEntity>> type) {
		DeferredBlock<ReactorCoreBlock> b = BLOCKS.register(name, () -> new ReactorCoreBlock(
				BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2F, LegacyResistance.of(10F)).requiresCorrectToolForDrops().sound(SoundType.METAL), type));
		ReactorItems.ITEMS.register(name, () -> new MachineBlockItem(b.get(), new Item.Properties()));
		return b;
	}

	/** Original ReactorTiles MAGNET, SOLENOID (BlockReactorTileModelled), INJECTOR, HEATER, ABSORBER (ReactorBlocks.REACTOR), COLLECTOR and
	 * MARKER (MODELMACHINE), MAGNETPIPE (DUCT) and the fusion multiblocks (BlockSolenoidMulti, BlockInjectorMulti, BlockHeaterMulti). */
	public static final DeferredBlock<FusionMachineBlock> TOROID_MAGNET = fusion("toroid_magnet", () -> ReactorBlockEntities.TOROID_MAGNET.get(), true);
	public static final DeferredBlock<FusionMachineBlock> SOLENOID = fusion("solenoid", () -> ReactorBlockEntities.SOLENOID.get(), true);
	public static final DeferredBlock<FusionMachineBlock> FUSION_INJECTOR = fusion("fusion_injector", () -> ReactorBlockEntities.FUSION_INJECTOR.get(), false);
	public static final DeferredBlock<FusionMachineBlock> FUSION_HEATER = fusion("fusion_heater", () -> ReactorBlockEntities.FUSION_HEATER.get(), false);
	public static final DeferredBlock<FusionMachineBlock> NEUTRON_ABSORBER = fusion("neutron_absorber", () -> ReactorBlockEntities.NEUTRON_ABSORBER.get(), false);
	/** Original ReactorTiles.REFLECTOR (ReactorBlocks.REACTOR = BlockReactorTile: hardness 2, setResistance(10); no orientation, textured cube). */
	public static final DeferredBlock<MachineBlock> NEUTRON_REFLECTOR = BLOCKS.register("neutron_reflector",
			() -> new MachineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
					.strength(2F, LegacyResistance.of(10F)).requiresCorrectToolForDrops().sound(SoundType.METAL),
					ReactorBlockEntities.NEUTRON_REFLECTOR, MachineBlock.Placement.NONE, false));

	static {
		ReactorItems.ITEMS.register("neutron_reflector", () -> new MachineBlockItem(NEUTRON_REFLECTOR.get(), new Item.Properties()));
	}
	/** Original ReactorTiles.TURBINEMETER (ReactorBlocks.MACHINE = BlockReactorTile: hardness 2, setResistance(10); no GUI, no orientation; own top, side and bottom textures). */
	public static final DeferredBlock<MachineBlock> TURBINE_METER = BLOCKS.register("turbine_meter",
			() -> new MachineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
					.strength(2F, LegacyResistance.of(10F)).requiresCorrectToolForDrops().sound(SoundType.METAL),
					ReactorBlockEntities.TURBINE_METER, MachineBlock.Placement.NONE, false));

	static {
		ReactorItems.ITEMS.register("turbine_meter", () -> new MachineBlockItem(TURBINE_METER.get(), new Item.Properties()));
	}
	public static final DeferredBlock<FusionMachineBlock> GAS_COLLECTOR = fusion("gas_collector", () -> ReactorBlockEntities.GAS_COLLECTOR.get(), true);
	public static final DeferredBlock<FusionMachineBlock> FUSION_MARKER = fusion("fusion_marker", () -> ReactorBlockEntities.FUSION_MARKER.get(), true);
	public static final DeferredBlock<MagneticPipeBlock> MAGNETIC_PIPE = BLOCKS.register("magnetic_pipe",
			//original ReactorBlocks.DUCT (BlockDuct): hardness 1, setResistance(1)
			() -> new MagneticPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(1F, LegacyResistance.of(1F))
					.sound(SoundType.METAL).noOcclusion()));
	/** Original ReactorTiles.GASPIPE (ReactorBlocks.DUCT, BlockDuct: hardness 1, setResistance(1)). */
	public static final DeferredBlock<GasDuctBlock> GAS_DUCT = BLOCKS.register("gas_duct",
			() -> new GasDuctBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1F, LegacyResistance.of(1F))
					.sound(SoundType.STONE).noOcclusion()));

	public static final String[] SOLENOID_PARTS = {"solenoid_base", "solenoid_linkage", "solenoid_central_magnet", "solenoid_auxiliary_magnet", "solenoid_hysteresis_rod", "solenoid_hub"};
	public static final String[] INJECTOR_PARTS = {"injector_base", "injector_lower_corner", "injector_side_panel", "injector_top", "injector_upper_corner",
			"injector_induction_coil", "injector_column", "injector_hysteresis_core"};
	public static final String[] HEATER_PARTS = {"heater_lens", "heater_insulation_core", "heater_housing_corner", "heater_housing_edge", "heater_housing_face"};
	public static final List<DeferredBlock<FusionMultiBlock>> SOLENOID_MULTI = fusionMulti(SOLENOID_PARTS, FusionMultiBlock.Kind.SOLENOID);
	public static final List<DeferredBlock<FusionMultiBlock>> INJECTOR_MULTI = fusionMulti(INJECTOR_PARTS, FusionMultiBlock.Kind.INJECTOR);
	public static final List<DeferredBlock<FusionMultiBlock>> HEATER_MULTI = fusionMulti(HEATER_PARTS, FusionMultiBlock.Kind.HEATER);

	static {
		ReactorItems.ITEMS.registerSimpleBlockItem(MAGNETIC_PIPE);
		ReactorItems.ITEMS.registerSimpleBlockItem(GAS_DUCT);
	}

	private static DeferredBlock<FusionMachineBlock> fusion(String name,
			Supplier<? extends BlockEntityType<? extends RotaryCraftBlockEntity>> type, boolean modelled) {
		DeferredBlock<FusionMachineBlock> b = BLOCKS.register(name, () -> new FusionMachineBlock(modelled
				? RotaryBlocks.machineProperties().strength(2F, LegacyResistance.of(10F))
				: BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2F, LegacyResistance.of(10F)).requiresCorrectToolForDrops().sound(SoundType.METAL), type, false));
		ReactorItems.ITEMS.register(name, () -> new MachineBlockItem(b.get(), new Item.Properties()));
		return b;
	}

	/** Original BlockReCMultiBlock: hardness 2, resistance 10; only the heater's lens lets light (and the heat ray) through. */
	private static List<DeferredBlock<FusionMultiBlock>> fusionMulti(String[] names, FusionMultiBlock.Kind kind) {
		List<DeferredBlock<FusionMultiBlock>> li = new ArrayList<>();
		for (int i = 0; i < names.length; i++) {
			int v = i;
			boolean lens = kind == FusionMultiBlock.Kind.HEATER && v == 0;
			DeferredBlock<FusionMultiBlock> b = BLOCKS.register(names[i], () -> {
				BlockBehaviour.Properties p = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2F, LegacyResistance.of(10F)).requiresCorrectToolForDrops()
						.sound(SoundType.METAL);
				if (lens)
					p = p.noOcclusion().isViewBlocking((s, w, q) -> false).isRedstoneConductor((s, w, q) -> false);
				return new FusionMultiBlock(p, kind, v);
			});
			ReactorItems.ITEMS.registerSimpleBlockItem(b);
			li.add(b);
		}
		return li;
	}

	private ReactorBlocks() {}

	public static DeferredBlock<ReactorOreBlock> ore(ReactorOres ore) {
		return ORES.get(ore);
	}

	public static DeferredBlock<FluoriteBlock> fluoriteOre(FluoriteTypes f) {
		return FLUORITE_ORES.get(f);
	}

	public static DeferredBlock<FluoriteBlock> fluoriteBlock(FluoriteTypes f) {
		return FLUORITE_BLOCKS.get(f);
	}
}

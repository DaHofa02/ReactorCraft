/*******************************************************************************
 * @author Reika Kalseki (original ReactorCraft). Private NeoForge 1.21.1 port; not for distribution.
 ******************************************************************************/
package reika.reactorcraft.registry;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.neoforge.registries.DeferredRegister;

import reika.reactorcraft.ReactorCraft;
import reika.reactorcraft.content.blockentity.ElectrolyzerBlockEntity;
import reika.reactorcraft.content.blockentity.HeavyPumpBlockEntity;
import reika.reactorcraft.content.blockentity.IsoCentrifugeBlockEntity;
import reika.reactorcraft.content.blockentity.SynthesizerBlockEntity;
import reika.reactorcraft.content.blockentity.UProcessorBlockEntity;
import reika.reactorcraft.content.piping.GasDuctBlockEntity;
import reika.reactorcraft.content.piping.HeatPipeBlockEntity;
import reika.reactorcraft.fusion.FusionHeaterBlockEntity;
import reika.reactorcraft.fusion.FusionInjectorBlockEntity;
import reika.reactorcraft.fusion.FusionMarkerBlockEntity;
import reika.reactorcraft.fusion.GasCollectorBlockEntity;
import reika.reactorcraft.fusion.MagneticPipeBlockEntity;
import reika.reactorcraft.fusion.NeutronAbsorberBlockEntity;
import reika.reactorcraft.fusion.SolenoidMagnetBlockEntity;
import reika.reactorcraft.fusion.ToroidMagnetBlockEntity;
import reika.reactorcraft.powergen.CondenserBlockEntity;
import reika.reactorcraft.powergen.HeatExchangerBlockEntity;
import reika.reactorcraft.powergen.HiPTurbineBlockEntity;
import reika.reactorcraft.powergen.ReactorFlywheelBlockEntity;
import reika.reactorcraft.powergen.ReactorGeneratorBlockEntity;
import reika.reactorcraft.powergen.ReactorPumpBlockEntity;
import reika.reactorcraft.powergen.SolarExchangerBlockEntity;
import reika.reactorcraft.powergen.SolarTopBlockEntity;
import reika.reactorcraft.powergen.SteamDiffuserBlockEntity;
import reika.reactorcraft.powergen.SteamGrateBlockEntity;
import reika.reactorcraft.powergen.SteamInjectorBlockEntity;
import reika.reactorcraft.powergen.SteamLineBlockEntity;
import reika.reactorcraft.powergen.TurbineCoreBlockEntity;
import reika.reactorcraft.powergen.TurbineMeterBlockEntity;
import reika.reactorcraft.reactor.BreederCoreBlockEntity;
import reika.reactorcraft.reactor.CO2HeaterBlockEntity;
import reika.reactorcraft.reactor.ControlRodBlockEntity;
import reika.reactorcraft.reactor.CpuBlockEntity;
import reika.reactorcraft.reactor.FuelDumpBlockEntity;
import reika.reactorcraft.reactor.FuelRodBlockEntity;
import reika.reactorcraft.reactor.NeutronReflectorBlockEntity;
import reika.reactorcraft.reactor.PebbleBedBlockEntity;
import reika.reactorcraft.reactor.ReactorBoilerBlockEntity;
import reika.reactorcraft.reactor.SodiumHeaterBlockEntity;
import reika.reactorcraft.reactor.ThoriumCoreBlockEntity;
import reika.reactorcraft.reactor.TritizerBlockEntity;
import reika.reactorcraft.reactor.WaterCellBlockEntity;
import reika.reactorcraft.waste.WasteContainerBlockEntity;
import reika.reactorcraft.waste.WasteDecayerBlockEntity;
import reika.reactorcraft.waste.WastePipeBlockEntity;
import reika.reactorcraft.waste.WasteStorageBlockEntity;

/** Original: ReactorTiles (the machines ported so far). */
public final class ReactorBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ReactorCraft.MOD_ID);

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<HeavyPumpBlockEntity>> HEAVY_PUMP = TYPES.register("fluid_extractor",
			() -> BlockEntityType.Builder.of(HeavyPumpBlockEntity::new, ReactorBlocks.FLUID_EXTRACTOR.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<UProcessorBlockEntity>> PROCESSOR = TYPES.register("processor",
			() -> BlockEntityType.Builder.of(UProcessorBlockEntity::new, ReactorBlocks.PROCESSOR.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<SynthesizerBlockEntity>> SYNTHESIZER = TYPES.register("synthesizer",
			() -> BlockEntityType.Builder.of(SynthesizerBlockEntity::new, ReactorBlocks.SYNTHESIZER.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<IsoCentrifugeBlockEntity>> ISO_CENTRIFUGE = TYPES.register("isocentrifuge",
			() -> BlockEntityType.Builder.of(IsoCentrifugeBlockEntity::new, ReactorBlocks.ISO_CENTRIFUGE.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<FuelRodBlockEntity>> FUEL_ROD = TYPES.register("fuel_rod",
			() -> BlockEntityType.Builder.of(FuelRodBlockEntity::new, ReactorBlocks.FUEL_ROD.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<WaterCellBlockEntity>> WATER_CELL = TYPES.register("coolant_cell",
			() -> BlockEntityType.Builder.of(WaterCellBlockEntity::new, ReactorBlocks.WATER_CELL.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<ControlRodBlockEntity>> CONTROL_ROD = TYPES.register("control_rod",
			() -> BlockEntityType.Builder.of(ControlRodBlockEntity::new, ReactorBlocks.CONTROL_ROD.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<CpuBlockEntity>> CPU = TYPES.register("cpu",
			() -> BlockEntityType.Builder.of(CpuBlockEntity::new, ReactorBlocks.CPU.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<ReactorBoilerBlockEntity>> BOILER = TYPES.register("reactor_boiler",
			() -> BlockEntityType.Builder.of(ReactorBoilerBlockEntity::new, ReactorBlocks.BOILER.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<SteamLineBlockEntity>> STEAM_LINE = TYPES.register("steam_line",
			() -> BlockEntityType.Builder.of(SteamLineBlockEntity::new, ReactorBlocks.STEAM_LINE.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<HeatPipeBlockEntity>> HEAT_PIPE = TYPES.register("heat_pipe",
			() -> BlockEntityType.Builder.of(HeatPipeBlockEntity::new, ReactorBlocks.HEAT_PIPE.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<SteamGrateBlockEntity>> STEAM_GRATE = TYPES.register("steam_grate",
			() -> BlockEntityType.Builder.of(SteamGrateBlockEntity::new, ReactorBlocks.STEAM_GRATE.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<TurbineCoreBlockEntity>> TURBINE_CORE = TYPES.register("turbine_core",
			() -> BlockEntityType.Builder.of(TurbineCoreBlockEntity::new, ReactorBlocks.TURBINE_CORE.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<CondenserBlockEntity>> CONDENSER = TYPES.register("condenser",
			() -> BlockEntityType.Builder.of(CondenserBlockEntity::new, ReactorBlocks.CONDENSER.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<SteamDiffuserBlockEntity>> STEAM_DIFFUSER = TYPES.register("steam_diffuser",
			() -> BlockEntityType.Builder.of(SteamDiffuserBlockEntity::new, ReactorBlocks.STEAM_DIFFUSER.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<ReactorPumpBlockEntity>> PRESSURIZER = TYPES.register("pressurizer",
			() -> BlockEntityType.Builder.of(ReactorPumpBlockEntity::new, ReactorBlocks.PRESSURIZER.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<WasteContainerBlockEntity>> WASTE_CONTAINER = TYPES.register("waste_container",
			() -> BlockEntityType.Builder.of(WasteContainerBlockEntity::new, ReactorBlocks.WASTE_CONTAINER.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<WasteStorageBlockEntity>> WASTE_STORAGE = TYPES.register("waste_storage",
			() -> BlockEntityType.Builder.of(WasteStorageBlockEntity::new, ReactorBlocks.WASTE_STORAGE.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<WasteDecayerBlockEntity>> WASTE_DECAYER = TYPES.register("waste_decayer",
			() -> BlockEntityType.Builder.of(WasteDecayerBlockEntity::new, ReactorBlocks.WASTE_DECAYER.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<BreederCoreBlockEntity>> BREEDER_CORE = TYPES.register("breeder_core",
			() -> BlockEntityType.Builder.of(BreederCoreBlockEntity::new, ReactorBlocks.BREEDER_CORE.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<SodiumHeaterBlockEntity>> SODIUM_HEATER = TYPES.register("sodium_heater",
			() -> BlockEntityType.Builder.of(SodiumHeaterBlockEntity::new, ReactorBlocks.SODIUM_HEATER.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<HeatExchangerBlockEntity>> HEAT_EXCHANGER = TYPES.register("heat_exchanger",
			() -> BlockEntityType.Builder.of(HeatExchangerBlockEntity::new, ReactorBlocks.HEAT_EXCHANGER.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<PebbleBedBlockEntity>> PEBBLE_BED = TYPES.register("pebble_bed",
			() -> BlockEntityType.Builder.of(PebbleBedBlockEntity::new, ReactorBlocks.PEBBLE_BED.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<CO2HeaterBlockEntity>> CO2_HEATER = TYPES.register("co2_heater",
			() -> BlockEntityType.Builder.of(CO2HeaterBlockEntity::new, ReactorBlocks.CO2_HEATER.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<ThoriumCoreBlockEntity>> THORIUM_CORE = TYPES.register("thorium_core",
			() -> BlockEntityType.Builder.of(ThoriumCoreBlockEntity::new, ReactorBlocks.THORIUM_CORE.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<FuelDumpBlockEntity>> FUEL_DUMP = TYPES.register("fuel_dump",
			() -> BlockEntityType.Builder.of(FuelDumpBlockEntity::new, ReactorBlocks.FUEL_DUMP.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<WastePipeBlockEntity>> WASTE_PIPE = TYPES.register("waste_pipe",
			() -> BlockEntityType.Builder.of(WastePipeBlockEntity::new, ReactorBlocks.WASTE_PIPE.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<HiPTurbineBlockEntity>> HIP_TURBINE = TYPES.register("hip_turbine",
			() -> BlockEntityType.Builder.of(HiPTurbineBlockEntity::new, ReactorBlocks.HIP_TURBINE.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<SteamInjectorBlockEntity>> STEAM_INJECTOR = TYPES.register("steam_injector",
			() -> BlockEntityType.Builder.of(SteamInjectorBlockEntity::new, ReactorBlocks.STEAM_INJECTOR.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<ReactorGeneratorBlockEntity>> GENERATOR = TYPES.register("reactor_generator",
			() -> BlockEntityType.Builder.of(ReactorGeneratorBlockEntity::new, ReactorBlocks.GENERATOR.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<ReactorFlywheelBlockEntity>> FLYWHEEL = TYPES.register("turbine_flywheel",
			() -> BlockEntityType.Builder.of(ReactorFlywheelBlockEntity::new, ReactorBlocks.FLYWHEEL.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<TritizerBlockEntity>> TRITIZER = TYPES.register("tritizer",
			() -> BlockEntityType.Builder.of(TritizerBlockEntity::new, ReactorBlocks.TRITIZER.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<SolarExchangerBlockEntity>> SOLAR_EXCHANGER = TYPES.register("solar_exchanger",
			() -> BlockEntityType.Builder.of(SolarExchangerBlockEntity::new, ReactorBlocks.SOLAR_EXCHANGER.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<SolarTopBlockEntity>> SOLAR_TOP = TYPES.register("solar_top",
			() -> BlockEntityType.Builder.of(SolarTopBlockEntity::new, ReactorBlocks.SOLAR_TOP.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<ElectrolyzerBlockEntity>> ELECTROLYZER = TYPES.register("electrolyzer",
			() -> BlockEntityType.Builder.of(ElectrolyzerBlockEntity::new, ReactorBlocks.ELECTROLYZER.get()).build(null));

	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<ToroidMagnetBlockEntity>> TOROID_MAGNET = TYPES.register("toroid_magnet",
			() -> BlockEntityType.Builder.of(ToroidMagnetBlockEntity::new, ReactorBlocks.TOROID_MAGNET.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<SolenoidMagnetBlockEntity>> SOLENOID = TYPES.register("solenoid",
			() -> BlockEntityType.Builder.of(SolenoidMagnetBlockEntity::new, ReactorBlocks.SOLENOID.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<FusionInjectorBlockEntity>> FUSION_INJECTOR = TYPES.register("fusion_injector",
			() -> BlockEntityType.Builder.of(FusionInjectorBlockEntity::new, ReactorBlocks.FUSION_INJECTOR.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<FusionHeaterBlockEntity>> FUSION_HEATER = TYPES.register("fusion_heater",
			() -> BlockEntityType.Builder.of(FusionHeaterBlockEntity::new, ReactorBlocks.FUSION_HEATER.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<NeutronAbsorberBlockEntity>> NEUTRON_ABSORBER = TYPES.register("neutron_absorber",
			() -> BlockEntityType.Builder.of(NeutronAbsorberBlockEntity::new, ReactorBlocks.NEUTRON_ABSORBER.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<NeutronReflectorBlockEntity>> NEUTRON_REFLECTOR = TYPES.register("neutron_reflector",
			() -> BlockEntityType.Builder.of(NeutronReflectorBlockEntity::new, ReactorBlocks.NEUTRON_REFLECTOR.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<TurbineMeterBlockEntity>> TURBINE_METER = TYPES.register("turbine_meter",
			() -> BlockEntityType.Builder.of(TurbineMeterBlockEntity::new, ReactorBlocks.TURBINE_METER.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<GasCollectorBlockEntity>> GAS_COLLECTOR = TYPES.register("gas_collector",
			() -> BlockEntityType.Builder.of(GasCollectorBlockEntity::new, ReactorBlocks.GAS_COLLECTOR.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<FusionMarkerBlockEntity>> FUSION_MARKER = TYPES.register("fusion_marker",
			() -> BlockEntityType.Builder.of(FusionMarkerBlockEntity::new, ReactorBlocks.FUSION_MARKER.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<MagneticPipeBlockEntity>> MAGNETIC_PIPE = TYPES.register("magnetic_pipe",
			() -> BlockEntityType.Builder.of(MagneticPipeBlockEntity::new, ReactorBlocks.MAGNETIC_PIPE.get()).build(null));
	@SuppressWarnings("DataFlowIssue")
	public static final Supplier<BlockEntityType<GasDuctBlockEntity>> GAS_DUCT = TYPES.register("gas_duct",
			() -> BlockEntityType.Builder.of(GasDuctBlockEntity::new, ReactorBlocks.GAS_DUCT.get()).build(null));

	private ReactorBlockEntities() {}
}

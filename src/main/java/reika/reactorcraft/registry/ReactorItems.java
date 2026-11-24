/*******************************************************************************
 * @author Reika Kalseki (original ReactorCraft). Private NeoForge 1.21.1 port; not for distribution.
 ******************************************************************************/
package reika.reactorcraft.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import reika.reactorcraft.ReactorCraft;
import reika.reactorcraft.content.item.BreederFuelItem;
import reika.reactorcraft.content.item.CanisterItem;
import reika.reactorcraft.content.item.FuelItem;
import reika.reactorcraft.content.item.GeigerCounterItem;
import reika.reactorcraft.content.item.HazmatSuitItem;
import reika.reactorcraft.content.item.IronFinderItem;
import reika.reactorcraft.content.item.MagnetItem;
import reika.reactorcraft.content.item.NuclearWasteItem;
import reika.reactorcraft.content.item.PlutoniumItem;
import reika.reactorcraft.content.item.RadiationCleanerItem;
import reika.reactorcraft.content.item.RadiationGogglesItem;
import reika.reactorcraft.content.item.RemoteControlItem;

import javax.annotation.Nullable;

import reika.rotarycraft.content.item.FluidBucketItem;
import reika.rotarycraft.content.item.HandbookItem;

/** Original: ReactorItems RAW, FLUORITE and INGOTS (one item per former metadata value). */
public final class ReactorItems {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ReactorCraft.MOD_ID);

	/** Original ReactorStacks / ReactorNames.rawNames, in metadata order. */
	public enum Raw {
		HF, FUELDUST, DEPDUST, AMMONIUM, LIME, CALCITE, MAGNETITE, THORDUST, EMERALDDUST, WASTEDUST;

		public String getName() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}
	}

	/** Original INGOTS metadata 0-3 (the ores' products: uraninite gives uranium). */
	public enum Ingot {
		URANIUM, CADMIUM, INDIUM, SILVER;

		public String getName() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}
	}

	/** Original CraftingItems (ReactorItems.CRAFTING metadata order). */
	public enum Crafting {
		CANISTER, ROD, TANK, ALLOY, BACKING, MAGNETIC, MAGNETCORE, COOLANT, WIRE, SHIELD, FERROINGOT, HYSTERESIS, HYSTERESISRING, GRAPHITE, UDUST,
		FABRIC, CARBIDEFLAKES, CARBIDE, TURBCORE;

		public String getName() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}
	}

	private static final Map<Crafting, DeferredItem<Item>> CRAFTING = new EnumMap<>(Crafting.class);
	private static final Map<Raw, DeferredItem<Item>> RAW = new EnumMap<>(Raw.class);
	private static final Map<FluoriteTypes, DeferredItem<Item>> FLUORITE = new EnumMap<>(FluoriteTypes.class);
	private static final Map<Ingot, DeferredItem<Item>> INGOTS = new EnumMap<>(Ingot.class);

	static {
		for (Raw r : Raw.values())
			RAW.put(r, ITEMS.registerSimpleItem(r.getName()));
		for (FluoriteTypes f : FluoriteTypes.colorList)
			FLUORITE.put(f, ITEMS.registerSimpleItem(f.getColorName()+"_fluorite"));
		for (Ingot i : Ingot.values())
			INGOTS.put(i, ITEMS.registerSimpleItem(i.getName()+"_ingot"));
		//"canister" would clash with the fluid canisters; the crafting part is the fuel canister
		for (Crafting c : Crafting.values())
			CRAFTING.put(c, ITEMS.registerSimpleItem(c == Crafting.CANISTER ? "fuel_canister" : c.getName()));
	}

	/** Original ReactorNames.canNames order (metadata) and the fluid each canister holds. */
	private static final String[][] CANISTERS = {{"uf6", "uranium_hexafluoride"}, {"hf", "hydrofluoric_acid"}, {"nh3", "ammonia"},
			{"na", "sodium"}, {"h2", "deuterium"}, {"h3", "tritium"}, {"cl", "chlorine"}, {"o", "oxygen"}, {"co2", "co2"}, {"hco2", "hot_co2"},
			{"hna", "hot_sodium"}, {"li", "lithium"}, {"lifbe", "lifbe"}, {"hlifbe", "hot_lifbe"}, {"lifbefuel", "lifbe_fuel"}};
	public static final DeferredItem<CanisterItem> EMPTY_CANISTER = ITEMS.register("empty_canister",
			() -> new CanisterItem(new Item.Properties().stacksTo(1), null));
	private static final Map<String, DeferredItem<CanisterItem>> FILLED = new LinkedHashMap<>();

	static {
		for (String[] c : CANISTERS)
			FILLED.put(c[1], ITEMS.register(c[0]+"_canister", () -> new CanisterItem(new Item.Properties().stacksTo(1)
					.craftRemainder(EMPTY_CANISTER.get()), ReactorFluids.SOURCE.get(c[1]))));
	}

	/** Original ReactorItems.BUCKET: heavy water, stack size 1, leaves a bucket in crafting, cannot be poured out. */
	public static final DeferredItem<FluidBucketItem> HEAVY_WATER_BUCKET = ITEMS.register("heavy_water_bucket",
			() -> new FluidBucketItem(ReactorFluids.get("heavy_water"), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));

	/** Original ReactorItems FUEL (100 depletion steps), DEPLETED and WASTE. */
	public static final DeferredItem<FuelItem> FUEL = ITEMS.register("fuel", () -> new FuelItem(new Item.Properties(), 100));
	public static final DeferredItem<Item> DEPLETED = ITEMS.registerSimpleItem("depleted");
	/** Original PLUTONIUM (100 steps) and BREEDERFUEL (20 steps). */
	public static final DeferredItem<PlutoniumItem> PLUTONIUM = ITEMS.register("plutonium", () -> new PlutoniumItem(new Item.Properties(), 100));
	/** Original HAZ armour material ("RCHazmat": 0 durability, 0 protection, enchantability 0) with the original armour textures haz_1/haz_2. */
	public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
			DeferredRegister.create(Registries.ARMOR_MATERIAL, ReactorCraft.MOD_ID);
	public static final Holder<ArmorMaterial> HAZMAT = ARMOR_MATERIALS.register("hazmat", () -> new ArmorMaterial(
			Map.of(ArmorItem.Type.BOOTS, 0, ArmorItem.Type.LEGGINGS, 0, ArmorItem.Type.CHESTPLATE, 0,
					ArmorItem.Type.HELMET, 0, ArmorItem.Type.BODY, 0), 0,
			SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.EMPTY,
			List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "haz"))), 0, 0));
	public static final DeferredItem<HazmatSuitItem> HAZ_HELMET = ITEMS.register("hazmat_helmet",
			() -> new HazmatSuitItem(HAZMAT, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final DeferredItem<HazmatSuitItem> HAZ_CHEST = ITEMS.register("hazmat_chestplate",
			() -> new HazmatSuitItem(HAZMAT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final DeferredItem<HazmatSuitItem> HAZ_LEGS = ITEMS.register("hazmat_leggings",
			() -> new HazmatSuitItem(HAZMAT, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final DeferredItem<HazmatSuitItem> HAZ_BOOTS = ITEMS.register("hazmat_boots",
			() -> new HazmatSuitItem(HAZMAT, ArmorItem.Type.BOOTS, new Item.Properties()));
	/** The port's own ReactorCraft handbook (own text; replaces the original's in-game manual). */
	public static final DeferredItem<HandbookItem> HANDBOOK = ITEMS.register("handbook",
			() -> new HandbookItem(new Item.Properties(), ReactorCraft.MOD_ID));
	/** Original GOGGLES and GEIGER (ItemReactorTool). */
	public static final DeferredItem<RadiationGogglesItem> GOGGLES = ITEMS.register("goggles",
			() -> new RadiationGogglesItem(new Item.Properties()));
	public static final DeferredItem<GeigerCounterItem> GEIGER = ITEMS.register("geiger",
			() -> new GeigerCounterItem(new Item.Properties()));
	/** Original CLEANUP (ItemRadiationCleaner) and IRONFINDER (ItemIronFinder). */
	public static final DeferredItem<RadiationCleanerItem> CLEANER = ITEMS.register("cleaner",
			() -> new RadiationCleanerItem(new Item.Properties()));
	public static final DeferredItem<IronFinderItem> IRON_FINDER = ITEMS.register("ironfinder",
			() -> new IronFinderItem(new Item.Properties()));
	/** Original REMOTE (ItemRemoteControl, CPU remote control). */
	public static final DeferredItem<RemoteControlItem> REMOTE = ITEMS.register("remotecpu",
			() -> new RemoteControlItem(new Item.Properties()));
	/** Original PELLET (HTGR fuel pebble, 25 steps) and OLDPELLET (spent pebble). */
	public static final DeferredItem<FuelItem> PELLET = ITEMS.register("pellet", () -> new FuelItem(new Item.Properties(), 25));
	public static final DeferredItem<Item> OLD_PELLET = ITEMS.registerSimpleItem("depleted_pellet");
	public static final DeferredItem<BreederFuelItem> BREEDER_FUEL = ITEMS.register("breeder_fuel", () -> new BreederFuelItem(new Item.Properties(), 20));
	public static final DeferredItem<NuclearWasteItem> WASTE = ITEMS.register("waste", () -> new NuclearWasteItem(new Item.Properties()));

	private ReactorItems() {}

	public static Collection<DeferredItem<CanisterItem>> filledCanisters() {
		return FILLED.values();
	}

	/** The canister that holds this fluid, or null. */
	@Nullable
	public static Item canisterFor(Fluid f) {
		for (var e : FILLED.entrySet())
			if (ReactorFluids.get(e.getKey()).isSame(f))
				return e.getValue().get();
		return null;
	}

	public static DeferredItem<Item> crafting(Crafting c) {
		return CRAFTING.get(c);
	}

	/** Original MAGNET: permanent magnets, 8 strengths (magnet_0 .. magnet_7). */
	public static final List<DeferredItem<MagnetItem>> MAGNETS = new ArrayList<>();

	static {
		for (int i = 0; i < 8; i++) {
			int lvl = i;
			MAGNETS.add(ITEMS.register("magnet_"+i, () -> new MagnetItem(new Item.Properties(), lvl)));
		}
	}

	public static DeferredItem<Item> raw(Raw r) {
		return RAW.get(r);
	}

	public static DeferredItem<Item> fluorite(FluoriteTypes f) {
		return FLUORITE.get(f);
	}

	public static DeferredItem<Item> ingot(Ingot i) {
		return INGOTS.get(i);
	}
}

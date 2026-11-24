/*******************************************************************************
 * @author Reika Kalseki (original ReactorCraft). Private NeoForge 1.21.1 port; not for distribution.
 ******************************************************************************/
package reika.reactorcraft.registry;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import reika.reactorcraft.ReactorCraft;
import reika.reactorcraft.content.menu.CpuMenu;
import reika.reactorcraft.content.menu.ElectrolyzerMenu;
import reika.reactorcraft.content.menu.IsoCentrifugeMenu;
import reika.reactorcraft.content.menu.NuclearCoreMenu;
import reika.reactorcraft.content.menu.SynthesizerMenu;
import reika.reactorcraft.content.menu.ThoriumCoreMenu;
import reika.reactorcraft.content.menu.UProcessorMenu;
import reika.reactorcraft.content.menu.WasteMenus;

/** Original: ReactorGuiHandler. */
public final class ReactorMenus {

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ReactorCraft.MOD_ID);

	public static final Supplier<MenuType<UProcessorMenu>> PROCESSOR = MENUS.register("processor", () -> IMenuTypeExtension.create(UProcessorMenu::fromNetwork));

	public static final Supplier<MenuType<ElectrolyzerMenu>> ELECTROLYZER = MENUS.register("electrolyzer",
			() -> IMenuTypeExtension.create(ElectrolyzerMenu::fromNetwork));
	public static final Supplier<MenuType<SynthesizerMenu>> SYNTHESIZER = MENUS.register("synthesizer",
			() -> IMenuTypeExtension.create(SynthesizerMenu::fromNetwork));

	public static final Supplier<MenuType<IsoCentrifugeMenu>> ISO_CENTRIFUGE = MENUS.register("isocentrifuge",
			() -> IMenuTypeExtension.create(IsoCentrifugeMenu::fromNetwork));

	public static final Supplier<MenuType<NuclearCoreMenu>> NUCLEAR_CORE = MENUS.register("nuclear_core",
			() -> IMenuTypeExtension.create(NuclearCoreMenu::fromNetwork));

	public static final Supplier<MenuType<CpuMenu>> CPU = MENUS.register("cpu",
			() -> IMenuTypeExtension.create(CpuMenu::fromNetwork));

	public static final Supplier<MenuType<WasteMenus.Container>> WASTE_CONTAINER = MENUS.register("waste_container",
			() -> IMenuTypeExtension.create(WasteMenus.Container::fromNetwork));
	public static final Supplier<MenuType<WasteMenus.Storage>> WASTE_STORAGE = MENUS.register("waste_storage",
			() -> IMenuTypeExtension.create(WasteMenus.Storage::fromNetwork));
	public static final Supplier<MenuType<WasteMenus.Decayer>> WASTE_DECAYER = MENUS.register("waste_decayer",
			() -> IMenuTypeExtension.create(WasteMenus.Decayer::fromNetwork));

	public static final Supplier<MenuType<WasteMenus.PebbleBed>> PEBBLE_BED = MENUS.register("pebble_bed",
			() -> IMenuTypeExtension.create(WasteMenus.PebbleBed::fromNetwork));

	public static final Supplier<MenuType<ThoriumCoreMenu>> THORIUM_CORE = MENUS.register("thorium_core",
			() -> IMenuTypeExtension.create(ThoriumCoreMenu::fromNetwork));

	private ReactorMenus() {}
}

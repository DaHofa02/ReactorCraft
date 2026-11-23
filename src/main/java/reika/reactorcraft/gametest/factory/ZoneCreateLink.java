/*******************************************************************************
 * Practice factory zone (see Factory): the port's Create converters (optional Create integration) set up by a simulated survival player.
 * Not part of the original mods. Without Create the converters do not exist and there is nothing to build.
 ******************************************************************************/
package reika.reactorcraft.gametest.factory;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import reika.reactorcraft.ReactorCraft;
import reika.rotarycraft.api.power.ShaftMachine;
import reika.rotarycraft.compat.OptionalMods;
import reika.rotarycraft.content.blockentity.DynamometerBlockEntity;
import reika.rotarycraft.content.blockentity.engine.EngineBlockEntity;
import reika.rotarycraft.gametest.SimPlayer;
import reika.rotarycraft.registry.RotaryBlocks;

@GameTestHolder(ReactorCraft.MOD_ID)
@PrefixGameTestTemplate(false)
public class ZoneCreateLink {

	public static final List<String> BLOCKS = List.of("rotarycraft:rotary_to_create", "rotarycraft:create_to_rotary");

	private static final String PLATFORM = "platform_9x8x9";

	private static Block block(String id) {
		return BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id));
	}

	/** Turns a placed converter so its Create side faces the given way (what Create's wrench does). */
	private static void face(GameTestHelper h, BlockPos pos, Direction createSide) {
		BlockState s = h.getBlockState(pos);
		DirectionProperty facing = (DirectionProperty)s.getBlock().getStateDefinition().getProperty("facing");
		h.setBlock(pos, s.setValue(facing, createSide));
	}

	/**
	 * Gas engine -> shaft power to stress -> stress to shaft power -> dynamometer, all along -x: the power goes through Create and comes
	 * back as shaft power at 16 rad/s per RPM of the Create link.
	 */
	@GameTest(template = PLATFORM, timeoutTicks = 400, batch = "factory")
	public static void shaftPowerRoundTripThroughCreate(GameTestHelper h) {
		if (!OptionalMods.create()) {
			h.succeed();
			return;
		}
		SimPlayer.floor(h, 0, 0, 8, 8);
		SimPlayer p = new SimPlayer(h, GameType.SURVIVAL);
		Block toCreate = block(BLOCKS.get(0)), toRotary = block(BLOCKS.get(1));
		p.give(new ItemStack(RotaryBlocks.GAS_ENGINE.get()));
		p.give(new ItemStack(toCreate));
		p.give(new ItemStack(toRotary));
		p.give(new ItemStack(RotaryBlocks.DYNAMOMETER.get()));
		BlockPos gas = new BlockPos(6, 2, 4), a = new BlockPos(5, 2, 4), b = new BlockPos(4, 2, 4), dyn = new BlockPos(3, 2, 4);
		p.placeSneaking(gas, new ItemStack(RotaryBlocks.GAS_ENGINE.get()), Direction.EAST); //drives west
		((EngineBlockEntity)h.getBlockEntity(gas)).addFuel(50000);
		p.placeSneaking(a, new ItemStack(toCreate), Direction.EAST);
		face(h, a, Direction.WEST); //shaft power in at the east, Create out west
		p.placeSneaking(b, new ItemStack(toRotary), Direction.EAST);
		face(h, b, Direction.EAST); //Create in at the east, shaft power out west
		p.placeSneaking(dyn, new ItemStack(RotaryBlocks.DYNAMOMETER.get()), Direction.EAST);
		DynamometerBlockEntity d = (DynamometerBlockEntity)h.getBlockEntity(dyn);
		h.succeedWhen(() -> {
			ShaftMachine in = (ShaftMachine)h.getBlockEntity(a), out = (ShaftMachine)h.getBlockEntity(b);
			int rpm = in.getOmega()/16;
			h.assertTrue(rpm > 0, "first converter gets "+in.getOmega()+" rad/s");
			h.assertTrue(out.getOmega() == rpm*16 && out.getTorque() > 0, "second converter gives "+out.getOmega()+" rad/s "+out.getTorque()+" Nm for "+rpm+" RPM");
			h.assertTrue(d.omega == out.getOmega() && d.torque == out.getTorque(), "dynamometer reads "+d.omega+" rad/s "+d.torque+" Nm");
			Factory.assertBuilt(h, BLOCKS, new BlockPos(0, 2, 0), new BlockPos(8, 3, 8));
			p.leave();
		});
	}
}

/*******************************************************************************
 * @author Reika Kalseki (original ReactorCraft). Private NeoForge 1.21.1 port; not for distribution.
 ******************************************************************************/
package reika.reactorcraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import reika.reactorcraft.ReactorCraft;
import reika.reactorcraft.client.model.ModelBigTurbine;
import reika.reactorcraft.client.model.ModelCentrifuge;
import reika.reactorcraft.client.model.ModelCondenser;
import reika.reactorcraft.client.model.ModelControl;
import reika.reactorcraft.client.model.ModelDiffuser;
import reika.reactorcraft.client.model.ModelElectrolyzer;
import reika.reactorcraft.client.model.ModelExchanger;
import reika.reactorcraft.client.model.ModelFlywheel;
import reika.reactorcraft.client.model.ModelGenerator;
import reika.reactorcraft.client.model.ModelHeavyPump;
import reika.reactorcraft.client.model.ModelProcessor;
import reika.reactorcraft.client.model.ModelReactorPump;
import reika.reactorcraft.client.model.ModelSolarExchanger;
import reika.reactorcraft.client.model.ModelSolarTop;
import reika.reactorcraft.client.model.ModelSteamGrate;
import reika.reactorcraft.client.model.ModelTurbine;
import reika.reactorcraft.client.model.ModelWasteStorage;
import reika.reactorcraft.client.screen.CpuScreen;
import reika.reactorcraft.client.screen.ElectrolyzerScreen;
import reika.reactorcraft.client.screen.IsoCentrifugeScreen;
import reika.reactorcraft.client.screen.NuclearCoreScreen;
import reika.reactorcraft.client.screen.SynthesizerScreen;
import reika.reactorcraft.client.screen.ThoriumCoreScreen;
import reika.reactorcraft.client.screen.UProcessorScreen;
import reika.reactorcraft.client.screen.WasteScreen;
import reika.reactorcraft.content.blockentity.ElectrolyzerBlockEntity;
import reika.reactorcraft.content.blockentity.HeavyPumpBlockEntity;
import reika.reactorcraft.content.blockentity.IsoCentrifugeBlockEntity;
import reika.reactorcraft.content.blockentity.UProcessorBlockEntity;
import reika.reactorcraft.content.menu.WasteMenus;
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
import reika.reactorcraft.powergen.TurbineCoreBlockEntity;
import reika.reactorcraft.reactor.ControlRodBlockEntity;
import reika.reactorcraft.registry.ReactorBlockEntities;
import reika.reactorcraft.registry.ReactorBlocks;
import reika.reactorcraft.registry.ReactorEffects;
import reika.reactorcraft.registry.ReactorEntities;
import reika.reactorcraft.registry.ReactorFluids;
import reika.reactorcraft.registry.ReactorMenus;
import reika.reactorcraft.waste.WasteStorageBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import java.util.List;

import org.joml.Matrix4f;

import reika.rotarycraft.client.MachineItemRenderer;
import reika.rotarycraft.client.model.TechneModel;
import reika.rotarycraft.client.render.IORenderer;
import reika.rotarycraft.client.render.PipeFluidRenderer;
import reika.rotarycraft.content.blockentity.SolarTowerBlockEntity;
import reika.rotarycraft.lib.math.BlackbodyColor;

/** Original: ReactorCraft.textureHook - the fluid icons (fluid/*); low pressure water uses the water texture. */
@EventBusSubscriber(modid = ReactorCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ReactorClient {

	private ReactorClient() {}

	public static final ModelLayerLocation HEAVY_PUMP = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "fluid_extractor"), "main");

	public static final ModelLayerLocation ISO_CENTRIFUGE = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "isocentrifuge"), "main");

	/** Original RenderCentrifuge: the model with the rotor turning by -phi. */
	static final class IsoCentrifuge implements BlockEntityRenderer<IsoCentrifugeBlockEntity> {
		private final ModelCentrifuge model;

		IsoCentrifuge(BlockEntityRendererProvider.Context ctx) {
			model = new ModelCentrifuge(ctx.bakeLayer(ISO_CENTRIFUGE));
		}

		@Override
		public void render(IsoCentrifugeBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("centrifuge.png"))), light, overlay, tile, null, -tile.phi, 0);
			pose.popPose();
		}
	}

	public static final ModelLayerLocation CONTROL_ROD = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "control_rod"), "main");

	/** Original RenderControl: the model with the rods moved by the rod position (one step = 1/28 block); the render box reaches 2 blocks up. */
	static final class ControlRod implements BlockEntityRenderer<ControlRodBlockEntity> {
		private final ModelControl model;

		ControlRod(BlockEntityRendererProvider.Context ctx) {
			model = new ModelControl(ctx.bakeLayer(CONTROL_ROD));
		}

		@Override
		public void render(ControlRodBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("control.png"))), light, overlay, tile, null, tile.getRodPosition(), 0);
			pose.popPose();
		}

		@Override
		public AABB getRenderBoundingBox(ControlRodBlockEntity tile) {
			return new AABB(tile.getBlockPos()).expandTowards(0, 2, 0);
		}
	}

	public static final ModelLayerLocation STEAM_GRATE = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "steam_grate"), "main");
	public static final ModelLayerLocation[] TURBINE = new ModelLayerLocation[5];

	static {
		for (int i = 0; i < TURBINE.length; i++)
			TURBINE[i] = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "turbine_core"), "stage"+i);
	}

	public static final ModelLayerLocation CONDENSER = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "condenser"), "main");

	public static final ModelLayerLocation STEAM_DIFFUSER = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "steam_diffuser"), "main");

	/** Original RenderSteamDiffuser: the model, turned by the metadata (0: 90, 1: 270, 2: 180, 3: 0 degrees). */
	static final class SteamDiffuser implements BlockEntityRenderer<SteamDiffuserBlockEntity> {
		private final ModelDiffuser model;

		SteamDiffuser(BlockEntityRendererProvider.Context ctx) {
			model = new ModelDiffuser(ctx.bakeLayer(STEAM_DIFFUSER));
		}

		@Override
		public void render(SteamDiffuserBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			int rot = switch (tile.getBlockMetadata()) {
				case 0 -> 90;
				case 1 -> 270;
				case 2 -> 180;
				default -> 0;
			};
			pose.mulPose(TechneModel.rotation(rot, 0, 1, 0));
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("diffuser.png"))), light, overlay, tile, null, -tile.phi, 0);
			pose.popPose();
		}
	}

	public static final ModelLayerLocation PRESSURIZER = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "pressurizer"), "main");

	/** Original RenderReactorPump: the model (not turned), its rotor at -phi (the pump turns it 15 degrees a tick while powered). */
	static final class Pressurizer implements BlockEntityRenderer<ReactorPumpBlockEntity> {
		private final ModelReactorPump model;

		Pressurizer(BlockEntityRendererProvider.Context ctx) {
			model = new ModelReactorPump(ctx.bakeLayer(PRESSURIZER));
		}

		@Override
		public void render(ReactorPumpBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("pump.png"))), light, overlay, tile, null, -tile.phi, 0);
			pose.popPose();
		}
	}

	public static final ModelLayerLocation WASTE_STORAGE = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "waste_storage"), "main");

	/** Original RenderWasteStorage: the model, unrotated. */
	static final class WasteStorage implements BlockEntityRenderer<WasteStorageBlockEntity> {
		private final ModelWasteStorage model;

		WasteStorage(BlockEntityRendererProvider.Context ctx) {
			model = new ModelWasteStorage(ctx.bakeLayer(WASTE_STORAGE));
		}

		@Override
		public void render(WasteStorageBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("storage.png"))), light, overlay, tile, null, 0, 0);
			pose.popPose();
		}
	}

	public static final ModelLayerLocation HEAT_EXCHANGER = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "heat_exchanger"), "main");

	/** Original RenderExchanger: the model and, while it has a coolant, the hot fluid in the inner column and the cold one in the outer ring. */
	static final class HeatExchanger implements BlockEntityRenderer<HeatExchangerBlockEntity> {
		private final ModelExchanger model;

		HeatExchanger(BlockEntityRendererProvider.Context ctx) {
			model = new ModelExchanger(ctx.bakeLayer(HEAT_EXCHANGER));
		}

		@Override
		public void render(HeatExchangerBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("exchanger2.png"))), light, overlay, tile, null, 0, 0);
			pose.popPose();
			var e = tile.getLevel() != null ? tile.getCurrentRecipe() : null;
			if (e != null) {
				fluidBox(pose, buf, e.getHotFluid(), 0.375F, 0.0635F, 0.375F, 0.625F, 1.0135F, 0.625F, light);
				fluidBox(pose, buf, e.getColdFluid(), -0.04F, 0.4025F, -0.04F, 1.04F, 0.5925F, 1.04F, light);
			}
		}
	}

	/** A box of the fluid's still texture and tint (original ReikaLiquidRenderer display list, scaled). */
	static void fluidBox(PoseStack pose, MultiBufferSource buf, Fluid f,
			float x0, float y0, float z0, float x1, float y1, float z1, int light) {
		var ext = IClientFluidTypeExtensions.of(f);
		var sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ext.getStillTexture());
		int clr = ext.getTintColor();
		var vc = buf.getBuffer(RenderType.translucent());
		Matrix4f m = pose.last().pose();
		float u0 = sprite.getU0(), u1 = sprite.getU1(), v0 = sprite.getV0(), v1 = sprite.getV1();
		float[][] q = {
				{x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0}, {x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1},
				{x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0}, {x1, y0, z1, x1, y1, z1, x0, y1, z1, x0, y0, z1},
				{x0, y0, z1, x0, y1, z1, x0, y1, z0, x0, y0, z0}, {x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1}};
		float[][] uv = {{u0, v0}, {u0, v1}, {u1, v1}, {u1, v0}};
		for (float[] f4 : q)
			for (int i = 0; i < 4; i++)
				vc.addVertex(m, f4[i*3], f4[i*3+1], f4[i*3+2]).setColor(clr).setUv(uv[i][0], uv[i][1]).setLight(light).setNormal(0, 1, 0);
	}

	/** Original RenderCondenser: the model, unrotated. */
	static final class Condenser implements BlockEntityRenderer<CondenserBlockEntity> {
		private final ModelCondenser model;

		Condenser(BlockEntityRendererProvider.Context ctx) {
			model = new ModelCondenser(ctx.bakeLayer(CONDENSER));
		}

		@Override
		public void render(CondenserBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("condenser.png"))), light, overlay, tile, null, 0, 0);
			pose.popPose();
		}
	}

	/** Original RenderSteamGrate: the model, unrotated. */
	static final class SteamGrate implements BlockEntityRenderer<SteamGrateBlockEntity> {
		private final ModelSteamGrate model;

		SteamGrate(BlockEntityRendererProvider.Context ctx) {
			model = new ModelSteamGrate(ctx.bakeLayer(STEAM_GRATE));
		}

		@Override
		public void render(SteamGrateBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("steamgrate.png"))), light, overlay, tile, null, 0, 0);
			pose.popPose();
		}
	}

	public static final ModelLayerLocation[] BIG_TURBINE = new ModelLayerLocation[7];

	static {
		for (int i = 0; i < BIG_TURBINE.length; i++)
			BIG_TURBINE[i] = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "hip_turbine"), "stage"+i);
	}

	/**
	 * Original RenderTurbine / RenderBigTurbine: the stage's model turned by the metadata (0: 90, 1: 270, 2: 180, 3: 0) and spun by -phi;
	 * damage thins the blades. A high pressure turbine without its multiblock is drawn as a dark block (multiblock icon 3).
	 */
	static final class TurbineCore<T extends TurbineCoreBlockEntity> implements BlockEntityRenderer<T> {
		private final ModelTurbine[] models;
		private final String texture;

		TurbineCore(BlockEntityRendererProvider.Context ctx, boolean big) {
			ModelLayerLocation[] layers = big ? BIG_TURBINE : TURBINE;
			models = new ModelTurbine[layers.length];
			for (int i = 0; i < models.length; i++)
				models[i] = big ? new ModelBigTurbine(ctx.bakeLayer(layers[i]), i) : new ModelTurbine(ctx.bakeLayer(layers[i]), i);
			texture = big ? "bigturbine.png" : "turbine.png";
		}

		@Override
		public void render(T tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			int rot = switch (tile.getBlockMetadata()) {
				case 0 -> 90;
				case 1 -> 270;
				case 2 -> 180;
				default -> 0;
			};
			pose.mulPose(TechneModel.rotation(rot, 0, 1, 0));
			var vc = buf.getBuffer(RenderType.entityCutout(tex(texture)));
			if (tile.getLevel() == null) { //item
				boolean big = tile instanceof HiPTurbineBlockEntity;
				float sc = big ? 0.4F : 0.6F;
				pose.scale(sc, sc, sc);
				pose.translate(big ? -0.3 : -0.1, big ? 1.7 : 0.8, 0);
				models[0].renderAll(pose, vc, light, overlay, tile, null, -tile.phi, 0);
				pose.popPose();
				return;
			}
			if (!tile.hasMultiBlock()) {
				pose.popPose();
				renderCube(pose, buf, light, "block/multi/turbine_3");
				return;
			}
			int dmg = tile.getDamage();
			int stage = Math.min(tile.getStage(), models.length-1);
			models[stage].renderAll(pose, vc, light, overlay, tile, dmg > 0 ? List.of(dmg) : null, -tile.phi, 0);
			pose.popPose();
		}

		@Override
		public AABB getRenderBoundingBox(T tile) {
			return new AABB(tile.getBlockPos()).inflate(6);
		}

		@Override
		public int getViewDistance() {
			return 128; //original: 4x the normal render distance
		}
	}

	/** The unformed turbine/generator cores: a plain cube with one block texture on every side. */
	static void renderCube(PoseStack pose, MultiBufferSource buf, int light, String texture) {
			var sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, texture));
			var vc = buf.getBuffer(RenderType.solid());
			var m = pose.last();
			float u0 = sprite.getU0(), u1 = sprite.getU1(), v0 = sprite.getV0(), v1 = sprite.getV1();
			float[][] q = {{0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0}, {0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1}, {0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0},
					{1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 1}, {0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0}, {1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1}};
			float[][] uv = {{u0, v0}, {u0, v1}, {u1, v1}, {u1, v0}};
			for (float[] f4 : q)
				for (int i = 0; i < 4; i++)
					vc.addVertex(m, f4[i*3], f4[i*3+1], f4[i*3+2]).setColor(0xFFFFFFFF).setUv(uv[i][0], uv[i][1]).setUv1(0, 10).setUv2(light & 0xFFFF, light >> 16)
							.setNormal(m, 0, 1, 0);
	}

	/**
	 * Original RenderGenerator: formed, the generator model (rotor turning by phi) with the texture of its mode; unformed, a cube with
	 * the mode's block texture (generator_10..12); as an item, the model at 1/8 size.
	 */
	static final class Generator implements BlockEntityRenderer<ReactorGeneratorBlockEntity> {
		private final ModelGenerator model;

		Generator(BlockEntityRendererProvider.Context ctx) {
			model = new ModelGenerator(ctx.bakeLayer(GENERATOR));
		}

		@Override
		public void render(ReactorGeneratorBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			int mode = tile.getMode().ordinal();
			if (tile.getLevel() != null && !tile.hasMultiBlock()) {
				renderCube(pose, buf, light, "block/multi/generator_"+(10+mode));
				return;
			}
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			int rot = switch (tile.getBlockMetadata()) {
				case 0 -> 270;
				case 1 -> 90;
				case 3 -> 180;
				default -> 0;
			};
			pose.mulPose(TechneModel.rotation(rot, 0, 1, 0));
			var vc = buf.getBuffer(RenderType.entityCutout(tex("generator"+mode+".png")));
			if (tile.getLevel() == null) { //item
				pose.mulPose(TechneModel.rotation(180, 0, 1, 0));
				pose.translate(0, 0.875, 0.5);
				pose.scale(0.125F, 0.125F, 0.125F);
				model.renderAll(pose, vc, light, overlay, tile, null, -tile.phi, 0);
			}
			else
				model.renderAll(pose, vc, light, overlay, tile, null, tile.phi, 0);
			pose.popPose();
		}

		@Override
		public AABB getRenderBoundingBox(ReactorGeneratorBlockEntity tile) {
			BlockPos p = tile.getBlockPos();
			Direction f = tile.getFacing();
			int l = ReactorGeneratorBlockEntity.GENERATOR_LENGTH;
			int x1 = p.getX()+1+f.getStepX()*l, z1 = p.getZ()+1+f.getStepZ()*l;
			return new AABB(Math.min(x1, p.getX()), p.getY()-2, Math.min(z1, p.getZ()), Math.max(x1, p.getX()), p.getY()+3,
					Math.max(z1, p.getZ())).inflate(6);
		}

		@Override
		public boolean shouldRenderOffScreen(ReactorGeneratorBlockEntity tile) {
			return true;
		}
	}

	/**
	 * Original RenderTurbineWheel: formed, the flywheel model turning with phi (6x the turbine's); unformed, a cube with the flywheel
	 * dampener texture; as an item, the model at 1/4 size.
	 */
	static final class Flywheel implements BlockEntityRenderer<ReactorFlywheelBlockEntity> {
		private final ModelFlywheel model;

		Flywheel(BlockEntityRendererProvider.Context ctx) {
			model = new ModelFlywheel(ctx.bakeLayer(FLYWHEEL));
		}

		@Override
		public void render(ReactorFlywheelBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			if (tile.getLevel() != null && !tile.hasMultiBlock()) {
				renderCube(pose, buf, light, "block/multi/flywheel_1");
				return;
			}
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			int rot = switch (tile.getBlockMetadata()) {
				case 0 -> 270;
				case 1 -> 90;
				case 3 -> 180;
				default -> 0;
			};
			pose.mulPose(TechneModel.rotation(rot, 0, 1, 0));
			var vc = buf.getBuffer(RenderType.entityCutout(tex("flywheel.png")));
			if (tile.getLevel() == null) { //item
				pose.mulPose(TechneModel.rotation(180, 0, 1, 0));
				pose.translate(0, 0.75, 0);
				pose.scale(0.25F, 0.25F, 0.25F);
				model.renderAll(pose, vc, light, overlay, tile, null, -tile.phi, 0);
			}
			else
				model.renderAll(pose, vc, light, overlay, tile, null, tile.phi, 0);
			pose.popPose();
		}

		@Override
		public AABB getRenderBoundingBox(ReactorFlywheelBlockEntity tile) {
			return new AABB(tile.getBlockPos()).inflate(3);
		}
	}

	/** Original RenderSolarExchanger: the model, unrotated. */
	static final class SolarExchanger implements BlockEntityRenderer<SolarExchangerBlockEntity> {
		private final ModelSolarExchanger model;

		SolarExchanger(BlockEntityRendererProvider.Context ctx) {
			model = new ModelSolarExchanger(ctx.bakeLayer(SOLAR_EXCHANGER));
		}

		@Override
		public void render(SolarExchangerBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("solar.png"))), light, overlay, tile, null, 0, 0);
			pose.popPose();
		}
	}

	/**
	 * Original RenderSolarTop: the receiver model (the cap upside down and turned 90 degrees on top of the lower part), its glowing parts
	 * in the blackbody colour of 200+2*T at full brightness (black as an item); above 400 C the active part also shows the solar flare,
	 * facing the viewer, (T-400)/1500*6 blocks wide and as bright as the mirrors.
	 */
	static final class SolarTop implements BlockEntityRenderer<SolarTopBlockEntity> {
		private final ModelSolarTop model;

		SolarTop(BlockEntityRendererProvider.Context ctx) {
			model = new ModelSolarTop(ctx.bakeLayer(SOLAR_TOP));
		}

		@Override
		public void render(SolarTopBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			boolean inWorld = tile.getLevel() != null;
			boolean flip = inWorld && tile.getLevel().getBlockEntity(tile.getBlockPos().below()) instanceof SolarTopBlockEntity;
			pose.pushPose();
			pose.translate(0, flip ? 0 : 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			if (flip) {
				pose.mulPose(TechneModel.rotation(180, 1, 0, 0));
				pose.mulPose(TechneModel.rotation(90, 0, 1, 0));
			}
			int c = inWorld ? BlackbodyColor.getColorForTemperature(200+tile.getTemperature()*2) : 0x000000;
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("solartop.png"))), light, overlay, tile, List.of(c, 0, 0), 0, 0);
			pose.popPose();
			if (inWorld && tile.isActive() && tile.getTemperature() > 400)
				this.renderFlare(tile, pose, buf);
		}

		private void renderFlare(SolarTopBlockEntity te, PoseStack pose, MultiBufferSource buf) {
			var sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "block/solarflare"));
			double f = (te.getTemperature()-400)/1500D;
			float s = (float)(6*f);
			float b = te.getLevel().getBlockEntity(te.getBlockPos().below()) instanceof SolarTowerBlockEntity t ? t.getArrayOverallBrightness() : 0;
			int g = (int)(b*255);
			int color = 0xFF000000 | g << 16 | g << 8 | g;
			pose.pushPose();
			pose.translate(0.5, 1.0, 0.5); //original: 0.5 above the centre in the flipped model space = the top face
			pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
			pose.scale(s, s, s);
			var vc = buf.getBuffer(RenderType.eyes(InventoryMenu.BLOCK_ATLAS)); //original additive blend
			var m = pose.last();
			vc.addVertex(m, -1, -1, 0).setColor(color).setUv(sprite.getU0(), sprite.getV1()).setOverlay(0).setLight(0xF000F0).setNormal(m, 0, 0, 1);
			vc.addVertex(m, 1, -1, 0).setColor(color).setUv(sprite.getU1(), sprite.getV1()).setOverlay(0).setLight(0xF000F0).setNormal(m, 0, 0, 1);
			vc.addVertex(m, 1, 1, 0).setColor(color).setUv(sprite.getU1(), sprite.getV0()).setOverlay(0).setLight(0xF000F0).setNormal(m, 0, 0, 1);
			vc.addVertex(m, -1, 1, 0).setColor(color).setUv(sprite.getU0(), sprite.getV0()).setOverlay(0).setLight(0xF000F0).setNormal(m, 0, 0, 1);
			pose.popPose();
		}

		@Override
		public AABB getRenderBoundingBox(SolarTopBlockEntity tile) {
			return new AABB(tile.getBlockPos()).inflate(4);
		}
	}

	/** Original RenderElectrolyzer: the model, unrotated. */
	static final class Electrolyzer implements BlockEntityRenderer<ElectrolyzerBlockEntity> {
		private final ModelElectrolyzer model;

		Electrolyzer(BlockEntityRendererProvider.Context ctx) {
			model = new ModelElectrolyzer(ctx.bakeLayer(ELECTROLYZER));
		}

		@Override
		public void render(ElectrolyzerBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("electrolyzer.png"))), light, overlay, tile, null, 0, 0);
			pose.popPose();
		}
	}

	public static final ModelLayerLocation ELECTROLYZER = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "electrolyzer"), "main");

	public static final ModelLayerLocation SOLAR_EXCHANGER = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "solar_exchanger"), "main");
	public static final ModelLayerLocation SOLAR_TOP = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "solar_top"), "main");

	public static final ModelLayerLocation FLYWHEEL = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "turbine_flywheel"), "main");

	public static final ModelLayerLocation GENERATOR = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "reactor_generator"), "main");

	public static final ModelLayerLocation PROCESSOR = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "processor"), "main");

	/**
	 * Original SteamLineRenderer: the steam line's wool is drawn at half brightness, the heat pipe's snow in TileEntityHeatPipe.getRenderColor
	 * (0xFFA54D: its temperature field stays 0 in the original, so the red hot colour never shows).
	 */
	@SubscribeEvent
	public static void blockColors(RegisterColorHandlersEvent.Block e) {
		e.register((state, world, pos, tint) -> 0xFF7F7F7F, ReactorBlocks.STEAM_LINE.get());
		e.register((state, world, pos, tint) -> 0xFFFFA54D, ReactorBlocks.HEAT_PIPE.get());
	}

	/** Original RenderWaterLine: the heat pipe item is drawn in colour (1, 0.65, 0.3). */
	@SubscribeEvent
	public static void itemColors(RegisterColorHandlersEvent.Item e) {
		e.register((stack, tint) -> tint == 0 ? 0xFFFFA54C : -1, ReactorBlocks.HEAT_PIPE.get());
	}

	@SubscribeEvent
	public static void screens(RegisterMenuScreensEvent e) {
		e.register(ReactorMenus.PROCESSOR.get(), UProcessorScreen::new);
		e.register(ReactorMenus.SYNTHESIZER.get(), SynthesizerScreen::new);
		e.register(ReactorMenus.ELECTROLYZER.get(), ElectrolyzerScreen::new);
		e.register(ReactorMenus.ISO_CENTRIFUGE.get(), IsoCentrifugeScreen::new);
		e.register(ReactorMenus.NUCLEAR_CORE.get(), NuclearCoreScreen::new);
		e.register(ReactorMenus.CPU.get(), CpuScreen::new);
		e.register(ReactorMenus.THORIUM_CORE.get(), ThoriumCoreScreen::new);
		e.register(ReactorMenus.WASTE_CONTAINER.get(),
				(WasteMenus.Container m, Inventory i, Component t) -> new WasteScreen<>(m, i, t, "wastecontainer2", 175));
		e.register(ReactorMenus.WASTE_STORAGE.get(),
				(WasteMenus.Storage m, Inventory i, Component t) -> new WasteScreen<>(m, i, t, "wastestorage", 186));
		e.register(ReactorMenus.PEBBLE_BED.get(),
				(WasteMenus.PebbleBed m, Inventory i, Component t) -> new WasteScreen<>(m, i, t, "pebblegui", 240, 237));
		e.register(ReactorMenus.WASTE_DECAYER.get(),
				(WasteMenus.Decayer m, Inventory i, Component t) -> new WasteScreen<>(m, i, t, "wastedecayer", 175));
	}

	/** Original RenderProcessor: lifted by 0.01, turned by the metadata (0: 270, 1: 90, 2: 0, 3: 180). */
	static final class Processor implements BlockEntityRenderer<UProcessorBlockEntity> {
		private final ModelProcessor model;

		Processor(BlockEntityRendererProvider.Context ctx) {
			model = new ModelProcessor(ctx.bakeLayer(PROCESSOR));
		}

		@Override
		public void render(UProcessorBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			pose.translate(0.0F, 0.01F, 0.0F);
			float a = 0;
			if (tile.getLevel() != null) {
				a = switch (tile.getBlockMetadata()) {
					case 0 -> 270;
					case 1 -> 90;
					case 3 -> 180;
					default -> 0;
				};
			}
			pose.mulPose(TechneModel.rotation(a, 0, 1, 0));
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("processor.png"))), light, overlay, tile, null, -tile.phi, 0);
			pose.popPose();
		}
	}

	static ResourceLocation tex(String name) {
		return ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "textures/machine/"+name);
	}

	@SubscribeEvent
	public static void guiLayers(RegisterGuiLayersEvent e) {
		//original IronFinderOverlay drew in the HELMET (camera overlay) pass
		e.registerAbove(VanillaGuiLayers.CAMERA_OVERLAYS, ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "iron_finder"),
				IronFinderOverlay::render);
	}

	@SubscribeEvent
	public static void layers(EntityRenderersEvent.RegisterLayerDefinitions e) {
		FusionRenderers.registerLayers(e);
		e.registerLayerDefinition(HEAVY_PUMP, ModelHeavyPump::createLayer);
		e.registerLayerDefinition(PROCESSOR, ModelProcessor::createLayer);
		e.registerLayerDefinition(ISO_CENTRIFUGE, ModelCentrifuge::createLayer);
		e.registerLayerDefinition(CONTROL_ROD, ModelControl::createLayer);
		e.registerLayerDefinition(STEAM_GRATE, ModelSteamGrate::createLayer);
		e.registerLayerDefinition(CONDENSER, ModelCondenser::createLayer);
		e.registerLayerDefinition(STEAM_DIFFUSER, ModelDiffuser::createLayer);
		e.registerLayerDefinition(PRESSURIZER, ModelReactorPump::createLayer);
		e.registerLayerDefinition(GENERATOR, ModelGenerator::createLayer);
		e.registerLayerDefinition(FLYWHEEL, ModelFlywheel::createLayer);
		e.registerLayerDefinition(ELECTROLYZER, ModelElectrolyzer::createLayer);
		e.registerLayerDefinition(SOLAR_EXCHANGER, ModelSolarExchanger::createLayer);
		e.registerLayerDefinition(SOLAR_TOP, ModelSolarTop::createLayer);
		e.registerLayerDefinition(HEAT_EXCHANGER, ModelExchanger::createLayer);
		e.registerLayerDefinition(WASTE_STORAGE, ModelWasteStorage::createLayer);
		for (int i = 0; i < TURBINE.length; i++) {
			int stage = i;
			e.registerLayerDefinition(TURBINE[i], () -> ModelTurbine.createLayer(stage));
		}
		for (int i = 0; i < BIG_TURBINE.length; i++) {
			int stage = i;
			e.registerLayerDefinition(BIG_TURBINE[i], () -> ModelBigTurbine.createLayer(stage));
		}
	}

	@SubscribeEvent
	public static void renderers(EntityRenderersEvent.RegisterRenderers e) {
		FusionRenderers.registerRenderers(e);
		e.registerEntityRenderer(ReactorEntities.RADIATION.get(), RadiationRenderer::new);
		e.registerEntityRenderer(ReactorEntities.NUCLEAR_WASTE.get(), ItemEntityRenderer::new);
		e.registerEntityRenderer(ReactorEntities.NEUTRON.get(), NeutronRenderer::new);
		e.registerBlockEntityRenderer(ReactorBlockEntities.HEAVY_PUMP.get(), ctx -> new IORenderer<>(new HeavyPump(ctx)));
		e.registerBlockEntityRenderer(ReactorBlockEntities.PROCESSOR.get(), ctx -> new Processor(ctx));
		e.registerBlockEntityRenderer(ReactorBlockEntities.CONTROL_ROD.get(), ctx -> new ControlRod(ctx));
		e.registerBlockEntityRenderer(ReactorBlockEntities.STEAM_GRATE.get(), ctx -> new SteamGrate(ctx));
		e.registerBlockEntityRenderer(ReactorBlockEntities.CONDENSER.get(), ctx -> new Condenser(ctx));
		e.registerBlockEntityRenderer(ReactorBlockEntities.STEAM_DIFFUSER.get(), ctx -> new IORenderer<>(new SteamDiffuser(ctx)));
		e.registerBlockEntityRenderer(ReactorBlockEntities.PRESSURIZER.get(), ctx -> new IORenderer<>(new Pressurizer(ctx)));
		e.registerBlockEntityRenderer(ReactorBlockEntities.WASTE_PIPE.get(), PipeFluidRenderer::new);
		e.registerBlockEntityRenderer(ReactorBlockEntities.GAS_DUCT.get(), PipeFluidRenderer::new);
		e.registerBlockEntityRenderer(ReactorBlockEntities.HEAT_EXCHANGER.get(), ctx -> new IORenderer<>(new HeatExchanger(ctx)));
		e.registerBlockEntityRenderer(ReactorBlockEntities.WASTE_STORAGE.get(), ctx -> new WasteStorage(ctx));
		e.registerBlockEntityRenderer(ReactorBlockEntities.TURBINE_CORE.get(), ctx -> new IORenderer<>(new TurbineCore<>(ctx, false)));
		e.registerBlockEntityRenderer(ReactorBlockEntities.HIP_TURBINE.get(), ctx -> new IORenderer<>(new TurbineCore<>(ctx, true)));
		e.registerBlockEntityRenderer(ReactorBlockEntities.GENERATOR.get(), ctx -> new Generator(ctx));
		e.registerBlockEntityRenderer(ReactorBlockEntities.FLYWHEEL.get(), ctx -> new IORenderer<>(new Flywheel(ctx)));
		e.registerBlockEntityRenderer(ReactorBlockEntities.ELECTROLYZER.get(), ctx -> new Electrolyzer(ctx));
		e.registerBlockEntityRenderer(ReactorBlockEntities.SOLAR_EXCHANGER.get(), ctx -> new IORenderer<>(new SolarExchanger(ctx)));
		e.registerBlockEntityRenderer(ReactorBlockEntities.SOLAR_TOP.get(), ctx -> new SolarTop(ctx));
		e.registerBlockEntityRenderer(ReactorBlockEntities.ISO_CENTRIFUGE.get(), ctx -> new IORenderer<>(new IsoCentrifuge(ctx)));
	}

	/** Original RenderHeavyPump: the model with the impeller turning by -phi. */
	static final class HeavyPump implements BlockEntityRenderer<HeavyPumpBlockEntity> {
		private final ModelHeavyPump model;

		HeavyPump(BlockEntityRendererProvider.Context ctx) {
			model = new ModelHeavyPump(ctx.bakeLayer(HEAVY_PUMP));
		}

		@Override
		public void render(HeavyPumpBlockEntity tile, float partial, PoseStack pose,
				MultiBufferSource buf, int light, int overlay) {
			pose.pushPose();
			pose.translate(0, 2.0F, 1.0F);
			pose.scale(1.0F, -1.0F, -1.0F);
			pose.translate(0.5F, 0.5F, 0.5F);
			model.renderAll(pose, buf.getBuffer(RenderType.entityCutout(tex("heavypump.png"))), light, overlay, tile, null, -tile.phi, 0);
			pose.popPose();
		}
	}

	@SubscribeEvent
	public static void extensions(RegisterClientExtensionsEvent e) {
		//original PotionRadiation had no status icon
		e.registerMobEffect(new IClientMobEffectExtensions() {
			@Override
			public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen,
					GuiGraphics g, int x, int y, int blitOffset) {
				return true;
			}

			@Override
			public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics g,
					int x, int y, float z, float alpha) {
				return true;
			}
		}, ReactorEffects.RADIATION.value());
		MachineItemRenderer[] r = new MachineItemRenderer[1];
		e.registerItem(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				if (r[0] == null)
					r[0] = new MachineItemRenderer();
				return r[0];
			}
		}, ReactorBlocks.FLUID_EXTRACTOR.get().asItem(), ReactorBlocks.PROCESSOR.get().asItem(),
				ReactorBlocks.ISO_CENTRIFUGE.get().asItem(), ReactorBlocks.CONTROL_ROD.get().asItem(),
				ReactorBlocks.STEAM_GRATE.get().asItem(), ReactorBlocks.TURBINE_CORE.get().asItem(),
				ReactorBlocks.CONDENSER.get().asItem(), ReactorBlocks.WASTE_STORAGE.get().asItem(),
				ReactorBlocks.STEAM_DIFFUSER.get().asItem(), ReactorBlocks.PRESSURIZER.get().asItem(),
				ReactorBlocks.HEAT_EXCHANGER.get().asItem(),
				ReactorBlocks.HIP_TURBINE.get().asItem(), ReactorBlocks.GENERATOR.get().asItem(),
				ReactorBlocks.FLYWHEEL.get().asItem(), ReactorBlocks.SOLAR_EXCHANGER.get().asItem(),
				ReactorBlocks.SOLAR_TOP.get().asItem(), ReactorBlocks.ELECTROLYZER.get().asItem(),
				//the fusion machines' item models are builtin/entity too: without this they were drawn as nothing
				ReactorBlocks.TOROID_MAGNET.get().asItem(), ReactorBlocks.SOLENOID.get().asItem(),
				ReactorBlocks.GAS_COLLECTOR.get().asItem(), ReactorBlocks.FUSION_MARKER.get().asItem());
		for (ReactorFluids.Def d : ReactorFluids.DEFS.values()) {
			boolean water = d.texture() == null;
			ResourceLocation still = water ? ResourceLocation.withDefaultNamespace("block/water_still") : ResourceLocation.fromNamespaceAndPath(ReactorCraft.MOD_ID, "block/fluid/"+d.texture());
			ResourceLocation flow = water ? ResourceLocation.withDefaultNamespace("block/water_flow") : still;
			e.registerFluidType(new IClientFluidTypeExtensions() {
				@Override
				public ResourceLocation getStillTexture() {
					return still;
				}

				@Override
				public ResourceLocation getFlowingTexture() {
					return flow;
				}

				/** The 1.7.10 water icon was already blue; the 1.21 one is grey and tinted. */
				@Override
				public int getTintColor() {
					return water ? 0xFF3F76E4 : 0xFFFFFFFF;
				}
			}, ReactorFluids.TYPE.get(d.id()).get());
		}
	}
}

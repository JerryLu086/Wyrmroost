package com.github.wolfshotz.wyrmroost.client.render.entity.butterfly;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.render.RenderHelper;
import com.github.wolfshotz.wyrmroost.client.render.entity.AbstractDragonRenderer;
import com.github.wolfshotz.wyrmroost.entities.dragon.ButterflyLeviathanEntity;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

public class ButterflyLeviathanRenderer extends AbstractDragonRenderer<ButterflyLeviathanEntity, ButterflyLeviathanModel>
{
    public static final ResourceLocation BLUE = resource("body_blue.png");
    public static final ResourceLocation PURPLE = resource("body_purple.png");
    public static final ResourceLocation CHRISTMAS = resource("christmas.png");
    // Special
    public static final ResourceLocation ALBINO = resource("body_albino.png");
    public static final ResourceLocation CHRISTMAS_SPECIAL = resource("christmas_special.png");
    // Glow
    public static final ResourceLocation GLOW = resource("activated.png");
    public static final ResourceLocation CHRISTMAS_GLOW = resource("christmas_activated.png");

    private static final RenderMaterial CONDUIT_CAGE_TEXTURE = new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/cage"));
    private static final RenderMaterial CONDUIT_WIND_TEXTURE = new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/wind"));
    private static final RenderMaterial CONDUIT_VERTICAL_WIND_TEXTURE = new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/wind_vertical"));
    private static final RenderMaterial CONDUIT_OPEN_EYE_TEXTURE = new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/open_eye"));

    public ButterflyLeviathanRenderer(EntityRendererManager manager)
    {
        super(manager, new ButterflyLeviathanModel(), 2f);
        addLayer(new LightningLayer());
        addLayer(new ConduitLayer());
    }

    @Override
    protected void preRenderCallback(ButterflyLeviathanEntity entity, MatrixStack ms, float partialTicks)
    {
        ms.scale(3, 3, 3);
        super.preRenderCallback(entity, ms, partialTicks);
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(ButterflyLeviathanEntity entity)
    {
        int variant = entity.getVariant();

        if (WRConfig.deckTheHalls)
        {
            return variant == -1? CHRISTMAS_SPECIAL : CHRISTMAS;
        }

        switch (variant)
        {
            default:
            case 0:
                return BLUE;
            case 1:
                return PURPLE;
            case -1:
                return ALBINO;
        }
    }

    public static ResourceLocation resource(String png)
    {
        return Wyrmroost.rl(BASE_PATH + "butterfly_leviathan/" + png);
    }

    public class LightningLayer extends LayerRenderer<ButterflyLeviathanEntity, ButterflyLeviathanModel>
    {
        public LightningLayer()
        {
            super(ButterflyLeviathanRenderer.this);
        }

        @Override
        public void render(MatrixStack ms, IRenderTypeBuffer buffer, int packedLight, ButterflyLeviathanEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
        {
            float alpha = MathHelper.clamp(entity.lightningCooldown, 1, 255);
            IVertexBuilder builder = buffer.getBuffer(RenderHelper.getTranslucentGlow(WRConfig.deckTheHalls? CHRISTMAS_GLOW : GLOW));
            getEntityModel().render(ms, builder, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, alpha);
        }
    }

    public class ConduitLayer extends LayerRenderer<ButterflyLeviathanEntity, ButterflyLeviathanModel>
    {
        public ModelRenderer conduitEye;
        public ModelRenderer conduitWind;
        public ModelRenderer conduitCage;

        public ConduitLayer()
        {
            super(ButterflyLeviathanRenderer.this);

            conduitEye = new ModelRenderer(16, 16, 0, 0);
            conduitWind = new ModelRenderer(64, 32, 0, 0);
            conduitCage = new ModelRenderer(32, 16, 0, 0);
            conduitEye.addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.01F);
            conduitWind.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
            conduitCage.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
        }

        @Override
        public void render(MatrixStack ms, IRenderTypeBuffer buffer, int light, ButterflyLeviathanEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float tick, float netHeadYaw, float headPitch)
        {
            if ((entity.getAnimation() == ButterflyLeviathanEntity.CONDUIT_ANIMATION && entity.getAnimationTick() < 15) || !entity.hasConduit())
                return;

            int overlay = getPackedOverlay(entity, getOverlayProgress(entity, partialTicks));
            float rotation = (tick * -0.0375f) * (180f / Mafs.PI);
            float translation = MathHelper.sin(tick * 0.1F) / 2.0F + 0.5F;
            translation = translation * translation + translation;
            if (!entity.canSwim()) headPitch /= 2;

            ms.push();
            ms.scale(0.33f, 0.33f, 0.33f);
            ms.translate(entityModel.head.rotationPointX / 16, entityModel.head.rotationPointY / 16, entityModel.head.rotationPointZ / 16);
            ms.rotate(Vector3f.YP.rotationDegrees(netHeadYaw * 0.75f)); // rotate to match head rotations
            ms.rotate(Vector3f.XP.rotationDegrees(headPitch));
            ms.translate(0,  0.5f - (entity.beachedTimer.get(partialTicks) * 1.1f), -3.65);

            // Cage
            ms.push();
            ms.translate(0, (0.3F + translation * 0.2F), 0);
            Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
            vector3f.normalize();
            ms.rotate(new Quaternion(vector3f, rotation, true));
            conduitCage.render(ms, CONDUIT_CAGE_TEXTURE.getBuffer(buffer, RenderType::getEntityCutoutNoCull), light, overlay);
            ms.pop();

            // Wind
            int gen = entity.ticksExisted / 66 % 3;
            ms.push();
            ms.translate(0, 0.5d, 0);
            if (gen == 1) ms.rotate(Vector3f.XP.rotationDegrees(90));
            else if (gen == 2) ms.rotate(Vector3f.ZP.rotationDegrees(90));
            IVertexBuilder builder = (gen == 1? CONDUIT_VERTICAL_WIND_TEXTURE : CONDUIT_WIND_TEXTURE).getBuffer(buffer, RenderType::getEntityCutoutNoCull);
            conduitWind.render(ms, builder, light, overlay);
            ms.pop();

            // Wind but its the second time
            ms.push();
            ms.scale(0.875f, 0.875f, 0.875f);
            ms.rotate(Vector3f.XP.rotationDegrees(180f));
            ms.rotate(Vector3f.ZP.rotationDegrees(180f));
            conduitWind.render(ms, builder, light, overlay);
            ms.pop();

            // Eye
            ms.push();
            ms.translate(0, (0.3F + translation * 0.2F), 0);
            ms.rotate(Vector3f.YN.rotationDegrees(entity.rotationYaw)); // negate stack rotation from entity for full rotation control
            ms.rotate(Vector3f.YP.rotationDegrees(getRenderManager().info.getYaw()));
            ms.rotate(Vector3f.XP.rotationDegrees(getRenderManager().info.getPitch()));
            ms.scale(0.8f, 0.8f, 0.8f);
            conduitEye.render(ms, CONDUIT_OPEN_EYE_TEXTURE.getBuffer(buffer, RenderType::getEntityCutoutNoCull), light, overlay);
            ms.pop();

            ms.pop();
        }
    }
}

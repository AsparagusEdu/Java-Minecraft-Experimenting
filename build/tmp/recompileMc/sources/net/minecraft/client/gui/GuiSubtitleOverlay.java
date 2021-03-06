package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSubtitleOverlay extends Gui implements ISoundEventListener
{
    private final Minecraft client;
    private final List<GuiSubtitleOverlay.Subtitle> field_184070_f = Lists.<GuiSubtitleOverlay.Subtitle>newArrayList();
    private boolean field_184071_g;

    public GuiSubtitleOverlay(Minecraft clientIn)
    {
        this.client = clientIn;
    }

    public void renderSubtitles(ScaledResolution p_184068_1_)
    {
        if (!this.field_184071_g && this.client.gameSettings.showSubtitles)
        {
            this.client.getSoundHandler().func_184402_a(this);
            this.field_184071_g = true;
        }
        else if (this.field_184071_g && !this.client.gameSettings.showSubtitles)
        {
            this.client.getSoundHandler().func_184400_b(this);
            this.field_184071_g = false;
        }

        if (this.field_184071_g && !this.field_184070_f.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            Vec3d vec3d = new Vec3d(this.client.thePlayer.posX, this.client.thePlayer.posY + (double)this.client.thePlayer.getEyeHeight(), this.client.thePlayer.posZ);
            Vec3d vec3d1 = (new Vec3d(0.0D, 0.0D, -1.0D)).rotatePitch(-this.client.thePlayer.rotationPitch * 0.017453292F).rotateYaw(-this.client.thePlayer.rotationYaw * 0.017453292F);
            Vec3d vec3d2 = (new Vec3d(0.0D, 1.0D, 0.0D)).rotatePitch(-this.client.thePlayer.rotationPitch * 0.017453292F).rotateYaw(-this.client.thePlayer.rotationYaw * 0.017453292F);
            Vec3d vec3d3 = vec3d1.crossProduct(vec3d2);
            int i = 0;
            int j = 0;
            Iterator<GuiSubtitleOverlay.Subtitle> iterator = this.field_184070_f.iterator();

            while (iterator.hasNext())
            {
                GuiSubtitleOverlay.Subtitle guisubtitleoverlay$subtitle = (GuiSubtitleOverlay.Subtitle)iterator.next();

                if (guisubtitleoverlay$subtitle.getStartTime() + 3000L <= Minecraft.getSystemTime())
                {
                    iterator.remove();
                }
                else
                {
                    j = Math.max(j, this.client.fontRendererObj.getStringWidth(guisubtitleoverlay$subtitle.getString()));
                }
            }

            j = j + this.client.fontRendererObj.getStringWidth("<") + this.client.fontRendererObj.getStringWidth(" ") + this.client.fontRendererObj.getStringWidth(">") + this.client.fontRendererObj.getStringWidth(" ");

            for (GuiSubtitleOverlay.Subtitle guisubtitleoverlay$subtitle1 : this.field_184070_f)
            {
                int k = 255;
                String s = guisubtitleoverlay$subtitle1.getString();
                Vec3d vec3d4 = guisubtitleoverlay$subtitle1.getLocation().subtract(vec3d).normalize();
                double d0 = -vec3d3.dotProduct(vec3d4);
                double d1 = -vec3d1.dotProduct(vec3d4);
                boolean flag = d1 > 0.5D;
                int l = j / 2;
                int i1 = this.client.fontRendererObj.FONT_HEIGHT;
                int j1 = i1 / 2;
                float f = 1.0F;
                int k1 = this.client.fontRendererObj.getStringWidth(s);
                int l1 = MathHelper.floor_double(MathHelper.denormalizeClamp(255.0D, 75.0D, (double)((float)(Minecraft.getSystemTime() - guisubtitleoverlay$subtitle1.getStartTime()) / 3000.0F)));
                int i2 = l1 << 16 | l1 << 8 | l1;
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)p_184068_1_.getScaledWidth() - (float)l * f - 2.0F, (float)(p_184068_1_.getScaledHeight() - 30) - (float)(i * (i1 + 1)) * f, 0.0F);
                GlStateManager.scale(f, f, f);
                drawRect(-l - 1, -j1 - 1, l + 1, j1 + 1, (int)((double)k * 0.8D) << 24);
                GlStateManager.enableBlend();

                if (!flag)
                {
                    if (d0 > 0.0D)
                    {
                        this.client.fontRendererObj.drawString(">", l - this.client.fontRendererObj.getStringWidth(">"), -j1, i2 + (k << 24 & -16777216));
                    }
                    else if (d0 < 0.0D)
                    {
                        this.client.fontRendererObj.drawString("<", -l, -j1, i2 + (k << 24 & -16777216));
                    }
                }

                this.client.fontRendererObj.drawString(s, -k1 / 2, -j1, i2 + (k << 24 & -16777216));
                GlStateManager.popMatrix();
                ++i;
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public void func_184067_a(ISound soundIn, SoundEventAccessor accessor)
    {
        if (accessor.func_188712_c() != null)
        {
            String s = accessor.func_188712_c().getFormattedText();

            if (!this.field_184070_f.isEmpty())
            {
                for (GuiSubtitleOverlay.Subtitle guisubtitleoverlay$subtitle : this.field_184070_f)
                {
                    if (guisubtitleoverlay$subtitle.getString().equals(s))
                    {
                        guisubtitleoverlay$subtitle.func_186823_a(new Vec3d((double)soundIn.getXPosF(), (double)soundIn.getYPosF(), (double)soundIn.getZPosF()));
                        return;
                    }
                }
            }

            this.field_184070_f.add(new GuiSubtitleOverlay.Subtitle(s, new Vec3d((double)soundIn.getXPosF(), (double)soundIn.getYPosF(), (double)soundIn.getZPosF())));
        }
    }

    @SideOnly(Side.CLIENT)
    public class Subtitle
    {
        private final String subtitle;
        private long startTime;
        private Vec3d location;

        public Subtitle(String subtitleIn, Vec3d locationIn)
        {
            this.subtitle = subtitleIn;
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }

        public String getString()
        {
            return this.subtitle;
        }

        public long getStartTime()
        {
            return this.startTime;
        }

        public Vec3d getLocation()
        {
            return this.location;
        }

        public void func_186823_a(Vec3d p_186823_1_)
        {
            this.location = p_186823_1_;
            this.startTime = Minecraft.getSystemTime();
        }
    }
}
package wtf.bhopper.nonsense.gui.screens.altmanager;

import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import wtf.bhopper.nonsense.alt.Alt;
import wtf.bhopper.nonsense.util.render.SkinCache;

public class AccountListEntry implements GuiListExtended.IGuiListEntry {

    private final GuiAltManager guiAccountManager;

    public final Alt account;
    private final Minecraft mc;

    private final ResourceLocation locationSkin;

    private long lastClicked = 0L;

    public AccountListEntry(Alt account, GuiAltManager guiAccountManager) {
        this.guiAccountManager = guiAccountManager;
        this.account = account;
        this.mc = Minecraft.getMinecraft();

        this.locationSkin = SkinCache.getSkin(account);
    }

    @Override
    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) { }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {

        this.mc.getTextureManager().bindTexture(locationSkin);
        GlStateManager.enableAlpha();
        Gui.drawModalRectWithCustomSizedTexture(x, y, 32, 32, 32, 32, 256, 256);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 160, 32, 32, 32, 256, 256);
        GlStateManager.disableAlpha();

        this.mc.fontRendererObj.drawStringWithShadow(this.account.getUsername(), x + 35, y + 1, 0xffffff);
        GlStateManager.scale(0.5, 0.5, 0.5);
        this.mc.fontRendererObj.drawStringWithShadow(this.account.getBanStatusString(), x * 2 + 70, y * 2 + 4 + this.mc.fontRendererObj.FONT_HEIGHT * 2, 0xffffff);
        this.mc.fontRendererObj.drawStringWithShadow(UUIDTypeAdapter.fromUUID(this.account.getUuid()), x * 2 + 70, y * 2 + 8 + this.mc.fontRendererObj.FONT_HEIGHT * 3, 0xaaaaaa);
        GlStateManager.scale(2, 2, 2);
    }

    @Override
    public boolean mousePressed(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

        this.guiAccountManager.setSelected(slotIndex);

        if (Minecraft.getSystemTime() - this.lastClicked < 250L) {
            this.account.login();
        }

        this.lastClicked = Minecraft.getSystemTime();

        return false;
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

    }
}


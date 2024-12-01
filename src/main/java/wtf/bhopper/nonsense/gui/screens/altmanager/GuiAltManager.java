package wtf.bhopper.nonsense.gui.screens.altmanager;

import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.alt.Alt;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.util.render.SkinCache;

import java.io.IOException;

public class GuiAltManager extends GuiScreen {

    public static String message = null;
    public static Exception lastError = null;

    private final GuiScreen parentScreen;
    private AccountSelectionList accountSelectionList;

    private Status status = Status.NONE;

    private GuiButton loginButton;
    private GuiButton refreshButton;
    private GuiButton viewButton;
    private GuiButton checkButton;
    private GuiButton deleteButton;


    public GuiAltManager(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.accountSelectionList = new AccountSelectionList(this, this.mc, this.width, this.height, 32, this.height - 64, 36);

        this.createButtons();
    }

    public void createButtons() {
        this.buttonList.add(this.loginButton = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, "Login"));
        this.buttonList.add(this.refreshButton = new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, "Refresh"));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, "Add"));

        this.buttonList.add(this.viewButton = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, "View"));
        this.buttonList.add(this.checkButton = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, "Check"));
        this.buttonList.add(this.deleteButton = new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, "Delete"));

        this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel")));

        this.loginButton.enabled = false;
        this.refreshButton.enabled = false;
        this.viewButton.enabled = false;
        this.checkButton.enabled = false;
        this.deleteButton.enabled = false;
    }

    @Override
    public void updateScreen() {
        this.accountSelectionList.update();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.accountSelectionList.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.accountSelectionList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.accountSelectionList.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        if (!button.enabled) {
            return;
        }

        if (button.id == 1) {
            AccountListEntry entry = this.accountSelectionList.getSelected();
            if (entry != null) {
                entry.account.login();
                Notification.send("Alt Manager", "Logged into account: " + entry.account.getUsername(), NotificationType.SUCCESS, 3000);
            }
        } else if (button.id == 4) {
            AccountListEntry entry = this.accountSelectionList.getSelected();
            if (entry != null) {
                Notification.send("Alt Manager", "Refreshing: " + entry.account.getUsername(), NotificationType.INFO, 3000);
                entry.account.refreshAccount();
            }
        } else if (button.id == 3) {
            this.mc.displayGuiScreen(new GuiAddAlt(this));
        } else if (button.id == 7) {
            // View account
        } else if (button.id == 2) {
            // Ban Check Account
        } else if (button.id == 8) {
            AccountListEntry entry = this.accountSelectionList.getSelected();
            if (entry != null) {
                Nonsense.getAltManager().alts.remove(entry.account.getUuid());
                Notification.send("Alt Manager", "Removed account: " + entry.account.getUsername(), NotificationType.SUCCESS, 3000);
            }
        } else if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (this.status == Status.ADDING) {
            this.mc.displayGuiScreen(this);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.accountSelectionList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, "Account Manager", this.width / 2, 20, 0xffffff);
        drawAccountInfo();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void setSelected(int index) {
        this.accountSelectionList.setIndex(index);

        boolean enable = this.accountSelectionList.getListEntry(index) != null;

        this.loginButton.enabled = enable;
        this.refreshButton.enabled = enable;
        this.viewButton.enabled = enable;
        this.checkButton.enabled = enable;
        this.deleteButton.enabled = enable;


    }

    public static void drawAccountInfo() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(SkinCache.getSkin(mc.getSession().getProfile()));
        GlStateManager.pushMatrix();
        Gui.drawModalRectWithCustomSizedTexture(2, 2, 16, 16, 16, 16, 128, 128);
        Gui.drawModalRectWithCustomSizedTexture(2, 2, 80, 16, 16, 16, 128, 128);
        mc.fontRendererObj.drawStringWithShadow(mc.getSession().getUsername(), 20, 4, 0xffffff);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        try {
            mc.fontRendererObj.drawStringWithShadow(Nonsense.getAltManager().alts.getOrDefault(UUIDTypeAdapter.fromString(mc.getSession().getPlayerID()), Alt.DEFAULT).getBanStatusString(), 40, 28, 0xffffff);
        } catch (IllegalArgumentException exception) {
            mc.fontRendererObj.drawStringWithShadow(Alt.DEFAULT.getBanStatusString(), 40, 28, 0xffffff);
        }
        GlStateManager.popMatrix();
    }

    enum Status {
        NONE,
        ADDING,
        VIEWING
    }

}

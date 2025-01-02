package wtf.bhopper.nonsense.gui.screens.altmanager;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.Sys;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.alt.Alt;
import wtf.bhopper.nonsense.alt.AltManager;
import wtf.bhopper.nonsense.alt.loginthread.BrowserLoginThread;
import wtf.bhopper.nonsense.alt.mslogin.MSAuthScheme;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;

import java.io.IOException;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_openFileDialog;

public class GuiAddBrowserAlt extends GuiScreen {


    private final GuiScreen parentScreen;

    public GuiAddBrowserAlt(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {

        GuiAltManager.message = "Waiting...";

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.format("gui.cancel")));

        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, 66, "Open Browser"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, 90, "Copy Link"));

        AltManager.loginThread = new BrowserLoginThread(loginData -> {
            Alt alt = new Alt(Alt.Type.BROWSER, loginData);
            Nonsense.getAltManager().addAccount(alt);
            alt.login();
            Notification.send("Alt Manager", "Logged into account: " + loginData.username, NotificationType.SUCCESS, 3000);
        }, error -> {
            Nonsense.LOGGER.error("Failed to login to account", error);
            Notification.send("Alt Manager", "Failed to login to account: " + error.getMessage(), NotificationType.ERROR, 3000);
            this.mc.displayGuiScreen(this.parentScreen);
        });
        AltManager.loginThread.start();

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 1) {
            Sys.openURL(MSAuthScheme.LOGIN_URL);
        } else if (button.id == 2) {
            GuiScreen.setClipboardString(MSAuthScheme.LOGIN_URL);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.fontRendererObj.drawStringWithShadow(GuiAltManager.message, this.width / 2.0F - this.fontRendererObj.getStringWidth(GuiAltManager.message) / 2.0F, 124, 0xffffff);
    }
}
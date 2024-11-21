package wtf.bhopper.nonsense.gui.screens.altmanager;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.alt.Alt;
import wtf.bhopper.nonsense.alt.AltManager;
import wtf.bhopper.nonsense.alt.loginthread.CookieLoginThread;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class GuiAddCookieAlt extends GuiScreen {

    private final GuiScreen parentScreen;

    private GuiTextField cookieFile;
    private GuiButton doneButton;

    public GuiAddCookieAlt(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {

        GuiAltManager.message = "Waiting...";

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 18, I18n.format("gui.cancel")));

        this.cookieFile = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, 66, 200, 20);
        this.cookieFile.setMaxStringLength(260);
        this.buttonList.add(new GuiButton(2, this.width / 2 + 104, 66, 50, 20, "Select..."));
        this.buttonList.add(this.doneButton = new GuiButton(1, this.width / 2 - 100, 94, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 1) {
            this.doneButton.enabled = false;
            AltManager.loginThread = new CookieLoginThread(new File(this.cookieFile.getText()), loginData -> {
                Alt account = new Alt(Alt.Type.COOKIE, loginData);
                Nonsense.getAltManager().addAccount(account);
                account.login();
                Notification.send("Alt Manager", "Logged into account: " + loginData.username, NotificationType.SUCCESS, 3000);
                GuiAddCookieAlt.this.mc.displayGuiScreen(GuiAddCookieAlt.this.parentScreen);
            }, error -> {
                Nonsense.LOGGER.error("Failed to login to account", error);
                Notification.send("Alt Manager", "Failed to login to account: " + error.getMessage(), NotificationType.ERROR, 3000);
                GuiAddCookieAlt.this.mc.displayGuiScreen(GuiAddCookieAlt.this.parentScreen);
            });
            AltManager.loginThread.start();
        } else if (button.id == 2) {

//            JFileChooser chooser = new JFileChooser();
//            chooser.setFileFilter(new FileNameExtensionFilter("Exported Cookie Files", "txt"));
//            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//                this.cookieFile.setText(chooser.getSelectedFile().getAbsolutePath());
//            }

            FileDialog dialog = new FileDialog((Frame)null, "Select Cookie File");
            dialog.setMode(FileDialog.LOAD);
            dialog.setFile("*.txt");
            dialog.setVisible(true);
            String file = new File(dialog.getDirectory(), dialog.getFile()).getAbsolutePath();
            dialog.dispose();
            this.cookieFile.setText(file);

        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.cookieFile.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.cookieFile.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        this.cookieFile.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.cookieFile.drawTextBox();
        this.fontRendererObj.drawStringWithShadow(GuiAltManager.message, this.width / 2.0F - this.fontRendererObj.getStringWidth(GuiAltManager.message) / 2.0F, 124, 0xffffff);
    }
}
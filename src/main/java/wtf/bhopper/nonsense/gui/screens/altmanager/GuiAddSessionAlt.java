package wtf.bhopper.nonsense.gui.screens.altmanager;

import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.alt.Alt;
import wtf.bhopper.nonsense.alt.AltManager;
import wtf.bhopper.nonsense.alt.loginthread.CookieLoginThread;
import wtf.bhopper.nonsense.alt.mslogin.LoginData;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class GuiAddSessionAlt extends GuiScreen {

    private final GuiScreen parentScreen;

    private GuiTextField username;
    private GuiTextField uuid;
    private GuiTextField accessToken;
    private GuiButton doneButton;

    public GuiAddSessionAlt(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        GuiAltManager.message = "Waiting...";

        this.username = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, this.height / 2 - 90, 200, 20);
        this.username.setMaxStringLength(16);
        this.uuid = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, this.height / 2 - 50, 200, 20);
        this.uuid.setMaxStringLength(36);
        this.accessToken = new GuiTextField(3, this.fontRendererObj, this.width / 2 - 100, this.height / 2 - 10, 200, 20);
        this.accessToken.setMaxStringLength(2000);

        this.buttonList.add(this.doneButton = new GuiButton(4, this.width / 2 - 100, this.height / 2 + 35, "Done"));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 57, I18n.format("gui.cancel")));
        this.buttonList.add(new GuiButton(12, this.width / 2 + 105, this.height / 2 - 50, 100, 20, "Random UUID"));
        this.buttonList.add(new GuiButton(13, this.width / 2 + 105, this.height / 2 - 90, 100, 20, "Random Username"));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 4) {
            this.doneButton.enabled = false;

            try {
                LoginData data = new LoginData(Alt.Type.SESSION, this.accessToken.getText(), this.uuid.getText(), this.username.getText(), "");
                Alt alt = new Alt(Alt.Type.SESSION, data);
                Nonsense.getAltManager().addAccount(alt);
                alt.login();
                Notification.send("Alt Manager", "Logged into account: " + data.username, NotificationType.SUCCESS, 3000);
                GuiAddSessionAlt.this.mc.displayGuiScreen(GuiAddSessionAlt.this.parentScreen);
            } catch (Exception error) {
                Notification.send("Alt Manager", "Failed to login to account: " + error.getMessage(), NotificationType.ERROR, 3000);
                this.doneButton.enabled = true;
            }

        } else if (button.id == 12) {
            this.uuid.setText(UUIDTypeAdapter.fromUUID(UUID.randomUUID()));
        } else if (button.id == 13) {
            this.username.setText(GeneralUtil.randomUsername(16));
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.username.textboxKeyTyped(typedChar, keyCode);
        this.uuid.textboxKeyTyped(typedChar, keyCode);
        this.accessToken.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.username.mouseClicked(mouseX, mouseY, mouseButton);
        this.uuid.mouseClicked(mouseX, mouseY, mouseButton);
        this.accessToken.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.uuid.updateCursorCounter();
        this.accessToken.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.fontRendererObj.drawStringWithShadow("Username", this.username.xPosition, this.username.yPosition - 1 - this.fontRendererObj.FONT_HEIGHT, -1);
        this.username.drawTextBox();

        this.fontRendererObj.drawStringWithShadow("UUID", this.uuid.xPosition, this.uuid.yPosition - 1 - this.fontRendererObj.FONT_HEIGHT, -1);
        this.uuid.drawTextBox();

        this.fontRendererObj.drawStringWithShadow("Access Token", this.accessToken.xPosition, this.accessToken.yPosition - 1 - this.fontRendererObj.FONT_HEIGHT, -1);
        this.accessToken.drawTextBox();

    }
}
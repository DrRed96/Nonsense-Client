package wtf.bhopper.nonsense.universe.test;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.GuiPasswordField;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.universe.Universe;

import java.io.IOException;

public class GuiTestNetwork extends GuiScreen {

    private GuiTextField username;
    private GuiTextField password;

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, 50, "Start"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, 100, "Stop"));
        this.username = new GuiTextField(101, mc.fontRendererObj, this.width / 2 - 100, 150, 200, 20);
        this.password = new GuiPasswordField(102, mc.fontRendererObj, this.width / 2 - 100, 200, 200, 20);
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, 250, "Request Token"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.username.drawTextBox();
        this.password.drawTextBox();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1 -> Nonsense.getUniverse().connect(Universe.SERVER_URI, Nonsense.getUniverse().getAccessToken());
            case 2 -> Nonsense.getUniverse().disconnect();
            case 3 -> new Thread(() -> {
                if (Nonsense.getUniverse().updateAccessToken(this.username.getText(), this.password.getText())) {
                    Notification.send("Universe", "Received token.", NotificationType.SUCCESS, 3000);
                } else {
                    Notification.send("Universe", "Failed to update token.", NotificationType.ERROR, 3000);
                }
            }).start();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.username.textboxKeyTyped(typedChar, keyCode);
        this.password.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.username.mouseClicked(mouseX, mouseY, mouseButton);
        this.password.mouseClicked(mouseX, mouseY, mouseButton);
    }



}

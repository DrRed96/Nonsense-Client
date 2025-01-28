package wtf.bhopper.nonsense.module.impl.other;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.GuiPasswordField;
import wtf.bhopper.nonsense.gui.hud.notification.Notification;
import wtf.bhopper.nonsense.gui.hud.notification.NotificationType;
import wtf.bhopper.nonsense.module.Module;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.ButtonProperty;
import wtf.bhopper.nonsense.module.property.impl.StringProperty;
import wtf.bhopper.nonsense.universe.Universe;

import java.io.IOException;

@ModuleInfo(name = "IRC",
        description = "Configure the IRC",
        category = ModuleCategory.OTHER,
        hidden = true, toggled = true)
public class IrcMod extends Module {

    public final StringProperty chatPrefix = new StringProperty("Chat Prefix", "Prefix for IRC chat", "-");
    public final ButtonProperty openLogin = new ButtonProperty("Login", "Login to the IRC", () -> mc.displayGuiScreen(new GuiLogin()));
    public final ButtonProperty reconnect = new ButtonProperty("Reconnect", "Reconnect to the IRC", () -> {
        new Thread(() -> {
            if (Nonsense.getUniverse().isConnected()) {
                Notification.send("Universe", "You are already connected!", NotificationType.SUCCESS, 3000);
                return;
            }
            Notification.send("Universe", "Reconnecting to the Nonsense Universe.", NotificationType.INFO, 3000);
            Nonsense.getUniverse().connect();
        }).start();
    });

    public IrcMod() {
        this.addProperties(this.chatPrefix, this.openLogin, this.reconnect);
    }

    @Override
    public void onDisable() {
        Nonsense.getUniverse().disconnect();
    }

    public static class GuiLogin extends GuiScreen {

        private GuiTextField username;
        private GuiTextField password;

        @Override
        public void initGui() {
            this.username = new GuiTextField(101, mc.fontRendererObj, this.width / 2 - 100, this.height / 2 - 60, 200, 20);
            this.password = new GuiPasswordField(102, mc.fontRendererObj, this.width / 2 - 100, this.height / 2 - 20, 200, 20);
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + 20, "Login"));
            this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 60, "Cancel"));
        }

        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
            switch (button.id) {
                case 0 -> mc.displayGuiScreen(null);
                case 1 -> {
                    new Thread(() -> {
                        Notification.send("Universe", "Logging in...", NotificationType.INFO, 5000);

                        boolean tokenResult = Nonsense.getUniverse().updateAccessToken(this.username.getText(), this.password.getText());

                        if (!tokenResult) {
                            Notification.send("Universe", "Failed to get access token: " + Universe.lastError, NotificationType.ERROR, 5000);
                            return;
                        }

                        if (!Nonsense.getUniverse().isConnected()) {
                            Nonsense.getUniverse().connect();
                        } else {
                            Notification.send("Universe", "Updated access token.", NotificationType.SUCCESS, 3000);
                        }

                    }).start();
                    mc.displayGuiScreen(null);
                }
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.username.drawTextBox();
            this.password.drawTextBox();

            this.drawString(mc.fontRendererObj, "Username", this.username.xPosition, this.username.yPosition - mc.fontRendererObj.FONT_HEIGHT - 1, 0xFFFFFFFF);
            this.drawString(mc.fontRendererObj, "Password", this.password.xPosition, this.password.yPosition - mc.fontRendererObj.FONT_HEIGHT - 1, 0xFFFFFFFF);
        }

        @Override
        public void updateScreen() {
            super.updateScreen();
            this.username.updateCursorCounter();
            this.password.updateCursorCounter();
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

}

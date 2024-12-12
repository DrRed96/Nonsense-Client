package wtf.bhopper.nonsense.network;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import wtf.bhopper.nonsense.Nonsense;

import java.io.IOException;

public class GuiTestNetwork extends GuiScreen {

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, 5, "Start"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        switch (button.id) {
            case 1 -> Nonsense.getNetHandler().connect();
        }

    }
}

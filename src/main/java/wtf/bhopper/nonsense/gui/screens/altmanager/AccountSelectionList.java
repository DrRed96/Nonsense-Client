package wtf.bhopper.nonsense.gui.screens.altmanager;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.alt.Alt;

import java.util.List;

public class AccountSelectionList extends GuiListExtended {

    private final GuiAltManager owner;
    private int index = -1;

    private final List<AccountListEntry> entries = Lists.newArrayList();

    public AccountSelectionList(GuiAltManager owner, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = owner;
    }

    @Override
    public AccountListEntry getListEntry(int index) {
        if (index < this.entries.size()) {
            return this.entries.get(index);
        }

        return null;
    }

    @Override
    public int getSize() {
        return this.entries.size();
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return slotIndex == index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public AccountListEntry getSelected() {
        return this.getListEntry(this.index);
    }

    public void update() {
        this.entries.clear();

        for (Alt account : Nonsense.getAltManager().alts.values()) {
            this.entries.add(new AccountListEntry(account, owner));
        }
    }

    protected int getScrollBarX() {
        return super.getScrollBarX() + 30;
    }

    public int getListWidth() {
        return super.getListWidth() + 85;
    }

}

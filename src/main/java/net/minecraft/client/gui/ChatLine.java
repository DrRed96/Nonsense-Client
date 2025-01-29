package net.minecraft.client.gui;

import net.minecraft.util.IChatComponent;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.hud.Hud;

public class ChatLine
{
    /** GUI Update Counter value this Line was created at */
    private final int updateCounterCreated;
    private final IChatComponent lineString;

    /**
     * int value to refer to existing Chat Lines, can be 0 which means unreferrable
     */
    private final int chatLineID;
    private final ChatColor color;

    public ChatLine(int updateCounterCreated, IChatComponent lineString, int chatLineID, ChatColor chatColor)
    {
        this.lineString = lineString;
        this.updateCounterCreated = updateCounterCreated;
        this.chatLineID = chatLineID;
        this.color = chatColor;
    }

    public IChatComponent getChatComponent()
    {
        return this.lineString;
    }

    public int getUpdatedCounter()
    {
        return this.updateCounterCreated;
    }

    public int getChatLineID()
    {
        return this.chatLineID;
    }

    public int getColor() {
        if (this.color == null) {
            return 0xFFFFFF;
        }
        return this.color.getColor();
    }

    public ChatColor getChatColor() {
        return this.color;
    }

    public static class ChatColor {
        private final boolean hud;
        private final int color;

        public ChatColor() {
            this.hud = true;
            this.color = 0;
        }

        public ChatColor(int color) {
            this.hud = false;
            this.color = color & 0xFFFFFF;
        }

        public int getColor() {
            if (this.hud) {
                return Hud.color() & 0xFFFFFF;
            }

            return this.color;
        }

    }

}

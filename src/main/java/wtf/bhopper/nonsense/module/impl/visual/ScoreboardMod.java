package wtf.bhopper.nonsense.module.impl.visual;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import wtf.bhopper.nonsense.gui.hud.Hud;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;

import java.util.List;

@ModuleInfo(name = "Scoreboard",
        description = "Improves the scoreboard",
        category = ModuleCategory.VISUAL)
public class ScoreboardMod extends AbstractModule {

    private final BooleanProperty shadow = new BooleanProperty("Shadow", "Draws a drop shadow on the text.", true);
    private final BooleanProperty noScore = new BooleanProperty("No Score", "Prevents the score (red numbers) from rendering.", true);
    private final BooleanProperty noBackground = new BooleanProperty("No Background", "Prevents the background from rendering.", false);
    private final BooleanProperty novoline = new BooleanProperty("Novoline", "Does the Novoline thing lol", false);
    private final BooleanProperty moduleListFix = new BooleanProperty("Module List Fix", "Prevents the scoreboard from overlapping with the module list", true);

    public ScoreboardMod() {
        super();
        this.addProperties(this.shadow, this.noScore, this.noBackground, this.novoline, this.moduleListFix);
    }

    public void drawScoreboard(ScaledResolution scaledRes, ScoreObjective objective, Scoreboard scoreboard, List<Score> scores, int minY) {
        FontRenderer font = mc.fontRendererObj;

        int objectiveWidth = font.getStringWidth(objective.getDisplayName());

        for (Score score : scores) {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            String teamStr = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());

            if (this.isNovoline(teamStr)) {
                teamStr = "www.bhopper.wtf";
            }

            if (!this.noScore.get()) {
                teamStr += ": " + EnumChatFormatting.RED + score.getScorePoints();
            }

            objectiveWidth = Math.max(objectiveWidth, font.getStringWidth(teamStr));
        }

        int j1 = scores.size() * font.FONT_HEIGHT;
        int k1 = scaledRes.getScaledHeight() / 2 + j1 / 3;
        byte b0 = 3;
        int j = scaledRes.getScaledWidth() - objectiveWidth - b0;
        int scoreIndex = 0;

        if (this.moduleListFix.get()) {
            k1 = Math.max(k1, minY + j1 + font.FONT_HEIGHT + 2);
        }

        for (Score score : scores) {
            ++scoreIndex;
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            String renderTeam = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
            String renderScore = this.noScore.get() ? "" : EnumChatFormatting.RED + "" + score.getScorePoints();
            int teamColor = 0x20ffffff;

            if (this.isNovoline(renderTeam)) {
                renderTeam = "www.bhopper.wtf";
                teamColor = Hud.color();
            }

            int yOff = k1 - scoreIndex * font.FONT_HEIGHT;
            int i1 = scaledRes.getScaledWidth() - b0 + 2;
            if (!this.noBackground.get()) {
                Gui.drawRect(j - 2, yOff, i1, yOff + font.FONT_HEIGHT, 0x50000000);
            }
            font.drawString(renderTeam, j, yOff, teamColor, this.shadow.get());
            font.drawString(renderScore, i1 - font.getStringWidth(renderScore), yOff, 0x20ffffff, this.shadow.get());

            if (scoreIndex == scores.size()) {
                String displayObjective = objective.getDisplayName();
                if (!this.noBackground.get()) {
                    Gui.drawRect(j - 2, yOff - font.FONT_HEIGHT - 1, i1, yOff - 1, 0x60000000);
                    Gui.drawRect(j - 2, yOff - 1, i1, yOff, 0x50000000);
                }
                font.drawString(displayObjective, j + (float)(objectiveWidth / 2) - (float)(font.getStringWidth(displayObjective) / 2), yOff - font.FONT_HEIGHT, 0x20ffffff, this.shadow.get());
            }
        }
    }

    public boolean isNovoline(String text) {
        if (!novoline.get()) {
            return false;
        }
        return EnumChatFormatting.getTextWithoutFormattingCodes(text).contains("www.hypixel.") || EnumChatFormatting.getTextWithoutFormattingCodes(text).contains("miniblox.io");
    }

}

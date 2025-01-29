package wtf.bhopper.nonsense.module.impl.other;

import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.client.EventTick;
import wtf.bhopper.nonsense.module.AbstractModule;
import wtf.bhopper.nonsense.module.ModuleCategory;
import wtf.bhopper.nonsense.module.ModuleInfo;
import wtf.bhopper.nonsense.module.property.impl.BooleanProperty;
import wtf.bhopper.nonsense.module.property.impl.GroupProperty;
import wtf.bhopper.nonsense.module.property.impl.NumberProperty;
import wtf.bhopper.nonsense.module.property.impl.StringProperty;
import wtf.bhopper.nonsense.util.minecraft.player.ChatUtil;
import wtf.bhopper.nonsense.util.misc.GeneralUtil;
import wtf.bhopper.nonsense.util.misc.Stopwatch;

import java.util.regex.Matcher;

@ModuleInfo(name = "Spammer",
        description = "Spams a message in chat.",
        category = ModuleCategory.OTHER)
public class Spammer extends AbstractModule {

    private final StringProperty message = new StringProperty("Message", "Message to send.", "weed");
    private final NumberProperty delay = new NumberProperty("Delay", "Delay between sending messages.", 3000, 0, 30000, 50, NumberProperty.FORMAT_MS);

    private final GroupProperty randomGroup = new GroupProperty("Random", "Randomizes the message send.", this);
    private final BooleanProperty randomAppend = new BooleanProperty("Append", "Appends random characters onto the end of the message.", false);
    private final NumberProperty randomAppendLength = new NumberProperty("Append Length", "Random characters length.", 8, 1, 16, 1);
    private final BooleanProperty randomReplace = new BooleanProperty("Replace", "Replaces %rand% with random characters.", false);
    private final NumberProperty randomReplaceLength = new NumberProperty("Replace Length", "Random characters length.", 3, 1, 16, 1);

    private final Stopwatch stopwatch = new Stopwatch();

    public Spammer() {
        super();
        this.randomGroup.addProperties(this.randomAppend, this.randomAppendLength, this.randomReplace, this.randomReplaceLength);
        this.addProperties(this.message, this.delay, this.randomGroup);
    }

    @EventLink
    public final Listener<EventTick> onTick = _ -> {
        if (!this.stopwatch.hasReached(this.delay.getInt())) {
            return;
        }

        String msg = this.message.get();

        if (this.randomReplace.get()) {
            while (msg.contains("%rand%")) {
                String random = Matcher.quoteReplacement(GeneralUtil.randomString(this.randomReplaceLength.getInt()));
                msg = msg.replaceFirst("%rand%", random);
            }
        }

        if (this.randomAppend.get()) {
            msg += GeneralUtil.randomString(this.randomAppendLength.getInt());
        }

        ChatUtil.send("%s", msg);
        this.stopwatch.reset();
    };

}

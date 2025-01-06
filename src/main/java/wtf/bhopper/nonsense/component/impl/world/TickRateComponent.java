package wtf.bhopper.nonsense.component.impl.world;

import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.util.MathHelper;
import wtf.bhopper.nonsense.component.Component;
import wtf.bhopper.nonsense.event.EventLink;
import wtf.bhopper.nonsense.event.Listener;
import wtf.bhopper.nonsense.event.impl.player.EventJoinGame;
import wtf.bhopper.nonsense.event.impl.packet.EventReceivePacket;
import wtf.bhopper.nonsense.util.minecraft.player.PlayerUtil;

import java.util.Arrays;

public class TickRateComponent extends Component {

    private final float[] tickRates = new float[20];
    private int nextIndex = 0;
    private long lastUpdateTime = -1;
    private long gameJoinTime;

    @EventLink
    public final Listener<EventReceivePacket> onReceivePacket = event -> {
        if (event.packet instanceof S03PacketTimeUpdate) {
            long now = System.currentTimeMillis();
            float timeElapsed = (float)(now - this.lastUpdateTime) / 1000.0F;
            this.tickRates[this.nextIndex] = MathHelper.clamp_float(20.0F / timeElapsed, 0.0F, 20.0F);
            this.nextIndex = (this.nextIndex + 1) % this.tickRates.length;
            this.lastUpdateTime = now;
        }
    };

    @EventLink
    public final Listener<EventJoinGame> onJoinGame = event -> {
        Arrays.fill(this.tickRates, 0.0F);
        this.nextIndex = 0;
        this.gameJoinTime = this.lastUpdateTime = System.currentTimeMillis();
    };

    public float getTickRate() {
        if (!PlayerUtil.canUpdate()) {
            return 0.0F;
        }

        if (System.currentTimeMillis() - this.gameJoinTime < 4000L) {
            return 20.0F;
        }

        int ticks = 0;
        float sumTickRates = 0.0F;
        for (float tickRate : this.tickRates) {
            if (tickRate > 0) {
                sumTickRates += tickRate;
                ticks++;
            }
        }

        return sumTickRates / ticks;
    }

    public long timeSinceLastTickMS() {
        long now = System.currentTimeMillis();
        if (now - this.gameJoinTime < 4000L) {
            return 0L;
        }
        return now - this.lastUpdateTime;
    }

}

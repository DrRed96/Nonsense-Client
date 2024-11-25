package wtf.bhopper.nonsense.network.connection;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer2;
import wtf.bhopper.nonsense.network.NonsenseNetHandler;
import wtf.bhopper.nonsense.network.connection.packet.NonsensePacket;

import java.net.SocketAddress;

public class NonsenseConnection extends SimpleChannelInboundHandler<NonsensePacket> {

    public static final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP = new LazyLoadBase<>() {
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }
    };

    private Channel channel;
    private NonsenseNetHandler handler;
    private SocketAddress socketAddress;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NonsensePacket msg) throws Exception {
        if (this.channel.isOpen()) {
            try {
                msg.processPacket(this.handler);
            } catch (Exception ignored) {}
        }
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        this.socketAddress = this.channel.remoteAddress();
    }

    public void setHandler(NonsenseNetHandler handler) {
        this.handler = handler;
    }

    public NonsenseConnection connect() {
        final NonsenseConnection network = new NonsenseConnection();

        new Bootstrap().group(CLIENT_NIO_EVENTLOOP.getValue()).handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException ignored) { }

                channel.pipeline()
                        .addLast("timeout", new ReadTimeoutHandler(30))
                        .addLast("splitter", new MessageDeserializer2())
                        .addLast("decoder", new NonsenseDeserializer())
                        .addLast("prepender", new MessageSerializer2())
                        .addLast("encoder", new NonsenseSerializer())
                        .addLast("packet_handler", network);


            }
        }).channel(NioSocketChannel.class).connect().syncUninterruptibly();
        return network;
    }


}

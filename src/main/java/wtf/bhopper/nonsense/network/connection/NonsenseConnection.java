package wtf.bhopper.nonsense.network.connection;

import com.google.common.collect.Queues;
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

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NonsenseConnection extends SimpleChannelInboundHandler<NonsensePacket> {

    public static final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP = new LazyLoadBase<>() {
        protected NioEventLoopGroup load() {
            return new NioEventLoopGroup(0, new ThreadFactoryBuilder()
                    .setNameFormat("Netty Client IO #%d")
                    .setDaemon(true)
                    .build());
        }
    };

    private Channel channel;
    private final NonsenseNetHandler handler;
    private SocketAddress socketAddress;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Queue<NonsensePacket> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();

    public NonsenseConnection(NonsenseNetHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NonsensePacket packet) throws Exception {
        if (this.channel.isOpen()) {
            try {
                packet.processPacket(this.handler);
            } catch (Exception ignored) {
            }
        }
    }

    public void sendPacket(NonsensePacket packet) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packet);
        } else {
            this.lock.writeLock().lock();
            try {
                this.outboundPacketsQueue.add(packet);
            } finally {
                this.lock.writeLock().unlock();
            }
        }
    }

    private void dispatchPacket(final NonsensePacket inPacket) {
        if (this.channel.eventLoop().inEventLoop()) {
            ChannelFuture channelfuture = this.channel.writeAndFlush(inPacket);
            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(() -> {
                ChannelFuture channelfuture1 = this.channel.writeAndFlush(inPacket);
                channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        this.socketAddress = this.channel.remoteAddress();
    }

    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    private void flushOutboundQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            this.lock.readLock().lock();

            try {
                while (!this.outboundPacketsQueue.isEmpty()) {
                    this.dispatchPacket(this.outboundPacketsQueue.poll());
                }
            } finally {
                this.lock.readLock().unlock();
            }
        }
    }

    public static NonsenseConnection connect(InetAddress address, int port, NonsenseNetHandler handler) {
        final NonsenseConnection network = new NonsenseConnection(handler);

        new Bootstrap()
                .group(CLIENT_NIO_EVENTLOOP.getValue())
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        try {
                            channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                        } catch (ChannelException ignored) {}
                        channel.pipeline()
                                .addLast("timeout", new ReadTimeoutHandler(30))
                                .addLast("splitter", new MessageDeserializer2())
                                .addLast("decoder", new NonsenseDecoder())
                                .addLast("prepender", new MessageSerializer2())
                                .addLast("encoder", new NonsenseEncoder())
                                .addLast("packet_handler", network);
                    }
                })
                .channel(NioSocketChannel.class)
                .connect(address, port)
                .syncUninterruptibly();
        return network;
    }


}

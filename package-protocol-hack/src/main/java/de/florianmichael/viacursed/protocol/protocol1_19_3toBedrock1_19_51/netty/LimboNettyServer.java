package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.netty;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viacursed.ViaCursed;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("Guava")
public class LimboNettyServer {
    private static final Supplier<NioEventLoopGroup> DEFAULT_CHANNEL = Suppliers.memoize(() -> new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build()));
    private static final Supplier<EpollEventLoopGroup> EPOLL_CHANNEL = Suppliers.memoize(() -> new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build()));

    private ChannelFuture future;
    private final InetSocketAddress targetAddress;

    public LimboNettyServer(InetSocketAddress targetAddress) {
        this.targetAddress = targetAddress;
    }

    public void startServer() {
        ViaCursed.getPlatform().getLogger().log(Level.INFO, "Starting local ProxyServer...");

        this.future = new ServerBootstrap().channel(LocalServerChannel.class).childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(@NotNull Channel channel) {
                channel.pipeline().addLast("splitter", new ByteToMessageDecoder() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
                        in.markReaderIndex();
                        final byte[] wrappedData = new byte[3];

                        for(int i = 0; i < wrappedData.length; ++i) {
                            if (!in.isReadable()) {
                                in.resetReaderIndex();
                                return;
                            }

                            wrappedData[i] = in.readByte();

                            if (wrappedData[i] >= 0) {
                                final ByteBuf wrappedBuffer = Unpooled.wrappedBuffer(wrappedData);
                                try {
                                    final int packetId = Type.VAR_INT.readPrimitive(wrappedBuffer);

                                    if (wrappedBuffer.readableBytes() >= packetId) {
                                        out.add(wrappedBuffer.readBytes(packetId));
                                        return;
                                    }
                                    wrappedBuffer.resetReaderIndex();
                                } finally {
                                    wrappedBuffer.release();
                                }
                                return;
                            }
                        }
                        throw new CorruptedFrameException("length wider than 21-bit");
                    }
                });
                channel.pipeline().addLast("decoder", new ByteToMessageDecoder() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {}
                });
                // It's impossible that the Limbo Servers sends packets, so we don't need an encoder here
            }
        }).group(Epoll.isAvailable() ? EPOLL_CHANNEL.get() : DEFAULT_CHANNEL.get()).localAddress(LocalAddress.ANY).bind().syncUninterruptibly();
        ViaCursed.getPlatform().getLogger().log(Level.INFO, "Started local ProxyServer!");
    }

    public void stopServer() {
        if (future == null) return;

        ViaCursed.getPlatform().getLogger().log(Level.INFO, "Stopping local ProxyServer...");
        this.future.channel().close().syncUninterruptibly();
    }

    public ChannelFuture getFuture() {
        return future;
    }

    public InetSocketAddress getTargetAddress() {
        return targetAddress;
    }
}

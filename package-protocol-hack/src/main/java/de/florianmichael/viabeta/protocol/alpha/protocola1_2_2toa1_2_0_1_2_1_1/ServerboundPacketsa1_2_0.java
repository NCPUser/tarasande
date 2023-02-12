package de.florianmichael.viabeta.protocol.alpha.protocola1_2_2toa1_2_0_1_2_1_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import de.florianmichael.viabeta.pre_netty.type.PreNettyTypes;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

public enum ServerboundPacketsa1_2_0 implements ServerboundPacketType, PreNettyPacketType {

    KEEP_ALIVE(0, (user, buf) -> {
    }),
    LOGIN(1, (user, buf) -> {
        buf.skipBytes(4);
        PreNettyTypes.readUTF(buf);
        PreNettyTypes.readUTF(buf);
        buf.skipBytes(9);
    }),
    HANDSHAKE(2, (user, buf) -> {
        PreNettyTypes.readUTF(buf);
    }),
    CHAT_MESSAGE(3, (user, buf) -> {
        PreNettyTypes.readUTF(buf);
    }),
    PLAYER_INVENTORY(5, (user, buf) -> {
        buf.skipBytes(4);
        int x = buf.readShort();
        for (int i = 0; i < x; i++) PreNettyTypes.readItemStackb1_2(buf);
    }),
    PLAYER_MOVEMENT(10, (user, buf) -> {
        buf.skipBytes(1);
    }),
    PLAYER_POSITION(11, (user, buf) -> {
        buf.skipBytes(33);
    }),
    PLAYER_ROTATION(12, (user, buf) -> {
        buf.skipBytes(9);
    }),
    PLAYER_POSITION_AND_ROTATION(13, (user, buf) -> {
        buf.skipBytes(41);
    }),
    PLAYER_DIGGING(14, (user, buf) -> {
        buf.skipBytes(11);
    }),
    PLAYER_BLOCK_PLACEMENT(15, (user, buf) -> {
        buf.skipBytes(12);
    }),
    HELD_ITEM_CHANGE(16, (user, buf) -> {
        buf.skipBytes(6);
    }),
    ANIMATION(18, (user, buf) -> {
        buf.skipBytes(5);
    }),
    SPAWN_ITEM(21, (user, buf) -> {
        buf.skipBytes(22);
    }),
    COMPLEX_ENTITY(59, (user, buf) -> {
        buf.skipBytes(10);
        int x = buf.readUnsignedShort();
        for (int i = 0; i < x; i++) buf.readByte();
    }),
    DISCONNECT(255, (user, buf) -> {
        PreNettyTypes.readUTF(buf);
    });

    private static final ServerboundPacketsa1_2_0[] REGISTRY = new ServerboundPacketsa1_2_0[256];

    static {
        for (ServerboundPacketsa1_2_0 packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ServerboundPacketsa1_2_0 getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ServerboundPacketsa1_2_0(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
        this.id = id;
        this.packetReader = packetReader;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public BiConsumer<UserConnection, ByteBuf> getPacketReader() {
        return this.packetReader;
    }

}

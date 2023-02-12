package de.florianmichael.viabeta.protocol.beta.protocolb1_5_0_2tob1_4_0_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

import static de.florianmichael.viabeta.pre_netty.type.PreNettyTypes.readItemStackb1_2;
import static de.florianmichael.viabeta.pre_netty.type.PreNettyTypes.readUTF;

public enum ServerboundPacketsb1_4 implements ServerboundPacketType, PreNettyPacketType {

    KEEP_ALIVE(0, (user, buf) -> {
    }),
    LOGIN(1, (user, buf) -> {
        buf.skipBytes(4);
        readUTF(buf);
        readUTF(buf);
        buf.skipBytes(9);
    }),
    HANDSHAKE(2, (user, buf) -> {
        readUTF(buf);
    }),
    CHAT_MESSAGE(3, (user, buf) -> {
        readUTF(buf);
    }),
    INTERACT_ENTITY(7, (user, buf) -> {
        buf.skipBytes(9);
    }),
    RESPAWN(9, (user, buf) -> {
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
        buf.skipBytes(10);
        readItemStackb1_2(buf);
    }),
    HELD_ITEM_CHANGE(16, (user, buf) -> {
        buf.skipBytes(2);
    }),
    ANIMATION(18, (user, buf) -> {
        buf.skipBytes(5);
    }),
    ENTITY_ACTION(19, (user, buf) -> {
        buf.skipBytes(5);
    }),
    POSITION(27, (user, buf) -> {
        buf.skipBytes(16);
        buf.readBoolean();
        buf.readBoolean();
    }),
    CLOSE_WINDOW(101, (user, buf) -> {
        buf.skipBytes(1);
    }),
    CLICK_WINDOW(102, (user, buf) -> {
        buf.skipBytes(6);
        readItemStackb1_2(buf);
    }),
    WINDOW_CONFIRMATION(106, (user, buf) -> {
        buf.skipBytes(4);
    }),
    UPDATE_SIGN(130, (user, buf) -> {
        buf.skipBytes(10);
        readUTF(buf);
        readUTF(buf);
        readUTF(buf);
        readUTF(buf);
    }),
    DISCONNECT(255, (user, buf) -> {
        readUTF(buf);
    });

    private static final ServerboundPacketsb1_4[] REGISTRY = new ServerboundPacketsb1_4[256];

    static {
        for (ServerboundPacketsb1_4 packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ServerboundPacketsb1_4 getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ServerboundPacketsb1_4(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
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

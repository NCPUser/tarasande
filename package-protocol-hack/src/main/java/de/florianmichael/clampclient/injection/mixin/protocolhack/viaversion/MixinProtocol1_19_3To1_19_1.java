/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 6/24/22, 8:12 PM
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */
/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 * <p>
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 * <p>
 * Changelog:
 * v1.0:
 * Added License
 * v1.1:
 * Ownership withdrawn
 * v1.2:
 * Version-independent validity and automatic renewal
 */

package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion;

import com.google.common.primitives.Longs;
import com.viaversion.viabackwards.protocol.protocol1_19_1to1_19_3.storage.NonceStorage;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.BitSetType;
import com.viaversion.viaversion.api.type.types.ByteArrayType;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ClientboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ServerboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ClientboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.Protocol1_19_3To1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ServerboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.storage.ReceivedMessagesStorage;
import kotlin.Pair;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.all_model.MessageMetadata1_19_all;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.v1_19_2.ChatSession1_19_2;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.v1_19_2.CommandArgumentsProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = Protocol1_19_3To1_19_1.class, remap = false)
public class MixinProtocol1_19_3To1_19_1 extends AbstractProtocol<ClientboundPackets1_19_1, ClientboundPackets1_19_3, ServerboundPackets1_19_1, ServerboundPackets1_19_3> {

    @Unique
    private static final ByteArrayType.OptionalByteArrayType OPTIONAL_MESSAGE_SIGNATURE_BYTES_TYPE = new ByteArrayType.OptionalByteArrayType(256);

    @Unique
    private static final ByteArrayType MESSAGE_SIGNATURE_BYTES_TYPE = new ByteArrayType(256);

    @Unique
    private static final BitSetType ACKNOWLEDGED_BIT_SET_TYPE = new BitSetType(20);

    @Unique
    private static final UUID ZERO_UUID = new UUID(0, 0);

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void fixKeys(CallbackInfo ci) {
        this.registerClientbound(State.LOGIN, ClientboundLoginPackets.HELLO.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // Server-ID
                map(Type.BYTE_ARRAY_PRIMITIVE); // Public Key
                map(Type.BYTE_ARRAY_PRIMITIVE); // Nonce
                handler(wrapper -> wrapper.user().put(new NonceStorage(wrapper.get(Type.BYTE_ARRAY_PRIMITIVE, 1))));
            }
        });
        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler(wrapper -> {
                    final UUID uuid = wrapper.read(Type.OPTIONAL_UUID);

                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    wrapper.write(Type.OPTIONAL_PROFILE_KEY, chatSession1192 != null ? chatSession1192.getProfileKey() : null);

                    wrapper.write(Type.OPTIONAL_UUID, uuid);
                });
            }
        });
        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.ENCRYPTION_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE_ARRAY_PRIMITIVE); // Keys
                create(Type.BOOLEAN, true); // Is nonce
                map(Type.BYTE_ARRAY_PRIMITIVE); // Encrypted challenge

                handler(wrapper -> {
                    final NonceStorage nonceStorage = wrapper.user().get(NonceStorage.class);
                    if (nonceStorage != null) {
                        final byte[] nonce = nonceStorage.nonce();
                        if (nonce == null) {
                            throw new IllegalStateException("Didn't tracked the packet nonce???");
                        }
                        final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                        if (chatSession1192 != null) {
                            wrapper.set(Type.BOOLEAN, 0, false); // Now it's a nonce
                            final long salt = chatSession1192.getSaltGenerator().nextLong();
                            final byte[] signedNonce = chatSession1192.getSigner().sign(updater -> {
                                if (updater != null) {
                                    updater.update(nonce);
                                    updater.update(Longs.toByteArray(salt));
                                }
                            });
                            wrapper.read(Type.BYTE_ARRAY_PRIMITIVE); // We don't this anymore

                            wrapper.write(Type.LONG, salt);
                            wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signedNonce);
                        }
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_19_1.PLAYER_CHAT, ClientboundPackets1_19_3.DISGUISED_CHAT, new PacketRemapper() {
            @Override
            public void registerMap() {
                read(Type.OPTIONAL_BYTE_ARRAY_PRIMITIVE); // Previous signature
                handler(wrapper -> {
                    final PlayerMessageSignature signature = wrapper.read(Type.PLAYER_MESSAGE_SIGNATURE);

                    // Store message signature for last seen
                    if (!signature.uuid().equals(ZERO_UUID) && signature.signatureBytes().length != 0) {
                        final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                        if (messagesStorage != null) {
                            messagesStorage.add(signature);
                            if (messagesStorage.tickUnacknowledged() > 64) {
                                messagesStorage.resetUnacknowledgedCount();

                                // Send chat acknowledgement
                                final PacketWrapper chatAckPacket = wrapper.create(ServerboundPackets1_19_1.CHAT_ACK);
                                chatAckPacket.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
                                wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, null);

                                chatAckPacket.sendToServer(Protocol1_19_3To1_19_1.class);
                            }
                        }
                    }

                    final String plainMessage = wrapper.read(Type.STRING);
                    JsonElement decoratedMessage = wrapper.read(Type.OPTIONAL_COMPONENT);

                    wrapper.read(Type.LONG); // Timestamp
                    wrapper.read(Type.LONG); // Salt
                    wrapper.read(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY); // Last seen

                    final JsonElement unsignedMessage = wrapper.read(Type.OPTIONAL_COMPONENT);
                    if (unsignedMessage != null) {
                        decoratedMessage = unsignedMessage;
                    }
                    if (decoratedMessage == null) {
                        decoratedMessage = GsonComponentSerializer.gson().serializeToTree(Component.text(plainMessage));
                    }

                    final int filterMaskType = wrapper.read(Type.VAR_INT);
                    if (filterMaskType == 2) { // Partially filtered
                        wrapper.read(Type.LONG_ARRAY_PRIMITIVE); // Mask
                    }

                    wrapper.write(Type.COMPONENT, decoratedMessage);
                    // Keep chat type at the end
                });
            }
        });

        registerServerbound(ServerboundPackets1_19_3.CHAT_COMMAND, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // Command
                map(Type.LONG); // Timestamp
                map(Type.LONG); // Salt
                map(Type.VAR_INT); // Signatures

                // Signature removing if we have a chat session
                handler(wrapper -> {
                    final int signatures = wrapper.get(Type.VAR_INT, 0);

                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    if (chatSession1192 != null) {
                        final CommandArgumentsProvider commandArgumentsProvider = Via.getManager().getProviders().get(CommandArgumentsProvider.class);
                        if (commandArgumentsProvider != null) {
                            for (int i = 0; i < signatures; i++) {
                                wrapper.read(Type.STRING); // Argument name
                                wrapper.read(MESSAGE_SIGNATURE_BYTES_TYPE); // Signature
                            }
                            return;
                        }
                    }

                    for (int i = 0; i < signatures; i++) {
                        wrapper.passthrough(Type.STRING); // Argument name

                        // Signature
                        wrapper.read(MESSAGE_SIGNATURE_BYTES_TYPE);
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, new byte[0]);
                    }
                });

                // Removing new acknowledgement
                handler(wrapper -> {
                    wrapper.read(Type.VAR_INT); // Offset
                    wrapper.read(ACKNOWLEDGED_BIT_SET_TYPE); // Acknowledged
                });

                // Signing all arguments
                handler(wrapper -> {
                    final UUID sender = wrapper.user().getProtocolInfo().getUuid();
                    final String command = wrapper.get(Type.STRING, 0);
                    final long timestamp = wrapper.get(Type.LONG, 0);
                    final long salt = wrapper.get(Type.LONG, 1);

                    if (sender == null) {
                        throw new IllegalStateException("ViaVersion didn't track the connected UUID correctly, please check your BaseProtocol1_7");
                    }

                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    if (chatSession1192 != null) {
                        final CommandArgumentsProvider commandArgumentsProvider = Via.getManager().getProviders().get(CommandArgumentsProvider.class);
                        if (commandArgumentsProvider != null) {
                            // Signing arguments
                            {
                                final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                                if (messagesStorage != null) {
                                    for (Pair<String, String> argument : commandArgumentsProvider.getSignedArguments(command)) {
                                        final byte[] signature = chatSession1192.sign(
                                                sender,
                                                new MessageMetadata1_19_all(
                                                        argument.component2(),
                                                        timestamp,
                                                        salt
                                                ),
                                                messagesStorage.lastSignatures()
                                        );


                                        wrapper.write(Type.STRING, argument.component1());
                                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                                    }
                                }
                            }
                        }
                    }
                    wrapper.write(Type.BOOLEAN, false); // No signed preview
                });

                // Adding old acknowledgement
                handler(wrapper -> {
                    final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                    if (messagesStorage != null) {
                        messagesStorage.resetUnacknowledgedCount();
                        wrapper.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
                        wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, null);
                    }
                });
            }
        });
        registerServerbound(ServerboundPackets1_19_3.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // Command
                map(Type.LONG); // Timestamp
                map(Type.LONG); // Salt
                handler(wrapper -> {
                    wrapper.read(OPTIONAL_MESSAGE_SIGNATURE_BYTES_TYPE); // Signature

                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    if (chatSession1192 == null) {
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, new byte[0]); // Signature
                        wrapper.write(Type.BOOLEAN, false); // No signed preview
                    }
                });

                // Emulate old Message chain
                handler(wrapper -> {
                    final UUID sender = wrapper.user().getProtocolInfo().getUuid();
                    final String message = wrapper.get(Type.STRING, 0);
                    final long timestamp = wrapper.get(Type.LONG, 0);
                    final long salt = wrapper.get(Type.LONG, 1);

                    if (sender == null) {
                        throw new IllegalStateException("ViaVersion didn't track the connected UUID correctly, please check your BaseProtocol1_7");
                    }

                    final ChatSession1_19_2 chatSession1192 = wrapper.user().get(ChatSession1_19_2.class);
                    if (chatSession1192 != null) {
                        final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                        if (messagesStorage != null) {
                            final byte[] signature = chatSession1192.sign(
                                    sender,
                                    new MessageMetadata1_19_all(
                                            message,
                                            timestamp,
                                            salt
                                    ),
                                    messagesStorage.lastSignatures()
                            );

                            wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                            wrapper.write(Type.BOOLEAN, false); // Signed Preview - not implemented yet, but i could do it
                        }
                    }
                });

                // Removing new acknowledgement
                handler(wrapper -> {
                    wrapper.read(Type.VAR_INT); // Offset
                    wrapper.read(ACKNOWLEDGED_BIT_SET_TYPE); // Acknowledged
                });

                // Adding old acknowledgement
                handler(wrapper -> {
                    final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                    if (messagesStorage != null) {
                        messagesStorage.resetUnacknowledgedCount();
                        wrapper.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
                        wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, null);
                    }
                });
            }
        });
    }
}

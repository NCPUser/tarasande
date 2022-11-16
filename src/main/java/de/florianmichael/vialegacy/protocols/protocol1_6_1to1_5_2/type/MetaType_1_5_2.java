package de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.type;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.type.TypeRegistry1_7_6_10;

public enum MetaType_1_5_2 implements MetaType {

	Byte(0, Type.BYTE),
	Short(1, Type.SHORT),
	Int(2, Type.INT),
	Float(3, Type.FLOAT),
	String(4, Type.STRING),
	Slot(5, TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM),
	Position(6, Type.VECTOR),
	NonExistent(-1, Type.NOTHING);

	private final int typeID;
	private final Type type;

	public static MetaType_1_5_2 byId(int id) {
		return values()[id];
	}

	MetaType_1_5_2(int typeID, Type type) {
		this.typeID = typeID;
		this.type = type;
	}

	public int typeId() {
		return this.typeID;
	}

	public Type type() {
		return this.type;
	}
}

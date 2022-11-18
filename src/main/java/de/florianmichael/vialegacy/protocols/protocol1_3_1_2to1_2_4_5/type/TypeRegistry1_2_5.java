/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.type;

import de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.type.impl.ItemType1_2_5;
import de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.type.impl.MetadataListType1_2_5;

public class TypeRegistry1_2_5 {
	
	public static final ItemType1_2_5 COMPRESSED_NBT_ITEM = new ItemType1_2_5(true);
	public static final MetadataListType1_2_5 METADATA_LIST = new MetadataListType1_2_5();
}

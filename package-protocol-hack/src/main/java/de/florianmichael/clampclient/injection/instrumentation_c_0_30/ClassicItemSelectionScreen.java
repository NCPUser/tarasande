package de.florianmichael.clampclient.injection.instrumentation_c_0_30;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
public class ClassicItemSelectionScreen extends Screen {

    public static ClassicItemSelectionScreen INSTANCE = new ClassicItemSelectionScreen();

    private final static int MAX_ROW_DIVIDER = 9;
    private final static int ITEM_XY_BOX_DIMENSION_CLASSIC = 25;
    private final static int SIDE_OFFSET = 15;
    private final static int ITEM_XY_BOX_DIMENSION_MODERN = 16;

    public Item[][] itemGrid = null;
    public ItemStack selectedItem = null;

    public ClassicItemSelectionScreen() {
        super(Text.literal("Classic item selection"));
    }

    public void reload(final VersionListEnum version) {
        final List<Item> allowedItems = new ArrayList<>();
        allowedItems.add(Items.OAK_WOOD);
        allowedItems.add(Items.OAK_PLANKS);
        allowedItems.add(Items.STONE);
        allowedItems.add(Items.COBBLESTONE);
        allowedItems.add(Items.MOSSY_COBBLESTONE);
        allowedItems.add(Items.BRICKS);
        allowedItems.add(Items.IRON_BLOCK);
        allowedItems.add(Items.GOLD_BLOCK);
        allowedItems.add(Items.GLASS);
        allowedItems.add(Items.DIRT);
        allowedItems.add(Items.GRAVEL);
        allowedItems.add(Items.SAND);
        allowedItems.add(Items.OBSIDIAN);
        allowedItems.add(Items.COAL_ORE);
        allowedItems.add(Items.IRON_ORE);
        allowedItems.add(Items.GOLD_ORE);
        allowedItems.add(Items.OAK_LEAVES);
        allowedItems.add(Items.OAK_SAPLING);
        allowedItems.add(Items.BOOKSHELF);
        allowedItems.add(Items.TNT);
        if (version.isNewerThan(VersionListEnum.c0_0_19a_06)) {
            allowedItems.add(Items.SPONGE);
            if (version.isNewerThan(VersionListEnum.c0_0_20ac0_27)) {
                allowedItems.add(Items.WHITE_WOOL);
                allowedItems.add(Items.ORANGE_WOOL);
                allowedItems.add(Items.MAGENTA_WOOL);
                allowedItems.add(Items.LIGHT_BLUE_WOOL);
                allowedItems.add(Items.YELLOW_WOOL);
                allowedItems.add(Items.LIME_WOOL);
                allowedItems.add(Items.PINK_WOOL);
                allowedItems.add(Items.CYAN_WOOL);
                allowedItems.add(Items.BLUE_WOOL);
                allowedItems.add(Items.BROWN_WOOL);
                allowedItems.add(Items.GREEN_WOOL);
                allowedItems.add(Items.BROWN_MUSHROOM);
                allowedItems.add(Items.GRAY_WOOL);
                allowedItems.add(Items.LIGHT_GRAY_WOOL);
                allowedItems.add(Items.PURPLE_WOOL);
                allowedItems.add(Items.RED_WOOL);
                allowedItems.add(Items.BLACK_WOOL);
                allowedItems.add(Items.SMOOTH_STONE_SLAB);
                allowedItems.add(Items.POPPY);
                allowedItems.add(Items.DANDELION);
                allowedItems.add(Items.RED_MUSHROOM);
                if (version == VersionListEnum.c0_30cpe) {
                    allowedItems.add(Items.MAGMA_BLOCK);
                    allowedItems.add(Items.QUARTZ_PILLAR);
                    allowedItems.add(Items.SANDSTONE_STAIRS);
                    allowedItems.add(Items.STONE_BRICKS);
                    allowedItems.add(Items.COBBLESTONE_SLAB);
                    allowedItems.add(Items.ICE);
                    allowedItems.add(Items.SNOW);
                }
            }
        }

        itemGrid = new Item[MathHelper.ceil(allowedItems.size() / (double) MAX_ROW_DIVIDER)][MAX_ROW_DIVIDER];
        int x = 0;
        int y = 0;
        for (Item allowedItem : allowedItems) {
            itemGrid[y][x] = allowedItem;
            x++;
            if (x == MAX_ROW_DIVIDER) {
                x = 0;
                y++;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedItem != null) {
            this.client.interactionManager.clickCreativeStack(selectedItem, MinecraftClient.getInstance().player.getInventory().selectedSlot + 36); // Beta Inventory Tracker

            this.client.player.getInventory().main.set(MinecraftClient.getInstance().player.getInventory().selectedSlot, selectedItem);
            this.client.player.playerScreenHandler.sendContentUpdates();

            this.close();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == this.client.options.inventoryKey.boundKey.getCode()) {
            this.close();
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        final int halfWidth = this.width / 2;
        final int halfHeight = this.height / 2;

        final int boxWidth = ITEM_XY_BOX_DIMENSION_CLASSIC * MAX_ROW_DIVIDER + SIDE_OFFSET * 2;
        final int boxHeight = ITEM_XY_BOX_DIMENSION_CLASSIC * itemGrid.length + SIDE_OFFSET * 2 + SIDE_OFFSET;

        final int renderX = halfWidth - boxWidth / 2;
        final int renderY = halfHeight - boxHeight / 2;

        fill(matrices, renderX, renderY, renderX + boxWidth, renderY + boxHeight, Integer.MIN_VALUE);
        drawCenteredText(matrices, textRenderer, "Select block", renderX + boxWidth / 2, renderY + SIDE_OFFSET, -1);
        selectedItem = null;

        int y = SIDE_OFFSET + SIDE_OFFSET;
        for (Item[] items : itemGrid) {
            int x = SIDE_OFFSET;
            for (Item item : items) {
                if (item == null) continue;

                if (mouseX > renderX + x && mouseY > renderY + y && mouseX < renderX + x + ITEM_XY_BOX_DIMENSION_CLASSIC && mouseY < renderY + y + ITEM_XY_BOX_DIMENSION_CLASSIC) {
                    fill(matrices, renderX + x, renderY + y, renderX + x + ITEM_XY_BOX_DIMENSION_CLASSIC, renderY + y + ITEM_XY_BOX_DIMENSION_CLASSIC, Integer.MAX_VALUE);
                    selectedItem = item.getDefaultStack();
                }
                MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(item.getDefaultStack(), renderX + x + ITEM_XY_BOX_DIMENSION_MODERN / 4, renderY + y + ITEM_XY_BOX_DIMENSION_MODERN / 4);
                x += ITEM_XY_BOX_DIMENSION_CLASSIC;
            }
            y += ITEM_XY_BOX_DIMENSION_CLASSIC;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}

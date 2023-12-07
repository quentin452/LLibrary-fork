package net.ilexiconn.llibrary.client;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.event.PlayerModelEvent;
import net.ilexiconn.llibrary.client.gui.SnackbarGUI;
import net.ilexiconn.llibrary.client.gui.survivaltab.PageButtonGUI;
import net.ilexiconn.llibrary.client.gui.survivaltab.SurvivalTab;
import net.ilexiconn.llibrary.client.gui.survivaltab.SurvivalTabGUI;
import net.ilexiconn.llibrary.client.gui.survivaltab.SurvivalTabHandler;
import net.ilexiconn.llibrary.client.gui.update.ModUpdateGUI;
import net.ilexiconn.llibrary.client.model.VoxelModel;
import net.ilexiconn.llibrary.client.util.ClientUtils;
import net.ilexiconn.llibrary.server.config.ConfigHandler;
import net.ilexiconn.llibrary.server.event.SurvivalTabClickEvent;
import net.ilexiconn.llibrary.server.snackbar.Snackbar;
import net.ilexiconn.llibrary.server.snackbar.SnackbarHandler;
import net.ilexiconn.llibrary.server.update.UpdateHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

@SideOnly(Side.CLIENT)
public enum ClientEventHandler {
    INSTANCE;

    private SnackbarGUI snackbarGUI;
    private boolean checkedForUpdates;
    private ModelBase voxelModel = new VoxelModel();

    public void setOpenSnackbar(SnackbarGUI snackbarGUI) {
        this.snackbarGUI = snackbarGUI;
    }

    @SubscribeEvent
    public void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiMainMenu) {
            int offsetX = 0;
            int offsetY = 0;
            int buttonX = event.gui.width / 2 - 124 + offsetX;
            int buttonY = event.gui.height / 4 + 48 + 24 * 2 + offsetY;
            while (true) {
                if (buttonX < 0) {
                    if (offsetY <= -48) {
                        buttonX = 0;
                        buttonY = 0;
                        break;
                    } else {
                        offsetX = 0;
                        offsetY -= 24;
                        buttonX = event.gui.width / 2 - 124 + offsetX;
                        buttonY = event.gui.height / 4 + 48 + 24 * 2 + offsetY;
                    }
                }

                Rectangle rectangle = new Rectangle(buttonX, buttonY, 20, 20);
                boolean intersects = false;
                for (int i = 0; i < event.buttonList.size(); i++) {
                    GuiButton button = (GuiButton) event.buttonList.get(i);
                    if (!intersects) {
                        intersects = rectangle.intersects(new Rectangle(button.xPosition, button.yPosition, button.width, button.height));
                    }
                }

                if (!intersects) {
                    break;
                }

                buttonX -= 24;
            }

            if (LLibrary.CONFIG.hasVersionCheck()) {
                event.buttonList.add(new GuiButton(ClientProxy.UPDATE_BUTTON_ID, buttonX, buttonY, 20, 20, "U"));
            }

            if (!this.checkedForUpdates && !UpdateHandler.INSTANCE.getOutdatedModList().isEmpty()) {
                this.checkedForUpdates = true;
                SnackbarHandler.INSTANCE.showSnackbar(Snackbar.create(StatCollector.translateToLocal("snackbar.llibrary.updates_found")));
            }
        } else if (event.gui instanceof GuiContainer && (LLibrary.CONFIG.areTabsAlwaysVisible() || SurvivalTabHandler.INSTANCE.getSurvivalTabList().size() > 1)) {
            GuiContainer container = (GuiContainer) event.gui;
            boolean flag = false;
            for (SurvivalTab survivalTab : SurvivalTabHandler.INSTANCE.getSurvivalTabList()) {
                if (survivalTab.getContainer() != null && survivalTab.getContainer().isInstance(event.gui)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                int count = 2;
                for (SurvivalTab tab : SurvivalTabHandler.INSTANCE.getSurvivalTabList()) {
                    if (tab.getPage() == SurvivalTabHandler.INSTANCE.getCurrentPage()) {
                        event.buttonList.add(new SurvivalTabGUI(count, tab));
                    }
                    count++;
                }
                if (count > 7) {
                    int offsetY = (container.ySize - 136) / 2 - 10;
                    if (LLibrary.CONFIG.areTabsLeftSide()) {
                        event.buttonList.add(new PageButtonGUI(-1, container.guiLeft - 82, container.guiTop + 136 + offsetY, container));
                        event.buttonList.add(new PageButtonGUI(-2, container.guiLeft - 22, container.guiTop + 136 + offsetY, container));
                    } else {
                        event.buttonList.add(new PageButtonGUI(-1, container.guiLeft + container.xSize + 2, container.guiTop + 136 + offsetY, container));
                        event.buttonList.add(new PageButtonGUI(-2, container.guiLeft + container.xSize + 62, container.guiTop + 136 + offsetY, container));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (ClientProxy.MINECRAFT.gameSettings.advancedItemTooltips) {
            event.toolTip.add(EnumChatFormatting.DARK_GRAY + "" + Item.itemRegistry.getNameForObject(event.itemStack.getItem()));
        }
    }

    @SubscribeEvent
    public void onButtonPressPre(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.gui instanceof GuiMainMenu && event.button.id == ClientProxy.UPDATE_BUTTON_ID) {
            ClientProxy.MINECRAFT.displayGuiScreen(new ModUpdateGUI((GuiMainMenu) event.gui));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onClientUpdate(TickEvent.ClientTickEvent event) {
        if (this.snackbarGUI == null && !ClientProxy.SNACKBAR_LIST.isEmpty()) {
            this.setOpenSnackbar(ClientProxy.SNACKBAR_LIST.get(0));
            ClientProxy.SNACKBAR_LIST.remove(this.snackbarGUI);
        }
        if (this.snackbarGUI != null) {
            this.snackbarGUI.updateSnackbar();
        }
    }

    @SubscribeEvent
    public void onRenderUpdate(TickEvent.RenderTickEvent event) {
        ClientUtils.updateLast();
    }

    @SubscribeEvent
    public void onRenderOverlayPost(RenderGameOverlayEvent.Post event) {
        if (ClientProxy.MINECRAFT.currentScreen == null && this.snackbarGUI != null && event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
            this.snackbarGUI.drawSnackbar();
        }
    }

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (this.snackbarGUI != null) {
            this.snackbarGUI.drawSnackbar();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        ConfigHandler.INSTANCE.saveConfigForID(event.modID);
    }

    @SubscribeEvent
    public void onSurvivalTabClick(SurvivalTabClickEvent event) {
        if (event.getLabel().equals("container.inventory")) {
            ClientProxy.MINECRAFT.displayGuiScreen(new GuiInventory(ClientProxy.MINECRAFT.thePlayer));
        }
    }

    private void renderVoxel(PlayerModelEvent.Render event, float scale, float color) {
        float bob = MathHelper.sin(((float) event.getEntityPlayer().ticksExisted + LLibrary.PROXY.getPartialTicks()) / 15.0F) * 0.1F;
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glRotatef(-ClientUtils.interpolate(event.getEntityPlayer().prevRenderYawOffset, event.getEntityPlayer().renderYawOffset, LLibrary.PROXY.getPartialTicks()), 0, 1.0F, 0);
        GL11.glColor4f(color, color, color, 1.0F);
        GL11.glTranslatef(0.0F, -1.0F + bob, 0.0F);
        GL11.glRotatef(ClientUtils.interpolate((event.getEntityPlayer().ticksExisted - 1) % 360, event.getEntityPlayer().ticksExisted % 360, LLibrary.PROXY.getPartialTicks()), 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.75F, 0.0F, 0.0F);
        GL11.glRotatef(ClientUtils.interpolate((event.getEntityPlayer().ticksExisted - 1) % 360, event.getEntityPlayer().ticksExisted % 360, LLibrary.PROXY.getPartialTicks()), 0.0F, 1.0F, 0.0F);
        GL11.glScalef(scale, scale, scale);
        this.voxelModel.render(event.getEntityPlayer(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getRotation(), event.getRotationYaw(), event.getRotationPitch(), event.getScale());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }
}

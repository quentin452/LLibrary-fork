package net.ilexiconn.llibrary.server;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.capability.EntityDataCapabilityImplementation;
import net.ilexiconn.llibrary.server.capability.EntityDataCapabilityStorage;
import net.ilexiconn.llibrary.server.capability.IEntityDataCapability;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.ilexiconn.llibrary.server.network.AnimationMessage;
import net.ilexiconn.llibrary.server.network.PropertiesMessage;
import net.ilexiconn.llibrary.server.network.SnackbarMessage;
import net.ilexiconn.llibrary.server.snackbar.Snackbar;
import net.ilexiconn.llibrary.server.update.UpdateHandler;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.server.FMLServerHandler;

public class ServerProxy {
    public void onPreInit() {
        MinecraftForge.EVENT_BUS.register(ServerEventHandler.INSTANCE);
        CapabilityManager.INSTANCE.register(IEntityDataCapability.class, new EntityDataCapabilityStorage(), EntityDataCapabilityImplementation.class);

        LLibrary.NETWORK_WRAPPER.registerMessage(AnimationMessage.class, AnimationMessage.class, 0, Side.CLIENT);
        LLibrary.NETWORK_WRAPPER.registerMessage(SnackbarMessage.class, SnackbarMessage.class, 1, Side.CLIENT);
        LLibrary.NETWORK_WRAPPER.registerMessage(PropertiesMessage.class, PropertiesMessage.class, 2, Side.CLIENT);
    }

    public void onInit() {
        UpdateHandler.INSTANCE.registerUpdateChecker(LLibrary.INSTANCE, "http://pastebin.com/raw/qh3Pnuuk");
    }

    public void onPostInit() {
        UpdateHandler.INSTANCE.searchForUpdates();
    }

    public <T extends AbstractMessage<T>> void handleMessage(final T message, final MessageContext messageContext) {
        WorldServer world = (WorldServer) messageContext.getServerHandler().playerEntity.worldObj;
        world.addScheduledTask(() -> message.onServerReceived(FMLServerHandler.instance().getServer(), message, messageContext.getServerHandler().playerEntity, messageContext));
    }

    public float getPartialTicks() {
        return 0.0F;
    }

    public void showSnackbar(Snackbar snackbar) {
        LLibrary.NETWORK_WRAPPER.sendToAll(new SnackbarMessage(snackbar));
    }
}

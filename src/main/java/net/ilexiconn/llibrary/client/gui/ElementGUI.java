package net.ilexiconn.llibrary.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.element.Element;
import net.ilexiconn.llibrary.client.gui.element.ElementHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic GUI that supports elements. You don't have to extend this class to use the element API, it just makes it easier.
 *
 * @author iLexiconn
 * @since 1.4.0
 */
@SideOnly(Side.CLIENT)
public abstract class ElementGUI extends GuiScreen {
    protected List<Element> elementList = new ArrayList<>();

    private boolean requiresInit;

    public abstract void initElements();

    @Override
    public void initGui() {
        this.requiresInit = true;
        ElementHandler.INSTANCE.clearElements(this);
        this.elementList.clear();
        this.initElements();
        for (Element element : this.elementList) {
            ElementHandler.INSTANCE.addElement(this, element);
        }
        ElementHandler.INSTANCE.onInit(this);
        this.requiresInit = false;
    }

    @Override
    public void updateScreen() {
        ElementHandler.INSTANCE.onUpdate(this);
    }

    public abstract void drawScreen(float mouseX, float mouseY, float partialTicks);

    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, this.width, this.height, LLibrary.CONFIG.getTertiaryColor());
        this.drawScreen(ElementHandler.INSTANCE.getPreciseMouseX(this), ElementHandler.INSTANCE.getPreciseMouseY(this), partialTicks);
        ElementHandler.INSTANCE.onRender(this, partialTicks);
        int scrollAmount = Mouse.getDWheel();
        if (scrollAmount != 0) {
            ElementHandler.INSTANCE.onMouseScrolled(this, scrollAmount);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        ElementHandler.INSTANCE.onMouseClicked(this, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        ElementHandler.INSTANCE.onMouseDragged(this, clickedMouseButton, timeSinceLastClick);
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
        ElementHandler.INSTANCE.onMouseReleased(this, state);
        super.mouseMovedOrUp(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        ElementHandler.INSTANCE.onKeyPressed(this, typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        ElementHandler.INSTANCE.clearElements(this);
    }

    public boolean doesRequireInit() {
        return this.requiresInit;
    }
}

package com.github.wolfshotz.wyrmroost.client.screen.widgets;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import com.github.wolfshotz.wyrmroost.network.packets.RenameEntityPacket;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;

public class NameFieldWidget extends TextFieldWidget
{
    public NameFieldWidget(FontRenderer font, int posX, int posY, int sizeX, int sizeY, AbstractDragonEntity dragon)
    {
        super(font, posX, posY, sizeX, sizeY, dragon.getName());

        setText(getMessage().getString());
        setCanLoseFocus(true);
        changeFocus(true);
        setEnableBackgroundDrawing(false);
        setMaxStringLength(35);
        setResponder(s ->
        {
            if (s.equals(dragon.getName().getUnformattedComponentText())) return;
            StringTextComponent name = s.isEmpty()? null : new StringTextComponent(s);
            Wyrmroost.NETWORK.sendToServer(new RenameEntityPacket(dragon, name));
        });
    }
}

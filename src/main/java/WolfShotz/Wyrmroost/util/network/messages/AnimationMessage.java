package WolfShotz.Wyrmroost.util.network.messages;

import WolfShotz.Wyrmroost.util.ModUtils;
import WolfShotz.Wyrmroost.util.network.IMessage;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AnimationMessage implements IMessage
{
    private int entityID;
    private int animationIndex;
    
    public AnimationMessage(int entityID, int index) {
        this.entityID = entityID;
        this.animationIndex = index;
    }
    
    public AnimationMessage(PacketBuffer buf) {
        entityID = buf.readInt();
        animationIndex = buf.readInt();
    }
    
    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(entityID);
        buf.writeInt(animationIndex);
    }
    
    @Override
    public void run(Supplier<NetworkEvent.Context> context) {
        World world = DistExecutor.callWhenOn(Dist.CLIENT, () -> ModUtils::getClientWorld);
        IAnimatedEntity entity = (IAnimatedEntity) world.getEntityByID(entityID);
        
        if (animationIndex < 0) entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
        else entity.setAnimation(entity.getAnimations()[animationIndex]);
    }
}
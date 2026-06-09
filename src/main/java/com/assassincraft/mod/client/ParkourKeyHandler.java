package com.assassincraft.mod.client;

import com.assassincraft.mod.capability.ParkourCapability;
import com.assassincraft.mod.capability.ParkourCapability.IParkourData;
import com.assassincraft.mod.common.PacketHandler;
import com.assassincraft.mod.common.ParkourState;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = com.assassincraft.mod.AssassinCraft.MOD_ID)
public class ParkourKeyHandler {

    public static final KeyMapping GRAB_KEY = new KeyMapping(
            "key.assassincraft.grab",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            "key.categories.assassincraft"
    );

    public static final KeyMapping BACK_EJECT_KEY = new KeyMapping(
            "key.assassincraft.back_eject",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_CONTROL,
            "key.categories.assassincraft"
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(GRAB_KEY);
        event.register(BACK_EJECT_KEY);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        ParkourCapability.get(player).ifPresent(cap -> {
            ParkourState state = cap.getState();
            boolean inGrabState = state == ParkourState.HANGING
                    || state == ParkourState.LEDGE_GRAB
                    || state == ParkourState.LEDGE_SHIMMY
                    || state == ParkourState.CLIMBING;

            if (!inGrabState) return;

            if (mc.options.keyJump.consumeClick()) {
                int ejectType = resolveEjectType(player, cap);
                PacketHandler.CHANNEL.sendToServer(new PacketHandler.C2S_EjectRequest(ejectType));
            }
        });
    }

    private static int resolveEjectType(LocalPlayer player, IParkourData cap) {
        if (BACK_EJECT_KEY.isDown()) return 0;

        float strafe = player.input.leftImpulse; 
        if (strafe > 0) return 2; 
        if (strafe < 0) return 1; 

        return 3; 
    }
}

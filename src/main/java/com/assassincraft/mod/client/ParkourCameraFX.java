package com.assassincraft.mod.client;

import com.assassincraft.mod.capability.ParkourCapability;
import com.assassincraft.mod.common.ParkourState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = com.assassincraft.mod.AssassinCraft.MOD_ID)
public class ParkourCameraFX {

    private static float smoothRoll     = 0f;
    private static float smoothFovDelta = 0f;

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        float[] targetRoll = {0f};
        ParkourCapability.get(player).ifPresent(cap -> {
            ParkourState state = cap.getState();
            targetRoll[0] = switch (state) {
                case BACK_EJECT   ->  0f;
                case SIDE_EJECT   ->  cap.getSideEjectDir() * -8f;
                case LEDGE_SHIMMY ->  player.input.leftImpulse * 5f;
                case VAULTING     -> -4f;
                case CLIMBING     ->  2f;
                case HANGING,
                     LEDGE_GRAB  ->  1f;
                default          ->  0f;
            };
        });

        smoothRoll = smoothRoll + (targetRoll[0] - smoothRoll) * 0.15f;
        event.setRoll(smoothRoll);
    }

    @SubscribeEvent
    public static void onComputeFOV(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        float[] fovDelta = {0f};
        ParkourCapability.get(player).ifPresent(cap -> {
            ParkourState state = cap.getState();
            fovDelta[0] = switch (state) {
                case BACK_EJECT, SIDE_EJECT -> 5f;
                case VAULTING               -> 3f;
                default                     -> 0f;
            };
        });

        smoothFovDelta = smoothFovDelta + (fovDelta[0] - smoothFovDelta) * 0.15f;
        event.setFov(event.getFov() + smoothFovDelta);
    }
        }
                  

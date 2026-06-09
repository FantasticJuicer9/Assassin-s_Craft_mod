package com.assassincraft.mod.client;

import com.assassincraft.mod.capability.ParkourCapability;
import com.assassincraft.mod.common.ParkourState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = com.assassincraft.mod.AssassinCraft.MOD_ID)
public class ParkourHUD {

    private static final int COLOUR_GRAB   = 0xFFE8A030;  
    private static final int COLOUR_EJECT  = 0xFFFFA0A0;  
    private static final int COLOUR_VAULT  = 0xFF80E8FF;  
    private static final int COLOUR_NORMAL = 0xFFAAAAAA;  

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;

        ParkourCapability.get(mc.player).ifPresent(cap -> {
            ParkourState state = cap.getState();
            if (state == ParkourState.NORMAL) return;

            Font font = mc.font;
            int width = mc.getWindow().getGuiScaledWidth();
            
            String label = buildLabel(state);
            String hint = buildHint(state);
            int color = colourFor(state);

            font.drawShadow(event.getPoseStack(), label, width - font.width(label) - 10, 10, color);
            if (!hint.isEmpty()) {
                font.drawShadow(event.getPoseStack(), hint, width - font.width(hint) - 10, 22, COLOUR_NORMAL);
            }
        });
    }

    private static String buildLabel(ParkourState state) {
        return switch (state) {
            case CLIMBING     -> "\u2B06 CLIMBING";
            case HANGING      -> "\u29D6 HANGING";
            case LEDGE_GRAB   -> "\u2299 LEDGE GRAB";
            case LEDGE_SHIMMY -> "\u25C1\u25B7 SHIMMY";
            case LEDGE_CLIMB  -> "\u2191 PULLING UP";
            case VAULTING     -> "\u21B7 VAULT";
            case BACK_EJECT   -> "\u21A9 BACK EJECT";
            case SIDE_EJECT   -> "\u21AA SIDE EJECT";
            case LANDING      -> "\u2199 ROLL";
            default           -> state.name();
        };
    }

    private static String buildHint(ParkourState state) {
        return switch (state) {
            case CLIMBING         -> "[GRAB] hold  [W/S] up/down  [JUMP] eject";
            case HANGING,
                 LEDGE_GRAB      -> "[JUMP] pull-up  [CTRL+JUMP] back eject  [A/D+JUMP] side  [release GRAB] drop";
            case LEDGE_SHIMMY    -> "[CTRL+JUMP] back eject  [JUMP] side eject";
            default              -> "";
        };
    }

    private static int colourFor(ParkourState state) {
        return switch (state) {
            case CLIMBING, HANGING, LEDGE_GRAB, LEDGE_SHIMMY -> COLOUR_GRAB;
            case BACK_EJECT, SIDE_EJECT                      -> COLOUR_EJECT;
            case VAULTING, LEDGE_CLIMB                       -> COLOUR_VAULT;
            default                                          -> COLOUR_NORMAL;
        };
    }
                                                   }
                            

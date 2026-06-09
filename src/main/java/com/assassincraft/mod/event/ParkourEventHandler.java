package com.assassincraft.mod.event;

import com.assassincraft.mod.capability.ParkourCapability;
import com.assassincraft.mod.capability.ParkourCapability.IParkourData;
import com.assassincraft.mod.common.PacketHandler;
import com.assassincraft.mod.common.ParkourState;
import com.assassincraft.mod.util.ParkourUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ParkourEventHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Player player = event.player;

        if (player.level.isClientSide()) return;
        ServerPlayer sPlayer = (ServerPlayer) player;

        ParkourCapability.get(sPlayer).ifPresent(cap -> {
            ParkourState oldState = cap.getState();
            ParkourState newState = computeNextState(sPlayer, cap);

            if (oldState != newState) {
                cap.setState(newState);
                PacketHandler.CHANNEL.sendTo(new PacketHandler.S2C_SyncState(sPlayer.getId(), newState.ordinal()), sPlayer.connection.connection, net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
            }

            applyStatePhysics(sPlayer, cap);
        });
    }

    private ParkourState computeNextState(ServerPlayer player, IParkourData cap) {
        return ParkourState.NORMAL;
    }

    private void applyStatePhysics(ServerPlayer player, IParkourData cap) {
        suppressGravity(player, 0.08);
    }

    private void suppressGravity(ServerPlayer player, double maxFallSpeed) {
        if (player.getDeltaMovement().y < -maxFallSpeed) {
            player.setDeltaMovement(player.getDeltaMovement().x, -maxFallSpeed, player.getDeltaMovement().z);
        }
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ParkourCapability.get(player).ifPresent(cap -> {
            ParkourState state = cap.getState();
            if (state == ParkourState.CLIMBING || state == ParkourState.HANGING || state == ParkourState.LEDGE_GRAB || state == ParkourState.LEDGE_SHIMMY || state == ParkourState.VAULTING) {
                event.setCanceled(true);
                return;
            }
            if (state == ParkourState.LANDING) {
                event.setDistance(event.getDistance() * 0.5f);
            }
        });
    }
        }
                  

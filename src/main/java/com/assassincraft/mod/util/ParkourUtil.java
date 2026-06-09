package com.assassincraft.mod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ParkourUtil {

    public static boolean isLedgeAt(Level level, BlockPos pos, Direction facing) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;

        BlockState aboveState = level.getBlockState(pos.above());
        if (!aboveState.isAir()) return false;

        VoxelShape shape = state.getCollisionShape(level, pos);
        if (shape.isEmpty()) return false;

        AABB bounds = shape.bounds();
        return bounds.maxY >= 0.85; 
    }

    public static boolean canVault(Player player, BlockPos pos, Direction facing) {
        Level level = player.level;
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;

        BlockState headState = level.getBlockState(pos.above());
        BlockState vaultState = level.getBlockState(pos.relative(facing));
        BlockState vaultHeadState = level.getBlockState(pos.relative(facing).above());

        return headState.isAir() && vaultState.isAir() && vaultHeadState.isAir();
    }
  }
          

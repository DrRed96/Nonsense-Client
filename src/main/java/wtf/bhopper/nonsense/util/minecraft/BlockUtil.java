package wtf.bhopper.nonsense.util.minecraft;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;

public class BlockUtil implements MinecraftInstance {

    public static Block getBlock(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

    public static boolean isAir(BlockPos blockPos) {
        return getBlock(blockPos).getMaterial() == Material.air;
    }

    public static Block getBlockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + offsetX, mc.thePlayer.posY + offsetY, mc.thePlayer.posZ + offsetZ)).getBlock();
    }

    public static Block getRelativeBlock(double x, double y, double z) {
        return getBlock(new BlockPos(mc.thePlayer).add(x, y, z));
    }

    public static boolean isSolid(BlockPos blockPos) {
        return getBlock(blockPos)
                .getMaterial()
                .blocksMovement();
    }

    public static BlockPos randomPos() {
        return new BlockPos(
                ThreadLocalRandom.current().nextInt(-30000000, 30000000),
                ThreadLocalRandom.current().nextInt(-30000000, 30000000),
                ThreadLocalRandom.current().nextInt(-30000000, 30000000)
        );
    }

}

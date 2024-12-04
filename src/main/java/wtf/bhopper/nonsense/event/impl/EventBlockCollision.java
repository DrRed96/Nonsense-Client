package wtf.bhopper.nonsense.event.impl;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import wtf.bhopper.nonsense.event.Cancellable;

public class EventBlockCollision extends Cancellable {

    public final BlockPos pos;
    public final IBlockState state;

    public EventBlockCollision(BlockPos pos, IBlockState state) {
        this.pos = pos;
        this.state = state;
    }

}

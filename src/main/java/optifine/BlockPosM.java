package optifine;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class BlockPosM extends BlockPos {
    private int mx;
    private int my;
    private int mz;
    private int level;
    private BlockPosM[] facings;
    private boolean needsUpdate;

    public BlockPosM(int mx, int my, int mz) {
        this(mx, my, mz, 0);
    }

    public BlockPosM(double mx, double my, double mz) {
        this(MathHelper.floor_double(mx), MathHelper.floor_double(my), MathHelper.floor_double(mz));
    }

    public BlockPosM(int mx, int my, int mz, int level) {
        super(0, 0, 0);
        this.mx = mx;
        this.my = my;
        this.mz = mz;
        this.level = level;
    }

    /**
     * Get the X coordinate
     */
    public int getX() {
        return this.mx;
    }

    /**
     * Get the Y coordinate
     */
    public int getY() {
        return this.my;
    }

    /**
     * Get the Z coordinate
     */
    public int getZ() {
        return this.mz;
    }

    public void setXyz(int mx, int my, int mz) {
        this.mx = mx;
        this.my = my;
        this.mz = mz;
        this.needsUpdate = true;
    }

    public void setXyz(double mx, double my, double mz) {
        this.setXyz(MathHelper.floor_double(mx), MathHelper.floor_double(my), MathHelper.floor_double(mz));
    }

    /**
     * Offset this BlockPos 1 block in the given direction
     */
    public BlockPos offset(EnumFacing facing) {
        if (this.level <= 0) {
            return super.offset(facing, 1);
        } else {
            if (this.facings == null) {
                this.facings = new BlockPosM[EnumFacing.VALUES.length];
            }

            if (this.needsUpdate) {
                this.update();
            }

            int i = facing.getIndex();
            BlockPosM blockposm = this.facings[i];

            if (blockposm == null) {
                int j = this.mx + facing.getFrontOffsetX();
                int k = this.my + facing.getFrontOffsetY();
                int l = this.mz + facing.getFrontOffsetZ();
                blockposm = new BlockPosM(j, k, l, this.level - 1);
                this.facings[i] = blockposm;
            }

            return blockposm;
        }
    }

    /**
     * Offsets this BlockPos n blocks in the given direction
     */
    public BlockPos offset(EnumFacing facing, int n) {
        return n == 1 ? this.offset(facing) : super.offset(facing, n);
    }

    private void update() {
        for (int i = 0; i < 6; ++i) {
            BlockPosM blockposm = this.facings[i];

            if (blockposm != null) {
                EnumFacing enumfacing = EnumFacing.VALUES[i];
                int j = this.mx + enumfacing.getFrontOffsetX();
                int k = this.my + enumfacing.getFrontOffsetY();
                int l = this.mz + enumfacing.getFrontOffsetZ();
                blockposm.setXyz(j, k, l);
            }
        }

        this.needsUpdate = false;
    }

    public static Iterable getAllInBoxMutable(BlockPos start, BlockPos end) {
        final BlockPos blockpos = new BlockPos(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()));
        final BlockPos blockpos1 = new BlockPos(Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
        return new Iterable() {
            public Iterator iterator() {
                return new AbstractIterator() {
                    private BlockPosM theBlockPosM = null;

                    protected BlockPosM computeNext0() {
                        if (this.theBlockPosM == null) {
                            this.theBlockPosM = new BlockPosM(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 3);
                            return this.theBlockPosM;
                        } else if (this.theBlockPosM.equals(blockpos1)) {
                            return (BlockPosM) this.endOfData();
                        } else {
                            int i = this.theBlockPosM.getX();
                            int j = this.theBlockPosM.getY();
                            int k = this.theBlockPosM.getZ();

                            if (i < blockpos1.getX()) {
                                ++i;
                            } else if (j < blockpos1.getY()) {
                                i = blockpos.getX();
                                ++j;
                            } else if (k < blockpos1.getZ()) {
                                i = blockpos.getX();
                                j = blockpos.getY();
                                ++k;
                            }

                            this.theBlockPosM.setXyz(i, j, k);
                            return this.theBlockPosM;
                        }
                    }

                    protected Object computeNext() {
                        return this.computeNext0();
                    }
                };
            }
        };
    }

    public BlockPos getImmutable() {
        return new BlockPos(this.getX(), this.getY(), this.getZ());
    }
}

package net.minecraft.util;

public class Matrix4f extends org.lwjgl.util.vector.Matrix4f
{
    public Matrix4f(float[] matrixIn)
    {
        this.m00 = matrixIn[0];
        this.m01 = matrixIn[1];
        this.m02 = matrixIn[2];
        this.m03 = matrixIn[3];
        this.m10 = matrixIn[4];
        this.m11 = matrixIn[5];
        this.m12 = matrixIn[6];
        this.m13 = matrixIn[7];
        this.m20 = matrixIn[8];
        this.m21 = matrixIn[9];
        this.m22 = matrixIn[10];
        this.m23 = matrixIn[11];
        this.m30 = matrixIn[12];
        this.m31 = matrixIn[13];
        this.m32 = matrixIn[14];
        this.m33 = matrixIn[15];
    }

    public Matrix4f()
    {
        this.m00 = this.m01 = this.m02 = this.m03 = this.m10 = this.m11 = this.m12 = this.m13 = this.m20 = this.m21 = this.m22 = this.m23 = this.m30 = this.m31 = this.m32 = this.m33 = 0.0F;
    }
}

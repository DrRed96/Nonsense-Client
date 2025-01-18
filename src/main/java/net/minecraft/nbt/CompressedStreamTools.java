package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class CompressedStreamTools {
    /**
     * Load the gzipped compound from the inputstream.
     */
    public static NBTTagCompound readCompressed(InputStream is) throws IOException {
        DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(is)));
        NBTTagCompound nbttagcompound;

        try {
            nbttagcompound = read(datainputstream, NBTSizeTracker.INFINITE);
        } finally {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    /**
     * Write the compound, gzipped, to the outputstream.
     */
    public static void writeCompressed(NBTTagCompound nbt, OutputStream outputStream) throws IOException {

        try (DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)))) {
            write(nbt, dataoutputstream);
        }
    }

    public static void safeWrite(NBTTagCompound nbt, File file) throws IOException {
        File file1 = new File(file.getAbsolutePath() + "_tmp");

        if (file1.exists()) {
            file1.delete();
        }

        write(nbt, file1);

        if (file.exists()) {
            file.delete();
        }

        if (file.exists()) {
            throw new IOException("Failed to delete " + file);
        } else {
            file1.renameTo(file);
        }
    }

    public static void write(NBTTagCompound nbt, File file) throws IOException {

        try (DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file))) {
            write(nbt, dataoutputstream);
        }
    }

    public static NBTTagCompound read(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }

        DataInputStream datainputstream = new DataInputStream(new FileInputStream(file));
        NBTTagCompound nbttagcompound;

        try {
            nbttagcompound = read(datainputstream, NBTSizeTracker.INFINITE);
        } finally {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    /**
     * Reads from a CompressedStream.
     */
    public static NBTTagCompound read(DataInputStream inputStream) throws IOException {
        return read(inputStream, NBTSizeTracker.INFINITE);
    }

    /**
     * Reads the given DataInput, constructs, and returns an NBTTagCompound with the data from the DataInput
     */
    public static NBTTagCompound read(DataInput p_152456_0_, NBTSizeTracker p_152456_1_) throws IOException {
        NBTBase nbtbase = func_152455_a(p_152456_0_, 0, p_152456_1_);

        if (nbtbase instanceof NBTTagCompound) {
            return (NBTTagCompound) nbtbase;
        }

        throw new IOException("Root tag must be a named compound tag");
    }

    public static void write(NBTTagCompound p_74800_0_, DataOutput p_74800_1_) throws IOException {
        writeTag(p_74800_0_, p_74800_1_);
    }

    private static void writeTag(NBTBase p_150663_0_, DataOutput p_150663_1_) throws IOException {
        p_150663_1_.writeByte(p_150663_0_.getId());

        if (p_150663_0_.getId() != 0) {
            p_150663_1_.writeUTF("");
            p_150663_0_.write(p_150663_1_);
        }
    }

    private static NBTBase func_152455_a(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {

        byte b0 = input.readByte();
        if (b0 == 0) {
            return new NBTTagEnd();
        }

        input.readUTF();
        NBTBase nbtbase = NBTBase.createNewByType(b0);

        try {
            nbtbase.read(input, depth, sizeTracker);
            return nbtbase;
        } catch (IOException ioexception) {
            CrashReport crashreport = CrashReport.makeCrashReport(ioexception, "Loading NBT data");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
            crashreportcategory.addCrashSection("Tag name", "[UNNAMED TAG]");
            crashreportcategory.addCrashSection("Tag type", b0);
            throw new ReportedException(crashreport);
        }

    }
}

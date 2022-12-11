package net.lenni0451.mcstructs.nbt;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NbtIO {


    public static INbtTag readFile(final File f, final NbtReadTracker readTracker) throws IOException {
        return readFile(f, false, readTracker);
    }

    public static void writeFile(final File f, final String name, final INbtTag tag) throws IOException {
        writeFile(f, name, tag, false);
    }


    public static INbtTag readCompressedFile(final File f, final NbtReadTracker readTracker) throws IOException {
        return readFile(f, true, readTracker);
    }

    public static void writeCompressedFile(final File f, final String name, final INbtTag tag) throws IOException {
        writeFile(f, name, tag, true);
    }


    public static INbtTag readFile(final File f, final boolean compressed, final NbtReadTracker readTracker) throws IOException {
        try (InputStream fis = Files.newInputStream(f.toPath())) {
            return read(fis, compressed, readTracker);
        }
    }

    public static void writeFile(final File f, final String name, final INbtTag tag, final boolean compressed) throws IOException {
        try (OutputStream fos = Files.newOutputStream(f.toPath())) {
            write(fos, name, tag, compressed);
        }
    }


    public static INbtTag read(final InputStream is, final boolean compressed, final NbtReadTracker readTracker) throws IOException {
        if (compressed) return read(new DataInputStream(new GZIPInputStream(is)), readTracker);
        else return read(new DataInputStream(is), readTracker);
    }

    public static void write(final OutputStream os, final String name, final INbtTag tag, final boolean compressed) throws IOException {
        if (compressed) {
            try (GZIPOutputStream gos = new GZIPOutputStream(os)) {
                write(new DataOutputStream(gos), name, tag);
            }
        } else {
            write(new DataOutputStream(os), name, tag);
        }
    }


    public static INbtTag read(final DataInput in, final NbtReadTracker readTracker) throws IOException {
        NbtHeader header = readNbtHeader(in, readTracker);
        if (header.isEnd()) return null;
        INbtTag tag = header.getType().newInstance();
        readTracker.pushDepth();
        tag.read(in, readTracker);
        readTracker.popDepth();
        return tag;
    }

    public static void write(final DataOutput out, final String name, final INbtTag tag) throws IOException {
        writeNbtHeader(out, new NbtHeader(NbtType.byClass(tag.getClass()), name));
        tag.write(out);
    }


    public static NbtHeader readNbtHeader(final DataInput in, final NbtReadTracker readTracker) throws IOException {
        byte type = in.readByte();
        if (NbtType.END.getId() == type) return NbtHeader.END;
        return new NbtHeader(NbtType.byId(type), in.readUTF());
    }

    public static void writeNbtHeader(final DataOutput out, final NbtHeader header) throws IOException {
        if (header.isEnd()) {
            out.writeByte(NbtType.END.getId());
        } else {
            out.writeByte(header.getType().getId());
            out.writeUTF(header.getName());
        }
    }

}

package com.example.hiwin.teacher_version_bob.communication.bluetooth.concrete;

import com.example.hiwin.teacher_version_bob.communication.bluetooth.framework.PackageHandler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SymbolPackageHandler implements PackageHandler {

    private final List<Byte> buffer = new LinkedList<>();
    private final byte[] symbol;
    private final List<byte[]> packages = new LinkedList<>();

    public SymbolPackageHandler(byte[] symbol) {
        this.symbol = symbol;
    }

    @Override
    public void handle(byte[] data) {

        for (byte b : data) {
            buffer.add(b);
        }

        int indexOfEOL = getIndexOfFirstEOL(buffer);
        while (indexOfEOL != -1) {
            packages.add(subArray(buffer, 0, indexOfEOL));

            for (int i = 0; i <= indexOfEOL; i++) {
                buffer.remove(0);
            }

            indexOfEOL = getIndexOfFirstEOL(buffer);
        }
    }

    @Override
    public boolean hasNextPackage() {
        return packages.size() != 0;
    }

    private int getIndexOfFirstEOL(List<Byte> data) {
        for (int i = 0; i < data.size(); i++) {
            int found = 0;
            for (int j = 0; j < symbol.length; j++) {
                if (i + j < data.size()) {
                    byte d1 = symbol[j];
                    byte d2 = data.get(i + j);
                    if (d1 == d2)
                        found++;
                }
            }
            if (found == symbol.length)
                return i;
        }
        return -1;
    }

    private byte[] subArray(List<Byte> data, int start, int len) {
        byte[] buf = new byte[len];
        for (int i = start; i < start + len; i++) {
            buf[i - start] = data.get(i);
        }
        return buf;
    }

    @Override
    public byte[] getPackageAndNext() {
        if (hasNextPackage()) {
            byte[] data = packages.get(0);
            byte[] buf = Arrays.copyOf(data, data.length);
            packages.remove(0);
            return buf;
        } else {
            throw new RuntimeException("No package");
        }
    }

    @Override
    public byte[] convertToPackage(byte[] data) {
        byte[] newArray=new byte[data.length+symbol.length];
        System.arraycopy(data,0,newArray,0,data.length);
        System.arraycopy(symbol,0,newArray,data.length,symbol.length);
        return newArray;
    }
}

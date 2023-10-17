package com.example.hiwin.teacher_version_bob.communication.bluetooth.framework;

public interface PackageHandler {
    void handle(byte[] data);
    boolean hasNextPackage();
    byte[] getPackageAndNext();
    byte[] convertToPackage(byte[] data);
}

package com.neoon.blesdk.core.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2019/3/19).
 * 功能:
 */


public class FilePackage {
    private List<byte[]> bytes20 = new ArrayList<>();

    public List<byte[]> getBytes20() {
        return bytes20;
    }

    public FilePackage(List<byte[]> bytes20) {
        this.bytes20 = bytes20;
    }

}

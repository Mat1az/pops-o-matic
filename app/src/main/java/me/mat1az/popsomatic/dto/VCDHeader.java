package me.mat1az.popsomatic.dto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class VCDHeader {

    /**
     * @param diskSize Data length size, not size by cluster!
     */
    public VCDHeader(int diskSize) {
        this.setSizePerSectorA(diskSize);
        this.setSizePerSectorB(diskSize);
    }

    private TOC TOC;
    private final int SECTORS = 2352;


    public TOC getTOC() {
        return TOC;
    }

    public void setTOC(TOC TOC) {
        this.TOC = TOC;
    }

    // 0x00000400 ; supposedly a magic
    private final byte[] MAGIC = {0x6D, 0x74, 0x7A, 0x00, 0x00, 0x00, 0x00, 0x00};

    // 0x00000408 ; disk size per sector
    private byte[] sizePerSectorA;
    // 0x0000040C ; disk size per sector
    private byte[] sizePerSectorB;

    private byte[] getMAGIC() {
        return MAGIC;
    }

    public int getSECTORS() {
        return SECTORS;
    }

    public byte[] getSizePerSectorA() {
        return sizePerSectorA;
    }

    public void setSizePerSectorA(int diskSize) {
        sizePerSectorA = new byte[4];
        System.arraycopy(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(diskSize / SECTORS).array(), 0, this.sizePerSectorA, 0, this.sizePerSectorA.length);
    }

    public byte[] getSizePerSectorB() {
        return sizePerSectorB;
    }

    public void setSizePerSectorB(int diskSize) {
        sizePerSectorB = new byte[4];
        System.arraycopy(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(diskSize / SECTORS).array(), 0, this.sizePerSectorB, 0, this.sizePerSectorB.length);
    }

    public byte[] getData() {
        //just concat in order
        byte[] combined = new byte[TOC.getSIZE() + MAGIC.length + sizePerSectorA.length + sizePerSectorB.length];
        System.arraycopy(TOC.getUniqueTracks(), 0, combined, 0, TOC.getUniqueTracks().length);
        System.arraycopy(MAGIC, 0, combined, 1024, MAGIC.length);
        System.arraycopy(sizePerSectorA, 0, combined, 1024 + MAGIC.length, sizePerSectorA.length);
        System.arraycopy(sizePerSectorB, 0, combined, 1024 + MAGIC.length + sizePerSectorA.length, sizePerSectorB.length);
        return combined;
    }

    @Override
    public String toString() {
        return "VCDHeader{" +
                "TOC=" + TOC +
                ", SECTORS=" + SECTORS +
                ", MAGIC=" + Arrays.toString(MAGIC) +
                ", sizePerSectorA=" + Arrays.toString(sizePerSectorA) +
                ", sizePerSectorB=" + Arrays.toString(sizePerSectorB) +
                '}';
    }
}

package me.mat1az.popsomatic.pojos;

import java.util.Arrays;

public class Track {

    public static final int SIZE = 10;
    private byte[] data = new byte[SIZE];

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getSIZE() {
        return SIZE;
    }

    @Override
    public String toString() {
        return "Track{" +
                "SIZE=" + SIZE +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}

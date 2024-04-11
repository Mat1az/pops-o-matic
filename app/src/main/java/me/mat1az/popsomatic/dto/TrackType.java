package me.mat1az.popsomatic.dto;

public enum TrackType {
    DATA_TRACK((byte) 0x41), AUDIO_TRACK((byte) 0x01);
    private final byte value;

    TrackType(byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }
}

package me.mat1az.popsomatic.dto;

import android.util.Log;

import java.util.Arrays;

import me.mat1az.popsomatic.pojos.Track;

public class TOC {

    public enum DiscType {
        XA((byte) 0x20);

        private final byte value;

        DiscType(byte value) {
            this.value = value;
        }

        public byte value() {
            return value;
        }
    }

    private final int SIZE;

    public TOC(int size, int uniqueSize) {
        this.SIZE = size;
        this.uniqueTracks = new Track[uniqueSize];
        this.tracks = new Track[Math.floorDiv(size, Track.SIZE) - uniqueSize];
        Log.println(Log.DEBUG, "dev11", "floor: " + (Math.floorDiv(size, Track.SIZE) - uniqueSize));
        /*
        Track a0 = TrackFactory.createTrack(TrackCategory.A0, new byte[]{fType.value(), 0x00, (byte) 0xA0, 0x00, 0x00, 0x00, 0x00, fIndex, discType.value(), 0x00});
        Track a1 = TrackFactory.createTrack(TrackCategory.A1, new byte[]{lType.value(), 0x00, (byte) 0xA1, 0x00, 0x00, 0x00, 0x00, lIndex, 0x00, 0x00});
        Track a2 = TrackFactory.createTrack(TrackCategory.A2, new byte[]{lType.value(), 0x00, (byte) 0xA2, 0x00, 0x00, 0x00, 0x00, totalTimecode.getMM(), totalTimecode.getSS(), totalTimecode.getFF()});
        System.arraycopy(a0.getData(), 0, result, 0, a0.getData().length);
        System.arraycopy(a1.getData(), 0, result, a0.getData().length, a1.getData().length);
        System.arraycopy(a2.getData(), 0, result, a0.getData().length + a1.getData().length, a2.getData().length);
         */
    }

    public int getSIZE() {
        return SIZE;
    }

    private Track[] tracks;
    private Track[] uniqueTracks;

    public Track[] getUniqueTracks() {
        return uniqueTracks;
    }

    public void setUniqueTracks(Track[] uniqueTracks) {
        this.uniqueTracks = uniqueTracks;
    }

    public Track[] getTracks() {
        return tracks;
    }

    public void setTracks(Track[] tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        return "TOC{" +
                "SIZE=" + SIZE +
                ", tracks=" + Arrays.toString(tracks) +
                ", uniqueTracks=" + Arrays.toString(uniqueTracks) +
                '}';
    }
}

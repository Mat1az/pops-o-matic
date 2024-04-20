package me.mat1az.popsomatic.pojos;

import java.util.List;

public class PSXImage {

    private List<TrackInfo> trackInfo;
    private byte[] data;

    public List<TrackInfo> getTrackInfo() {
        return trackInfo;
    }

    public void setTrackInfo(List<TrackInfo> trackInfo) {
        this.trackInfo = trackInfo;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}

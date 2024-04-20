package me.mat1az.popsomatic.pojos;

import java.util.Map;

public class TrackInfo {

    private String bin;
    private Map.Entry<String, TrackMode> track;
    private Map<String, Timecode> index;

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public Map.Entry<String, TrackMode> getTrack() {
        return track;
    }

    public void setTrack(Map.Entry<String, TrackMode> track) {
        this.track = track;
    }

    public Map<String, Timecode> getIndex() {
        return index;
    }

    public void setIndex(Map<String, Timecode> index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "TrackInfo{" +
                "bin='" + bin + '\'' +
                ", track=" + track +
                ", index=" + index +
                '}';
    }
}

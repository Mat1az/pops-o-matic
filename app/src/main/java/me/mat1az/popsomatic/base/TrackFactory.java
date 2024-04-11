package me.mat1az.popsomatic.base;

import me.mat1az.popsomatic.dto.TrackCategory;
import me.mat1az.popsomatic.pojos.Track;

public class TrackFactory {

    public static Track createTrack(TrackCategory category, byte[] data) {
        switch (category) {
            case A0: {
                Track track = new Track();
                track.setData(data);
                return track;
            }
            case A1: {
                Track track = new Track();
                track.setData(data);
                return track;
            }
            case A2: {
                Track track = new Track();
                track.setData(data);
                return track;
            }
            case CD: {
                Track track = new Track();
                track.setData(data);
                return track;
            }
        }
        return null;
    }
}

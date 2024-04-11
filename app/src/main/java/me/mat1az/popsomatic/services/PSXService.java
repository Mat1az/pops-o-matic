package me.mat1az.popsomatic.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import me.mat1az.popsomatic.R;
import me.mat1az.popsomatic.base.TrackFactory;
import me.mat1az.popsomatic.dto.TOC;
import me.mat1az.popsomatic.dto.Timecode;
import me.mat1az.popsomatic.dto.TrackCategory;
import me.mat1az.popsomatic.dto.TrackType;
import me.mat1az.popsomatic.dto.VCDHeader;
import me.mat1az.popsomatic.models.PSXGame;
import me.mat1az.popsomatic.pojos.Track;
import me.mat1az.popsomatic.utils.Utils;

public class PSXService {

    JSONObject titles;
    Context c;

    public PSXService(Context c) {
        try {
            this.c = c;
            InputStreamReader in = new InputStreamReader(c.getResources().openRawResource(R.raw.titles), StandardCharsets.UTF_8);
            titles = new JSONObject(new JSONTokener(IOUtils.toString(in)));
        } catch (Exception ignored) {
        }
    }

    @Nullable
    public byte[] mergeBinaries(String[] binaries, String dir) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            for (String bin : binaries) {
                InputStream in = new FileInputStream(dir + bin);
                byte[] b = IOUtils.toByteArray(in);
                stream.write(b);
            }
            return stream.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable

    public PSXGame getPSXGame(byte[] in) {
        String fileSize = Utils.humanReadableByteCountBin(in.length);
        String gameID = findID(in);
        String gameDBTitle = findBDTitle(gameID);
        int cDiskSize = Math.floorDiv(in.length, 2352);
        cDiskSize += 150;
        int mm = Math.floorDiv(cDiskSize, 4500);
        int ss = Math.floorDiv(cDiskSize % 4500, 75);
        int ff = cDiskSize % 75;
        Timecode totalTime = new Timecode(
                Byte.parseByte(String.valueOf(mm), 16),
                Byte.parseByte(String.valueOf(ss), 16),
                Byte.parseByte(String.valueOf(ff), 16));
        TOC toc = new TOC(1024, 3);
        Track a0 = TrackFactory.createTrack(TrackCategory.A0, new byte[]{TrackType.DATA_TRACK.value(), 0x00, (byte) 0xA0, 0x00, 0x00, 0x00, 0x00, (byte) 1, TOC.DiscType.XA.value(), 0x00});
        Track a1 = TrackFactory.createTrack(TrackCategory.A1, new byte[]{TrackType.DATA_TRACK.value(), 0x00, (byte) 0xA1, 0x00, 0x00, 0x00, 0x00, (byte) 1, 0x00, 0x00});
        Track a2 = TrackFactory.createTrack(TrackCategory.A2, new byte[]{TrackType.DATA_TRACK.value(), 0x00, (byte) 0xA2, 0x00, 0x00, 0x00, 0x00, totalTime.getMM(), totalTime.getSS(), totalTime.getFF()});
        toc.setUniqueTracks(new Track[]{a0, a1, a2});
        Log.println(Log.DEBUG, "dev11", mm + ":" + ss + ":" + ff);
        Log.println(Log.DEBUG, "dev11", toc.toString());
        VCDHeader vcdHeader = new VCDHeader(in.length);
        vcdHeader.setTOC(toc);
        return new PSXGame(gameID, gameDBTitle, fileSize, in, vcdHeader);
    }

    public boolean makeVCD(PSXGame psxGame) {
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(psxGame.getDir(), (psxGame.isDB() ? psxGame.getDBTitle() : psxGame.getName()).concat(".VCD")));
            Log.println(Log.DEBUG, "dev11", psxGame.getVCDHeader().toString());
            Log.println(Log.DEBUG, "dev11", psxGame.getVCDHeader().getTOC().toString());
            Log.println(Log.DEBUG, "dev11", psxGame.getVCDHeader().getData().length + "");
            Log.println(Log.DEBUG, "dev11", psxGame.getVCDHeader().getTOC().getUniqueTracks().length + "");
            outputStream.write(psxGame.getVCDHeader().getData());
            //outputStream.write(psxGame.getData());
            //outputStream.write(new byte[1047536]);
            //outputStream.write(psxGame.getData()); //IOUtils required, readAllBytes is API 33+
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    public String findID(byte[] in) {
        final int OFFSET = 37696;
        final int ID_LENGTH = 16;
        byte[] bytesID = new byte[ID_LENGTH];
        System.arraycopy(in, OFFSET, bytesID, 0, ID_LENGTH);
        return new String(bytesID).trim().replace("_", "-");
    }

    public String findBDTitle(String gameID) {
        try {
            return titles.getString(gameID);
        } catch (JSONException e) {
            return "BD Title Not Found!";
        }
    }
}

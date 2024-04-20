package me.mat1az.popsomatic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.mat1az.popsomatic.pojos.PSXImage;
import me.mat1az.popsomatic.pojos.Timecode;
import me.mat1az.popsomatic.pojos.TrackMode;
import me.mat1az.popsomatic.pojos.PSXGame;
import me.mat1az.popsomatic.pojos.TrackInfo;
import me.mat1az.popsomatic.services.PSXService;
import me.mat1az.popsomatic.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private PSXGame psxGame;
    public PSXService psxService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        psxService = new PSXService(getApplicationContext());
    }

    private void initComponents(PSXGame psxGame) {
        TextView title = findViewById(R.id.title), id = findViewById(R.id.id), name = findViewById(R.id.name), size = findViewById(R.id.size);
        Button nextBtn = findViewById(R.id.nextBtn);
        title.setText(psxGame.getDBTitle());
        id.setText(psxGame.getID());
        name.setText(psxGame.getName());
        size.setText(psxGame.getSize());
        title.setTextColor(getColor(R.color.success));
        id.setTextColor(getColor(R.color.success));
        name.setTextColor(getColor(R.color.success));
        size.setTextColor(getColor(R.color.success));
        title.setSelected(true);
        nextBtn.setEnabled(true);
        nextBtn.setAlpha(1);
    }

    public void loadFile(View view) {
        Intent filePicker = new Intent(Intent.ACTION_GET_CONTENT);
        filePicker.setType("*/*");
        startActivityForResult(filePicker, 4452);
        System.out.println("test");
    }


    /**
     * @param cue    .cue File
     * @param binDir Binaries directory
     * @return Returns raw bytes of the merged binaries
     */
    @Nullable
    public PSXImage getPSXImage(Uri cue, String binDir) {
        PSXImage psxImage = new PSXImage();
        try {
            InputStreamReader reader = new InputStreamReader(getContentResolver().openInputStream(cue), StandardCharsets.UTF_8);
            String regex = "FILE \"([^\"]+\\.bin)\" BINARY((?:(?!FILE).)*)?";
            Matcher m = Pattern.compile(regex, Pattern.DOTALL).matcher(IOUtils.toString(reader));
            List<TrackInfo> infoList = new ArrayList<>();
            while (m.find()) {
                TrackInfo trackInfo = new TrackInfo();
                trackInfo.setBin(m.group(1).trim());
                for (String s : m.group(2).split("\n")) {
                    String[] line = s.trim().split(" ");
                    if (s.contains("TRACK")) {
                        TrackMode trackMode = TrackMode.MODE2_2352; //default
                        if (line[2].equals("MODE2/2352")) {
                            trackMode = TrackMode.MODE2_2352;
                        }
                        trackInfo.setTrack(new AbstractMap.SimpleEntry<>(line[1], trackMode));
                    } else if (s.contains("INDEX")) {
                        String[] timecode = line[2].split(":");
                        Map<String, Timecode> map = new HashMap<>();
                        map.put(line[1], new Timecode(
                                Byte.parseByte(String.valueOf(timecode[0]), 16),
                                Byte.parseByte(String.valueOf(timecode[1]), 16),
                                Byte.parseByte(String.valueOf(timecode[2]), 16)));
                        trackInfo.setIndex(map);
                    }
                }
                infoList.add(trackInfo);
            }
            psxImage.setTrackInfo(infoList);
            Log.println(Log.DEBUG, "dev11", String.valueOf(psxImage.getTrackInfo()));
            List<String> binaries = new ArrayList<>();
            while (m.find()) {
                binaries.add(m.group(1));
            }
            if (!binaries.isEmpty() && binaries.size() > 1) {
                //merge binaries
                psxImage.setData(psxService.mergeBinaries(binaries.toArray(new String[0]), binDir));
            } else {
                //getting the single binary
                psxImage.setData(IOUtils.toByteArray(new FileInputStream(binDir + binaries.get(0))));
            }
        } catch (FileNotFoundException ignored) {
            Toast.makeText(this, "[Error] Storage access required", Toast.LENGTH_LONG).show();
        } catch (IOException ignored) {
            Log.println(Log.DEBUG, "dev11", "IOException getPSXImage: " + ignored);
        }
        return psxImage;
    }

    /**
     * @param uri Data Uri
     * @return 0 = directory; 1 = name; 2 = extension
     */
    private String[] resolvePath(Uri uri) {
        String[] result = new String[3];
        String path = Utils.getRealPath(this.getApplicationContext(), uri);
        Matcher matcher = Pattern.compile("(/.+/)(.+)(\\.\\w{3})").matcher(path);
        if (matcher.find()) {
            result = new String[]{matcher.group(1), matcher.group(2), matcher.group(3)};
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 4452 && data != null) {
            String fileType = getContentResolver().getType(Objects.requireNonNull(data.getData()));
            assert fileType != null;
            if (fileType.equals("application/octet-stream")) {
                try {
                    PSXImage psxImage = new PSXImage();
                    String[] path = resolvePath(data.getData());
                    if (path[2].equalsIgnoreCase(".cue")) {
                        psxImage = this.getPSXImage(data.getData(), path[0]);
                        Log.println(Log.DEBUG, "dev11", "0: " + path[0]);
                        Log.println(Log.DEBUG, "dev11", "1: " + path[1]);
                        Log.println(Log.DEBUG, "dev11", "2: " + path[2]);

                    } else if (path[2].equalsIgnoreCase(".bin")) { // TODO .bin without .cue? Testing pending
                        //IOUtils.toByteArray(Objects.requireNonNull(getContentResolver().openInputStream(data.getData())));
                    }
                    psxGame = psxService.getPSXGame(psxImage);
                    psxGame.setName(path[1] + path[2]);
                    psxGame.setDir(path[0]);
                    psxService.makeVCD(psxGame);
                    initComponents(psxGame);
                } catch (Exception ignored) {
                    Log.println(Log.DEBUG, "dev11", "onActivityResult: " + ignored);
                    Log.println(Log.DEBUG, "dev11", "onActivityResult: " + ignored.getMessage());
                    Log.println(Log.DEBUG, "dev11", "onActivityResult: " + ignored.getLocalizedMessage());
                    Log.println(Log.DEBUG, "dev11", "onActivityResult: " + ignored.getCause());

                }
            }
        }
    }

    public void toSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("layout", R.layout.settings);
        intent.putExtra("game", psxGame.info());
        startActivity(intent);
    }
}
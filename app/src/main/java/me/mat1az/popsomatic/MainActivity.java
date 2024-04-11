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
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.mat1az.popsomatic.models.PSXGame;
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
    public byte[] getPSXImage(Uri cue, String binDir) {
        byte[] psxImage = null;
        try {
            InputStreamReader reader = new InputStreamReader(getContentResolver().openInputStream(cue), StandardCharsets.UTF_8);
            Matcher m = Pattern.compile("FILE \"(.+)\" BINARY").matcher(IOUtils.toString(reader));
            ArrayList<String> binaries = new ArrayList<>();
            while (m.find()) {
                Log.println(Log.DEBUG, "dev11", "find");
                binaries.add(m.group(1));
            }
            if (!binaries.isEmpty() && binaries.size() > 1) {
                //merge binaries
                Log.println(Log.DEBUG, "dev11", "merge");
                psxImage = psxService.mergeBinaries(binaries.toArray(new String[0]), binDir);
            } else {
                //getting the single binary
                Log.println(Log.DEBUG, "dev11", "single bin");
                psxImage = IOUtils.toByteArray(new FileInputStream(binDir + binaries.get(0)));
            }
        } catch (FileNotFoundException ignored) {
            Toast.makeText(this, "[Error] Storage access required", Toast.LENGTH_LONG).show();
        } catch (IOException ignored) {
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
                    byte[] in;
                    String[] path = resolvePath(data.getData());
                    in = path[2].equalsIgnoreCase(".cue") ? this.getPSXImage(data.getData(), path[0]) : path[2].equalsIgnoreCase(".bin") ? IOUtils.toByteArray(Objects.requireNonNull(getContentResolver().openInputStream(data.getData()))) : null;
                    psxGame = psxService.getPSXGame(in);
                    psxGame.setName(path[1] + path[2]);
                    psxGame.setDir(path[0]);
                    psxService.makeVCD(psxGame);
                    initComponents(psxGame);
                } catch (Exception ignored) {
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
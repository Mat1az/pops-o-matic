package me.mat1az.popsomatic;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initComponents();
    }

    private boolean make(View view) {
        /*
        if (psxService.makeVCD(psxGame, currentDir)) {
            Toast.makeText(getApplicationContext(), "Wrote at " + currentDir, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error making VCD", Toast.LENGTH_LONG).show();
        }
         */
        return true;
    }

    private void initComponents() {
        String[] info = getIntent().getStringExtra("game").split("\n");
        TextView title = findViewById(R.id.title), id = findViewById(R.id.id), size = findViewById(R.id.size), dir = findViewById(R.id.dir);
        EditText file = findViewById(R.id.file);
        id.setText(info[0]);
        title.setText(info[2]);
        size.setText(info[3]);
        dir.setText(info[4]);
        file.setText(info[0] + "." + info[2] + ".VCD");
        title.setSelected(true);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

}
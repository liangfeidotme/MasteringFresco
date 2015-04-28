package com.liangfeizc.masteringfresco;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity {

    @InjectView(R.id.circle_img)
    private SimpleDraweeView img;

    @InjectView(R.id.aspect_ratio_bar)
    private SeekBar aspectRatioSeekBar;

    @InjectView(R.id.which_fixed)
    private RadioGroup whichFixedRadioGrp;

    @InjectView(R.id.aspect_ratio_value)
    private TextView aspectRatioValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        img.setImageURI(Uri.parse(Images.URL));

        img.setAspectRatio(1);

        aspectRatioSeekBar.setMax(9);
        aspectRatioValue.setText(String.valueOf(aspectRatioSeekBar.getProgress() + 1));
        aspectRatioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                img.setAspectRatio(progress + 1);
                aspectRatioValue.setText("" + img.getAspectRatio());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.action_drawable:
                startActivity(new Intent(this, DrawableActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

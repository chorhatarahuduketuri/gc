package dadeindustries.game.gc.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.media.MediaPlayer;


import com.example.gc.R;

import dadeindustries.game.gc.logic.Core;

public class Start extends Activity {

    private GalaxyView gv;
    private Core core;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        core = new Core();
        gv = new GalaxyView(this, core);

        mediaPlayer = MediaPlayer.create(this, R.raw.lj_kruzer_chantiers_navals);
        mediaPlayer.setLooping(true);
        mediaPlayer.start(); // TODO: switch to async preparation method

        setContentView(gv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
     * (non-Javadoc) Override the back button on Android phone to do what WE want
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("GalaxyConquest");

        alertDialogBuilder
                .setMessage("Do you wish to exit GC?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                finish();
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onPause() {
        mediaPlayer.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaPlayer.start();
    }
}

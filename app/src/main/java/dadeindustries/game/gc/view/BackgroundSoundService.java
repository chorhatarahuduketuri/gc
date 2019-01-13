package dadeindustries.game.gc.view;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.gc.R;

public class BackgroundSoundService extends Service {

    private static final String TAG = null;
    MediaPlayer player;

    public IBinder onBind(Intent arg0) {
        Log.i("MyIntent", ""+arg0.toString());
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        player = MediaPlayer.create(this, R.raw.lj_kruzer_chantiers_navals);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

    }
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!player.isPlaying()) {
            player.start();
        }

        if (intent.getAction() != null) {
            if (intent.getAction().equals("PAUSE")) {
                pause();
            }
        }

        Log.i("Music", "starting");

        return 1;
    }

    public void pause() {
        player.pause();
    }

    public void resume() {
        player.start();
    }


    public void onStart(Intent intent, int startId) {
        // TODO
        player.start();
    }

    public IBinder onUnBind(Intent arg0) {
        // TODO Auto-generated method stub

        return null;
    }

    @Override
    public void onDestroy() {

        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }
}


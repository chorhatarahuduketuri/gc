package dadeindustries.game.gc.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.gc.R;

import java.util.ArrayList;

import dadeindustries.game.gc.mechanics.Event;
import dadeindustries.game.gc.mechanics.turn.TurnProcessor;

public class Start extends Activity {

	private MediaPlayer mediaPlayer, battleAlert;
	private GalaxyView galaxyView;
	private Button button;
	private TurnProcessor turnProcessor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		galaxyView = (GalaxyView) findViewById(R.id.myview);
		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				turnProcessor = new TurnProcessor();
				ArrayList<Event> events = turnProcessor.endTurn(galaxyView.getGlobalGameData());
				for (Event event : events) {
					if (event.getEventType() == Event.EventType.BATTLE) {
						showBattleReport(event);
					}
				}
				galaxyView.invalidate();
			}
		});

		mediaPlayer = MediaPlayer.create(this, R.raw.lj_kruzer_chantiers_navals);
		battleAlert = MediaPlayer.create(this, R.raw.redalert_klaxon_sttos_recreated_178032);
		mediaPlayer.setLooping(true);
		mediaPlayer.start(); // TODO: switch to async preparation method

	}

	/**
	 * Display information about event in GUI popup window (such as a battle report)
	 *
	 * @param event The event object with all the information
	 */
	public void showBattleReport(Event event) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setTitle("Battle report (" +
						event.getCoordinates().x + "," +
						event.getCoordinates().y + ")")
				.setMessage(event.getDescription())
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Deliberately empty. Just dismisses the popup.
							}
						})
				.create()
				.show();
		battleAlert.start();
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

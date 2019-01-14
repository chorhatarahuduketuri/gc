package dadeindustries.game.gc.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.gc.R;

import java.util.ArrayList;
import java.util.List;

import dadeindustries.game.gc.mechanics.Event;
import dadeindustries.game.gc.mechanics.turn.TurnProcessor;
import dadeindustries.game.gc.view.EmpireView;

public class Start extends Activity {

	private List<Event> eventlist = null;
	private MediaPlayer battleAlert;
	private GalaxyView galaxyView;
	private Button endTurnButton;
	private TurnProcessor turnProcessor;
	private Intent music;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Disables global menu button for now */
		Button menuButton = (Button) findViewById(R.id.menu);
		menuButton.setEnabled(true);
		menuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent myIntent = new Intent(Start.this, EmpireView.class);
				Start.this.startActivityForResult(myIntent, 1);
			}
		});

		galaxyView = (GalaxyView) findViewById(R.id.myview);

		/* When the end turn button is pressed */
		endTurnButton = (Button) findViewById(R.id.button);
		endTurnButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				turnProcessor = new TurnProcessor();
				ArrayList<Event> events = turnProcessor.endTurn(galaxyView.getGlobalGameData());

				/* Get events from the next turn and display ones of interest to UI */
				for (Event event : events) {

					switch (event.getEventType()) {
						case BATTLE:
							showBattleReport(event);
							break;
						case UNIT_CONSTRUCTION_COMPLETE:
							galaxyView.makeToast(event.getDescription());
							break;
						case RANDOM_EVENT:
							break;
						case COLONISE:
							showColonisationEvent(event.getDescription());
							break;
						case WINNER:
							showEndGamePopup(event.getDescription());
							break;
					}
				}
				galaxyView.invalidate(); /* Force a repaint of the screen */
				eventlist = events;
			}
		});

		/* When the Summary button is pressed, show what happened during the last turn */
		Button summaryButton = (Button) findViewById(R.id.summary);
		summaryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSummary();
			}
		});

		/* Prepare music */
		music = new Intent(this, BackgroundSoundService.class);
		startService(music);
        Log.i("Music", "attempting");

		battleAlert = MediaPlayer.create(this, R.raw.redalert_klaxon_sttos_recreated_178032);
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

	/**
	 * Shows a list of all events in the last turn in a popup box
	 */
	public void showSummary() {

		String s = "";

		if (eventlist != null) {
			for (Event event : eventlist) {
				s = s + "* " + event.getDescription() + "\n";
			}
		} else {
			s = "No events";
		}

		AlertDialog.Builder builder =
				new AlertDialog.Builder(this).
						setTitle("Turn summary").
						setMessage(s).
						setIcon(android.R.drawable.ic_dialog_info).
						setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
		builder.create().show();
	}

	public void showColonisationEvent(String s) {
		ImageView image = new ImageView(this);
		image.setMaxHeight(getResources().getDisplayMetrics().heightPixels / 2);
		image.setImageResource(R.drawable.colonise);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		image.setLayoutParams(params);

		AlertDialog.Builder builder =
				new AlertDialog.Builder(this).
						setMessage(s).
						setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).
						setView(image);
		builder.create().show();
	}

	public void showEndGamePopup(String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Game over");
		dialog.setCancelable(false);
		dialog.setMessage(message);
		dialog.setIcon(android.R.drawable.ic_popup_sync);
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// TODO: Restart the application
				// Currently it closes
				moveTaskToBack(true);
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
		});
		dialog.create().show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if(resultCode == RESULT_OK) {
				//String strEditText = data.getStringExtra("editTextValue");
			}
		}
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
								moveTaskToBack(true);
								android.os.Process.killProcess(android.os.Process.myPid());
								System.exit(1);
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
		music.setAction("PAUSE");
		stopService(music);
		super.onPause();
	}

	@Override
	public void onResume() {
		music.setAction("RESUME");
		startService(music);
		super.onResume();
	}
}

package dadeindustries.game.gc.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
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

public class Start extends Activity {

	private List<Event> eventlist = null;
	private MediaPlayer mediaPlayer, battleAlert;
	private GalaxyView galaxyView;
	private Button endTurnButton;
	private TurnProcessor turnProcessor;

	/**
	 * This is called when the game is launched. It initialises:
	 * - the game variables
	 * - music
	 * - button subroutines
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Disables global menu button for now */
		Button menuButton = (Button) findViewById(R.id.menu);
		menuButton.setEnabled(false);

		galaxyView = (GalaxyView) findViewById(R.id.myview);

		/* When the end turn button is pressed */
		endTurnButton = (Button) findViewById(R.id.button);
		endTurnButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onTurnButtonPressed();
			}
		});

		/* When the Summary button is pressed, show what happened during the last turn */
		Button summaryButton = (Button) findViewById(R.id.summary);
		summaryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSummaryPopup();
			}
		});

		/* Prepare music */
		mediaPlayer = MediaPlayer.create(this, R.raw.lj_kruzer_chantiers_navals);
		battleAlert = MediaPlayer.create(this, R.raw.redalert_klaxon_sttos_recreated_178032);
		mediaPlayer.setLooping(true);
		mediaPlayer.start(); // TODO: switch to async preparation method
		galaxyView.invalidate();
	}

	/**
	 * This is executed when the player pressed the End Turn button.
	 *
	 */
	public void onTurnButtonPressed() {

		turnProcessor = new TurnProcessor();

		ArrayList<Event> events = turnProcessor.endTurn(galaxyView.getGlobalGameData());

		/* Get events from the next turn and display the appropriate UI popup */
		for (Event event : events) {

			switch (event.getEventType()) {
				case BATTLE:
					showBattleReport(event);
					break;
				case UNIT_CONSTRUCTION_COMPLETE:
					galaxyView.makeToast(event.getDescription());
					break;
				case RANDOM_EVENT:
					// TODO: Fill in when random events are defined
					break;
				case COLONISE:
					showColonisationPopup(event.getDescription());
					break;
				case WINNER:
					showEndGamePopup(event.getDescription());
					break;
			}
		}
		galaxyView.invalidate(); /* Force a repaint of the screen */
		eventlist = events;
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
	public void showSummaryPopup() {

		String s = "";

		/* Get each event and make a bulleted list for the popup */
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

	public void showColonisationPopup(String s) {
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
		mediaPlayer.pause(); // Pause the music
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mediaPlayer.start(); // Unpause the music
	}
}

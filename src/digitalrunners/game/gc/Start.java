package digitalrunners.game.gc;

import com.example.gc.R;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;

public class Start extends Activity {

	GalaxyView gv;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        gv = new GalaxyView(this);
        setContentView(gv);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}

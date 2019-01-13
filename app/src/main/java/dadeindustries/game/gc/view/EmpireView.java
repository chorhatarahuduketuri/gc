package dadeindustries.game.gc.view;



import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.example.gc.R;

import java.util.ArrayList;
import java.util.List;


public class EmpireView extends AppCompatActivity {

	private Toolbar toolbar;
	private TabLayout tabLayout;
	private ViewPager viewPager;
	private int[] tabIcons = {
			R.drawable.system1,
			R.drawable.system1,
			R.drawable.system2
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empire_activity);
/*
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);
		setupTabIcons();
	}

	private void setupTabIcons() {

		TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
		tabOne.setText("Science");
		tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.system1, 0, 0);
		tabLayout.getTabAt(0).setCustomView(tabOne);

		TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
		tabTwo.setText("Diplomacy");
		tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.system1, 0, 0);
		tabLayout.getTabAt(1).setCustomView(tabTwo);

		TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
		tabThree.setText("Espionage");
		tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.system1, 0, 0);
		tabLayout.getTabAt(2).setCustomView(tabThree);
	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFrag(new ScienceFragment(), "ONE");
		adapter.addFrag(new DiplomacyFragment(), "TWO");
		adapter.addFrag(new EspionageFragment(), "THREE");
		viewPager.setAdapter(adapter);
	}

	class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFrag(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}
}

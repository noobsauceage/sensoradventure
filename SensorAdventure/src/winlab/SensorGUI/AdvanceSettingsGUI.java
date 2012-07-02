package winlab.SensorGUI;

import winlab.sensoradventure.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class AdvanceSettingsGUI extends ListActivity implements OnClickListener {
	public OnLongClickListener longClickListner;
	LinearLayout panel1, panel2, panel3, panel5;
	TextView text1, text2, text3;
	View openLayout;
	private Spinner preferredNetworkType;
	private Spinner preferredLoggingrategps;
	private Spinner micsampleingrate;
	private Spinner micchannelinput;
	private Spinner micchannelaudio;
	private Spinner othersamplingrate;

	private String[] mPreferredNetworkLabels = { "Network", "GPS", };

	private String[] mloggingrate = { "1", "5", "10", "30", "1", };

	private String[] micloggingrate = { "44.1", "22.05", "16", "11.025", };

	private String[] micchannelrate = { "MONO", "STEREO", };

	private String[] micchannelencoding = { "16", "8", };

	private String[] othersamplingrates1 = { "1", "5", "10", "30", "60", };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advancesettings);
		panel1 = (LinearLayout) findViewById(R.id.panel1);
		panel2 = (LinearLayout) findViewById(R.id.panel2);
		panel3 = (LinearLayout) findViewById(R.id.panel3);

		text1 = (TextView) findViewById(R.id.text1);
		text2 = (TextView) findViewById(R.id.text2);
		text3 = (TextView) findViewById(R.id.text3);

		text1.setOnClickListener(this);
		text2.setOnClickListener(this);
		text3.setOnClickListener(this);

		preferredNetworkType = (Spinner) findViewById(R.id.preferredNetworkType);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mPreferredNetworkLabels);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		preferredNetworkType.setAdapter(adapter);

		preferredLoggingrategps = (Spinner) findViewById(R.id.preferredLoggingrategps);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mloggingrate);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		preferredLoggingrategps.setAdapter(adapter1);

		micsampleingrate = (Spinner) findViewById(R.id.micsampleingrate);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, micloggingrate);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		micsampleingrate.setAdapter(adapter2);

		micchannelinput = (Spinner) findViewById(R.id.micchannelinput);
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, micchannelrate);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		micchannelinput.setAdapter(adapter3);

		micchannelaudio = (Spinner) findViewById(R.id.micchannelaudio);
		ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, micchannelencoding);
		adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		micchannelaudio.setAdapter(adapter4);

		othersamplingrate = (Spinner) findViewById(R.id.othersamplingrate);
		ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, othersamplingrates1);
		adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		othersamplingrate.setAdapter(adapter5);

	}

	String m = "mala" + panel1;

	public void onClick(View v) {
		hideOthers(v);
	}

	private void hideThemAll() {
		if (openLayout == null)
			return;
		if (openLayout == panel1)
			panel1.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel1, true));
		if (openLayout == panel2)
			panel2.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel2, true));
		if (openLayout == panel3)
			panel3.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel3, true));

	}

	private void hideOthers(View layoutView) {
		{
			int v;
			if (layoutView.getId() == R.id.text1) {
				v = panel1.getVisibility();
				if (v != View.VISIBLE) {
					panel1.setVisibility(View.VISIBLE);
					Log.v("CZ", "height..." + panel1.getHeight());
				}

				hideThemAll();
				if (v != View.VISIBLE) {
					panel1.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel1, true));
				}
			} else if (layoutView.getId() == R.id.text2) {
				v = panel2.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel2.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel2, true));
				}
			}

			else if (layoutView.getId() == R.id.text3) {
				v = panel3.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel3.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel3, true));
				}
			}

		}
	}

	public class ScaleAnimToHide extends ScaleAnimation {
		private View mView;

		private LayoutParams mLayoutParams;

		private int mMarginBottomFromY, mMarginBottomToY;

		private boolean mVanishAfter = false;

		public ScaleAnimToHide(float fromX, float toX, float fromY, float toY,
				int duration, View view, boolean vanishAfter) {
			super(fromX, toX, fromY, toY);
			setDuration(duration);
			openLayout = null;
			mView = view;
			mVanishAfter = vanishAfter;
			mLayoutParams = (LayoutParams) view.getLayoutParams();
			int height = mView.getHeight();
			mMarginBottomFromY = (int) (height * fromY)
					+ mLayoutParams.bottomMargin - height;
			mMarginBottomToY = (int) (0 - ((height * toY) + mLayoutParams.bottomMargin))
					- height;

			Log.v("CZ", "height..." + height + " , mMarginBottomFromY...."
					+ mMarginBottomFromY + " , mMarginBottomToY.."
					+ mMarginBottomToY);
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				int newMarginBottom = mMarginBottomFromY
						+ (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime);
				mLayoutParams.setMargins(mLayoutParams.leftMargin,
						mLayoutParams.topMargin, mLayoutParams.rightMargin,
						newMarginBottom);
				mView.getParent().requestLayout();
			} else if (mVanishAfter) {
				mView.setVisibility(View.GONE);
			}
		}
	}

	public class ScaleAnimToShow extends ScaleAnimation {

		private View mView;

		private LayoutParams mLayoutParams;

		private int mMarginBottomFromY, mMarginBottomToY;

		private boolean mVanishAfter = false;

		public ScaleAnimToShow(float toX, float fromX, float toY, float fromY,
				int duration, View view, boolean vanishAfter) {
			super(fromX, toX, fromY, toY);
			openLayout = view;
			setDuration(duration);
			mView = view;
			mVanishAfter = vanishAfter;
			mLayoutParams = (LayoutParams) view.getLayoutParams();
			mView.setVisibility(View.VISIBLE);
			int height = mView.getHeight();
			mMarginBottomFromY = 0;
			mMarginBottomToY = height;

		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				int newMarginBottom = (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime)
						- mMarginBottomToY;
				mLayoutParams.setMargins(mLayoutParams.leftMargin,
						mLayoutParams.topMargin, mLayoutParams.rightMargin,
						newMarginBottom);
				mView.getParent().requestLayout();
			}
		}

	}
}
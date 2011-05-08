package ch.hsr.eyecam.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import ch.hsr.eyecam.R;
import ch.hsr.eyecam.Settings;
import ch.hsr.eyecam.colormodel.ColorTransform;

public class SettingsMenu extends LinearLayout {

	private Context mContext;
	private RadioGroup mRadioGroupPartial;
	private RadioGroup mRadioGroupMenuSize;
	private RadioGroup mRadioGroupTextSize;
	private TextView mSeperator;
	private int mEnabled = 0;
	private int mDisabled = 1;
	
	private OnCheckedChangeListener mOnCheckedPartail = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if(checkedId == mEnabled){
				Settings.Partial = true;
				ColorTransform.setPartialEffect(Settings.ChossenFilter);
			}
			else {
				Settings.Partial = false;
				ColorTransform.setEffect(Settings.ChossenFilter);
			}
			//TODO evtl. doch postinvalidate() ?
			mRadioGroupPartial.invalidate();			
		}
	};
	//private final static String LOG_TAG = "ch.hsr.eyecam.view.SettingsMenu";
	
	public SettingsMenu(Context context) {
		super(context);
		
		mContext = context;
		mRadioGroupPartial = new RadioGroup(mContext);
		mRadioGroupMenuSize = new RadioGroup(mContext);
		mRadioGroupTextSize = new RadioGroup(mContext);
		mRadioGroupPartial.setOnCheckedChangeListener(mOnCheckedPartail);
		initItems();
	}
	
	private void initItems() {
		mSeperator = new TextView(mContext);
		mSeperator.setText("Partial");
		addView(mSeperator, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addRadioButton(R.string.enable_partial, mEnabled, mRadioGroupPartial);
		addRadioButton(R.string.disable_partial, mDisabled,mRadioGroupPartial);
		addView(mRadioGroupPartial);
		
		mSeperator = new TextView(mContext);
		mSeperator.setText("TextSize");
		addView(mSeperator, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addRadioButton(R.string.text_size_small, mEnabled, mRadioGroupTextSize);
		addRadioButton(R.string.text_size_medium, 1, mRadioGroupTextSize);
		addRadioButton(R.string.text_size_large, 2, mRadioGroupTextSize);
		addView(mRadioGroupTextSize);
		
		mSeperator = new TextView(mContext);
		mSeperator.setText("MenuSize");
		addView(mSeperator, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addRadioButton(R.string.menu_size_small, mEnabled, mRadioGroupMenuSize);
		addRadioButton(R.string.menu_size_medium, 1, mRadioGroupMenuSize);
		addRadioButton(R.string.menu_size_large, 2, mRadioGroupMenuSize);
		addView(mRadioGroupMenuSize);
	}
	
	private void addRadioButton(int text, int id, RadioGroup radioGroup) {
		RadioButton btn = new RadioButton(mContext);
		btn.setText(text);
		btn.setId(id);
		radioGroup.addView(btn, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	}

}

package ch.hsr.eyecam.view;

import ch.hsr.eyecam.R;
import ch.hsr.eyecam.colormodel.ColorTransform;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FilterListView extends LinearLayout implements OnCheckedChangeListener{

	private RadioGroup mRadioGroup;
	private Context mContext;
	
	public FilterListView(Context context) {
		super(context);
		mContext = context;
		
		mRadioGroup = new RadioGroup(context);
		createTitle();
		createFilterItems();
		
		mRadioGroup.setOnCheckedChangeListener(this);
		addView(mRadioGroup);
	}
	
	private void createTitle() {
	}

	private void createFilterItems() {
		RadioButton btn_none = new RadioButton(mContext);
		btn_none.setText(R.string.filter_none);
		btn_none.setId(ColorTransform.COLOR_EFFECT_NONE);
		mRadioGroup.addView(btn_none);
		
		RadioButton btn_sim = new RadioButton(mContext);
		btn_sim.setText(R.string.filter_simulate);
		btn_sim.setId(ColorTransform.COLOR_EFFECT_SIMULATE);
		mRadioGroup.addView(btn_sim);
		
		RadioButton btn_false = new RadioButton(mContext);
		btn_false.setText(R.string.filter_false_colors);
		btn_false.setId(ColorTransform.COLOR_EFFECT_FALSE_COLORS);
		mRadioGroup.addView(btn_false);
		
		RadioButton btn_intense = new RadioButton(mContext);
		btn_intense.setText(R.string.filter_intensify);
		btn_intense.setId(ColorTransform.COLOR_EFFECT_INTENSIFY_DIFFERENCE);
		mRadioGroup.addView(btn_intense);
		
		RadioButton btn_black = new RadioButton(mContext);
		btn_black.setText(R.string.filter_black);
		btn_black.setId(ColorTransform.COLOR_EFFECT_BLACK);
		mRadioGroup.addView(btn_black);
		
		RadioButton btn_black1 = new RadioButton(mContext);
		btn_black1.setText(R.string.filter_black);
		btn_black1.setId(ColorTransform.COLOR_EFFECT_BLACK);
		mRadioGroup.addView(btn_black1);
		
		RadioButton btn_black2 = new RadioButton(mContext);
		btn_black2.setText(R.string.filter_black);
		btn_black2.setId(ColorTransform.COLOR_EFFECT_BLACK);
		mRadioGroup.addView(btn_black2);
		
		RadioButton btn_black3 = new RadioButton(mContext);
		btn_black3.setText(R.string.filter_black);
		btn_black3.setId(ColorTransform.COLOR_EFFECT_BLACK);
		mRadioGroup.addView(btn_black3);
		
		RadioButton btn_black4 = new RadioButton(mContext);
		btn_black4.setText(R.string.filter_black);
		btn_black4.setId(ColorTransform.COLOR_EFFECT_BLACK);
		mRadioGroup.addView(btn_black4);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		ColorTransform.setEffect(checkedId);
	}
}

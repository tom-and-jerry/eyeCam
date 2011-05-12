package ch.hsr.eyecam.view;

import ch.hsr.eyecam.R;
import ch.hsr.eyecam.colormodel.ColorTransform;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FilterMenu extends LinearLayout implements OnCheckedChangeListener{
	private Context mContext;
	private RadioGroup mRadioGroup;

	public FilterMenu(Context context) {
		super(context);
		
		mContext = context;
		mRadioGroup = new RadioGroup(context);
		mRadioGroup.setOnCheckedChangeListener(this);
		
		initItems();
	}
	
	private void initItems() {
		addRadioButton(R.string.filter_none, ColorTransform.COLOR_EFFECT_NONE);
		addRadioButton(R.string.filter_simulate, ColorTransform.COLOR_EFFECT_SIMULATE);
		addRadioButton(R.string.filter_false_colors, ColorTransform.COLOR_EFFECT_FALSE_COLORS);
		addRadioButton(R.string.filter_intensify, ColorTransform.COLOR_EFFECT_INTENSIFY_DIFFERENCE);
		addRadioButton(R.string.filter_daltonize, ColorTransform.COLOR_EFFECT_DALTONIZE);
		
		addView(mRadioGroup);
	}

	private void addRadioButton(int text, int id) {
		RadioButton btn = new RadioButton(mContext);
		btn.setText(text);
		btn.setId(id);
		mRadioGroup.addView(btn, android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		ColorTransform.setEffect(checkedId);
		mRadioGroup.invalidate();
	}
}

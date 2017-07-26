package com.Adapters.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rtomyj.Diary.R;

import java.util.ArrayList;

public class FontAdapter extends ArrayAdapter<String> {

	static ArrayList<String> fonts = new ArrayList<String>();
	static Context c;
	static SharedPreferences sp;

	private static TextView tv;
	private static TextView tv2;

	public FontAdapter(Context context, ArrayList<String> objects) {

		super(context, R.id.fonts, objects);
		fonts.addAll(objects);
		c = context;
	}



	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.fonts_adapter_layout, parent, false);

		sp = PreferenceManager
				.getDefaultSharedPreferences(c);

		if (sp.getString("Theme", "Default").contains("Onyx")) {
			LinearLayout p = rowView.findViewById(R.id.parent);

			p.setBackground(c.getResources().getDrawable(
					R.drawable.onyx_selector));


			TextView tv2 = rowView.findViewById(R.id.textView2);

			tv2.setTextColor(c.getResources().getColor(R.color.UIDarkNormalText));
		}

		tv = rowView.findViewById(R.id.textView1);
		tv2 = rowView.findViewById(R.id.textView2);

		if (position == 1) {
					tv.setText(fonts.get(position));
					tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
							"fonts_adapter_layout/Ubuntu-Light.ttf"));
					tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
							"fonts_adapter_layout/Ubuntu-Light.ttf"));

		} else if (position == 2) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/bendable.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/bendable.ttf"));
		} else if (position == 3) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/Mercato-Light.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/Mercato-Light.ttf"));

		} else if (position == 4) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/Mercato-Light.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/ShareTechMono-Regular.ttf"));
		} else if (position == 5) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/Plain&SimplyFat.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/Plain&SimplyFat.ttf"));
		} else if (position == 6) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/MarckScript-Regular.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/MarckScript-Regular.ttf"));
		} else if (position == 7) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/TypeWritersSubstitute.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/TypeWritersSubstitute.ttf"));
		} else if (position == 8) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/Rothenburg Decorative.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/Rothenburg Decorative.ttf"));
		}
		else if (position == 9) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/data_activity_layout-latin.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/data_activity_layout-latin.ttf"));
		}
	else if (position ==10) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/day_roman.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/day_roman.ttf"));
		}
		else if (position == 11) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/Monoglyceride.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/Monoglyceride.ttf"));
		}
		else if (position == 12) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/always forever.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/always forever.ttf"));
		}
		else if (position == 13) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/Marker Felt.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/Marker Felt.ttf"));
		}
		else if (position == 14) {
			tv.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/girlnextdoor.ttf"));
			tv.setText(fonts.get(position));
			tv2.setTypeface(Typeface.createFromAsset(c.getAssets(),
					"fonts_adapter_layout/girlnextdoor.ttf"));
		}
		return rowView;
	}

}

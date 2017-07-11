package Settings;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.rtomyj.Diary.R;


public class NotifTimePicker extends DialogPreference{

	public NotifTimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPersistent(false);
		setDialogLayoutResource(R.layout.time_picker);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setTitle("Change Time of Notification.");
        setKey("TimePicker");
        
        setDialogIcon(null);
	}
}
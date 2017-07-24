package alfaloop.com.ipos.dialog;

import android.app.Dialog;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import alfaloop.com.ipos.R;

/**
 * Created by kiec on 2016/8/23.
 */
public class DialogSettingOnCheckedChangedListener implements CheckBox.OnCheckedChangeListener {

    private Dialog mDialog;

    public DialogSettingOnCheckedChangedListener (Dialog mDialog) {
        ((CheckBox) mDialog.findViewById(R.id.cb_TicketEnable)).setOnCheckedChangeListener(this);
        ((CheckBox) mDialog.findViewById(R.id.cb_StickEnable)).setOnCheckedChangeListener(this);
        this.mDialog = mDialog;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_StickEnable:
                mDialog.findViewById(R.id.et_StickIP).setEnabled(isChecked);
                break;
            case R.id.cb_TicketEnable:
                mDialog.findViewById(R.id.et_TicketIP).setEnabled(isChecked);
                break;
        }
    }
}

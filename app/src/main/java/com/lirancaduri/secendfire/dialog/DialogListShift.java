package com.lirancaduri.secendfire.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lirancaduri.secendfire.R;


public class DialogListShift extends DialogFragment {


    private DialogListShiftListener btnDeleteListener, btnAskForReplaceListener;

    // לפני שנוצר הדיאלוג אני לוקח אליו פוינטר ומשנה לו את הסטייל ככה שיהיה אנימציה שיבוא או יסגר
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SimpleStyleDialogShow;
        return dialog;
    }


    // יצרתי את כל VIEWS ונתתי להם LISTENER האזנה ללחיצה בלחיצה הדילוג יסגר ובנוסף מה שמי שקרה לי רצה שיקרה , יקרה איך ? עם ממשק LISTENER
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_item_shift_list,container,false);
        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnAskForReplace = view.findViewById(R.id.btnAskForReplace);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnDeleteListener != null){
                    btnDeleteListener.onClick();
                }
                dismiss();
            }
        });
        btnAskForReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnAskForReplaceListener != null) {
                    btnAskForReplaceListener.onClick();
                }
                dismiss();
            }
        });

        return view;
    }

    public interface DialogListShiftListener{
        void onClick();
    }


    public void setBtnDeleteListener(DialogListShiftListener btnDeleteListener) {
        this.btnDeleteListener = btnDeleteListener;
    }

    public void setBtnAskForReplaceListener(DialogListShiftListener btnAskForReplaceListener) {
        this.btnAskForReplaceListener = btnAskForReplaceListener;
    }


}

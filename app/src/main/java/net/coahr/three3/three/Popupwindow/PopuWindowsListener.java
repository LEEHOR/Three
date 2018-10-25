package net.coahr.three3.three.Popupwindow;

import android.widget.PopupWindow;

/**
 * Created by 李浩 on 2018/3/27.
 */

public interface PopuWindowsListener {

      void  subjectItem(PopupWindow popupWindow);
      void  singleRecording(PopupWindow popupWindow);
      void  totalRecording(PopupWindow popupWindow);
      void  stopRecording(PopupWindow popupWindow);
      void  exitAccess(PopupWindow popupWindow);
}

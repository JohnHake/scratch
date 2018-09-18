package com.intellij.sample.checkbox_party;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.Calendar;



public class CbpToolWindowFactory implements ToolWindowFactory {

  private JButton refreshToolWindowButton;
  private JButton hideToolWindowButton;
  private JLabel currentDate;
  private JLabel currentTime;
  private JLabel timeZone;
  private JPanel CbpToolWindowContent;
  private ToolWindow myToolWindow;


  public CbpToolWindowFactory() {
    hideToolWindowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        myToolWindow.hide(null);
      }
    });
    refreshToolWindowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        com.intellij.sample.checkbox_party.CbpToolWindowFactory.this.currentDateTime();
      }
    });
  }

  // Create the tool window content.
  public void createToolWindowContent(Project project, ToolWindow toolWindow) {
    myToolWindow = toolWindow;
    this.currentDateTime();
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(CbpToolWindowContent, "", false);
    toolWindow.getContentManager().addContent(content);

  }

  public void currentDateTime() {
    // Get current date and time
    Calendar instance = Calendar.getInstance();
    currentDate.setText(String.valueOf(instance.get(Calendar.DAY_OF_MONTH)) + "/"
                        + String.valueOf(instance.get(Calendar.MONTH) + 1) + "/" +
                        String.valueOf(instance.get(Calendar.YEAR)));
    currentDate.setIcon(new ImageIcon(getClass().getResource("/myToolWindow/Calendar-icon.png")));
    int min = instance.get(Calendar.MINUTE);
    String strMin;
    if (min < 10) {
      strMin = "0" + String.valueOf(min);
    } else {
      strMin = String.valueOf(min);
    }
    currentTime.setText(instance.get(Calendar.HOUR_OF_DAY) + ":" + strMin);
    currentTime.setIcon(new ImageIcon(getClass().getResource("/myToolWindow/Time-icon.png")));
    // Get time zone
    long gmt_Offset = instance.get(Calendar.ZONE_OFFSET); // offset from GMT in milliseconds
    String str_gmt_Offset = String.valueOf(gmt_Offset / 3600000);
    str_gmt_Offset = (gmt_Offset > 0) ? "GMT + " + str_gmt_Offset : "GMT - " + str_gmt_Offset;
    timeZone.setText(str_gmt_Offset);
    timeZone.setIcon(new ImageIcon(getClass().getResource("/myToolWindow/Time-zone-icon.png")));


  }

}

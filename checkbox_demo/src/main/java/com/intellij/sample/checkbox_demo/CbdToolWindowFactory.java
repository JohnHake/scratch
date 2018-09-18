package com.intellij.sample.checkbox_demo;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;

import javax.swing.*;
import java.awt.event.*;


public class CbdToolWindowFactory implements ToolWindowFactory {

  private JPanel bottomPanel;
  private JButton hideButton;
  private JButton refreshButton;

  private JPanel topPanel;
  private JCheckBox addErrorStripeMarkersCb;
  private JCheckBox showActionsInToolbarCb;

  private JPanel tripletSubpanel;
  private JCheckBox camelHumpsCb;
  private JCheckBox changeFontSizeZoomCheckBox;
  private JCheckBox enableDragNDropCheckBox;

  private JPanel softWrapsSubpanel;
  private JLabel softWrapLabel;

  private JPanel softWrapCbdanel;
  private JCheckBox useSoftWrapsCb;
  private JCheckBox useOriginalLineIndentCb;
  private JCheckBox showSoftWrapIndicatorCb;

  private JPanel CbdToolWindowContent;
  private ToolWindow CbdToolWindow;


  public CbdToolWindowFactory() {
    hideButton.addActionListener(new CbdHideListener());
    refreshButton.addActionListener(new CbdResetListener());
    addErrorStripeMarkersCb.addActionListener(new genericCheckboxListener());
    showActionsInToolbarCb.addActionListener(new genericCheckboxListener());
    camelHumpsCb.addActionListener(new genericCheckboxListener());
    changeFontSizeZoomCheckBox.addActionListener(new genericCheckboxListener());
    enableDragNDropCheckBox.addActionListener(new genericCheckboxListener());
    useSoftWrapsCb.addActionListener(new genericCheckboxListener());
    useOriginalLineIndentCb.addActionListener(new genericCheckboxListener());
    showSoftWrapIndicatorCb.addActionListener(new genericCheckboxListener());
  }

  // Create the tool window content.
  public void createToolWindowContent(Project project, ToolWindow toolWindow) {
    CbdToolWindow = toolWindow;
    this.resetPanel();
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(CbdToolWindowContent, "", false);
    toolWindow.getContentManager().addContent(content);
  }

  public class CbdHideListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      CbdToolWindow.hide(null);
    }
  }

  public class CbdResetListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      com.intellij.sample.checkbox_demo.CbdToolWindowFactory.this.resetPanel();
    }
  }

  public class genericCheckboxListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      System.out.println(e.getSource().toString());
    }
  }

  public void resetPanel() {
    addErrorStripeMarkersCb.setSelected(false);
    showActionsInToolbarCb.setSelected(false);
    camelHumpsCb.setSelected(false);
    changeFontSizeZoomCheckBox.setSelected(false);
    enableDragNDropCheckBox.setSelected(false);
    useSoftWrapsCb.setSelected(false);
    useOriginalLineIndentCb.setSelected(false);
    showSoftWrapIndicatorCb.setSelected(false);
  }
}

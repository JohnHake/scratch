// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.maxOpenPrj;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.ui.Messages;
import org.intellij.sdk.utils.SdkBalloonHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Listener to detect project open and close.
 * Depends on org.intellij.sdk.maxOpenPrj.ProjectCountingService
 * Depends on org.intellij.sdk.utils.SdkBalloonHelper
 */
public class ProjectOpenCloseListener implements ProjectManagerListener {
  private static final String MAX_OPEN_PROJ_DISCLAIM = "\nThis is an IntelliJ Platform SDK demo message.\n\n";

  /**
   * Invoked on project open.
   *
   * @param project opening project
   */
  @Override
  public void projectOpened(@NotNull Project project) {
    // Ensure this isn't part of testing
    if (ApplicationManager.getApplication().isUnitTestMode()) return;
    // Get the counting service
    ProjectCountingService projectCountingService = ServiceManager.getService(ProjectCountingService.class);
    // Increment the project count
    projectCountingService.incrProjectCount();
    // See if the total # of projects violates the limit.
    if (projectCountingService.projectLimitExceeded()) {
      // Transitioned to outside the limit
      String title = String.format("Opening Project \"%s\"", project.getName());
      String message = MAX_OPEN_PROJ_DISCLAIM + "The number of open projects exceeds the SDK plugin max_opened_projects limit.";
      SdkBalloonHelper balloonHelper = SdkBalloonHelper.getSdkBalloonHelper();
      balloonHelper.showBalloon(project, title, message);
    }
  }

  /**
   * Invoked on project close.
   * How do I find out which of several project windows is most visible (on top)? Assume each project is open in a separate window.
   * Context:
   * I have an application listener with topic ProjectManagerListener. In my implementation of projectClosed() , I want to show a popup about the project being closed. (But its window is now hidden.) There may be several remaining projects open, each in its own window, and all the project windows are overlapping and visible to some degree. I need to find the (remaining) project window that is on top to display the popup. Workaround: I could display the information in projectClosing(), but it still leaves me with the general question.
   * @param project closing project
   */
  @Override
  public void projectClosed(@NotNull Project project) {
    // Ensure this isn't part of testing
    if (ApplicationManager.getApplication().isUnitTestMode()) return;
    // Get the counting service
    ProjectCountingService projectCountingService = ServiceManager.getService(ProjectCountingService.class);
    // Was the count above the limit?
    boolean previouslyOverCount = projectCountingService.projectLimitExceeded();
    // Decrement the count because a project just closed
    projectCountingService.decrProjectCount();
    // See if the total # of projects no longer violates the limit.
    if (!projectCountingService.projectLimitExceeded() && previouslyOverCount) {
      // Transitioned to within the limit.
      String title = String.format("\"%s\" Has Been Closed", project.getName());
      String message = MAX_OPEN_PROJ_DISCLAIM + "The number of open projects is below the SDK plugin max_opened_projects limit.";
      SdkBalloonHelper balloonHelper = SdkBalloonHelper.getSdkBalloonHelper();
      balloonHelper.showBalloon(project, title, message);
    }
  }

}

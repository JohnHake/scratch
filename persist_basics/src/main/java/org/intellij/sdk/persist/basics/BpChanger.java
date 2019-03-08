/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.sdk.persist.basics;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.*;

/*
* This class exists solely to change the state of the persistent data, BpPersistComponent.BpState.
* It has NOTHING to do with persistence per se.
*/
public class BpChanger implements ProjectComponent {
  
  public BpChanger(Project project) {
  }
  
  /**
   * Invoked when the project corresponding to this component instance is opened.
   */
  @Override
  public void projectOpened() {
    // TODO BpPersistComponent can be null if nothing has been persisted yet.
    BpPersistComponent configService = ServiceManager.getService(BpPersistComponent.class);
    String prevStrState = configService.getData();
    configService.setData( prevStrState + "1" );
    StringBuilder displayStr = new StringBuilder( "Old BpState: " + prevStrState + "\n" + "New BpState: " + configService.getData() );
    Messages.showMessageDialog(displayStr.toString(), "BpPersistComponent.BpState", BpIcons.SDK_ICON);
  }
  
  /**
   * Invoked when the project corresponding to this component instance is closed.
   */
  @Override
  public void projectClosed() {
    this.projectOpened();
  }
  
  /**
   * Component should perform initialization and communication with other components in this method.
   */
  @Override
  public void initComponent() {}
  
  /**
   * Unique name of this component.
   */
  @NotNull
  public String getComponentName() {
    return getClass().getName();
  }
  
}

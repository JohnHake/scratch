/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.sdk.persist.basics;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.*;

/*
* This class exists solely to change the state of the persistent data, BpState.State.
* It has NOTHING to do with persistence per se.
*/
public class BpComponent implements ProjectComponent {
  
  public BpComponent(Project project) {
  }
  
  /**
   * Invoked when the project corresponding to this component instance is opened.
   */
  @Override
  public void projectOpened() {
    BpState configService = ServiceManager.getService(BpState.class);
    String prevStrState = configService.getFoo();
    configService.setFoo( prevStrState + "1" );
    StringBuilder displayStr = new StringBuilder( "Old State: " + prevStrState + "\n" + "New State: " + configService.getFoo() );
    Messages.showMessageDialog(displayStr.toString(), "BpState.State", Messages.getErrorIcon());
  }
  
  /**
   * Invoked when the project corresponding to this component instance is closed.
   */
  @Override
  public void projectClosed() {}
  
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

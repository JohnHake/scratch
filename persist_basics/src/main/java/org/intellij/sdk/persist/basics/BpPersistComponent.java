/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.sdk.persist.basics;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.*;

/* This is the class that demonstrates persistence.
*  It implements PersistentStateComponent for its state class BpPersistComponent.BpState.
*  It implements ApplicationComponent so IntelliJ Platform will manage the state.
*/
@State(
        name = "BpPersistComponent",
        storages = {
                @Storage(value = "$APP_CONFIG$/persistBasics.xml")
        }
)
public class BpPersistComponent implements BaseComponent, PersistentStateComponent<BpPersistComponent.BpState> {
  
  BpState myBpState = new BpState();
  
  static class BpState {
    public BpState() {}
    public String myData;
  }
  
  public String getData() {
    if (this.myBpState.myData == null) {
      setData("initial:");
    }
    return this.myBpState.myData;
  }
  
  public void setData(String newData) {
    this.myBpState.myData = newData;
  }
  
   // Application Component Overrides
  @Override
  public void disposeComponent() {}
  @Override
  public void initComponent() {}

  // Persistent BpState Component Overrides
  @Override
  @Nullable
  public BpState getState() {
    return myBpState; //Saves all public variables to disk.
  }
  @Override
  public void loadState(@NotNull BpState bpState) {
    this.myBpState = bpState; //restores bpState from disk
  }
  
  
}
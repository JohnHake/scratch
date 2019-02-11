/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.sdk.persist.basics;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.*;

/* This is the class that demonstrates persistence.
*  It implements PersistentStateComponent for its state class BpState.State.
*  It implements ApplicationComponent so IntelliJ Platform will manage the state.
*/
@State(
        name = "BpState",
        storages = {
                @Storage(value = "$APP_CONFIG$/persistBasics.xml")
        }
)
public class BpState implements BaseComponent, PersistentStateComponent<BpState.State> {
  
  State myState = new State();
  
  static class State {
    public State() {}
    public String foo;
  }
  
  public String getFoo() {
    if (this.myState.foo == null) {
      setFoo("initial:");
    }
    return this.myState.foo;
  }
  
  public void setFoo(String foo) {
    this.myState.foo = foo;
  }
  
   // Application Component Overrides
  @Override
  public void disposeComponent() {}
  @Override
  public void initComponent() {}

  // Persistent State Component Overrides
  @Override
  @Nullable
  public BpState.State getState() {
    return myState; //Saves all public variables to disk.
  }
  @Override
  public void loadState(@NotNull BpState.State state) {
    this.myState = state; //restores state from disk
  }
  
  
}
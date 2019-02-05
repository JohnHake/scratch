package com.intellij.sample.simplelang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.sample.simplelang.SimpleLanguage;
import org.jetbrains.annotations.*;

public class SimpleElementType extends IElementType {
  public SimpleElementType(@NotNull @NonNls String debugName) {
    super(debugName, SimpleLanguage.INSTANCE);
  }
}
/**
* Copyright 2018 Kinetics Northwest LLC.
*    See the LICENSE.TXT file for the specific language governing permissions and limitations.
*/

package com.jetbrains.sdk.StringReversal;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.*;

/**
 * Implements an intention action to reverse the letters in a Java string literal,
 * for example "I tell you." becomes ".uoy llet I".
 *
 * Intention actions are invoked by pressing Alt-Enter in the code editor at the
 * caret location where an intention is available, and can be enabled or disabled in
 * the "Intentions" settings dialog.
 *
 * @author John Hake
 */
@NonNls
public class ConditionalStringReverser extends PsiElementBaseIntentionAction implements IntentionAction {

  /**
   * If this action is applicable, returns the text to be shown in the list of
   * intention actions available.
   */
  @NotNull
  @Override
  public String getText() {
    return "Reverse the String Literal";
  }


  /**
   * Returns text for name of this family of intentions. It is used to externalize
   * "auto-show" state of intentions. Only one intention action is being provided,
   * so the family name is the same text as the intention action list entry.
   *
   * @return the intention family name.
   * @see ConditionalStringReverser#getText()
   */
  @NotNull
  @Override
  public String getFamilyName() {
    return getText();
  }


  /**
   * Checks whether this intention is available at the caret offset in file - the caret
   * must sit in a literal string. If this condition is met, this intention's entry is
   * shown in the available intentions list.
   *
   * @param project a reference to the Project object being edited.
   * @param editor  a reference to the object editing the project source
   * @param element a reference to the PSI element currently under the cursor
   * @return
   * <ul>
   * <li> true if the caret is in a literal string element, so this functionality
   * should be added to the intention menu.</li>
   * <li> false for all other types of caret positions</li>
   * </ul>
   * @see ConditionalStringReverser#checkElement(PsiElement)
   */
  public boolean isAvailable( @NotNull Project project, Editor editor, @Nullable PsiElement element ) {
    return checkElement( element );
  }


  /**
   * Reverses a string literal expression, creates a new element containing the reversed string, and
   * swaps the new (reversed) string literal in the Psi tree. Called when user selects this intention action
   * from the available intentions list.
   *
   *   @param  project   a reference to the Project object being edited.
   *   @param  editor    a reference to the editor object doing the editing
   *   @param  element   a reference to the PSI element currently under the cursor
   *   @throws IncorrectOperationException Thrown by underlying (Psi model) write action context
   *   when manipulation of the psi tree fails.
   *   @see ConditionalStringReverser#startInWriteAction()
   */
  @Override
  public void invoke(@NotNull Project project, Editor editor, @Nullable PsiElement element ) throws IncorrectOperationException {

    // Verify the element reference is a java token of type string literal
    if ( checkElement( element ) ) {

      // Get the parent of the (JavaToken) element, of type literal expression
      PsiLiteralExpression stringLiteralExpr = PsiTreeUtil.getParentOfType( element, PsiLiteralExpression.class, false );

      // There should always be a valid literal expression parent of the string literal element,
      // but check for anti-bugging purposes
      if (stringLiteralExpr != null ) {

        // Get the contents of the java literal token, and reverse it
        StringBuilder scratchTxt = new StringBuilder( stringLiteralExpr.getText() );
        scratchTxt.reverse();

        // Get the project's factory for creating psi elements
        final PsiElementFactory factory = JavaPsiFacade.getInstance( project ).getElementFactory();

        // Create a new psi literal expression element.
        // Note using the parent of the current literal expression element to provide context for the new element
        PsiExpression newLiteralExpr = factory.createExpressionFromText( scratchTxt.toString(), stringLiteralExpr.getParent() );


        // Substitute the new literal expression for the parent of the old java token string literal
        // Changes to the child java token contents are updated automatically
        // Option: check that result is not null. Throw an exception if it is null
        PsiLiteralExpression result = (PsiLiteralExpression) stringLiteralExpr.replace( newLiteralExpr );

      }
    }
  }


  /**
   * Indicates this intention action expects the Psi framework to provide the write action
   * (reversible transaction) context for any changes.
   *
   * @return <ul>
   * <li> true if the intention requires a write action context to be provided</li>
   * <li> false if this intention action will start a write action</li>
   * </ul>
   */
  @Override
  public boolean startInWriteAction() {
    return true;
  }


  /**
   *   Encapsulates verifying the element is something this
   *   intention action could act upon: a literal string.
   *
   *   @param  element   a reference to the PSI element currently under the cursor
   *   @return
   *       <ul>
   *         <li> true if the PsiElement is a writable instance of JavaTokenType.STRING_LITERAL,
   *         and is the child of type PsiLiteralExpression. </li>
   *         <li> false for all other types of PsiElement</li>
   *       </ul>
   */
  private boolean checkElement( @Nullable PsiElement element ) {

    boolean amAvailable = false;

    if ( element != null ) {
      if ( element.isWritable() ) {                     // Must be able to modify it
        if ( element instanceof PsiJavaToken ) {        // Must be a PsiJavaToken

          // Cast the element to a Java Token
          final PsiJavaToken token = (PsiJavaToken) element;

          // Must be a string literal
          if ( token.getTokenType() == JavaTokenType.STRING_LITERAL ) {

            // This intention action applies to caret location
            amAvailable = true;

          }
        }
      }
    }
    return amAvailable;
  }


}

package com.intellij.sdk.phpstorm;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.SmartList;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.AssignmentExpression;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4;
import com.jetbrains.php.lang.psi.visitors.PhpRecursiveElementVisitor;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.coverage.gnu.trove.THashMap;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class PhpStaticFactoryTypeProvider extends CompletionContributor implements PhpTypeProvider4 {
  
  private static final Key< CachedValue<Map<String, Map<String, String> >> > STATIC_FACTORY_TYPE_MAP =
        new Key<CachedValue<Map<String, Map<String, String>>>>("STATIC_FACTORY_TYPE_MAP");
  
  
  @Override
  public char getKey() {
    return 'S';
  }
  
  @Nullable
  @Override
  public PhpType getType( PsiElement e ) {
    if ( e instanceof MethodReference && ( (MethodReference) e ).isStatic() ) {

      Map< String, Map< String, String > > methods = getStaticMethodTypesMap( e.getProject() );
      String refSignature = ( (MethodReference) e ).getSignature();
      if ( methods.containsKey( refSignature ) ) {
        PsiElement[] parameters = ( (MethodReference) e ).getParameters();

        if ( parameters.length > 0 ) {
          PsiElement parameter = parameters[0];
          if ( parameter instanceof StringLiteralExpression ) {
            PhpType typePhp = new PhpType();
            return typePhp.add(parameter);
          }
        }
      }
    }
    return null;
  }
  
  @Nullable
  @Override
  public PhpType complete( String s, Project project ) {
    return null;
  }
  
  @Override
  public Collection< ? extends PhpNamedElement > getBySignature( String s, Set< String > set, int i, Project project ) {
    Map<String, Map<String, String>> methods = getStaticMethodTypesMap(project);
    int dot = s.lastIndexOf('.');
    String refSignature = s.substring(0, dot);
    Map<String, String> types = methods.get(refSignature);
    if (types != null) {
      String type = types.get(s.substring(dot + 1));
      if (type != null) return PhpIndex.getInstance( project).getAnyByFQN( type);
    }
    return Collections.emptySet();
  }
  
  synchronized Map<String, Map<String, String>> getStaticMethodTypesMap(final Project project) {
    CachedValue<Map<String, Map<String, String>>> myStaticMethodTypesMap = project.getUserData(STATIC_FACTORY_TYPE_MAP);
    if (myStaticMethodTypesMap == null) {
      myStaticMethodTypesMap = CachedValuesManager.getManager( project).createCachedValue(
            new CachedValueProvider<Map<String, Map<String, String>>>() {
              @Nullable
              @Override
              public Result<Map<String, Map<String, String>>> compute() {
                Map<String, Map<String, String>> map = new THashMap<String, Map<String, String>>();
                Collection< Variable > variables = getVariables( project, "STATIC_METHOD_TYPES");
                for (Variable variable : variables) {
                  if (!"\\PHPSTORM_META\\".equals(variable.getNamespaceName())) continue;
                  PsiElement parent = variable.getParent();
                  if (parent instanceof AssignmentExpression ) {
                    PsiElement temp = parent;
                    PhpPsiElement value = ((AssignmentExpression)parent).getValue();
                    if (value instanceof ArrayCreationExpression) {
                      for (ArrayHashElement element : ((ArrayCreationExpression)value).getHashElements()) {
                        PhpPsiElement match = element.getKey();
                        if (match instanceof MethodReference) {
                          String matchSignature = ((MethodReference)match).getSignature();
                          Map<String, String> types = map.get(matchSignature);
                          if (types == null) {
                            types = new THashMap<String, String>();
                            map.put(matchSignature, types);
                          }
                          PhpPsiElement val = element.getValue();
                          if (val instanceof ArrayCreationExpression) {
                            PhpPsiElement child = val.getFirstPsiChild();
                            while (child != null) {
                              if (child.getFirstPsiChild() instanceof BinaryExpression) {
                                BinaryExpression binary = ((BinaryExpression)child.getFirstPsiChild());
                                if (binary.getOperation().getNode().getElementType() == PhpTokenTypes.kwINSTANCEOF) {
                                  PsiElement leftOperand = binary.getLeftOperand();
                                  PsiElement rightOperand = binary.getRightOperand();
                                  if (leftOperand instanceof StringLiteralExpression && rightOperand != null) {
                                    types.put(((StringLiteralExpression)leftOperand).getContents(), rightOperand.getText());
                                  }
                                }
                              }
                              child = child.getNextPsiSibling();
                            }
                          }
                        }
                      }
                    }
                  }
                }
                return CachedValueProvider.Result.create(map, getMetaFile(project));
              }
            }, false);
      project.putUserData(STATIC_FACTORY_TYPE_MAP, myStaticMethodTypesMap);
    }
    return myStaticMethodTypesMap.getValue();
  }
  
  private static Collection<Variable> getVariables(Project project, final String key) {
    PsiFile file = getMetaFile( project);
    final Collection<Variable> result = new SmartList<Variable>();
    if (file instanceof PhpFile ) {
      //AG not the most elegant way - but still an allowed usage.
      //noinspection deprecation
      file.accept(new PhpRecursiveElementVisitor() {
        @Override
        public void visitPhpAssignmentExpression(AssignmentExpression assignmentExpression) {
          PhpPsiElement variable = assignmentExpression.getVariable();
          if (variable instanceof Variable) {
            if (((Variable)variable).getNameCS().equals(key)) {
              result.add((Variable)variable);
            }
          }
        }
      });
    }
    return result;
  }
  
  public static PsiFile getMetaFile(Project project) {
    VirtualFile metaFile = LocalFileSystem.getInstance().findFileByPath( project.getBasePath() + File.separatorChar + ".phpstorm.meta.php");
    return metaFile != null ? PsiManager.getInstance( project).findFile( metaFile) : null;
  }
  
  @Override
  public void fillCompletionVariants( CompletionParameters parameters, CompletionResultSet result) {
    final ProcessingContext context = new ProcessingContext();
    PsiElement position = parameters.getPosition();
    if (parameters.getCompletionType() == CompletionType.BASIC &&
              psiElement().withParent(StringLiteralExpression.class).withSuperParent(2, ParameterList.class)
                          .accepts(position, context)) {
      ParameterListOwner parameterListOwner = PsiTreeUtil.getStubOrPsiParentOfType( position, ParameterListOwner.class);
      if (parameterListOwner instanceof MethodReference && ((MethodReference)parameterListOwner).isStatic()) {
        
        Map<String, String> map = getStaticMethodTypesMap(position.getProject()).get(((MethodReference)parameterListOwner).getSignature());
        for (String s : map.keySet()) {
          result.addElement( LookupElementBuilder.create( s).appendTailText( map.get( s), true));
        }
      }
    }
    super.fillCompletionVariants(parameters, result);
  }
  
}

/*
 * SonarQube CSS / Less Plugin
 * Copyright (C) 2013-2016 Tamas Kende and David RACODON
 * mailto: kende.tamas@gmail.com and david.racodon@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.css.tree.impl.less;

import com.google.common.collect.Iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

import org.sonar.css.tree.impl.SyntaxList;
import org.sonar.css.tree.impl.TreeImpl;
import org.sonar.plugins.css.api.tree.Tree;
import org.sonar.plugins.css.api.tree.css.SyntaxToken;
import org.sonar.plugins.css.api.tree.less.LessMixinParameterTree;
import org.sonar.plugins.css.api.tree.less.LessMixinParametersTree;
import org.sonar.plugins.css.api.visitors.DoubleDispatchVisitor;

public class LessMixinParametersTreeImpl extends TreeImpl implements LessMixinParametersTree {

  private final SyntaxToken openParenthesis;
  private final SyntaxToken closeParenthesis;
  private final SyntaxList<LessMixinParameterTree> parameterSyntaxList;
  private final List<LessMixinParameterTree> parameters;

  public LessMixinParametersTreeImpl(SyntaxToken openParenthesis, @Nullable SyntaxList<LessMixinParameterTree> parameterSyntaxList, SyntaxToken closeParenthesis) {
    this.openParenthesis = openParenthesis;
    this.closeParenthesis = closeParenthesis;
    this.parameterSyntaxList = parameterSyntaxList;
    this.parameters = parameterSyntaxList != null ? parameterSyntaxList.allElements(LessMixinParameterTree.class) : new ArrayList<>();
  }

  @Override
  public Kind getKind() {
    return Kind.LESS_MIXIN_PARAMETERS;
  }

  @Override
  public Iterator<Tree> childrenIterator() {
    if (parameterSyntaxList != null) {
      return Iterators.concat(
        Iterators.singletonIterator(openParenthesis),
        parameterSyntaxList.all().iterator(),
        Iterators.singletonIterator(closeParenthesis));
    } else {
      return Iterators.forArray(openParenthesis, closeParenthesis);
    }
  }

  @Override
  public void accept(DoubleDispatchVisitor visitor) {
    visitor.visitLessMixinParameters(this);
  }

  @Override
  public SyntaxToken openParenthesis() {
    return openParenthesis;
  }

  @Override
  public List<LessMixinParameterTree> parameters() {
    return parameters;
  }

  @Override
  public SyntaxToken closeParenthesis() {
    return closeParenthesis;
  }

}

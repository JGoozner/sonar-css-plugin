/*
 * SonarQube CSS Plugin
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
package org.sonar.css.tree.impl;

import com.google.common.collect.Iterators;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

import org.sonar.css.model.Vendor;
import org.sonar.css.model.pseudo.pseudofunction.StandardPseudoFunction;
import org.sonar.css.model.pseudo.pseudofunction.StandardPseudoFunctionFactory;
import org.sonar.plugins.css.api.tree.IdentifierTree;
import org.sonar.plugins.css.api.tree.PseudoFunctionTree;
import org.sonar.plugins.css.api.tree.SyntaxToken;
import org.sonar.plugins.css.api.tree.Tree;
import org.sonar.plugins.css.api.visitors.DoubleDispatchVisitor;

public class PseudoFunctionTreeImpl extends CssTree implements PseudoFunctionTree {

  private final SyntaxToken prefix;
  private final IdentifierTree function;
  private final SyntaxToken openParenthesis;
  private final List<Tree> parameterElements;
  private final SyntaxToken closeParenthesis;
  private final Vendor vendor;
  private final StandardPseudoFunction standardFunction;

  public PseudoFunctionTreeImpl(SyntaxToken prefix, IdentifierTree function, SyntaxToken openParenthesis, @Nullable List<Tree> parameterElements,
    SyntaxToken closeParenthesis) {
    this.prefix = prefix;
    this.function = function;
    this.openParenthesis = openParenthesis;
    this.parameterElements = parameterElements;
    this.closeParenthesis = closeParenthesis;

    this.vendor = setVendor();
    this.standardFunction = setStandardFunction();
  }

  @Override
  public Kind getKind() {
    return Kind.PSEUDO_FUNCTION;
  }

  @Override
  public Iterator<Tree> childrenIterator() {
    if (parameterElements != null) {
      return Iterators.concat(
        Iterators.forArray(prefix, function, openParenthesis),
        parameterElements.iterator(),
        Iterators.singletonIterator(closeParenthesis));
    } else {
      return Iterators.forArray(prefix, function, openParenthesis, closeParenthesis);
    }
  }

  @Override
  public void accept(DoubleDispatchVisitor visitor) {
    visitor.visitPseudoFunction(this);
  }

  @Override
  public SyntaxToken prefix() {
    return prefix;
  }

  @Override
  public IdentifierTree function() {
    return function;
  }

  @Override
  public boolean isVendorPrefixed() {
    return vendor != null;
  }

  @Override
  public Vendor vendor() {
    return vendor;
  }

  @Override
  public StandardPseudoFunction standardFunction() {
    return standardFunction;
  }

  @Override
  public SyntaxToken openParenthesis() {
    return openParenthesis;
  }

  @Override
  public SyntaxToken closeParenthesis() {
    return closeParenthesis;
  }

  @Override
  public List<Tree> parameterElements() {
    return parameterElements;
  }

  private Vendor setVendor() {
    for (Vendor v : Vendor.values()) {
      if (function.text().toLowerCase(Locale.ENGLISH).startsWith(v.getPrefix())) {
        return v;
      }
    }
    return null;
  }

  private StandardPseudoFunction setStandardFunction() {
    String name = function.text();
    if (isVendorPrefixed()) {
      name = name.substring(vendor.getPrefix().length());
    }
    return StandardPseudoFunctionFactory.getByName(name);
  }

}

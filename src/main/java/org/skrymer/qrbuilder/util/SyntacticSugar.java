package org.skrymer.qrbuilder.util;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

/**
 * Some syntactic sugar
 */
public class SyntacticSugar {

  public static void throwIllegalArgumentExceptionIfEmpty(String parameter, String parameterName) {
    if (StringUtils.isEmpty(parameter)) {
      throw new IllegalArgumentException("Parameter " + parameter + " cannot be empty");
    }
  }

  public static void throwIf(Supplier<Boolean> condition, Supplier<RuntimeException> exceptionSupplier) {
    if(condition.get()){
      throw exceptionSupplier.get();
    }
  }
}

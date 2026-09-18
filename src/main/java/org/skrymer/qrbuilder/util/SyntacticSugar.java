package org.skrymer.qrbuilder.util;

/**
 * Some syntactic sugar
 */
public class SyntacticSugar {

  public static void throwIllegalArgumentExceptionIfEmpty(String parameter, String parameterName) {
    if (parameter == null || parameter.isEmpty()) {
      throw new IllegalArgumentException("Parameter " + parameterName + " cannot be empty");
    }
  }
}

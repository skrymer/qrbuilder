package org.skrymer.qrbuilder.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Some syntactic sugar
 */
public class SyntacticSugar {

  public static void throwIllegalArgumentExceptionIfEmpty(String parameter, String parameterName) {
    if (StringUtils.isEmpty(parameter)) {
      throw new IllegalArgumentException("Parameter " + parameter + " cannot be empty");
    }
  }
}

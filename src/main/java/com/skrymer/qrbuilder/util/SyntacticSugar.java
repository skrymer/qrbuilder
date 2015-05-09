package com.skrymer.qrbuilder.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by skrymer on 10/05/15.
 */
public class SyntacticSugar {

    public static void throwIllegalArgumentExceptionIfEmpty(String parameter, String parameterName){
        if(StringUtils.isEmpty(parameter)){
            throw new IllegalArgumentException("Parameter " + parameter + " cannot be empty");
        }
    }
}

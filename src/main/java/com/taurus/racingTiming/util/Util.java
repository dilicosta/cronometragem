package com.taurus.racingTiming.util;

import java.awt.Dimension;
import org.apache.commons.validator.GenericValidator;

/**
 *
 * @author Diego
 */
public class Util {

    public static String dimensionToString(Dimension dimension) {
        if (dimension != null) {
            return dimension.width + " x " + dimension.height;
        } else {
            return null;
        }
    }

    public static Dimension stringToDimension(String string) {
        if (GenericValidator.isBlankOrNull(string)) {
            return null;
        } else {
            String[] resolucaoStr = string.split("x");
            if (resolucaoStr.length != 2) {
                return null;
            }
            if (!GenericValidator.isInt(resolucaoStr[0].trim()) || !GenericValidator.isInt(resolucaoStr[1].trim())) {
                return null;
            }
            return new Dimension(Integer.valueOf(resolucaoStr[0].trim()), Integer.valueOf(resolucaoStr[1].trim()));
        }
    }
}

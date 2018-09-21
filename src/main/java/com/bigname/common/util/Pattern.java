package com.bigname.common.util;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public enum Pattern {
    NUMERIC("0-9"),
    LOWER_ALPHA("a-z"),
    UPPER_ALPHA("A-Z"),
    ALPHA(LOWER_ALPHA, UPPER_ALPHA),
    SPACE("\\s"),
    UNDERSCORE("_"),
    HYPHEN("-");

    String regEx = "";

    Pattern(String regEx) {
        this.regEx = regEx;
    }

    Pattern(Pattern... patterns) {
        for (Pattern pattern : ConversionUtil.toList(patterns)) {
            regEx += pattern.regEx;
        }
    }

    public String getRegEx() {
        return "[" + this.regEx + "]";
    }

    public String getRegEx(Object... additionalPatterns) {
        String regEx = this.regEx;
        for (Object pattern : ConversionUtil.toList(additionalPatterns)) {
            regEx += pattern instanceof String ? (String) pattern : ((Pattern) pattern).regEx;
        }

        return "[" + regEx + "]";
    }

    public static String buildRegEx(Object... patterns) {
        StringBuilder regEx = new StringBuilder();
        for (Object pattern : ConversionUtil.toList(patterns)) {
            regEx.append(pattern instanceof String ? (String) pattern : ((Pattern) pattern).regEx);
        }

        return "[" + regEx + "]";
    }
}

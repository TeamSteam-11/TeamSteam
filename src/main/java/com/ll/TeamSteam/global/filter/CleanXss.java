package com.ll.TeamSteam.global.filter;

public class CleanXss {
    /**
     * XSS 및 Sql Injection Replace
     */
    public static String replaceXssAnsSqlInjection(String value) {
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']","\"\"");
        value = value.replaceAll("script","");
        value = value.replaceAll("/((\\%3D)|(=))[^\\n]*((\\%27)|(\')|(\\-\\-)|(\\%3B)|(;))/i","");
        value = value.replaceAll("/((\\%27)|(\'))union/ix","");
        value = value.replaceAll("/\\w*((\\%27)|(\\'))((\\%6F)|o|(\\%4F))((\\%72)|r|(\\%52))/ix","");
        value = value.replaceAll("insert|update|delete|having|drop|(\'|%27).(and|or).(\'|%27)|(\'|%27).%7C{0,2}|%7C{2}","");
        value = value.replaceAll("/((\\%3C)|<)((\\%69)|i|(\\%49))((\\%6D)|m|(\\%4D))((\\%67)|g|(\\%47))[^\n]+((\\%3E)|>)/I","");

        return value;
    }
}

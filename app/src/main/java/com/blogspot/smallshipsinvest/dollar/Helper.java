package com.blogspot.smallshipsinvest.dollar;

import android.content.Context;

import java.text.DecimalFormat;

public class Helper {

    public static String rubleFormat(double a, Currency cur) {

        DecimalFormat df;

        switch (cur) {

            case EUR:
                df = new DecimalFormat("\u20BD#.#/€");
                break;

            case USD: default:
                df = new DecimalFormat("\u20BD#.#/$");
                break;

        }

        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        return df.format(a);

    }

    public static boolean isDigitOrDot(char c) {
        return (c >= '0' && c <= '9') || c == '.';
    }

//    public static boolean isDigitOrDotOrSpaceOrColon(char c) {
//        return (c >= '0' && c <= '9') || c == '.' || c == ' ' || c == ':';
//    }

    public static boolean checkRateFormat(String s) {
        boolean answer = true;
        char[] ca = s.toCharArray();
        if (ca.length == 0) {
            answer = false;
        }
        for (char c : ca) {
            if (!isDigitOrDot(c)) {
                answer = false;
                break;
            }
        }
        return answer;
    }

    public static String getMonth(Context context, int month) {

        String answer;

        switch (month) {
            case 0:
                answer = context.getResources().getString(R.string.jan);
                break;
            case 1:
                answer = context.getResources().getString(R.string.feb);
                break;
            case 2:
                answer = context.getResources().getString(R.string.mar);
                break;
            case 3:
                answer = context.getResources().getString(R.string.apr);
                break;
            case 4:
                answer = context.getResources().getString(R.string.may);
                break;
            case 5:
                answer = context.getResources().getString(R.string.jun);
                break;
            case 6:
                answer = context.getResources().getString(R.string.jul);
                break;
            case 7:
                answer = context.getResources().getString(R.string.aug);
                break;
            case 8:
                answer = context.getResources().getString(R.string.sep);
                break;
            case 9:
                answer = context.getResources().getString(R.string.oct);
                break;
            case 10:
                answer = context.getResources().getString(R.string.nov);
                break;
            case 11:
                answer = context.getResources().getString(R.string.dec);
                break;
            default:
                answer = "ERR";
                break;
        }

        return answer;

    }

//    public static boolean checkTimeFormat(String s) {
//        boolean answer = true;
//        char[] ca = s.toCharArray();
//        if (ca.length == 0) {
//            answer = false;
//        }
//        for (char c : ca) {
//            if (!isDigitOrDotOrSpaceOrColon(c)) {
//                answer = false;
//                break;
//            }
//        }
//        return answer;
//    }

}

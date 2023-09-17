package com.blogspot.smallshipsinvest.dollar;

//import android.annotation.SuppressLint;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
//import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String ERROR = "Error";

    private static final String USD_SITE = "https://www.profinance.ru/currency_usd.asp";
    private static final double USD_MIDMARKET_CORRECTOR = 0.005d;

    private static final String EUR_SITE = "https://www.profinance.ru/currency_eur.asp";
    private static final double EUR_MIDMARKET_CORRECTOR = 0.01d;

    private static final String TIME_SIGN = "<td class=cell align=center colspan=\"2\"><b>";
    private static final String TIME_RIGHT_SIGN = "</b>";

    private static final String RATE_SIGN = "<td class=cell align=center colspan=\"2\"><font " +
            "color=\"Red\"><b>";
    private static final String RATE_RIGHT_SIGN = "</b></font><b>";


    private static TextView rateView;
    private static TextView timeView;

    private static Currency currency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rateView = (TextView) findViewById(R.id.rate);
        timeView = (TextView) findViewById(R.id.time);

        currency = Currency.USD;

        Typeface typeFaceRate = Typeface.createFromAsset(getAssets(), "calibrib.ttf");
        Typeface typeFaceTime = Typeface.createFromAsset(getAssets(), "calibri.ttf");

        rateView.setTypeface(typeFaceRate);
        timeView.setTypeface(typeFaceTime);

//        if (!isMicexTradingHour()) {
//            Toast.makeText(this, getResources().getText(R.string.warning), Toast.LENGTH_LONG)
//                    .show();
//        }

        (new RubleAsyncTask()).execute(currency);

    }

    public void runRefresh(View view) {
        timeView.setText(getResources().getText(R.string.wait));
        (new RubleAsyncTask()).execute(currency);
    }

    public void runUSD(View view) {
        timeView.setText(getResources().getText(R.string.wait));
        currency = Currency.USD;
        (new RubleAsyncTask()).execute(currency);
    }

    public void runEUR(View view) {
        timeView.setText(getResources().getText(R.string.wait));
        currency = Currency.EUR;
        (new RubleAsyncTask()).execute(currency);
    }

    public void runInfo(View view) {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    public void runExit(View view) {
        finish();
    }

//    public static boolean isMicexTradingHour() {
//
//        boolean answer;
//
//        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
//        Calendar ca = Calendar.getInstance();
//        ca.setTime(new Date());
//        ca.setTimeZone(tz);
//        int mins = ca.get(Calendar.HOUR_OF_DAY) * 60 + ca.get(Calendar.MINUTE);
//        int dayOfWeek = ca.get(Calendar.DAY_OF_WEEK);
//
//        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
//            answer = false;
//        } else if (dayOfWeek == Calendar.MONDAY && mins < 600) {
//            answer = false;
//        } else {
//            answer = true;
//        }
//
//        return answer;
//
//    }

    @SuppressLint("StaticFieldLeak")
    private class RubleAsyncTask extends AsyncTask<Currency, Void, String[]> {

//        private static final String ERROR = "Error";

        @Override
        protected String[] doInBackground(Currency... params) {
            String[] result = getRate(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            timeView.setText(result[0]);
            rateView.setText(result[1]);
        }

        private String[] getRate(Currency cur) {

            String site;
            double corrector;

            switch (cur) {

                case EUR:
                    site = EUR_SITE;
                    corrector = EUR_MIDMARKET_CORRECTOR;
                    break;

                case USD:
                default:
                    site = USD_SITE;
                    corrector = USD_MIDMARKET_CORRECTOR;
                    break;

            }

            String[] result = readDoubleData(site, TIME_SIGN, TIME_RIGHT_SIGN, RATE_SIGN,
                    RATE_RIGHT_SIGN);

            result[0] = getLocalTimeFromSource(result[0]);

            if (!result[0].equals(ERROR) && Helper.checkRateFormat(result[1])) {
                result[1] = Helper.rubleFormat(Double.parseDouble(result[1]) + corrector, cur);
            } else {
                result[0] = getResources().getString(R.string.not_available);
                result[1] = getResources().getString(R.string.not_available);
            }

            return result;

        }

    }

    private String getLocalTimeFromSource(String sourceTime) {

        String answer;

        TimeZone sourceTimeZone = TimeZone.getTimeZone("Europe/Moscow");
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        sourceDateFormat.setTimeZone(sourceTimeZone);
        Calendar calendar = Calendar.getInstance();
        Context context = getApplicationContext();

        try {

            calendar.setTime(sourceDateFormat.parse(sourceTime));

            String minutes = "";
            if (calendar.get(Calendar.MINUTE) < 10) {
                minutes += "0";
            }
            minutes += Integer.toString(calendar.get(Calendar.MINUTE));

            answer = Integer.toString(calendar.get(Calendar.YEAR)) + "-" + Helper.getMonth
                    (context, calendar.get(Calendar.MONTH)) + "-" + Integer
                    .toString(calendar.get
                            (Calendar.DATE)) + "  " + Integer.toString(calendar.get(Calendar
                    .HOUR_OF_DAY)) + ":" + minutes;

        } catch (ParseException e) {
            e.printStackTrace();
            answer = ERROR;
        }

        return answer;

    }

    private static String[] readDoubleData(String site, String sign1, String rightSign1, String
            sign2, String rightSign2) {

        String[] result = {ERROR , ERROR};

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader((new URL(site))
                    .openStream()));

            String s;

            while ((s = reader.readLine()) != null) {
                if (s.contains(sign1)) {
                    int leftIndex = s.indexOf(sign1) + sign1.length();
                    int rightIndex = s.indexOf(rightSign1);
                    if (rightIndex != -1) {
                        result[0] = s.substring(leftIndex, rightIndex);
                    }
                    break;
                }
            }

            while ((s = reader.readLine()) != null) {
                if (s.contains(sign2)) {
                    int leftIndex = s.indexOf(sign2) + sign2.length();
                    int rightIndex = s.indexOf(rightSign2);
                    if (rightIndex != -1) {
                        result[1] = s.substring(leftIndex, rightIndex);
                    }
                    break;
                }
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

//    public static String readData(String site, String sign, String rightSign) {
//
//        String result = ERROR;
//
//        try {
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader((new URL(site))
//                    .openStream()));
//
//            String s;
//
//            while ((s = reader.readLine()) != null) {
//                if (s.contains(sign)) {
//                    result = s.substring(sign.length());
//                    break;
//                }
//            }
//
//            if (!result.equals(ERROR)) {
//
//                int i = result.indexOf(rightSign);
//
//                if (i != -1) {
//                    result = result.substring(0, i);
//                } else {
//                    result = ERROR;
//                }
//
//            }
//
//            reader.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return result;
//
//    }

}

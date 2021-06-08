package com.myappcompany.rajan.healthdepot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.myappcompany.rajan.healthdepot.model.Verhoeff;

import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static boolean validateAadharNumber(String aadharNumber){
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if(isValidAadhar){
            isValidAadhar = Verhoeff.validateVerhoeff(aadharNumber);
        }
        return isValidAadhar;
    }

    public static String[] getAadharCredentialsFromQR(String input) {

        String[] result = new String[5];

        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();

            myParser.setInput(new StringReader(input));

            int event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.END_TAG:
                        if (name.equals("PrintLetterBarcodeData")) {
                            result[0] = myParser.getAttributeValue(null, "uid");
                            result[1] = myParser.getAttributeValue(null, "name");
                            result[2] = myParser.getAttributeValue(null, "gender");
                            result[3] = myParser.getAttributeValue(null, "yob");
                            result[4] = myParser.getAttributeValue(null, "co")
                                    + ", " + myParser.getAttributeValue(null, "lm")
                                    + ", " + myParser.getAttributeValue(null, "loc")
                                    + ", " + myParser.getAttributeValue(null, "vtc")
                                    + ", " + myParser.getAttributeValue(null, "po")
                                    + ", " + myParser.getAttributeValue(null, "dist")
                                    + ", " + myParser.getAttributeValue(null, "state")
                                    + ", " + myParser.getAttributeValue(null, "pc");

                            if(validateAadharNumber(result[0])) {
                                return result;
                            }
                            return null;
                        }
                        break;
                }
                event = myParser.next();
            }
        }
        catch (Exception e) {
            return null;
        }
        return null;
    }

    private boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isLoggedIn) {
                    //Home screen
                }
                else {
                    Intent i = EntryActivity.newIntent(MainActivity.this);
                    startActivity(i);
                }
            }
        }, 3000);
    }
}
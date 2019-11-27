package com.example.swimmingpoolmonitor.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.swimmingpoolmonitor.MainActivity;
import com.example.swimmingpoolmonitor.R;
import com.example.swimmingpoolmonitor.Setting;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        int index = getArguments().getInt(ARG_SECTION_NUMBER);

        //Monitor Stream View
        if (index == 1) {
            View root = inflater.inflate(R.layout.fragment_monitor, container, false);
            //Set Date
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            TextView dateTextView = (TextView)root.findViewById(R.id.DateField);
            dateTextView.setText("Date: " + dateFormat.format(date));

            //Set Frame Rate
            TextView fpsTextView = (TextView)root.findViewById(R.id.frameRateField);
            fpsTextView.setText("Frame Rate: 1 fps");

            final WebView myWebView = (WebView) root.findViewById(R.id.webView1);

            String frameVideo =  "http://192.168.43.91:8080/stream_simple.html";
            myWebView.setWebChromeClient(new WebChromeClient());
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUseWideViewPort(true);
            myWebView.setInitialScale(1);
            myWebView.loadUrl(frameVideo);

            Button button = (Button) root.findViewById(R.id.archiveCurrentButton);
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Toast.makeText(getActivity(), "Data Saved", Toast.LENGTH_LONG).show();

                }
            });
            return root;
        //Archive Video View
        } else if (index == 2) {
            View root = inflater.inflate(R.layout.fragment_archive, container, false);

            CalendarView simpleCalendarView = (CalendarView) root.findViewById(R.id.calendarView);
            final TextView dateField = (TextView) root.findViewById(R.id.dateEntryField);
            simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                    dateField.setText(dayOfMonth + "-" + month + "-" + year);
                }
            });
            return root;
        //Settings View
        } else {
            View root = inflater.inflate(R.layout.fragment_settings, container, false);

            //Load in settings
            Context context = getContext();
            String settingString;
            ObjectMapper mapper = new ObjectMapper();
            Setting initSettings = new Setting();

            try {
                System.out.println(context.getFilesDir().toString() + "/poolSettings.json");
                FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), "poolSettings.json"));
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                settingString = sb.toString();

                fis.close();

                System.out.println(settingString);

                initSettings = mapper.readValue(settingString, Setting.class);

                System.out.println(initSettings.getDesiredBitRate());

            } catch (IOException ex) {
                System.out.println("no input file");
                ex.printStackTrace();
            }

            final Setting settings = initSettings;

            CheckBox drownRadio = (CheckBox) root.findViewById(R.id.potDrownButton);
            CheckBox unsafeRadio = (CheckBox) root.findViewById(R.id.unsafePlayButton);
            CheckBox afterRadio = (CheckBox) root.findViewById(R.id.afterHoursButton);
            final CheckBox[] monitorArray = {drownRadio, unsafeRadio, afterRadio};

            RadioButton p240 = (RadioButton) root.findViewById(R.id.button240);
            RadioButton p360 = (RadioButton) root.findViewById(R.id.button360);
            RadioButton p480 = (RadioButton) root.findViewById(R.id.button480);
            RadioButton p720 = (RadioButton) root.findViewById(R.id.button720);
            final RadioButton[] bitRateArray = {p240, p360, p480, p720};

            boolean[] monitorTypes = settings.getMonitoring();
            for(int i = 0; i < monitorTypes.length; i++) {
                if (monitorTypes[i] == true) {
                    monitorArray[i].setChecked(true);
                }
            }

            final CheckBox alertRadio = (CheckBox) root.findViewById(R.id.emergButton);

            if (settings.getAlert() == true) {
                alertRadio.setChecked(true);
            }

            int bitRate = settings.getDesiredBitRate();

            bitRateArray[bitRate].setChecked(true);


            Button button = (Button) root.findViewById(R.id.settingsButton);
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    boolean[] newMonitorTypes = new boolean[3];
                    for(int i = 0; i < newMonitorTypes.length; i++) {
                        if (monitorArray[i].isChecked()) {
                            newMonitorTypes[i] = true;
                        } else {
                            newMonitorTypes[i] = false;
                        }
                    }
                    boolean authorities = false;
                    if (alertRadio.isChecked()) {
                        authorities = true;
                    } else {
                        authorities = false;
                    }

                    int bitRate = 0;
                    for (int i = 0; i < bitRateArray.length; i++) {
                        if (bitRateArray[i].isChecked()) {
                            bitRate = i;
                        }
                    }

                    settings.saveSettings(newMonitorTypes, authorities, bitRate);

                    Context context = getContext();
                    File file = new File(context.getFilesDir(), "poolSettings.json");

                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        mapper.writeValue(file, settings);
                    } catch(Exception e) {
                        Toast.makeText(getActivity(), "Failed to Save.", Toast.LENGTH_LONG).show();
                    }

                    settings.saveSettings(newMonitorTypes, authorities, bitRate);

                    Toast.makeText(getActivity(), "Settings Saved", Toast.LENGTH_LONG).show();
                }
            });
            return root;
        }
    }

}
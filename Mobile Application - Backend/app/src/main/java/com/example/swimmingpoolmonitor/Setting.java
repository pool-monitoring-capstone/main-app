package com.example.swimmingpoolmonitor;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Setting {

    private boolean[] monitoring;
    private boolean allowAuthorities;
    private int desiredBitRate;
    private String fileName = "poolSettings.json";

    public Setting() {
        //default settings
        monitoring = new boolean[3];
        monitoring[0] = true;
        monitoring[1] = true;
        monitoring[2] = true;

        allowAuthorities = true;

        desiredBitRate = 1;
    }

    public boolean[] getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(boolean[] monitor) {
        monitoring = monitor;
    }

    public boolean getAlert() {
        return allowAuthorities;
    }

    public void setAlert(boolean alert) {
        allowAuthorities = alert;
    }

    public int getDesiredBitRate() {
        return desiredBitRate;
    }

    public void setDesiredBitRate(int bitRate) {
        desiredBitRate = bitRate;
    }

    public void saveSettings(boolean[] monitor, boolean alert, int bitRate) {
        setMonitoring(monitor);
        setAlert(alert);
        setDesiredBitRate(bitRate);

    }

}

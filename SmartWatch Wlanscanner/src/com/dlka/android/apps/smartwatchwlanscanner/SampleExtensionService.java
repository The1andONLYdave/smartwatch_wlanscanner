/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.dlka.android.apps.smartwatchwlanscanner;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.dlka.android.apps.smartwatchwlanscanner.controls.ControlManagerSmartWatch2;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

/**
 * The Sample Extension Service handles registration and keeps track of all
 * controls on all accessories.
 */

public class SampleExtensionService extends ExtensionService {

    public static final String EXTENSION_KEY = "com.dlka.android.apps.smartwatchwlanscanner.key";

    public static final String LOG_TAG = "SmartWatch Wlanscanner";

    public SampleExtensionService() {
        super(EXTENSION_KEY);
    }

    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    public List<ScanResult> wifiList;
    public StringBuilder sb = new StringBuilder();
    public String mainText;
    /**
     * {@inheritDoc}
     *
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(SampleExtensionService.LOG_TAG, "Service: onCreate");
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (mainWifi.isWifiEnabled() == false)
        {  
            // If wifi disabled then enable it
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled",
            Toast.LENGTH_LONG).show();
            mainWifi.setWifiEnabled(true);
        } 
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
          mainWifi.startScan();
          Log.d(SampleExtensionService.LOG_TAG, "Service: Starting Scan...");
       }
    
    public void onPause(){
//    	unregisterReceiver(receiverWifi);
    }
 
    public void onExit(){
    	unregisterReceiver(receiverWifi);
    	super.onDestroy();
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new SampleRegistrationInformation(this);
    }

    /*
     * (non-Javadoc)
     * @see com.sonyericsson.extras.liveware.aef.util.ExtensionService#
     * keepRunningWhenConnected()
     */
    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }

    @Override
    public ControlExtension createControlExtension(String hostAppPackageName) {
        Log.d(SampleExtensionService.LOG_TAG, "Service: createControlExtension");
        boolean advancedFeaturesSupported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(
                this, hostAppPackageName);
        if (advancedFeaturesSupported) {
            Log.d(SampleExtensionService.LOG_TAG,
                    "Service: Advanced features supported, returning SmartWatch2 extension control manager");
            return new ControlManagerSmartWatch2(this, hostAppPackageName);
        } else {
            Log.d(SampleExtensionService.LOG_TAG,
                    "Service: Advanced features not supported, exiting");
            // Possible to return a legacy control extension here
            throw new IllegalArgumentException("No control for: " +
                    hostAppPackageName);
        }
    }

// Broadcast receiver class called its receive method
// when number of wifi connections changed

class WifiReceiver extends BroadcastReceiver {

    // This method call when number of wifi connections changed
    public void onReceive(Context c, Intent intent) {

        sb = new StringBuilder();
         wifiList = mainWifi.getScanResults();
        sb.append("\n        Number Of Wifi connections :"+wifiList.size()+"\n\n");

        for(int i = 0; i < wifiList.size(); i++){

            sb.append(new Integer(i+1).toString() + ". ");
            sb.append((wifiList.get(i)).toString());
            Log.d(SampleExtensionService.LOG_TAG, "added:"+(wifiList.get(i)).toString());
            sb.append("\n\n");
        }

        	
        mainText=sb.toString();
        Log.d(SampleExtensionService.LOG_TAG, "mainText:"+mainText);
        
    }

}}
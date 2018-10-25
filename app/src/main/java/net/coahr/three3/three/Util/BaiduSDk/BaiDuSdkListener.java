package net.coahr.three3.three.Util.BaiduSDk;

import android.location.Location;

import com.baidu.location.BDLocation;

public interface BaiDuSdkListener {

    void getLongLatAddress(Double Longitude, Double Latitude, String address, StringBuffer sb, BDLocation location);
}

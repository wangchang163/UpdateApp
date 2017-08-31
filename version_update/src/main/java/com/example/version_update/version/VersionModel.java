package com.example.version_update.version;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/30.
 */

public class VersionModel implements Serializable{

    public int versionCode;
    public String versionName;
    public String desc;
    public String url;
    public int forced;

}

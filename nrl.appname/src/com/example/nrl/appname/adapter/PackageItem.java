package com.example.nrl.appname.adapter;

import android.graphics.drawable.Drawable;

public class PackageItem {
	private Drawable icon;

    private String name;

    private String packageName;
    private String dir;
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }
    public String getdir(){
    	return dir;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setdir(String dir)
    {
    	this.dir=dir;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}

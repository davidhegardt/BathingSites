package se.miun.dahe1501.bathingsites;

/**
 * Created by Dave on 2017-05-12.
 * Class used to represent a bathingsite-object.
 * Used for database storage
 */

public class BathingSite {

    private long id;
    private String Name;
    private String Address;
    private String Desc;
    private String Longitude;
    private String Latitude;
    private float Grade;
    private double WaterTemp;
    private String DateTemp;

    public void setName(String pName){
        this.Name = pName;
    }

    public String getName(){
        return this.Name;
    }

    public void setAddress(String pAddress){
        this.Address = pAddress;
    }

    public String getAddress(){
        return this.Address;
    }

    public void setId(long pId){
        this.id = pId;
    }

    public long getId(){
        return this.id;
    }

    public void setDesc(String pDesc){
        this.Desc = pDesc;
    }

    public String getDesc(){
        return this.Desc;
    }

    public void setLatitude(String pLatitude){
        this.Latitude = pLatitude;
    }

    public String getLatitude(){
        return this.Latitude;
    }

    public void setLongitude(String pLongitude){
        this.Longitude = pLongitude;
    }

    public String getLongitude(){
        return this.Longitude;
    }

    public void setGrade(float pGrade){
        this.Grade = pGrade;
    }

    public float getGrade(){
        return this.Grade;
    }

    public void setWaterTemp(double pWaterTemp){
        this.WaterTemp = pWaterTemp;
    }

    public double getWaterTemp(){
        return this.WaterTemp;
    }

    public void setDateTemp(String pDateTemp){
        this.DateTemp = pDateTemp;
    }

    public String getDateTemp(){
        return this.DateTemp;
    }



}

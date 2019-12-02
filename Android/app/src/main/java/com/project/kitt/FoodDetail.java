package com.project.kitt;

public class FoodDetail
{
    private int foodID;
    private String foodName;
    private int foodDay;
    private int foodMon;
    private int foodYr;
    private String foodNotifications;

    public int getFoodID()
    {
        return foodID;
    }

    public void setFoodID(int foodID)
    {
        this.foodID = foodID;
    }

    public String getFoodName()
    {
        return foodName;
    }

    public void setFoodName(String foodName)
    {
        this.foodName = foodName;
    }

    public int getFoodDay()
    {
        return foodDay;
    }

    public int getFoodMon()
    {
        return foodMon;
    }

    public int getFoodYr()
    {
        return foodYr;
    }

    public void setFoodDay(int foodDay)
    {
        this.foodDay = foodDay;
    }

    public void setFoodMon(int foodMon)
    {
        this.foodMon = foodMon;
    }

    public void setFoodYr(int foodYr)
    {
        this.foodYr = foodYr;
    }

    public void setFoodNotification(String ids) { this.foodNotifications = ids; }

    public String getFoodNotification() { return foodNotifications; }
}

package com.example.tourguideapp.Models;

import org.json.JSONArray;
import org.json.JSONException;

public class VenueModel {

  private String venueName;
  private String latLong;
  private int distance;
  private String venueIcon;
  private String description;
  private final static String venueIconSize = "64";

  public VenueModel(String venueName, int distance, JSONArray venueCategories, String description, String latLong) {
    this.venueName = venueName;
    this.distance = distance;
    this.latLong = latLong;
    this.venueIcon = buildIconUri(venueCategories);
    this.description = description;
  }

  private String buildIconUri(JSONArray venueCategories) {

    try {
      return venueCategories.getJSONObject(0).getJSONObject("icon").getString("prefix") + venueIconSize
          + venueCategories.getJSONObject(0).getJSONObject("icon").getString("suffix");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }
  public String getLatLong() {
    return latLong;
  }

  public void setLatLong(String latLong) {
    this.latLong = latLong;
  }
  public String getVenueName() {
    return venueName;
  }

  public int getDistance() {
    return distance;
  }


  public String getVenueIcon() {
    return venueIcon;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof VenueModel)) {
      return false;
    }
    VenueModel that = (VenueModel) other;
    return this.latLong.equals(that.latLong) && this.getVenueName().equals(that.getVenueName());
  }
}
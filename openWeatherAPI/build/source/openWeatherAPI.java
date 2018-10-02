import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class openWeatherAPI extends PApplet {

/**
 * Loading JSON Data
 * by Daniel Shiffman.
 *
 * This example demonstrates how to use loadJSONObject/Array()
 * to retrieve JSON data via URL
 */






OscP5 oscP5;
NetAddress dest;
ControlP5 cp5;

// We're going to store the temperature
float temperature, pressure, humidity, tempMin, tempMax, vis, windSpeed, deg, cloudP, gust;
float longitude, latitude;
int textX = 300;
// We're going to store text about the weather
String weather = "";
String apiKey = "45cf034f83cf5675c113ee304a8f4f62";
String units = "units=metric";
String city = "Sydney";
String url = "http://api.openweathermap.org/data/2.5/weather?q=";
String randomCityName;

JSONArray cityList;

public void setup() {
  

  /* start oscP5, listening for incoming messages at port 12000 */
  oscP5 = new OscP5(this, 9000);
  dest = new NetAddress("127.0.0.1", 6448);


  //initialise CP5
  cp5 = new ControlP5(this);

  //create 10 buttons for prototype cities
  cp5.addButton("Sydney")
    .setValue(10)
    .setPosition(10, 50)
    .setSize(200, 19)
    ;

  cp5.addButton("Paris")
    .setValue(100)
    .setPosition(10, 80)
    .setSize(200, 19)
    ;

  cp5.addButton("London")
    .setValue(100)
    .setPosition(10, 110)
    .setSize(200, 19)
    ;

  cp5.addButton("Melbourne")
    .setValue(100)
    .setPosition(10, 140)
    .setSize(200, 19)
    ;
  cp5.addButton("Tokyo")
    .setValue(100)
    .setPosition(10, 170)
    .setSize(200, 19)
    ;

  cp5.addButton("Kyoto")
    .setValue(100)
    .setPosition(10, 200)
    .setSize(200, 19)
    ;

  cp5.addButton("Warsaw")
    .setValue(100)
    .setPosition(10, 230)
    .setSize(200, 19)
    ;

  cp5.addButton("Portland")
    .setValue(100)
    .setPosition(10, 260)
    .setSize(200, 19)
    ;

  cp5.addButton("Amsterdam")
    .setValue(100)
    .setPosition(10, 290)
    .setSize(200, 19)
    ;

  cp5.addButton("Berlin")
    .setValue(100)
    .setPosition(10, 320)
    .setSize(200, 19)
    ;

  cp5.addButton("Random")
      .setValue(100)
      .setPosition(10, 350)
      .setSize(200, 19)
      ;

  //Call the update call to display the initial city
  updateCall();
}

public void draw() {
  background(255);
  fill(0);
  drawCall();
  sendOsc();



}


public void Sydney(int theValue) {
  println("a button event from Sydney: "+theValue);
  city = "Sydney";
  updateCall();
}

public void Paris(int theValue) {
  println("a button event from Paris: "+theValue);
  city = "Paris";
  updateCall();
}

public void London(int theValue) {
  println("a button event from London: "+theValue);
  city = "London,uk";
  updateCall();
}

public void Melbourne(int theValue) {
  println("a button event from Melbourne: "+theValue);
  city = "Melbourne";
  updateCall();
}

public void Tokyo(int theValue) {
  println("a button event from Tokyo: "+theValue);
  city = "Tokyo";
  updateCall();
}

public void Kyoto(int theValue) {
  println("a button event from Kyoto: "+theValue);
  city = "Kyoto";
  updateCall();
}

public void Warsaw(int theValue) {
  println("a button event from Warsaw: "+theValue);
  city = "Warsaw";
  updateCall();
}

public void Portland(int theValue) {
  println("a button event from Portland: "+theValue);
  city = "Portland";
  updateCall();
}

public void Amsterdam(int theValue) {
  println("a button event from Amsterdam: "+theValue);
  city = "Amsterdam";
  updateCall();
}

public void Berlin(int theValue) {
  println("a button event from Berlin: "+theValue);
  city = "Berlin";
  updateCall();
}

public void Random(int theValue) {
  println("a button event from Random: " + theValue);

  // String randomCity = "Hobart";
  int maxCities = cityList.size();
  println(maxCities);
  int randomNumber = PApplet.parseInt(random(maxCities));

  JSONObject ranCity = cityList.getJSONObject(randomNumber);
  randomCityName = ranCity.getString("name");

  city = randomCityName;
  updateCall();
}

public void updateCall() {

  // The URL for the JSON data (replace "imperial" with "metric" for celsius)


  // Load the XML document
  JSONObject json = loadJSONObject(url + city + "&" + units +"&appid=" + apiKey);

  //// Get parameters from the main section
  JSONObject main = json.getJSONObject("main");
  temperature = main.getFloat("temp");
  pressure = main.getFloat("pressure");
  humidity = main.getFloat("humidity");
  tempMin = main.getFloat("temp_min");
  tempMax = main.getFloat("temp_max");

  JSONObject wind = json.getJSONObject("wind");
  windSpeed = wind.getFloat("speed");
  deg = wind.getFloat("deg");

  JSONObject coord = json.getJSONObject("coord");
  longitude = coord.getInt("lon");
  latitude = coord.getInt("lat");

  JSONObject clouds = json.getJSONObject("clouds");

  cloudP = clouds.getInt("all");

  cityList = loadJSONArray("city.list.json");

}

public void drawCall() {
  //// Display all the stuff we want to display
  text("City: " + city, textX, 50);
  text("Current Temperature: " + temperature + " degrees c", textX, 70);
  text("Atmospheric Pressure: " + pressure, textX, 90);
  text("Humidity: " + humidity, textX, 110);
  text("Temperature Min: " + tempMin, textX, 130);
  text("Temperature Max: " + tempMax, textX, 150);
  text("Clouds: " + cloudP + "%", textX, 170);
  text("Wind Speed: " + windSpeed, textX, 190);
  text("Wind Direction: " + deg, textX, 210);
  text("Longitude: " + longitude, textX, 230);
  text("Latitude: " + latitude, textX, 250);
}

public void sendOsc() {
  OscMessage msg = new OscMessage("/wek/inputs");
  msg.add((float)temperature);
  msg.add((float)pressure);
  msg.add((float)humidity);
  msg.add((float)tempMin);
  msg.add((float)tempMax);
  msg.add((float)cloudP);
  msg.add((float)windSpeed);
  msg.add((float)deg);
  msg.add((float)longitude);
  msg.add((float)latitude);
  oscP5.send(msg, dest);
}
  public void settings() {  size(600, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "openWeatherAPI" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

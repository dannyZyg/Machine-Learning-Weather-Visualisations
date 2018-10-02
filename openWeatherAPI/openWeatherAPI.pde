/**
 * Danny Keig 2018
 *
 *
 * Patch started from:
 * "Loading JSON Data"
 * by Daniel Shiffman.
 * https://github.com/shiffman/LearningProcessing/blob/master/chp18\_data/exercise\_18\_17\_loadJSON\_weather/exercise\_18\_17\_loadJSON\_weather.pde
 * 
 * Wind Direction Data sometimes causes issues, so is left deactivated for now.
 *
 */


import controlP5.*;
import oscP5.*;
import netP5.*;

OscP5 oscP5;
NetAddress dest;
ControlP5 cp5;

float temperature, pressure, humidity, tempMin, tempMax, vis, windSpeed, deg, cloudP, gust;
float longitude, latitude;
int textX = 300;
String call = "";
String weather = "";
String apiKey = "45cf034f83cf5675c113ee304a8f4f62";
String units = "units=metric";
String city = "Sydney";
String url = "http://api.openweathermap.org/data/2.5/weather?q=";
String randomCityName;

JSONArray cityList;

void setup() {
  size(550, 400);

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

  cp5.addButton("HK")
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

void draw() {
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

public void HK(int theValue) {
  println("a button event from Hong Kong: "+theValue);
  city = "Hong Kong";
  updateCall();
}

public void Random(int theValue) {
  println("a button event from Random: " + theValue);

  // String randomCity = "Hobart";
  int maxCities = cityList.size();
  println(maxCities);
  int randomNumber = int(random(maxCities - 1));

  JSONObject ranCity = cityList.getJSONObject(randomNumber);
  randomCityName = ranCity.getString("name");

  city = randomCityName;
  updateCall();
}

void updateCall() {


  // Load the XML document
  // construct the API call from various chunks
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

  //Wind Direction occassionaly causes issues if the data is not available, so is currently not active
  //deg = wind.getInt("deg");

  JSONObject coord = json.getJSONObject("coord");
  longitude = coord.getInt("lon");
  latitude = coord.getInt("lat");

  JSONObject clouds = json.getJSONObject("clouds");
  cloudP = clouds.getInt("all");

  cityList = loadJSONArray("city.list.json");
}

void drawCall() {
  //// Display all the results of the call
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

void sendOsc() {

  //construct a new OSC message
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

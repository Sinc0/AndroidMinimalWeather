//name
package android.example.basicweatherapp;



//includes
import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.Math;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;



//main
public class MainActivity extends AppCompatActivity {
    //Widgets
    EditText editTextEnterWeatherLocation;
    TextView textViewDisplayWeatherInfo;
    Toast toastCityNotFound;
    Button buttonWeatherInfo;

    //Strings
    String editTextString;
    String sunrise;
    String sunset;
    String weatherInfoText;
    String formattedWeatherLocation;
    String weatherLocation;
    //String downloadUrl;
    String humidity;
    String temperature;
    String feelsLike;
    String airFeels;
    String description;
    String celsius;
    String country;
    String tempMax;
    String tempMin;
    String pressure;
    String clouds;
    String coords;
    String weather;
    String wind;
    String visibility;
    String dt;
    String locationTimezone;
    String locationId;
    String locationName;

    //API Strings
    String API_Key = "f394e4fb836c1332f30df5d91d30d9ab";
    String API_URL_Weather = "https://api.openweathermap.org/data/2.5/weather?q=";
    String API_URL_Forecast = "https://api.openweathermap.org/data/2.5/forecast?q=";

    //ints
    int humidityInt = 0;
    int celsiusInt = 0;
    int feelsLikeInt = 0;

    //doubles
    double temperatureDouble = 0;
    double feelsLikeDouble = 0;



    //functions
    public void fetchWeatherData(View v)
    {
        //variables
        weatherLocation = editTextEnterWeatherLocation.getText().toString(); //weather location from textbox
        String downloadUrl = API_URL_Forecast + weatherLocation + "&appid=" + API_Key; //download url 1
        //downloadUrl = API_URL_Weather + weatherLocation + "&appid=" + API_Key; //set download url 2

        //start data download
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(downloadUrl);
    }


    /* public String getWeekdayName(Date value)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(value);

        int weekday = c.get(Calendar.DAY_OF_WEEK);
        String dayOfWeek = "";

        if(weekday == 1) { dayOfWeek = "Monday"; }
        else if(weekday == 2) { dayOfWeek = "Tuesday"; }
        else if(weekday == 3) { dayOfWeek = "Wednesday"; }
        else if(weekday == 4) { dayOfWeek = "Thursday"; }
        else if(weekday == 5) { dayOfWeek = "Friday"; }
        else if(weekday == 6) { dayOfWeek = "Saturday"; }
        else if(weekday == 7) { dayOfWeek = "Sunday"; }

        return dayOfWeek;
    } */


    public String getTempFormatted(JSONObject obj, String type) throws JSONException {
        //variables
        String formattedString = "";
        double temperature = 0;
        JSONObject main = obj.getJSONObject("main");

        //check type
        if(type == "temp") { formattedString = main.getString("temp"); }
        else if(type == "tmin") { formattedString = main.getString("temp_min"); }
        else if(type == "tmax") { formattedString = main.getString("temp_max"); }
        else if(type == "tfeels_like") { formattedString = main.getString("feels_like"); }

        //convert from kelvin to celsius
        temperature = Double.valueOf(formattedString);
        temperature = temperature - 273.15;
        temperature = Math.round(temperature);

        //set formatted string
        formattedString = String.valueOf(temperature).substring(0, 2) + "°";

        return formattedString;
    }


    public String getDateFormatted(String value)
    {
        //variables
        String formattedString = value.substring(6).replace("-", "/");
        String[] split = formattedString.split("/");
        String part1 = split[0];
        String part2 = split[1];

        //check date
        if(Objects.equals(part1, "1")) { part1 = "Jan"; }
        else if(Objects.equals(part1, "2")) { part1 = "Feb"; }
        else if(Objects.equals(part1, "3")) { part1 = "Mar"; }
        else if(Objects.equals(part1, "4")) { part1 = "Apr"; }
        else if(Objects.equals(part1, "5")) { part1 = "May"; }
        else if(Objects.equals(part1, "6")) { part1 = "Jun"; }
        else if(Objects.equals(part1, "7")) { part1 = "Jul"; }
        else if(Objects.equals(part1, "8")) { part1 = "Aug"; }
        else if(Objects.equals(part1, "9")) { part1 = "Sep"; }
        else if(Objects.equals(part1, "10")) { part1 = "Oct"; }
        else if(Objects.equals(part1, "11")) { part1 = "Nov"; }
        else if(Objects.equals(part1, "12")) { part1 = "Dec"; }

        //set formatted string
        formattedString =  part1 + " " + part2;

        return formattedString;
    }


    public String getSunDataFormatted(JSONObject obj, String type) throws JSONException {
        //variables
        String formattedString = "";
        long timestamp;
        Date date;
        JSONObject city = obj.getJSONObject("city");

        //check type
        if(type == "sunrise") { formattedString = city.getString("sunrise"); }
        else if(type == "sunset") { formattedString = city.getString("sunset"); }

        //convert from unix to date
        timestamp = Long.parseLong(formattedString);
        date = new Date();
        date.setTime((long)timestamp*1000);

        //set formatted string
        formattedString = date.toString().substring(11, 16);

        return formattedString;
    }


    public String getHumidityFormatted(JSONObject obj, String type) throws JSONException {
        //variables
        String formattedString = "";
        JSONObject main = obj.getJSONObject("main");

        //check type
        if(type == "humidity") { formattedString = main.getString("humidity"); }

        //set formatted string
        formattedString = formattedString + "%";

        return formattedString;
    }


    public String getWeatherFormatted(JSONObject obj, String type) throws JSONException {
        //variables
        String formattedString = "";
        JSONArray weather1 = obj.getJSONArray("weather");
        JSONObject weather2 = weather1.getJSONObject(0);
        String weather3 = weather2.getString("main").toLowerCase();

        //check type
        if(type == "weather") { formattedString = weather3; }

        return formattedString;
    }


    public void setBackgroundImage(String temperature)
    {
        //variables
        int currentTemp = Integer.parseInt(temperature.substring(0, 2));

        //set background based on temperature
        if (currentTemp < 10) { getWindow().setBackgroundDrawableResource(R.drawable.cold); }
        else if (currentTemp > 10 && currentTemp <= 20) { getWindow().setBackgroundDrawableResource(R.drawable.normal); }
        else if (currentTemp > 20) { getWindow().setBackgroundDrawableResource(R.drawable.sun); }
    }


    public void setFormattedLocationEditText(JSONObject obj) throws JSONException {
        //variables
        JSONObject city = obj.getJSONObject("city");
        String location = city.getString("name");
        String country = city.getString("country");

        //set formatted string
        formattedWeatherLocation = location.substring(0, 1)
                                           .toUpperCase() + location
                                           .substring(1) + " (" + country + ")";

        //update UI
        editTextEnterWeatherLocation.setText(formattedWeatherLocation);
    }


    public String setCalendarDate(Calendar value, String type)
    {
        //variables
        Date date = null;
        String shortDateString;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        //check type
        if(type == "today")
        {
            date = value.getTime();
        }
        else if(type == "increment")
        {
            value.add(Calendar.DAY_OF_YEAR, 1);
            date = value.getTime();
        }

        //set formatted string
        shortDateString = dateFormat.format(date).substring(0, 10).replace("/", "-");

        return shortDateString;
    }



    //classes
    public class DownloadTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            //variables
            String result = "";
            URL url;

            //set http url connection
            HttpURLConnection urlConnection = null;

            //try download
            try
            {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputReader = new InputStreamReader(inputStream);

                int data = inputReader.read();

                while (data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = inputReader.read();
                }

                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //try parse data
            try
            {
                //set JSON variables
                JSONObject jsonObject = new JSONObject(s); //data as JSON object
                JSONArray list = jsonObject.getJSONArray("list"); //data as JSON array

                //set Calendar
                Calendar calendar = Calendar.getInstance(); //calendar

                //today arrays
                List<String> todaysTemperatureData = new ArrayList<>(); //today temp
                List<String> todaysTminData = new ArrayList<>(); //today temp min
                List<String> todaysTmaxData = new ArrayList<>(); //today temp max
                List<String> todaysTfeelsData = new ArrayList<>(); //today temp feels like
                List<String> todaysHumidityData = new ArrayList<>(); //today humidity
                List<String> todaysWeatherData = new ArrayList<>(); //today weather description

                //tomorrow arrays
                List<String> tomorrowsTminData = new ArrayList<>(); //tomorrow temp min
                List<String> tomorrowsTmaxData = new ArrayList<>(); //tomorrow temp max
                List<String> tomorrowsWeatherData = new ArrayList<>(); //tomorrow weather description

                //in 2 days arrays
                List<String> in2DaysTminData = new ArrayList<>();
                List<String> in2DaysTmaxData = new ArrayList<>();
                List<String> in2DaysWeatherData = new ArrayList<>();

                //in 3 days arrays
                List<String> in3DaysTminData = new ArrayList<>();
                List<String> in3DaysTmaxData = new ArrayList<>();
                List<String> in3DaysWeatherData = new ArrayList<>();

                //in 4 days arrays
                List<String> in4DaysTminData = new ArrayList<>();
                List<String> in4DaysTmaxData = new ArrayList<>();
                List<String> in4DaysWeatherData = new ArrayList<>();

                //in 5 days arrays
                List<String> in5DaysTminData = new ArrayList<>();
                List<String> in5DaysTmaxData = new ArrayList<>();
                List<String> in5DaysWeatherData = new ArrayList<>();

                //debugging
                //Log.i("JSON Obj", jsonObject.toString());

                //set dates
                String todaysDate = setCalendarDate(calendar, "today");
                String tomorrowsDate = setCalendarDate(calendar, "increment");
                String dateIn2Days = setCalendarDate(calendar, "increment");
                String dateIn3Days = setCalendarDate(calendar, "increment");
                String dateIn4Days = setCalendarDate(calendar, "increment");
                String dateIn5Days = setCalendarDate(calendar, "increment");

                //set weekdays
                //String weekdayToday = getWeekdayName(today);
                //String weekdayTomorrow = getWeekdayName(tomorrow);
                //String weekdayIn2Days = getWeekdayName(in2Days);
                //String weekdayIn3Days = getWeekdayName(in3Days);
                //String weekdayIn4Days = getWeekdayName(in4Days);
                //String weekdayIn5Days = getWeekdayName(in5Days);

                //sort JSON data
                for(int i = 0; i < 40; i++)
                {
                    //variables
                    JSONObject itemObj = list.getJSONObject(i);
                    String itemDate = list.getJSONObject(i).getString("dt_txt").substring(0, 10);
                    String temperatureData = getTempFormatted(itemObj, "temp");
                    String tminData = getTempFormatted(itemObj, "tmin");
                    String tmaxData = getTempFormatted(itemObj, "tmax");
                    String tfeels_likeData = getTempFormatted(itemObj, "tfeels_like");
                    String humidityData = getHumidityFormatted(itemObj, "humidity");
                    String weatherData = getWeatherFormatted(itemObj, "weather");
                    //String count = String.valueOf(i);
                    //String weatherData = getWeatherDataFormatted(itemObj);

                    //debugging
                    //Log.i("JSON List Item " + count, itemObj);

                    //add todays values
                    if(Objects.equals(itemDate, todaysDate))
                    {
                        todaysTemperatureData.add(temperatureData);
                        todaysTminData.add(tminData);
                        todaysTmaxData.add(tmaxData);
                        todaysTfeelsData.add(tfeels_likeData);
                        todaysHumidityData.add(humidityData);
                        todaysWeatherData.add(weatherData);
                    }

                    //add tomorrows values
                    else if(Objects.equals(itemDate, tomorrowsDate)) //tomorrow
                    {
                        tomorrowsTminData.add(tminData);
                        tomorrowsTmaxData.add(tmaxData);
                        tomorrowsWeatherData.add(weatherData);
                    }

                    //add in 2 days values
                    else if(Objects.equals(itemDate, dateIn2Days)) //in 2 days
                    {
                        in2DaysTminData.add(tminData);
                        in2DaysTmaxData.add(tmaxData);
                        in2DaysWeatherData.add(weatherData);
                    }

                    //add in 3 days values
                    else if(Objects.equals(itemDate, dateIn3Days)) //in 3 days
                    {
                        in3DaysTminData.add(tminData);
                        in3DaysTmaxData.add(tmaxData);
                        in3DaysWeatherData.add(weatherData);
                    }

                    //add in 4 days values
                    else if(Objects.equals(itemDate, dateIn4Days)) //in 4 days
                    {
                        in4DaysTminData.add(tminData);
                        in4DaysTmaxData.add(tmaxData);
                        in4DaysWeatherData.add(weatherData);
                    }

                    //add in 5 days values
                    else if(Objects.equals(itemDate, dateIn5Days)) //in 5 days
                    {
                        in5DaysTminData.add(tminData);
                        in5DaysTmaxData.add(tmaxData);
                        in5DaysWeatherData.add(weatherData);
                    }
                }

                //debugging
                //Log.i("todaysTemperatureData", todaysTemperatureData.toString());
                //Log.i("tomorrowsTminData", tomorrowsTminData.toString());
                //Log.i("in2DaysTminData", in2DaysTminData.toString());
                //Log.i("in3DaysTminData", in3DaysTminData.toString());
                //Log.i("in4DaysTminData", in4DaysTminData.toString());
                //Log.i("in5DaysTminData", in5DaysTminData.toString());
                //Log.i("JSON List Item 1", list.getJSONObject(0).getString("dt_txt"));
                //Log.i("JSON List Item 2", list.getJSONObject(1).getString("dt_txt"));
                //Log.i("JSON List Item 3", list.getJSONObject(2).getString("dt_txt"));

                /*
                //set air feels like
                if (humidityInt <= 20) { airFeels = "Dry"; }
                else if (humidityInt >= 20 && humidityInt <= 60) { airFeels = "Normal"; }
                else if (humidityInt >= 60 && humidityInt > 60) { airFeels =  "Wet"; }

                //display weather info textview
                textViewDisplayWeatherInfo.setVisibility(View.VISIBLE);
                textViewDisplayWeatherInfo.setText(weatherInfoText);
                */

                //sort arrays (lowest to highest)
                Collections.sort(todaysTminData);
                Collections.sort(todaysTmaxData);
                Collections.sort(tomorrowsTminData);
                Collections.sort(tomorrowsTmaxData);
                Collections.sort(in2DaysTminData);
                Collections.sort(in2DaysTmaxData);
                Collections.sort(in3DaysTminData);
                Collections.sort(in3DaysTmaxData);
                Collections.sort(in4DaysTminData);
                Collections.sort(in4DaysTmaxData);
                Collections.sort(in5DaysTminData);
                Collections.sort(in5DaysTmaxData);

                //set variables
                dateIn2Days = getDateFormatted(dateIn2Days); //set formatted date string
                dateIn3Days = getDateFormatted(dateIn3Days); //set formatted date string
                dateIn4Days = getDateFormatted(dateIn4Days); //set formatted date string
                dateIn5Days = getDateFormatted(dateIn5Days); //set formatted date string
                sunrise = getSunDataFormatted(jsonObject, "sunrise"); //set sunrise
                sunset = getSunDataFormatted(jsonObject, "sunset"); //set sunset
                weatherInfoText = "Today" +
                                  "\n" + "· temp: " + todaysTemperatureData.get(0) +
                                  "\n" + "· feels like: " + todaysTfeelsData.get(0) +
                                  "\n" + "· range: " + todaysTminData.get(0) + " -  " + todaysTmaxData.get(todaysTmaxData.size() - 1) +
                                  //"\n" + "· humidity: " + todaysHumidityData.get(0) +
                                  "\n" + "· sun: " + sunrise + "  -  " + sunset +
                                  "\n" + "· weather: " + todaysWeatherData.get(0) +
                                  "\n\n" + "Tomorrow" + "\n" +
                                  "· temp: " + tomorrowsTminData.get(0) + " -  " + tomorrowsTmaxData.get(tomorrowsTmaxData.size() - 1) + "\n" +
                                  "· weather: " + tomorrowsWeatherData.get(0) +
                                  "\n\n" + dateIn2Days + "\n" +
                                  "· temp: " + in2DaysTminData.get(0) + " -  " + in2DaysTmaxData.get(in2DaysTmaxData.size() - 1) + "\n" +
                                  "· weather: " + in2DaysWeatherData.get(0) +
                                  "\n\n" + dateIn3Days + "\n" +
                                  "· temp: " + in3DaysTminData.get(0) + " -  " + in3DaysTmaxData.get(in3DaysTmaxData.size() - 1) + "\n" +
                                  "· weather: " + in3DaysWeatherData.get(0) +
                                  "\n\n" + dateIn4Days + "\n" +
                                  "· temp: " + in4DaysTminData.get(0) + " -  " + in4DaysTmaxData.get(in4DaysTmaxData.size() - 1) + "\n" +
                                  "· weather: " + in4DaysWeatherData.get(0) +
                                  "\n\n" + dateIn5Days + "\n" +
                                  "· temp: " + in5DaysTminData.get(0) + " -  " + in5DaysTmaxData.get(in5DaysTmaxData.size() - 1) + "\n" +
                                  "· weather: " + in5DaysWeatherData.get(0) +
                                  "\n\n\n\n";

                //update UI
                setFormattedLocationEditText(jsonObject);
                setBackgroundImage(todaysTemperatureData.get(0));
                textViewDisplayWeatherInfo.setText(weatherInfoText);
            }
            catch (Exception e)
            {
                //debugging
                e.printStackTrace();
                Log.i("Tag", "City not found");

                //display error message as toast
                toastCityNotFound.show();

                //reset weather info
                textViewDisplayWeatherInfo.setText("");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load startup activity
        setContentView(R.layout.activity_main);

        //set widgets
        editTextEnterWeatherLocation = findViewById(R.id.editTextEnterWeatherLocation);
        textViewDisplayWeatherInfo = findViewById(R.id.textViewDisplayWeatherInfo);
        //buttonWeatherInfo = findViewById(R.id.buttonWeatherInfo);

        //set main background image
        getWindow().setBackgroundDrawableResource(R.drawable.mainbackground);

        //set error message location not found as toast
        toastCityNotFound = Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT);

        //set listener for weather location on keyboard done
        editTextEnterWeatherLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) { fetchWeatherData(v); }
                else { }
                return false;
            }
        });

        //set listener for weather location on click
        editTextEnterWeatherLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear enter weather location text
                editTextEnterWeatherLocation.getText().clear();
                //editTextEnterWeatherLocation.setText("");
            }
        });
    }
}
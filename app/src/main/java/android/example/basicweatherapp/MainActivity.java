//namespace
package android.example.basicweatherapp;

//includes
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.Math;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;


//main class
public class MainActivity extends AppCompatActivity {
    //variables
    EditText editTextEnterWeatherLocation;
    EditText editTextTab2;
    TextView textViewDisplayWeatherInfo;
    Toast toastLocationNotFound;
    String sunrise;
    String sunset;
    String weatherInfoText;
    String formattedWeatherLocation;
    String weatherLocation;
    String temperature;
    String downloadUrl;
    String API_Key = "";
    String API_URL_Forecast = "https://api.openweathermap.org/data/2.5/forecast?q=";
    String API_URL_Weather = "https://api.openweathermap.org/data/2.5/weather?q=";


    //functions
    public void fetchWeatherData(View v)
    {
        //get weather location from textbox
        weatherLocation = editTextEnterWeatherLocation.getText().toString(); //weather location from textbox

        //set download url
        downloadUrl = API_URL_Forecast + weatherLocation + "&appid=" + API_Key; //download url 1

        //start async data download
        new DownloadTask().execute(downloadUrl);
    }


    public void fetchDefaultLocationData(String defaultLocation)
    {
        //get weather location from textbox
        weatherLocation = defaultLocation;

        //set download url
        downloadUrl = API_URL_Forecast + weatherLocation + "&appid=" + API_Key; //download url 1

        //start async data download
        new DownloadTask().execute(downloadUrl);
    }


    public String getWeekdayName(Date value)
    {
        //variables
        Calendar c = Calendar.getInstance();
        c.setTime(value);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        String dayOfWeek = "";

        //check weekday
        if(weekday == 1) { dayOfWeek = "Monday"; }
        else if(weekday == 2) { dayOfWeek = "Tuesday"; }
        else if(weekday == 3) { dayOfWeek = "Wednesday"; }
        else if(weekday == 4) { dayOfWeek = "Thursday"; }
        else if(weekday == 5) { dayOfWeek = "Friday"; }
        else if(weekday == 6) { dayOfWeek = "Saturday"; }
        else if(weekday == 7) { dayOfWeek = "Sunday"; }

        //return value
        return dayOfWeek;
    }


    public String getTempFormatted(JSONObject obj, String type) throws JSONException
    {
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
        formattedString = String.valueOf(temperature);
        if(formattedString.length() == 4) { formattedString = String.valueOf(temperature).substring(0, 2); }
        else if(formattedString.length() == 3) { formattedString = String.valueOf(temperature).substring(0, 1); }
        else { }

        //return value
        return formattedString;
    }


    public String getDateFormatted(String value)
    {
        //variables
        String formattedString = value;
        String[] split = formattedString.split("-");
        String part1 = split[1];
        String part2 = split[2];

        //format month
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

        //format date
        if(Objects.equals(part2, "01")) { part2 = "1"; }
        else if(Objects.equals(part2, "02")) { part2 = "2"; }
        else if(Objects.equals(part2, "03")) { part2 = "3"; }
        else if(Objects.equals(part2, "04")) { part2 = "4"; }
        else if(Objects.equals(part2, "05")) { part2 = "5"; }
        else if(Objects.equals(part2, "06")) { part2 = "6"; }
        else if(Objects.equals(part2, "07")) { part2 = "7"; }
        else if(Objects.equals(part2, "08")) { part2 = "8"; }
        else if(Objects.equals(part2, "09")) { part2 = "9"; }

        //set formatted string
        formattedString =  part1 + " " + part2;

        //return value
        return formattedString;
    }


    public String getSunDataFormatted(JSONObject obj, String type) throws JSONException
    {
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

        //return value
        return formattedString;
    }


    public String getHumidityFormatted(JSONObject obj, String type) throws JSONException
    {
        //variables
        String formattedString = "";
        JSONObject main = obj.getJSONObject("main");

        //check type
        if(type == "humidity") { formattedString = main.getString("humidity"); }

        //set formatted string
        formattedString = formattedString + "%";

        //return value
        return formattedString;
    }


    public String getWeatherFormatted(JSONObject obj, String type) throws JSONException
    {
        //variables
        String formattedString = "";
        JSONArray weather1 = obj.getJSONArray("weather");
        JSONObject weather2 = weather1.getJSONObject(0);
        String weather3 = weather2.getString("main").toLowerCase();

        //check type
        if(type == "weather") { formattedString = weather3; }

        //return value
        return formattedString;
    }


    public void setBackgroundImage(String temperature)
    {
        //variables
        int currentTemp;
        View constraintLayout1 = findViewById(R.id.constraintLayout1);

        //null check
        if(temperature == null) { constraintLayout1.setBackgroundResource(R.drawable.mainbackground); return; }

        //set current temp
        currentTemp = Integer.parseInt(temperature);

        //set background based on temperature
        if (currentTemp < 10)
            { constraintLayout1.setBackgroundResource(R.drawable.cold); } //temp below 10 = cold
        else if (currentTemp >= 10 && currentTemp <= 26)
            { constraintLayout1.setBackgroundResource(R.drawable.mainbackground); } //temp between 10 and 26 = normal
        else if (currentTemp > 26)
            { constraintLayout1.setBackgroundResource(R.drawable.sun); } //temp above 26 = hot
    }


    public void setFormattedLocationEditText(JSONObject obj) throws JSONException
    {
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
            { date = value.getTime(); }
        else if(type == "increment")
            { value.add(Calendar.DAY_OF_YEAR, 1); date = value.getTime(); }

        //set formatted string
        shortDateString = dateFormat.format(date).substring(0, 10).replace("/", "-");

        //return value
        return shortDateString;
    }


    public void handleDownloadData(String s)
    {
        //try parse data
        try {
            //set JSON variables
            JSONObject jsonObject = new JSONObject(s); //data as JSON object
            JSONArray list = jsonObject.getJSONArray("list"); //data as JSON array

            //debugging
            //Log.i("JSON Obj", jsonObject.toString());

            //set Calendar
            Calendar calendar = Calendar.getInstance(); //calendar

            //temperature arrays
            List<String> todaysTemperatureData = new ArrayList<>(); //today temp
            List<String> todaysTminData = new ArrayList<>(); //today temp min
            List<String> todaysTmaxData = new ArrayList<>(); //today temp max
            List<String> todaysTfeelsData = new ArrayList<>(); //today temp feels like
            List<String> todaysHumidityData = new ArrayList<>(); //today humidity
            List<String> todaysWeatherData = new ArrayList<>(); //today weather description
            List<String> tomorrowsTminData = new ArrayList<>(); //tomorrow temp min
            List<String> tomorrowsTmaxData = new ArrayList<>(); //tomorrow temp max
            List<String> tomorrowsWeatherData = new ArrayList<>(); //tomorrow weather description
            List<String> in2DaysTminData = new ArrayList<>();
            List<String> in2DaysTmaxData = new ArrayList<>();
            List<String> in2DaysWeatherData = new ArrayList<>();
            List<String> in3DaysTminData = new ArrayList<>();
            List<String> in3DaysTmaxData = new ArrayList<>();
            List<String> in3DaysWeatherData = new ArrayList<>();
            List<String> in4DaysTminData = new ArrayList<>();
            List<String> in4DaysTmaxData = new ArrayList<>();
            List<String> in4DaysWeatherData = new ArrayList<>();
            List<String> in5DaysTminData = new ArrayList<>();
            List<String> in5DaysTmaxData = new ArrayList<>();
            List<String> in5DaysWeatherData = new ArrayList<>();

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
            for (int i = 0; i < 40; i++)
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

                if(Objects.equals(weatherData, "rain")) { weatherData = "rainy"; }
                else if(Objects.equals(weatherData, "clouds")) { weatherData = "cloudy"; }
                else if(Objects.equals(weatherData, "snow")) { weatherData = "snowy"; }
                else if(Objects.equals(weatherData, "clear")) { weatherData = "clear"; }

                //debugging
                //Log.i("JSON List Item " + count, itemObj);

                //add todays values
                if (Objects.equals(itemDate, todaysDate)) {
                    todaysTemperatureData.add(temperatureData);
                    todaysTminData.add(tminData);
                    todaysTmaxData.add(tmaxData);
                    todaysTfeelsData.add(tfeels_likeData);
                    todaysHumidityData.add(humidityData);
                    todaysWeatherData.add(weatherData);
                }

                //add other days values
                else if (Objects.equals(itemDate, tomorrowsDate))
                    { tomorrowsTminData.add(tminData); tomorrowsTmaxData.add(tmaxData); tomorrowsWeatherData.add(weatherData); }
                else if (Objects.equals(itemDate, dateIn2Days))
                    { in2DaysTminData.add(tminData); in2DaysTmaxData.add(tmaxData); in2DaysWeatherData.add(weatherData); }
                else if (Objects.equals(itemDate, dateIn3Days))
                    { in3DaysTminData.add(tminData); in3DaysTmaxData.add(tmaxData); in3DaysWeatherData.add(weatherData); }
                else if (Objects.equals(itemDate, dateIn4Days))
                    { in4DaysTminData.add(tminData); in4DaysTmaxData.add(tmaxData); in4DaysWeatherData.add(weatherData); }
                else if (Objects.equals(itemDate, dateIn5Days))
                    { in5DaysTminData.add(tminData); in5DaysTmaxData.add(tmaxData); in5DaysWeatherData.add(weatherData); }
            }

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

            //set date strings
            dateIn2Days = getDateFormatted(dateIn2Days); //set formatted date string
            dateIn3Days = getDateFormatted(dateIn3Days); //set formatted date string
            dateIn4Days = getDateFormatted(dateIn4Days); //set formatted date string
            dateIn5Days = getDateFormatted(dateIn5Days); //set formatted date string

            //set sun values
            sunrise = getSunDataFormatted(jsonObject, "sunrise"); //set sunrise
            sunset = getSunDataFormatted(jsonObject, "sunset"); //set sunset

            //set todays temperature
            temperature = todaysTemperatureData.get(0);

            //set weather info text
            weatherInfoText = "Today" +
                "\n" + "· temperature: " + todaysTemperatureData.get(0) + "°" +
                "\n" + "· feels like: " + todaysTfeelsData.get(0) + "°" +
                "\n" + "· range: " + todaysTminData.get(0) + "° to  " + todaysTmaxData.get(todaysTmaxData.size() - 1) + "°" +
                //"\n" + "· humidity: " + todaysHumidityData.get(0) +
                "\n" + "· sunrise: " + sunrise +
                "\n" + "· sunset: " + sunset +
                "\n" + "· weather: " + todaysWeatherData.get(0) +
                "\n\n" + "Tomorrow" + "\n" +
                "· " + tomorrowsTminData.get(0) + "° to  " + tomorrowsTmaxData.get(tomorrowsTmaxData.size() - 1) + "°" +
                " " + tomorrowsWeatherData.get(0) +
                "\n\n" + dateIn2Days + "\n" +
                "· " + in2DaysTminData.get(0) + "° to  " + in2DaysTmaxData.get(in2DaysTmaxData.size() - 1) + "°" +
                " " + in2DaysWeatherData.get(0) +
                "\n\n" + dateIn3Days + "\n" +
                "· " + in3DaysTminData.get(0) + "° to  " + in3DaysTmaxData.get(in3DaysTmaxData.size() - 1) + "°" +
                " " + in3DaysWeatherData.get(0) +
                "\n\n" + dateIn4Days + "\n" +
                "· " + in4DaysTminData.get(0) + "° to  " + in4DaysTmaxData.get(in4DaysTmaxData.size() - 1) + "°" +
                " " + in4DaysWeatherData.get(0) +
                "\n\n" + dateIn5Days + "\n" +
                "· " + in5DaysTminData.get(0) + "° to  " + in5DaysTmaxData.get(in5DaysTmaxData.size() - 1) + "°" +
                " " + in5DaysWeatherData.get(0) +
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
            toastLocationNotFound.show();

            //reset weather info
            textViewDisplayWeatherInfo.setText("");
        }
    }


    public void changeToTab1(View v)
    {
        //debugging
        System.out.println("changed to tab 1");

        //variables
        View linearLayout1 = findViewById(R.id.linearLayout);
        View linearLayout2 = findViewById(R.id.linearLayout2);
        View linearLayout3 = findViewById(R.id.linearLayout3);
        View constraintLayout1 = findViewById(R.id.constraintLayout1);
        View constraintLayout2 = findViewById(R.id.constraintLayout2);
        View constraintLayout3 = findViewById(R.id.constraintLayout3);

        //reset visibility
        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);
        linearLayout3.setVisibility(View.GONE);
        constraintLayout1.setVisibility(View.GONE);
        constraintLayout2.setVisibility(View.GONE);
        constraintLayout3.setVisibility(View.GONE);

        //display tab 1
        linearLayout1.setVisibility(View.VISIBLE);
        constraintLayout1.setVisibility(View.VISIBLE);
    }


    public void changeToTab2(View v)
    {
        //debugging
        System.out.println("changed to tab 2");

        //variables
        View linearLayout1 = findViewById(R.id.linearLayout);
        View linearLayout2 = findViewById(R.id.linearLayout2);
        View linearLayout3 = findViewById(R.id.linearLayout3);
        View constraintLayout1 = findViewById(R.id.constraintLayout1);
        View constraintLayout2 = findViewById(R.id.constraintLayout2);
        View constraintLayout3 = findViewById(R.id.constraintLayout3);

        //reset visibility
        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);
        linearLayout3.setVisibility(View.GONE);
        constraintLayout1.setVisibility(View.GONE);
        constraintLayout2.setVisibility(View.GONE);
        constraintLayout3.setVisibility(View.GONE);

        //display tab 2
        linearLayout2.setVisibility(View.VISIBLE);
        constraintLayout2.setVisibility(View.VISIBLE);
    }


    public void changeToTab3(View v)
    {
        //debugging
        System.out.println("changed to tab 3");

        //variables
        View linearLayout1 = findViewById(R.id.linearLayout);
        View linearLayout2 = findViewById(R.id.linearLayout2);
        View linearLayout3 = findViewById(R.id.linearLayout3);
        View constraintLayout1 = findViewById(R.id.constraintLayout1);
        View constraintLayout2 = findViewById(R.id.constraintLayout2);
        View constraintLayout3 = findViewById(R.id.constraintLayout3);

        //reset visibility
        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);
        linearLayout3.setVisibility(View.GONE);
        constraintLayout1.setVisibility(View.GONE);
        constraintLayout2.setVisibility(View.GONE);
        constraintLayout3.setVisibility(View.GONE);

        //display tab 3
        linearLayout3.setVisibility(View.VISIBLE);
        constraintLayout3.setVisibility(View.VISIBLE);
    }


    public void saveDefaultLocation()
    {
        //variables
        String defaultLocation;
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE); //getActivity().getPreferences(Context.MODE_PRIVATE);
        defaultLocation = editTextTab2.getText().toString();

        //null check
        if(defaultLocation.equals("Null") || defaultLocation.equals(""))
        {
            //save empty default location to shared preferences
            sharedPref.edit().putString("defaultLocation", "").apply();
        }
        else if(!defaultLocation.equals("Null"))
        {
            //format string
            defaultLocation = defaultLocation.substring(0, 1).toUpperCase() + defaultLocation.substring(1);

            //save default location to shared preferences
            sharedPref.edit().putString("defaultLocation", defaultLocation).apply();

            //get default location from shard preferences
            String dl = sharedPref.getString("defaultLocation", "");

            //update UI
            editTextTab2.setText(dl);

            //fetchDefaultLocationData(defaultLocation);
        }
    }


    public String getDefaultLocation()
    {
        //variables
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        String dl = sharedPref.getString("defaultLocation", "");

        //debugging
        //Log.i("Default Location", dl);

        //return value
        return dl;
    }


    public void clearSharedPreferences()
    {
        //variables
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);

        //clear shared preferences data
        sharedPref.edit().clear();
    }


    @Override protected void onCreate(Bundle savedInstanceState)
    {
        //on create
        super.onCreate(savedInstanceState);

        //load startup activity
        setContentView(R.layout.activity_main);

        //set widgets
        editTextEnterWeatherLocation = findViewById(R.id.editTextEnterWeatherLocation);
        textViewDisplayWeatherInfo = findViewById(R.id.textViewDisplayWeatherInfo);
        editTextTab2 = findViewById(R.id.editTextTab2);

        //set error message (location not found)
        toastLocationNotFound = Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT);

        //set default location
        String defaultLocation = getDefaultLocation();

        //fetch default location data
        if(defaultLocation.equals("") || defaultLocation.equals("Null"))
            { /* do nothing */ }
        else
            { editTextTab2.setText(defaultLocation); fetchDefaultLocationData(defaultLocation);}


        //set listener 1 (search weather location on keyboard done)
        editTextEnterWeatherLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    { fetchWeatherData(v); }
                else
                    { return false; }

                //return value
                return false;
            }
        });

        //set listener 2 (search weather location on click)
        editTextEnterWeatherLocation.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editTextEnterWeatherLocation.getText().clear(); //clear enter weather location text
            }
        });

        //set listener 3 (default weather location on keyboard done)
        editTextTab2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    { saveDefaultLocation(); }
                else
                    { return false; }

                //return value
                return false;
            }
        });

        //set listener 4 (default weather location on click)
        editTextTab2.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                editTextTab2.getText().clear(); //clear default weather location text
            }
        });
    }


    //async download class
    public class DownloadTask extends AsyncTask<String, Void, String>
    {
        //try download
        @Override protected String doInBackground(String... urls)
        {
            try
            {
                //variables
                String result = "";
                URL url;
                HttpURLConnection urlConnection = null;
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputReader = new InputStreamReader(inputStream);
                int data = inputReader.read();

                //process data
                while (data != -1) { char current = (char) data; result += current; data = inputReader.read(); }

                //return value
                return result;
            }
            catch (Exception e)
            {
                //debugging
                e.printStackTrace();

                //return value
                return null;
            }
        }

        //post download execute
        @Override protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            handleDownloadData(s);
        }
    }
}
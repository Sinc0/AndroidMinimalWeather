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
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.Math;


//main
public class MainActivity extends AppCompatActivity {
    //widgets
    Button buttonWeatherInfo;
    EditText editTextEnterWeatherLocation;
    TextView textViewDisplayWeatherInfo;
    Toast toastCityNotFound;


    //variables
    String editTextString;
    String downloadUrl;
    String weatherLocation;
    String temperature;
    String feelsLike;
    String humidity;
    String airFeels;
    String description;
    String celsius;
    String country;
    String sunrise;
    String sunset;
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
    String weatherInfoText;
    String formattedWeatherLocation;
    String API_Key = "f394e4fb836c1332f30df5d91d30d9ab";
    //String API_URL_Weather = "https://api.openweathermap.org/data/2.5?q/weather=";
    String API_URL_Weather = "https://api.openweathermap.org/data/2.5/weather?q=";
    String API_URL_Forecast = "https://api.openweathermap.org/data/2.5/forecast?q=";
    int humidityInt = 0;
    int celsiusInt = 0;
    int feelsLikeInt = 0;
    double temperatureDouble = 0;
    double feelsLikeDouble = 0;


    //functions
    public void DownloadWeatherData()
    {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(downloadUrl);
    }

    public void onClickWeatherInfo(View v)
    {
        //variables
        editTextString = editTextEnterWeatherLocation.getText().toString();
        weatherLocation = editTextString;
        downloadUrl = API_URL_Weather + weatherLocation + "&appid=" + API_Key;

        //fetch weather data
        DownloadWeatherData();

        //log
        Log.i("Tag", "Weather Info Button Pressed");
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
                //variables
                //JSONObject jsonObject = new JSONObject(s);

                //set weather info array
                // JSONArray arrayWeatherInfo = new JSONArray(weatherInfo);

                //sort JSON weather content
                /* for (int i = 0; i < arrayWeatherInfo.length(); i++)
                {
                    JSONObject weatherHolder = arrayWeatherInfo.getJSONObject(i);

                    description = weatherHolder.getString("description");
                    description = description.substring(0,1).toUpperCase() + description.substring(1).toLowerCase();
                } */

                //sort JSON content
                JSONObject jsonObject = new JSONObject(s);
                JSONObject mainHolder = jsonObject.getJSONObject("main");
                JSONObject sysHolder = jsonObject.getJSONObject("sys");
                String coordHolder = jsonObject.getString("coord");
                String weatherHolder = jsonObject.getString("weather");
                String visibilityHolder = jsonObject.getString("visibility");
                String windHolder = jsonObject.getString("wind");
                String cloudsHolder = jsonObject.getString("clouds");
                String datetimeHolder = jsonObject.getString("dt");
                String timezoneHolder = jsonObject.getString("timezone");
                String idHolder = jsonObject.getString("id");
                String nameHolder = jsonObject.getString("name");

                //log
                Log.i("JSON Obj", jsonObject.toString());
                Log.i("JSON Main Content", mainHolder.toString());
                Log.i("JSON Sys Content", sysHolder.toString());
                Log.i("JSON Coord Content", coordHolder);
                Log.i("JSON Weather Content", weatherHolder);
                Log.i("JSON Visibility Content", visibilityHolder);
                Log.i("JSON Wind Content", windHolder);
                Log.i("JSON Clouds Content", cloudsHolder);
                Log.i("JSON Datetime Content", datetimeHolder);
                Log.i("JSON Timezone Content", idHolder);
                Log.i("JSON Id Content", idHolder);
                Log.i("JSON Name Content", nameHolder);

                //set weather info variables
                coords = coordHolder;
                temperature = mainHolder.getString("temp");
                tempMax = mainHolder.getString("temp_max");
                tempMin = mainHolder.getString("temp_min");
                feelsLike = mainHolder.getString("feels_like");
                humidity = mainHolder.getString("humidity");
                country = sysHolder.getString("country");
                sunrise = sysHolder.getString("sunrise");
                sunset = sysHolder.getString("sunset");
                weather = weatherHolder;
                visibility = visibilityHolder;
                wind = windHolder;
                clouds = cloudsHolder;
                dt = datetimeHolder;
                locationTimezone = timezoneHolder;
                locationId = idHolder;
                locationName = nameHolder;

                //convert temperatures
                temperatureDouble = Double.parseDouble(temperature); //from string to double
                temperatureDouble = Math.round(temperatureDouble); ////round up numbers
                temperatureDouble = temperatureDouble - 273; //from kelvin to celsius
                celsiusInt = (int) temperatureDouble; //from double to int
                celsius = Integer.toString(celsiusInt); //from int to string

                //convert feels like
                feelsLikeDouble = Double.parseDouble(feelsLike); //from string to double
                feelsLikeDouble = Math.round(feelsLikeDouble); ////round up numbers
                feelsLikeDouble = feelsLikeDouble - 273; //from kelvin to celsius
                feelsLikeInt = (int) feelsLikeDouble; //from double to int
                feelsLike = Integer.toString(feelsLikeInt); //from int to string

                //convert humidity
                humidityInt = Integer.parseInt(humidity); //from int to string

                //set air feels like
                if (humidityInt <= 20) { airFeels = "Dry"; }
                else if (humidityInt >= 20 && humidityInt <= 60) { airFeels = "Normal"; }
                else if (humidityInt >= 60 && humidityInt > 60) { airFeels =  "Wet"; }

                //set background image based on temperature
                if (celsiusInt < 10) { getWindow().setBackgroundDrawableResource(R.drawable.cold); }
                else if (celsiusInt > 10 && celsiusInt <= 20) { getWindow().setBackgroundDrawableResource(R.drawable.normal); }
                else if (celsiusInt > 20) { getWindow().setBackgroundDrawableResource(R.drawable.sun); }

                //display weather info textview
                textViewDisplayWeatherInfo.setVisibility(View.VISIBLE);

                //update weather info text
                weatherInfoText =
                    "Today" + "\n" +
                    "· Temp Now: " + celsius + "°" + "\n" +
                    "· Feels like: " + feelsLike + "°" + "\n" +
                    "· Humidity: " + humidity + "%" + "\n" +
                    "· Air: " + airFeels + "\n" +
                    "· Temp Max: " + tempMin + "°" + "\n" +
                    "· Temp Min: " + tempMax + "°" + "\n" +
                    "· Sunset: " + sunset + "\n" +
                    "· Sunrise: " + sunrise + "\n" +
                    "· Wind: " + wind + "\n" +
                    "\n" +
                    "Details" + "\n" +
                    //"Basics" + "\n" +
                    "· Coords: " + coords + "\n" +
                    "· Temp Now: " + celsius + "°" + "\n" +
                    "· Temp Max: " + tempMin + "°" + "\n" +
                    "· Temp Min: " + tempMax + "°" + "\n" +
                    "· Feels like: " + feelsLike + "°" + "\n" +
                    "· Humidity: " + humidity + "%" + "\n" +
                    //"\n" +
                    //"Details" + "\n" +
                    "· Weather: " + weather + "\n" +
                    //"· Air: " + airFeels + "\n" +
                    "· Country: " + country + "\n" +
                    //"· Sunset: " + sunset + "\n" +
                    //"· Sunrise: " + sunrise + "\n" +
                    "· Visibility: " + visibility + "\n" +
                    //"· Wind: " + wind + "\n" +
                    "· Clouds: " + clouds + "\n" +
                    "· Datetime: " + dt + "\n" +
                    "· Timezone: " + locationTimezone + "\n" +
                    "· Name: " + locationName + "\n" +
                    "· Id: " + locationId + "\n";

                textViewDisplayWeatherInfo.setText(weatherInfoText);

                formattedWeatherLocation = locationName.substring(0, 1).toUpperCase() + locationName.substring(1) + " (" + country + ")";
                editTextEnterWeatherLocation.setText(formattedWeatherLocation);
            }
            catch (Exception e)
            {
                //error message
                e.printStackTrace();
                Log.i("Tag", "City not found");
                toastCityNotFound.show();

                //reset weather info
                textViewDisplayWeatherInfo.setText("");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load main activity
        setContentView(R.layout.activity_main);

        //set main background image
        getWindow().setBackgroundDrawableResource(R.drawable.mainbackground);

        //widgets
        buttonWeatherInfo = findViewById(R.id.buttonWeatherInfo);
        editTextEnterWeatherLocation = findViewById(R.id.editTextEnterWeatherLocation);
        textViewDisplayWeatherInfo = findViewById(R.id.textViewDisplayWeatherInfo);

        //on done listener for enter weather location
        editTextEnterWeatherLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    //doSomething();
                    Log.i("Test", "keyboard done pressed");
                    onClickWeatherInfo(v);
                    //return true;
                }
                else
                {
                    //Log.i("Test", "keyboard done pressed");
                }

                return false;
            }
        });

        //on click listener for enter weather location
        editTextEnterWeatherLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                Log.i("Test", "edit text enter location opened");
                //editTextEnterWeatherLocation.setText("");
                editTextEnterWeatherLocation.getText().clear();
            }
        });

        //location not found error message
        toastCityNotFound = Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT);
    }
}
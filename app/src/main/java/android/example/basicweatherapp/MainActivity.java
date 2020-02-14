package android.example.basicweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    Button buttonWeatherInfo;

    EditText editTextEnterWeatherLocation;

    TextView textViewDisplayWeatherInfo;

    String editTextString;
    String downloadUrl;
    String weatherLocation;
    String ak = "&appid=f394e4fb836c1332f30df5d91d30d9ab";
    String temperature;
    String feelsLike;
    String humidity;
    String airFeels;
    String description;
    String celsius;
    String country;

    int humidityInt = 0;
    int celsiusInt = 0;
    int feelsLikeInt = 0;

    double temperatureDouble = 0;
    double feelsLikeDouble = 0;

    Toast toastCityNotFound;

    public void DownloadWeatherData()
    {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(downloadUrl);
    }

    public void onClickWeatherInfo(View v)
    {

        editTextString = editTextEnterWeatherLocation.getText().toString();

        weatherLocation = editTextString;

        downloadUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + weatherLocation + ak;

        DownloadWeatherData();

        Log.i("Tag", "Weather Info Button Pressed");
    }

    public class DownloadTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

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
                return  null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try
            {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                String mainInfo = jsonObject.getString("main");
                String sysInfo = jsonObject.getString("sys");

                Log.i("JSON Weather Content", weatherInfo);
                Log.i("JSON Main Content", mainInfo);
                Log.i("JSON Sys Content", sysInfo);

                JSONArray arrayWeatherInfo = new JSONArray(weatherInfo);

                //sort JSON weather content
                for (int i = 0; i < arrayWeatherInfo.length(); i++)
                {
                    JSONObject weatherHolder = arrayWeatherInfo.getJSONObject(i);

                    description = weatherHolder.getString("description");

                    description = description.substring(0,1).toUpperCase() + description.substring(1).toLowerCase();

                }

                //sort JSON main content
                JSONObject mainHolder = jsonObject.getJSONObject("main");
                temperature = mainHolder.getString("temp");
                feelsLike = mainHolder.getString("feels_like");
                humidity = mainHolder.getString("humidity");

                //convert temperatures from string to double
                temperatureDouble = Double.parseDouble(temperature);
                feelsLikeDouble = Double.parseDouble(feelsLike);

                //round up numbers
                temperatureDouble = Math.round(temperatureDouble);
                feelsLikeDouble = Math.round(feelsLikeDouble);

                //convert from kelvin to celsius
                temperatureDouble = temperatureDouble - 273;
                feelsLikeDouble = feelsLikeDouble - 273;

                //convert temperatures from double to int
                celsiusInt = (int) temperatureDouble;
                feelsLikeInt = (int) feelsLikeDouble;

                //convert temperatures from int to string
                humidityInt = Integer.parseInt(humidity);
                celsius = Integer.toString(celsiusInt);
                feelsLike = Integer.toString(feelsLikeInt);

                //sets air feel based on humidity number
                if (humidityInt <= 20)
                {
                    airFeels = "Dry";
                }

                if (humidityInt >= 20 && humidityInt <= 60)
                {
                    airFeels = "Normal";
                }

                if (humidityInt >= 60 && humidityInt > 60)
                {
                    airFeels =  "Wet";
                }

                //sets background based on celsius number
                if (celsiusInt < 10)
                {
                    getWindow().setBackgroundDrawableResource(R.drawable.cold);
                }

                if (celsiusInt > 10 && celsiusInt <= 20)
                {
                    getWindow().setBackgroundDrawableResource(R.drawable.normal);
                }

                if (celsiusInt > 20)
                {
                    getWindow().setBackgroundDrawableResource(R.drawable.sun);
                }

                JSONObject sysHolder = jsonObject.getJSONObject("sys");
                country = sysHolder.getString("country");

                textViewDisplayWeatherInfo.setVisibility(View.VISIBLE);

                textViewDisplayWeatherInfo.setText
                (
                    "Temperature: " + celsius + "°" + "\n" +
                    "Feels like: " + feelsLike + "°" + "\n" +
                    "Humidity: " + humidity + "%" + "\n" +
                    "Status: " + description + "\n" +
                    "Air: " + airFeels + "\n" +
                    "Country: " + country
                );

            }

            catch (Exception e)
            {
                e.printStackTrace();

                Log.i("Tag", "City not found");

                toastCityNotFound.show();

                textViewDisplayWeatherInfo.setText("");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setBackgroundDrawableResource(R.drawable.mainbackground);

        buttonWeatherInfo = findViewById(R.id.buttonWeatherInfo);
        editTextEnterWeatherLocation = findViewById(R.id.editTextEnterWeatherLocation);
        textViewDisplayWeatherInfo = findViewById(R.id.textViewDisplayWeatherInfo);

        toastCityNotFound = Toast.makeText(getApplicationContext(), "City not found", Toast.LENGTH_SHORT);

    }
}
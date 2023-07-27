//name
package android.example.basicweatherapp;

//includes
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
        downloadUrl = "https://api.openweathermap.org/data/2.5?q/weather=" + weatherLocation + ak;

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
                return  null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //try parse data
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

                //sort JSON content
                JSONObject mainHolder = jsonObject.getJSONObject("main");
                temperature = mainHolder.getString("temp");
                feelsLike = mainHolder.getString("feels_like");
                humidity = mainHolder.getString("humidity");

                //convert temperatures
                temperatureDouble = Double.parseDouble(temperature); //from string to double
                feelsLikeDouble = Double.parseDouble(feelsLike); //from string to double
                temperatureDouble = Math.round(temperatureDouble); ////round up numbers
                feelsLikeDouble = Math.round(feelsLikeDouble); ////round up numbers
                temperatureDouble = temperatureDouble - 273; //from kelvin to celsius
                feelsLikeDouble = feelsLikeDouble - 273; //from kelvin to celsius
                celsiusInt = (int) temperatureDouble; //from double to int
                feelsLikeInt = (int) feelsLikeDouble; //from double to int
                humidityInt = Integer.parseInt(humidity); //from int to string
                celsius = Integer.toString(celsiusInt); //from int to string
                feelsLike = Integer.toString(feelsLikeInt); //from int to string

                //set airFeels
                if (humidityInt <= 20) { airFeels = "Dry"; }
                if (humidityInt >= 20 && humidityInt <= 60) { airFeels = "Normal"; }
                if (humidityInt >= 60 && humidityInt > 60) { airFeels =  "Wet"; }

                //sets image background based on celsius number
                if (celsiusInt < 10) { getWindow().setBackgroundDrawableResource(R.drawable.cold); }
                if (celsiusInt > 10 && celsiusInt <= 20) { getWindow().setBackgroundDrawableResource(R.drawable.normal); }
                if (celsiusInt > 20) { getWindow().setBackgroundDrawableResource(R.drawable.sun); }

                //set country
                JSONObject sysHolder = jsonObject.getJSONObject("sys");
                country = sysHolder.getString("country");

                //display weather info textview
                textViewDisplayWeatherInfo.setVisibility(View.VISIBLE);

                //update weather info text
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

        //error message
        toastCityNotFound = Toast.makeText(getApplicationContext(), "City not found", Toast.LENGTH_SHORT);
    }
}
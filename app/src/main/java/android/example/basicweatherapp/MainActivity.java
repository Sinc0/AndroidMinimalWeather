package android.example.basicweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button buttonWeatherInfo;
    EditText editTextEnterWeatherLocation;

    String editTextString;
    String downloadUrl;
    String weatherLocation;
    String ak = "&appid=f394e4fb836c1332f30df5d91d30d9ab";
    String temperature;
    String humidity;
    String feelsLike;

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

                Log.i("JSON Weather Content", weatherInfo);
                Log.i("JSON Main Content", mainInfo);

                JSONArray arrayWeatherInfo = new JSONArray(weatherInfo);

                for (int i = 0; i < arrayWeatherInfo.length(); i++)
                {
                    JSONObject weatherHolder = arrayWeatherInfo.getJSONObject(i);

                    Log.i("Description", weatherHolder.getString("description"));
                }

                JSONObject mainHolder = jsonObject.getJSONObject("main");
                temperature = mainHolder.getString("temp");
                feelsLike = mainHolder.getString("feels_like");
                humidity = mainHolder.getString("humidity");


                Log.i("Temperature", temperature);
                Log.i("Feels Like", feelsLike);
                Log.i("Humidity", humidity);

            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonWeatherInfo = findViewById(R.id.buttonWeatherInfo);
        editTextEnterWeatherLocation = findViewById(R.id.editTextEnterWeatherLocation);

    }
}
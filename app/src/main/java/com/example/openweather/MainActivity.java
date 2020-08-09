package com.example.openweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText cityname;
    TextView currentWeather;
    DownloadWeather downloadweather;
    public class DownloadWeather extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... apis) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url=new URL(apis[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char current =(char)data;
                    result+=current;
                    data=reader.read();
                }
                return result;
            }catch (Exception e){
                Log.i("In download error",""+e);
                //Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.i("JSON",s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherinfo=jsonObject.getString("weather");
                //Log.i("Weather Array",weatherinfo);
                JSONArray jsonArray=new JSONArray(weatherinfo);
                String message="";
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonpart=jsonArray.getJSONObject(i);
                    String main=jsonpart.getString("main");
                    String desc=jsonpart.getString("description");
                    //Log.i("main",jsonpart.getString("main"));
                    //Log.i("description",jsonpart.getString("description"));
                    message=main+":     "+desc+"\r\n";
                    //currentWeather.setText(jsonpart.getString("main")+":    "+jsonpart.getString("description"));
                }
                if(message!="")
                    currentWeather.setText(message);
                else
                    Toast.makeText(MainActivity.this, "Nothing Found", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Log.i("On Post Execute error",""+e);
                Toast.makeText(MainActivity.this, "City Not Found :(", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityname=(EditText)findViewById(R.id.cityName);
        currentWeather=(TextView)findViewById(R.id.currentweather);
//        downloadweather=new DownloadWeather();
//        downloadweather.execute("https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=439d4b804bc8187953eb36d2a8c26a02");
    }
    public void findweather(View view){
        String m="http://api.openweathermap.org/data/2.5/weather?q="+cityname.getText()+"&appid=3529bd5a6fdb4cfbe3016fcfff41a366";
        //In our case the api is handling input with spaces but for rest do this
        try {
            String encodedCityName = URLEncoder.encode(cityname.getText() + "", "UTF-8");
            m="http://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=3529bd5a6fdb4cfbe3016fcfff41a366";
        }
        catch(Exception e){
            Log.i("encoding error",""+e);
            Toast.makeText(MainActivity.this, "Something Went Wrong:(", Toast.LENGTH_SHORT).show();
        }
        downloadweather=new DownloadWeather();
        downloadweather.execute(m);
        //Code to let keyboard appear in such a way that it does not block textview.No need in this case but for further use
        InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(currentWeather.getWindowToken(),0);
    }
}
package kr.ac.jbnu.se.sentiment.diary;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class write extends AppCompatActivity implements Runnable {
    Button pictureBtn;
    Uri fileUri;

    public static final int REQUEST_CODE_MENU2 = 101;
    public static final int REQUEST_CODE_MENU3 = 102;

    Button writeXbtn;
    EditText editText;
    Button btn_selectDate;
    Button writeInsert;
    SQLiteDatabase db;
    String str;
    String st;

    int imageIndex=0;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat= new SimpleDateFormat("yyyy-MM-dd");

    ImageView happy, soso, sad;

    //ImageView imageView5;
    String uriBool = "none";

    String main;
    static RequestQueue requestQueue;
    ImageView clear, thunderstorm, rain, snow, atmosphere, clouds;
    int weatherNum;

    //GPS
    private static final int REQUEST_CODE_LOCATION = 2;
    double lat = 0;
    double lng = 0;
    LocationManager locationManager;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        clear = (ImageView)findViewById(R.id.clear);
        thunderstorm = (ImageView)findViewById(R.id.thunderstorm);
        rain = (ImageView)findViewById(R.id.rain);
        snow = (ImageView)findViewById(R.id.snow);
        atmosphere = (ImageView)findViewById(R.id.atmosphere);
        clouds = (ImageView)findViewById(R.id.clouds);

        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        getMyLocation();//위치 권한
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // GPS 프로바이더 사용가능여부
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d("GPSWrite***", "isGPSEnabled=" + isGPSEnabled);
        Log.d("GPSWrite***", "isNetworkEnabled=" + isNetworkEnabled);

        getLo(locationManager);// 자동이 아닌 수동으로 위치 구하기


        editText = findViewById(R.id.editText);

        btn_selectDate = findViewById(R.id.btn_selectDate);
        btn_selectDate.setText(getTime());
        btn_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clock(view);
            }
        });

        happy = (ImageView)findViewById(R.id.happy);
        soso = (ImageView)findViewById(R.id.soso);
        sad = (ImageView)findViewById(R.id.sad);

        //수정을 누른다면 여기
        String updateDate=getIntent().getStringExtra("updateDate");
        String updateContent = getIntent().getStringExtra("updateContent");
//        int updateResId = getIntent().getIntExtra("updateImgResId", 0);
        String updatePictureUri = getIntent().getStringExtra("updatePictureUri");
        int position = getIntent().getIntExtra("position", -1);


        if(updateDate == null) { //수정 버튼을 누른게 아니라 글 작성 버튼을 눌러서 여기 왔을 때
            makeWeatherRequest(lat, lng);//날씨
        }
        else {  //수정 버튼을 눌렀을 때 눌러서 여기 왔을 떄
            Log.d(updateDate, "btn_selectDate.getText().toString()***");
            Log.d(getTime(), "getTime()***");

            if(getTime().equals(updateDate))
            {
                //Log.d("날짜가 같음", "clcl***");
                makeWeatherRequest(lat, lng);
            }
            if(!getTime().equals(updateDate))
            {
                //Log.d("날짜가 같지 않음", "clcl***");
                //Toast.makeText(getApplicationContext(), "날씨 저장이 되지 않습니다.", Toast.LENGTH_LONG).show();
                weatherNum = -1;

                clear.setVisibility(View.INVISIBLE);
                thunderstorm.setVisibility(View.INVISIBLE);
                rain.setVisibility(View.INVISIBLE);
                snow.setVisibility(View.INVISIBLE);
                atmosphere.setVisibility(View.INVISIBLE);
                clouds.setVisibility(View.INVISIBLE);

            }

            editText.setText(updateContent);
            btn_selectDate.setText(updateDate);
//
//
//            imageIndex= updateResId; //mood
//
//            if(updateResId == 0)
//            {
//                happy.setVisibility(View.VISIBLE);
//            }
//            if(updateResId == 1)
//            {
//                sad.setVisibility(View.VISIBLE);
//            }
//            if(updateResId == 2)
//            {
//                soso.setVisibility(View.VISIBLE);
//            }


        }

        writeInsert = findViewById(R.id.writeInsert);

        writeInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createDB();
                createTable();

                String content = editText.getText().toString();
                String dates = btn_selectDate.getText().toString();

                int updatePkNum= getIntent().getIntExtra("updatePkNum", -1);

                if(updatePkNum ==  -1)//insert 하기
                {
                    st = String.valueOf(editText.getText());
                    Thread th = new Thread((Runnable) write.this);
                    th.start();
                    insert(dates, imageIndex, content, uriBool, weatherNum);
                }
                else {//수정 하기
                    st = String.valueOf(editText.getText());
                    Thread th = new Thread((Runnable) write.this);
                    th.start();

                    if(getTime().equals(dates)) {
                        update(dates, imageIndex, content, uriBool, weatherNum, updatePkNum);
                    }
                    else {
                        update(dates, imageIndex, content, uriBool, updatePkNum);
                    }
                    Log.d("dates***", dates);

                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("position", position);
                startActivityForResult(intent, REQUEST_CODE_MENU2);
                finish();
            }
        });

        writeXbtn = findViewById(R.id.writeXbtn);

        writeXbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    public void createDB()
    {
        db = openOrCreateDatabase("mooda", MODE_PRIVATE, null);
        Log.d("db 생성", "write***");
    }

    public void createTable()
    {
        String sql = "create table if not exists mooda " +
                "(num integer primary key autoincrement, " +
                "dates date, mood integer, content text, pictureUri text, weather integer);";

        db.execSQL(sql);
    }

    void insert(String dates, int mood, String content, String pictureUri, int weatherNums)
    {
        String sql = "insert into mooda(dates, mood, content, pictureUri, weather) values('"
                + dates + "', " + mood + ", '" + content +"', '"+pictureUri +"', "+ weatherNums +");";

        db.execSQL(sql);
    }
    void update(String dates, int mood, String content, String pictureUri, int weatherNums ,int updatePkNum)
    {
        String sql = "update mooda set dates = '"+dates+"', mood = "+mood+", content = '"
                +content+"', pictureUri = '"+pictureUri+"', weather = "+ weatherNums +" where num = "+updatePkNum;

        db.execSQL(sql);
    }
    void update(String dates, int mood, String content, String pictureUri, int updatePkNum)
    {
        String sql = "update mooda set dates = '"+dates+"', mood = "+mood+", content = '"
                +content+"', pictureUri = '"+pictureUri+"' where num = "+updatePkNum;

        db.execSQL(sql);
    }

    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);

        //Log.d(mFormat.format(mDate)+"", "clock***");
        return mFormat.format(mDate);
    }

    public void clock(View view)
    {
        if(view == btn_selectDate)
        {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);


            //Log.d("hour***", mYear +" , "+hour+", "+ minute);




            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                            if(dayOfMonth < 10)
                            {
                                String strDayOfMonth = "0"+dayOfMonth;
                                btn_selectDate.setText(year+"-" + (month+1) + "-" + strDayOfMonth);

                                if(getTime().equals(btn_selectDate.getText().toString()))
                                {
                                    //Log.d("날짜가 같음", "clcl***");
                                    makeWeatherRequest(lat, lng);
                                }
                                if(!getTime().equals(btn_selectDate.getText().toString()))
                                {
                                    //Log.d("날짜가 같지 않음", "clcl***");
                                    Toast.makeText(getApplicationContext(), "날씨 및 시간 저장이 되지 않습니다.", Toast.LENGTH_LONG).show();
                                    weatherNum = -1;

                                    clear.setVisibility(View.INVISIBLE);
                                    thunderstorm.setVisibility(View.INVISIBLE);
                                    rain.setVisibility(View.INVISIBLE);
                                    snow.setVisibility(View.INVISIBLE);
                                    atmosphere.setVisibility(View.INVISIBLE);
                                    clouds.setVisibility(View.INVISIBLE);
                                }
                            }
                            else if(dayOfMonth >= 10)
                            {
                                btn_selectDate.setText(year+"-" + (month+1) + "-" + dayOfMonth);

                                if(getTime().equals(btn_selectDate.getText().toString()))
                                {
                                    //Log.d("날짜가 같음", "clcl***");
                                    makeWeatherRequest(lat, lng);
                                }
                                if(!getTime().equals(btn_selectDate.getText().toString()))
                                {
                                    //Log.d("날짜가 같지 않음", "clcl***");
                                    Toast.makeText(getApplicationContext(), "날씨 저장이 되지 않습니다.", Toast.LENGTH_LONG).show();
                                    weatherNum = -1;

                                    clear.setVisibility(View.INVISIBLE);
                                    thunderstorm.setVisibility(View.INVISIBLE);
                                    rain.setVisibility(View.INVISIBLE);
                                    snow.setVisibility(View.INVISIBLE);
                                    atmosphere.setVisibility(View.INVISIBLE);
                                    clouds.setVisibility(View.INVISIBLE);
                                }
                            }

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }



    }

    //캘린더 끝



    //날씨

    public void makeWeatherRequest(double lat, double lng)
    {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+ lat +"&lon="+lng+"&appid=9429a8a51df3d29e1ea25e60417ba76a";

        if(lat == 0)//위치 값을 못 받아온 경우는 대강 서울 날씨 정보를 가져온다.
        {
            url = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=9429a8a51df3d29e1ea25e60417ba76a";
        }


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //println("응답 - > " + response);

                Log.d(response,"response***");

                String json = response;
                try {
                    JSONObject jsonObject = new JSONObject(json);

                    String weather = jsonObject.getString("weather");

                    JSONArray jarray = new JSONArray(weather);
                    for(int i=0; i < jarray.length(); i++){
//                    for(int i=0; i < 1; i++){
                        JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                        main = jObject.getString("main");


                        Log.d(main,"main***");
                        Log.d(jarray.length()+"","main***");

                        if(main.equals("Clear"))
                        {
                            clear.setVisibility(View.VISIBLE);
                            weatherNum = 0;
                        }
                        if(main.equals("Thunderstorm"))
                        {
                            thunderstorm.setVisibility(View.VISIBLE);
                            weatherNum = 1;
                        }
                        if(main.equals("Rain")  || main.equals("Drizzle"))
                        {
                            rain.setVisibility(View.VISIBLE);
                            atmosphere.setVisibility(View.INVISIBLE);
                            weatherNum = 2;

                            break;//비랑 안개랑 같이 끼기도 해서 이렇게 한다.
                        }
                        if(main.equals("Snow"))
                        {
                            snow.setVisibility(View.VISIBLE);
                            weatherNum = 3;
                        }
                        if(main.equals("Atmosphere")  || main.equals("Mist")  || main.equals("Smoke")  || main.equals("Haze")  ||
                        main.equals("Dust")  || main.equals("Fog")  || main.equals("Sand")  || main.equals("Ash")
                        || main.equals("Squall") || main.equals("Tornado"))
                        {
                            atmosphere.setVisibility(View.VISIBLE);
                            weatherNum = 4;
                        }
                        if(main.equals("Clouds"))
                        {
                            clouds.setVisibility(View.VISIBLE);
                            weatherNum = 5;
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("에러 - > " + error, "error***");
                    }
                }
        )
        {
            protected Map<String, String> getParms() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };


        request.setShouldCache(false);
        requestQueue.add(request);


    }

    private void getMyLocation() {//위치 권한
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("사용자에게 권한을 요청해야함"   , "log***");
            
            //권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION);

            return;//자동 반복 방지
        }
        else {
            Log.d("사용자에게 권한요청 안해도됨"   , "log***");

        }
    }

    private void getLo(LocationManager locationManager) { //GPS
        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("사용자에게 권한을 요청해야함"   , "log***");

            //권한 요청 사실 이건 getMyLocation() 불러와도 되긴 하는데 그럴 경우 안드로이드가 빨간 밑줄을 친다. 그래서 그냥 함수 호출을 안 하고 또 적었다.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION);

            return;//자동 반복 방지
        }
        else {
            Log.d("사용자에게 권한요청 안해도됨"   , "log***");

        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
            lng = lastKnownLocation.getLatitude();
            lat = lastKnownLocation.getLatitude();
            Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);

            //Toast.makeText(this, lng + ", latitude=" + lat, Toast.LENGTH_LONG).show();
        }
    }
    // handler = 백그라운드 thread에서 전달된 메시지 처리(UI변경 등을 여기서 해줌.)
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(str.equals("positive")){
                imageIndex = 0;
                happy.setVisibility(View.VISIBLE);
                soso.setVisibility(View.INVISIBLE);
                sad.setVisibility(View.INVISIBLE);
            } else if(str.equals("negative")){
                imageIndex = 1;
                happy.setVisibility(View.INVISIBLE);
                soso.setVisibility(View.INVISIBLE);
                sad.setVisibility(View.VISIBLE);
            } else {
                imageIndex = 2;
                happy.setVisibility(View.INVISIBLE);
                soso.setVisibility(View.VISIBLE);
                sad.setVisibility(View.INVISIBLE);
            }
        }
    };

    private SentimentVO sentimentOk(String content) throws JSONException {
        //----------------

        String clientId = "ko113bshe9";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "t48g6KVs1UYFUpaxcjjuKHpeXSpOgsos5e861K47";//애플리케이션 클라이언트 시크릿값";

        //버퍼 아래서 이동(원래있던 복사한 파일)
        BufferedReader br = null;//전송받은 정보가 있는 InputStream
        StringBuffer response = new StringBuffer();  //버퍼 아래서 이동(원래있던 복사한 파일)

        HashMap map = new HashMap();


        try {
            //클라우드로 보낼 데이터를 준비한다. 제이슨은 {key:value}형식으로 된것이 제이슨 오브젝이다.
            JSONObject jsonData = new JSONObject();//처음에 프로젝트 만들때 org.json을 추가했다. | 이객체안에 title, content 내용을 넣는다.
            jsonData.put("content", content);

            String jsonStr = jsonData.toString(); //문자열로 바꾼것 {"content":"글내용..."}
            System.out.println("jsonString"+ jsonStr);
            //-------------------------

            String apiURL = "https://naveropenapi.apigw.ntruss.com/sentiment-analysis/v1/analyze"; // 감정분석
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            // multipart request
            // String boundary = "---" + System.currentTimeMillis() + "---";
            con.setRequestMethod("POST");//전송을 포스트 방식

            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            con.setRequestProperty("Content-Type", "application/json");

            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.write(jsonStr.getBytes());
            dos.flush();
            dos.close();

            //데이터 보내기 끝

            //데이터 받기: 응답받기

            int responseCode = con.getResponseCode();
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 오류 발생
                System.out.println("error!!!!!!! responseCode= " + responseCode);
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            //전송받은 InputStream의 값을 읽어내기
            String inputLine;
            if(br != null) {
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        //-----------------
        System.out.println(response.toString());
        //**************************
        JSONObject jsonResult = new JSONObject(response.toString());
        // 전체 감정분석
        JSONObject document = jsonResult.getJSONObject("document");
        SentimentVO analVO = new SentimentVO();
        analVO.setSentiment(document.getString("sentiment"));

        JSONObject confidence = document.getJSONObject("confidence");
        analVO.setNeutral(confidence.getDouble("neutral"));
        analVO.setPositive(confidence.getDouble("positive"));
        analVO.setNegative(confidence.getDouble("negative"));

        return analVO;
    }

    @Override
    public void run() {
        Log.d("버튼 눌림", "버튼 눌림");
        try {
            SentimentVO result = sentimentOk(st);
            System.out.println(result.getSentiment());
            str = (String) result.getSentiment();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myLog_error", "에러발생했습니다...");
        }
        handler.sendEmptyMessage(0);
    }



    

}























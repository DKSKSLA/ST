package com.example.myapplication;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.gson.Gson;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    Context context;

    private MapView mapView; // 지도 뷰 (fragment)
    private NaverMap naverMap; // 인터페이스 사용을 위한 NaverMap
    private InfoWindow infoWindow = new InfoWindow();

    private final int LOCATION_PERMISSTION_REQUEST_CODE = 1000;
    private UiSettings uiSettings; // 네이버 지도 유저 인터페이스
    private FusedLocationSource locationSource; // 위치를 반환하는 구현체

    private Marker marker = new Marker();

    String finalAddress_addr = null; // 최종적으로 나온 지번주소
    String finalAddress_roadaddr = null; //         도로명주소

    private SearchView searchView;
    
    String search = null; // 검색한 텍스트
    LatLng searchLatLng = null; // 검색한 장소 경도위도
    LatLng clickedLatLng = null; // 클릭한 위치 경도위도

    Gson gson = null; // json 받아오는거
    Address address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        context = this;

        // 지도 뷰 객체
        mapView = findViewById(R.id.map_view);
        // NaverMap 객체 생성
        mapView.getMapAsync(this); // OnMapReady 호출

        // 검색창 뷰 객체
        searchView = findViewById(R.id.search_view);

        // 권한요청
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE);

        // 검색창 뷰 리스너
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { // 검색 했을 시 호출
                search = query; // 전역변수에 검색값 저장
                requestGeocode(false); // 주소값 변환 요청

                // TODO 미안하다 이거밖에 방법이 없었다 - 마커 생성은 메인 스레드에서만 됨. OnCreate 안에서만 된다는건데, 여기서 해버리면 검색한 주소의 좌표 받아오는데(requestGeocode) 시간이 걸려서 딜레이 주는수밖에 없었음
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try {
                            // 지도에 마커 생성 작업
                            // 마커 위치 설정
                            marker.setPosition(searchLatLng);
                            // 실제 마커 생성
                            marker.setMap(naverMap);

                            // 마커 정보 창 설정
                            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(context) { // 마커 정보 창 갱신
                                @Override
                                public CharSequence getText(InfoWindow infoWindow) {
                                    Log.d("마커 정보창 텍스트", "변경");

                                    return search; // 검색한 내용을 그대로 출력 TODO 이건 좀 아닌거같은데 출력 어떻게 해야하지?
                                }
                            });

                            // 마커 정보창 위치 설정
                            infoWindow.setPosition(searchLatLng);
                            // 실제 정보창 생성
                            infoWindow.open(marker);
                        } catch (Overlay.InvalidCoordinateException e){
                            Toast.makeText(context, "정확한 도로명 주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 1000);// 1.0초 딜레이를 준 후 시작

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { // 검색창에 글자 하나 칠 때마다 호출
                return false;
            }
        });
    }

    // 위치 권한 관련 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(NaverMap map) { // NaverMap 객체 생성 시 호출
        naverMap = map;

        naverMap.setMinZoom(10.5); // 지도 축소 제한

        uiSettings = naverMap.getUiSettings(); // 유저 인터페이스 사용
        uiSettings.setLocationButtonEnabled(true); // 현재 위치 버튼 활성화
        naverMap.setLocationSource(locationSource); // 현재 위치 버튼 활성화

        // 지도 클릭 시 이벤트
        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(PointF pointF, LatLng _clickedLatLng) {
                clickedLatLng = _clickedLatLng; // 전역변수에 터치한 좌표 저장

                requestGeocode(true);
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try {
                            if (address.results[0].region.area1.name != null)
                                finalAddress_roadaddr = address.results[0].region.area1.name + " ";
                            if (address.results[0].region.area2.name != null)
                                finalAddress_roadaddr += address.results[0].region.area2.name + " ";
                            if (address.results[0].region.area3.name != null)
                                finalAddress_roadaddr += address.results[0].region.area3.name + " ";
                            if (address.results[0].region.area4.name != null) // 이거 왠진 모르겠는데 아무것도 안나옴
                                finalAddress_roadaddr += address.results[0].region.area4.name + " ";
                            if (address.results[0].land.addition0.value != null)
                                finalAddress_roadaddr += address.results[0].land.addition0.value;

                            marker.setPosition(clickedLatLng);
                            marker.setMap(naverMap);

                            // 마커 정보 창 설정
                            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(context) { // 마커 정보 창 갱신
                                @Override
                                public CharSequence getText(InfoWindow infoWindow) {
                                    Log.d("마커 정보창 텍스트", "변경");

                                    return finalAddress_roadaddr; // 리버스 지오코딩 사용해서 좌표를 주소로 변환
                                    // return marker.getPosition().toString();
                                }
                            });

                            // 마커 정보창 위치 설정
                            infoWindow.setPosition(clickedLatLng);
                            // 실제 정보창 생성
                            infoWindow.open(marker);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            Toast.makeText(context, "도로명 주소를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        } catch (NullPointerException e){
                            Toast.makeText(context, "도로명 주소를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 300);// 0.3초 딜레이를 준 후 시작

                Log.d("", "맵 클릭");
            }
        });

        infoWindow.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                Toast.makeText(context, "클릭", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    // 리버스 지오코딩을 요청하는지를 boolean 매개변수로 판별함
    public void requestGeocode(boolean requestReverseGeoCode) {
        new Thread(() -> {
            try {
                BufferedReader bufferedReader;
                StringBuilder stringBuilder = new StringBuilder();
                HttpURLConnection conn = null;
                URL url = null;
                String query = null;

                if (requestReverseGeoCode) { // 좌표 -> 주소 (리버스 지오코딩)
                    String coord = String.format("%.7f", clickedLatLng.longitude) + "," + String.format("%.7f", clickedLatLng.latitude); // 경도 위도 저장;
                    Log.d("클릭한 좌표값", coord);

                    query = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords="
                            + coord + "&sourcecrs=epsg:4326&output=json&orders=roadaddr&output=xml"; // coord 기준으로 json 받아오는 쿼리문
                }
                else { // 주소 -> 좌표 (지오코딩)
                    query = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + URLEncoder.encode(search, "UTF-8");
                }

                url = new URL(query);
                conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "up10bmidqj"); // Client ID
                    conn.setRequestProperty("X-NCP-APIGW-API-KEY", "kAz0pY2vGXJlGOAngfIIXiCchPamadb9bg9oSEpK"); // Client Secret
                    conn.setDoInput(true);
                    
                    int responseCode = conn.getResponseCode();
                    
                    Log.d("응답코드 200이면 성공", String.valueOf(responseCode));
                    if (responseCode == 200) { // 200 = 정상 응답
                        bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }

                    if (requestReverseGeoCode) { // 좌표 -> 주소 (리버스 지오코딩)
                        gson = new Gson(); // json 받아오는거
                        address = gson.fromJson(String.valueOf(stringBuilder), Address.class);
                    }

                    if (!requestReverseGeoCode) { // 주소 -> 좌표 (지오코딩)
                        try{
                            int indexFirst;
                            int indexLast;

                            if (stringBuilder != null) {
                                indexFirst = stringBuilder.indexOf("\"x\":\"");
                                indexLast = stringBuilder.indexOf("\",\"y\":");
                                String x = stringBuilder.substring(indexFirst + 5, indexLast);

                                indexFirst = stringBuilder.indexOf("\"y\":\"");
                                indexLast = stringBuilder.indexOf("\",\"distance\":");
                                String y = stringBuilder.substring(indexFirst + 5, indexLast);
                                searchLatLng = new LatLng(Double.valueOf(y), Double.valueOf(x));
                                Log.d("검색한 주소 좌표", searchLatLng.toString());

                                // 카메라 이동시키기
                                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(searchLatLng).animate(CameraAnimation.Easing);
                                naverMap.moveCamera(cameraUpdate);
                            }
                        } catch (StringIndexOutOfBoundsException e){
                            Log.d("검색 오류", e.toString());
                        }
                    }

                    bufferedReader.close();
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        Log.d("qwe", "지오코드 실행");
    }
}


// json 주소 처리하기 위한 클래스들
class Addition {
    String type;
    String value;
}
class Area {
    String name;
    Coords coords;
}
class Area1 {
    String name;
    Coords coords;
    String alias;
}
class Coords {
    String crs;
    double x;
    double y;
}
class Land {
    String type;
    String number1;
    String number2;
    Addition addition0; // 건물명
    Addition addition1; // zipcode
    Addition addition2; // roadGroupCode
    Addition addition3;
    Addition addition4;
    String name;
    Coords coords;
}
class Region {
    Area area0; // 국가
    Area1 area1; // 도
    Area area2; // 시 구
    Area area3; // 동
    Area area4; // 뭔지 모르겠음
}
class Status {
    long code;
    String name;
    String message;
}
class Address {
    Status status;
    Result[] results;
}

class Result {
    String name;
    String id;
    String type;
    String mappingID;
    Region region;
    Land land;
}
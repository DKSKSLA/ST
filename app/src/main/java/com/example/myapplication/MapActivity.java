package com.example.myapplication;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;

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
    String infoText = null; // 마커 정보창 텍스트

    private SearchView searchView;

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                requestGeocode(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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

        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() { // 지도 클릭 시 이벤트
            @Override
            public void onMapClick(PointF pointF, LatLng clickedLatLng) {
                marker.setPosition(clickedLatLng); // 마커 위치 설정

                marker.setMap(naverMap); // 마커 생성

                // 마커 정보 창 설정
                infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(context) { // 마커 정보 창 갱신
                    @Override
                    public CharSequence getText(InfoWindow infoWindow) {
                        Log.d("qwe", "setAdapter 호출");
                        requestReverseGeocode(clickedLatLng);

                        infoText = finalAddress_roadaddr;

//                        if (finalAddress_roadaddr != null) // 도로명주소가 받아졌으면 도로명주소 출력
//                            infoText = finalAddress_roadaddr;
//                        else                                // TODO 아니면 지번주소 출력
//                            infoText = finalAddress_addr;

                        return infoText; // 리버스 지오코딩 사용해서 좌표를 주소로 변환
                        // return marker.getPosition().toString();
                    }
                });
                Log.d("qwe", "맵 클릭");

                infoWindow.setPosition(clickedLatLng); // 마커 정보창 위치 설정
                infoWindow.open(marker); // 마커 위치 기반으로 위치 정보 생성

            }
        });
    }

    // TODO 굉장히 비효율적이게 리버스 지오코딩이랑 겹치는 부분이 많음
    public void requestGeocode(String search) {
        new Thread(() -> {
            try {
                BufferedReader bufferedReader;
                StringBuilder stringBuilder = new StringBuilder();
                String addr = search; // "ex) 분당구 성남대로 601";
                String query = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + URLEncoder.encode(addr, "UTF-8");
                URL url = new URL(query);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "up10bmidqj"); // Client ID
                    conn.setRequestProperty("X-NCP-APIGW-API-KEY", "kAz0pY2vGXJlGOAngfIIXiCchPamadb9bg9oSEpK"); // Client Secret
                    conn.setDoInput(true);

                    int responseCode = conn.getResponseCode();

                    if (responseCode == 200) { // 200 = 성공
                        bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }

                    int indexFirst;
                    int indexLast;

                    indexFirst = stringBuilder.indexOf("\"x\":\"");
                    indexLast = stringBuilder.indexOf("\",\"y\":");
                    String x = stringBuilder.substring(indexFirst + 5, indexLast);

                    indexFirst = stringBuilder.indexOf("\"y\":\"");
                    indexLast = stringBuilder.indexOf("\",\"distance\":");
                    String y = stringBuilder.substring(indexFirst + 5, indexLast);

                    LatLng searchLatLng = new LatLng(Double.valueOf(y), Double.valueOf(x));

                    // 카메라 이동시키기
                    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(searchLatLng).animate(CameraAnimation.Easing);
                    naverMap.moveCamera(cameraUpdate);

                    marker.setPosition(searchLatLng); // 마커 위치 설정

                    // 마커 정보 창 설정 (TODO 개 쌉 버러지 코드여서 앱 확장 시 무조건 바꿔야됨)
                    infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(context) { // 마커 정보 창 갱신
                        @Override
                        public CharSequence getText(InfoWindow infoWindow) {
                            return search; // 리버스 지오코딩 사용해서 좌표를 주소로 변환
                            // return marker.getPosition().toString();
                        }
                    });

                    marker.setMap(naverMap); // 마커 생성

                    infoWindow.setPosition(searchLatLng); // 마커 정보창 위치 설정
                    infoWindow.open(marker); // 마커 위치 기반으로 위치 정보 생성

                    bufferedReader.close();
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    // TODO 존나 비효율적이게 지오코딩이랑 겹치는 부분 개많음
    public void requestReverseGeocode(LatLng clickedLatLng) {
        new Thread(() -> {
            try {
                BufferedReader bufferedReader;
                StringBuilder stringBuilder = new StringBuilder();
                String coord = String.format("%.7f", clickedLatLng.longitude) + "," + String.format("%.7f", clickedLatLng.latitude); // "127.1234308,37.3850143"; //  // "111,222" 형태로 위도 경도가 저장됨;
                Log.d("qwe", coord);

                // 도로명 주소
                String query = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords="
                        + coord + "&sourcecrs=epsg:4326&output=json&orders=addr,admcode,roadaddr&output=xml"; // coord 기준으로 json 받아오는 쿼리문
                URL url = new URL(query);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "up10bmidqj"); // Client ID
                    conn.setRequestProperty("X-NCP-APIGW-API-KEY", "kAz0pY2vGXJlGOAngfIIXiCchPamadb9bg9oSEpK"); // Client Secret
                    conn.setDoInput(true);

                    int responseCode = conn.getResponseCode();
                    Log.d("qwe", String.valueOf(responseCode));
                    if (responseCode == 200) { // 200 = 정상 응답
                        bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    }
                    else {
                        bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }

                    Gson gson = new Gson(); // json 받아오는거
                    Address address = gson.fromJson(String.valueOf(stringBuilder), Address.class);

                    if (address.results[0].region.area1.name != null)
                        finalAddress_roadaddr = address.results[0].region.area1.name;
                    if (address.results[0].region.area2.name != null)
                        finalAddress_roadaddr += address.results[0].region.area2.name;
                    if (address.results[0].region.area3.name != null)
                        finalAddress_roadaddr += address.results[0].region.area3.name;
                    if (address.results[0].region.area4.name != null)
                        finalAddress_roadaddr += address.results[0].region.area4.name;
                    if (address.results[0].land.addition0.value != null)
                        finalAddress_roadaddr += address.results[0].land.addition0.value;

                    bufferedReader.close();
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Log.d("qwe", "requestGeocode() 호출");
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
    Area area4;
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
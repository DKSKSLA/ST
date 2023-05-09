package com.example.myapplication;

import android.content.Context;
import android.graphics.PointF;
import android.icu.text.IDNA;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    Context context;

    private MapView mapView; // 지도 뷰 (fragment)
    private NaverMap naverMap; // 인터페이스 사용을 위한 NaverMap
    private InfoWindow infoWindow = new InfoWindow();

    private final int LOCATION_PERMISSTION_REQUEST_CODE = 1000;
    private UiSettings uiSettings; // 네이버 지도 유저 인터페이스
    private FusedLocationSource locationSource; // 위치를 반환하는 구현체

    private Marker marker = new Marker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        context = this;

        // 지도 뷰 객체
        mapView = findViewById(R.id.map_view);
        // NaverMap 객체 생성
        mapView.getMapAsync(this); // OnMapReady 호출

        // 권한요청
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE);
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
                marker.setPosition(clickedLatLng);
                marker.setMap(naverMap); // 마커 생성

                // 마커 정보 창 설정
                infoWindow.setPosition(clickedLatLng);
                infoWindow.open(marker); // 마커 위치 기반으로 위치 정보 생성
            }
        });

        // TODO 마커 정보 창
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(context) {
            @Override
            public CharSequence getText(InfoWindow infoWindow) {
                // TODO Reverse Geocoding 사용해서 좌표를 주소로 변환
                return marker.getPosition().toString();
            }
        });
    }
}

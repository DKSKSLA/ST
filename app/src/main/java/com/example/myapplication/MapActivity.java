package com.example.myapplication;

import android.graphics.PointF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView; // 지도 뷰 (fragment)
    private NaverMap naverMap; // 인터페이스 사용을 위한 NaverMap

    private final int LOCATION_PERMISSTION_REQUEST_CODE = 1000;
    private UiSettings uiSettings; // 네이버 지도 유저 인터페이스
    private FusedLocationSource locationSource; // 위치를 반환하는 구현체

    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        // 지도 뷰 객체
        mapView = findViewById(R.id.map_view);
        // NaverMap 객체 생성
        mapView.getMapAsync(this); // OnMapReady 호출

        // 권한요청
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE);
    }

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
    public void onMapReady(NaverMap map) {
        naverMap = map;

        naverMap.setMinZoom(10.5); // 지도 축소 제한

        // 유저 인터페이스 사용
        uiSettings = naverMap.getUiSettings();

        uiSettings.setLocationButtonEnabled(true); // 현재 위치 버튼 활성화
        naverMap.setLocationSource(locationSource); // TODO 현재 위치 버튼 구현

        marker = new Marker(); // 마커 생성
        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(PointF pointF, LatLng clickedLatLng) {
                if (marker.getMap() != null)
                    marker.setMap(null); // 기존 마커 제거
                marker.setPosition(clickedLatLng);
                marker.setMap(naverMap);
            }
        });
    }
}

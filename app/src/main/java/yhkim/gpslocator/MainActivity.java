package yhkim.gpslocator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements OnPermissionCallback{

    private Button getCoordBtn;
    private TextView coordTxt;
    private LocationManager locationManager;
    private LocationListener locationListener;
    PermissionHelper permissionHelper;

    final String DIALOG_TITLE = "GPS Information Required";
    final String DIALOG_MESSAGE = "App needs access to GPS to find nearby users";
    final String PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final long GPS_REFRESH_TIME = 5000; //in milliseconds
    private static final long GPS_MIN_DISTANCE = 1; //in meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getCoordBtn = (Button) findViewById(R.id.retrieve_location_button);
        coordTxt = (TextView) findViewById(R.id.CoordinatesText);
        permissionHelper = PermissionHelper.getInstance(this);  //MAKES SETTING PERMISSIONS EASIER

        permissionHelper.setForceAccepting(false).request(PERMISSION);

        /*

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10 );
                return;
            }
        }
        else {
            configureButton();
        }

        */

        //locationManager.requestLocationUpdates("gps", GPS_REFRESH_TIME, GPS_MIN_DISTANCE, locationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted(String[] permissionName) {

        configureGPS();
        configureButton();
        locationManager.requestLocationUpdates("gps", GPS_REFRESH_TIME, GPS_MIN_DISTANCE, locationListener);
    }

    @Override
    public void onPermissionDeclined(String[] permissionName) {
        Toast.makeText(MainActivity.this, "GPS ACCESS REJECTED", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionPreGranted(String permissionsName) {
        configureGPS();
        configureButton();
        locationManager.requestLocationUpdates("gps", GPS_REFRESH_TIME, GPS_MIN_DISTANCE, locationListener);
    }

    @Override
    public void onPermissionNeedExplanation(String permissionName) {
        /*
        Show dialog here and ask permission again. Say why
         */
        showAlertDialog(DIALOG_TITLE, DIALOG_MESSAGE, PERMISSION);
    }

    @Override
    public void onPermissionReallyDeclined(String permissionName) {
        Toast.makeText(MainActivity.this, "GPS Permissions Required, Please Enable", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNoPermissionNeeded() {

    }


    private void showAlertDialog(String title, String message, final String permission) {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        permissionHelper.requestAfterExplanation(permission);

                    }
                })
                .create();

        dialog.show();
    }



    private void configureButton() {
        getCoordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.requestLocationUpdates("gps", GPS_REFRESH_TIME, GPS_MIN_DISTANCE, locationListener);
            }
        });
    }






    private void configureGPS(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            //METHOD WHEN LOCATION CHANGES
            public void onLocationChanged(Location location) {
                coordTxt.setText("Coordinates: \n" + "Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            //METHOD WHEN GPS IS DISABLED
            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
    }


}




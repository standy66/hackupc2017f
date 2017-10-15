package me.standy.findmycar.findmycar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.logging.Logger;

/**
 * Created by astepanov on 14/10/2017.
 */

public class InitialActivity extends AppCompatActivity {
    View notification_view;

    @Override
    protected void onRestart() {
        super.onRestart();
        notification_view.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new CarRadarAPI(this, "http://dolgop.standy.me");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Button nextButton = (Button) findViewById(R.id.next_button);
        final EditText emailField = (EditText) findViewById(R.id.user_email);
        final EditText licensePlateNumber = (EditText) findViewById(R.id.license_plate_number);
        notification_view = findViewById(R.id.notification_layout);


        licensePlateNumber.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (licensePlateNumber.getText().toString().trim().equals("")) {
                    licensePlateNumber.setError("This field is required");
                    return;
                }

                if (emailField.getText().toString().trim().equals("")) {
                    emailField.setError("This field is required");
                    return;
                }



                String email = licensePlateNumber.getText().toString();
                String licencePlate = licensePlateNumber.getText().toString();

                CarRadarAPI.getInstance().query(licencePlate, new CarRadarAPI.QueryResultListener() {
                    @Override
                    public void onResult(CarRadarAPI.QueryResult result) {
                        if (result != null) {
                            Toast.makeText(InitialActivity.this, "HOT", Toast.LENGTH_LONG).show();

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            Log.w("MAIN", String.valueOf(result.photo.length));
                            Bitmap bitmap = BitmapFactory.decodeByteArray(result.photo, 0,
                                    result.photo.length, options);

                            if (bitmap != null) {
                                Intent intent = new Intent(InitialActivity.this, ClaimActivity.class);
                                intent.putExtra("EXTRA_LATITUDE", result.latitude);
                                intent.putExtra("EXTRA_LONGITUDE", result.longitude);
                                intent.putExtra("EXTRA_IMAGE", bitmap);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(InitialActivity.this, MapsActivity.class);
                                intent.putExtra("EXTRA_LATITUDE", result.latitude);
                                intent.putExtra("EXTRA_LONGITUDE", result.longitude);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(InitialActivity.this, "NOT", Toast.LENGTH_LONG).show();
                            notification_view.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }
}

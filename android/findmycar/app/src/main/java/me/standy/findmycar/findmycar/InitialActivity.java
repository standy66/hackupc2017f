package me.standy.findmycar.findmycar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.logging.Logger;

/**
 * Created by astepanov on 14/10/2017.
 */

public class InitialActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Button nextButton = (Button) findViewById(R.id.next_button);
        final EditText emailField = (EditText) findViewById(R.id.user_email);
        final EditText licensePlateNumber = (EditText) findViewById(R.id.license_plate_number);

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

                Intent intent = new Intent(InitialActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}

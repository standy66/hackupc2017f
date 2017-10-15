package me.standy.findmycar.findmycar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ClaimActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim);

        Button yes_button = (Button) findViewById(R.id.yes_button);
        Button no_button = (Button) findViewById(R.id.no_button);
        ImageView image = (ImageView) findViewById(R.id.car_image);

        Bitmap bmp = getIntent().getParcelableExtra("EXTRA_IMAGE");
        Log.w("MAIN", String.valueOf(bmp.getHeight()));
        Log.w("MAIN", String.valueOf(bmp.getWidth()));

        image.setImageBitmap(bmp);

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClaimActivity.this, MapsActivity.class);
                intent.putExtra("EXTRA_LATITUDE", getIntent().getDoubleExtra("EXTRA_LATITUDE", 0.0));
                intent.putExtra("EXTRA_LONGITUDE", getIntent().getDoubleExtra("EXTRA_LONGITUDE", 0.0));
                startActivity(intent);
            }
        });

        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

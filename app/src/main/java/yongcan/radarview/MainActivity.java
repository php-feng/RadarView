package yongcan.radarview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import yongcan.radarview.view.RadarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RadarView radarView = (RadarView) findViewById(R.id.radarView);
        List<Float> dataTest = new ArrayList();
        dataTest.add(15.6f);
        dataTest.add(16.6f);
        dataTest.add(11f);
        dataTest.add(13f);
        dataTest.add(11f);

        radarView.setData(dataTest);
        radarView.startAnim();
    }
}

# RadarView
高仿支付宝芝麻信用的雷达图控件

#Sample Usage

#XML:
      <yongcan.radarview.view.RadarView
          android:id="@+id/radarView"
          android:layout_width="wrap_content"
          android:layout_height="wrap_content" />
  
#Java
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

          
          
  

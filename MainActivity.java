package lab2_206_03.uwaterloo.ca.lab2_206_03;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import ca.uwaterloo.sensortoy.LineGraphView;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    public LineGraphView graph;
    public TextView accel;
    public Button reset;
    public Button resetstp;
    public int step;
    public SensorEventListeners al;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout l = (LinearLayout) findViewById(R.id.linearLayout);
        l.setOrientation(LinearLayout.VERTICAL);

        accel = new TextView(getApplicationContext());
        accel.setTextColor(Color.BLACK);
        l.addView(accel);

        graph = new LineGraphView(getApplicationContext(), 100, Arrays.asList("x", "y", "z"));
        l.addView(graph);
        graph.setVisibility(View.VISIBLE);
        reset = new Button(getApplicationContext());
        reset.setText("RESET GRAPH");
        reset.setGravity(Gravity.CENTER_HORIZONTAL);
        l.addView(
                reset,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
        );
        resetstp = new Button(getApplicationContext());
        resetstp.setText("RESET STEPS");
        resetstp.setGravity(Gravity.CENTER_HORIZONTAL);
        l.addView(
                resetstp,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
        );

        //request the sensor manager
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //instantiate sensor listeners
       al = new SensorEventListeners(accel, graph, step);
        sensorManager.registerListener(al, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
//        reset button
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graph.purge();
            }
        });
        resetstp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al.resetit();
            }
        });

    }
}

class SensorEventListeners implements SensorEventListener {
    TextView output;
    LineGraphView Graph;
    float z;
    File file;
    PrintWriter mPrintWriter;
    int counter = 0;
    int prev = 0;
    int state= 0;
    public SensorEventListeners(TextView outputView, LineGraphView grp, int stp) {
        output = outputView;
        Graph = grp;
        counter = stp;
    }

    public void resetit() {
        counter = 0;
    }
    public void onAccuracyChanged(Sensor s, int i) {
    }

    public void onSensorChanged(SensorEvent se) {

        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            Graph.addPoint(se.values);

            z = se.values[2];

            try {
                file = new File(Environment.getExternalStorageDirectory(), "data.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                mPrintWriter = new PrintWriter(new FileWriter(file,true));
                mPrintWriter.println(z);
                mPrintWriter.close();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }

            if(z>=4){
                state = 1;
            }else if (z<=-4) {
                state = 0;
            }else{
                state = 2;
            }
            if(prev==0 && state==1){
                counter++;
                prev = state;
            }else if (prev == 1 && state == 0){
                prev = state;
            }
            output.setText("Number of Steps: " + counter);
        }
    }
}

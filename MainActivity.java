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
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import ca.uwaterloo.sensortoy.LineGraphView;
import java.lang.String;
import static java.lang.Math.abs;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    public LineGraphView graph;
    public TextView accel;
    public Button reset;

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
        reset.setText("RESET");
        reset.setGravity(Gravity.CENTER_HORIZONTAL);
        l.addView(
                reset,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
        );

        //request the sensor manager
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //instantiate sensor listeners
        SensorEventListener al = new SensorEventListeners(accel, graph);
        sensorManager.registerListener(al, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
//        reset button
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graph.purge();
            }
        });
    }
}

class SensorEventListeners implements SensorEventListener {
    TextView output;
    LineGraphView Graph;
    float a, b, c;
    float max1 = 0, max2 = 0, max3 = 0;
    File file;
    PrintWriter mPrintWriter;

    public SensorEventListeners(TextView outputView, LineGraphView grp) {
        output = outputView;
        Graph = grp;
    }

    public void onAccuracyChanged(Sensor s, int i) {
    }

    public void onSensorChanged(SensorEvent se) {

        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            String x = String.format("(%.2f, %.2f, %.2f)", se.values[0], se.values[1], se.values[2]);
            Graph.addPoint(se.values);
            a = se.values[0];
            b = se.values[1];
            c = se.values[2];

            try {
                file = new File(Environment.getExternalStorageDirectory(), "dat.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
//                FileOutputStream stream = new FileOutputStream(file);
//                OutputStreamWriter writer = new OutputStreamWriter(stream);
//                BufferedWriter buffer = new BufferedWriter(writer);
                mPrintWriter = new PrintWriter(new FileWriter(file,true));
                mPrintWriter.println(c);
                mPrintWriter.close();
            }
            catch(IOException ex){
                ex.printStackTrace();
            }

//            to find maximum value
            if (max1 < abs(a) && max2 < abs(b) && max3 < abs(c)) {
                max1 = abs(a);
                max2 = abs(b);
                max3 = abs(c);
            } else if (max1 < abs(a)) {
                max1 = abs(a);
            } else if (max2 < abs(b)) {
                max2 = abs(b);
            } else if (max3 < abs(c)) {
                max3 = abs(c);
            }
            String AS = String.format("(%.2f, %.2f, %.2f)", max1, max2, max3);
            output.setText("Accelerometer: \n" + x + "\nMaximum acceleration:\n" + AS + "\n");
        }

    }
}

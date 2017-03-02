package za.co.riggaroo.buttonledcontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button button;
    private Gpio ledGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            PeripheralManagerService peripheralManagerService = new PeripheralManagerService();
            ledGpio = peripheralManagerService.openGpio(BoardDefaults.getGPIOForLED());
            ledGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            button = new Button(BoardDefaults.getGPIOForButton(), Button.LogicState.PRESSED_WHEN_LOW);
            button.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(final Button button, final boolean pressed) {
                    try {
                        Log.i(TAG, "Button value changed:" + pressed);
                        if (pressed) {
                            Toast.makeText(getApplicationContext(), "Button pressed, yay!", Toast.LENGTH_SHORT).show();
                        }
                        ledGpio.setValue(pressed);
                    } catch (IOException e) {
                        Log.e(TAG, "Exception setting led on:", e);
                    }
                }
            });


        } catch (IOException e) {
            Log.e(TAG, "Failed to setup peripherals - IO Exception:", e);
            throw new IllegalArgumentException("Failed to setup peripherals");

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ledGpio.close();
            button.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception closing peripherals:", e);
        }
    }
}

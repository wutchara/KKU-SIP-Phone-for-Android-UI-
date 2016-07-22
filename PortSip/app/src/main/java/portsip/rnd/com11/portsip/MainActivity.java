package portsip.rnd.com11.portsip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.portsip.OnPortSIPEvent;
import com.portsip.PortSipSdk;

public class MainActivity extends AppCompatActivity {

    PortSipSdk mPortSIP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPortSIP = new PortSipSdk();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

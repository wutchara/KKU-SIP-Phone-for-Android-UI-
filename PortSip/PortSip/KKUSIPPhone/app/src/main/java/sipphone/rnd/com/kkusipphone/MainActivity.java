package sipphone.rnd.com.kkusipphone;

/*
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.portsip.PortSipSdk;

public class MainActivity extends FragmentActivity {

    //PortSipSdk protSipSdk;

    //Main
    Button testLogin;
    Button testOneFrag;

    //Other layouts

    Login loginView;
    OneFragment fragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        testLogin = (Button) findViewById(R.id.buttonLogin_test);
        testOneFrag = (Button) findViewById(R.id.buttonTest);

        loginView = new Login();
        fragment = new OneFragment();

        addAction();
    }

    private void addAction() {
        testLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadLogin();
            }
        });

        testOneFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTest();
            }
        });
    }

    private void loadLogin(){
        if(loginView == null){
            loginView = new Login();
        }

        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, loginView);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadTest(){
        if(fragment == null){
            fragment = new OneFragment();
        }

        FragmentTransaction transaction;
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
*/
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener{

    ActionBar ab;

    //TODO: Declear global variables each pages
    Login loginView;
    More moreView;
    Numpad numpadView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        //TODO: Init Data All Variables use this Application
        initLogin();
        //init();
        /*
        Button login = (Button) findViewById(R.id.loginBtn);
        uname = (EditText) findViewById(R.id.editTextUname);
        upass = (EditText) findViewById(R.id.editTextUPass);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Login View", uname.getText().toString() + ":" + upass.getText().toString());

                setContentView(R.layout.activity_main);
                ActionBar ab = getSupportActionBar();
                ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

                // Three tab to display in actionbar
                ab.addTab(ab.newTab().setText("Numpad").setTabListener(MainActivity.this));
                ab.addTab(ab.newTab().setText("History").setTabListener(MainActivity.this));
                ab.addTab(ab.newTab().setText("Concat").setTabListener(MainActivity.this));
            }
        });
        */

        //loginView = new Login();
        //Log.d("Test Main", loginView.uname.getText().toString());
        /*
        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Three tab to display in actionbar
        ab.addTab(ab.newTab().setText("Numpad").setTabListener(this));
        ab.addTab(ab.newTab().setText("History").setTabListener(this));
        ab.addTab(ab.newTab().setText("Concat").setTabListener(this));
        */
    }


    private void initNumpad() {
        numpadView = new Numpad();
        numpadView.btn0 = (Button) findViewById(R.id.button0);
        numpadView.btn1 = (Button) findViewById(R.id.button1);
        numpadView.btn2 = (Button) findViewById(R.id.button2);
        numpadView.btn3 = (Button) findViewById(R.id.button3);
        numpadView.btn4 = (Button) findViewById(R.id.button4);
        numpadView.btn5 = (Button) findViewById(R.id.button5);
        numpadView.btn6 = (Button) findViewById(R.id.button6);
        numpadView.btn7 = (Button) findViewById(R.id.button7);
        numpadView.btn8 = (Button) findViewById(R.id.button8);
        numpadView.btn9 = (Button) findViewById(R.id.button9);
        numpadView.btn_call = (Button) findViewById(R.id.button_call);
        numpadView.btn_delete = (Button) findViewById(R.id.button_delete);

        numpadView.input_text = (TextView) findViewById(R.id.input_text);
    }

    private void setOnClickNumpad() {
        numpadView.btn0.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                numpadView.callOutNumber += "0";
                numpadView.input_text.setText(numpadView.callOutNumber);
            }
        });

        numpadView.btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                numpadView.callOutNumber += "1";
                numpadView.input_text.setText(numpadView.callOutNumber);
            }
        });

        numpadView.btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                numpadView.callOutNumber += "2";
                numpadView.input_text.setText(numpadView.callOutNumber);
            }
        });
        numpadView.btn3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                numpadView.callOutNumber += "3";
                numpadView.input_text.setText(numpadView.callOutNumber);
            }
        });

        numpadView.btn4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                numpadView.callOutNumber += "4";
                numpadView.input_text.setText(numpadView.callOutNumber);
            }
        });

        numpadView.btn5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                numpadView.callOutNumber += "5";
                numpadView.input_text.setText(numpadView.callOutNumber);
            }
        });
        numpadView.btn6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                numpadView.callOutNumber += "6";
                numpadView.input_text.setText(numpadView.callOutNumber);
            }
        });

        numpadView.btn7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                numpadView.callOutNumber += "7";
                numpadView.input_text.setText(numpadView.callOutNumber);
            }
        });

        numpadView.btn8.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                numpadView.callOutNumber += "8";
                numpadView.input_text.setText(numpadView.callOutNumber);
            }
        });

        numpadView.btn9.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                numpadView.callOutNumber += "9";
                numpadView.input_text.setText(numpadView.callOutNumber);
            }
        });

        numpadView.btn_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                numpadView.callOutNumber = numpadView.callOutNumber.substring(0,numpadView.callOutNumber.length()-1);
                numpadView.input_text.setText(numpadView.callOutNumber);
            }
        });



        //########################## MARK CALL ##############################

        numpadView.btn_call.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                numpadView.callOutNumber = "";
                numpadView.input_text.setText("KKU SIP PHONE NUMBER");
            }
        });


    }


    //TODO: Init Main
    private void initMain(){
        if(ab == null){
            ab = getSupportActionBar();
            ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            // Three tab to display in actionbar
            ab.addTab(ab.newTab().setText("Numpad").setTabListener(MainActivity.this));
            ab.addTab(ab.newTab().setText("History").setTabListener(MainActivity.this));
            ab.addTab(ab.newTab().setText("Contracts").setTabListener(MainActivity.this));
        }else{
            ab = getSupportActionBar();
            ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            ab.show();
        }
    }

    //TODO: Init More
    private void initMore(){
        if(moreView == null)
            moreView = new More();

        moreView.userName = (TextView) findViewById(R.id.moreUserNameText);
        moreView.userPass = (TextView) findViewById(R.id.morePasswordText);
        moreView.deleteBtn = (Button) findViewById(R.id.deleteProfileBtn);
        moreView.reloginBtn = (Button) findViewById(R.id.reLoginBtn);

        moreView.reloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("More View", "ReLogin");

                setContentView(R.layout.login_view);
                initLogin();
                ab.hide();
            }
        });
        moreView.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("More View", "Delete Profile");

                moreView.userName.setText("User Name :   -- EMPTY --");
                moreView.userPass.setText("Password :   -- EMPTY --");
                Toast.makeText(MainActivity.this, "Test Delete Profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //TODO: Init Login
    private void initLogin() {
        if(loginView == null)
            loginView = new Login();

        loginView.login = (Button) findViewById(R.id.loginBtn);
        loginView.uname = (EditText) findViewById(R.id.editTextUname);
        loginView.upass = (EditText) findViewById(R.id.editTextUPass);

        loginView.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Login View", loginView.uname.getText().toString() + ":" + loginView.upass.getText().toString());

                setContentView(R.layout.activity_main);
                initMain();
            }
        });
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        //Called when a tab is selected
        int nTabSelected = tab.getPosition();
        //TODO: Show Contents
        switch (nTabSelected) {
            case 0:
                setContentView(R.layout.numpad_view);
                initNumpad();
                setOnClickNumpad();
                break;
            case 1:
                setContentView(R.layout.history_view);
                break;
            case 2:
                setContentView(R.layout.concat_view);
                break;
        }


    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // Called when a tab unselected.
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        // Called when a tab is selected again.
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //TODO: Show More Page
            //Toast.makeText(MainActivity.this, "Test Setting", Toast.LENGTH_LONG).show();
            setContentView(R.layout.more_view);
            initMore();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
package sipphone.rnd.com.kkusipphone;


import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by linuxham on 21/7/2559.
 */
public class Login extends AppCompatActivity {
    protected Button login;

    protected EditText uname;
    protected EditText upass;

    public Login(){
        //login = (Button) findViewById(R.id.loginBtn);

        //uname = (EditText) findViewById(R.id.editTextUname);
        //upass = (EditText) findViewById(R.id.editTextUPass);

        /*
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Check input form Login View
                //check input
                //uname.setText(uname.getText().toString() + " :Checked");
                //upass.setText(upass.getText().toString() + " :Checked");
                Log.d("Login View", uname.getText().toString() + ":" + upass.getText().toString());

                setContentView(R.layout.activity_main);
            }
        });*/
    }

    public void clickLogin(){
        Log.d("Login View", uname.getText().toString() + ":" + upass.getText().toString());
    }

        /*extends Fragment {



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_view, container, false);

        login = (Button) rootView.findViewById(R.id.loginBtn);

        uname = (EditText) rootView.findViewById(R.id.editTextUname);
        upass = (EditText) rootView.findViewById(R.id.editTextUPass);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Check input form Login View
                //check input
                //uname.setText(uname.getText().toString() + " :Checked");
                //upass.setText(upass.getText().toString() + " :Checked");
                Log.d("Login View", uname.getText().toString() + ":" + upass.getText().toString());

                //Destory this view for go to tab bar
                getFragmentManager().beginTransaction().remove(Login.this).commit();

                //test
                //loadSomting();
            }
        });

        return rootView;
    }

    private void loadSomting() {
        OneFragment fragment = new OneFragment();

        FragmentTransaction transaction;
        transaction = getFragmentManager().beginTransaction();
        //transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    */
    /*
    private void initView() {
        login = (Button) findViewById(R.id.loginBtn);

        uname = (EditText) findViewById(R.id.editTextUname);
        upass = (EditText) findViewById(R.id.editTextUPass);

        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(
                Login.this,
                "User Name : "
                        + uname.getText().toString()
                        + "\nUser Pass : "
                        + upass.getText().toString()
                , Toast.LENGTH_LONG)
                .show();
    }
    */
}

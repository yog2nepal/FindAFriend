package com.example.biespiel_pc.and_2;
import android.support.test.filters.SmallTest;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Yogesh Chaudhary
 */

public class LoginTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public LoginTest() {
        super(LoginActivity.class);
    }
    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }
    //test the initialization of the interface
    @SmallTest
    public void testPassword(){
        EditText e = (EditText) getActivity().findViewById(R.id.loginpassword);
        assertNotNull(e);
    }
    @SmallTest
    public void testEmail(){
        EditText e = (EditText) getActivity().findViewById(R.id.loginemail);
        assertNotNull(e);
    }
    public void testButton(){
        Button b = (Button) getActivity().findViewById(R.id.btnsignin);
        assertNotNull(b);
    }
    public void testSignupButton(){
        Button b = (Button) getActivity().findViewById(R.id.btnsignup);
        assertNotNull(b);
    }
    public void testForgotPasswordButton(){
        TextView b = (TextView) getActivity().findViewById(R.id.btnForgotPass);
        assertNotNull(b);
    }
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}

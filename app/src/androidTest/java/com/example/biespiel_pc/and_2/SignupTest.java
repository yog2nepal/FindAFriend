package com.example.biespiel_pc.and_2;
import android.support.test.filters.SmallTest;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
/**
 * Created by User on 22/01/2018.
 */

public class SignupTest extends ActivityInstrumentationTestCase2<SignUpActivity> {
    public SignupTest() {
        super(SignUpActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    @SmallTest
    public void testUserName(){
        EditText e = (EditText) getActivity().findViewById(R.id.su_username);
        assertNotNull(e);
    }
    @SmallTest
    public void testFullName(){
        EditText e = (EditText) getActivity().findViewById(R.id.su_fullname);
        assertNotNull(e);
    }
    public void testEmail(){
        EditText e = (EditText) getActivity().findViewById(R.id.su_email);
        assertNotNull(e);
    }

    public void testAge(){
        EditText e = (EditText) getActivity().findViewById(R.id.su_age);
        assertNotNull(e);
    }

    public void testPassword(){
        EditText e = (EditText) getActivity().findViewById(R.id.su_password);
        assertNotNull(e);
    }

    public void testSignUPButton(){
        Button b = (Button) getActivity().findViewById(R.id.sign_up_button);
        assertNotNull(b);
    }



    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


}

package com.example.biespiel_pc.and_2;
import android.support.test.filters.SmallTest;

import com.example.biespiel_pc.and_2.Model.User;
import com.google.android.gms.maps.model.LatLng;
import junit.framework.TestCase;
/**
 * Created by User on 22/01/2018.
 */

public class UserTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }


    @SmallTest
    public  void testSetGetName()
    {
        User u = new User();
        String random = "Something random";
        u.setUserName(random);
        String s = u.getUserName();
        assertEquals(random, s);
    }

    @SmallTest
    public void testSetGetLatitude(){
        User u = new User();
        double latitude = 50;
        u.setLatitude(latitude);
        double result = u.getLatitude();
        assertEquals(result, latitude);
   }



    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

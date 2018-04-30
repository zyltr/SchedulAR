package com.Schedular.About;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import com.Schedular.R;
import com.Schedular.Schedule.Schedules;
import com.Schedular.ScheduleTargets.ScheduleTargets;


public class AboutActivity extends Activity {

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_about );
    }

    public void startAugmentedReality (View view)
    {
        // Starts ScheduleTargets
//        Intent intent = new Intent( this, ScheduleTargets.class);

        // Starts Schedules
        Intent intent = new Intent( this, Schedules.class );

        startActivity(intent);
    }

}

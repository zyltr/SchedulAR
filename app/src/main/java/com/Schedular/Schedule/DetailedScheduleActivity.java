package com.Schedular.Schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.Schedular.R;

public class DetailedScheduleActivity extends Activity
{
    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_detailed_schedule );

        // Get Intent
        Intent intent = getIntent ();

        // Get Strings Arrays from Intent
        String sectionNumber = intent.getStringExtra ( Schedules.SectionNumberKey );
        String[] instructorKeys = intent.getStringArrayExtra ( Schedules.InstructorKeys );
        String[] instructorValues = intent.getStringArrayExtra ( Schedules.InstructorValues );
        String[] courseKeys = intent.getStringArrayExtra ( Schedules.CourseKeys );
        String[] courseValues = intent.getStringArrayExtra ( Schedules.CourseValues );

        if ( instructorKeys == null || instructorValues == null || courseKeys == null || courseValues == null )
            return;

        if ( instructorKeys.length != instructorValues.length && courseKeys.length != courseValues.length )
            return;

        // Update Views

        String courseNumber = ":D: :CN: - " + sectionNumber;
        String courseName = "";
        String courseDescription = "";

        for ( int index = 0; index < courseKeys.length; ++index )
        {
            switch ( courseKeys[index].toUpperCase () )
            {
                case "DEPARTMENT":
                {
                    courseNumber = courseNumber.replaceFirst ( ":D:", courseValues[index] );
                    break;
                }
                case "COURSENUMBER":
                {
                    courseNumber = courseNumber.replaceFirst ( ":CN:", courseValues[index] );
                    break;
                }
                case "COURSENAME":
                {
                    courseName = courseValues[index];
                    break;
                }
                case "COURSEDESCRIPTION":
                {
                    break;
                }
                default:
                    break;
            }
        }

        ( ( TextView ) findViewById ( R.id.courseNumberTextView ) ).setText ( courseNumber );
        ( ( TextView ) findViewById ( R.id.courseNameTextView ) ).setText ( courseName );
        ( ( TextView ) findViewById ( R.id.courseDescriptionTextView ) ).setText ( courseDescription );

        String instructor = "";
        String officeBuilding = "";
        String officeRoom = "";
        String email = "";

        for ( int index = 0; index < instructorKeys.length; ++index )
        {
            switch ( instructorKeys[index].toUpperCase () )
            {
                case "INSTRUCTORNAME":
                {
                    instructor = instructorValues[index];
                    break;
                }
                case "OFFICEBUILDING":
                {
                    officeBuilding = instructorValues[index];
                    break;
                }
                case "OFFICEROOM":
                {
                    officeRoom = instructorValues[index];
                    break;
                }
                default:
                    break;
            }
        }

        ( ( TextView ) findViewById ( R.id.instructorTextView ) ).setText ( instructor );
        ( ( TextView ) findViewById ( R.id.officeBuildingTextView ) ).setText ( officeBuilding );
        ( ( TextView ) findViewById ( R.id.officeRoomTextView ) ).setText ( officeRoom );
    }
}

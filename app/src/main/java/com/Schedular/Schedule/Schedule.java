package com.Schedular.Schedule;

public class Schedule
{
    private String target;
    private String course;
    private String schedule;
    private String instructor;

    public Schedule ( ) { }


    public void setTarget ( String target ) { this.target = target; }

    public void setCourse ( String course )
    {
        this.course = course;
    }

    public void setSchedule ( String schedule )
    {
        this.schedule = schedule;
    }

    public void setInstructor ( String instructor )
    {
        this.instructor = instructor;
    }

    public String getTarget ( ) { return target; }

    public String getCourse ( )
    {
        return course;
    }

    public String getSchedule ( )
    {
        return schedule;
    }

    public String getInstructor ( )
    {
        return instructor;
    }

    public void fillUsingRow ( Row row )
    {
        target = ":B: :R:";
        course = ":D: :CN: - :SN:";
        schedule = ":D: from :ST: to :ET:";
        instructor = ":I:";

        for ( String key : row.data.keySet ( ) )
        {
            String value = row.data.get ( key );

            switch ( key.toUpperCase ( ) )
            {
                case "DEPARTMENT":
                {
                    course = course.replaceFirst ( ":D:", value );
                    break;
                }
                case "COURSENUMBER":
                {
                    course = course.replaceFirst ( ":CN:", value );
                    break;
                }
                case "SECTIONNUMBER":
                {
                    course = course.replaceFirst ( ":SN:", value );
                    break;
                }
                case "DAYS":
                {
                    schedule = schedule.replaceFirst ( ":D:", value );
                    break;
                }
                case "STARTTIME":
                {
                    schedule = schedule.replaceFirst ( ":ST:", value );
                    break;
                }
                case "ENDTIME":
                {
                    schedule = schedule.replaceFirst ( ":ET:", value );
                    break;
                }
                case "BUILDING":
                {
                    target = target.replaceFirst ( ":B:", value );
                    break;
                }
                case "ROOM":
                {
                    target = target.replaceFirst ( ":R:", value );
                    break;
                }
                case "INSTRUCTOR":
                {
                    instructor = instructor.replace ( ":I:", value );
                    break;
                }
                default:
                    break;
            }
        }
    }
}

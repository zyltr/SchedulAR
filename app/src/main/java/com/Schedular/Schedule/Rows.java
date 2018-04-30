package com.Schedular.Schedule;

import android.database.Cursor;

import java.util.ArrayList;

public class Rows
{
    private ArrayList<Row> rows = new ArrayList<> (  );

    public Rows ( final Cursor cursor )
    {
        if (!cursor.isFirst ())
            cursor.moveToFirst ();

        for ( int count = 0, rowCount = cursor.getCount (); count < rowCount; ++count, cursor.moveToNext () )
        {
            Row row = new Row ();
            for ( int columnIndex = 0, columns = cursor.getColumnCount (); columnIndex < columns; ++columnIndex )
            {
                row.data.put ( cursor.getColumnName ( columnIndex ), cursor.getString ( columnIndex ) );
            }
            rows.add ( row );
        }
    }

    public ArrayList<Row> getRows ( )
    {
        return rows;
    }
}

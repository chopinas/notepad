package com.example.chopin.notepad;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {
    private SimpleCursorAdapter adapter;
    private MnoteDb db;
    private SQLiteDatabase dbread;
    public static final int REQUEST_CODE_ADD_NOTE=1;
    public static final int REQUEST_CODE_EDIT_NOTE=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db=new MnoteDb(this);
        dbread=db.getReadableDatabase();

        adapter=new SimpleCursorAdapter(this,R.layout.item_mainlist
                ,null,new String[]{MnoteDb.COLUMN_NAME_NOTE_NAME,MnoteDb.COLUMN_NAME_NOTE_DATE}
                ,new int[]{R.id.tv_name,R.id.tv_date}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setListAdapter(adapter);
        refreshview();

        findViewById(R.id.btn_addnote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,EditNote.class),REQUEST_CODE_ADD_NOTE);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c=adapter.getCursor();
        c.moveToPosition(position);
        Intent i=new Intent(MainActivity.this ,EditNote.class);
        i.putExtra(EditNote.EXTRA_NOTEID,c.getInt(c.getColumnIndex(MnoteDb.COLUMN_NAME_ID)));
        i.putExtra(EditNote.EXTRA_NOTENAME,c.getString(c.getColumnIndex(MnoteDb.COLUMN_NAME_NOTE_NAME)));
        i.putExtra(EditNote.EXTRA_NOTECONTENT,c.getString(c.getColumnIndex(MnoteDb.COLUMN_NAME_NOTE_CONTENT)));
        startActivityForResult(i,REQUEST_CODE_EDIT_NOTE);
        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_ADD_NOTE:
            case REQUEST_CODE_EDIT_NOTE:
                if(resultCode== Activity.RESULT_OK){
                    refreshview();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void refreshview(){
        adapter.changeCursor(dbread.query(MnoteDb.TBALE_NAME_NOTES,null,null,null,null,null,null));
    }
}

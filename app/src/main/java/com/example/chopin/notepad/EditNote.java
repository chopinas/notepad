package com.example.chopin.notepad;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

public class EditNote extends ListActivity {
    private int noteid=-1;
    public static  final  String EXTRA_NOTEID="noteId";
    public static  final  String EXTRA_NOTENAME="noteName";
    public static  final  String EXTRA_NOTECONTENT="noteContent";
    public static  final  int  REQUEST_CODE_GET_PHOTO=1;
    public static  final  int  REQUEST_CODE_GET_VEDIO=2;

    private String currentpath=null;

    private MediaAdapter adapter;

    private View.OnClickListener OnClickHandler=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;
            File f;
            switch (view.getId()){
                case R.id.btn_addPhoto:
                    intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    f=new File(getMediapath(),System.currentTimeMillis()+".jpg");
                    if(!f.exists()){
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    currentpath=f.getAbsolutePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    Log.d("photo", String.valueOf(Uri.fromFile(f)));
                    startActivityForResult(intent,REQUEST_CODE_GET_PHOTO);
                    break;
                case R.id.btn_addvedio:
                    intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    f=new File(getMediapath(),System.currentTimeMillis()+".mp4");
                    if(!f.exists()){
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    currentpath=f.getAbsolutePath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent,REQUEST_CODE_GET_VEDIO);
                    break;
                case R.id.btn_save:
                    saveMedia(savenote());
                    setResult(RESULT_OK);
                    finish();
                    break;
                case R.id.btn_cancel:
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
            }
        }
    };

    private MnoteDb db;
    private SQLiteDatabase dbread,dbwrite;

    private EditText et_name,et_content;
    private Button btn_;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        db=new MnoteDb(this);
        dbread=db.getReadableDatabase();
        dbwrite=db.getWritableDatabase();

        adapter=new MediaAdapter(this);
        setListAdapter(adapter);

        et_name= (EditText) findViewById(R.id.et_name);
        et_content= (EditText) findViewById(R.id.et_content);

        noteid=getIntent().getIntExtra(EXTRA_NOTEID,-1);

        findViewById(R.id.btn_save).setOnClickListener(OnClickHandler);
        findViewById(R.id.btn_addPhoto).setOnClickListener(OnClickHandler);
        findViewById(R.id.btn_cancel).setOnClickListener(OnClickHandler);
        findViewById(R.id.btn_addvedio).setOnClickListener(OnClickHandler);

        if (noteid>-1){
            et_name.setText(getIntent().getStringExtra(EXTRA_NOTENAME));
            et_content.setText(getIntent().getStringExtra(EXTRA_NOTECONTENT));

            Cursor c=dbread.query(MnoteDb.TBALE_NAME_MEDIA,null
                    ,MnoteDb.COLUMN_NAME_MEDIA_OWNER_NOTE_ID+"=?"
            ,new String[]{noteid+""},null,null,null);
            while (c.moveToNext()){
                adapter.add(new MediaListCellData(
                        c.getString(c.getColumnIndex(MnoteDb.COLUMN_NAME_MEDIA_PATH))
                         ,c.getInt(c.getColumnIndex(MnoteDb.COLUMN_NAME_ID)))
                );
                adapter.notifyDataSetChanged();
            }
        }else {
            //添加日志操作
        }


    }
    public File getMediapath(){
        File dir=new File(Environment.getExternalStorageDirectory(),"NotesMedia");
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }

    public void saveMedia(int noteid){
        MediaListCellData data;
        ContentValues  cv;

        for (int i=0;i<adapter.getCount();i++){
            data=adapter.getItem(i);
            if(data.id<=-1){
                cv=new ContentValues();
                cv.put(MnoteDb.COLUMN_NAME_MEDIA_PATH,data.path);
                cv.put(MnoteDb.COLUMN_NAME_MEDIA_OWNER_NOTE_ID,noteid);
                dbwrite.insert(MnoteDb.TBALE_NAME_MEDIA,null,cv);
            }
        }
    }

    public int savenote(){
        ContentValues cv=new ContentValues();
        cv.put(MnoteDb.COLUMN_NAME_NOTE_NAME,et_name.getText().toString());
        cv.put(MnoteDb.COLUMN_NAME_NOTE_CONTENT,et_content.getText().toString());
        cv.put(MnoteDb.COLUMN_NAME_NOTE_DATE,new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));

        if(noteid>-1){
            dbwrite.update(MnoteDb.TBALE_NAME_NOTES,cv
                    ,MnoteDb.COLUMN_NAME_ID+"=?"
                    ,new String[]{noteid+""});
            return  noteid;
        }else {
            return (int) dbwrite.insert(MnoteDb.TBALE_NAME_NOTES,null,cv);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_GET_PHOTO:
            case REQUEST_CODE_GET_VEDIO:
                if(resultCode==RESULT_OK){
                    adapter.add(new MediaListCellData(currentpath));
                    adapter.notifyDataSetChanged();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MediaListCellData data=adapter.getItem(position);
        Intent intent;
        switch (data.type){
            case MediaType.PHOTO:
                intent=new Intent(this,PhotoView.class);
                intent.putExtra(PhotoView.EXTRA_PATH,data.path);
                startActivity(intent);
                break;
            case MediaType.VEDIO:
                intent=new Intent(this,Vedioview.class);
                intent.putExtra(Vedioview.EXRTA_PATH,data.path);
                startActivity(intent);
                break;
        }
        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onDestroy() {
        dbread.close();
        dbwrite.close();
        super.onDestroy();
    }
}

 class  MediaAdapter extends BaseAdapter{

     private Context context;
     private List<MediaListCellData> list=new ArrayList<MediaListCellData>();

     MediaAdapter(Context context){
         this.context=context;
     }

     public void add(MediaListCellData data){
         list.add(data);
     }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MediaListCellData getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.item_media,null);
        }
        MediaListCellData  data=getItem(i);
        ImageView iv=(ImageView) view.findViewById(R.id.iv_icon);
        TextView tv_path=(TextView) view.findViewById(R.id.tv_path);

        iv.setImageResource(data.iconid);
        tv_path.setText(data.path);
        return view;
    }
}
class  MediaListCellData{
    int type=0;
    int id=-1;
    String path="";
    int iconid=R.mipmap.ic_launcher;

    public MediaListCellData(String path){
        this.path=path;
        if(path.endsWith(".jpg")){
            iconid=R.drawable.photo;
            type=MediaType.PHOTO;
        }
        else if(path.endsWith(".mp4")){
            iconid=R.drawable.vedio;
            type=MediaType.VEDIO;
        }
    }

    MediaListCellData(String path,int id){
        this(path);
        this.id=id;
    }
}
class MediaType{
    static final int PHOTO=1;
    static final int VEDIO=2;

}

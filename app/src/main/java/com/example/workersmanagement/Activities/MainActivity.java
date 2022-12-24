package com.example.workersmanagement.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersmanagement.Adapter.RecyclerViewAdapter;
import com.example.workersmanagement.DataBase.DBhandler;
import com.example.workersmanagement.Model.Worker;
import com.example.workersmanagement.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView appname;
    private EditText worker_id
            ,last_name
            ,first_name
            ,birthday
            ,phone_number
            ,email
            ,field_of_work
            ,email_choice
            ,receiver
            ,sms_choice;
    private ImageView app_icone,add_picture,picture;
    private Animation scaled ,scaled_2;
    private Dialog dialog;
    private Button confirm_btn;
    private ArrayList<Worker> workerList;
    private byte[] image;
    private DBhandler mDBhandler;
    private RecyclerViewAdapter adapter;
    private Worker worker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SearchView searchView = findViewById(R.id.search);
        appname = findViewById(R.id.appname);

        // verifyer les permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        // instancier database
        mDBhandler = new DBhandler(MainActivity.this);
        workerList = new ArrayList<>();
        // remplir les donnée dans workerLIst avec la fonction storeDataArrays
        storeDataInArrays();
        // instancier l'adapter
        adapter = new RecyclerViewAdapter(workerList, new RecyclerViewAdapter.OnWorkerListener() {
            @Override
            public void onWorkerClick(int position,View v) {
                // affecter une animation dans floating button
                scaled_2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // deplacer vers l'activity worker profile
                        Intent i = new Intent(MainActivity.this, Worker_profile.class);
                        i.putExtra("id", String.valueOf(workerList.get(position).getId()));
                        startActivity(i);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                v.startAnimation(scaled_2);

            }
            //recupérer le numéro de telephone est appelé
            @Override
            public void onWorkerCall(int position) {
                Uri uri = Uri.parse("tel:"+workerList.get(position).getPhone_number());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
            //recupérer le numéro de telephone est envoyes un sms
            @Override
            public void onWorkerSend(int position) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);

                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.send_dialog, null);
                dialogBuilder.setView(dialogView);

                sms_choice = dialogView.findViewById(R.id.sms_choice);
                email_choice = dialogView.findViewById(R.id.email_choice);
                receiver=dialogView.findViewById(R.id.receiver);
                email_choice.setInputType(InputType.TYPE_NULL);
                sms_choice.setInputType(InputType.TYPE_NULL);
                receiver.setInputType(InputType.TYPE_NULL);
                change_focus(position);
                Button cancel_sending=dialogView.findViewById(R.id.cancel_sending);
                Button confirm_sending=dialogView.findViewById(R.id.confirm_sending);
                EditText description=dialogView.findViewById(R.id.content);
                change_sender(position);
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                confirm_sending.setOnClickListener(v ->{
                    if(sms_choice.hasFocus()){
                        Uri uri=Uri.parse("sms:"+receiver.getText().toString());//xxxxxxxx représente un numéro de téléphone
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        String message=description.getText().toString();
                        intent.putExtra("sms_body",message );
                        startActivity(intent);
                    }
                    else if(email_choice.hasFocus()){
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{receiver.getText().toString()});
                        email.putExtra(Intent.EXTRA_TEXT, description.getText().toString());

                        //need this to prompts email client only
                        email.setType("message/rfc822");

                        startActivity(Intent.createChooser(email, "Choose an Email client :"));
                    }
                });
                cancel_sending.setOnClickListener(view -> {
                    alertDialog.cancel();
                });
            }
        });

        RecyclerView recyclerView = findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        app_icone = findViewById(R.id.app_icone);
        searchView.setOnCloseListener(() -> {
            appname.setVisibility(View.VISIBLE);
            app_icone.setVisibility(View.VISIBLE);
            return false;
        });

        searchView.setOnSearchClickListener(view -> {
            appname.setVisibility(View.INVISIBLE);
            app_icone.setVisibility(View.INVISIBLE);
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                search_for(text);
                return true;
            }
        });
        scaled_2 = AnimationUtils.loadAnimation(this, R.anim.scale_d);
        scaled = AnimationUtils.loadAnimation(this, R.anim.scale_d);
        scaled.setDuration(400);
        scaled.setInterpolator(input -> (float) ((1 * (Math.pow(Math.E, -input / 0.3)) * Math.sin(10 * input) + 1)));

    }

    private void search_for(String query) {
        ArrayList<Worker> search_result=new ArrayList<>();
        for(Worker worker:workerList){
            if(worker.getId().toString().contains(query.toLowerCase()) ||
                    worker.getFirst_name().toLowerCase().contains(query.toLowerCase()) ||
                    worker.getLast_name().toLowerCase().contains(query.toLowerCase()) ){
                search_result.add(worker);
            }
        }
        if(search_result.isEmpty()){
            Toast.makeText(MainActivity.this,"sorry no exact matches found",Toast.LENGTH_SHORT);
        }
        else {
            adapter.setSearchResult(search_result);
        }
    }

    private void change_focus(int position) {
        email_choice.setOnClickListener( view -> {
            //email_choice.setCursorVisible(false); no need for that
            // email_choice.setShowSoftInputOnFocus(false);
            sms_choice.setFocusableInTouchMode(false);
            sms_choice.setFocusable(false);
            sms_choice.setFocusableInTouchMode(true);
            sms_choice.setFocusable(true);
        });
        email_choice.setOnFocusChangeListener((view, b) -> {
            email_choice.requestFocus();
            sms_choice.setFocusableInTouchMode(false);
            sms_choice.setFocusable(false);
            sms_choice.setFocusableInTouchMode(true);
            sms_choice.setFocusable(true);
            change_sender(position);
        });
        sms_choice.setOnClickListener( view -> {
            email_choice.setFocusableInTouchMode(false);
            email_choice.setFocusable(false);
            email_choice.setFocusableInTouchMode(true);
            email_choice.setFocusable(true);

        });
        sms_choice.setOnFocusChangeListener((view, b) -> {
            sms_choice.requestFocus();
            email_choice.setFocusableInTouchMode(false);
            email_choice.setFocusable(false);
            email_choice.setFocusableInTouchMode(true);
            email_choice.setFocusable(true);
            change_sender(position);
        });
    }

    private void change_sender(int position) {
        if(email_choice.hasFocus()){
            receiver.setText(workerList.get(position).getEmail()); }
        else  if (sms_choice.hasFocus()) {
            receiver.setText(workerList.get(position).getPhone_number());}
        change_focus(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void add_worker(View v) {
        scaled.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onAnimationEnd(Animation animation) {
                if (dialog != null) if (dialog.isShowing()) return;
                dialog = new BottomSheetDialog(MainActivity.this);
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.addView(View.inflate(MainActivity.this, R.layout.add_worker, null));
                dialog.setContentView(layout);
                dialog.show();
                worker_id = dialog.findViewById(R.id.worker_id);
                last_name = dialog.findViewById(R.id.last_name);
                first_name = dialog.findViewById(R.id.first_name);
                email = dialog.findViewById(R.id.email_adresse);
                add_picture=dialog.findViewById(R.id.add_picture);
                picture=dialog.findViewById(R.id.profile);

                birthday= dialog.findViewById(R.id.birthday);
                birthday.setInputType(InputType.TYPE_NULL);
                birthday.setOnClickListener( view ->   {
                    DatePickerDialog dialog=new DatePickerDialog(MainActivity.this);
                    dialog.show();
                    dialog.setOnDismissListener(dialogInterface -> birthday.setText(dialog.getDatePicker().getDayOfMonth()+"/"
                            +(dialog.getDatePicker().getMonth()+1)+"/"
                            +dialog.getDatePicker().getYear()));

                });
                birthday.setOnFocusChangeListener((v1, hasFocus) -> {
                    if (hasFocus) {
                        DatePickerDialog dialog=new DatePickerDialog(MainActivity.this);
                        dialog.show();
                        dialog.setOnDismissListener(dialogInterface -> birthday.setText(dialog.getDatePicker().getDayOfMonth()+"/"
                                +(dialog.getDatePicker().getMonth()+1)+"/"
                                +dialog.getDatePicker().getYear()));
                    }
                });
                phone_number = dialog.findViewById(R.id.phone_numberr);
                field_of_work = dialog.findViewById(R.id.field_of_work);
                confirm_btn = dialog.findViewById(R.id.confirm);
                confirm_btn.setOnClickListener(view -> {
                    if(!worker_id.getText().toString().isEmpty() && !last_name.getText().toString().isEmpty() && !first_name.getText().toString().isEmpty()&&
                            !email.getText().toString().isEmpty() && !phone_number.getText().toString().isEmpty() && !field_of_work.getText().toString().isEmpty() &&
                            !birthday.getText().toString().isEmpty()){
                        add();
                        workerList.clear();
                        storeDataInArrays();
                        adapter.notifyDataSetChanged(); }
                    else {
                        Toast.makeText(MainActivity.this, "u must enter all fields", Toast.LENGTH_SHORT).show();
                    }

                });
                add_picture.setOnClickListener(view ->{
                    boolean pick=true;
                    if(pick){
                        if(!CheckCameraPermission()){
                            RequestCameraPermission();
                        }else {
                            PickImage();
                        }
                    }else{
                        if(!CheckStoragePermission()){
                            RequestStoragePermission();
                        }else { PickImage();  }

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(scaled);

    }
    //afficher l'activity de crop image pour recupérer l'image

    private void PickImage() {
        CropImage.activity()
                .start(this);
    }
    // les permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void RequestStoragePermission() {
        requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void RequestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }

    private boolean CheckStoragePermission() {
        Boolean res2= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    private boolean CheckCameraPermission() {
        Boolean res1= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED;
        Boolean res2= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }
    //ajouter l'employe dans la database
    private void add() {
        worker=new Worker(Long.parseLong(worker_id.getText().toString().trim()),last_name.getText().toString().trim(),first_name.getText().toString().trim()
                ,phone_number.getText().toString().trim(),email.getText().toString().trim(),field_of_work.getText().toString().trim(),birthday.getText().toString().trim(),ImageToBitmap(picture));
        mDBhandler.addWorker(worker);
        dialog.cancel();

    }
    //convertir l'image au tableau de byte

    private byte[] ImageToBitmap(ImageView picture) {

        Bitmap bitmap=((BitmapDrawable) picture.getDrawable()).getBitmap();
        ByteArrayOutputStream stream=new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
        byte[] bytes=stream.toByteArray();
        //bitmap.recycle();
        return bytes;
    }

    public void search(View view) {

    }
    public void add_picture(View view) {

    }


    void storeDataInArrays(){
        Cursor cursor = mDBhandler.getWorkers();
        if(cursor.getCount() == 0){
      //      Toast.makeText(MainActivity.this, "No data", Toast.LENGTH_SHORT).show();
        }
        else{
            while (cursor.moveToNext()){

                worker=new Worker(cursor.getLong(1),cursor.getString(2),cursor.getString(3),
                        cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getBlob(8));
                workerList.add(worker);
            }
        }
    }
    // notifier que la liste a etait changer
    @Override
    protected void onResume() {
        super.onResume();
        workerList.clear();
        storeDataInArrays();
        adapter.notifyDataSetChanged();


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }
    //recupérer l'image apartir l'activity de crop image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    InputStream stream=getContentResolver().openInputStream(resultUri);
                    Bitmap bitmap= BitmapFactory.decodeStream(stream);
                    picture.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

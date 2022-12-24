package com.example.workersmanagement.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workersmanagement.DataBase.DBhandler;
import com.example.workersmanagement.Model.Worker;
import com.example.workersmanagement.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class Worker_profile extends AppCompatActivity {
private DBhandler db;
private EditText first_name,name,birthday,phone_number,email,work_field,
        sms_receiver,email_receiver,sms_description,email_description,subject;
private ImageButton phone_arrow,email_arrow;
private ImageView profile_picture,modifier,supprimer,exit_dialog;

    private ImageView add_picture,arrow_back;
    private Button send_sms ,call,send_email,cancel_email;
    private Worker worker;
    private ArrayList<Worker> workerList;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile);
        //initialisation de l'instance "db" de la clasee DBhandler
        db = new DBhandler(Worker_profile.this);
        //recupérer l'employé selectionné par "id"
        String id = getIntent().getStringExtra("id");

        worker = db.getWorker(id);   //getWorker est la méthode de la classe DBhandler

       //findViewById est la méthode qui trouve la vue par l'ID qui lui est donné.
        //Donc findViewById(R. id. myName) trouve la vue avec le nom 'myName'.
        name = findViewById(R.id.profile_name);
        first_name = findViewById(R.id.profile_first_name);
        phone_number = findViewById(R.id.profile_phone);
        email = findViewById(R.id.profile_email);
        birthday = findViewById(R.id.profile_birthday);
        work_field = findViewById(R.id.profile_work);
        profile_picture = findViewById(R.id.profile_picture);
        supprimer = findViewById(R.id.profile_supp);
        modifier = findViewById(R.id.profile_edit);
        arrow_back = findViewById(R.id.Return);
        add_picture=findViewById(R.id.add_pictureProfile);
        phone_arrow=findViewById(R.id.phone_arrow);
        email_arrow=findViewById(R.id.email_arrow);
        arrow_back.setOnClickListener(view -> finish());

        //remplir les champs de profile d'employé

        phone_number.setText(worker.getPhone_number());
        name.setText(worker.getLast_name() );
        first_name.setText(worker.getFirst_name());
        birthday.setText(worker.getDate());
        work_field.setText(worker.getField());
        email.setText(worker.getEmail());
        /*recupérer l'image apartir de sqlite sous la forme blob est tranformé la au bitmap
        pour l'afficher */
        profile_picture.setImageBitmap(BitmapFactory.decodeByteArray(worker.getImage(), 0, worker.getImage().length));
        //supprime un employé avec la fonction deleteWorker de la classe DBhandler
        supprimer.setOnClickListener(v -> {
            db.deleteWorker(id);
            finish();
        });
        // afficher un dialog pour manipuler les action d'appel est sms
        phone_arrow.setOnClickListener(v-> {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Worker_profile.this);

                    LayoutInflater inflater = Worker_profile.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_sms_call, null);
                    dialogBuilder.setView(dialogView);
                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();

                    sms_receiver=dialogView.findViewById(R.id.sms_receiver);
                    sms_description=dialogView.findViewById(R.id.sms_content);
                    sms_receiver.setText(phone_number.getText().toString());
                    call=dialogView.findViewById(R.id.confirm_call);
                    send_sms=dialogView.findViewById(R.id.confirm_sms);
                    exit_dialog=dialogView.findViewById(R.id.exit_dialog);

                    exit_dialog.setOnClickListener(view ->{
                        alertDialog.cancel();
                    });
                    //recupérer le numéro de telephone est appelé
                    call.setOnClickListener(view ->{
                        Uri uri = Uri.parse("tel:"+phone_number);
                        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                        startActivity(intent);
                    });
                   //recupérer le numéro de telephone est envoyer un sms
                    send_sms.setOnClickListener(view ->{
                        Uri uri=Uri.parse("sms:"+phone_number.getText().toString());//xxxxxxxx représente un numéro de téléphone
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.putExtra("sms_body",sms_description.getText().toString());
                        startActivity(intent);
                    });

                });
        // afficher un dialog pour manipuler l'action d'envoyer email
        email_arrow.setOnClickListener(v-> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Worker_profile.this);

            LayoutInflater inflater = Worker_profile.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_email, null);
            dialogBuilder.setView(dialogView);
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();

            email_receiver=dialogView.findViewById(R.id.email_receiver);
            email_description=dialogView.findViewById(R.id.email_content);
            email_receiver.setText(email.getText().toString());
            subject=dialogView.findViewById(R.id.subject);
            send_email=dialogView.findViewById(R.id.confirm_email);
            cancel_email=dialogView.findViewById(R.id.cancel_email);

            cancel_email.setOnClickListener(view ->{
                alertDialog.cancel();
            });
            send_email.setOnClickListener(view ->{
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email.getText().toString()});
                intent.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
                intent.putExtra(Intent.EXTRA_TEXT, email_description.getText().toString());

                //need this to prompts email client only
                intent.setType("message/rfc822");

                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            });
        });
        //recursive methodes to control
        change0();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void change0() {
        modifier.setOnClickListener(view -> {

            add_picture.setVisibility(View.VISIBLE);
            first_name.setEnabled(true);
            name.setEnabled(true);
            email.setEnabled(true);
            phone_number.setEnabled(true);
            work_field.setEnabled(true);
            birthday.setEnabled(true);
            add_picture.setEnabled(true);

            modifier.setImageResource(R.drawable.ic_check);
            change();

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void change() {
        add_picture.setOnClickListener(view -> PickImage());
      modifier.setOnClickListener(v ->{
          if (!name.getText().toString().isEmpty() && !first_name.getText().toString().isEmpty() &&
                  !email.getText().toString().isEmpty() && !phone_number.getText().toString().isEmpty() && !work_field.getText().toString().isEmpty() &&
                  !birthday.getText().toString().isEmpty()) {
              birthday.setInputType(InputType.TYPE_NULL);
              birthday.setOnClickListener(view -> {
                  DatePickerDialog dialog = new DatePickerDialog(Worker_profile.this);
                  dialog.show();
                  dialog.setOnDismissListener(dialogInterface -> birthday.setText(dialog.getDatePicker().getDayOfMonth() + "/"
                          + (dialog.getDatePicker().getMonth() + 1) + "/"
                          + dialog.getDatePicker().getYear()));

              });
              birthday.setOnFocusChangeListener((v1, hasFocus) -> {
                  if (hasFocus) {
                      DatePickerDialog dialog = new DatePickerDialog(Worker_profile.this);
                      dialog.show();
                      dialog.setOnDismissListener(dialogInterface -> birthday.setText(dialog.getDatePicker().getDayOfMonth() + "/"
                              + (dialog.getDatePicker().getMonth() + 1) + "/"
                              + dialog.getDatePicker().getYear()));
                  }
              });
              worker = new Worker(worker.getId(), name.getText().toString().trim(), first_name.getText().toString().trim()
                      , phone_number.getText().toString().trim(), email.getText().toString().trim(), work_field.getText().toString().trim(), birthday.getText().toString().trim(), ImageToBitmap_toArray(profile_picture));
              db.updateWorker(worker);
              phone_number.setText(worker.getPhone_number());
              name.setText(worker.getLast_name());
              first_name.setText(worker.getFirst_name());
              birthday.setText(worker.getDate());
              email.setText(worker.getEmail());
              work_field.setText(worker.getField());
              Bitmap bm = BitmapFactory.decodeByteArray(worker.getImage(), 0, worker.getImage().length);
              profile_picture.setImageBitmap(bm);
              modifier.setImageResource(R.drawable.ic_edit);
              first_name.setEnabled(false);
              name.setEnabled(false);
              email.setEnabled(false);
              phone_number.setEnabled(false);
              work_field.setEnabled(false);
              birthday.setEnabled(false);
              add_picture.setEnabled(false);
              add_picture.setVisibility(View.INVISIBLE);


          } else {
              Toast.makeText(Worker_profile.this, "u must enter all fields", Toast.LENGTH_SHORT).show();
          }
         change0();
      });
    }

    //afficher l'activity de crop image pour recupérer l'image
    private void PickImage() {
        CropImage.activity()
                .start(this);
    }
   //convertir l'image au tableau de byte
    private byte[] ImageToBitmap_toArray(ImageView picture) {

        Bitmap bitmap=((BitmapDrawable) picture.getDrawable()).getBitmap();
        ByteArrayOutputStream stream=new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
        byte[] bytes=stream.toByteArray();
        //bitmap.recycle();
        return bytes;
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
                    profile_picture.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
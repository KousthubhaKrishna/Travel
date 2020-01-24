package com.example.travel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class InfoActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    String placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        placeName = intent.getStringExtra("Monument name");
        setContentView(R.layout.activity_info);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("Monuments").child(placeName);
        System.out.println(placeName);
        int fid;
        HashMap<String,Integer> hm = new HashMap<String,Integer>();
        hm.put("amboja_temple",R.drawable.amboja_temple);
        hm.put("bom_jesus_basilica",R.drawable.bom_jesus_basilica);
        hm.put("chandeshwar_bhoothnath_temple",R.drawable.chandeshwar_bhoothnath_temple);
        hm.put("dudhsagar_falls",R.drawable.dudhsagar_falls);
        hm.put("fort_aguada",R.drawable.fort_aguada);
        hm.put("lady_church",R.drawable.lady_church);
        hm.put("pandava_caves",R.drawable.pandava_caves);
        hm.put("safa_masjid",R.drawable.safa_masjid);
        hm.put("st_agustin_tower",R.drawable.st_agustin_tower);

//        Bitmap image = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier( placeName , "drawable", getPackageName()));

        ImageView im = (ImageView)findViewById(R.id.kkimage);
        im.setImageResource(hm.get(placeName.toLowerCase()));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = dataSnapshot.child("name").getValue().toString();
                String info = dataSnapshot.child("info").getValue().toString();
                TextView tv = (TextView)findViewById(R.id.titleme);
                tv.setText(title);
                tv = (TextView)findViewById(R.id.desc);
                tv.setText(info);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

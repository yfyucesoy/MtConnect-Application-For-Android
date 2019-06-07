package com.yusuffurkanyucesoy.girisekrani;

import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CihazSecimActivity extends AppCompatActivity {
    ImageButton btnMt1,btnMt2,btnMt3,btnMt4;
    TextView txtKullaniciAdi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cihaz_secim);
        btnMt1=findViewById(R.id.btnMc1);
        btnMt2=findViewById(R.id.btnMc2);
        btnMt3=findViewById(R.id.btnMc3);
        btnMt4=findViewById(R.id.btnMc4);
        txtKullaniciAdi=findViewById(R.id.txtKullaniciAdi);


    }
    public void mzk1(View view){
        Intent mazak1Ekran= new Intent(CihazSecimActivity.this,Mazak1Activity.class);
        startActivity(mazak1Ekran);

    }
    public void mzk2(View view){
        Intent mazak2Ekran= new Intent(CihazSecimActivity.this,Mazak2Activity.class);
        startActivity(mazak2Ekran);

    }
    public void mzk3(View view){
        Intent mazak3Ekran= new Intent(CihazSecimActivity.this,Mazak3Activity.class);
        startActivity(mazak3Ekran);

    }
    public void mzk4(View view){
        Intent mazak4Ekran= new Intent(CihazSecimActivity.this,Mazak4Activity.class);
        startActivity(mazak4Ekran);

    }
}

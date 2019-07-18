package com.nada.psikologiku;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

//import com.nada.psikologiku.adapter.ListQuizAdapter;

import com.nada.psikologiku.adapter.MyStepperAdapter;
import com.nada.psikologiku.database.DatabaseHelper;
import com.nada.psikologiku.model.Quiz;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuizActivity extends AppCompatActivity implements StepperLayout.StepperListener {
    private static final String TAG = "Quiz";
    private RecyclerView lvQuiz;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Quiz> mQuizList;
    private DatabaseHelper mDBHelper;
    private StepperLayout stepperLayout;
    private RadioGroup mRadioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        stepperLayout = findViewById(R.id.stepperLayout);
        stepperLayout.setAdapter(new MyStepperAdapter(getSupportFragmentManager(), this));
        stepperLayout.setListener(this);

        File database = getApplicationContext().getDatabasePath(DatabaseHelper.DBNAME);
        if (!database.exists()){
            mDBHelper.getReadableDatabase();
            if (copyDatabase(this)){
                Toast.makeText(this,"COPY SUCCESS",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"COPY ERROR",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean copyDatabase(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(DatabaseHelper.DBNAME);
            String outFileName = DatabaseHelper.DBLOCATION + DatabaseHelper.DBNAME;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) > 0){
                outputStream.write(buff,0,length);
            }
            outputStream.flush();
            outputStream.close();
            Log.w("MainActivity","DB copied");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onCompleted(View completeButton) {
        Log.e(TAG, "onCompleted: " + DatabaseHelper.HASIL);
        for (int i = 0; i < DatabaseHelper.HASIL.size(); i++) {
            String value = (String) DatabaseHelper.HASIL.get(i);
            if (value.equals("0")) {
                DatabaseHelper.A1 += 1;
            } else if (value.equals("1")) {
                DatabaseHelper.B1 += 1;
            } else if (value.equals("2")){
                DatabaseHelper.C1 += 1;
            } else if (value.equals("3")){
                DatabaseHelper.D1 += 1;
            }else if (value.equals("4")){
                DatabaseHelper.E1 += 1;
            }
        }
        int a = DatabaseHelper.A1, b = DatabaseHelper.B1, c = DatabaseHelper.C1, d = DatabaseHelper.D1, e = DatabaseHelper.E1;
        int score =  b + c*2 + d*3 + e*4;

        if(score<14){
            Intent intent = new Intent(getApplicationContext(), HasilQuizAnxietyActivity.class);
            intent.putExtra("pesan1","Tidak ada Kecemasan");
            intent.putExtra("score1",score);
            startActivity(intent);
        }else if ((score>=14) && (score<=20)){
            Intent intent = new Intent(getApplicationContext(), HasilQuizAnxietyActivity.class);
            intent.putExtra("pesan1","Kecemasan Ringan");
            intent.putExtra("score1",score);
            startActivity(intent);
        }else if ((score>=21) && (score<=27)){
            Intent intent = new Intent(getApplicationContext(), HasilQuizAnxietyActivity.class);
            intent.putExtra("pesan1","Kecemasan Sedang");
            intent.putExtra("score1",score);
            startActivity(intent);
        }else if ((score>=28) && (score<=41)){
            Intent intent = new Intent(getApplicationContext(), HasilQuizAnxietyActivity.class);
            intent.putExtra("pesan1","Kecemasan Berat");
            intent.putExtra("score1",score);
            startActivity(intent);
        }else if ((score>=42) && (score<=56)){
            Intent intent = new Intent(getApplicationContext(), HasilQuizAnxietyActivity.class);
            intent.putExtra("pesan1","Kecemasan Sangat Berat");
            intent.putExtra("score1",score);
            startActivity(intent);
        }
        Log.d(TAG, "jumlah: "+a+" "+b+" "+c+" "+d+" "+e);
        Log.d("score=","score"+score);
    }

    @Override
    public void onError(VerificationError verificationError) {
        Toast.makeText(this, "onError! -> " + verificationError.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        mRadioGroup = stepperLayout.findViewById(R.id.radioGroupWrapper);
        mRadioGroup.getCheckedRadioButtonId();
        Toast.makeText(this, "onStepSelected! -> " + newStepPosition +" " +
                ""+mRadioGroup.getCheckedRadioButtonId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReturn() {
        finish();
    }
}

package com.example.rafael_cruz.prototipo.activity;



import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.SelectOnMapFragment;
import com.example.rafael_cruz.prototipo.config.DAO;
import com.example.rafael_cruz.prototipo.model.Eventos;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {
    static Eventos eventos;

    private EditText outroEditText;
    private Button buttonAvancar;

    private RadioGroup radioGroup;
    private LatLng localizacao;
    private String      tipoevento;
    private Switch aSwitchSemlimitetempo;
    private EditText    editTextData;
    private EditText    editTextHora;
    private int         year_x,month_x,day_x,hour_x,minute_x,hora;
    static final int    DIALOG_ID_DATE = 0;
    static final int    DIALOG_ID_TIME = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);


        radioGroup              = findViewById(R.id.radioGroup);
        outroEditText           = findViewById(R.id.editText_outro);
        buttonAvancar           = findViewById(R.id.button_adicionar_evento);
        aSwitchSemlimitetempo   = findViewById(R.id.switch_sem_limite_tempo);
        editTextData            = findViewById(R.id.editText_data_2);
        editTextHora            = findViewById(R.id.editText_hora);

        //mascara para a data
        SimpleMaskFormatter simpleMaskdata = new SimpleMaskFormatter( "NN/NN/NNNN" );
        SimpleMaskFormatter simpleMaskhora = new SimpleMaskFormatter( "NN:NN" );
        MaskTextWatcher maskData = new MaskTextWatcher(editTextData,simpleMaskdata);
        MaskTextWatcher maskHora = new MaskTextWatcher(editTextHora,simpleMaskhora);
        editTextData.addTextChangedListener(maskData);
        editTextHora.addTextChangedListener(maskHora);

        //database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("01").setValue("40");

        final Calendar calendar = Calendar.getInstance();
        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);


        editTextData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID_DATE);
            }
        });

        editTextHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID_TIME);
            }
        });


        buttonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // addEvent();
                SelectOnMapFragment fragment = new SelectOnMapFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container_add_event, fragment);
                fragmentTransaction.commit();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");
                myRef.setValue("Hello, World!");
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_animal_perdido){
                    tipoevento = "Animal Perdido";
                    outroEditText.setEnabled(false);
                    Toast.makeText(AddEventActivity.this,"Animal",Toast.LENGTH_LONG).show();
                } else if (checkedId == R.id.radio_button_coleta_de_lixo){
                    tipoevento = "Coleta de lixo";
                    outroEditText.setEnabled(false);
                    Toast.makeText(AddEventActivity.this,"Coleta",Toast.LENGTH_LONG).show();
                } else if (checkedId == R.id.radio_button_outro){
                    outroEditText.setEnabled(true);
                    tipoevento = outroEditText.getText().toString();
                }
            }
        });




//        aSwitchSemlimitetempo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (aSwitchSemlimitetempo.isActivated()){
//                    Log.i("if",String.valueOf( aSwitchSemlimitetempo.isActivated()));
//                    editTextHora.setEnabled(false);
//                    hora = 0000;
//                    editTextData.setText(hora);
//                }else {
//                    Log.i("else",String.valueOf( aSwitchSemlimitetempo.isActivated()));
//                    editTextHora.setEnabled(true);
//                    editTextHora.setText("0000");
//                    hora = Integer.parseInt(editTextHora.getText().toString().replace("/",""));
//                }
//            }
//        });
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_ID_DATE){
            return new DatePickerDialog(this,dpickerListener,year_x,month_x,day_x);
        }else {
            return new TimePickerDialog(this,kTimeListener,hour_x,minute_x,false);
        }
    }

    private TimePickerDialog.OnTimeSetListener kTimeListener =  new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;
            String parse;
            if (hour_x < 9 && minute_x < 9){
                parse = "0"+hour_x +"0"+minute_x;
            }else if (hour_x > 9 && minute_x < 9){
                parse = hour_x +"0"+minute_x;
            }else if (hour_x < 9 && minute_x > 9){
                parse = "0"+hour_x +minute_x;
            }else {
                parse = hour_x +""+minute_x;
            }
            editTextHora.setText(parse);
        }
    };

    private DatePickerDialog.OnDateSetListener dpickerListener =  new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;
            String parse;
            if (month_x<10&&day_x>9){
                parse = day_x+"0"+month_x +""+year_x;
            }else if (day_x<10&&month_x<10){
                parse = "0"+day_x+"0"+month_x +""+year_x;
            }else if (day_x < 10&&month_x>9){
                parse = "0"+day_x+""+month_x +""+year_x;
            }else {
                parse = day_x+""+month_x +""+year_x;
            }
            String dataString = String.valueOf(parse);
            Log.i("dia",String.valueOf(day_x));
            Log.i("mes",String.valueOf(month_x));
            Log.i("ano",String.valueOf(year_x));
            editTextData.setText(String.valueOf(dataString));
        }
    };

    private void addEvent(){
        eventos =  new Eventos();
        //TODO metodo para adicionar evento
        DatabaseReference database = DAO.getFireBase().child("eventos").child(tipoevento);
        //inicializa o evento
        eventos =  new Eventos();
        //Obtem opção do radiogroup tipo de evento
        eventos.setTipoEvento(tipoevento);
        //define se o evento terá horario especifico
        eventos.setHorario(hora);
        Log.i("Hora do evento",Integer.toString(hora));

        // data
        String data = editTextData.getText().toString().replace("/","");
        data.replaceAll("[/]","");
        Log.i("data",data);
        eventos.setData(Integer.parseInt(data));


        database.push().setValue(eventos);

    }


    public void setLocalizacao(LatLng localizacao) {
        this.localizacao = localizacao;
    }
}

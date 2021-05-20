package com.example.forest1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.example.forest1.RazryadActivity.Num;
import static com.example.forest1.MainActivity.Num1;
import static com.example.forest1.R.id.spPorodaOT;

public class OtvodActivity extends AppCompatActivity  implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CM_DELETE_ID = 1;
    Button btnAddOT, btnClearOT, btnCalcOT;
    ListView lvOtvod;
    EditText edtNumOT, edtSrRazOT;
    DEL kub;
    DatabaseHelper myDbHelper;
    SimpleCursorAdapter scAdapter;
    Cursor c = null;
    ArrayList<String> listPoroda;
    ArrayList<String> listPorodaDel;
    ArrayList<String> listD;
    String Poroda, Kat, NumD;
    int D, kolDel, kolDr, i, sRazr, idKub;
    double vDel, vDr, vAll;
    String[] kat = {"Деловая", "Дровяная"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otvod);

        btnAddOT = (Button) findViewById(R.id.btnAddOT);
        btnClearOT = (Button) findViewById(R.id.btnClearOT);
        btnCalcOT = (Button) findViewById(R.id.btnCalcOT);
        btnAddOT.setOnClickListener(this);
        btnClearOT.setOnClickListener(this);
        btnCalcOT.setOnClickListener(this);
        btnCalcOT.setEnabled(false);

        edtNumOT = (EditText) findViewById(R.id.edtNumOT);
        edtSrRazOT = (EditText) findViewById(R.id.edtSrRazOT);

        kub = new DEL(this);
        kub.open();
        //kub.delRecKub();
        edtNumOT.setEnabled(false);
        edtSrRazOT.setEnabled(false);

        LoadDB();

        System.out.println("№Делянки " + Num);
        if (Num != ""){
            NumD = Num;
            btnCalcOT.setEnabled(false);
        }
        if (Num1 != ""){
            NumD = Num1;
            btnCalcOT.setEnabled(true);
        }
        edtNumOT.setText(NumD);
       // setNumD();
        AddSpinnersOtvod();
        LVShow();
     //   Toast.makeText(this, razr.NumDel, Toast.LENGTH_SHORT).show();
    }

    private void setNumD() {
      /*  c = kub.getAllKub();
        if (c.moveToFirst()){
            do {
                System.out.println("Poroda: " + c.getString(0)+
                        ", D: " + c.getString(1)+
                        ", R: " + c.getString(2)+
                        ", kat: " + c.getString(3)+
                        ", V: " + c.getString(4));
            }while (c.moveToNext());
        }
        c.close();

       */
    }


        private void LVShow() {
        //    String[] from = new String[] {DEL.KUB_NUMD, DEL.KUB_PORODA, DEL.KUB_D, DEL.KUB_KAT, DEL.KUB_V};
        //    int[] to = new int[] { R.id.tvNumO, R.id.tvPorodaO, R.id.tvDO, R.id.tvKatO, R.id.tvVO};
            String[] from = new String[] {DEL.SRAZR_Poroda, DEL.KUB_D, DEL.SRAZR_SRazr, DEL.KUB_KAT, DEL.KUB_V};
            int[] to = new int[] {R.id.tvPorodaO, R.id.tvDO, R.id.tvRO, R.id.tvKatO, R.id.tvVO};
        //    String[] from = new String[] {DEL.KUB_ID, DEL.KUB_SRAZR_ID, DEL.KUB_D, DEL.KUB_KAT, DEL.KUB_V};
         //   int[] to = new int[] {R.id.tvPorodaO, R.id.tvDO, R.id.tvRO, R.id.tvKatO, R.id.tvVO};
            scAdapter = new SimpleCursorAdapter(this, R.layout.otv_item, null, from, to, 0);
            lvOtvod = (ListView) findViewById(R.id.lvOtvod);
            lvOtvod.setAdapter(scAdapter);
            // добавляем контекстное меню к списку
            registerForContextMenu(lvOtvod);
            // создаем лоадер для чтения данных
            getSupportLoaderManager().initLoader(0, null, this);
        }

    private void LoadDB() {
        int h;
        myDbHelper = new DatabaseHelper(this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;

        }
        Log.d("myLogs","------------ТАБЛИЦА ОБЪЁМОВ---------------------");
        c = myDbHelper.query("SORT_TABLE", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                Log.d("myLogs", "Poroda: " + c.getString(1)+
                        ", D: " + c.getInt(2)+
                        ", R: " + c.getInt(3)+
                        ", V_DEL: " + c.getDouble(4)+
                        ", V_DR: " + c.getDouble(5)
                );
            } while (c.moveToNext());
        }
        c.close();
    }

    private void AddSpinnersOtvod() {
        //порода

        listPoroda = kub.getPorodaSR(Integer.valueOf(NumD),1);
        ArrayAdapter<String> adapterP = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listPoroda);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = (Spinner) findViewById(R.id.spPorodaOT);
        spinner.setAdapter(adapterP);
        //выделяем елемент
        spinner.setSelection(0);
        //установка обработчика нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //элемент выпадающего списка
                Poroda = spinner.getSelectedItem().toString();
                c = kub.getPorodaSRazr(Poroda,Integer.valueOf(NumD));
                if (c.moveToFirst()){
                    do {
                       // Toast.makeText(OtvodActivity.this, c.getString(3), Toast.LENGTH_SHORT).show();
                        edtSrRazOT.setText(c.getString(3));
                        sRazr = c.getInt(3);
                    }while (c.moveToNext());
                c.close();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //ступень толщины
        listD = myDbHelper.getD(2);
        ArrayAdapter<String> adapterST = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listD);
        adapterST.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinnerST = (Spinner) findViewById(R.id.spWidthOT);
        spinnerST.setAdapter(adapterST);
        spinnerST.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //элемент выпадающего списка
                String s;
                s = spinnerST.getSelectedItem().toString();
                D = Integer.valueOf(s);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapterKat = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kat);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinnerKat = (Spinner) findViewById(R.id.spKategoryOT);
        spinnerKat.setAdapter(adapterKat);
        //выделяем елемент
        spinnerKat.setSelection(0);
        spinnerKat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //элемент выпадающего списка
                Kat = spinnerKat.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int numD, srazrID=0;
        double V;
        switch (v.getId()){
            case R.id.btnAddOT:
                //расчёт объёма
                V = getV(sRazr);
                if (V==0) Toast.makeText(OtvodActivity.this,
                        "Ошибка! (Проверьте значения полей)", Toast.LENGTH_SHORT).show();
                else
                {
                    numD = Integer.valueOf(NumD);
                    srazrID = kub.getIdSrazr(numD, Poroda);
                    kub.addKUB(srazrID, D, Kat, V);
                    setNumD();
                    getSupportLoaderManager().getLoader(0).forceLoad();
                    i++;
                    if (i>=2) btnCalcOT.setEnabled(true);
                }
                break;
            case R.id.btnClearOT:
                AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                        OtvodActivity.this);
                quitDialog.setTitle("Вы действительно желаете отчистить таблицу?");

                quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int n;
                        n = Integer.valueOf(NumD);
                        kub.delRecKubNum(n);
                        getSupportLoaderManager().getLoader(0).forceLoad();
                        Toast.makeText(OtvodActivity.this, "Таблица отчищена!", Toast.LENGTH_SHORT).show();
                        i = 0;
                        btnCalcOT.setEnabled(false);
                    }
                });
                quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                });
                quitDialog.show();
                break;
            case R.id.btnCalcOT:
                String poroda;
                kolDel =0;
                kolDr = 0;
                vDel = 0;
                vDr = 0;
                vAll = 0;
                numD = Integer.valueOf(NumD);
                listPorodaDel = kub.getPorodaSR(Integer.valueOf(NumD),2);
                for (i=0;i<listPorodaDel.size();i++){
                    poroda = listPorodaDel.get(i);
                    c = kub.getDataForDel(poroda,numD);
                  //  Toast.makeText(this, poroda, Toast.LENGTH_SHORT).show();
                    if (c.moveToFirst()) {
                        do {
                            Log.d("Mylogs", "Номер: " + c.getInt(0) +
                                    ", Порода: " + c.getString(1) +
                                    ", Категория: " + c.getString(2) +
                                    ", Количество: " + c.getInt(3) +
                                    ", ИД отвода: " + c.getInt(5) +
                                    ", Объём: " + c.getDouble(4)
                            );
                            if (c.getString(2).equals("Деловая")){
                                kolDel = c.getInt(3);
                                vDel = c.getDouble(4);
                            }
                            if (c.getString(2).equals("Дровяная")){
                                kolDr = c.getInt(3);
                                vDr = c.getDouble(4);
                            }
                            idKub = c.getInt(5);
                        } while (c.moveToNext());
                    }
                    vAll = vDel+vDr;
                    if (Num != "") kub.addDel(idKub, kolDel, kolDr, vDel, vDr, vAll);
                    if (Num1 != "") kub.updateDel(idKub, kolDel, kolDr, vDel, vDr, vAll);
                    Log.d("Mylogs", "Номер: " + numD +
                            ", Порода: " + poroda +
                            ", Кол.деловой: " + kolDel +
                            ", Кол.дровяной: " + kolDr +
                            ", Объём деловой: " + vDel +
                            ", Объём дровяной: " + vDr +
                            ", Итог: " + vAll
                    );
                    kolDel =0;
                    kolDr = 0;
                    vDel = 0;
                    vDr = 0;
                    vAll = 0;
                }
                Num = "";
                Toast.makeText(OtvodActivity.this, "Данные о делянке успешно добавлены!",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    private double getV(int srH) {
        double v=0;
        c = myDbHelper.query("SORT_TABLE", null, null, null, null, null, null);
        if (c.moveToFirst()){
            do {
                if (Poroda.equals(c.getString(1)) && D == c.getInt(2) && srH == c.getInt(3)){
                    if (Kat == "Деловая") {v = c.getDouble(4); break;};
                    if (Kat == "Дровяная") {v = c.getDouble(5); break;};
                }
            }while (c.moveToNext());
        }
        return v;
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            kub.delRecKub(acmi.id);
            // получаем новый курсор с данными
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, kub);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    static class MyCursorLoader extends CursorLoader {
        DEL kub;
        public MyCursorLoader(Context context, DEL kub) {
            super(context);
            this.kub = kub;
        }

        @Override
        public Cursor loadInBackground() {
           // Cursor cursor = kub.getAllDataKub(Integer.valueOf(Num));
            Cursor cursor=null;
                if (Num != "") cursor = kub.getAllKub(Num);
                if (Num1 != "") cursor = kub.getAllKub(Num1);
          //  Cursor cursor = null;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return cursor;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        kub.close();
        myDbHelper.close();
        c.close();
        finish();
    }
}
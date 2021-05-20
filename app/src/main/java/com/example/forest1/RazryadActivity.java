package com.example.forest1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RazryadActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int CM_DELETE_ID = 1;
    public static String Num = "";
    final String ATTRIBUTE_NOMER_TEXT = "nomer";
    final String ATTRIBUTE_PORODA_TEXT = "poroda";
    final String ATTRIBUTE_D_TEXT = "d";
    final String ATTRIBUTE_H_TEXT = "h";
    final String ATTRIBUTE_RAZR_TEXT = "razr";
    SimpleAdapter sAdapter;
    ArrayList<Map<String, Object>> data;
    Map<String, Object> m;
    SharedPreferences sPref;

    Button btnAddR, btnClearR, btnCalcR;
    DatabaseHelper myDbHelper;
    DEL raz;
    SettingsActivity setting;
    SimpleCursorAdapter scAdapt;
    int kolDel = 0, kolMain=0, kolDop=0;
    Cursor c = null;
    EditText edtNumD, edtHeightD, edtKolD;
    ListView lvRazr;
    String Poroda;
    Integer D;

    ArrayList<String> listPorodaIzm = new ArrayList<String>();
    ArrayList<Integer> listOstIzm = new ArrayList<Integer>();;
    ArrayList<String> listPoroda;
    ArrayList<String> listD;
    ArrayList<String> listAddPoroda = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razryad);

        btnAddR = (Button) findViewById(R.id.btnAddR);
        btnClearR = (Button) findViewById(R.id.btnClearR);
        btnCalcR = (Button) findViewById(R.id.btnCalcR);
        btnAddR.setOnClickListener(this);
        btnClearR.setOnClickListener(this);
        btnCalcR.setOnClickListener(this);
        btnCalcR.setEnabled(false);

        lvRazr = (ListView) findViewById(R.id.lvRazr);
        edtNumD = (EditText) findViewById(R.id.edtNumDel);
        edtHeightD = (EditText) findViewById(R.id.edtHeightTree);
        edtKolD = (EditText) findViewById(R.id.edtKolD);

        getSettings();
//        Toast.makeText(this, "Получаяем? "+String.valueOf(kolMain) +" и " +String.valueOf(kolDop) , Toast.LENGTH_SHORT).show();

        raz = new DEL(this);
        raz.open();
        LoadDB();//загрузка таблицы разрядов высот и сортиментной таблицы
        LVonShow(); //отображение списка
        AddSpiners(); //заполнение выпадающих списков значениями
        ShowSRAZR();  //отображенние в ЛОГЕ данных из таблицы Средний разряд высот

    }

    public void getSettings() {
        sPref = getSharedPreferences("settings",MODE_PRIVATE);
        kolMain = Integer.valueOf(sPref.getString("saved_main","15"));
        kolDop = Integer.valueOf(sPref.getString("saved_dop","5"));
        kolDel = kolMain;
        edtKolD.setText(String.valueOf(kolDel));
    }

    private void ShowSRAZR() {
        c = raz.getDataSRazr();
        if(c.moveToFirst()){
            do {
                Log.d("myLogs", "_id: " + c.getInt(0) +
                        ", NumD: " + c.getInt(1)+
                        ", Poroda " + c.getString(2)+
                        ", SRazrH " + c.getInt(3)
                );
            }while(c.moveToNext());
        }
        c.close();
    }

    private void AddSpiners() {
        listPoroda = myDbHelper.getPoroda();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listPoroda);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = (Spinner) findViewById(R.id.spPoroda);
        spinner.setAdapter(adapter);
        //заголовок
        spinner.setPrompt("Порода дерева");
        //выделяем елемент
        spinner.setSelection(1);
        //установка обработчика нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //элемент выпадающего списка
                Poroda = spinner.getSelectedItem().toString();
                ostIzm(Poroda,0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listD = myDbHelper.getD(1);
        ArrayAdapter<String> adapterD = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listD);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinnerD = (Spinner) findViewById(R.id.spWidthTree);
        spinnerD.setAdapter(adapterD);

        //установка обработчика нажатия
        spinnerD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s;
                //элемент выпадающего списка
                s = spinnerD.getSelectedItem().toString();
                D=Integer.valueOf(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void LoadDB() {
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
        Log.d("myLogs","------------ТАБЛИЦА РАЗРЯД ВЫСОТ---------------------");
        c = myDbHelper.query("RAZR_H", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                Log.d("myLogs", "_id: " + c.getInt(0) +
                        ", Poroda: " + c.getString(1)+
                        ", D: " + c.getInt(2)+
                        ", minH: " + c.getDouble(3)+
                        ", maxH: " + c.getDouble(4)+
                        ", Razr: " + c.getInt(5)
                );
            } while (c.moveToNext());
        }
        c.close();
    }

    private void LVonShow() {
       /* String[] from = new String[] {DEL.RAZR_PORODA, DEL.RAZR_H,
                DEL.RAZR_D, DEL.RAZR_RAZ};
        int[] to = new int[] {R.id.tvP, R.id.tvH, R.id.tvD, R.id.tvVoz};
        // создааем адаптер и настраиваем список
        scAdapt = new SimpleCursorAdapter(this, R.layout.razr_item, null, from, to, 0);
        lvRazr.setAdapter(scAdapt);
        // добавляем контекстное меню к списку
        registerForContextMenu(lvRazr);
        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);
        */
        data = new ArrayList<Map<String, Object>>();
        //массив имён атрибутов, из которых будут читаться данные
        String[] from = {ATTRIBUTE_PORODA_TEXT, ATTRIBUTE_D_TEXT, ATTRIBUTE_H_TEXT, ATTRIBUTE_RAZR_TEXT};
        //массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.tvP, R.id.tvD, R.id.tvH, R.id.tvVoz};
        //создаём адаптер
        sAdapter = new SimpleAdapter(this, data, R.layout.razr_item, from, to);
        //присваиваем списку адаптер
        lvRazr.setAdapter(sAdapter);
        registerForContextMenu(lvRazr);
    }


    @Override
    public void onClick(View v) {
        double h;
        int razr, nD=0, flag, srazr;
        switch (v.getId()){
            case R.id.btnAddR:            //по кнопке добавить
                //проверка на заполнение полей
                if (edtNumD.getText().toString().equals("") || edtHeightD.getText().toString().equals("") )
                    Toast.makeText(RazryadActivity.this, "Пожалуйста! заполните поля", Toast.LENGTH_SHORT).show();
                else{
                    h = Double.valueOf(edtHeightD.getText().toString());
                    nD = Integer.valueOf(edtNumD.getText().toString());
                    razr = RastRazrH(Poroda, D, h);     //функция для получения разрядов высот по таблице
                    if (razr==0){
                        Toast.makeText(RazryadActivity.this, "Расчёт для данной породы не выполнен! Проверьте правильность заполнения полей!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (raz.findNumDSrazr(nD) == true){
                            Toast.makeText(this, "Данная делянка уже расчитана", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //Создаём новый Map
                            m = new HashMap<String, Object>();
                            m.put(ATTRIBUTE_NOMER_TEXT, nD);
                            m.put(ATTRIBUTE_PORODA_TEXT, Poroda);
                            m.put(ATTRIBUTE_D_TEXT, String.valueOf(D));
                            m.put(ATTRIBUTE_H_TEXT, edtHeightD.getText().toString());
                            m.put(ATTRIBUTE_RAZR_TEXT, String.valueOf(razr));
                            //добавим его в коллекцию
                            data.add(m);
                            flag = FindData(Poroda);
                            if (flag == 0) {
                                listAddPoroda.add(Poroda);
                                if (listAddPoroda.size()==1){
                                    listOstIzm.add(kolMain);
                                }else
                                {
                                    kolDel += kolDop;
                                    listOstIzm.add(kolDop);
                                }
                                System.out.println(Poroda);
                            }
                            //уведомляем, что данные изменились
                            sAdapter.notifyDataSetChanged();
                            edtNumD.setEnabled(false);
                            Num = edtNumD.getText().toString();
                            // Toast.makeText(this, raz.NUMD, Toast.LENGTH_SHORT).show();
                            edtNumD.setGravity(9);
                            ostIzm(Poroda,1);
                            if (kolDel == 0) btnCalcR.setEnabled(true);
                        }
                    }
                }
                break;
            case R.id.btnClearR:    //по кнопке отчистить таблицу
                AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                        RazryadActivity.this);
                quitDialog.setTitle("Вы действительно желаете отчистить таблицу?");

                quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // raz.delRecRazr();               //удаляем все записи из таблицы разряд высот
                                             // getSupportLoaderManager().getLoader(0).forceLoad();     //обновляем список
                        data.clear();
                        sAdapter.notifyDataSetChanged();
                        Toast.makeText(RazryadActivity.this, "Таблица отчищена!", Toast.LENGTH_SHORT).show();
                        btnCalcR.setEnabled(false);
                        kolDel = kolMain;
                        edtKolD.setText(String.valueOf(kolDel));
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
            case R.id.btnCalcR:     // по кнопке рассчитать
                for (int i=0;i<listAddPoroda.size();i++){
                    srazr=calcSrazr(listAddPoroda.get(i));
                    System.out.println(listAddPoroda.get(i) + ": разряд " + String.valueOf(srazr));
                    raz.addSrazr(Integer.valueOf(Num),listAddPoroda.get(i).toString(),srazr);     //заполняем таблицу средний разряд высот
                }
                ShowSRAZR();
                Intent intent = new Intent(this, OtvodActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
    //расчёт среднего разряда высот
    public int calcSrazr(String poroda){
        String str, p="", r="";
        int n=0, raz=0, z=0, count=0, srazr=0;
        for (int i = 0;i<data.size();i++){
            str = data.get(i).toString();
            String[] words = str.split(", ");
            n=0;
            for(String word : words){
                switch (n){
                    case 1:
                        p = word.substring(7);
                        break;
                    case 2:
                        r = word.substring(5);
                        z = Integer.valueOf(r);
                        // System.out.println(z);
                        if (p.equals(poroda)==true) {
                            raz+=z;
                            count++;
                        }
                        //  System.out.println(raz);
                        break;
                }
                //  System.out.println(word);
                n++;
                if (n>2)break;
            }
        }
        if (count != 0) srazr =  raz/count;
       // Toast.makeText(this, Integer.toString(srazr), Toast.LENGTH_SHORT).show();
        return srazr;
    }
    //группировка по добавленным породам
    public int FindData(String poroda){
        int f=0,n;
        String str, p="";
        for (int i = 0;i<data.size()-1;i++) {
            str = data.get(i).toString();
            String[] words = str.split(", ");
            n=0;
            for (String word : words) {
                switch (n) {
                    case 1:
                        p = word.substring(7);
                        if (p.equals(poroda)==true) {
                            f=1;
                        }
                        break;
                }
                //System.out.println(word);
                n++;
                if (n>1)break;
            }
            if (f==1) break;
        }
        return f;
    }

    //функция для получения породы из списка которую мы удалили
    public String getPoroda(int pos){
        String poroda="",str;
        Integer n;
        str = data.get(pos).toString();
        String[] words = str.split(", ");
        n=0;
        for (String word : words) {
            switch (n) {
                case 1:
                    poroda = word.substring(7);
                    break;
            }
            n++;
        }
        return poroda;
    }

    //функция для определения остатка ввода количества деревьев
    private void ostIzm(String poroda, int r) {
            //kolDel--;
            int k=0;
            if (r == 1){
                for (int i=0; i<listAddPoroda.size(); i++) {
                    if (poroda == listAddPoroda.get(i)) {
                        k = listOstIzm.get(i);
                        if(k != 0){
                            k--;
                            listOstIzm.set(i, k);
                            kolDel--;
                            edtKolD.setText(String.valueOf(listOstIzm.get(i)));
                        }
                        if (k == 0)
                            Toast.makeText(this, "По " + listAddPoroda.get(i) + " данные заполнены!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (r == 0) {
                for (int i = 0; i < listAddPoroda.size(); i++) {
                    if (poroda.equals(listAddPoroda.get(i))) {
                        k = listOstIzm.get(i);
                        edtKolD.setText(String.valueOf(k));
                    }
                }
            }
            if (kolDel <= 0) {
                kolDel=0;
                Toast.makeText(RazryadActivity.this, "Вы заполнили таблицу!", Toast.LENGTH_SHORT).show();
                btnCalcR.setEnabled(true);
                edtKolD.setText(String.valueOf(kolDel));
            }
    }

    //функция для получения разряда высот из сорт.таблицы
    private int RastRazrH(String poroda, Integer d, double h) {
        Double maxH, minH;
        int r=0;
        String p;
        c = myDbHelper.query("RAZR_H", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                if (poroda.equals(c.getString(1)) && d == c.getInt(2)){
                    minH=c.getDouble(3);
                    maxH=c.getDouble(4);
                    if (minH<=h && h<=maxH){
                        r = c.getInt(5);
                        break;
                    }
                }
            } while (c.moveToNext());
        }
        return r;
    }
    //функцция создания контексного меню для списка
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    //функция считывающаю нажатие на элемент контексного меню
    public boolean onContextItemSelected(MenuItem item) {
        String poroda, poroda1;
        int p=0;
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            //получаем инфу о пункте списка
            acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            //удаляем Map из коллекции, используя позицию пункта в списке
            poroda = getPoroda(acmi.position);
            data.remove(acmi.position);
            for(int i = 0; i<listAddPoroda.size(); i++){
                poroda1 = listAddPoroda.get(i);
                if (listAddPoroda.get(i).equals(poroda)==true){
                    p = listOstIzm.get(i);
                    p++;
                    kolDel++;
                    listOstIzm.set(i,p);
                    ostIzm(poroda,0);
                }
            }
          //  Toast.makeText(this, String.valueOf(poroda), Toast.LENGTH_SHORT).show();
            //уведомляем что данные изменились
            sAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }
/*
    @Override
    public MyCursorLoader onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, raz);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapt.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
/*
    static class MyCursorLoader extends CursorLoader {
        DEL raz;
        public MyCursorLoader(Context context, DEL raz) {
            super(context);
            this.raz = raz;
        }

        @Override
        public Cursor loadInBackground() {
            int i=0;
            Cursor cursor = raz.getDataSRazr();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return cursor;
        }
    }
*/
    //при закрытии активити закрываем все подключения
    @Override
    protected void onDestroy() {
        super.onDestroy();
        raz.close();
        myDbHelper.close();
        c.close();
        finish();
    }
}
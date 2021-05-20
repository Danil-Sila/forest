package com.example.forest1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CM_DELETE_ID = 1;
    private static final int CM_UPDATE_ID = 2;
    Button btnAddDel, btnClearDel;
    DEL del;
    SimpleCursorAdapter scAdapter;
    ListView lvDel;
    public static String Num1 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddDel = (Button) findViewById(R.id.btnAddDel);
        btnClearDel = (Button) findViewById(R.id.btnClearDel);
        btnAddDel.setOnClickListener(this);
        btnClearDel.setOnClickListener(this);

        del = new DEL(this);
        del.open();
        lvDelShow();
    }


    private void lvDelShow() {
        String[] from = new String[] {DEL.SRAZR_NUMD, DEL.SRAZR_Poroda, DEL.DEL_KOLDEL,
                DEL.DEL_KOLDR, DEL.DEL_VDEL, DEL.DEL_VDR, DEL.DEL_ITOG};
        int[] to = new int[] {R.id.tvNumDel, R.id.tvPorodaDel, R.id.tvKolDel, R.id.tvKolDr,
                R.id.tvVDel, R.id.tvVDr, R.id.tvItogV};

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.del_item, null, from, to, 0);
        lvDel = (ListView) findViewById(R.id.lvDel);
        lvDel.setAdapter(scAdapter);
        // добавляем контекстное меню к списку
        registerForContextMenu(lvDel);
        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddDel:
                Intent intent = new Intent(this, RazryadActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Num1 = "";
                break;
            case R.id.btnClearDel:
                AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                        MainActivity.this);
                quitDialog.setTitle("Вы действительно желаете отчистить таблицу(БД)?");

                quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        del.delRecDel();
                        del.delRecKub();
                        del.delRecSRazr();
                        getSupportLoaderManager().getLoader(0).forceLoad();
                        Toast.makeText(MainActivity.this, "Таблица отчищена!",
                                Toast.LENGTH_SHORT).show();;
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
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        menu.add(0, CM_UPDATE_ID, 0, R.string.update_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            del.delRecDel(acmi.id);
            // получаем новый курсор с данными
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        if (item.getItemId() == CM_UPDATE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            //del.delRecDel(acmi.id);
            int id;
            id = del.getDEL_SRAZRID(acmi.id);
            Num1 = String.valueOf(id);
            Intent intent = new Intent(this, OtvodActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    //создание и обработка пунктов меню

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                break;
            case R.id.action_exit:
                Toast.makeText(MainActivity.this, getString(R.string.action_exit), Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                MainActivity.this);
        quitDialog.setTitle("Вы действительно желаете выйти?");

        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                finish();
                System.exit(0);
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, del);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    static class MyCursorLoader extends CursorLoader {
        DEL del;
        public MyCursorLoader(Context context, DEL del) {
            super(context);
            this.del = del;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = del.getDataDel();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return cursor;
        }
    }


}
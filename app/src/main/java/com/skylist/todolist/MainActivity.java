package com.skylist.todolist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText        textoTarefa = null;
    private Button          botaoAdicionar = null;
    private ListView        listaTarefas = null;
    private SQLiteDatabase  bd;

    private ArrayAdapter<String> itensAdaptador = null;
    private ArrayList<String> itens = null;
    private ArrayList<Integer> ids = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            textoTarefa = findViewById(R.id.texto_id);
            botaoAdicionar = findViewById(R.id.btn_adicionar_id);
            listaTarefas = findViewById(R.id.list_view_id);

            //Banco de dados
            bd = openOrCreateDatabase("APP_TAREFAS", MODE_PRIVATE, null);

            //Tabela tarefas
            bd.execSQL("CREATE TABLE IF NOT EXISTS tarefas( id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            botaoAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    salvarTarefa( textoTarefa.getText().toString() );
                }
            });

            listaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    removerTarefa( ids.get( position ) );
                }
            });

            //Listar tarefas
            recuperarTarefas();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void salvarTarefa(String texto){

        try {
            if( texto.equals("") ) {
                Toast.makeText(getApplicationContext(), "Digite uma tarefa!", Toast.LENGTH_SHORT).show();
            }else{
                bd.execSQL("INSERT INTO tarefas (tarefa) VALUES ('" + texto + "')");
                Toast.makeText(getApplicationContext(), "Tarefa salva com sucesso!", Toast.LENGTH_SHORT).show();
                recuperarTarefas();
                textoTarefa.setText("");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void recuperarTarefas(){
        try{
            //Recupera as tarefas
            Cursor cursor = bd.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            //recuperar os ids das colunas
            int indiceColunaid = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            //Lista
            listaTarefas = findViewById(R.id.list_view_id);

            //Criar adaptador
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itensAdaptador = new ArrayAdapter<String>( getApplicationContext(), android.R.layout.simple_list_item_2, android.R.id.text2, itens );
            listaTarefas.setAdapter(itensAdaptador);

            //listar tarefas
            cursor.moveToFirst();
            while( cursor != null ){

                itens.add( cursor.getString( indiceColunaTarefa ) );
                ids.add( Integer.parseInt( cursor.getString(indiceColunaid) ) );
                Log.i("RESULTADO - ", "Tarefa: "+ cursor.getString( indiceColunaTarefa ) + "- ID: " + cursor.getInt(indiceColunaid)  );

                cursor.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void removerTarefa( Integer id){
        try{

            bd.execSQL("DELETE FROM tarefas WHERE id=" + id );
            recuperarTarefas();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

package todolist.cursoandroid.com.todolist;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText textoTarefa;
    private Button   botaoAdicionar;
    private ListView listaTarefas;
    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    private AlertDialog.Builder dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //recuperar componentes
            textoTarefa = (EditText) findViewById(R.id.textoId);
            botaoAdicionar = (Button) findViewById(R.id.botaoAdicionarId);


            //lista
            listaTarefas = (ListView) findViewById(R.id.listViewId);


            //Banco dados
            bancoDados = openOrCreateDatabase("apptarefas", MODE_PRIVATE, null);

            //tabela tarefas

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas(id INTEGER PRIMARY KEY AUTOINCREMENT,tarefa VARCHAR)");



            botaoAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String textoDigitado = textoTarefa.getText().toString();

                    salvarTarefa(textoDigitado);



                  //  bancoDados.execSQL("INSERT INTO tarefas(tarefa) VALUES('" + textoDigitado + "')");
                }
            });

            listaTarefas.setLongClickable(true);
            listaTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                    // Log.i("ITEM: ", i + "/" + ids.get(i));
                    removerTarefa(ids.get(i));

                    return true;
                }

            });

            //recuperar tarefas
            recuperarTarefas();



        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void salvarTarefa(String texto){

        try{

            if(texto.equals("")){

                Toast.makeText(MainActivity.this,"Digite uma tarefa",Toast.LENGTH_SHORT).show();
            }else{

                bancoDados.execSQL("INSERT INTO tarefas(tarefa) VALUES('" + texto + "')");
                Toast.makeText(MainActivity.this,"Tarefa salva com sucesso!",Toast.LENGTH_SHORT).show();
                recuperarTarefas();
                textoTarefa.setText("");
            }



        }catch(Exception e){

            e.printStackTrace();
        }




    }

    private void recuperarTarefas(){

        try{
            //Recupera as tarefas
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC",null);

            //recuperar os ids das colunas
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");


            //Criar adaptador
            itens = new ArrayList<String>();
            ids =   new ArrayList<Integer>();
            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text2,
                   itens );
            listaTarefas.setAdapter(itensAdaptador);



            //listar as tarefas. Volta para o primeiro elemento do cursor
            cursor.moveToFirst();
            //Enquanto tiver elemento dentro do cursor o while vai ser executado
            while(cursor!= null){

                Log.i("Resultado - "," Id Tarefa:"+ cursor.getString(indiceColunaId)+" Tarefa: "+ cursor.getString(indiceColunaTarefa));

               //adiciona elementos dentro do arraylist
               itens.add(cursor.getString(indiceColunaTarefa));
               ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
                cursor.moveToNext();


            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void removerTarefa(final Integer id){

        try{


            dialog = new AlertDialog.Builder(MainActivity.this);

            dialog.setTitle("ATENÇÃO");

            dialog.setMessage("Deseja remover a tarefa?");

            dialog.setCancelable(false);

            dialog.setIcon(android.R.drawable.ic_dialog_alert);


            dialog.setNegativeButton("NÃO",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

            dialog.setPositiveButton("SIM",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            bancoDados.execSQL("DELETE FROM tarefas WHERE id="+id);
                            recuperarTarefas();
                            Toast.makeText(MainActivity.this,"Tarefa removida",Toast.LENGTH_SHORT).show();

                        }
                    });

            dialog.create();
            dialog.show();



        }catch(Exception e){

            e.printStackTrace();
        }
    }


}

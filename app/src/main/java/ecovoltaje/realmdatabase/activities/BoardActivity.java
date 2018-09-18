package ecovoltaje.realmdatabase.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ecovoltaje.realmdatabase.R;
import ecovoltaje.realmdatabase.adapters.BoardAdapter;
import ecovoltaje.realmdatabase.models.Board;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class BoardActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Board>>, AdapterView.OnItemClickListener {

    private Realm realm;
    private FloatingActionButton fab;
    private ListView boardListView;
    private BoardAdapter boardAdapter;
    private RealmResults<Board> boards;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm =  Realm.getDefaultInstance();
        boards = realm.where(Board.class).findAll();
        boards.addChangeListener(this);

        setContentView(R.layout.activity_board);
        fab= (FloatingActionButton)findViewById(R.id.fabAddButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showAlertForCreatingBoard("Create a new board","Write a name for your board");

            }
        });
        boardListView = (ListView)findViewById(R.id.listViewBoard);

        boardListView.setOnItemClickListener(this);

        boardAdapter = new BoardAdapter(this,boards,R.layout.list_view_board_item);

        boardListView.setAdapter(boardAdapter);
        registerForContextMenu(boardListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.delete_all:
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(boards.get(info.position).getTitle());
        getMenuInflater().inflate(R.menu.context_menu_board_activity, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.delete_board:
                deleteBoard(boards.get(info.position));

                return true;
            case R.id.edit_board:
                showAlertForEditingBoard("Edit Board","Change the name of the board", boards.get(info.position));

                return true;
            default:
                return super.onContextItemSelected(item);
        }


    }


    private void deleteBoard(Board board){
        realm.beginTransaction();
        board.deleteFromRealm();
        realm.commitTransaction();
    }

    private void editBoard(String newName,Board board){
        realm.beginTransaction();
        board.setTitle(newName);
        realm.copyToRealmOrUpdate(board);
        realm.commitTransaction();
    }

    private void showAlertForCreatingBoard(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(title != null) builder.setTitle(title);
        if(title != null) builder.setTitle(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_board, null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextBoardName);


        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String boardName = input.getText().toString().trim();
                if(boardName.length() > 0)
                    createNewBoard(boardName);
                else
                    Toast.makeText(getApplicationContext(),"The name is requierd to create  a New Board",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showAlertForEditingBoard(String title, String message,final Board board){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(title != null) builder.setTitle(title);
        if(title != null) builder.setTitle(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_board, null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextBoardName);


        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String boardName = input.getText().toString().trim();
                if(boardName.length() ==0) {
                    Toast.makeText(getApplicationContext(),"The name is requierd to edit the current board",Toast.LENGTH_SHORT).show();
                }
                else if(boardName.equals(board.getTitle())){
                    Toast.makeText(getApplicationContext(),"The name is the same it was before",Toast.LENGTH_SHORT).show();
                }
                else {
                    editBoard(boardName,board);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void createNewBoard(String boardName) {
        realm.beginTransaction();
        Board board = new Board(boardName);
        realm.copyToRealm(board );
        realm.commitTransaction();

    }

    @Override
    public void onChange(RealmResults<Board> boards) {
        boardAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(BoardActivity.this,NoteActivity.class);
        intent.putExtra("id",boards.get(position).getId());
        startActivity(intent);

    }
}

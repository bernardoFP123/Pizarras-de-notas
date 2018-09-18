package ecovoltaje.realmdatabase.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import ecovoltaje.realmdatabase.R;
import ecovoltaje.realmdatabase.adapters.NoteAdapter;
import ecovoltaje.realmdatabase.models.Board;
import ecovoltaje.realmdatabase.models.Note;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

public class NoteActivity extends AppCompatActivity implements RealmChangeListener<Board> {

    private ListView listView;
    private FloatingActionButton fab;

    private NoteAdapter noteAdapter;
    private RealmList<Note> notes;
    private Realm realm;

    private int boardId;
    private Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        realm = Realm.getDefaultInstance();

        if(getIntent().getExtras() != null){
            boardId = getIntent().getExtras().getInt("id");
        }

        board = realm.where(Board.class).equalTo("id",boardId).findFirst();
        board.addChangeListener(this);
        notes = board.getNotes();
        this.setTitle(board.getTitle());

        fab = (FloatingActionButton) findViewById(R.id.fabAddNote);
        listView = (ListView)findViewById(R.id.listViewNote);
        noteAdapter = new NoteAdapter(this,notes,R.layout.list_view_note_item);

        listView.setAdapter(noteAdapter);

        registerForContextMenu(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForCreatingNote("Crear nueva nota","Texto");
            }
        });


    }

    private void showAlertForCreatingNote(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(title != null) builder.setTitle(title);
        if(message != null) builder.setTitle(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_note, null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextNewNote);


        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String note = input.getText().toString().trim();
                if(note.length() > 0)
                    createNewNote(note);
                else
                    Toast.makeText(getApplicationContext(),"Can't add an empty note",Toast.LENGTH_LONG).show();
            }


        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    //Actions
    private void createNewNote(String note) {
        realm.beginTransaction();
        Note _note = new Note(note);
        realm.copyToRealm(_note);
        board.getNotes().add(_note);
        realm.commitTransaction();
    }

    private void deleteNote(Note note){
        realm.beginTransaction();
        note.deleteFromRealm();
        realm.commitTransaction();
    }

    private void editNote(String newNoteDescription,Note note){
        realm.beginTransaction();
        note.setDescription(newNoteDescription);
        realm.copyToRealmOrUpdate(note);
        realm.commitTransaction();
    }

    private void deleteAllNotes(Board board){
        realm.beginTransaction();
        board.getNotes().deleteAllFromRealm();
        realm.commitTransaction();
    }

    private void showAlertForEditingNote(String title, String message, final Note note) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(title != null) builder.setTitle(title);
        if(message != null) builder.setTitle(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_note, null);
        builder.setView(viewInflated);


        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextNewNote);
        input.setText(note.getDescription());

        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String description = input.getText().toString().trim();
                if(description.length() > 0)
                    editNote(description,note);
                else
                    Toast.makeText(getApplicationContext(),"Can't add an empty note",Toast.LENGTH_LONG).show();
            }


        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    //Events


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu_note_activity,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.edit_note:
                showAlertForEditingNote("Edit Note","Change the description of the note",notes.get(info.position));
                return true;
            case R.id.delete_note:
                deleteNote(notes.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notas, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.deleteAllNotes:
                deleteAllNotes(board);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }



    @Override
    public void onChange(Board board) {
        noteAdapter.notifyDataSetChanged();
    }
}

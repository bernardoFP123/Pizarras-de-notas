package ecovoltaje.realmdatabase.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import ecovoltaje.realmdatabase.R;
import ecovoltaje.realmdatabase.models.Board;
import ecovoltaje.realmdatabase.models.Note;

/**
 * Created by Bernardo_NoAdmin on 12/06/2017.
 */

public class NoteAdapter extends BaseAdapter {

    private Context context;
    private List<Note> list;
    private int layout;


    public NoteAdapter(Context context, List<Note> notes, int layout){
        this.context = context;
        this.list = notes;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder vh;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout,null);
            vh = new ViewHolder();
            vh.description = (TextView) convertView.findViewById(R.id.textViewNoteDescription);
            vh.createdAt = (TextView) convertView.findViewById(R.id.textViewNoteCreatedAt);
            convertView.setTag(vh);
        }
        else{
            vh = (ViewHolder) convertView.getTag();
        }

        Note note = list.get(position);
        vh.description.setText(note.getDescription());
        DateFormat df = new SimpleDateFormat("dd/mm/yyyy");
        String date = df.format(note.getCreatedAt());
        vh.createdAt.setText(date);
        return convertView;
    }


    public class ViewHolder{
        TextView description;
        TextView createdAt;
    }
}

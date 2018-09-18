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

public class BoardAdapter extends BaseAdapter {

    private Context context;
    private List<Board> list;
    private int layout;


    public BoardAdapter(Context context, List<Board> boards, int layout){
        this.context = context;
        this.list = boards;
        this.layout = layout;
    }



    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Board getItem(int position) {
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
            vh.title = (TextView)convertView.findViewById(R.id.textViewBoardTitle);
            vh.notes = (TextView)convertView.findViewById(R.id.textViewBoardNotes);
            vh.createdAt = (TextView)convertView.findViewById(R.id.textViewBoardDate);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder)convertView.getTag();
        }
        Board board = list.get(position);

        int numberOfNotes = board.getNotes().size();
        String textForNotes = (numberOfNotes == 1) ? numberOfNotes  + " Note" : numberOfNotes + " Notes";
        DateFormat df = new SimpleDateFormat("dd/mm/yyyy");
        String createdAt = df.format(board.getCreatedAt());
        vh.createdAt.setText(createdAt);
        vh.notes.setText(textForNotes);
        vh.title.setText(board.getTitle());
        return convertView;
    }

    public class ViewHolder{
        TextView title;
        TextView notes;
        TextView createdAt;
    }
}

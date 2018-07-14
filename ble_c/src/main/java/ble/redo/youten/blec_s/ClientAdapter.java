package ble.redo.youten.blec_s;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ClientAdapter extends BaseAdapter {

    private ArrayList<ChatMessage> mList;
    private LayoutInflater mInflater;

    public ClientAdapter(Context context, ArrayList<ChatMessage> messages) {
        this.mList = messages;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        ChatMessage message = mList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder((View) convertView.findViewById(R.id.list_child)
                    , (TextView) convertView.findViewById(R.id.chat_msg));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (message.isSiri()) {
            viewHolder.msg.setTextColor(Color.RED);
            viewHolder.msg.setGravity(Gravity.LEFT);
        } else {
            viewHolder.msg.setTextColor(Color.BLUE);
            viewHolder.msg.setGravity(Gravity.RIGHT);
        }
        viewHolder.msg.setText(message.getMessage());
        return convertView;
    }

    class ViewHolder {
        protected View child;
        protected TextView msg;

        public ViewHolder(View child, TextView msg) {
            this.child = child;
            this.msg = msg;
        }
    }
}
package com.sqy.scancode.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sqy.scancode.R;
import com.sqy.scancode.bluetoogh.ChatMessage;

import java.util.ArrayList;

public class ServerAdapter extends BaseAdapter {

	private ArrayList<ChatMessage> list;
	private LayoutInflater mInflater;
	
	public ServerAdapter(Context context, ArrayList<ChatMessage> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		ChatMessage message = list.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, null);
			viewHolder = new ViewHolder((View)convertView.findViewById(R.id.list_child)
					, (TextView)convertView.findViewById(R.id.chat_msg));
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

        public ViewHolder(View child, TextView msg){
            this.child = child;
            this.msg = msg;
        }
  }
}
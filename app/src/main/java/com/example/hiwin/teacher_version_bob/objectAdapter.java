package com.example.hiwin.teacher_version_bob;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/*
    reference:
        https://qiita.com/vc7/items/c863908b5273edd4fe53
        https://xnfood.com.tw/android-listview-baseadapter/
 */
public class objectAdapter extends BaseAdapter {
    private final JSONArray stories;
    private static LayoutInflater inflater = null;

    public objectAdapter(Context context, JSONArray stories) {
        this.stories = stories;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return stories.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return stories.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalStateException("not json object");
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        viewHolder = new ViewHolder();

        convertView = inflater.inflate(R.layout.row_of_object, parent, false);
        // 把拿到的 textView 設定進 view holder
        viewHolder.story_name = (TextView) convertView.findViewById(R.id.object_row_name);
        viewHolder.story_total = (TextView) convertView.findViewById(R.id.object_row_total);

        try {
            viewHolder.story_name.setText(stories.getJSONObject(position).getString("story_name"));
            viewHolder.story_total.setText("Total page:" + stories.getJSONObject(position).getString("total"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        TextView story_name;
        TextView story_total;
    }

    private String parseBondState(int value) {
        switch (value) {
            case 0xa:
                return "BOND_NONE";
            case 0xb:
                return "BOND_BONDING";
            case 0xc:
                return "BOND_BONDED";
            default:
                return null;
        }
    }
}

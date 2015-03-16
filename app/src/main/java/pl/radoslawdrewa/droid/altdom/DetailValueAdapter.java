package pl.radoslawdrewa.droid.altdom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by radoslaw.drewa on 2014-10-24.
 */
public class DetailValueAdapter extends ArrayAdapter<DetailValue> {

    Context context;
    int layoutResourceId;
    DetailValue data[] = null;

    public DetailValueAdapter(Context ctx, int resource, DetailValue[] objects) {
        super(ctx, resource, objects);
        context = ctx;
        layoutResourceId = resource;
        data = objects;
    }

    @Override
    public DetailValue getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = LayoutInflater.from(context);
        row = inflater.inflate(layoutResourceId, parent, false);

        TextView txtDetail = (TextView) row.findViewById(R.id.txtDitail);
        TextView txtValue = (TextView) row.findViewById(R.id.txtValue);

        DetailValue detailValue = data[position];

        txtDetail.setText(detailValue.detail);
        txtValue.setText(detailValue.value);

        return row;
    }
}

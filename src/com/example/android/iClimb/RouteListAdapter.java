package com.example.android.iClimb;

import java.util.List;

import com.example.android.BluetoothChat.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class RouteListAdapter extends ArrayAdapter<Route> 
{
		private List<Route> routes;
		private int layoutResourceId;
		private Context context;
		
		RouteListAdapter(Context context, int layoutResourceId, List<Route> routes)
		{
			super(context, layoutResourceId, routes);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.routes = routes;
		}
		
		@Override 
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View row = convertView;
			RouteHolder holder = null;
			
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new RouteHolder();
			holder.route = routes.get(position);
			holder.deleteRouteButton = (ImageButton) row.findViewById(R.id.delete_route);
			holder.deleteRouteButton.setTag(holder.route);
			holder.loadRouteButton = (ImageButton) row.findViewById(R.id.load_route);
			holder.loadRouteButton.setTag(holder.route);
			
			holder.name = (TextView) row.findViewById(R.id.route_name);
			
			row.setTag(holder);
			
			setUpItem(holder);
			
			return row;
		}
		
		private void setUpItem(RouteHolder holder)
		{
			holder.name.setText(holder.route.getName());
		}
		
		public static class RouteHolder
		{
			Route route;
			TextView name;
			ImageButton deleteRouteButton, loadRouteButton;
		}
}

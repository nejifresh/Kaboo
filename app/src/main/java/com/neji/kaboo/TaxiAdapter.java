package com.neji.kaboo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Neji on 10/13/2014.
 */
public class TaxiAdapter extends BaseAdapter {
    final Context context;
    final List<Taxi> TaxiList;

    public TaxiAdapter(Context context, List<Taxi> taxis){
        this.context = context;
        this.TaxiList = taxis;
    }
public Taxi giveItemPosition(int position){
    return this.TaxiList.get(position);
}
    @Override
    public int getCount() {
        return this.TaxiList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.TaxiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_items, parent, false);
        }
        TextView driverName = (TextView) convertView.findViewById(R.id.bizname);
        TextView driverCar = (TextView) convertView.findViewById(R.id.address);
       TextView distanceAway = (TextView) convertView.findViewById(R.id.distance);
        CircleImageView imageView = (CircleImageView)convertView.findViewById(R.id.profile_image);
       RatingBar ratingBar = (RatingBar)convertView.findViewById(R.id.ratebar);
ImageView dot = (ImageView)convertView.findViewById(R.id.dotonline);

        Taxi t = this.TaxiList.get(position);
        if(!t.equals(null)) {

            if (t.getLiveStatus().toString().matches("Live")) {
                dot.setImageResource(R.drawable.dotonline);
            } else {
                dot.setImageResource(R.drawable.dotoffline);
            }

            driverName.setText(toCamelCase(t.getDriverName()));
            driverCar.setText(toCamelCase(t.getVehicleMake() + " " + t.getVechicleModel() + ", " + t.getVehicleYear()));

            Picasso.with(context)
                    .load(t.getImageURL())
                    .placeholder(R.drawable.taxi)// optional
                    .resize(150, 150)
                    .centerCrop()
                    .error(R.drawable.photo) // optional
                    .into(imageView);


            // imageView.setImageBitmap(t.getImage());
            ratingBar.setRating(2);
            distanceAway.setText(t.getDistanceAway());
        }
   // distanceAway.setText("Approx. "+ (String.valueOf(roundTwoDecimals(t.getDistanceFrom()/1000)  +" km away"))); //provide distance in km
     // distanceAway.setText(t.getTimeDifference());
        notifyDataSetChanged();

        return convertView;

    }

    float roundTwoDecimals(float d)
    {
        DecimalFormat twoDForm = new DecimalFormat("####.#");
        return Float.valueOf(twoDForm.format(d));

    }

    public static String toCamelCase(String inputString) {
        String result = "";
        if (inputString.length() == 0) {
            return result;
        }
        char firstChar = inputString.charAt(0);
        char firstCharToUpperCase = Character.toUpperCase(firstChar);
        result = result + firstCharToUpperCase;
        for (int i = 1; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            char previousChar = inputString.charAt(i - 1);
            if (previousChar == ' ') {
                char currentCharToUpperCase = Character.toUpperCase(currentChar);
                result = result + currentCharToUpperCase;
            } else {
                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result = result + currentCharToLowerCase;
            }
        }
        return result;
    }

}

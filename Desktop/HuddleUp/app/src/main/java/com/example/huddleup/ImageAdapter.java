package com.example.huddleup;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ImageModel> arrayList;
    private final String[] gridColor;
    public int selectedImage = -1;

    /**
     * Parameterized constructor of imageAdapter()
     * @param context Context
     * @param arrayList ArrayList<ImageModel>
     */
    public ImageAdapter(Context context, ArrayList<ImageModel> arrayList, String[] gridcolor) {
        this.context = context;
        this.arrayList = arrayList;
        this.gridColor = gridcolor;
    }

    /**
     * Get number of images
     * @return int
     */
    @Override
    public int getCount() {
        return arrayList.size();
    }

    /**
     * Select a specific image based on its position
     * @param position int
     * @return Object
     */
    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    /**
     * Returns item id
     * @param i int
     * @return long
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Returns specific view for image
     * @param position int
     * @param convertView View
     * @param parent ViewGroup
     * @return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.image_list, parent, false);
        }
        // Set background color as transparent
        convertView.setBackgroundColor(Color.parseColor(gridColor[position]));


        // Mount image to gridView
        ImageView imageView;
        imageView = convertView.findViewById(R.id.image);
        imageView.setImageResource(arrayList.get(position).getThumbIds());

        // Highlight selected image only
        //if (position == selectedImage) {
        //convertView.setBackgroundColor(Color.YELLOW);
        //}

        // Set view bounds (reduce spacing)
        imageView.setAdjustViewBounds(true);

        // Return current view
        return convertView;
    }

    /**
     * Allows background color to update after an element has been selected
     * Only one cell is colored at a time
     * @param position int
     * @param color String
     */
    public void setGridColor(int position, String color) {
        Arrays.fill(this.gridColor, "#00FFFFFF");
        this.gridColor[position] = color;
    }
}
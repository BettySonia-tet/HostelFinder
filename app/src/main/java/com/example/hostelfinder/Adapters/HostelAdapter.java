package com.example.hostelfinder.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hostelfinder.Activities.ContentActivity;
import com.example.hostelfinder.Model.HostelData;
import com.example.hostelfinder.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class HostelAdapter extends RecyclerView.Adapter<HostelAdapter.ViewHolder> {

    private List<HostelData> mHostelDataList;
    private Context context;

    public HostelAdapter(Context context, List<HostelData> mHostelDataList ) {
        this.mHostelDataList = mHostelDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.hostel_item_list,parent,false);
        ViewHolder viewHolder =new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HostelData hostelDataList = mHostelDataList.get(position);

        holder.textViewName.setText("Name: "+ hostelDataList.getHostelName());
        holder.textViewLocation.setText("Location: "+ hostelDataList.getLocation());
        holder.textViewType.setText("Type: "+ hostelDataList.getType());
        holder.textViewSharing.setText("Sharing: "+ hostelDataList.getSharing());
        holder.textViewPrice.setText("Price: "+ hostelDataList.getPrice());
        holder.textViewAvSpaces.setText("Available spaces: "+ hostelDataList.getAvailableSpaces());
       /* holder.linearlayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContentActivity.class);
                intent.putExtra("hostel name", hostelDataList.getHostelName());
                intent.putExtra("Hostel Location", hostelDataList.getLocation());
                intent.putExtra("Hostel Type", hostelDataList.getType());
                intent.putExtra("Hostel Sharing", hostelDataList.getSharing());
                intent.putExtra("Hostel Price", hostelDataList.getLocation());
                intent.putExtra("Hostel Available Spaces", hostelDataList.getAvailableSpaces());
                intent.putExtra("Hostel Image", hostelDataList.getHostelImage1());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.linearlayout1.getContext().startActivity(intent);
            }
        });*/

        Glide.with(context).load(hostelDataList.getHostelImage1()).into(holder.hostelImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postid = hostelDataList.getPostid();
                Intent intent = new Intent(context, ContentActivity.class);
                intent.putExtra("postid", postid);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mHostelDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView hostelImage;
        TextView textViewName;
        TextView textViewLocation;
        TextView textViewType;
        TextView textViewSharing;
        TextView textViewPrice;
        TextView textViewAvSpaces;
        CardView linearlayout1;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            hostelImage = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.txtName);
            textViewLocation= itemView.findViewById(R.id.txtLocation);
            textViewType = itemView.findViewById(R.id.txtType);
            textViewSharing = itemView.findViewById(R.id.txtShare);
            textViewPrice = itemView.findViewById(R.id.txtPrice);
            textViewAvSpaces = itemView.findViewById(R.id.txtAvSpaces);
            linearlayout1 = itemView.findViewById(R.id.linearlayout1);
        }
    }
}

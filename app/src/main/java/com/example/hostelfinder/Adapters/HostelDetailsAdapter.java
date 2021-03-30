package com.example.hostelfinder.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hostelfinder.Activities.BookingActivity;
import com.example.hostelfinder.Activities.GoogleMapsActivity;
import com.example.hostelfinder.Model.HostelData;
import com.example.hostelfinder.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HostelDetailsAdapter extends  RecyclerView.Adapter<HostelDetailsAdapter.ViewHolder>{

    private List<HostelData> mHostelDataList;
    private Context context;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public HostelDetailsAdapter(List<HostelData> mHostelDataList, Context context) {
        this.mHostelDataList = mHostelDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public HostelDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.hostel_details,parent,false);
        HostelDetailsAdapter.ViewHolder viewHolder =new HostelDetailsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final HostelDetailsAdapter.ViewHolder holder, int position) {
        final HostelData hostelDataList = mHostelDataList.get(position);

        holder.hostelName.setText("Name: "+ hostelDataList.getHostelName());
        holder.txtLocation.setText("Location: "+ hostelDataList.getLocation());
        holder.txtType.setText("Type: "+ hostelDataList.getType());
        holder.txtShare.setText("Sharing: "+ hostelDataList.getSharing());
        holder.txtPrice.setText("Price: "+ hostelDataList.getPrice());
        holder.txtAvSpaces.setText("Available spaces: "+ hostelDataList.getAvailableSpaces());
        holder.txtRules.setText(hostelDataList.getRules());

        Glide.with(context).load(hostelDataList.getHostelImage1()).into(holder.image1);
        Glide.with(context).load(hostelDataList.getHostelImage2()).into(holder.image2);
        Glide.with(context).load(hostelDataList.getHostelImage3()).into(holder.image3);

        holder.bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postid = hostelDataList.getPostid();
                Intent intent = new Intent(context, BookingActivity.class);
                intent.putExtra("postid", postid);
                intent.putExtra("name",hostelDataList.getHostelName());
                intent.putExtra("sharing", hostelDataList.getSharing());
                intent.putExtra("availablespaces", hostelDataList.getAvailableSpaces());
                intent.putExtra("type", hostelDataList.getType());
                intent.putExtra("price", hostelDataList.getPrice());
                intent.putExtra("location", hostelDataList.getLocation());
                context.startActivity(intent);
            }

        });

        holder.showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GoogleMapsActivity.class);
                intent.putExtra("Latitude", hostelDataList.getHostelLatitude());
                intent.putExtra("Longitude", hostelDataList.getHostelLongitude());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mHostelDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView hostelName, txtLocation, txtType, txtShare, txtPrice, txtAvSpaces, txtRules;
        // private String name;
        private ImageView image1, image2, image3;
        private Button bookBtn, showLocation;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            hostelName = itemView.findViewById(R.id.hostelName);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtType =itemView. findViewById(R.id.txtType);
            txtShare = itemView.findViewById(R.id.txtShare);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtAvSpaces = itemView.findViewById(R.id.txtAvSpaces);
            txtRules = itemView.findViewById(R.id.txtRules);
            image1 = itemView.findViewById(R.id.image1);
            image2 = itemView.findViewById(R.id.image2);
            image3 = itemView.findViewById(R.id.image3);
            bookBtn =itemView. findViewById(R.id.bookBtn);
            showLocation = itemView.findViewById(R.id.showLocation);

        }
    }
}

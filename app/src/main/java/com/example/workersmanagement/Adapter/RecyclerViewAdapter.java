package com.example.workersmanagement.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersmanagement.R;
import com.example.workersmanagement.Model.Worker;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    private List<Worker> mWorkerList;
    private OnWorkerListener mOnWorkerListener;
    private Filter filter;
    public RecyclerViewAdapter(List<Worker> mWorkerList ,OnWorkerListener mOnWorkerListener  ){
        this.mWorkerList=mWorkerList;
        this.mOnWorkerListener=mOnWorkerListener;
    }
    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workeritem,parent,false);
        return new MyViewHolder (view,mOnWorkerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {
        Worker worker = mWorkerList.get(position);
        holder.Id.setText(String.valueOf(worker.getId()));
        holder.name.setText(worker.getLast_name());
        holder.first_name.setText(worker.getFirst_name());
        holder.field.setText(worker.getField());
        byte[] bytes = worker.getImage();


            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            holder.picture.setImageBitmap(bm);


    }

    @Override
    public int getItemCount() {
        return mWorkerList.size();
    }



    public void setSearchResult(ArrayList<Worker> search_result) {
        mWorkerList=search_result;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView Id,name,first_name,field;
        private ImageView picture;
        private ImageButton email,phone_call;
        private OnWorkerListener mOnWorkerListener;
        public MyViewHolder(@NonNull View itemView,OnWorkerListener mOnWorkerListener) {
            super(itemView);
            this.mOnWorkerListener = mOnWorkerListener;
            Id=itemView.findViewById(R.id.Id);
            name=itemView.findViewById(R.id.name);
            first_name=itemView.findViewById(R.id.first_nametext);
            field=itemView.findViewById(R.id.field);
            email=itemView.findViewById(R.id.email);
            picture=itemView.findViewById(R.id.imageView);
            phone_call=itemView.findViewById(R.id.phone_call);

            itemView.setOnClickListener(v ->{
                mOnWorkerListener.onWorkerClick(getAdapterPosition(),v);
            });
            phone_call.setOnClickListener(v ->{
                mOnWorkerListener.onWorkerCall(getAdapterPosition());

            });
            email.setOnClickListener(v->{
                mOnWorkerListener.onWorkerSend(getAdapterPosition());
            });
        }

    }
    public interface OnWorkerListener{
        void onWorkerClick(int position,View v);

        void onWorkerCall(int position);

        void onWorkerSend(int position);
    }
}

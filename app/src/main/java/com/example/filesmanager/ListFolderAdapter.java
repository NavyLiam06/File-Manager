package com.example.filesmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ListFolderAdapter extends RecyclerView.Adapter<ListFolderAdapter.ViewHolder> {
    private ArrayList<com.example.filesmanager.Folder> listFolder;
    private Context mContext;
    private MyInterface mInterface;
    private boolean showSelect;

    public boolean isSelectedAll() {
        for (com.example.filesmanager.Folder f : listFolder)
            if (!f.isSelected())
                return false;
        return true;
    }

    public void setShowSelect(boolean showSelect) {
        this.showSelect = showSelect;
    }

    public boolean isShowSelect() {
        return showSelect;
    }

    public void setmInterface(MyInterface mInterface) {
        this.mInterface = mInterface;
    }

    public ListFolderAdapter(Context context, ArrayList<com.example.filesmanager.Folder> list) {
        listFolder = list;
        mContext = context;
        showSelect = false;
    }

    public void clearSelected() {
        for (com.example.filesmanager.Folder f : listFolder) {
            f.setSelected(false);
        }
    }

    void selectAll() {
        for (com.example.filesmanager.Folder f : listFolder) {
            f.setSelected(true);
        }
    }

    boolean empty() {
        for (com.example.filesmanager.Folder f : listFolder)
            if (f.isSelected())
                return false;
        return true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.recycleview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (showSelect) holder.cbxSelected.setVisibility(View.VISIBLE);
        else holder.cbxSelected.setVisibility(View.GONE);
        holder.cbxSelected.setChecked(listFolder.get(position).isSelected());
        Folder folder = listFolder.get(position);
        holder.tvName.setText(folder.getFolderName());
        if (folder.isFolder) holder.tvNumberChild.setText(folder.getNumberChild() + " files");
        else {
            holder.tvNumberChild.setText(Formatter.formatShortFileSize(mContext, folder.getNumberChild()));
        }
        holder.tvCreatedDate.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm").format(folder.getCreatedDate()));
        if (folder.isFolder) holder.imvFolder.setImageResource(R.drawable.ic_folder);
        else if (folder.getFolderName().matches(".*mp3") || folder.getFolderName().matches(".*wav"))
            holder.imvFolder.setImageResource(R.drawable.ic_music);
        else if (folder.getFolderName().matches(".*apk"))
            holder.imvFolder.setImageResource(R.drawable.ic_android);
        else if (folder.getFolderName().matches(".*mp4"))
            holder.imvFolder.setImageResource(R.drawable.mp4);
        else if (folder.getFolderName().matches(".*GIF"))
            holder.imvFolder.setImageResource(R.drawable.ic_image);
        else if (folder.getFolderName().matches(".*png") || folder.getFolderName().matches(".*jpg") || folder.getFolderName().matches(".*jpeg")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (listFolder.get(position).getBitmap() == null) {
                    try {
                        Uri uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", folder.getThis());
                        Bitmap thumbnail = mContext.getContentResolver().loadThumbnail(uri, new Size(80, 70), null);
                        folder.setBitmap(thumbnail);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (listFolder.get(position).getBitmap() != null)
                    holder.imvFolder.setImageBitmap(listFolder.get(position).getBitmap());
                else holder.imvFolder.setImageResource(R.drawable.ic_image);
            } else {
                holder.imvFolder.setImageResource(R.drawable.ic_image);
            }
        } else holder.imvFolder.setImageResource(R.drawable.ic_docs);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInterface.mOnclick(listFolder.get(position));
                if (showSelect) notifyItemChanged(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mInterface.mOnLongClick(listFolder.get(position));
                return true;
            }
        });
        holder.cbxSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listFolder.get(position).setSelected(!listFolder.get(position).isSelected());
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listFolder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvCreatedDate, tvNumberChild;
        private ImageView imvFolder;
        private CheckBox cbxSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFolderName);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            tvNumberChild = itemView.findViewById(R.id.tvNumberChild);
            imvFolder = itemView.findViewById(R.id.imvFolder);
            cbxSelected = itemView.findViewById(R.id.cbxSelected);
        }
    }

    public interface MyInterface {
        void mOnclick(com.example.filesmanager.Folder folder);

        void mOnLongClick(com.example.filesmanager.Folder folder);
    }

}

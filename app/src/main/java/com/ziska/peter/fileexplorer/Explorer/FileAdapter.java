package com.ziska.peter.fileexplorer.Explorer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ziska.peter.fileexplorer.R;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyViewHolder> {

    private ExplorerContract.Presenter mPresenter;

    public FileAdapter(ExplorerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.explorer_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        mPresenter.onBindFilesRowViewAtPosition(position, myViewHolder);
    }

    @Override
    public int getItemCount() {
        if (mPresenter == null) {
            return 0;
        }
        return mPresenter.getFilesRowsCount();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView mTextView;
        private ImageView mFileImage;
        private View mItemView;

        public MyViewHolder(@NonNull final View itemView) {

            super(itemView);
            mItemView = itemView;
            mTextView = itemView.findViewById(R.id.fileName);
            mFileImage = itemView.findViewById(R.id.fileImage);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public void setTextView(String text) {
            mTextView.setText(text);
        }

        public void setSelectedItemView(int color) {
            mItemView.setBackgroundColor(color);
        }

        public void setFolderImage() {
            mFileImage.setImageResource(R.drawable.ic_folder);
        }

        @Override
        public void onClick(View view) {
            Integer position = getAdapterPosition();

            if (mPresenter.isMultiChoiceEnabled()) {
                if (mPresenter.isFileSelected(position)) {
                    mPresenter.deselectFile(position);
                } else {
                    mPresenter.selectFile(position);
                }
                notifyDataSetChanged();
            } else {
                mPresenter.handleClickOnRowItem(position);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Integer position = getAdapterPosition();
            if (!mPresenter.isMultiChoiceEnabled()) {
                mPresenter.enableCAB();
            }
            mPresenter.selectFile(position);
            mPresenter.setMultiChoiceEnabled(true);
            notifyItemChanged(position);
            return true;
        }
    }
}

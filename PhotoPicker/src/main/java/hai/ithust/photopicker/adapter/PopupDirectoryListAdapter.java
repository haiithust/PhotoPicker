package hai.ithust.photopicker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import hai.ithust.photopicker.R;
import hai.ithust.photopicker.entity.PhotoDirectory;

/**
 * @author conghai on 12/20/18.
 */
public class PopupDirectoryListAdapter extends BaseAdapter {
    private List<PhotoDirectory> directories = new ArrayList<>();
    private RequestManager glide;

    public PopupDirectoryListAdapter(RequestManager glide) {
        this.glide = glide;
    }

    public void setDirectories(List<PhotoDirectory> directories) {
        this.directories.clear();
        if (directories != null) {
            this.directories.addAll(directories);
        }
        notifyDataSetChanged();
    }

    public PhotoDirectory getDirectoryByPos(int pos) {
        if (pos >= 0 && pos < directories.size()) {
            return directories.get(pos);
        }
        return null;
    }

    @Override
    public int getCount() {
        return directories.size();
    }


    @Override
    public PhotoDirectory getItem(int position) {
        return directories.get(position);
    }


    @Override
    public long getItemId(int position) {
        return directories.get(position).hashCode();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.picker_item_directory, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bindData(directories.get(position), glide);

        return convertView;
    }

    private static class ViewHolder {
        private ImageView ivCover;
        private TextView tvName;
        private TextView tvCount;

        private ViewHolder(View rootView) {
            ivCover = rootView.findViewById(R.id.iv_dir_cover);
            tvName = rootView.findViewById(R.id.tv_dir_name);
            tvCount = rootView.findViewById(R.id.tv_dir_count);
        }

        private void bindData(PhotoDirectory directory, RequestManager glide) {
            glide.load(directory.getCoverPath())
                    .dontTransform().dontAnimate().override(800, 800)
                    .into(ivCover);
            tvName.setText(directory.getName());
            tvCount.setText(String.valueOf(directory.getPhotos().size()));
        }
    }

}

package app.invictus.com.samplemusic.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;

import app.invictus.com.samplemusic.R;

import static android.R.attr.data;

/**
 * Created by invictus on 3/3/17.
 */

public class MusicCursorAdapter extends RecyclerView.Adapter<MusicCursorAdapter.ViewHolder> {

    private static final String LOG_TAG = MusicCursorAdapter.class.getSimpleName();
    private Cursor cursor;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_song_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Cursor cursor = this.getItem(position);
        holder.populateView(cursor);
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public Cursor getItem(final int position) {
        if (this.cursor != null && !this.cursor.isClosed()) {
            this.cursor.moveToPosition(position);
        }
        return this.cursor;
    }

    public Cursor getCursor() {
        return this.cursor;
    }

    public void swapCursor(final Cursor cursor) {
        this.cursor = cursor;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView songName;
        TextView songArtist;
        ImageView songArt;

        public ViewHolder(View view) {
            super(view);

            songName = (TextView) view.findViewById(R.id.song_name);
            songArtist = (TextView) view.findViewById(R.id.song_artist);
            songArt = (ImageView) view.findViewById(R.id.song_art);
        }

        public void populateView(Cursor cursor) {
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String album1 = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//            int duration1 = Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));


            /**
             * Sorry I will right a better cache method later.
             * **/
            final MediaMetadataRetriever mdata = new MediaMetadataRetriever();
            mdata.setDataSource(data);

            DownloadImages downloadImages = new DownloadImages();
            downloadImages.execute(mdata);

            downloadImages.setFinishedDownload(new finishedInterface() {
                @Override
                public void getBitmap(byte[] bitmap) {
                    Glide.with(songArt.getContext())
                            .load(bitmap)
                            .asBitmap()
                            .placeholder(R.drawable.music_icon)
                            .into(songArt);
                }
            });


            songName.setText(title);
            songArtist.setText(artist);
        }

    }

    private class DownloadImages extends AsyncTask<MediaMetadataRetriever, Void, byte[]> {


        @Override
        protected byte[] doInBackground(MediaMetadataRetriever... strings) {


            if (strings[0].getEmbeddedPicture() != null) {
                return strings[0].getEmbeddedPicture();

            }

            return null;
        }

        @Override
        protected void onPostExecute(byte[] bitmap) {
            super.onPostExecute(bitmap);
            anInterface.getBitmap(bitmap);
        }


        private finishedInterface anInterface;

        public void setFinishedDownload(finishedInterface finishedInterface) {
            this.anInterface = finishedInterface;
        }
    }

    public interface finishedInterface {
        public void getBitmap(byte[] bitmap);
    }

}

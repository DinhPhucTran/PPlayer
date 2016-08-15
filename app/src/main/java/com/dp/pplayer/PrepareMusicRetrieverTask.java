package com.dp.pplayer;

import android.os.AsyncTask;

/**
 * Created by DinhPhuc on 10/03/2016.
 */
public class PrepareMusicRetrieverTask extends AsyncTask<Void, Void, Void> {
    MusicRetriever mRetriever;
    MusicRetrieverPreparedListener mListener;

    public PrepareMusicRetrieverTask(MusicRetriever retriever, MusicRetrieverPreparedListener listener) {
        mRetriever = retriever;
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mRetriever.prepare();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mListener.onMusicRetrieverPrepared();
    }

    public interface MusicRetrieverPreparedListener {
        public void onMusicRetrieverPrepared();
    }
}

package com.dp.pplayer;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    private Button btnPlay;
    private Button btnNext;
    private Button btnPre;
    private Button btnRepeat;
    private Button btnShuffle;
    private Button btnPlaylist;
    private SeekBar sbSongProgress;
    private TextView tvCurrentDuration;
    private TextView tvTotalDuration;
    private TextView tvSongTitle;
    private TextView tvSongArtist;
    private ImageView ivAlbumArt;
    private ImageView ivPlayerHeaderArt;
    public ListView playerPlaylist;
    private Button btnHeaderPlay;

    private Bitmap bitmap;
    private Handler handler;
    public RelativeLayout playerHeaderBg;

    public PlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        //----------Init views--------------
        btnPlay = (Button) view.findViewById(R.id.btnPlay);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        btnPre = (Button) view.findViewById(R.id.btnPre);
        btnShuffle = (Button) view.findViewById(R.id.btnShuffle);
        btnRepeat = (Button) view.findViewById(R.id.btnRepeat);
        btnPlaylist = (Button) view.findViewById(R.id.btnPlaylist);
        sbSongProgress = (SeekBar) view.findViewById(R.id.playerSeekbar);
        tvCurrentDuration = (TextView) view.findViewById(R.id.tvCurrentDuration);
        tvTotalDuration = (TextView) view.findViewById(R.id.tvTotalDuration);
        tvSongTitle = (TextView) view.findViewById(R.id.playerSongTitle);
        tvSongArtist = (TextView) view.findViewById(R.id.playerArtist);
        ivAlbumArt = (ImageView) view.findViewById(R.id.playerAlbumArt);
        ivPlayerHeaderArt = (ImageView) view.findViewById(R.id.playerHeaderArt);
        playerPlaylist = (ListView) view.findViewById(R.id.playerPlaylist);
        btnHeaderPlay = (Button) view.findViewById(R.id.btnHeaderPlay);
        playerHeaderBg = (RelativeLayout)view.findViewById(R.id.playerHeaderBg);
        //---------------------------------------------

//        playerPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent playIntent = new Intent(getContext(), MusicService.class);
//                playIntent.setAction(Integer.toString(position));
//                view.getContext().startService(playIntent);
//            }
//        });
        playerPlaylist.setAdapter(MainActivity.songAdapter);

        //------Click events-----
        ivAlbumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(MusicService.ACTION_TOGGLE_PLAYBACK);
                v.getContext().startService(playIntent);
                if (MusicService.mPlayer.isPlaying()) {
                    btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play));
                    btnHeaderPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play));
                } else {
                    btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_pause));
                    btnHeaderPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_pause));
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent = new Intent(v.getContext(), MusicService.class);
                nextIntent.setAction(MusicService.ACTION_NEXT);
                v.getContext().startService(nextIntent);
            }
        });

        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent preIntent = new Intent(v.getContext(), MusicService.class);
                preIntent.setAction(MusicService.ACTION_PREV);
                v.getContext().startService(preIntent);
//                setSeekbarValues();
//                updatePlayerUI();
            }
        });

        btnHeaderPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(MusicService.ACTION_TOGGLE_PLAYBACK);
                v.getContext().startService(playIntent);
                if (MusicService.mPlayer.isPlaying()) {
                    btnHeaderPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play));
                    //btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play));
                    btnPlay.setBackgroundResource(R.drawable.btn_play);
                } else {
                    btnHeaderPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_pause));
                    btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_pause));
                }
            }
        });

        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerPlaylist.getVisibility() == View.GONE) {
                    btnPlaylist.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_playlist_red));
                    playerPlaylist.setVisibility(View.VISIBLE);
                } else {
                    btnPlaylist.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_playlist));
                    playerPlaylist.setVisibility(View.GONE);
                }
                Toast.makeText(getContext(), "Btn Playlist clicked", Toast.LENGTH_SHORT).show();
            }
        });
        //---------------------------

        handler = new Handler();
        sbSongProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (MusicService.getState() == MusicService.STATE_PLAYING) {
                        MusicService.mPlayer.pause();
                    }

                    MusicService.mPlayer.seekTo(progress * 1000);
                    int currentDuration = MusicService.mPlayer.getCurrentPosition();
                    tvCurrentDuration.setText("" + formatTime(currentDuration));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicService.mPlayer.start();
                btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_pause));
            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MusicService.mPlayer != null) {
                    int currentDuration = MusicService.mPlayer.getCurrentPosition();
                    tvCurrentDuration.setText("" + formatTime(currentDuration));
                    sbSongProgress.setProgress(currentDuration / 1000);
                    if (currentDuration / 1000 == 1)
                        setSeekbarValues();
                }
                handler.postDelayed(this, 100);
            }
        });
        return view;
    }

    private String formatTime(int miliSec) {
        String timerString = "";
        String secondsString = "";

        int hours = (int) (miliSec / (1000 * 60 * 60));
        int minutes = (int) ((miliSec % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((miliSec % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        if (hours > 0)
            timerString = hours + ":";

        if (seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = Integer.toString(seconds);

        timerString += minutes + ":" + secondsString;
        return timerString;
    }

    public void updatePlayerUI() {
        tvSongTitle.setText(MusicService.mSongTitle);
        tvSongArtist.setText(MusicService.mArtist);
        bitmap = BitmapFactory.decodeFile(getCoverArtPath(getContext(), MusicService.mAlbumId));
        if (bitmap != null) {
            ivAlbumArt.setImageBitmap(bitmap);
            ivPlayerHeaderArt.setImageBitmap(bitmap);
        } else {
            ivAlbumArt.setImageResource(R.drawable.default_album_art);
            ivPlayerHeaderArt.setImageResource(R.drawable.default_album_art);
        }

        if (MusicService.mPlayer.isPlaying()) {
            btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_pause));
            btnHeaderPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_pause));
        } else {
            btnPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play));
            btnHeaderPlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_play));
        }
    }

    public void setSeekbarValues() {
        int totalDuration = MusicService.mPlayer.getDuration();
        sbSongProgress.setMax(totalDuration / 1000);
        tvTotalDuration.setText("" + formatTime(totalDuration));
    }

    public String getCoverArtPath(Context context, long androidAlbumId) {
        String path = null;
        Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                new String[]{Long.toString(androidAlbumId)},
                null);
        if (c != null) {
            if (c.moveToFirst()) {
                path = c.getString(0);
            }
            c.close();
        }
        return path;
    }

    public void changeHeaderButton() {
        if(btnHeaderPlay.getVisibility() == View.GONE) {
            btnHeaderPlay.setVisibility(View.VISIBLE);
            btnPlaylist.setVisibility(View.GONE);
        } else {
            btnHeaderPlay.setVisibility(View.GONE);
            btnPlaylist.setVisibility(View.VISIBLE);
        }
    }

    public void showPlaylistButton() {
        btnHeaderPlay.setVisibility(View.GONE);
        btnPlaylist.setVisibility(View.VISIBLE);
    }

    public void hidePlaylistButton() {
        btnHeaderPlay.setVisibility(View.VISIBLE);
        btnPlaylist.setVisibility(View.GONE);
    }
}

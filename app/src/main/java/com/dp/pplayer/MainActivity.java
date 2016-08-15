package com.dp.pplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PrepareMusicRetrieverTask.MusicRetrieverPreparedListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SlidingUpPanelLayout slidingLayout;

    public static FloatingActionButton fab;
    private PlayerFragment mainPlayerFragment;
    private BroadcastReceiver receiver;

    public static SongAdapter songAdapter;
    public static AlbumAdapter albumAdapter;
    public static ArrayList<Song> songList;

    public static boolean isRetrieving;
    public static boolean playAfterRetrieving;
    public static MusicRetriever musicRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        musicRetriever = new MusicRetriever(getContentResolver());
        (new PrepareMusicRetrieverTask(musicRetriever, MainActivity.this)).execute();
        isRetrieving = true;

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(MusicService.ACTION_ON_COMPLETION) || intent.getAction().equals(MusicService.ACTION_ON_PREPARED_PLAYER)) {
                    mainPlayerFragment.setSeekbarValues();
                    mainPlayerFragment.updatePlayerUI();
                    if(slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN)
                        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    Toast.makeText(getApplicationContext(), "UI updated", Toast.LENGTH_SHORT).show();
                }
            }
        };

        registerReceiver(receiver, new IntentFilter(MusicService.ACTION_ON_PREPARED_PLAYER));

        //-----Set up toolbar--------
        viewPager = (ViewPager)findViewById(R.id.playlistViewPager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout)findViewById(R.id.mainTabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        if (!MainActivity.fab.isShown())
                            MainActivity.fab.show();
                        break;
                    case 1:
                        MainActivity.fab.hide();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //---------------------------------------------

        //Replace sliding up panel with player fragment
        mainPlayerFragment = new PlayerFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mainPlayerContainer, mainPlayerFragment).commit();

        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.mainSlidingLayout);
        slidingLayout.setPanelSlideListener(onSlideListener());
        //slidingLayout.setDragView(mainPlayerFragment.playerHeaderBg);
        slidingLayout.setScrollableView(mainPlayerFragment.playerPlaylist);
        if(MusicService.getState().equals("Stopped") || MusicService.getState().equals("Paused") || MusicService.getState().equals("Preparing")){
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_exit) {
            Intent stopIntent = new Intent(MusicService.ACTION_STOP);
            startService(stopIntent);
            unregisterReceiver(receiver);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                this.finishAffinity();
            } else {
                finish();
                System.exit(0);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMusicRetrieverPrepared() {
        isRetrieving = false;
        songList = musicRetriever.getSongList();
        songAdapter = new SongAdapter(this, songList);

        Toast.makeText(this, "Retrieving completed",Toast.LENGTH_SHORT).show();
//        if(playAfterRetrieving) {
//            Intent intent = new Intent(MusicService.ACTION_PLAY);
//            startService(intent);
//        }
    }

    private void setupViewPager(ViewPager pager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new PlaylistFragment(), "Song");
        viewPagerAdapter.addFragment(new AlbumFragment(), "Album");
        viewPagerAdapter.addFragment(new ArtistFragment(), "Artist");
        pager.setAdapter(viewPagerAdapter);
    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                MainActivity.fab.hide();
            }

            @Override
            public void onPanelCollapsed(View panel) {
                mainPlayerFragment.hidePlaylistButton();
                MainActivity.fab.show();
            }

            @Override
            public void onPanelExpanded(View panel) {
                mainPlayerFragment.showPlaylistButton();
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        };
    }

    //-------------------Click events--------------
    public void songPicked(View view) {
        Intent playIntent = new Intent(this, MusicService.class);
        playIntent.setAction(Integer.toString(Integer.parseInt(view.getTag().toString())));
        startService(playIntent);
    }

    public void playerHeaderClick(View view) {
        if(slidingLayout.getPanelState()== SlidingUpPanelLayout.PanelState.EXPANDED)
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        else
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

    }
    //---------------------------------------------
}

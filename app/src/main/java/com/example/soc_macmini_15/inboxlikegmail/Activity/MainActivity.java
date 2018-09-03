package com.example.soc_macmini_15.inboxlikegmail.Activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.soc_macmini_15.inboxlikegmail.Adapter.MessageAdapter;
import com.example.soc_macmini_15.inboxlikegmail.Model.Message;
import com.example.soc_macmini_15.inboxlikegmail.Network.ApiClient;
import com.example.soc_macmini_15.inboxlikegmail.Network.ApiInterface;
import com.example.soc_macmini_15.inboxlikegmail.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created By Navneet Singh
 * Date :- 30/08/2018
 */


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MessageAdapter.MessageAdapterListener {

    private static final String TAG = "MainActivity";
    private List<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private DrawerLayout mDrawableLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    /**
     * Initialising the values of views
     */
    private void init() {

        mDrawableLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SendEmailActivity.class);
                startActivity(intent);
                Snackbar.make(v, "Compose Email", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawableLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.nav_primary:
                        showData();
                        Toast.makeText(MainActivity.this, "Primary !", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_social:
                        showData();
                        Toast.makeText(MainActivity.this, "Social !", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_promotions:
                        showData();
                        Toast.makeText(MainActivity.this, "Promotions !", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_updates:
                        showData();
                        Toast.makeText(MainActivity.this, "Updates !", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_forums:
                        showData();
                        Toast.makeText(MainActivity.this, "Forums !", Toast.LENGTH_SHORT).show();
                        return true;
                }

                return true;
            }
        });
        showData();
    }

    /**
     * Function for showing the data in recyclerview
     */
    private void showData() {
        messageAdapter = new MessageAdapter(this, messages, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setAdapter(messageAdapter);

        actionModeCallback = new ActionModeCallback();

        // Show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getInbox();
                    }
                }
        );
    }


    /**
     * Fetches mail messages by making HTTP request
     */

    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<List<Message>> call = apiService.getInbox();
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                // Clear the inbox
                messages.clear();
                Log.d(TAG, "onResponse: " + response.body());
                //add All the messages
                //  messages.addAll(response.body());

                // TODO - avoid looping
                // the loop was performed to add colors to each message
                for (Message message : response.body()) {
                    // generate a random color
                    message.setColor(getRandomMaterialColor("400"));
                    messages.add(message);
                }
                messageAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Unable to fetch Json: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //  Handle action bar items click here.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                Toast.makeText(getApplicationContext(), "Search. . .", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                mDrawableLayout.openDrawer(GravityCompat.START);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        // Swipe refresh is performed, fetch the messages again
        getInbox();

    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);

    }

    @Override
    public void onIconImportantClicked(int position) {
        // Star icon is clicked
        // mark the message as important
        Message message = messages.get(position);
        message.setImportant(!message.isImportant());
        messages.set(position, message);
        messageAdapter.notifyDataSetChanged();

    }

    @Override
    public void onMessageRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (messageAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // Read the message which removes bold from the row
            Message message = messages.get(position);
            message.setRead(true);
            messages.set(position, message);
            messageAdapter.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(), "Read: " + message.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);

    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        messageAdapter.toggleSelection(position);
        int count = messageAdapter.getSelectedItemCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

            //disable swipe refresh if action mode is enabled
            swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    //delete all the selected messages
                    deleteMessages();
                    mode.finish();
                    return true;
                case R.id.action_archive:
                    Toast.makeText(MainActivity.this, "Archived Mail", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;
                case R.id.action_read:
                    Toast.makeText(MainActivity.this, "Marked as Read", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;
                case R.id.action_move_to:
                    Toast.makeText(MainActivity.this, "Move to", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;
                case R.id.action_change_label:
                    Toast.makeText(MainActivity.this, "Change Labels", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;
                case R.id.action_report_spam:
                    Toast.makeText(MainActivity.this, "Report Spam", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            messageAdapter.clearSelection();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.resetAnimationIndex();

                }
            });

        }
    }

    private void deleteMessages() {
        messageAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions = messageAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            messageAdapter.removeData(selectedItemPositions.get(i));
        }
        messageAdapter.notifyDataSetChanged();
    }
}

package com.mcgovern.bened.messaging_app_and;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.core.Tag;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<ChatMessage, PostViewHolder> mAdapter;
    private DatabaseReference mRef;
    private static final String TAG = "MyActivity";
    private String currentUID;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.message_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Starts process of populating view with messages
        initialiseView();
        //Gets metadata for message user
        currentUID = user.getUid();
        //Log.d(TAG,"UID: "+currentUID);

        //Allows for screen resize when keyboard is brought up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        ImageButton fab =
                (ImageButton) findViewById(R.id.send);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(view);
                autoScroll();
            }
        });
        AutoCompleteTextView editText = (AutoCompleteTextView)findViewById(R.id.input);
        //editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    send(v);
                    Log.d(TAG, "Reached");
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void initialiseView() {
        recyclerView = (RecyclerView)findViewById(R.id.message_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRef = FirebaseDatabase.getInstance().getReference();
        setUpAdapter();
        mAdapter.notifyDataSetChanged();
        //Auto scrolls to the last message sent

    }

    private void setUpAdapter() {
        mAdapter = new FirebaseRecyclerAdapter<ChatMessage, PostViewHolder>(
                ChatMessage.class,
                R.layout.message,
                PostViewHolder.class,
                mRef
        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, ChatMessage model, int position) {
                // Get references to the views of message.xml
                String thisUID = model.getUID();
                boolean isMe = false;
                int timeDif = 60;
                if(thisUID.equals(currentUID)){
                    Log.d(TAG,"1. Reached");
                    isMe =true;
                }
                if(position != 0)
                    timeDif = timeInSeconds(DateFormat.format("HHmmss ",mAdapter.getItem(position).getTime())) - timeInSeconds(DateFormat.format("HHmmss ",mAdapter.getItem(position-1).getTime()));

                LinearLayout layout = (LinearLayout)findViewById(R.id.chatBubble);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 0);;


                //To remove padding and display name if message is same and sent within 45 seconds of last message
                if(timeDif<30 && isMe){
                    layout.setLayoutParams(layoutParams );
                    viewHolder.message_user.setVisibility(View.GONE);
                    viewHolder.message_user.setText(model.getSender());
                    viewHolder.message_text.setText(model.getMessage());
                    viewHolder.message_date.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTime()));
                }
                else{
                    viewHolder.message_user.setVisibility(View.VISIBLE);
                    viewHolder.message_user.setText(model.getSender());
                    viewHolder.message_text.setText(model.getMessage());
                    viewHolder.message_date.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",model.getTime()));
                }
                Log.d(TAG,"2. UID is: "+model.getUID()+ " Current UID is: " + currentUID + " TimeDif is: " + timeDif);
            }
        };
        recyclerView.setAdapter(mAdapter);
        autoScroll();
    }

    private int timeInSeconds(CharSequence time) {
        return (Integer.parseInt(time.subSequence(0,2).toString()))*360 + (Integer.parseInt(time.subSequence(2,4).toString()))*60 + (Integer.parseInt(time.subSequence(4,6).toString()));
    }


    //*** NEED THESE ***
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            Intent launchNewIntent = new Intent(MainActivity.this,LoginActivity.class);
                            startActivityForResult(launchNewIntent, 0);
                        }
                    });
        }
        return true;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    public void autoScroll(){
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                // Call smooth scroll
                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            }
        });
    }

//Function to send a message also includes constructor to create message object
    public void send(View view){
        EditText input = (EditText)findViewById(R.id.input);
        if(!(input.getText().toString().equals(""))) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .push()
                    .setValue(new ChatMessage(input.getText().toString(),
                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                            currentUID)
                    );
        }
        // Clear the input
        input.setText("");
        autoScroll();
    }


    public static class PostViewHolder extends RecyclerView.ViewHolder{
        public TextView message_user, message_text, message_date;
        public LinearLayout chatBubble;

        public PostViewHolder(View itemView) {
            super(itemView);
            message_user = (TextView) itemView.findViewById(R.id.message_user);
            message_text = (TextView) itemView.findViewById(R.id.message_text);
            message_date = (TextView) itemView.findViewById(R.id.message_date);
            chatBubble = (LinearLayout) itemView.findViewById(R.id.chatBubble);
        }

        public void bindMessage(ChatMessage message) {
            message_user.setText(message.getMessage());
            message_text.setText(message.getSender());
            message_date.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getTime()));
        }
    }

}


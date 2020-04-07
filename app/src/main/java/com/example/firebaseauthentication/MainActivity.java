package com.example.firebaseauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.firebaseauthentication.auth.LoginActivity;
import com.example.firebaseauthentication.auth.RegisterActivity;
import com.example.firebaseauthentication.auth.Splash;
import com.example.firebaseauthentication.model.Adapter;
import com.example.firebaseauthentication.model.Note;
import com.example.firebaseauthentication.note.AddNote;
import com.example.firebaseauthentication.note.EditNote;
import com.example.firebaseauthentication.note.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    RecyclerView noteList;
    Adapter adapter;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ////////////////////////FIREBASE WORK///////////////////////////////////////////
        fStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        Query query = fStore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);
        // query notes > uuid > myNotes > all notes

        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final Note note) {
                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());
                final int code = getRandomColor();
                noteViewHolder.mCardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code, null));
                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), NoteDetails.class);
                        i.putExtra("title", note.getTitle());
                        i.putExtra("content", note.getContent());
                        i.putExtra("code", code);
                        i.putExtra("noteId", docId);
                        v.getContext().startActivity(i);
                    }
                });

                ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(), v);
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = new Intent(v.getContext(), EditNote.class);
                                intent.putExtra("title", note.getTitle());
                                intent.putExtra("content", note.getContent());
                                intent.putExtra("noteId", docId);
                                startActivity(intent);
                                return false;
                            }
                        });

                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference docRef = fStore.collection("notes").document(user.getUid()).collection("myNotes").document(docId);
                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Deleted succesfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error in deleting Note", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        menu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };


        ////////////////////////FIREBASE WORK///////////////////////////////////////////


        noteList = findViewById(R.id.notelist);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();


        noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteList.setAdapter(noteAdapter);


/////////////TO GET USERNAME AND EMAIL IN NAV HEADER///////////////////////////////
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userDisplayName);
        TextView useremail = headerView.findViewById(R.id.userDisplayEmail);
        if (user.isAnonymous()) {
            useremail.setVisibility(View.GONE);
            username.setText("Temporary User");
        } else {
            useremail.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }
//////////////////////////////////////////////////////////////////////////////////////////


        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), AddNote.class));
            }
        });


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.add_note:
                startActivity(new Intent(this, AddNote.class));
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                break;

            case R.id.sync:
                if (user.isAnonymous()) {
                    startActivity(new Intent(this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                    } else {
                    Toast.makeText(this, "You are connected", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.logout:
                checkUser();
                break;

            default:
                Toast.makeText(this, "Clicked ", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkUser() {
        // if user is real or not
        if (user.isAnonymous()) {
            displayAlert();
        }
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, Splash.class));
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you Sure? ")
                .setMessage("You are logged with Temporary account. Logging out will delete all data")
                .setPositiveButton("sync note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // delete all the data created by anynomimus user and user id

                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), Splash.class));
                                finish();
                            }
                        });

                    }
                });
        warning.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            Toast.makeText(this, "Setting Clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent;
        View view;
        CardView mCardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }

    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.notgreen);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.red);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);

    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }
}



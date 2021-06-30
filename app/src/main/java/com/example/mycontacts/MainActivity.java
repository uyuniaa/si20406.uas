package com.example.mycontacts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public ContactssAdapter mAdapter;
    public static final int CONTACTLOADER = 0;


    RecyclerView recyclerView;
    ArrayList<CONTACTS> mData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Read from the database

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("data").child("contacts");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String name = null;
                    String phone = null;
                    String email = null;
                    String key ;

                    key = dataSnapshot1.getKey();
                    name = dataSnapshot1.child("name").getValue().toString();
                    phone = dataSnapshot1.child("phone").getValue().toString()+"";
                    email = dataSnapshot1.child("email").getValue().toString();

                    mData.add(new CONTACTS(key,name, phone, email));

                }

                if (dataSnapshot.getChildrenCount() == mData.size()) {
                    setData(mData);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("scann", "Failed to read value.", error.toException());
            }
        });


    }

    void setData(ArrayList<CONTACTS> mData) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ContactssAdapter(this, mData);
        recyclerView.setAdapter(mAdapter);
    }


    public class ContactssAdapter extends RecyclerView.Adapter<ContactssAdapter.ViewHolder> {

        private ArrayList<CONTACTS> mData;
        private LayoutInflater mInflater;
        Context context;

        ContactssAdapter(Context context, ArrayList<CONTACTS> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.listitem, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String name = mData.get(position).getName();
            String phone = mData.get(position).getPhone();
            String email = mData.get(position).getEmail();
            holder.itemName.setText(name);
            holder.itemEmail.setText(email);
            holder.itemPhone.setText(phone);
            //we will set photo in last

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView itemName, itemPhone, itemEmail;
            Button delete;

            RelativeLayout relativeLayout;
            CircleImageView itemPhoto;

            ViewHolder(View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.textName);
                itemPhone = itemView.findViewById(R.id.textNumber);
                itemPhoto = itemView.findViewById(R.id.imageContact);
                delete = itemView.findViewById(R.id.delete);
                itemEmail = itemView.findViewById(R.id.textEmail);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Delete this contact");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked the "Delete" button, so delete the product.
                                String key = mData.get(getAdapterPosition()).getKey();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("data").child("contacts").child(key);
                                databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mData.remove(getAdapterPosition());
                                        mAdapter.notifyItemRemoved(getAdapterPosition());
                                        Toast.makeText(context,"Deleted successfuly",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        });


                        // Create and show the AlertDialog
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });


            }

        }

    }




}
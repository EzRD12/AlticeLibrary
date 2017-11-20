package com.example.ezrodriguez.bibliotecaaltice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ezrodriguez.bibliotecaaltice.entity.Book;
import com.example.ezrodriguez.bibliotecaaltice.entity.Favorite;
import com.example.ezrodriguez.bibliotecaaltice.entity.Purchase;
import com.example.ezrodriguez.bibliotecaaltice.entity.UserProfile;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final int GALLERY_INTENT = 1;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private FirebaseDatabase database;
    private StorageReference mStorage;
    private FirebaseUser user;
    private Button button_change_photo,cancel_edit;
    private DatabaseReference reference;
    private View viewProfile;
    private RecyclerView mRecyclerView_Profile;
    private BottomNavigationView mBottomNavigation;
    private CircleImageView pProfileImage;
    private ProgressDialog mProgressDialog;
    private ImageView edit_profile;


    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewProfile = inflater.inflate(R.layout.fragment_profile, container, false);

        mRecyclerView_Profile = viewProfile.findViewById(R.id.myRecyclerView_Profile);
        mBottomNavigation = viewProfile.findViewById(R.id.bar_profile);
        button_change_photo = viewProfile.findViewById(R.id.button_change_photo);
        edit_profile = viewProfile.findViewById(R.id.edit_profile);
        cancel_edit = viewProfile.findViewById(R.id.button_cancel_edit);


        mStorage = FirebaseStorage.getInstance().getReference();
        pProfileImage = viewProfile.findViewById(R.id.profile_photo);

        mProgressDialog = new ProgressDialog(getContext());

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_change_photo.setVisibility(View.VISIBLE);
                cancel_edit.setVisibility(View.VISIBLE);

            }
        });
        cancel_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_change_photo.setVisibility(View.GONE);
                cancel_edit.setVisibility(View.GONE);
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user = firebaseAuth.getCurrentUser();
            }
        };
        List<Book> lista = new ArrayList<>();
        getBooksFav();
        Book book1 = new Book();
        book1.setAutor("UN LOCO");
        book1.setResumen("PRUEBA ESTO");
        book1.setBody("PRUEBA ESTO");
        book1.setTitle("TITULO DE PRUEBA");
        book1.setUrl("https://upload.wikimedia.org/wikipedia/en/f/fd/Davincicodesoundtrack.jpg");
        book1.setRental(210);
        book1.setPrice(6500);
        book1.setQuantity(4);
        book1.setQuantitySales(60);
        book1.setCategory("2");
        lista.add(book1);


        mRecyclerView_Profile.setAdapter(new myRecyclerViewAdapter(lista));
        getBooksPurchased();

        mBottomNavigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_fav:
                        getBooksFav();
                        break;
                    default:
                        Toast.makeText(getContext(),"Accion en proceso de creacion",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        button_change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alert = new AlertDialog.Builder(getContext())
                        .setTitle("Cambiar foto de perfil")
                        .setMessage("Elija la fuente de la fotografia")
                        .setPositiveButton("CAMARA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {



                            }
                        })
                        .setNegativeButton("GALERIA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent, GALLERY_INTENT);
                            }
                        })
                        .create();
                alert.show();
            }
        });

        return viewProfile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            Uri uri = data.getData();
            mProgressDialog.setTitle("Subiendo...");
            mProgressDialog.setMessage("Subiendo foto a servidor");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            StorageReference filePath = mStorage.child("user_photo").child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final DatabaseReference myRef = FirebaseDatabase.getInstance()
                            .getReference().child("userProfile");
                    myRef.child(user.getUid()).child("url_photo").setValue(taskSnapshot.getDownloadUrl().toString());
                    Glide.with(getContext())
                            .load(taskSnapshot.getDownloadUrl())
                            .into(pProfileImage);
                    mProgressDialog.dismiss();
                    Toast.makeText(getContext(),"Se cargo su nueva foto con exito",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getBooksPurchased(){
        user = mFirebaseAuth.getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference()
                .child("purchase").orderByChild("user_key").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children =
                        dataSnapshot.getChildren();
                Purchase purchase;
                final List<Purchase> list = new ArrayList<>();
                for (DataSnapshot child : children) {
                    purchase = child.getValue(Purchase.class);
                    list.add(purchase);

                }
                if (!children.equals(null)) {
                    reference = FirebaseDatabase.getInstance().getReference("books");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            Iterable<DataSnapshot> children =
                                    dataSnapshot.getChildren();
                            Book book;
                            List<Book> lista = new ArrayList<>();
                            for (DataSnapshot child : children) {
                                book = child.getValue(Book.class);
                                for (Purchase purchase1 : list) {
                                    if (book.getTitle().equals(purchase1.getBook_title())) {
                                        lista.add(book);

                                    }

                                }
                            }
                            Book book1 = new Book();
                            book1.setAutor("UN LOCO");
                            book1.setResumen("PRUEBA ESTO");
                            book1.setBody("PRUEBA ESTO");
                            book1.setTitle("TITULO DE PRUEBA");
                            book1.setUrl("https://upload.wikimedia.org/wikipedia/en/f/fd/Davincicodesoundtrack.jpg");
                            book1.setRental(210);
                            book1.setPrice(6500);
                            book1.setQuantity(4);
                            book1.setQuantitySales(60);
                            book1.setCategory("2");
                            lista.add(book1);

                            mRecyclerView_Profile.setAdapter(new myRecyclerViewAdapter(lista));
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Toast.makeText(getContext()
                                    , "Failed to read list of books."
                                    , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getBooksFav(){
        reference = FirebaseDatabase.getInstance().getReference("books");
        user = mFirebaseAuth.getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("favorites").orderByChild("user_key").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children =
                        dataSnapshot.getChildren();
                Favorite favorite;
                final List<Favorite> list = new ArrayList<>();
                for (DataSnapshot child : children) {
                    favorite = child.getValue(Favorite.class);
                    list.add(favorite);

                }
                if (!children.equals(null)){
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            Iterable<DataSnapshot> children =
                                    dataSnapshot.getChildren();
                            Book book;
                            List<Book> lista = new ArrayList<>();
                            for (DataSnapshot child : children) {
                                book = child.getValue(Book.class);
                                for (Favorite fav : list) {
                                    if(book.getTitle().equals(fav.getBook_title())){
                                        lista.add(book);

                                    }

                                }
                            }
                            Book book1 = new Book();
                            book1.setAutor("UN LOCO");
                            book1.setResumen("PRUEBA ESTO");
                            book1.setBody("PRUEBA ESTO");
                            book1.setTitle("TITULO DE PRUEBA");
                            book1.setUrl("https://upload.wikimedia.org/wikipedia/en/f/fd/Davincicodesoundtrack.jpg");
                            book1.setRental(210);
                            book1.setPrice(6500);
                            book1.setQuantity(4);
                            book1.setQuantitySales(60);
                            book1.setCategory("2");
                            lista.add(book1);

                            mRecyclerView_Profile.setAdapter(new myRecyclerViewAdapter(lista));
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Toast.makeText(getContext()
                                    ,"Failed to read list of books."
                                    ,Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();
        String[] data = bundle.getStringArray("UserData");
        setUserData(data);
        if(mBottomNavigation.getSelectedItemId() == R.id.item_fav ){
            getBooksFav();
        }

    }

    private void setUserData(String[] userData) {
        final TextView pUserName = viewProfile.findViewById(R.id.profile_name);
        TextView pUserMail = viewProfile.findViewById(R.id.profile_email);

        pUserName.setText(userData[0]);
        pUserMail.setText(userData[1]);
        Glide.with(getContext())
                .load(userData[2])
                .into(pProfileImage);

    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class myRecyclerViewAdapter extends RecyclerView.Adapter<myRecyclerViewHolder>{
        private final List<Book> items;

        private myRecyclerViewAdapter(List<Book> items) {
            super();
            this.items = items;
        }

        @Override
        public myRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.book_item_view, parent, false);
            myRecyclerViewHolder holder = new myRecyclerViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(myRecyclerViewHolder holder, int position) {
            final Book item = this.items.get(position);
            holder.title.setText(item.getTitle());
            holder.autor.setText(item.getAutor());
            holder.resumen.setText(item.getResumen());
            Glide.with(holder.itemView.getContext())
                    .load(item.getUrl())
                    .into(holder.portada);

            holder.contenido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] dataBook = new String[]{item.getAutor()
                            , item.getBody()
                            , item.getTitle()
                            , item.getUrl()
                            ,String.valueOf(item.getQuantitySales())
                            ,String.valueOf(item.getQuantity())
                            ,item.getKey()};
                    long[] priceBook = new long[]{item.getPrice()
                            , item.getRental()};
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("dataBook",dataBook);
                    bundle.putLongArray("pricesBook",priceBook);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    BookFragment bookFragment = new BookFragment();
                    bookFragment.setArguments(bundle);


                    fragmentTransaction.replace(R.id.home_fragment,bookFragment)
                            .addToBackStack(null)
                            .commit();

                }
            });

        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }
    }

    private class myRecyclerViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView autor;
        private final TextView resumen;
        private final ImageView portada;
        private final LinearLayout contenido;


        public myRecyclerViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.book_title);
            autor = itemView.findViewById(R.id.book_autor);
            resumen = itemView.findViewById(R.id.book_resume);
            portada = itemView.findViewById(R.id.book_portada);
            contenido = itemView.findViewById(R.id.book_content);

        }
    }
}

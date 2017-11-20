package com.example.ezrodriguez.bibliotecaaltice;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ezrodriguez.bibliotecaaltice.entity.Book;
import com.example.ezrodriguez.bibliotecaaltice.entity.Favorite;
import com.example.ezrodriguez.bibliotecaaltice.entity.Purchase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView title, autor, price, rental, body;
    private ImageView portada;
    private Button purchase, rent;
    private FloatingActionButton button_fav;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private String[] dataBook;
    private DatabaseReference reference;
    private Bundle bundle;


    boolean hasFav = false;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookFragment.
     */
    public static BookFragment newInstance(String param1, String param2) {
        BookFragment fragment = new BookFragment();
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
    public void onStart() {
        super.onStart();
        final String titulo = title.getText().toString();
        user = mFirebaseAuth.getCurrentUser();


        Query query = FirebaseDatabase.getInstance().getReference()
                .child("favorites").orderByChild("user_key").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children =
                        dataSnapshot.getChildren();
                Favorite favoriteGet;
                for (DataSnapshot child : children) {
                    favoriteGet = child.getValue(Favorite.class);
                    if(favoriteGet.getBook_title().equals(titulo)) {
                        hasFav = true;
                        button_fav.setImageResource(R.mipmap.ic_favorite_book);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference().child("books");
        bookRef.keepSynced(true);


        mFirebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user = firebaseAuth.getCurrentUser();
            }
        };

        title = view.findViewById(R.id.book_title_detail);
        body = view.findViewById(R.id.book_body_detail);
        autor = view.findViewById(R.id.book_autor_detail);
        price = view.findViewById(R.id.book_price_detail);
        rental = view.findViewById(R.id.book_rental_detail);
        portada = view.findViewById(R.id.book_portada_detail);
        button_fav = view.findViewById(R.id.book_fav);
        purchase = view.findViewById(R.id.button_purchase);
        rent = view.findViewById(R.id.button_rent);

        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alert = new AlertDialog.Builder(getContext())
                        .setTitle("COMPRA")
                        .setMessage("¿Esta seguro de realizar su compra?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(!dataBook[4].equals("0")) {

                                    Query query = FirebaseDatabase.getInstance().getReference()
                                            .child("books")
                                            .orderByChild("title")
                                            .equalTo(title.getText().toString());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Iterable<DataSnapshot> children =
                                                    dataSnapshot.getChildren();
                                            Book book;
                                            for (DataSnapshot child : children) {
                                                book = child.getValue(Book.class);
                                                book.setQuantitySales(Integer.parseInt(dataBook[4]) - 1);
                                                reference = FirebaseDatabase.getInstance().getReference("books");
                                                reference.child(dataBook[6]).setValue(book);
                                                dataBook[4] = String.valueOf(Integer.parseInt(dataBook[4]) - 1);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    user = mFirebaseAuth.getCurrentUser();
                                    Purchase purchase = new Purchase();
                                    purchase.setBook_title(title.getText().toString());
                                    purchase.setBook_autor(autor.getText().toString());
                                    purchase.setUser_key(user.getUid());

                                    reference = FirebaseDatabase.getInstance().getReference("purchase");
                                    String key = reference.push().getKey();
                                    reference.child(key).setValue(purchase);

                                    AlertDialog alert = new AlertDialog.Builder(getContext())
                                            .setTitle("Confirmacion de compra")
                                            .setMessage("Felicidades, tu compra fue un exito, para retirar el producto pase por el " +
                                                    "area de ventas de la Biblioteca Altice y presente su codigo de compra: " +
                                                    key)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) { }
                                            })
                                            .create();
                                    alert.show();

                                }else{
                                    Snackbar.make(getView(), "Lo sentimos, todos los libros de "+ dataBook[2]+ " se han agotado por el momento," +
                                            "intente solicitar su alquiler por el tiempo que guste", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }

                            }
                        })
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) { }
                        })
                        .create();
                alert.show();
            }
        });

        button_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference myRef = database.getReference().child("favorites");
                final String[] id = {""};

                    user = mFirebaseAuth.getCurrentUser();

                    Query query = FirebaseDatabase.getInstance().getReference()
                            .child("favorites").orderByChild("user_key").equalTo(user.getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> children =
                                    dataSnapshot.getChildren();
                            Favorite favoriteGet;
                            String titulo = title.getText().toString();
                            Favorite favorite = new Favorite();
                            List<Favorite> list = new ArrayList<>();
                            for (DataSnapshot child : children) {
                                favoriteGet = child.getValue(Favorite.class);

                                if(favoriteGet.getBook_title().equals(titulo)) {
                                    list.add(favoriteGet);
                                    button_fav.setImageResource(R.mipmap.ic_unfavorite);
                                    favorite.setBook_title(titulo);
                                    favorite.setUser_key(user.getUid());
                                    myRef.child(child.getKey()).setValue(null);
                                    hasFav = true;
                                    break;
                                }else{
                                    hasFav = false;
                                }
                            }
                            if(!hasFav) {
                                // Write a message to the database
                                button_fav.setImageResource(R.mipmap.ic_favorite_book);
                                favorite.setBook_title(titulo);
                                favorite.setUser_key(user.getUid());

                                myRef.push().setValue(favorite);
                                hasFav = !hasFav;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            }
        });
        Bundle bundle = getArguments();
        dataBook = bundle.getStringArray("dataBook");
        long[] priceBook = bundle.getLongArray("pricesBook");

        setDataBook(dataBook,priceBook);

        return view;

    }

    private void setDataBook(String[] dataBook, long[] priceBook) {
        autor.setText(dataBook[0]);
        body.setText(dataBook[1]);
        title.setText(dataBook[2]);
        price.setText(new StringBuilder().append(String.valueOf(priceBook[0])).append(" RD$").toString());
        rental.setText(String.valueOf(priceBook[1])+ " RD$");

        Glide.with(getContext())
                .load(dataBook[3])
                .into(portada);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
}

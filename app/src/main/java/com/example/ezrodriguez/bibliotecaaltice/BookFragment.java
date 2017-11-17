package com.example.ezrodriguez.bibliotecaaltice;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ezrodriguez.bibliotecaaltice.entity.Book;
import com.example.ezrodriguez.bibliotecaaltice.entity.Favorite;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView title, autor, price, rental, body;
    private ImageView portada;
    private FloatingActionButton button_fav;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private FirebaseUser user;
    boolean hasFav = false;

    // TODO: Rename and change types of parameters
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
    // TODO: Rename and change types and number of parameters
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
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

        button_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasFav) {
                    button_fav.setImageResource(R.mipmap.ic_favorite_book);

                    final String titulo = title.getText().toString();

                    Query query = FirebaseDatabase.getInstance().getReference("favorites")
                            .child("uiIdUser").equalTo(user.getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> children =
                                    dataSnapshot.getChildren();
                            Favorite favoriteGet;
                            List<Favorite> list = new ArrayList<>();
                            for (DataSnapshot child : children) {
                                favoriteGet = child.getValue(Favorite.class);
                                if(favoriteGet.getBook_title() == titulo)
                                    list.add(favoriteGet);
                            }
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("favorites");
                            if(list.isEmpty()){
                                // Write a message to the database
                                user = mFirebaseAuth.getCurrentUser();
                                Favorite favorite = new Favorite();
                                favorite.setBook_title(titulo);
                                favorite.setUser_key(user.getUid());

                                myRef.push().setValue(favorite);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else { button_fav.setImageResource(R.mipmap.ic_unfavorite); }

                hasFav = !hasFav;
            }
        });

        Bundle bundle = getArguments();
        String[] dataBook = bundle.getStringArray("dataBook");
        long[] priceBook = bundle.getLongArray("pricesBook");

        setDataBook(dataBook,priceBook);

        return view;

    }

    private void setDataBook(String[] dataBook, long[] priceBook) {
        autor.setText(dataBook[0]);
        body.setText(dataBook[1]);
        title.setText(dataBook[2]);
        price.setText(String.valueOf(priceBook[0])+ " RD$");
        rental.setText(String.valueOf(priceBook[1])+ " RD$");

        Glide.with(getContext())
                .load(dataBook[3])
                .into(portada);
    }


    // TODO: Rename method, update argument and hook method into UI event
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

package com.example.ezrodriguez.bibliotecaaltice;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ezrodriguez.bibliotecaaltice.entity.Book;
import com.example.ezrodriguez.bibliotecaaltice.entity.UserProfile;
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
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView mRecyclerView;
    private FloatingActionButton buttonToAdd;


    private OnFragmentInteractionListener mListener;
    private DatabaseReference reference;
    private FirebaseAuth mFirebaseAuth;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView =  view.findViewById(R.id.home_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(super.getContext()
                ,LinearLayoutManager.VERTICAL,false));
        //buttonToAdd = view.findViewById(R.id.button_add_books);

        reference = FirebaseDatabase.getInstance().getReference("books");

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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


    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference referenceCatalog = FirebaseDatabase.getInstance().getReference();
        Bundle bundle = getArguments();

        if(bundle.getString("Catalog") != null){
    //        buttonToAdd.setVisibility(View.VISIBLE);
            Query query = referenceCatalog.child("books")
                    .orderByChild("category")
                    .equalTo(bundle.getString("position"));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children =
                            dataSnapshot.getChildren();
                    Book book;
                    List<Book> list = new ArrayList<>();
                    for (DataSnapshot child : children) {
                        book = child.getValue(Book.class);
                        list.add(book);
                    }
                    mRecyclerView.setAdapter(new myRecyclerViewAdapter(list));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext()
                            ,"Failed to read list of books."
                            ,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
//            buttonToAdd.setVisibility(View.GONE);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Iterable<DataSnapshot> children =
                            dataSnapshot.getChildren();
                    Book book;
                    List<Book> list = new ArrayList<>();
                    for (DataSnapshot child : children) {
                        book = child.getValue(Book.class);
                        list.add(book);
                    }
                    mRecyclerView.setAdapter(new myRecyclerViewAdapter(list));
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

        // Read from the database
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
                                                , item.getUrl()};
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


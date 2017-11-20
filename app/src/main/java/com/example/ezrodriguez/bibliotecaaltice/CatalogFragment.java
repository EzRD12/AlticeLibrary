package com.example.ezrodriguez.bibliotecaaltice;

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

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CatalogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CatalogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CatalogFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;


    private OnFragmentInteractionListener mListener;

    public CatalogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CatalogFragment.
     */
    public static CatalogFragment newInstance(String param1, String param2) {
        CatalogFragment fragment = new CatalogFragment();
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
        View view=  inflater.inflate(R.layout.fragment_catalog, container, false);
        mRecyclerView =  view.findViewById(R.id.myRecyclerView_Catalog);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(super.getContext()
                ,LinearLayoutManager.VERTICAL,false));

        List<String> listSection = Arrays.asList("Fantasía","Romance","Terror","Acción", "Comedia"
                , "Misterio", "Horror", "Ciencia ficción"
                        );
        mRecyclerView.setAdapter(new myRecyclerViewAdapter(listSection));

        return view;

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

    private class myRecyclerViewAdapter extends RecyclerView.Adapter<CatalogFragment.myRecyclerViewHolder>{
        private final List<String> items;

        private myRecyclerViewAdapter(List<String> items) {
            super();
            this.items = items;
        }

        @Override
        public CatalogFragment.myRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_categories, parent, false);
            CatalogFragment.myRecyclerViewHolder holder = new CatalogFragment.myRecyclerViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(CatalogFragment.myRecyclerViewHolder holder, final int position) {
            List<String> listImage = Arrays.asList("https://images5.alphacoders.com/637/thumb-1920-637668.jpg"
                    ,"https://www.collegefashion.net/.image/t_share/MTQwMTA5NDYwNTIwNTc2NTMw/mebeforeyoujpg.jpg"
                    ,"https://k60.kn3.net/taringa/E/C/6/6/D/1/avbocatus/4EA.jpg"
                    ,"http://www.dailytrend.mx/media/bi/mediabrowser/2016/12/peliculas-que-se-estrenan-en-2017-4.jpg"
                    , "http://www.animalpolitico.com/wp-content/uploads/2017/05/como-ser-un-latin-lover-960x500.jpg"
                    , "https://cdn.hobbyconsolas.com/sites/navi.axelspringer.es/public/media/image/2016/03/577746-15-mejores-peliculas-suspense-misterio.jpg"
                    , "http://cdn.20m.es/img2/recortes/2017/06/22/496910-944-531.jpg"
                    , "http://polimates.org/wp-content/uploads/2017/07/Teoria-del-dise%C3%B1o-inteligente.jpg"
            );
            final String item = this.items.get(position);
            holder.section_name.setText(item);
            Glide.with(holder.itemView.getContext())
                    .load(listImage.get(position))
                    .into(holder.section_image);

            holder.category_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("Catalog","I Call you");
                    bundle.putString("position",String.valueOf(position +1));
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    HomeFragment homeFragment = new HomeFragment();
                    homeFragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.home_fragment,homeFragment)
                            .addToBackStack(null)
                            .commit();


                }
            });
            holder.button_add_book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }
    }

    private class myRecyclerViewHolder extends RecyclerView.ViewHolder {

        private final TextView section_name;
        private final ImageView section_image;
        private final LinearLayout category_content;
        private final FloatingActionButton button_add_book;


        public myRecyclerViewHolder(View itemView) {
            super(itemView);
            section_name = itemView.findViewById(R.id.section_categorie);
            section_image = itemView.findViewById(R.id.image_categorie);
            category_content = itemView.findViewById(R.id.category_content);
            button_add_book = itemView.findViewById(R.id.button_add_book);


        }
    }

}

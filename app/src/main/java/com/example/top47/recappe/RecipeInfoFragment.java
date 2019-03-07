package com.example.top47.recappe;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipeInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String name;
    private String ingredients;
    private String preparation_method;
    private String notes;
    private String image_path;

    private OnFragmentInteractionListener mListener;

    public RecipeInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeInfoFragment newInstance(String param1, String param2) {
        RecipeInfoFragment fragment = new RecipeInfoFragment();
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
            name = getArguments().getString("name");
            ingredients = getArguments().getString("ingredients");
            preparation_method = getArguments().getString("preparation_method");
            notes = getArguments().getString("notes");

            image_path = getArguments().getString("image_path");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_recipe_info, container, false);
        View recipeInfoView =  inflater.inflate(R.layout.fragment_recipe_info, container, false);
        TextView recipeNameTextView = (TextView) recipeInfoView.findViewById(R.id.recipeNameTextView);
        TextView recipeIngredientsTextView = (TextView) recipeInfoView.findViewById(R.id.ingredientsTextView);
        TextView recipePreparationMethodTextView = (TextView) recipeInfoView.findViewById(R.id.preperationMethodTextView);
        TextView recipeNotesTextView = (TextView) recipeInfoView.findViewById(R.id.notesTextView);

        recipeNameTextView.setText(name);
        recipeIngredientsTextView.setText(ingredients);
        recipePreparationMethodTextView.setText(preparation_method);
        recipeNotesTextView.setText(notes);





        File imgFile = new File(image_path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            ImageView myImage = (ImageView) recipeInfoView.findViewById(R.id.recipeImage);
//            myImage.setImageBitmap(myBitmap);
            PhotoView photoView = (PhotoView) recipeInfoView.findViewById(R.id.recipeImage);
            photoView.setImageBitmap(myBitmap);

        }
        return recipeInfoView;
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

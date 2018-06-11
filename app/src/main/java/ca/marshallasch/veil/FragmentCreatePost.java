package ca.marshallasch.veil;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-11
 */
public class FragmentCreatePost extends Fragment
{
    public FragmentCreatePost() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_create_post, container,false);


        return view;
    }
}

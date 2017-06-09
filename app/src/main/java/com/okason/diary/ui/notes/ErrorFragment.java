package com.okason.diary.ui.notes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.okason.diary.R;
import com.okason.diary.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorFragment extends Fragment {

    @BindView(R.id.error_textView)
    TextView errorText;



    public ErrorFragment() {
        // Required empty public constructor
    }

    public static ErrorFragment newInstance(String errorMessage){
        ErrorFragment fragment = new ErrorFragment();
        if (!TextUtils.isEmpty(errorMessage)){
            Bundle bundle = new Bundle();
            bundle.putString(Constants.ERROR_MESSAGE, errorMessage);
            fragment.setArguments(bundle);
        }

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_error, container, false);
        ButterKnife.bind(this, rootView);
        if (getArguments() != null && getArguments().containsKey(Constants.ERROR_MESSAGE)){
            String errorMessage = getArguments().getString(Constants.ERROR_MESSAGE);
            errorText.setText(errorMessage);
        }
        return rootView;
    }

}

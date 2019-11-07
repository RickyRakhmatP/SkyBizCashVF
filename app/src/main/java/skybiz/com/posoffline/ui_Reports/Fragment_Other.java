package skybiz.com.posoffline.ui_Reports;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import skybiz.com.posoffline.R;

/**
 * Created by 7 on 13/12/2017.
 */

public class Fragment_Other extends Fragment {
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_other, container, false);
        return view;
    }
}

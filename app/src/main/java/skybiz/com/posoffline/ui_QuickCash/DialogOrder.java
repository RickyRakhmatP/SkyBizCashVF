package skybiz.com.posoffline.ui_QuickCash;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_QuickCash.m_OrderQuick.DownloaderOrder;


/**
 * Created by 7 on 17/01/2018.
 */

public class DialogOrder extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rvOrder;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_order, container, false);
        rvOrder=(RecyclerView) view.findViewById(R.id.rvOrder);
        getDialog().setTitle("List of Order");
        refresh("");
        return view;
    }

    public void refresh(String Keyword) {
        rvOrder.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvOrder.setLayoutManager(lLayout);
        rvOrder.setItemAnimator(new DefaultItemAnimator());
        DownloaderOrder downloaderOrder=new DownloaderOrder(getActivity(), rvOrder,
                getActivity().getSupportFragmentManager());
        downloaderOrder.execute();
    }

}

package skybiz.com.posoffline.ui_CreditNote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewItem.DownloaderItem;
import skybiz.com.posoffline.m_NewItemGroup.DownloaderGroup;
import skybiz.com.posoffline.m_NewMisc.DownloaderMisc;


public class Fragment_Items extends Fragment {
    View view;
    RecyclerView rvG,rv;
    private GridLayoutManager lLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentcn_items, container, false);

        rvG=(RecyclerView) view.findViewById(R.id.list_group);
        rv=(RecyclerView) view.findViewById(R.id.rec_list);
        retItemGroup();
        retItem("");
        return view;
    }
    public void retItem(String ItemGroup) {
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 2);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        if(ItemGroup.equals("Miscellaneous")) {
            DownloaderMisc dItem = new DownloaderMisc(getActivity(), "CusCN", rv);
            dItem.execute();
        }else{
            DownloaderItem dItem = new DownloaderItem(getActivity(), "CusCN", ItemGroup, rv);
            dItem.execute();
        }
    }
    public void retItemGroup() {
        rvG.setHasFixedSize(true);
        rvG.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvG.setItemAnimator(new DefaultItemAnimator());
        DownloaderGroup dGroup = new DownloaderGroup(getActivity(),"CusCN", rvG);
        dGroup.execute();
    }
}

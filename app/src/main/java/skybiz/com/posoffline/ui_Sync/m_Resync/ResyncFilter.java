package skybiz.com.posoffline.ui_Sync.m_Resync;

import android.widget.Filter;

import java.util.ArrayList;

import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;

/**
 * Created by 7 on 02/01/2018.
 */

public class ResyncFilter extends Filter {
    ResyncAdapter adapter;
    ArrayList<Spacecraft_Trn> filterList;
    public ResyncFilter(ArrayList<Spacecraft_Trn> filterList, ResyncAdapter adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;
    }
    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<Spacecraft_Trn> filteredPlayers=new ArrayList<>();
            for (int i=0;i<filterList.size();i++)
            {
                //CHECK
                if(filterList.get(i).getDoc1No().toUpperCase().contains(constraint))
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(filterList.get(i));
                }
            }
            results.count=filteredPlayers.size();
            results.values=filteredPlayers;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.spacecrafts= (ArrayList<Spacecraft_Trn>) results.values;
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}

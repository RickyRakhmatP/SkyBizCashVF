package skybiz.com.posoffline.ui_SalesOrder.m_Item;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import skybiz.com.posoffline.R;

/**
 * Created by 7 on 27/10/2017.
 */

public class PicassoClient {
    public static void downloadImage(Context c, String PhotoFile, ImageView img) {
        if(PhotoFile.length() > 0 && PhotoFile!= null){
            Picasso.with(c).load(PhotoFile).placeholder(R.drawable.placeholder);
        }else{
            Picasso.with(c).load(R.drawable.placeholder).into(img);
        }
    }
}

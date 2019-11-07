package skybiz.com.posoffline.ui_Member.m_PointLedger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewCustomer.DialogCustomer;

public class HistoryPoint extends AppCompatActivity {

    private GridLayoutManager lLayout;
    RecyclerView rv;
    TextView txtDate,txtDescPoint,txtPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_point2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Point Ledger");
        rv=(RecyclerView)findViewById(R.id.rvHistoryPoint);
        txtDate=(TextView) findViewById(R.id.txtDate);
        txtDescPoint=(TextView) findViewById(R.id.txtDescPoint);
        txtPoint=(TextView) findViewById(R.id.txtPoint);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.mnCustomer) {
            Bundle b=new Bundle();
            b.putString("DOCTYPE_KEY","HistoryPoint");
            DialogCustomer dialogCustomer = new DialogCustomer();
            dialogCustomer.setArguments(b);
            dialogCustomer.show(getSupportFragmentManager(), "List Customer");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void retHistory(String CusCode, String CusName){
        getSupportActionBar().setTitle(CusName);
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloadHistory downloadHistory=new DownloadHistory(this,CusCode,rv);
        downloadHistory.execute();
    }
    public void setHeader(String CusCode,String CurCode, String TotalPoint){
        txtPoint.setText(TotalPoint+"P");
        txtDescPoint.setText("Total Points Available :");
        txtDate.setText(datedNow());
    }

    private String datedNow(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String D_ateTime = sdf.format(date);
        return D_ateTime;
    }
}

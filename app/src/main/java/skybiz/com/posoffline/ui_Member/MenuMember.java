package skybiz.com.posoffline.ui_Member;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.MainActivity;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_Member.m_MemberList.MemberList;
import skybiz.com.posoffline.ui_Member.m_PointLedger.HistoryPoint;
import skybiz.com.posoffline.ui_Member.m_PointRedemption.PointRedeem;
import skybiz.com.posoffline.ui_Reports.Reports;

public class MenuMember extends AppCompatActivity {

    TextView txtPointRedeem,vPointRedeem,txtPointLedger,
            vPointLedger,txtMemberList,vMemberList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_member);
        getSupportActionBar().setTitle("Member");
        txtPointRedeem=(TextView)findViewById(R.id.txtPointRedeem);
        vPointRedeem=(TextView)findViewById(R.id.vPointRedeem);
        txtPointLedger=(TextView)findViewById(R.id.txtPointLedger);
        vPointLedger=(TextView)findViewById(R.id.vPointLedger);
        txtMemberList=(TextView)findViewById(R.id.txtMemberList);
        vMemberList=(TextView)findViewById(R.id.vMemberList);

        txtPointLedger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MenuMember.this, HistoryPoint.class);
                startActivity(mainIntent);
            }
        });
        vPointLedger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MenuMember.this, HistoryPoint.class);
                startActivity(mainIntent);
            }
        });

        txtPointRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MenuMember.this, PointRedeem.class);
                startActivity(mainIntent);
            }
        });
        vPointRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MenuMember.this, PointRedeem.class);
                startActivity(mainIntent);
            }
        });

        txtMemberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MenuMember.this, MemberList.class);
                startActivity(mainIntent);
            }
        });
        vMemberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MenuMember.this, MemberList.class);
                startActivity(mainIntent);
            }
        });
    }
}

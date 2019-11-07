package skybiz.com.posoffline;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import skybiz.com.posoffline.m_Backup.BackupDB;
import skybiz.com.posoffline.m_Connection.ConnectivityReceiver;
import skybiz.com.posoffline.m_ServiceSync.ServiceSync;
import skybiz.com.posoffline.m_ServiceSync.ServiceSyncIN;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CreditNote.CreditNote;
import skybiz.com.posoffline.ui_Dashboard.Dashboard;
import skybiz.com.posoffline.ui_Item.ItemListing;
import skybiz.com.posoffline.ui_ItemGroup.ItemGroupList;
import skybiz.com.posoffline.ui_Listing.Listing;
import skybiz.com.posoffline.ui_Member.MenuMember;
import skybiz.com.posoffline.ui_QuickCash.QuickCash;
import skybiz.com.posoffline.ui_RegisterNFC.IssueGiftCard;
import skybiz.com.posoffline.ui_RegisterNFC.RegisterNFC;
import skybiz.com.posoffline.ui_Reports.Reports;
import skybiz.com.posoffline.ui_SalesOrder.SalesOrder;
import skybiz.com.posoffline.ui_Service.MService;
import skybiz.com.posoffline.ui_Setting.Setting;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;
import skybiz.com.posoffline.ui_Sync.Sync;
import skybiz.com.posoffline.ui_TopUpNFC.TopUpNFC;
import skybiz.com.posoffline.ui_UpdateNFC.UpdateNFC;

public class MainActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {

    Button btnCashReceipt,btnReport,btnSetting,
            btnSync,btnSalesOrder,btnCreditNote,
            btnListing,btnService,btnMember,
            btnItem,btnItemGroup,btnDashboard,
            btnUpdateNFC,btnRegisterNFC,btnTopUpNFC,
            btnQuickCash;
    boolean doubleBackToExitPressedOnce = false;
    String IPAddress,ConnYN;

    public static final int R_PERM = 2822;
    private static final int REQUEST= 112;
    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.app_version);

        btnCashReceipt=(Button)findViewById(R.id.btnCashReceipt);
        btnReport=(Button)findViewById(R.id.btnReport);
        btnSetting=(Button)findViewById(R.id.btnSetting);
        btnSync=(Button)findViewById(R.id.btnSync);
        btnSalesOrder=(Button)findViewById(R.id.btnSalesOrder);
        btnCreditNote=(Button)findViewById(R.id.btnCreditNote);
        //btnListing=(Button)findViewById(R.id.btnListing);
        btnService=(Button)findViewById(R.id.btnService);
        btnMember=(Button)findViewById(R.id.btnMember);
        btnItem=(Button)findViewById(R.id.btnItem);
        btnItemGroup=(Button)findViewById(R.id.btnItemGroup);
        btnDashboard=(Button)findViewById(R.id.btnDashboard);
        btnUpdateNFC=(Button)findViewById(R.id.btnUpdateNFC);
        btnRegisterNFC=(Button)findViewById(R.id.btnRegisterNFC);
        btnTopUpNFC=(Button)findViewById(R.id.btnTopUpNFC);
        btnQuickCash=(Button)findViewById(R.id.btnQuickCash);

        btnCashReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // didTapButton(btnCashReceipt);
                Intent mainIntent = new Intent(MainActivity.this, CashReceipt.class);
                startActivity(mainIntent);
            }
        });
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, Listing.class);
                startActivity(mainIntent);
               // Intent mainIntent = new Intent(MainActivity.this, Reports.class);
                //startActivity(mainIntent);
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, Setting.class);
                startActivity(mainIntent);
            }
        });
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, Sync.class);
                startActivity(mainIntent);
            }
        });
        btnSalesOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, SalesOrder.class);
                startActivity(mainIntent);
            }
        });
        btnCreditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, CreditNote.class);
                startActivity(mainIntent);
            }
        });
       /* btnListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, Listing.class);
                startActivity(mainIntent);
            }
        });*/
        btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, MService.class);
                startActivity(mainIntent);
            }
        });
        btnMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, MenuMember.class);
                startActivity(mainIntent);
            }
        });
        btnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, ItemListing.class);
                startActivity(mainIntent);
            }
        });
        btnItemGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, ItemGroupList.class);
                startActivity(mainIntent);
            }
        });
        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, Dashboard.class);
                startActivity(mainIntent);
            }
        });

        btnTopUpNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, TopUpNFC.class);
                startActivity(mainIntent);
                //fnupdatenfc();
            }
        });

        btnRegisterNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, IssueGiftCard.class);
                startActivity(mainIntent);
                //fnupdatenfc();
            }
        });

        btnQuickCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, QuickCash.class);
                startActivity(mainIntent);
            }
        });

        btnUpdateNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, UpdateNFC.class);
                startActivity(mainIntent);
                //fnupdatenfc();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        }
        /*if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            }, 1);

        }*/
       /* if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG","@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
                    android. Manifest.permission.NFC,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };


            if (!hasPermissions(mContext, PERMISSIONS)) {
                Log.d("TAG","@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                Log.d("TAG","@@@ IN ELSE hasPermissions");
                callNextActivity();
            }
        } else {
            Log.d("TAG","@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            callNextActivity();
        }*/
        //checkAlter();
        checkConnection();
        initButton();
        fnchecksetting();
        checkAlter();
        startService(new Intent(this, ServiceSync.class));
        startService(new Intent(this, ServiceSyncIN.class));
    }

    private void fnupdatenfc(){

    }
    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int wtite           = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read            = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int readphone       = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int bluetooth       = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int bluetoothadmin  = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (readphone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (bluetooth != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH);
        }
        if (bluetoothadmin != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }
    boolean canAddItem = false;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ///DialogPassword dialogTable=new DialogPassword();
        //dialogTable.show(getSupportFragmentManager(),"mTag");
        getMenuInflater().inflate(R.menu.menu_mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mnSetting) {
            DialogPassword dialogPassword=new DialogPassword();
           // dialogPassword.setCancelable(false);
            dialogPassword.show(getSupportFragmentManager(),"mTag");
            return true;
        }else if(id == R.id.mnBackup){
            BackupDB backupDB=new BackupDB(this);
            backupDB.execute();
            return true;
        }else if(id == R.id.mnAdminPassword){
            DialogAdminPassword dialogPassword=new DialogAdminPassword();
            dialogPassword.show(getSupportFragmentManager(),"mTag");
            return true;
        }else if(id==R.id.mnForceSync) {
            Bundle b=new Bundle();
            b.putString("UFROM_KEY","MainMenu");
            DialogForceSync dialogForceSync = new DialogForceSync();
            dialogForceSync.setArguments(b);
            dialogForceSync.show(getSupportFragmentManager(), "mForceSync");
            return true;
        }else if(id==R.id.mnDBManager) {
            fnopendbmanager();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fnopendbmanager(){
        Bundle b=new Bundle();
        b.putString("CODE_KEY","");
        b.putString("TYPE_KEY","");
        b.putString("UFROM_KEY","mainactivity");
        DialogSuperUser dialogSuperUser=new DialogSuperUser();
        dialogSuperUser.setArguments(b);
        dialogSuperUser.show(getSupportFragmentManager(),"mTag");
    }
    /*Menu mMenu;
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(canAddItem){
            //getSupportActionBar().hide();
            mMenu = menu;
            mMenu.findItem(R.id.mnSetting).setVisible(false);
            mMenu.findItem(R.id.mnBackup).setVisible(false);
            DialogPassword dialogPassword=new DialogPassword();
            dialogPassword.setCancelable(false);
            dialogPassword.show(getSupportFragmentManager(),"mTag");
            canAddItem = false;
        } else{
            canAddItem = true;
            mMenu = menu;
            mMenu.findItem(R.id.mnSetting).setVisible(true);
            mMenu.findItem(R.id.mnBackup).setVisible(true);
           // getSupportActionBar().show();
            //menu.getItem(0).setIcon(R.drawable.ic_content_new);

        }

        return super.onPrepareOptionsMenu(menu);
    }
    */
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void checkAlter(){
        DBAdapter db=new DBAdapter(this);
        db.openDB();

        try {
            //create new table
            String newTable = "CREATE TABLE IF NOT EXISTS stk_othercharges(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, OCCode TEXT, Description TEXT," +
                    " R_ate REAL, T_ype TEXT, GLCode TEXT, L_ink TEXT, UnitCost REAL, UOM TEXT," +
                    " PurchaseTaxCode TEXT, SalesTaxCode TEXT, RetailTaxCode TEXT, OCGroup TEXT, OCAna1 TEXT," +
                    " OCAna2 TEXT, OCAna3 TEXT, SuspendedYN TEXT, MinimumCharge REAL, PurchaseAccount TEXT, " +
                    " BasedGrossAmountYN TEXT, ImportServiceYN TEXT) ";

            String newTable1 = "CREATE TABLE IF NOT EXISTS stk_service_dt(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, Doc1No TEXT, repairpartscode TEXT," +
                    " repairpartsserialno TEXT, repairpartsqty REAL, repairunitcost REAL, " +
                    " repairlineamount REAL, UnitCost REAL, L_ink TEXT," +
                    " BlankLine TEXT, N_o TEXT, ItemCode TEXT, " +
                    " Description TEXT, Qty REAL, FactorQty REAL, " +
                    " UOM TEXT, UOMSingular TEXT, HCUnitCost REAL, " +
                    " DisRate1 REAL, DisRate2 REAL, DisRate3 REAL, " +
                    " HCDiscount REAL, TaxRate1 REAL, TaxRate2 REAL, " +
                    " TaxRate3 REAL, DetailTaxCode TEXT, DetailTaxType TEXT," +
                    " HCTax REAL, HCLineAmt REAL, DocType TEXT, " +
                    " DefectCode TEXT, SynYN TEXT, repairpartsdesc TEXT) ";

            String newTable2 = "CREATE TABLE IF NOT EXISTS stk_service_hd(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, Doc1No TEXT, cuscode TEXT," +
                    " cusname TEXT, mobileno TEXT, receiptno TEXT, " +
                    " receiptdate TEXT, repairtype TEXT, casetype TEXT," +
                    " entryid TEXT, d_ate TEXT, outputid TEXT, " +
                    " outputdate TEXT, receivemode TEXT, termcode TEXT, " +
                    " productmodel TEXT, partno TEXT, serialno TEXT, " +
                    " supplierserialno TEXT, warrantystatus TEXT, warrantydesc TEXT, " +
                    " warrantyexpirydate TEXT, accessories TEXT, problemdesc TEXT, " +
                    " collectedby TEXT, collecteddate TEXT, sendtovendorYN TEXT," +
                    " sendtovendordate TEXT, vendorwarrantystatus TEXT, vendorcode TEXT, " +
                    " vendorname TEXT, vendortelno TEXT, backfromvendorYN TEXT," +
                    " backfromvendordate TEXT, returnbackenduserYN TEXT," +
                    " returnbackenduserdate TEXT, returnbackenduserby TEXT," +
                    " servicenoteremark TEXT, L_ink TEXT, Address TEXT," +
                    " ContactTel TEXT, Email TEXT, servicestatus TEXT," +
                    " ProductBrand TEXT, ProductCategory TEXT, PrincipleCode TEXT, " +
                    " Technician TEXT, DealerRunNo TEXT, SymptomCode TEXT," +
                    " ConditionCode TEXT, RepairFaultCode TEXT, BranchJSNo TEXT," +
                    " Signature TEXT, ImgService TEXT, SynYN TEXT," +
                    " Contact TEXT, ActionTimeStart TEXT, ActionTimeEnd TEXT," +
                    " TechnicalReport TEXT, Priority TEXT, InstallationDate TEXT,  " +
                    " T_ime TEXT, PhotoFile TEXT, PhotoFile2 BLOB)";


            String newTable3 = "CREATE TABLE IF NOT EXISTS stk_service_hd_temp(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, Doc1No TEXT, cuscode TEXT," +
                    " cusname TEXT, mobileno TEXT, receiptno TEXT, " +
                    " receiptdate TEXT, repairtype TEXT, casetype TEXT," +
                    " entryid TEXT, d_ate TEXT, outputid TEXT, " +
                    " outputdate TEXT, receivemode TEXT, termcode TEXT, " +
                    " productmodel TEXT, partno TEXT, serialno TEXT, " +
                    " supplierserialno TEXT, warrantystatus TEXT, warrantydesc TEXT, " +
                    " warrantyexpirydate TEXT, accessories TEXT, problemdesc TEXT, " +
                    " collectedby TEXT, collecteddate TEXT, sendtovendorYN TEXT," +
                    " sendtovendordate TEXT, vendorwarrantystatus TEXT, vendorcode TEXT, " +
                    " vendorname TEXT, vendortelno TEXT, backfromvendorYN TEXT," +
                    " backfromvendordate TEXT, returnbackenduserYN TEXT," +
                    " returnbackenduserdate TEXT, returnbackenduserby TEXT," +
                    " servicenoteremark TEXT, L_ink TEXT, Address TEXT," +
                    " ContactTel TEXT, Email TEXT, servicestatus TEXT," +
                    " ProductBrand TEXT, ProductCategory TEXT, PrincipleCode TEXT, " +
                    " Technician TEXT, DealerRunNo TEXT, SymptomCode TEXT," +
                    " ConditionCode TEXT, RepairFaultCode TEXT, BranchJSNo TEXT," +
                    " Signature TEXT, ImgService TEXT, SynYN TEXT," +
                    " Contact TEXT, ActionTimeStart TEXT, ActionTimeEnd TEXT," +
                    " TechnicalReport TEXT, Priority TEXT, InstallationDate TEXT,  " +
                    " T_ime TEXT, PhotoFile TEXT, PhotoFile2 BLOB)";

            String newTable4 = "CREATE TABLE IF NOT EXISTS signature_temp(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, Doc1No TEXT, " +
                    " Signature TEXT, ImgService TEXT, SynYN TEXT ) ";

            String newTable5 = "CREATE TABLE IF NOT EXISTS ret_membership_class(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, Class TEXT, " +
                    " Description TEXT, CollectionMethod TEXT, DateFrom TEXT," +
                    " DateTo TEXT, RatioPoint REAL, RatioAmount REAL, L_ink TEXT) ";

            String newTable6 = "CREATE TABLE IF NOT EXISTS ret_pointadjustment(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, cuscode TEXT, " +
                    " D_ate TEXT, Point REAL, DocType TEXT," +
                    " Remark TEXT, Screen TEXT, SynYN TEXT) ";

            String newTable7 = "CREATE TABLE IF NOT EXISTS ret_pointredeem_hd(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, Doc1No TEXT, " +
                    " D_ate TEXT, T_ime TEXT, Remark TEXT," +
                    " ClientCode TEXT, L_ink TEXT, D_ateTime TEXT," +
                    " SynYN TEXT) ";

            String newTable8 = "CREATE TABLE IF NOT EXISTS ret_pointredeem_dt(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, Doc1No TEXT, " +
                    " ItemCode TEXT, Point REAL, UnitCost REAL," +
                    " Qty REAL, FactorQty REAL, UOM TEXT," +
                    " Description TEXT, VoucherFrom TEXT, VoucherTo TEXT," +
                    " SynYN TEXT) ";

            String newTable9 = "CREATE TABLE IF NOT EXISTS ret_point(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, Item TEXT, " +
                    " Point REAL, B_ase TEXT, L_ink TEXT," +
                    " DocType TEXT) ";

            String newTable10 = "CREATE TABLE IF NOT EXISTS stk_master_photo(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ItemCode TEXT, " +
                    " PhotoFile  BLOB) ";

            String newTable11 = "CREATE TABLE IF NOT EXISTS stk_category(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " CategoryCode TEXT, Description TEXT Default '', AccountCode TEXT Default '', " +
                    " L_ink  TEXT Default '', CategoryFor TEXT Default '', SMobileYN TEXT Default '') ";

            String newTable12 = "CREATE TABLE IF NOT EXISTS stk_branch(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " BranchCode TEXT, Description TEXT Default '', Address TEXT Default '', " +
                    " AreaCode  TEXT Default '', Tel TEXT Default '', Fax TEXT Default ''," +
                    " L_ink  TEXT Default '', CusCode  TEXT Default '', UserDefine1 TEXT Default ''," +
                    " UserDefine2 TEXT Default '', UserDefine3 TEXT Default '' ) ";

            String newTable13 = "CREATE TABLE IF NOT EXISTS tb_salesperson(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " SalesPersonCode TEXT Default '', " +
                    " SalesPersonName  TEXT Default '') ";

            String newTable14 = "CREATE TABLE IF NOT EXISTS stk_salesman(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " SalesPersonCode TEXT Default '', N_ame TEXT Default '', Address TEXT Default ''," +
                    " AreaCode TEXT Default '', Tel TEXT Default '', Fax TEXT Default ''," +
                    " ParentCode TEXT Default '', DetailYN TEXT Default '', Status TEXT Default '', " +
                    " R_atio REAL, BranchCode TEXT Default '') ";

            String newTable15 = "CREATE TABLE IF NOT EXISTS stk_uom(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " UOM TEXT Default '', UOMPlural TEXT Default '') ";

            String newTable16 = "CREATE TABLE IF NOT EXISTS tb_othersetting(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ServiceCharges REAL Default 0, " +
                    " Remark TEXT Default '') ";
            db.addQuery(newTable16);

            String newTable17 = "CREATE TABLE IF NOT EXISTS sys_general_setup4(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " C_ode TEXT Default '', T_ype TEXT Default ''," +
                    " E_num TEXT Default '', D_ate TEXT Default '', T_ext TEXT Default '', " +
                    " I_nteger TEXT Default '', D_ouble REAL Default 0, D_ateTime TEXT Default ''," +
                    " T_ime TEXT Default '', ProgramName TEXT Default '') ";

            db.addQuery(newTable17);

            String newTable18 = "CREATE TABLE IF NOT EXISTS dum_stk_sales_order_hd(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " Doc1No TEXT Default '', " +
                    " Doc2No TEXT Default ''," +
                    " Attention TEXT Default '') ";
            db.addQuery(newTable18);

            String newTable19 = "CREATE TABLE IF NOT EXISTS stk_master_uom(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ItemCode TEXT Default '', " +
                    " UOM TEXT Default '', " +
                    " FactorQty REAL Default 0, " +
                    " UnitPrice REAL Default 0, " +
                    " ChildCode TEXT Default '', " +
                    " MSP REAL Default 0, " +
                    " MAXSP REAL Default 0, " +
                    " MPP REAL Default 0) ";
            db.addQuery(newTable19);

            String newTable20 = "CREATE TABLE IF NOT EXISTS cloud_cus_inv_hd(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " Doc1No TEXT Default '', " +
                    " Doc2No TEXT Default '', " +
                    " Doc3No TEXT Default '', " +
                    " D_ate TEXT Default '', " +
                    " CustCpde TEXT Default '', " +
                    " TermCode TEXT Default '', " +
                    " D_ay TEXT Default '', " +
                    " Attention TEXT Default '', " +
                    " HCGbDiscount REAL Default 0, " +
                    " HCGbTax REAL Default 0, " +
                    " HCNetAmt REAL Default 0, " +
                    " DocType TEXT Default '') ";
            db.addQuery(newTable20);

            String newTable21 = "CREATE TABLE IF NOT EXISTS stk_pricematrix(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, ItemCode TEXT, " +
                    " ItemGroup TEXT, CategoryCode TEXT, Description TEXT, " +
                    " Pct REAL, Criteria TEXT, Comparison TEXT, " +
                    " Qty REAL, PeriodYN TEXT, DateStart TEXT, " +
                    " DateEnd TEXT, QuotaYN TEXT, QuotaQty REAL, " +
                    " B_ase TEXT, CusCode TEXT, BranchCode TEXT, " +
                    " UnitPriceFormula TEXT, CusAna1 TEXT, CusAna2 TEXT, " +
                    " CusAna3 TEXT, ItemAna3 TEXT, LowerLimitQty REAL, " +
                    " UpperLimitQty REAL, Y1 REAL, Y2 REAL, " +
                    " Y3 REAL, Y4 REAl, Y5 REAL, " +
                    " Status TEXT, TimeStart TEXT, TimeEnd TEXT, " +
                    " L_ink TEXT, DateTimeModify TEXT, UserCode TEXT, " +
                    " MatrixType TEXT, UOMType TEXT, SpecialPriceYN TEXT, " +
                    " PriceCode TEXT, ServiceChargeYN TEXT Default '1', Memo TEXT Default '', " +
                    " iRunNo TEXT Default '')";
            db.addQuery(newTable21);

            String newTable22 = "CREATE TABLE IF NOT EXISTS sys_general_setup2(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " UseSerialNoYN TEXT Default '0', ItemGradingYN TEXT Default '0', PricingMatrixYN TEXT Default '0',  "+
                    " TransactionBatchYN TEXT Default '0', StockBundlingYN  TEXT Default '0',  ItemBatchYN TEXT Default '0', " +
                    " QuantityFormulaYN TEXT Default '0', UnitPriceFormulaYN TEXT Default '0', WarrantyDateYN TEXT Default '0', "+
                    " SalesPersonYN TEXT Default '0', BranchYN TEXT Default '0', DepartmentYN TEXT Default '0', "+
                    " ProjectYN TEXT Default '0', LocationYN TEXT Default '0', UserDefineAnalysisCodeYN TEXT Default '0', "+
                    " ResetDetailYN TEXT Default '0', CompBranchYN TEXT Default '0', CompDepartmentYN  TEXT Default '0', "+
                    " CompProjectYN TEXT Default '0', CompLocationYN TEXT Default '0', PostGlobalTaxYN TEXT Default '0', "+
                    " DepositAccountCode TEXT Default '0')";
            db.addQuery(newTable22);

            // `UOM`,`UOMPlural`
            //Log.d("new TABLE",newTable1);
            db.addQuery(newTable);
            db.addQuery(newTable1);
            db.addQuery(newTable2);
            db.addQuery(newTable3);
            db.addQuery(newTable4);
            db.addQuery(newTable5);
            db.addQuery(newTable6);
            db.addQuery(newTable7);
            db.addQuery(newTable8);
            db.addQuery(newTable9);
            db.addQuery(newTable10);
            db.addQuery(newTable11);
            db.addQuery(newTable12);
            db.addQuery(newTable13);
            db.addQuery(newTable14);
            db.addQuery(newTable15);

            if (db.isColumnExists("cloud_cus_inv_dt", "ItemGroup") != true) {
                String alterCloud = "ALTER TABLE cloud_cus_inv_dt ADD COLUMN ItemGroup TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("cloud_cus_inv_dt", "Description2") != true) {
                String alterCloud = "ALTER TABLE cloud_cus_inv_dt ADD COLUMN Description2 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_receipt2", "VoidYN") != true) {
                String alterCloud = "ALTER TABLE stk_receipt2 ADD COLUMN VoidYN TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_sales_order_dt", "Description2") != true) {
                String alterCloud = "ALTER TABLE stk_sales_order_dt ADD COLUMN Description2 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("tb_setting", "PostAs") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN PostAs TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("cloud_sales_order_dt", "ItemGroup") != true) {
                String alterCloud = "ALTER TABLE cloud_sales_order_dt ADD COLUMN ItemGroup TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("tb_setting", "EncodeType") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN EncodeType TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("tb_setting", "ReceiptType") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN ReceiptType TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_group", "Printer") != true) {
                String alterCloud = "ALTER TABLE stk_group ADD COLUMN Printer TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("cloud_cus_inv_dt", "Printer") != true) {
                String alterCloud = "ALTER TABLE cloud_cus_inv_dt ADD COLUMN Printer TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_sales_order_dt", "Printer") != true) {
                String alterCloud = "ALTER TABLE stk_sales_order_dt ADD COLUMN Printer TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("tb_setting", "Mgt01YN") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN Mgt01YN TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("tb_setting", "DepartmentCode") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN DepartmentCode TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("tb_setting", "CompCustomerYN") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN CompCustomerYN TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("tb_setting", "AdminPassword") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN AdminPassword TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("stk_master", "AlternateItem") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN AlternateItem TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("cloud_cus_inv_dt", "AlternateItem") != true) {
                String alterCloud = "ALTER TABLE cloud_cus_inv_dt ADD COLUMN AlternateItem TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("stk_sales_order_dt", "AlternateItem") != true) {
                String alterCloud = "ALTER TABLE stk_sales_order_dt ADD COLUMN AlternateItem TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_cus_inv_dt", "AlternateItem") != true) {
                String alterCloud = "ALTER TABLE stk_cus_inv_dt ADD COLUMN AlternateItem TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("cloud_sales_order_dt", "WarrantyDate") != true) {
                String alterCloud = "ALTER TABLE cloud_sales_order_dt ADD COLUMN WarrantyDate TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("cloud_sales_order_dt", "AnalysisCode2") != true) {
                String alterCloud = "ALTER TABLE cloud_sales_order_dt ADD COLUMN AnalysisCode2 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("cloud_sales_order_dt", "SORunNo") != true) {
                String alterCloud = "ALTER TABLE cloud_sales_order_dt ADD COLUMN SORunNo TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("tb_setting", "BranchCode") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN BranchCode TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("tb_setting", "LocationCode") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN LocationCode TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("stk_master", "HCDiscount") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN HCDiscount TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_master", "DisRate1") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN DisRate1 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("cloud_cus_inv_dt", "DUD6") != true) {
                String alterCloud = "ALTER TABLE cloud_cus_inv_dt ADD COLUMN DUD6 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("cloud_sales_order_dt", "DUD6") != true) {
                String alterCloud = "ALTER TABLE cloud_sales_order_dt ADD COLUMN DUD6 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_sales_order_dt", "DUD6") != true) {
                String alterCloud = "ALTER TABLE stk_sales_order_dt ADD COLUMN DUD6 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_cus_inv_dt", "DUD6") != true) {
                String alterCloud = "ALTER TABLE stk_cus_inv_dt ADD COLUMN DUD6 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("customer", "MembershipClass") != true) {
                String alterCloud = "ALTER TABLE customer ADD COLUMN MembershipClass TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("tb_member", "MembershipClass") != true) {
                String alterCloud = "ALTER TABLE tb_member ADD COLUMN MembershipClass TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("tb_member", "RatioPoint") != true) {
                String alterCloud = "ALTER TABLE tb_member ADD COLUMN RatioPoint REAL default 0; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("tb_member", "RatioAmount") != true) {
                String alterCloud = "ALTER TABLE tb_member ADD COLUMN RatioAmount REAL default 0; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_master", "Point") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN Point REAL default 0; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_master", "SupplierItemCode") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN SupplierItemCode TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_master", "MPP") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN MPP REAL default 0; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_master", "DateTimeModified") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN DateTimeModified TEXT default '1990-01-01 00:01:01'; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_master", "BaseCode") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN BaseCode TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_master", "UOMCode1") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN UOMCode1 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_master", "UOMCode2") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN UOMCode2 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_master", "UOMCode3") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN UOMCode3 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("stk_master", "UOMCode4") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN UOMCode4 TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("stk_master", "SuspendedYN") != true) {
                String alterCloud = "ALTER TABLE stk_master ADD COLUMN SuspendedYN TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("cloud_cus_inv_dt", "Point") != true) {
                String alterCloud = "ALTER TABLE cloud_cus_inv_dt ADD COLUMN Point REAL default 0; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("ret_pointadjustment", "D_ateTime") != true) {
                String alterCloud = "ALTER TABLE ret_pointadjustment ADD COLUMN D_ateTime TEXT default '0000-00-00 00:00:00'; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("ret_pointadjustment", "CardNo") != true) {
                String alterCloud = "ALTER TABLE ret_pointadjustment ADD COLUMN CardNo TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("tb_setting", "Modules") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN Modules TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("customer", "RegistrationDate") != true) {
                String alterCloud = "ALTER TABLE customer ADD COLUMN RegistrationDate TEXT default '1990-01-01'; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("customer", "ExpirationDate") != true) {
                String alterCloud = "ALTER TABLE customer ADD COLUMN ExpirationDate TEXT default '1990-01-01'; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("customer", "CategoryCode") != true) {
                String alterCloud = "ALTER TABLE customer ADD COLUMN CategoryCode TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("customer", "MaritialStatus") != true) {
                String alterCloud = "ALTER TABLE customer ADD COLUMN MaritialStatus TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("customer", "Race") != true) {
                String alterCloud = "ALTER TABLE customer ADD COLUMN Race TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("customer", "DateStart") != true) {
                String alterCloud = "ALTER TABLE customer ADD COLUMN DateStart TEXT default '1990-01-01'; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("tb_setting", "CategoryCode") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN CategoryCode TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("customer", "P_assword") != true) {
                String alterCloud = "ALTER TABLE customer ADD COLUMN P_assword TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("ret_paymenttype", "MerchantCode") != true) {
                String alterCloud = "ALTER TABLE ret_paymenttype ADD COLUMN MerchantCode TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("ret_paymenttype", "MerchantKey") != true) {
                String alterCloud = "ALTER TABLE ret_paymenttype ADD COLUMN MerchantKey TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("tb_member", "ContactTel") != true) {
                String alterCloud = "ALTER TABLE tb_member ADD COLUMN ContactTel TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("tb_member", "Email") != true) {
                String alterCloud = "ALTER TABLE tb_member ADD COLUMN Email TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("tb_setting", "SalesPersonCode") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN SalesPersonCode TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("tb_setting", "DirectPrintYN") != true) {
                String alterCloud = "ALTER TABLE tb_setting ADD COLUMN DirectPrintYN TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("companysetup", "Footer_CR") != true) {
                String AlterTable = "ALTER TABLE companysetup ADD COLUMN Footer_CR TEXT default '' ";
                db.addQuery(AlterTable);
            }
            if (db.isColumnExists("tb_setting", "GroupByItemYN") != true) {
                String AlterTable = "ALTER TABLE tb_setting ADD COLUMN GroupByItemYN TEXT default '1' ";
                db.addQuery(AlterTable);
            }
            if (db.isColumnExists("tb_setting", "AutoSyncYN") != true) {
                String AlterTable = "ALTER TABLE tb_setting ADD COLUMN AutoSyncYN TEXT default '0' ";
                db.addQuery(AlterTable);
            }
            if (db.isColumnExists("tb_setting", "D_ateTimeSync") != true) {
                String AlterTable = "ALTER TABLE tb_setting ADD COLUMN D_ateTimeSync TEXT default '' ";
                db.addQuery(AlterTable);
            }

            if (db.isColumnExists("tb_member", "CategoryCode") != true) {
                String alterCloud = "ALTER TABLE tb_member ADD COLUMN CategoryCode TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("cloud_cus_inv_dt", "MSP") != true) {
                String alterCloud = "ALTER TABLE cloud_cus_inv_dt ADD COLUMN MSP REAL default 0; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("tb_kitchenprinter", "EachSlip") != true) {
                String alterCloud = "ALTER TABLE tb_kitchenprinter ADD COLUMN EachSlip TEXT default ''; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("cloud_cus_inv_dt", "UnitCost") != true) {
                String alterCloud = "ALTER TABLE cloud_cus_inv_dt ADD COLUMN UnitCost REAL default 0; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("stk_sales_order_dt", "ServiceChargeYN") != true) {
                String alterCloud = "ALTER TABLE stk_sales_order_dt ADD COLUMN ServiceChargeYN TEXT '1' ; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("cloud_cus_inv_dt", "ServiceChargeYN") != true) {
                String alterCloud = "ALTER TABLE cloud_cus_inv_dt ADD COLUMN ServiceChargeYN TEXT '1' ; ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }
            if (db.isColumnExists("tb_setting", "FastKeypadYN") != true) {
                String AlterTable = "ALTER TABLE tb_setting ADD COLUMN FastKeypadYN TEXT default '0' ";
                db.addQuery(AlterTable);
            }
            if (db.isColumnExists("tb_othersetting", "ReceiptHeader") != true) {
                String AlterTable = "ALTER TABLE tb_othersetting ADD COLUMN ReceiptHeader TEXT default '' ";
                db.addQuery(AlterTable);
            }
            if (db.isColumnExists("tb_othersetting", "NewUOMYN") != true) {
                String AlterTable = "ALTER TABLE tb_othersetting ADD COLUMN NewUOMYN TEXT default '1' ";
                db.addQuery(AlterTable);
            }
            if (db.isColumnExists("tb_settingprinter", "PaperSize") != true) {
                String AlterTable = "ALTER TABLE tb_settingprinter ADD COLUMN PaperSize TEXT default '' ";
                db.addQuery(AlterTable);
            }
            if (db.isColumnExists("tb_kitchenprinter", "PaperSize") != true) {
                String AlterTable = "ALTER TABLE tb_kitchenprinter ADD COLUMN PaperSize TEXT default '' ";
                db.addQuery(AlterTable);
            }
            if (db.isColumnExists("tb_othersetting", "HidePaymentYN") != true) {
                String AlterTable = "ALTER TABLE tb_othersetting ADD COLUMN HidePaymentYN TEXT default '0' ";
                db.addQuery(AlterTable);
            }

            if (db.isColumnExists("stk_receipt2", "CurRate1") != true) {
                String AlterTable = "ALTER TABLE stk_receipt2 ADD COLUMN CurRate1 REAL default 1 ";
                db.addQuery(AlterTable);
            }

            if (db.isColumnExists("companysetup", "PhotoFile") != true) {
                String AlterTable = "ALTER TABLE companysetup ADD COLUMN PhotoFile BLOB  ";
                db.addQuery(AlterTable);
            }

            if (db.isColumnExists("tb_othersetting", "SMobileYN") != true) {
                String AlterTable = "ALTER TABLE tb_othersetting ADD COLUMN SMobileYN TEXT default '0' ";
                db.addQuery(AlterTable);
            }
            if (db.isColumnExists("stk_sales_order_dt", "VoidQty") != true) {
                String alterCloud = "ALTER TABLE stk_sales_order_dt ADD COLUMN VoidQty REAL default 0  ";
                Log.d("ALTER", alterCloud);
                db.addQuery(alterCloud);
            }

            if (db.isColumnExists("tb_settingprinter", "Copies") != true) {
                String AlterTable = "ALTER TABLE tb_settingprinter ADD COLUMN Copies TEXT default '1' ";
                db.addQuery(AlterTable);
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
    }
    private void initButton(){
        btnCashReceipt.setVisibility(View.GONE);
        btnReport.setVisibility(View.GONE);
        btnSync.setVisibility(View.GONE);
        btnSalesOrder.setVisibility(View.GONE);
        btnCreditNote.setVisibility(View.GONE);
       // btnListing.setVisibility(View.GONE);
        btnService.setVisibility(View.GONE);
        btnMember.setVisibility(View.GONE);
        btnSetting.setVisibility(View.GONE);
        btnItem.setVisibility(View.GONE);
        btnItemGroup.setVisibility(View.GONE);
        btnDashboard.setVisibility(View.GONE);
        btnUpdateNFC.setVisibility(View.GONE);
        btnRegisterNFC.setVisibility(View.GONE);
        btnTopUpNFC.setVisibility(View.GONE);
        btnQuickCash.setVisibility(View.GONE);
    }
    public void fncheckmodules(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qModule="select Modules from tb_setting";
            Cursor rsModule=db.getQuery(qModule);
            String lisModule="";
            while(rsModule.moveToNext()){
                lisModule=rsModule.getString(0);
            }
            String[] modules = lisModule.split(";");
            for (String add : modules) {
               showModule(add);
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void showModule(String module){
        switch (module) {
            case "Sales Order":
                btnSalesOrder.setVisibility(View.VISIBLE);
                break;
            case "Cash Receipt":
                btnCashReceipt.setVisibility(View.VISIBLE);
                break;
            case "Credit Note":
                btnCreditNote.setVisibility(View.VISIBLE);
                 break;
            case "Service":
                btnService.setVisibility(View.VISIBLE);
            case "Listing":
               // btnListing.setVisibility(View.VISIBLE);
                break;
            case "Report":
                btnReport.setVisibility(View.VISIBLE);
                break;
            case "Member":
                btnMember.setVisibility(View.VISIBLE);
                break;
            case "Sync":
                btnSync.setVisibility(View.VISIBLE);
                break;
            case "Item":
                btnItem.setVisibility(View.VISIBLE);
                break;
            case "Item Group":
                btnItemGroup.setVisibility(View.VISIBLE);
                break;
            case "Dashboard":
                btnDashboard.setVisibility(View.VISIBLE);
                break;
            case "Update NFC":
                btnUpdateNFC.setVisibility(View.VISIBLE);
                break;
            case "Register NFC":
                btnRegisterNFC.setVisibility(View.VISIBLE);
                break;
            case "Top Up NFC":
                btnTopUpNFC.setVisibility(View.VISIBLE);
                break;
            case "Quick Cash":
                btnQuickCash.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void fnchecksetting(){
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        Cursor c=db.getAllSeting();
        while (c.moveToNext()) {
            int RunNo=c.getInt(0);
            Log.d("RESULT", ""+RunNo);
            IPAddress = c.getString(1);
            ConnYN = c.getString(6);
        }
        if(IPAddress==null || !ConnYN.equals("success")) {
            /*btnCashReceipt.setEnabled(false);
            btnReport.setEnabled(false);
            btnSync.setEnabled(false);
            btnSalesOrder.setEnabled(false);
            btnCreditNote.setEnabled(false);
            btnListing.setEnabled(false);
            btnService.setEnabled(false);
            btnMember.setEnabled(false);
            btnCashReceipt.setBackgroundColor(Color.BLACK);
            btnReport.setBackgroundColor(Color.BLACK);
            btnSync.setBackgroundColor(Color.BLACK);
            btnSalesOrder.setBackgroundColor(Color.BLACK);
            btnCreditNote.setBackgroundColor(Color.BLACK);
            btnListing.setBackgroundColor(Color.BLACK);
            btnService.setBackgroundColor(Color.BLACK);
            btnMember.setBackgroundColor(Color.BLACK);*/
        }else{
            fncheckmodules();
            /*btnCashReceipt.setEnabled(true);
            btnReport.setEnabled(true);
            btnSync.setEnabled(true);
            btnSalesOrder.setEnabled(true);
            btnCreditNote.setEnabled(true);
            btnListing.setEnabled(true);
            btnService.setEnabled(true);
            btnMember.setEnabled(true);*/
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                    callNextActivity();
                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");
                    Toast.makeText(mContext, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public void callNextActivity()
    {
        Intent ss = new Intent(MainActivity.this, MainActivity.class);
        ss.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        ss.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ss.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ss.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(ss);
        finish();
    }
    public void openMainMenu(){
        Intent mainIntent = new Intent(MainActivity.this, Setting.class);
        startActivity(mainIntent);

    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        SetStatusBar(isConnected);
    }

    // Showing the status on status bar
    private void SetStatusBar(boolean isConnected) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if(isConnected) {
                window.setStatusBarColor(getResources().getColor(R.color.colorSkyBiz));
            }else{
                window.setStatusBarColor(Color.RED);
            }
        }else{

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        GlobalApplication.getInstance().setConnectivityListener(this);
    }
    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        SetStatusBar(isConnected);
    }
}

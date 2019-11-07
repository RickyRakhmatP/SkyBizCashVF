package skybiz.com.posoffline.ui_Setting.m_Local;

/**
 * Created by 7 on 30/10/2017.
 */

public class Constants {
    //COLUMNS
    static final String RunNo="RunNo";
    static final String ServerName = "ServerName";
    static final String UserName = "UserName";
    static final String Password = "Password";
    static final String DBName = "DBName";
    static final String Port = "Port";
    static final String ConnYN = "ConnYN";

    static final String CurCode = "CurCode";
    static final String GSTNo = "GSTNo";
    static final String CompanyName = "CompanyName";
    static final String RoundingCS = "RoundingCS";
    static final String LayawayAsSalesYN = "LayawayAsSalesYN";
    static final String vPostGlobalTaxYN = "vPostGlobalTaxYN";
    static final String Doc1No = "Doc1No";

    static final String TypePrinter = "TypePrinter";
    static final String NamePrinter = "NamePrinter";
    static final String IPPrinter = "IPPrinter";
    static final String UUID = "UUID";
    static final String PaperSize = "PaperSize";

    //pay type
    static final String PaymentCode = "PaymentCode";
    static final String PaymentType = "PaymentType";
    static final String PaidByCompanyYN = "PaidByCompanyYN";
    static final String Charges1 = "Charges1";

    //
    static final String ItemCode        = "ItemCode";
    static final String Description     = "Description";
    static final String ItemGroup       = "ItemGroup";
    static final String UnitPrice       = "UnitPrice";
    static final String UnitCost        = "UnitCost";
    static final String DefaultUOM      = "DefaultUOM";
    static final String UOM             = "UOM";
    static final String UOM1            = "UOM1";
    static final String UOM2            = "UOM2";
    static final String UOM3            = "UOM3";
    static final String UOM4            = "UOM4";
    static final String UOMFactor1      = "UOMFactor1";
    static final String UOMFactor2      = "UOMFactor2";
    static final String UOMFactor3      = "UOMFactor3";
    static final String UOMFactor4      = "UOMFactor4";
    static final String UOMPrice1       = "UOMPrice1";
    static final String UOMPrice2       = "UOMPrice2";
    static final String UOMPrice3       = "UOMPrice4";
    static final String UOMPrice4       = "UOMPrice4";
    static final String AnalysisCode1   = "AnalysisCode1";
    static final String AnalysisCode2   = "AnalysisCode2";
    static final String AnalysisCode3   = "AnalysisCode3";
    static final String AnalysisCode4   = "AnalysisCode4";
    static final String AnalysisCode5   = "AnalysisCode5";
    static final String MAXSP           = "MAXSP";
    static final String MAXSP1          = "MAXSP1";
    static final String MAXSP2          = "MAXSP2";
    static final String MAXSP3          = "MAXSP3";
    static final String MAXSP4          = "MAXSP4";
    static final String SalesTaxCode    = "SalesTaxCode";
    static final String RetailTaxCode   = "RetailTaxCode";
    static final String PurchaseTaxCode = "PurchaseTaxCode";
    static final String FixedPriceYN = "FixedPriceYN";
    //stk tax
    static final String TaxCode          = "TaxCode";
    static final String R_ate            = "R_ate";
    static final String TaxType          = "TaxType";
    static final String GSTTaxType       = "GSTTaxType";
    static final String GSTTaxCode       = "GSTTaxCode";

    //DB PROPERTIES
    static final String DB_NAME             ="db_offlinepos";
    static final String TB_NAME             ="tb_setting";
    static final String TB_NAME2            ="tb_sysgeneralsetup";
    static final String TB_PRINTER          ="tb_settingprinter";
    static final String TB_PAYTYPE          ="ret_paymenttype";
    static final String TB_STK_MASTER       ="stk_master";
    static final String TB_STK_TAX          ="stk_tax";
    static final String TB_CLOUD            ="cloud_cus_inv_dt";
    static final String TB_CLOUD2            ="cloud_sales_order_dt";
    static final String TB_STK_CUS_INV_HD   ="stk_cus_inv_hd";
    static final String TB_STK_CUS_INV_DT   ="stk_cus_inv_dt";
    static final String TB_STK_RECEIPT2     ="stk_receipt2";
    static final String TB_STK_DETAIL_TRN_OUT="stk_detail_trn_out";
    static final String TB_SYS_RUNNO_DT     ="sys_runno_dt";
    static final String TB_SYS_GENERAL_SETUP3 ="sys_general_setup3";
    static final String TB_COMPANYSETUP        ="companysetup";
    static final String TB_STK_COUNTER_TRN     ="stk_counter_trn";
    static final String TB_STK_SALES_ORDER_HD  ="stk_sales_order_hd";
    static final String TB_STK_SALES_ORDER_DT  ="stk_sales_order_dt";
    static final String TB_KITCHENPRINTER="tb_kitchenprinter";
    static final String TB_CUSTOMER="customer";

    static final int DB_VERSION='3';
    static final String CREATE_TB="CREATE TABLE tb_setting(RunNo INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "ServerName TEXT NOT NULL,UserName TEXT NOT NULL,Password TEXT,DBName TEXT NOT NULL," +
            " Port TEXT,ConnYN TEXT, DBStatus TEXT, ItemConn TEXT, UserCode TEXT, CounterCode TEXT);";

    static final String CREATE_TB2="CREATE TABLE tb_sysgeneralsetup(RunNo INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "CurCode TEXT NOT NULL,GSTNo TEXT,CompanyName TEXT,RoundingCS TEXT, " +
            " LayawayAsSalesYN TEXT,vPostGlobalTaxYN TEXT,Doc1No TEXT);";

    static final String CREATE_TB3="CREATE TABLE tb_settingprinter(RunNo INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "TypePrinter TEXT NOT NULL,NamePrinter TEXT,IPPrinter TEXT,UUID TEXT, Port TEXT);";

    static final String CREATE_TBPAYTYPE="CREATE TABLE ret_paymenttype(RunNo INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "PaymentCode TEXT NOT NULL,PaymentType TEXT,Charges1 TEXT,PaidByCompanyYN TEXT);";

    static final String CREATE_STK_MASTER="CREATE TABLE stk_master(RunNo INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "ItemCode TEXT NOT NULL, Description TEXT, ItemGroup TEXT, UnitPrice REAL, UnitCost TEXT,UOM TEXT, DefaultUOM TEXT," +
            "  UOM1 TEXT, UOM2 TEXT, UOM3 TEXT, UOM4 TEXT, UOMFactor1 TEXT, UOMFactor2 TEXT, UOMFactor3 TEXT, UOMFactor4 TEXT," +
            "  UOMPrice1 REAL, UOMPrice2 REAL, UOMPrice3 REAL, UOMPrice4 REAL, MSP REAL, MSP1 REAL, MSP2 REAL, MSP3 REAL, MSP4 REAL," +
            "  AnalysisCode1 TEXT, AnalysisCode2 TEXT, AnalysisCode3 TEXT, AnalysisCode4 TEXT, AnalysisCode5 TEXT, MAXSP REAL, MAXSP1 REAL, MAXSP2 REAL, MAXSP3 REAL, MAXSP4 REAL," +
            "  SalesTaxCode TEXT, RetailTaxCode TEXT, PurchaseTaxCode TEXT, FixedPriceYN TEXT);";

    static final String CREATE_STK_TAX="CREATE TABLE stk_tax(RunNo INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "TaxCode TEXT NOT NULL, R_ate TEXT, TaxType TEXT, GSTTaxType TEXT, GSTTaxCode TEXT, Description TEXT);";

    static final String CREATE_SYS_RUNNO_DT="CREATE TABLE sys_runno_dt(RunNo INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "RunNoCode TEXT NOT NULL, Prefix TEXT, LastNo TEXT);";


    static final String CREATE_SYS_GENERAL_SETUP3="CREATE TABLE sys_general_setup3(RunNo INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "SalesTaxCode TEXT, SalesTaxRate TEXT, PurchaseTaxCode TEXT,PurchaseTaxRate TEXT,RetailTaxCode TEXT, RetailTaxRate TEXT);";

    static final String CREATE_CLOUD="CREATE TABLE cloud_cus_inv_dt(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
            " Doc1No TEXT, N_o TEXT, ItemCode TEXT, Description TEXT, Qty REAL, FactorQty REAL, UOM TEXT, UOMSingular TEXT," +
            " HCUnitCost REAL, DisRate1 REAL, HCDiscount REAL, TaxRate1 REAL, HCTax REAL, DetailTaxCode TEXT, HCLineAmt REAL," +
            " BranchCode TEXT, DepartmentCode TEXT, ProjectCode TEXT, SalesPersonCode TEXT, LocationCode TEXT,WarrantyDate TEXT," +
            " BlankLine TEXT, DocType TEXT,AnalysisCode2 TEXT, ComputerName TEXT, LineNo TEXT, SORunNo TEXT, Doc1NoSO TEXT );";

    static final String CREATE_CLOUD2="CREATE TABLE cloud_sales_order_dt(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
            " Doc1No TEXT, N_o TEXT, ItemCode TEXT, Description TEXT, Qty REAL, FactorQty REAL, UOM TEXT, UOMSingular TEXT," +
            " HCUnitCost REAL, DisRate1 REAL, HCDiscount REAL, TaxRate1 REAL, HCTax REAL, DetailTaxCode TEXT, HCLineAmt REAL," +
            " BranchCode TEXT, DepartmentCode TEXT, ProjectCode TEXT, SalesPersonCode TEXT, LocationCode TEXT," +
            " BlankLine TEXT, DocType TEXT, ComputerName TEXT, LineNo TEXT);";


    static final String CREATE_STK_CUS_INV_DT="CREATE TABLE stk_cus_inv_dt(RunNo INTEGER PRIMARY KEY AUTOINCREMENT ," +
            " Doc1No TEXT, N_o TEXT, ItemCode TEXT, Description TEXT, Qty REAL, FactorQty REAL, UOM TEXT, UOMSingular TEXT, " +
            " HCUnitCost REAL, DisRate1 REAL, HCDiscount REAL, TaxRate1 REAL, HCTax REAL, DetailTaxCode TEXT, HCLineAmt REAL, " +
            " BranchCode TEXT, DepartmentCode TEXT, ProjectCode TEXT, SalesPersonCode TEXT, LocationCode TEXT, WarrantyDate TEXT," +
            " LineNo TEXT, BlankLine TEXT, DocType TEXT, AnalysisCode2 TEXT, SynYN TEXT, SORunNo TEXT);" ;


    static final String CREATE_STK_CUS_INV_HD="CREATE TABLE stk_cus_inv_hd(RunNo INTEGER PRIMARY KEY AUTOINCREMENT ," +
            " Doc1No TEXT, Doc2No TEXT, Doc3No TEXT," +
            " D_ate TEXT,D_ateTime TEXT, CusCode TEXT, MemberNo TEXT, DueDate TEXT, TaxDate TEXT," +
            " CurCode TEXT, CurRate1 TEXT, CurRate2 TEXT, CurRate3 TEXT," +
            " TermCode TEXT, D_ay TEXT, Attention TEXT, AddCode," +
            " BatchCode TEXT, GbDisRate1 REAL, GbDisRate2 REAL, GbDisRate3 REAL, HCGbDiscount REAL," +
            " GbTaxRate1 REAL, GbTaxRate2 REAL, GbTaxRate3 REAL, HCGbTax REAL, GlobalTaxCode TEXT, HCDtTax REAL," +
            " HCNetAmt REAL, AdjAmt REAL, GbOCCode TEXT, GbOCRate REAL, GbOCAmt REAL," +
            " DocType TEXT, ApprovedYN TEXT,RetailYN TEXT,UDRunNo TEXT," +
            " L_ink TEXT, Status TEXT,Status2 TEXT,SynYN TEXT);";


    static final String CREATE_STK_SALES_ORDER_HD="CREATE TABLE stk_sales_order_hd(RunNo INTEGER PRIMARY KEY AUTOINCREMENT ," +
            " Doc1No TEXT, Doc2No TEXT, Doc3No TEXT," +
            " D_ate TEXT, CusCode TEXT, DueDate TEXT," +
            " CurCode TEXT, CurRate1 TEXT, CurRate2 TEXT, CurRate3 TEXT," +
            " TermCode TEXT, D_ay TEXT, Attention TEXT, AddCode," +
            " GbDisRate1 REAL, GbDisRate2 REAL, GbDisRate3 REAL, HCGbDiscount REAL," +
            " GbTaxRate1 REAL, GbTaxRate2 REAL, GbTaxRate3 REAL, HCGbTax REAL, GlobalTaxCode TEXT, HCDtTax REAL," +
            " HCNetAmt REAL, GbOCCode TEXT, GbOCRate REAL, GbOCAmt REAL," +
            " DepositNo TEXT, Deposit REAL, DepositAccountCode TEXT,"+
            " DocType TEXT, ApprovedYN TEXT, UDRunNo TEXT," +
            " L_ink TEXT,SynYN TEXT, Status Text);";

    static final String CREATE_STK_SALES_ORDER_DT="CREATE TABLE stk_sales_order_dt(RunNo INTEGER PRIMARY KEY AUTOINCREMENT ," +
            " Doc1No TEXT, N_o TEXT, ItemCode TEXT, Description TEXT, Qty REAL, FactorQty REAL, UOM TEXT, UOMSingular TEXT, " +
            " HCUnitCost REAL, DisRate1 REAL, HCDiscount REAL, TaxRate1 REAL, HCTax REAL, DetailTaxCode TEXT, HCLineAmt REAL, " +
            " BranchCode TEXT, DepartmentCode TEXT, ProjectCode TEXT, SalesPersonCode TEXT, LocationCode TEXT," +
            " LineNo TEXT, BlankLine TEXT, SynYN TEXT, AnalysisCode2 TEXT);" ;

    static final String CREATE_STK_RECEIPT2="CREATE TABLE stk_receipt2(RunNo INTEGER PRIMARY KEY AUTOINCREMENT ," +
            " D_ate TEXT, T_ime TEXT, D_ateTime TEXT, Doc1No TEXT, CashAmt REAL," +
            " CC1Code TEXT, CC1Amt TEXT, CC1No TEXT, CC1Expiry TEXT, CC1ChargesAmt REAL, CC1ChargesRate REAL," +
            " CC2Code TEXT, CC2Amt TEXT, CC2No TEXT, CC2Expiry TEXT, CC2ChargesAmt REAL, CC2ChargesRate REAL," +
            " Cheque1Code TEXT, Cheque1Amt REAL, Cheque1No TEXT," +
            " Cheque2Code TEXT, Cheque2Amt REAL, Cheque2No TEXT," +
            " PointAmt REAL, VoucherAmt REAL, CurCode TEXT, CurRate TEXT, FCAmt REAL," +
            " CusCode TEXT, BalanceAmount REAL, ChangeAmt REAL, CounterCode TEXT, UserCode TEXT, DocType TEXT,SynYN TEXT);";

    static final String CREATE_STK_DETAIL_TRN_OUT="CREATE TABLE stk_detail_trn_out(RunNo INTEGER PRIMARY KEY AUTOINCREMENT ," +
            " ItemCode TEXT, Doc3No TEXT, D_ate TEXT, QtyOUT REAL, FactorQty REAL, UOM TEXT, UnitPrice REAL, CusCode TEXT," +
            " DocType3 TEXT, Doc3NoRunNo TEXT, LocationCode TEXT, L_ink TEXT, HCTax REAL, BookDate TEXT, SynYN TEXT);" ;

    static final String CREATE_COMPANYSETUP="CREATE TABLE companysetup(RunNo INTEGER PRIMARY KEY AUTOINCREMENT ," +
            " CompanyCode TEXT, CompanyName TEXT, Address TEXT, ComTown TEXT, ComState TEXT," +
            " ComCountry TEXT, Tel1 TEXT, Fax1 TEXT,CompanyEmail TEXT, GSTNo TEXT, CurCode TEXT);" ;

    static final String CREATE_STK_COUNTER_TRN="CREATE TABLE stk_counter_trn(RunNo INTEGER PRIMARY KEY AUTOINCREMENT ," +
            " CounterCode TEXT, ComputerName TEXT, UserCode TEXT, DateOpen TEXT, DateClose TEXT," +
            " TimeOpen TEXT, TimeClose TEXT, DateTimeOpen TEXT, DateTimeClose TEXT," +
            " Value_100 TEXT, Value_50 TEXT, Value_20 TEXT, Value_10 TEXT, Value_5 TEXT," +
            " Value_2 TEXT, Value_1 TEXT, Value_050 TEXT, Value_020 TEXT," +
            " Value_010 TEXT, Value_005 TEXT, Value_001 TEXT," +
            " Value_100000 TEXT, Value_50000 TEXT, Value_20000 TEXT, Value_10000 TEXT," +
            " Value_5000 TEXT, Value_1000 TEXT, Value_500 TEXT, Value_200 TEXT);" ;

    static final String CREATE_KITCHENPRINTER="CREATE TABLE tb_kitchenprinter(RunNo INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "TypePrinter TEXT NOT NULL,NamePrinter TEXT,IPPrinter TEXT,UUID TEXT, Port TEXT);";

   /* CusCode,CusName,FinCatCode,AccountCode,Address,CurCode" +
            " TermCode,D_ay,SalesPersonCode,Tel,Tel2,Fax,Fax2,Contact,ContactTel,Email,StatusBadYN, " +
            " Town,State,Country,PostCode,L_ink,NRICNo,DATE_FORMAT(DOB,'%Y-%m-%d') as DOB, Sex," +
            " MemberType,CardNo,PaymentCode,DATE_FORMAT(DateTimeModified.'%Y-%m-%d %H:%i:%s') as DateTimeModified*/
   static final String CREATE_CUSTOMER="CREATE TABLE customer(RunNo INTEGER PRIMARY KEY AUTOINCREMENT," +
           " FinCatCode TEXT ,AccountCode TEXT, CusCode TEXT, CusName TEXT, Address TEXT," +
           " CurCode TEXT, TermCode TEXT, D_ay TEXT, SalesPersonCode TEXT, Tel TEXT, Tel2 TEXT," +
           " Fax TEXT, Fax2 TEXT, Contact TEXT, ContactTel TEXT, Email TEXT, StatusBadYN TEXT," +
           " Town TEXT, State TEXT, Country TEXT, PostCode TEXT, L_ink TEXT, NRICNo TEXT, DOB TEXT, Sex TEXT," +
           " MemberType TEXT, CardNo TEXT, PaymentCode TEXT, DateTimeModified TEXT);";

    static final String CREATE_TBMEMBER="CREATE TABLE tb_member(RunNo INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "CusCode TEXT NOT NULL, CusName TEXT, TermCode TEXT, D_ay TEXT, SalesPersonCode TEXT);";

    static final String CREATE_STK_GROUP="CREATE TABLE stk_group(RunNo INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "ItemGroup TEXT NOT NULL, Description TEXT, Description2 TEXT, " +
            " L_ink TEXT, Modifier1 TEXT, Modifier2 TEXT, DateTimeModified TEXT);";

    static final String ALTER_RECEIPT2="ALTER TABLE stk_receipt2 ADD COLUMN VoidYN TEXT default '0';";

    static final String ALTER_CLOUD_CUS_INV_DT="ALTER TABLE cloud_cus_inv_dt ADD COLUMN Description2 TEXT default '';";

    static final String ALTER_STK_SALES_ORDER_DT="ALTER TABLE stk_sales_order_dt ADD COLUMN Description2 TEXT default '';";
}

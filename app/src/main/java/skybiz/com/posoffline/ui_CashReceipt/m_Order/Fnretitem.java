package skybiz.com.posoffline.ui_CashReceipt.m_Order;

/**
 * Created by 7 on 06/11/2017.
 */

public class Fnretitem  {
 /*   //Spacecraft
    Double UnitPrice,UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4,FactorQty;
    String URL,DetailTaxCode,Description,DefaultUOM,UOM,UOM1,UOM2,UOM3,UOM4,RetailTaxCode,PurchaseTaxCode,SalesTaxCode,
        ItemCode, IPAddress, UserName, Password, DBName;

    public  Fnretitem(String ItemCode, String IPAddress,String UserName, String Password,String DBName) {
        try{
            Log.d("RESULT",ItemCode+"-"+IPAddress+"-"+UserName+"-"+Password+"/"+DBName);
            URL="jdbc:mysql://"+IPAddress+"/"+DBName;
            Connection conn= Connector.connect(URL, UserName, Password);
            if (conn == null) {
                Log.d("RESULT","Error Connection");
            }
            String sql="Select UnitPrice,DefaultUOM,Description,UOM,UOM1,UOM2,UOM3,UOM4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4" +
                    "UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,RetailTaxCode,PurchaseTaxCode, SalesTaxCode from stk_master where ItemCode='"+ItemCode+"' ";
            Statement statement = conn.createStatement();
            if (statement.execute(sql)) {
                ResultSet resultSet         = statement.getResultSet();
                ResultSetMetaData columns   = resultSet.getMetaData();
                while (resultSet.next()) {
                    UnitPrice       = Double.parseDouble(columns.getColumnName(1));
                    DefaultUOM      = columns.getColumnName(2);
                    Description     = columns.getColumnName(3);
                    UOM             = columns.getColumnName(4);
                    UOM1            = columns.getColumnName(5);
                    UOM2            = columns.getColumnName(6);
                    UOM3            = columns.getColumnName(7);
                    UOM4            = columns.getColumnName(8);
                    UOMFactor1      =  Double.parseDouble(columns.getColumnName(9));
                    UOMFactor2      =  Double.parseDouble(columns.getColumnName(10));
                    UOMFactor3      =  Double.parseDouble(columns.getColumnName(11));
                    UOMFactor4      =  Double.parseDouble(columns.getColumnName(12));
                    UOMPrice1       =  Double.parseDouble(columns.getColumnName(13));
                    UOMPrice2       =  Double.parseDouble(columns.getColumnName(14));
                    UOMPrice3       =  Double.parseDouble(columns.getColumnName(15));
                    UOMPrice4       =  Double.parseDouble(columns.getColumnName(16));
                    UOMPrice1       =  Double.parseDouble(columns.getColumnName(17));
                    RetailTaxCode   =  columns.getColumnName(18);
                }

                if(DefaultUOM.equals("0")){
                    //UnitPrice   =UnitPrice;
                    FactorQty   =1.00;
                   // UOM         =UOM;
                }else if(DefaultUOM.equals("1")){
                    UnitPrice   =UOMPrice1;
                    FactorQty   =UOMFactor1;
                    UOM         =UOM1;
                }else if(DefaultUOM.equals("2")){
                    UnitPrice   =UOMPrice2;
                    FactorQty   =UOMFactor2;
                    UOM         =UOM2;
                }else if(DefaultUOM.equals("3")){
                    UnitPrice   =UOMPrice3;
                    FactorQty   =UOMFactor3;
                    UOM         =UOM3;
                }else if(DefaultUOM.equals("4")){
                    UnitPrice   =UOMPrice4;
                    FactorQty   =UOMFactor4;
                    UOM         =UOM4;
                }
                //resultSet.close();
            }
            statement.close();
        } catch (SQLException e ){
            e.printStackTrace();
        }
        //return new Fnretitem(Description,UnitPrice,UOM,FactorQty,DetailTaxCode);
    }*/
}

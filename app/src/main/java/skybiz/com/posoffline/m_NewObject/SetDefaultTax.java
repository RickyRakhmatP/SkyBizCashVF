package skybiz.com.posoffline.m_NewObject;

public class SetDefaultTax {
    public static String DetailTax(String DocType,String RetailTaxCode, String PurchaseTaxCode, String SalesTaxCode){
        String DefaultTax="";
        switch(DocType){
            case "SO"       : DefaultTax=SalesTaxCode; break;
            case "CS"       : DefaultTax=RetailTaxCode; break;
            case "CusCN"    : DefaultTax=RetailTaxCode; break;
        }
        return DefaultTax;
    }
}

 /*
        //Purchase
	if($DocType=='PurReq_IN') {
		$Tax="PurchaseTaxCode";
	}
	else if($DocType=='PurReq') {
		$Tax="PurchaseTaxCode";
	}
	else if($DocType=='SupQuo') {
		$Tax="PurchaseTaxCode";
	}
	else if($DocType=='PO') {
		$Tax="PurchaseTaxCode";
	}
	else if($DocType=='GRNDO') {
		$Tax="PurchaseTaxCode";
	}
	else if($DocType=='SupInv'){
		$Tax="PurchaseTaxCode";
	}
	else if($DocType=='SupCN') {
		$Tax="PurchaseTaxCode";
	}
	else if($DocType=='SupDN') {
		$Tax="PurchaseTaxCode";
	}


	//sales
	else if($DocType=='Quo') {
		$Tax="SalesTaxCode";
	}
	else if($DocType=='SO') {
		$Tax="SalesTaxCode";
	}
	else if($DocType=='ProInv') {
		$Tax="SalesTaxCode";
	}
	else if($DocType=='DO') {
		$Tax="SalesTaxCode";
	}
	else if($DocType=='CusInv') {
		$Tax="SalesTaxCode";
	}
	else if($DocType=='CS') {
		$Tax="RetailTaxCode";
	}
	else if($DocType=='CusCN') {
		$Tax="RetailTaxCode";
	}
	else if($DocType=='CusDN') {
		$Tax="RetailTaxCode";
	}


	//inventory
	else if($DocType=='IS') {
		$Tax="RetailTaxCode";
	}

	else if($DocType=='RS') {
		$Tax="RetailTaxCode";
	}
	else if($DocType=='stk_ResStk') {
		$Tax="RetailTaxCode";
	}
	else if($DocType=='TS') {
		$Tax="RetailTaxCode";
	}
	else if($DocType=='SA') {
		$Tax="RetailTaxCode";
	}

     */
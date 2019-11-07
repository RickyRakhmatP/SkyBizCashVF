package skybiz.com.posoffline.m_NewObject;

import java.sql.Blob;

public class Spacecraft_JS {
    int id;
    String Doc1No, cuscode, cusname,
            mobileno, receiptno, receiptdate,
            repairtype, casetype, entryid,
            d_ate, outputid, outputdate,
            receivemode, termcode, productmodel,
            partno, serialno, supplierserialno,
            warrantystatus, warrantydesc, warrantyexpirydate,
            accessories, problemdesc, collectedby,
            collecteddate, sendtovendorYN, sendtovendordate,
            vendorwarrantystatus, vendorcode, vendorname,
            vendortelno, backfromvendorYN, backfromvendordate,
            returnbackenduserYN, returnbackenduserdate, returnbackenduserby,
            servicenoteremark, L_ink, Address, ContactTel,
            Email, servicestatus, Technician,
            Contact, Priority, T_ime,
            InstallationDate,TechnicalReport,DateTimeAttended,
            PhotoFile,PhotoFile2,PhotoFileName,ActionTimeStart,ActionTimeEnd;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDoc1No() {
        return Doc1No;
    }

    public void setDoc1No(String doc1No) {
        Doc1No = doc1No;
    }

    public String getCuscode() {
        return cuscode;
    }

    public void setCuscode(String cuscode) {
        this.cuscode = cuscode;
    }

    public String getCusname() {
        return cusname;
    }

    public void setCusname(String cusname) {
        this.cusname = cusname;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getReceiptno() {
        return receiptno;
    }

    public void setReceiptno(String receiptno) {
        this.receiptno = receiptno;
    }

    public String getReceiptdate() {
        return receiptdate;
    }

    public void setReceiptdate(String receiptdate) {
        this.receiptdate = receiptdate;
    }

    public String getRepairtype() {
        return repairtype;
    }

    public void setRepairtype(String repairtype) {
        this.repairtype = repairtype;
    }

    public String getCasetype() {
        return casetype;
    }

    public void setCasetype(String casetype) {
        this.casetype = casetype;
    }

    public String getEntryid() {
        return entryid;
    }

    public void setEntryid(String entryid) {
        this.entryid = entryid;
    }

    public String getD_ate() {
        return d_ate;
    }

    public void setD_ate(String d_ate) {
        this.d_ate = d_ate;
    }

    public String getOutputid() {
        return outputid;
    }

    public void setOutputid(String outputid) {
        this.outputid = outputid;
    }

    public String getOutputdate() {
        return outputdate;
    }

    public void setOutputdate(String outputdate) {
        this.outputdate = outputdate;
    }

    public String getReceivemode() {
        return receivemode;
    }

    public void setReceivemode(String receivemode) {
        this.receivemode = receivemode;
    }

    public String getTermcode() {
        return termcode;
    }

    public void setTermcode(String termcode) {
        this.termcode = termcode;
    }

    public String getProductmodel() {
        return productmodel;
    }

    public void setProductmodel(String productmodel) {
        this.productmodel = productmodel;
    }

    public String getPartno() {
        return partno;
    }

    public void setPartno(String partno) {
        this.partno = partno;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getSupplierserialno() {
        return supplierserialno;
    }

    public void setSupplierserialno(String supplierserialno) {
        this.supplierserialno = supplierserialno;
    }

    public String getWarrantystatus() {
        return warrantystatus;
    }

    public void setWarrantystatus(String warrantystatus) {
        this.warrantystatus = warrantystatus;
    }

    public String getWarrantydesc() {
        return warrantydesc;
    }

    public void setWarrantydesc(String warrantydesc) {
        this.warrantydesc = warrantydesc;
    }

    public String getWarrantyexpirydate() {
        return warrantyexpirydate;
    }

    public void setWarrantyexpirydate(String warrantyexpirydate) {
        this.warrantyexpirydate = warrantyexpirydate;
    }

    public String getAccessories() {
        return accessories;
    }

    public void setAccessories(String accessories) {
        this.accessories = accessories;
    }

    public String getProblemdesc() {
        return problemdesc;
    }

    public void setProblemdesc(String problemdesc) {
        this.problemdesc = problemdesc;
    }

    public String getCollectedby() {
        return collectedby;
    }

    public void setCollectedby(String collectedby) {
        this.collectedby = collectedby;
    }

    public String getCollecteddate() {
        return collecteddate;
    }

    public void setCollecteddate(String collecteddate) {
        this.collecteddate = collecteddate;
    }

    public String getSendtovendorYN() {
        return sendtovendorYN;
    }

    public void setSendtovendorYN(String sendtovendorYN) {
        this.sendtovendorYN = sendtovendorYN;
    }

    public String getSendtovendordate() {
        return sendtovendordate;
    }

    public void setSendtovendordate(String sendtovendordate) {
        this.sendtovendordate = sendtovendordate;
    }

    public String getVendorwarrantystatus() {
        return vendorwarrantystatus;
    }

    public void setVendorwarrantystatus(String vendorwarrantystatus) {
        this.vendorwarrantystatus = vendorwarrantystatus;
    }

    public String getVendorcode() {
        return vendorcode;
    }

    public void setVendorcode(String vendorcode) {
        this.vendorcode = vendorcode;
    }

    public String getVendorname() {
        return vendorname;
    }

    public void setVendorname(String vendorname) {
        this.vendorname = vendorname;
    }

    public String getVendortelno() {
        return vendortelno;
    }

    public void setVendortelno(String vendortelno) {
        this.vendortelno = vendortelno;
    }

    public String getBackfromvendorYN() {
        return backfromvendorYN;
    }

    public void setBackfromvendorYN(String backfromvendorYN) {
        this.backfromvendorYN = backfromvendorYN;
    }

    public String getBackfromvendordate() {
        return backfromvendordate;
    }

    public void setBackfromvendordate(String backfromvendordate) {
        this.backfromvendordate = backfromvendordate;
    }

    public String getReturnbackenduserYN() {
        return returnbackenduserYN;
    }

    public void setReturnbackenduserYN(String returnbackenduserYN) {
        this.returnbackenduserYN = returnbackenduserYN;
    }

    public String getReturnbackenduserdate() {
        return returnbackenduserdate;
    }

    public void setReturnbackenduserdate(String returnbackenduserdate) {
        this.returnbackenduserdate = returnbackenduserdate;
    }

    public String getReturnbackenduserby() {
        return returnbackenduserby;
    }

    public void setReturnbackenduserby(String returnbackenduserby) {
        this.returnbackenduserby = returnbackenduserby;
    }

    public String getServicenoteremark() {
        return servicenoteremark;
    }

    public void setServicenoteremark(String servicenoteremark) {
        this.servicenoteremark = servicenoteremark;
    }

    public String getL_ink() {
        return L_ink;
    }

    public void setL_ink(String l_ink) {
        L_ink = l_ink;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getContactTel() {
        return ContactTel;
    }

    public void setContactTel(String contactTel) {
        ContactTel = contactTel;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getServicestatus() {
        return servicestatus;
    }

    public void setServicestatus(String servicestatus) {
        this.servicestatus = servicestatus;
    }

    public String getTechnician() {
        return Technician;
    }

    public void setTechnician(String technician) {
        Technician = technician;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public String getT_ime() {
        return T_ime;
    }

    public void setT_ime(String t_ime) {
        T_ime = t_ime;
    }

    public String getInstallationDate() {
        return InstallationDate;
    }

    public void setInstallationDate(String installationDate) {
        InstallationDate = installationDate;
    }

    public String getTechnicalReport() {
        return TechnicalReport;
    }

    public void setTechnicalReport(String technicalReport) {
        TechnicalReport = technicalReport;
    }

    public String getDateTimeAttended() {
        return DateTimeAttended;
    }

    public void setDateTimeAttended(String dateTimeAttended) {
        DateTimeAttended = dateTimeAttended;
    }

    public String getPhotoFile() {
        return PhotoFile;
    }

    public void setPhotoFile(String photoFile) {
        PhotoFile = photoFile;
    }

    public String getPhotoFile2() {
        return PhotoFile2;
    }

    public void setPhotoFile2(String photoFile2) {
        PhotoFile2 = photoFile2;
    }

    public String getPhotoFileName() {
        return PhotoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        PhotoFileName = photoFileName;
    }

    public String getActionTimeStart() {
        return ActionTimeStart;
    }

    public void setActionTimeStart(String actionTimeStart) {
        ActionTimeStart = actionTimeStart;
    }

    public String getActionTimeEnd() {
        return ActionTimeEnd;
    }

    public void setActionTimeEnd(String actionTimeEnd) {
        ActionTimeEnd = actionTimeEnd;
    }
}

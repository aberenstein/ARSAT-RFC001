package cust.psdi.app.asset;

import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldAssetSerialnum extends MboValueAdapter {
	final int fldWidth = 12;
	
	public FldAssetSerialnum(MboValue mbv) throws MXException {
		super(mbv);
	}

	public void validate() throws MXException, MXApplicationException, java.rmi.RemoteException 
	{
		String fldValue = getMboValue().getString();
		
		if(fldValue.contains("|"))
		{
			int pipeIndex = fldValue.indexOf("|");

			String serialnum = fldValue.substring(0, pipeIndex);
			getMboValue("SERIALNUM").setValue(serialnum, NOACCESSCHECK|NOVALIDATION_AND_NOACTION);

			String chipsetnum = fldValue.substring(pipeIndex + 1);
			getMboValue("ARS_CHIPSETNUM").setValue(chipsetnum, NOACCESSCHECK|NOVALIDATION_AND_NOACTION);
		}
		
		super.validate();
    }
	
}

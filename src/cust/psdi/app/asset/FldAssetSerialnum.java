package cust.psdi.app.asset;

import java.rmi.RemoteException;

import psdi.app.asset.Asset;
import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.tamit.app.asset.TAMITFldAssetChild;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldAssetSerialnum extends TAMITFldAssetChild {
	String chipsetnum;
	int fldWidth = 12;
	
	public FldAssetSerialnum(MboValue mbv) throws MXException {
		super(mbv);
	}

	public void validate() throws MXException, MXApplicationException, java.rmi.RemoteException 
	{
		String fldValue = getMboValue().getString();
		
		if(fldValue.contains("|"))
		{
			int pipeIndex = fldValue.indexOf("|");
			chipsetnum = fldValue.substring(pipeIndex + 1); 
			String serialnum = fldValue.substring(0, pipeIndex - 1).trim().substring(0, fldWidth);

		    Asset asset = (Asset)getMboValue().getMbo();
		    asset.setValue("serialnum", serialnum, Mbo.NOVALIDATION);
		}
		
		super.validate();
    }
	
	public void action() throws MXException, RemoteException
    {
		if (!chipsetnum.isEmpty())
		{
		    Asset asset = (Asset)getMboValue().getMbo();
		    asset.setValue("chipsetnum", chipsetnum, Mbo.NOACCESSCHECK);
		}
	}
			  
}

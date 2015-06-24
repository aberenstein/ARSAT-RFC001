package cust.psdi.app.inventory.virtual;

import java.rmi.RemoteException;
import psdi.app.inventory.virtual.AssetInputRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class AssetInput
  extends psdi.app.inventory.virtual.AssetInput
  implements AssetInputRemote
{
  public AssetInput(MboSet ms)
    throws MXException, RemoteException
  {
    super(ms);
  }
  
  public void save()
    throws MXException, RemoteException
  {
    MboSetRemote assetSet = getMboSet("$ASSETINPUT2ASSET", "ASSET", "ASSETNUM = :ASSETNUM AND ITEMNUM = :ITEMNUM AND SITEID = :SITEID");
    if (!assetSet.isEmpty()) {
      for (MboRemote currMbo = assetSet.moveFirst(); currMbo != null; currMbo = assetSet.moveNext()) {
        currMbo.setValue("ARS_CHIPSETNUM", getString("ARS_CHIPSETNUM"), 2L);
      }
    }
    super.save();
  }
}

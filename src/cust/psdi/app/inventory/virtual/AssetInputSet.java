package cust.psdi.app.inventory.virtual;

import java.rmi.RemoteException;
import psdi.app.inventory.virtual.AssetInputSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class AssetInputSet
  extends psdi.app.inventory.virtual.AssetInputSet
  implements AssetInputSetRemote
{
  public AssetInputSet(MboServerInterface ms)
    throws MXException, RemoteException
  {
    super(ms);
  }
  
  protected Mbo getMboInstance(MboSet ms)
    throws MXException, RemoteException
  {
    return new AssetInput(ms);
  }
}

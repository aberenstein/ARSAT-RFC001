package custom.webclient.components;

public class ItemsRotablesSplit extends ItemsRotables {

	/**
	 * Valida el item rotable. 
	 *
	 * @param  itemRotable  Es el item rotable a validar.
	 * @exception  IllegalArgumentException  Si el argumento es inv�lido.
	 */	
	protected void Valid(ItemRotable itemRotable) throws IllegalArgumentException
	{
		if (itemRotable.getItemNum() == null || itemRotable.getItemNum().isEmpty() || 
			itemRotable.getAssetNum() == null || itemRotable.getAssetNum().isEmpty()) {
			String msg = "Argumento inv�lido: ItemRotable('"+itemRotable.getItemNum()+"','"+itemRotable.getAssetNum()+"','"+itemRotable.getSerialNum()+"','"+itemRotable.getGlAccount()+"','"+itemRotable.getChipsetNum()+"')";
			throw new IllegalArgumentException(msg);
		}
	}
	
}


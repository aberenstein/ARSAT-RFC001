package custom.webclient.components;

public class ItemsRotablesInput extends ItemsRotables {

	/**
	 * Valida el item rotable. 
	 *
	 * @param  itemRotable  Es el item rotable a validar.
	 * @exception  IllegalArgumentException  Si el argumento es inválido.
	 */	
	protected void Valid(ItemRotable itemRotable) throws IllegalArgumentException
	{
		if (itemRotable.getItemNum() == null || itemRotable.getItemNum().isEmpty() || 
			itemRotable.getSerialNum() == null || itemRotable.getSerialNum().isEmpty()) {
			String msg = "Argumento inválido: ItemRotable('"+itemRotable.getItemNum()+"','"+itemRotable.getAssetNum()+"','"+itemRotable.getSerialNum()+"','"+itemRotable.getGlAccount()+"')";
			throw new IllegalArgumentException(msg);
		}
	}
	
}


 /*
 * ItemRotable.java
 *
 * Creado 2014-03-28T22:00:00
 */

/**
 * Clase que representa un artículo rotable.
 * Lleva los datos necesarios para su carga en el recibo de OC.
 * @author  AMB
 */

package custom.webclient.components;

public class ItemRotable {
	private String itemNum;
	private String assetNum;
	private String serialNum;
	private String glAccount;
	private String chipsetNum;
	
	/**
	 * Constructor nulo. 
	 */	
	public ItemRotable() {}
	
	/**
	 * Constructor. 
	 *
	 * @param  itemNum  Es el código del artículo en el Maestro de Artículos, es el valor itemnum en la BD.
	 * @param  assetNum  Es el código del activo individual, es el  valor assetnum en la BD.
	 * @param  serialNum  Es el N° de serie del activo, es el valor serialnum en la BD.
	 * @param  glAccount  Es el código de la cuenta del LM, es el valor glaccount en la BD.
	 * @param  chipsetNum  Es el N° de chipset, es el valor ars_chipsetnum en la BD.
	 * @exception  IllegalArgumentException  Si alguno de los argumentos no es válido.
	 */	
	public ItemRotable(String itemNum, String assetNum, String serialNum, String glAccount, String chipsetNum) throws IllegalArgumentException
	{
		this.setItemNum(itemNum);
		this.setAssetNum(assetNum);
		this.setSerialNum(serialNum);
		this.setGlAccount(glAccount);
		this.setChipsetNum(chipsetNum);
	}

	/**
	 * Devuelve el código de artículo.
	 *
	 * @return  El código del artículo en el Maestro de Artículo, es el valor itemnum en la BD.
	 */	
	public String getItemNum() {return this.itemNum;}
	
	/**
	 * Devuelve el código de activo.
	 *
	 * @return  El codigo del activo individual, es el valor assetnum en la BD.
	 */	
	public String getAssetNum() {return this.assetNum;}
	
	/**
	 * Devuelve el código de la cuenta del LM.
	 *
	 * @return  El código de la cuenta del LM, es el valor glaccount en la BD.
	 */	
	public String getGlAccount() {return this.glAccount;}
	
	/**
	 * Devuelve el N° de chipset.
	 *
	 * @return  El N° de chipset, es el valor ars_chipsetnum en la BD.
	 */	
	public String getChipsetNum() {return this.chipsetNum;}
	
	/**
	 * Devuelve el N° de serie del activo.
	 *
	 * @return  El N° de serie del activo, es el valor serialnum en la BD.
	 */	
	public String getSerialNum() {return this.serialNum;}

	/**
	 * Establece el valor del código de artículo.
	 *
	 * @param  itemNum  Es el código del artículo en el Maestro de Artículo, es el valor itemnum en la BD.
	 * @exception  IllegalArgumentException  Si el argumento es inválido.
	 */	
	public void setItemNum(String itemNum) throws IllegalArgumentException {
		boolean valid = itemNum == null || itemNum.isEmpty() || (itemNum != null && !itemNum.isEmpty() && itemNum.length() <= 30);
		if (!valid) {
			String errorMsg = "Argumento inválido: itemNum.";
			throw new IllegalArgumentException(errorMsg);
		}
		this.itemNum = itemNum == null? null: itemNum.trim().toUpperCase();
	}

	/**
	 * Establece el código de activo. 
	 *
	 * @param  assetNum  Es el código del activo individual, es el valor assetnum en la BD.
	 * @exception  IllegalArgumentException  Si el argumento es inválido.
	 */	
	public void setAssetNum(String assetNum) throws IllegalArgumentException {
		boolean valid = assetNum == null || assetNum.isEmpty() || (assetNum != null && !assetNum.isEmpty() && assetNum.length() <= 12);
		if (!valid) {
			String errorMsg = "Argumento inválido: assetNum.";
			throw new IllegalArgumentException(errorMsg);
		}		
		this.assetNum = assetNum == null? null: assetNum.trim().toUpperCase();
	}

	/**
	 * Establece el N° de chipset. 
	 *
	 * @param  glAccount  Es el N° de chipset, es el valor ars_chipsetnum en la BD.
	 * @exception  IllegalArgumentException  Si el argumento es inválido.
	 */	
	public void setChipsetNum(String chipsetNum) throws IllegalArgumentException {
		boolean valid = chipsetNum == null || chipsetNum.isEmpty() || (chipsetNum != null && !chipsetNum.isEmpty() && chipsetNum.length() <= 30);
		if (!valid) {
			String errorMsg = "Argumento inválido: chipsetNum.";
			throw new IllegalArgumentException(errorMsg);
		}		
		this.chipsetNum = chipsetNum == null? null: chipsetNum.trim().toUpperCase();
	}

	/**
	 * Establece el valor del código de la cuenta del LM. 
	 *
	 * @param  glAccount  Es el código de la cuenta del LM, es el valor glaccount en la BD.
	 * @exception  IllegalArgumentException  Si el argumento es inválido.
	 */	
	public void setGlAccount(String glAccount) throws IllegalArgumentException {
		boolean valid = glAccount == null || glAccount.isEmpty() || glAccount.matches("\\d{4}-\\d{4}-\\d{15}");
		if (!valid) {
			String errorMsg = "Argumento inválido: glAccount.";
			throw new IllegalArgumentException(errorMsg);
		}		
		this.glAccount = glAccount == null? null: glAccount.trim().toUpperCase();
	}

	/**
	 * Establece el valor del N° de serie del activo. 
	 *
	 * @param  serialNum  Es el N° de serie del activo, es el valor serialnum en la BD.
	 * @exception  IllegalArgumentException  Si el argumento es inválido.
	 */	
	public void setSerialNum(String serialNum) throws IllegalArgumentException {
		boolean valid = serialNum == null || serialNum.isEmpty() || (serialNum != null && !serialNum.isEmpty() && serialNum.length() <= 64);
		if (!valid) {
			String errorMsg = "Argumento inválido: serialNum.";
			throw new IllegalArgumentException(errorMsg);
		}		
		this.serialNum = serialNum == null? null: serialNum.trim().toUpperCase();
	}

}


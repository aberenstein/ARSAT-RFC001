 /*
 * SelAttachBean.java
 *
 * Creado 2014-04-07T23:00:00
 */

/**
 * Bean con el código de la ventana de selección de archivos adjuntos para la carga de ítems rotables en la recepción de OC.
 * 
 * @author  AMB
 */

package custom.webclient.beans.invusage;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.ibm.icu.impl.InvalidFormatException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.beans.invusage.InvUseLineSplitBean;
import psdi.webclient.system.beans.DataBean;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;
import custom.webclient.components.*;

public class SelAttachBean extends DataBean
{
	
	/**
	 * Graba el mensaje en los registros de auditoría del sistema como DEBUG.
	 *
	 * @param  msg  Es el mensaje a registrar.
	 */	
	@SuppressWarnings("unused")
	private static void log(String msg)
	{
		MXLogger myLogger = MXLoggerFactory.getLogger("maximo.uisession");
		if (myLogger.isDebugEnabled()) {myLogger.debug(msg);}
	}
	
	/**
	 * Graba el mensaje en los registros de auditoría del sistema como ERROR.
	 *
	 * @param  msg  Es el mensaje a registrar.
	 */	
	private static void errLog(String msg)
	{
		MXLogger myLogger = MXLoggerFactory.getLogger("maximo.uisession");
		if (myLogger.isErrorEnabled()) {myLogger.error(msg);}
	}
		
	/**
	 * Abre un diálogo con un mensaje y un botón de Aceptar.
	 *
	 * @param  msg  Es el mensaje a mostrar.
	 */	
	@SuppressWarnings("deprecation")
	private void msgbox(String msg)
	{
		String params[] = {msg};
		this.clientSession.showMessageBox(this.clientSession.getCurrentEvent(), "messagebox", "CustomMessage", params);
	}
	
	/**
	 * Constructor nulo.
	 *
	 */	
	public SelAttachBean() {
		super();
	}

	/**
	 * Inicializa el bean.
	 *
	 * @exception  MXException
	 * @exception  RemoteException
	 */	
	protected void initialize() throws MXException, RemoteException
	{
	    super.initialize();
	}
	
	/**
	 * Ejecuta la funcionalidad del diálogo.
	 *
	 * @exception  MXException
	 * @exception  RemoteException
	 */	
	public int execute() throws MXException, RemoteException
	{
	    try {
			// Contenedor para los ítems rotables que vienen en los adjuntos 
			ItemsRotables artsRots = new ItemsRotablesSplit();
			
	    	// Obtengo el dataset con adjuntos e itero
	    	MboSetRemote mbosetAdjunto = this.getMboSet();
	    	
	    	for (int i = 0; i < mbosetAdjunto.count(); i++) {
	    		// Obtengo un registro con datos del adjunto
	    		Mbo mboAdjunto = (Mbo)mbosetAdjunto.getMbo(i);
	    	
	    		// Le pregunto al objeto de datos si el adjunto ha sido seleccionado
				boolean isSelected = mboAdjunto.isSelected();
				
				if (isSelected) {
					// Seleccionado
					// Cargo el contenedor con items rotables
		    		String pathname = mboAdjunto.getString("urlname");
		    		try {
		    			artsRots.LeerDeArchivo(pathname);
		    		}
		    		catch (FileNotFoundException ex)
		    		{
			    		// Le pido al objeto de datos el nombre completo del adjunto
			    		String documento = mboAdjunto.getString("document");

			    		// Le aviso al operador
						String errorMsg = "El archivo '" + documento + "' no puede ser leido.\nPor favor, subirlo nuevamente observando las siguientes indicaciones:\n";
						errorMsg += "En el ícono 'Adjuntos' seleccione 'Agregar nuevos adjuntos', a continuación 'Agregar archivo nuevo'.";
						errorMsg += "En la ventana 'Crear un archivo adjunto' seleccione 'Copiar el documento en la ubicación predeterminada...' y deseleccione todas las otras opciones.";
						throw new Exception(errorMsg);
		    			
		    		}
		    		catch (InvalidFormatException ex)
		    		{
			    		// Le pido al objeto de datos el nombre completo del adjunto
			    		String documento = mboAdjunto.getString("document");

			    		// Le aviso al operador
						String errorMsg = "Error al leer el archivo '" + documento + "': formato inválido";
						throw new Exception(errorMsg);
		    		}
		    		catch (IOException ex)
		    		{
		    		    errLog(ex.getMessage());
		    		    throw ex;
		    		}
		    		catch (IllegalArgumentException ex)
		    		{
		    		    errLog(ex.getMessage());
		    		    throw ex;
		    		}
				}
	    	}
	    	
	    	// Verifico si el operador seleccionó algo
	    	if (artsRots.size() <= 0) {
	    		// No seleccionó nada
				String msg = "No se han seleccionado archivos";
				msgbox(msg);
			} else {
				// Seleccionó. Ahí vamos.
				// Ya tengo artsRots cargado con todos los rotables del archivo

				// Valido las cantidades
				HashMap<String, Integer> itemsADespachar = new HashMap<String, Integer>();
				
				// Obtengo el consumo de inventario para asignarle los ítems de los adjuntos
				MboRemote invuse = this.parent.getMbo();
				if (invuse == null) {
					 String errorMsg = "Error inesperado: no se encuentra el consumo de inventario";
					throw new Exception(errorMsg);
				}
				
				// Obtengo del consumo de inventario un dataset con los rotables a asignar
				MboSetRemote mbosetItemsADespachar = invuse.getMboSet("SPLITUSELINE");
		    	
				// Itero sobre los ítems rotables despachables
		    	for (int i = 0; i < mbosetItemsADespachar.count(); i++) {
		    		
		    		// Averiguo que activo es este ítem
		    		Mbo mboItemADespachar = (Mbo)mbosetItemsADespachar.getMbo(i);
		    		String itemnum = mboItemADespachar.getString("itemnum");
		    		int quantity = mboItemADespachar.getInt("quantity");

		    		Integer cantidadAcumulada = itemsADespachar.get(itemnum);
		    		if (cantidadAcumulada == null)
		    		{
		    			cantidadAcumulada = 0;
		    		}
		    		cantidadAcumulada += quantity;
		    		itemsADespachar.put(itemnum, cantidadAcumulada);
		    	}
		    	// Ya tengo en itemsADespachar pares <itemnum, quantity> acumuladas de las lineas de consumo
		    	// Ahora valido las cantidades

		    	// Itero sobreitemsADespachar (consumo) y lo comparo con artsRots (archivo)
		    	int cantidadTotalAConsumir = 0;
				for (Map.Entry<String, Integer> entry:itemsADespachar.entrySet()){
					String itemnumADespachar = entry.getKey();
					int cantidadADespacharArchivo = artsRots.size(itemnumADespachar);
					int cantidadADespacharConsumo = entry.getValue();

					if (cantidadADespacharConsumo != cantidadADespacharArchivo)
					{
						 String errorMsg = "Error: las cantidades indicadas en el/los archivo/s adjuntos no se corresponden con las cantidades de consumo";
						 throw new Exception(errorMsg);
					}
					
					cantidadTotalAConsumir += cantidadADespacharConsumo;
				}
				
				if (cantidadTotalAConsumir != artsRots.size())
				{
					 String errorMsg = "Error: las cantidades indicadas en el/los archivo/s adjuntos no se corresponden con las cantidades de consumo";
					 throw new Exception(errorMsg);
				}
				
				// Si llegué hasta acá es porque las cantidades cierran 
				// Ahora invoco AUTOSPLITQTY para dividir las cantidades e insertar las líneas de consumo
				
				InvUseLineSplitBean invUseLineSplitBean = (InvUseLineSplitBean)this.app.getDataBean("SPLITUSAGEQTY");
			    if (invUseLineSplitBean == null)
			    {
			    	String errorMsg = "Error inesperado: no se encuentra el componente SPLITUSAGEQTY";
					throw new Exception(errorMsg);
			    }
		    	invUseLineSplitBean.AUTOSPLITQTY();

				// Ahora itero y limpio en cada línea el número de artículo 
		    	for (int i = 0; i < mbosetItemsADespachar.count(); i++) {
		    		
		    		// Averiguo que activo es este ítem
		    		Mbo mboItemADespachar = (Mbo)mbosetItemsADespachar.getMbo(i);

		    		// Obtengo los despachados
					MboSetRemote mbosetItemsDespachados = mboItemADespachar.getMboSet("LINESPLIT");
					
					// Itero y reemplazo por lo indicado en el archivo
			    	for (int j = 0; j < mbosetItemsDespachados.count(); j++) {
			    		// Averiguo que activo es este ítem
			    		Mbo mboItemDespachado = (Mbo)mbosetItemsDespachados.getMbo(j);
			    	
			    		// Reemplazo el número de artículo por un string vacío
			    		mboItemDespachado.setValue("rotassetnum", "");
			    	}
		    	}
				
				// Ahora itero y reemplazo en cada línea el número de artículo 
		    	for (int i = 0; i < mbosetItemsADespachar.count(); i++) {
		    		
		    		// Averiguo que activo es este ítem
		    		Mbo mboItemADespachar = (Mbo)mbosetItemsADespachar.getMbo(i);

		    		// Obtengo los despachados
					MboSetRemote mbosetItemsDespachados = mboItemADespachar.getMboSet("LINESPLIT");
					
					// Itero y reemplazo por lo indicado en el archivo
			    	for (int j = 0; j < mbosetItemsDespachados.count(); j++) {
			    		// Averiguo que activo es este ítem
			    		Mbo mboItemDespachado = (Mbo)mbosetItemsDespachados.getMbo(j);
			    	
			    		// Obtengo la clave
			    		String itemnum = mboItemDespachado.getString("itemnum");
			    		
			    		// Obtengo el número de ítem
			    		ItemRotable itemRotable = artsRots.getItemRotable(itemnum);
			    		
			    		// Reemplazo el número de artículo por el del archivo
			    		mboItemDespachado.setValue("rotassetnum", itemRotable.getAssetNum());
			    	}
		    	}
				
		        this.app.getDataBean("splitteduselinesRot").refreshTable();
		        this.app.getDataBean("splitteduselinesRotCost").refreshTable();
		        this.app.getDataBean("splitteduselines").refreshTable();
		        this.app.getDataBean().getParent().refreshTable();
		        this.app.getDataBean().refreshTable();
			}
	    }
	    catch (Exception ex) {		
			msgbox(ex.getMessage());
	    }   

	    return 1;
	}

}


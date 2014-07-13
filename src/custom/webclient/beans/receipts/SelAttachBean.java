 /*
 * SelAttachBean.java
 *
 * Creado 2014-03-28T23:00:00
 */

/**
 * Bean con el c�digo de la ventana de selecci�n de archivos adjuntos para la carga de �tems rotables en la recepci�n de OC.
 * 
 * @author  AMB
 */

package custom.webclient.beans.receipts;

import java.rmi.RemoteException;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.ibm.icu.impl.InvalidFormatException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

import custom.webclient.components.*;

public class SelAttachBean extends DataBean
{
	
	/**
	 * Graba el mensaje en los registros de auditor�a del sistema como DEBUG.
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
	 * Graba el mensaje en los registros de auditor�a del sistema como ERROR.
	 *
	 * @param  msg  Es el mensaje a registrar.
	 */	
	private static void errLog(String msg)
	{
		MXLogger myLogger = MXLoggerFactory.getLogger("maximo.uisession");
		if (myLogger.isErrorEnabled()) {myLogger.error(msg);}
	}
		
	/**
	 * Abre un di�logo con un mensaje y un bot�n de Aceptar.
	 *
	 * @param  msg  Es el mensaje a mostrar.
	 */	
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
	 * Ejecuta la funcionalidad del di�logo.
	 *
	 * @exception  MXException
	 * @exception  RemoteException
	 */	
	@SuppressWarnings("deprecation")
	public int execute() throws MXException, RemoteException
	{
	    try {
			// Contenedor para los �tems rotables que vienen en los adjuntos 
			ItemsRotables artsRots = new ItemsRotablesInput();
			
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
						errorMsg += "En el �cono 'Adjuntos' seleccione 'Agregar nuevos adjuntos', a continuaci�n 'Agregar archivo nuevo'.";
						errorMsg += "En la ventana 'Crear un archivo adjunto' seleccione 'Copiar el documento en la ubicaci�n predeterminada...' y deseleccione todas las otras opciones.";
						throw new Exception(errorMsg);
		    			
		    		}
		    		catch (InvalidFormatException ex)
		    		{
			    		// Le pido al objeto de datos el nombre completo del adjunto
			    		String documento = mboAdjunto.getString("document");

			    		// Le aviso al operador
						String errorMsg = "Error al leer el archivo '" + documento + "': formato inv�lido";
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
	    	
	    	// Verifico si el operador seleccion� algo
	    	if (artsRots.size() <= 0) {
	    		// No seleccion� nada
				String msg = "No se han seleccionado archivos";
				msgbox(msg);
			} else {
				// Seleccion�. Ah� vamos.
				// Ya tengo artsRots cargado con todos los rotables
				
				// Obtengo la OC para asignar los �tems de los adjuntos
				MboRemote po = this.parent.getMbo();
				if (po == null) {
					 String errorMsg = "Error inesperado: no se encuentra la OC";
					throw new Exception(errorMsg);
				}
				
				// Obtengo de la OC un dataset con los rotables a asignar

				MboSetRemote mbosetArticulosRecibidos = po.getMboSet("ASSETINPUT");
		    	
		    	// Me preparo para contar
				int asignados = 0;
				int sinAsignarEnOC = mbosetArticulosRecibidos.count();
				int sinAsignarEnAdj = artsRots.size();

				// Itero sobre los �tems rotables de la OC
		    	for (int i = 0; i < mbosetArticulosRecibidos.count(); i++) {
		    		// Si no hay nada para repartir salgo de la iteraci�n
		    		if (sinAsignarEnAdj == 0) break;
		    		
		    		// Averiguo que activo es este �tem
		    		Mbo mboArticuloRecibido = (Mbo)mbosetArticulosRecibidos.getMbo(i);
		    		String itemNum = mboArticuloRecibido.getString("itemnum");
		    		// ya tengo el c�digo de activo del �tem recibido
		    		
		    		// ahora saco un articulo correspondiente de la bolsa (archivo adjunto)
		    		ItemRotable itemRotable = artsRots.getItemRotable(itemNum);
		    	
		    		if (itemRotable != null) {
		    			// saque un art�culo de la bolsa: asigno

		    			String assetnum = itemRotable.getAssetNum();
		    			String serialnum = itemRotable.getSerialNum();
		    			String glaccount = itemRotable.getGlAccount();

		    			if (assetnum != null && assetnum != "")
		    			{
		    				mboArticuloRecibido.setValue("assetnum", assetnum, Mbo.NOACCESSCHECK);
		    			}
		    			
		    			if (serialnum != null && serialnum != "")
		    			{
		    				mboArticuloRecibido.setValue("serialnum", serialnum, Mbo.NOACCESSCHECK);
		    			}
		    			
		    			if (glaccount != null && glaccount != "")
		    			{
		    				mboArticuloRecibido.setValue("glaccount", glaccount, Mbo.NOACCESSCHECK);
		    			}
		    					    			
		    			// Cuento as� despu�s informo
		    			asignados++;
		    			sinAsignarEnOC--;
		    			sinAsignarEnAdj--;
		    		} else {
		    			// no hay art�culos en la bolsa con ese c�digo: no puedo asignar, no hago nada
		    		}
		    	}

		    	DataBean databean = this.getParent();
		    	if (databean != null) {
		    		databean.fireStructureChangedEvent();
		    		databean.reloadTable();
		    		this.sessionContext.queueRefreshEvent();
		    	} else {
					 String errorMsg = "Error inesperado: no se encuentra la tabla rotasset_rotatingitems_table";
					throw new Exception(errorMsg);
		    	}

		    	// Final. Le informo al usuario.
		    	String msg = "Fin de la operaci�n.\n";
		    	msg += "Registros asignados: " + String.valueOf(asignados) + "\n";
		    	msg += "Registros no asignados de la OC: " + String.valueOf(sinAsignarEnOC) + "\n";
		    	msg += "Registros no asignados de archivos adjuntos: " + String.valueOf(sinAsignarEnAdj) + "\n";
				msgbox(msg);
			}
	    }
	    catch (Exception ex) {		
			msgbox(ex.getMessage());
	    }   

	    return 1;
	}

}


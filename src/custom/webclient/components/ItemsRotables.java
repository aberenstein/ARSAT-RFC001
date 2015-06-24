 /*
 * ItemsRotables.java
 *
 * Creado 2014-03-28T22:30:00
 */

/**
 * Clase que representa una colección de ítems rotables.
 * Se carga la colección de uno o más archivos en formato excel o texto (csv).
 * Se recuperan los ítems de a uno solicitándolos de la colección por código de artículo.
 * 
 * @author  AMB
 */

package custom.webclient.components;

import java.io.*;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ibm.icu.impl.InvalidFormatException;

import au.com.bytecode.opencsv.CSVReader;

public abstract class ItemsRotables
{
	protected Map<String, List<ItemRotable>> itemsRotables = new HashMap<String, List<ItemRotable>>();
	
	/**
	 * Constructor nulo. 
	 */	
	public ItemsRotables() {}
	
	/**
	 * Carga la colección de ítems rotables de un archivo.
	 * Se admite el formato excel o texto (csv). 
	 *
	 * @param  pathname  Es el nombre completo del archivo con los ítems rotables.
	 * @exception  InvalidFormatException  Si el formato del archivo o sus datos son inválidos.
	 * @exception  IllegalArgumentException  Si el argumento es nulo o vacío.
	 * @exception  IOException  Si no se puede abrir, leer o cerrar el archivo.
	 * @exception  FileNotFoundException  Si el archivo no existe o es un directorio. 
	 */	
	public void LeerDeArchivo(String pathname) throws FileNotFoundException, InvalidFormatException, IllegalArgumentException, IOException {

		File archivo = new File(pathname);
		if (pathname == null || pathname.isEmpty()) {
			String errorMsg = "[ItemsRotables:LeerDeArchivo] Error inesperado al intentar leer el archivo: el nombre del archivo no puede ser nulo o vacío.";
			throw new IllegalArgumentException(errorMsg);
		} else if (!archivo.exists() || archivo.isDirectory()) {
			String errorMsg = "Error al intentar leer el archivo '" + pathname + "': no existe o es un directorio.";
			throw new FileNotFoundException(errorMsg);
		}
			
		try {
			List<String[]> registros;
			
			String extension = pathname.substring(pathname.lastIndexOf('.')).toLowerCase();
			if (extension.equalsIgnoreCase(".csv") || extension.equalsIgnoreCase(".txt"))
				registros = this.LoadFromCsv(pathname);
			else if (extension.equalsIgnoreCase(".xls") || extension.equalsIgnoreCase(".xlsx"))
				registros = this.LoadFromExcel(pathname);
			else {
				String errorMsg = "Error al intentar leer el archivo '" + pathname + "': formato inválido.";
				throw new InvalidFormatException(errorMsg);
			}
			
			// Ya tengo el contenido del adjunto en el contenedor 'registros'
			this.CargarDatos(registros);
		}
		catch (IllegalArgumentException ex) {
			String errorMsg = "Error al leer el archivo '" + pathname + "': formato inválido.";
			throw new InvalidFormatException(errorMsg);
		}
	}

	/**
	 * Carga la colección de ítems rotables en el contenedor.
	 *
	 * @param  registros  Es la colección leida del archivo.
	 * @exception  InvalidFormatException  Si el formato del archivo o sus datos son inválidos.
	 */	
	public void CargarDatos(List<String[]> registros) throws InvalidFormatException {
		
		String [] titulo = registros.get(0);

		// ahora itero sobre el resto de la lista 
		for(int i = 1; i < registros.size(); i++)
		{
			String itemNum = null;
			String assetNum = null;
			String serialNum = null;
			String glAccount = null;
			String chipsetNum = null;
			
			for(int j = 0; j < titulo.length && j < 5; j++)
			{
				if (titulo[j].equalsIgnoreCase("ITEMNUM"))  		itemNum = registros.get(i)[j];
				else if (titulo[j].equalsIgnoreCase("ASSETNUM"))	assetNum = registros.get(i)[j];
				else if (titulo[j].equalsIgnoreCase("SERIALNUM"))	serialNum = registros.get(i)[j];
				else if (titulo[j].equalsIgnoreCase("GLACCOUNT"))	glAccount = registros.get(i)[j];
				else if (titulo[j].equalsIgnoreCase("CHIPSETNUM"))	chipsetNum = registros.get(i)[j];
			}
			
			ItemRotable itemRotable = new ItemRotable();
			itemRotable.setItemNum(itemNum);
			itemRotable.setAssetNum(assetNum);
			itemRotable.setSerialNum(serialNum);
			itemRotable.setGlAccount(glAccount);
			itemRotable.setChipsetNum(chipsetNum);
			
			this.Put(itemRotable);
		}
	
	}
	
	/**
	 * Devuelve un ítem rotable con el identificador de activo indicado y lo remueve de la colección.
	 *
	 * @param  itemNum  Es el código del artículo en el Maestro de Artículos, es el valor itemnum en la BD.
	 * @return  Un ItemRotable con el código indicado o null si no hay ítems con ese código en la colección.
	 * @exception  IllegalArgumentException  Si el argumento es nulo o vacío.
	 */	
	public ItemRotable getItemRotable(String itemNum) throws IllegalArgumentException { 
		// Convierto a mayúsculas
		if (itemNum == null || itemNum.isEmpty()) {
			String errorMsg = "ItemsRotables.getItemRotable: argumento inválido.";
			throw new IllegalArgumentException(errorMsg);
		} else {
			itemNum = itemNum.trim().toUpperCase();
		}
		
		// obtengo la lista de ítems para el artículo del argumento
		List<ItemRotable> itemsPorClave = this.itemsRotables.get(itemNum);
		
		if (itemsPorClave == null || itemsPorClave.size() == 0) {
			// la lista es vacía: devuelvo null
			return null;
		} else {
			// la lista tiene ítems
			
			// traigo el primer ítem de la lista
			ItemRotable itemRotable = itemsPorClave.get(0);
			
			// remuevo el ítem
			itemsPorClave.remove(0);
			
			// pongo la lista actualizada en la colección
			this.itemsRotables.put(itemNum, itemsPorClave);
			
			// devuelvo el ítem al solicitante
			return itemRotable;
		}
	}
	
	/**
	 * Devuelve un ítem rotable lo remueve de la colección.
	 *
	 * @return  Un ItemRotable o null si no hay ítems en la colección.
	 */	
	public ItemRotable getItemRotable() {

		ItemRotable itemRotable = null;
		
		for (Map.Entry<String, List<ItemRotable>> entry:this.itemsRotables.entrySet()){
			List<ItemRotable> itemsRotablesPorClave = (List<ItemRotable>)entry.getValue();
			itemRotable = itemsRotablesPorClave.get(0);
			itemsRotablesPorClave.remove(0);
			if (itemsRotablesPorClave.size() == 0) this.itemsRotables.remove(entry.getKey());
			break;
		}
			
		// devuelvo el ítem al solicitante
		return itemRotable;
	}
	
	/**
	 * Devuelve la cantidad de ítems en la colección.
	 *
	 * @return  Cantidad de ítems en la colección.
	 */	
	public int size() {
		int size = 0;
		
		for (Map.Entry<String, List<ItemRotable>> entry:this.itemsRotables.entrySet()){
			List<ItemRotable> itemsRotablesPorClave = (List<ItemRotable>)entry.getValue();
			size += itemsRotablesPorClave.size();
		}
		
		return size;
	}

	/**
	 * Devuelve la cantidad de ítems en la colección del tipo del parámetro.
	 *
	 * @param  itemNum  Es el código del artículo en el Maestro de Artículos, es el valor itemnum en la BD.
	 * @return  Cantidad de ítems en la colección.
	 */	
	public int size(String itemNum) {
		if (itemNum == null || itemNum.isEmpty()) {
			return 0;
		} else {
			itemNum = itemNum.trim().toUpperCase();
		}

		int size = 0;
		
		for (Map.Entry<String, List<ItemRotable>> entry:this.itemsRotables.entrySet()) {
			String key = entry.getKey();
			if (key.equalsIgnoreCase(itemNum))
			{
				List<ItemRotable> itemsRotablesPorClave = (List<ItemRotable>)entry.getValue();
				size += itemsRotablesPorClave.size();
			}
		}
		
		return size;
	}

	/**
	 * Inserta un ítem rotable con el identificador de activo indicado en la colección.
	 *
	 * @param  itemRotable  ItemRotable a ser insertado.
	 * @exception  IllegalArgumentException  Si alguno de los argumentos no es válido.
	 */	
	private void Put(ItemRotable itemRotable) throws IllegalArgumentException {
		// Valido al item rotable
		Valid(itemRotable);
		
		// La clave es el código de activo
		String key = itemRotable.getItemNum();
		
		// Hago la búsqueda case insensitive
		key = key.trim().toUpperCase();
		
		// obtengo la lista de ítems con el código del artículo del argumento
		List<ItemRotable> itemsPorClave = this.itemsRotables.get(key);
		if (itemsPorClave == null) {
			// no hay ítems con este código: creo una lista nueva 
			itemsPorClave = new ArrayList<ItemRotable>();
		}
		// agrego a la lista el artículo del argumento
		itemsPorClave.add(itemRotable);
		
		// pongo en la colección la lista actualizada
		this.itemsRotables.put(key, itemsPorClave);
	}
	
	/**
	 * Valida el item rotable. 
	 *
	 * @param  itemRotable  Es el item rotable a validar.
	 * @exception  IllegalArgumentException  Si el argumento es inválido.
	 */	
	abstract protected void Valid(ItemRotable itemRotable) throws IllegalArgumentException;
	
	/**
	 * Carga la colección de ítems rotables de un archivo de texto (csv).
	 *
	 * @param  pathname  es el nombre completo del archivo con los ítems rotables.
	 * @return  Lista de arreglos de cadena de caracteres, cada una conteniendo una fila del archivo.
	 * @exception  InvalidFormatException  Si el formato del archivo o sus datos son inválidos.
	 * @exception  IOException  Si no se puede abrir, leer o cerrar el archivo.
	 * @exception  FileNotFoundException  Si el archivo no existe o es un directorio. 
	 */	
	private List<String[]> LoadFromCsv(String pathname) throws FileNotFoundException, IOException, InvalidFormatException {
		try {
			// Intento leer con separador ','
		    CSVReader readerC = new CSVReader(new FileReader(pathname), ',');
		    List<String[]> registrosC = readerC.readAll();
		    readerC.close();
		      
			// Intento leer con separador ';'
		    CSVReader readerPC = new CSVReader(new FileReader(pathname), ';');
		    List<String[]> registrosPC = readerPC.readAll();
		    readerPC.close();

		    int lenC = ((String[])registrosC.get(0)).length;
		    int lenPC = ((String[])registrosPC.get(0)).length;
		    
		    // Por la cantidad de campos elijo que lector uso
		    if (lenPC == 1 && lenC > 1) {
		      // Separador es ','
		      return registrosC;
		    }
		    else if (lenC == 1 && lenPC > 1) {
		      // Separador es ';'
		      return registrosPC;
		    }
		    else
		    {
		    	// Separador es otro
		    	throw new Exception();
		    }
		}
		catch (Exception ex) {
			String errorMsg = "Error al leer el archivo '" + pathname + "': formato inválido.";
			throw new InvalidFormatException(errorMsg);
		}
	}
	
	/**
	 * Carga la colección de ítems rotables de un archivo excel.
	 *
	 * @param  pathname  es el nombre completo del archivo con los ítems rotables.
	 * @return  Lista de arreglos de cadena de caracteres, cada una conteniendo una fila del archivo.
	 * @exception  InvalidFormatException  Si el formato del archivo o sus datos son inválidos.
	 * @exception  IOException  Si no se puede abrir, leer o cerrar el archivo.
	 * @exception  FileNotFoundException  Si el archivo no existe o es un directorio. 
	 */	
	private List<String[]> LoadFromExcel(String pathname) throws FileNotFoundException, IOException, InvalidFormatException {
		try {
			List<String[]> registros = new ArrayList<String[]>();
			
		    Iterator<Row> rowIterator = null;

			String extension = pathname.substring(pathname.lastIndexOf('.')).toLowerCase();

			File file = new File(pathname);
			FileInputStream fileInputStream = new FileInputStream(file);

			if (extension.equalsIgnoreCase(".xlsx"))
			{
				XSSFWorkbook wb = new XSSFWorkbook(fileInputStream);
				XSSFSheet sheet = wb.getSheetAt(0);
			    rowIterator = sheet.iterator();
			} else if (extension.equalsIgnoreCase(".xls")) {
				HSSFWorkbook wb = new HSSFWorkbook(fileInputStream);
				HSSFSheet sheet = wb.getSheetAt(0);
			    rowIterator = sheet.iterator();
			} else {
				String errorMsg = "Error al intentar leer el archivo '" + pathname + "': formato inválido.";
				fileInputStream.close();
				throw new InvalidFormatException(errorMsg);
			}

			fileInputStream.close();

			// Itero sobre cada fila de la primer hoja del libro
		    while (rowIterator.hasNext()) 
		    {
		    	Row row = rowIterator.next();
		        	
				Cell c0 = row.getCell(0);
				Cell c1 = row.getCell(1);
				Cell c2 = row.getCell(2);
				Cell c3 = row.getCell(3);
				Cell c4 = row.getCell(4);

				String [] fila = new String[5];
				fila[0] = this.CellToString(c0);
				fila[1] = this.CellToString(c1);
				fila[2] = this.CellToString(c2);
				fila[3] = this.CellToString(c3);
				fila[4] = this.CellToString(c4);
				
				boolean celda0EsNulaOVacia = fila[0] == null || fila[0].isEmpty();
				boolean celda1EsNulaOVacia = fila[1] == null || fila[1].isEmpty();
					
				if (celda0EsNulaOVacia && celda1EsNulaOVacia)  break;
				
				registros.add(fila);
		    }
		    
		    return registros;
		}
		catch (IllegalArgumentException ex) {
			String errorMsg = "Error al leer el archivo '" + pathname + "': formato inválido.";
			throw new InvalidFormatException(errorMsg);
		}
		catch (NullPointerException ex) {
			String errorMsg = "Error al leer el archivo '" + pathname + "': nombre inválido.";
			throw new FileNotFoundException(errorMsg);
		}
	}
	
	/**
	 * Convierte el contenido de una celda de planilla excel en una cadena de caracteres.
	 *
	 * @param  cell  Es la celda de la planilla excel.
	 * @exception  InvalidFormatException  Si el formato de la celda no es caracter o numérico con valor enetero y la celda no es vacía.
	 */ 	
	private String CellToString(Cell cell) throws InvalidFormatException
	{
		if (cell == null) return new String();
		
        switch (cell.getCellType()) 
        {
        	case Cell.CELL_TYPE_NUMERIC:
        		double numericValue = cell.getNumericCellValue();
        		if (numericValue != Math.rint(numericValue)) {
        			throw new InvalidFormatException();
        		}
        		return String.valueOf((int)numericValue);
        		
        	case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();

        	case Cell.CELL_TYPE_BLANK:
                return new String();

        	default:
    			throw new InvalidFormatException();
        }	
	}

}


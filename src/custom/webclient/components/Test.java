package custom.webclient.components;

public class Test {

	private static void testSplit(String archivo, String caso) {
		try
		{
			ItemsRotables artsRots = new ItemsRotablesSplit();
			artsRots.LeerDeArchivo(archivo);
			System.out.println("\nARCHIVO:"+archivo);
			System.out.println("CASO:"+caso);
			System.out.println("ITEMNUM\t\tASSETNUM\t\tSERIALNUM\t\tGLACCOUNT\t\tCHIPSETNUM");
			System.out.println("-------\t\t--------\t\t---------\t\t---------\t\t----------");
			int size = artsRots.size();
			for (int i = 0; i < size; i++) {
				ItemRotable ir = artsRots.getItemRotable();
				System.out.println(ir.getItemNum() + "\t\t" + ir.getAssetNum() + "\t\t" + ir.getSerialNum() + "\t\t" + ir.getGlAccount() + "\t\t" + ir.getChipsetNum());
			}
		}
		catch (Exception ex)
		{
			System.out.println("\n");
			System.out.println(ex.getMessage());
		}	
	}
	
	private static void testInput(String archivo, String caso) {
		try
		{
			ItemsRotables artsRots = new ItemsRotablesInput();
			artsRots.LeerDeArchivo(archivo);
			System.out.println("\nARCHIVO:"+archivo);
			System.out.println("CASO:"+caso);
			System.out.println("ITEMNUM\t\tASSETNUM\t\tSERIALNUM\t\tGLACCOUNT\t\tCHIPSETNUM");
			System.out.println("-------\t\t--------\t\t---------\t\t---------\t\t----------");
			int size = artsRots.size();
			for (int i = 0; i < size; i++) {
				ItemRotable ir = artsRots.getItemRotable();
				System.out.println(ir.getItemNum() + "\t\t" + ir.getAssetNum() + "\t\t" + ir.getSerialNum() + "\t\t" + ir.getGlAccount() + "\t\t" + ir.getChipsetNum());
			}
		}
		catch (Exception ex)
		{
			System.out.println("\n");
			System.out.println(ex.getMessage());
		}	
	}
	
	public static void main(String[] args) {
		
		Test.testInput("C:\\temp\\EJEMPLO01.csv", "REC OK 4 columnas");
		Test.testInput("C:\\temp\\EJEMPLO02.csv", "REC OK 4 columnas distinto orden");
		Test.testSplit("C:\\temp\\EJEMPLO01.csv", "INV OK 3 columnas una espurea");
		Test.testSplit("C:\\temp\\EJEMPLO02.csv", "INV falta una columna");
		
	}

}

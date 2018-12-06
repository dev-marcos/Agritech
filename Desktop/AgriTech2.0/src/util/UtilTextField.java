package utilitarios;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class UtilTextField extends PlainDocument {

	private int limit;
	private int maximo = 300;
	private boolean toUppercase = false;
	
	private final String LETRAS = "ÁÀÂÃáàâãAaBbCcDdÉÊéêEeFfGgHhÍÌíIiJjKkLlMmNnÓÔÕóôõOoPpQqRrSsTtÚúUuVvWwXxYyZzÇç ";
	private final String NUMEROS = "0123456789";
	private final String ESPECIAIS = ",.<>;:/?^~'!@#$%¨&*()_-+={[}]| ";
	private String caracteres = "";

	/**
	 * Utilizado para limitar o numero de caracteres, converter os caracteres em UpperCase,
	 * aceitar somente letras, números, caracteres especiais ou escolher os
	 * caracteres que poderão ser digitados dos campos JTextField.
	 * Exemplo: <br>campoTextField.setDocument(new UtilTextField(50, false, true, true, false, ",."));
	 * 
	 * @author Felipe Roger C. Ribeiro
	 * @param limit = limite do campo
	 * @param upper = Somente letras maiusculas.
	 * @param letras = Se true aceita as Letras <br>
	 * Letras: ÁÀÂÃáàâãAaBbCcDdÉÊéêEeFfGgHhÍÌíIiJjKkLlMmNnÓÔÕóôõOoPpQqRrSsTtÚúUuVvWwXxYyZzÇç 
	 * @param numeros = Se true aceita os Números<br>
	 * Números: 0123456789
	 * @param especiais = Se true aceita as Caracteres especiais<br>
	 * Especiais: ,.<>;:/?^~'!@#$%¨&*()_-+={[}]| 
	 * @param itens = Caracteres a parte.
	 * Ex: °ºª¬¢¹²³/;.,{§
	 */
	public UtilTextField(int limit, boolean upper, boolean letras, boolean numeros, boolean especiais, String itens){
		super();
		if (limit == 0){
			this.limit = maximo;			
		}else {
			this.limit = limit;			
		}
		toUppercase = upper;
		
		if(letras){
			caracteres+=this.LETRAS;
		}
		if(numeros){
			caracteres+=this.NUMEROS;
		}
		if(especiais){
			caracteres+=this.ESPECIAIS;
		}
		caracteres+=itens;
	}
	

	@Override
	public void insertString(int offset, String str, AttributeSet attr)
			throws BadLocationException {

		if (str == null) {
			return;
		}
		if ((getLength() + str.length()) <= limit) {
			if (toUppercase) {
				str = str.toUpperCase();
			}
			if (caracteres.contains(str.substring(str.length() - 1) + "")){
				super.insertString(offset, str, attr);				
			}
		}
	}
}